package dk.webbies.tajscheck.tajstester;

import com.google.gson.Gson;
import dk.brics.tajs.analysis.*;
import dk.brics.tajs.analysis.FunctionCalls.CallInfo;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.monitoring.DefaultAnalysisMonitoring;
import dk.brics.tajs.solver.BlockAndContext;
import dk.brics.tajs.solver.WorkList;
import dk.brics.tajs.type_testing.TypeTestRunner;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy.ConsistencyKeepingExpansionPolicy;
import dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy.ExpansionPolicy;
import dk.webbies.tajscheck.benchmark.options.staticOptions.RetractionPolicy;
import dk.webbies.tajscheck.tajstester.data.TestCertificate;
import dk.webbies.tajscheck.tajstester.data.Timers;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
import dk.webbies.tajscheck.tajstester.monitors.SuspiciousnessMonitor;
import dk.webbies.tajscheck.tajstester.monitors.TestTransfersMonitor;
import dk.webbies.tajscheck.testcreator.test.*;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static dk.brics.tajs.util.Collections.newList;

public class TajsTypeTester extends DefaultAnalysisMonitoring implements TypeTestRunner {
    private static final boolean DEBUG = true;
    private static final boolean DEBUG_VALUES = false;

    private final List<Test> tests;
    private final BenchmarkInfo info;
    private final ExpansionPolicy expansionPolicy;
    private TypeValuesHandler valueHandler = null;

    final private List<TypeViolation> violations = newList();
    final private List<TypeViolation> warnings = newList();
    final private List<TestCertificate> certificates = newList();

    // violations etc. reported while the analysis was not yet done.
    private List<TypeViolation> notDoneViolations = newList();
    private List<TypeViolation> notDoneWarnings = newList();
    private List<TestCertificate> notDoneCertificates = newList();

    private final Set<Test> performed = new LinkedHashSet<>();

    private final RetractionPolicy retractionPolicy;
    private final Map<Test, Exception> exceptionsEncountered = new HashMap<>();

    private BasicBlock allTestsBlock;
    private Context allTestsContext;
    private TesterContextSensitivity sensitivity;

    private SuspiciousnessMonitor suspiciousMonitor;
    private TestTransfersMonitor transferMonitor;

    private Timers timers = new Timers();
    private List<Test> typeCheckedTests = new ArrayList<>();

    public TajsTypeTester(List<Test> tests, BenchmarkInfo info) {
        this.tests = tests.stream().sorted((a, b) -> {
            int aValue = a instanceof FunctionTest ? 1 : 0;
            int bValue = b instanceof FunctionTest ? 1 : 0;
            return Integer.compare(aValue, bValue);
        }).collect(Collectors.toList());
        this.info = info;
        this.retractionPolicy = this.info.options.staticOptions.retractionPolicy;
        this.expansionPolicy = new ConsistencyKeepingExpansionPolicy(this.info.options.staticOptions.expansionPolicy);
        this.transferMonitor = new TestTransfersMonitor(this, retractionPolicy::notifyTestTransfer);
        this.suspiciousMonitor = new SuspiciousnessMonitor(this, retractionPolicy::notifySuspiciousLocation);
    }

    public Timers getTimers() {return timers; }

    private Context previousTestContext = null;

    public void triggerTypeTests(Solver.SolverInterface c) {
        if(allTestsBlock == null) {
            init(c);
        }

        if(TesterContextSensitivity.isLocalTestContext(c.getState().getContext())) {
            if(!c.isScanning()) {
                if(DEBUG_VALUES) System.out.println("New flow for " + c.getState().getBasicBlock().getIndex() + ", " + c.getState().getContext());
                // Then we can re-run the tests to see if more can be performed
                enqueueTypeTester(c);
            }
            return;
        }

        if (!c.getWorklist().isEmpty()) {
            enqueueTypeTester(c);
            return;
        }

        valueHandler.clearCreatedValueCache();
        performed.clear();
        expansionPolicy.nextRound();
        valueHandler.cleanUp();

        typeCheckedTests.clear();

        boolean progress;
        do {
            progress = iterateAllNonPerformedTests(c);

            if (progress) {
                continue;
            }

            for (Test test : expansionPolicy.getTestsToPerformAnyway(c)) {
                if (performed.contains(test)) {
                    continue;
                }
                progress = true;
                valueHandler.clearCreatedValueCache();
                valueHandler.clearValuesForTest(test);
                Context newc = sensitivity.makeLocalTestContext(allTestsContext, test);
                propagateStateToContext(c, newc, Timers.Tags.PROPAGATING_TO_THIS_CONTEXT, allTestsBlock);
                State testState = c.getAnalysisLatticeElement().getState(allTestsBlock, newc);

                c.withState(testState, () -> {
                    boolean argsNotAvailable = false;
                    try {
                        if (test.getDependsOn().stream().map(type -> valueHandler.createValue(type, test.getTypeContext())).anyMatch(Value::isNone)) {
                            argsNotAvailable = true;
                        }
                    } catch (Exception e) {
                        exceptionsEncountered.put(test, e);
                        argsNotAvailable = true;
                    }
                    if (argsNotAvailable) {
                        expansionPolicy.nextRound();
                        return;
                    }

                    performTest(c, test, newc);
                });
            }
        } while (progress);

        valueHandler.clearValuesForTest(null); // null is the special test used for saved arguments from higher-order-functions.

        propagateStateToContext(c, allTestsContext, Timers.Tags.PROPAGATING_BACK_TO_LOOP_ENTRY, allTestsBlock);

        endOfInnerLoopCallbacks.forEach(Runnable::run);
        endOfInnerLoopCallbacks.clear();

        if (DEBUG && !c.isScanning()) System.out.println(" .... finished a round of doable tests, performed " + performed.size() + " tests\n");

        // Recording partial results.
        this.notDoneCertificates = this.notDoneCertificates.stream().distinct().collect(Collectors.toList());
        this.notDoneWarnings = this.notDoneWarnings.stream().distinct().collect(Collectors.toList());
        this.notDoneViolations = this.notDoneViolations.stream().distinct().collect(Collectors.toList());


        if (!c.isScanning()) {
            TAJSUtil.TajsAnalysisResults partialResults = new TAJSUtil.TajsAnalysisResults(this, true);
            try {
                Gson gson = new Gson();
                Util.writeFile(Paths.get(info.bench.dTSFile).getParent().resolve("partialResult.json").toAbsolutePath().toString(), gson.toJson(partialResults.summary()));
                Util.writeFile("partialResult.txt", partialResults.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (!c.getWorklist().isEmpty()) {
            enqueueTypeTester(c);
        }
    }

    private void enqueueTypeTester(Solver.SolverInterface c) {
        allTestsBlock.setOrder(Integer.MAX_VALUE);// Making sure that the TypeTester is the last to run.
        c.addToWorklist(allTestsBlock, allTestsContext); // Making sure the TypeTester runs when the worklist is otherwise empty.
    }

    private boolean iterateAllNonPerformedTests(Solver.SolverInterface c) {
        boolean progress = false;
        for (Test test : tests) {
            valueHandler.clearCreatedValueCache(); // TODO: Needed this much?
            if (performed.contains(test)) {
                continue;
            }
            valueHandler.clearValuesForTest(test);

            if (retractionPolicy.isRetracted(test) || exceptionsEncountered.containsKey(test)) { // importantly, even if it is timed out, we still continue.
                continue;
            }

            if (test.getTypeToTest().stream().map(type -> new TypeWithContext(type, test.getTypeContext())).map(valueHandler::findFeedbackValue).anyMatch(Objects::isNull)) {
                if(performed.contains(test))
                    throw new RuntimeException("Previously performed test is now skipped because of no values for the types to test");

                if (DEBUG && !c.isScanning()) System.out.println("Skipped test " + test);
                if (DEBUG && c.isScanning()) System.out.println("Never performed test " + test);
                continue;
            }

            // Generating one local context per test
            Context newc = sensitivity.makeLocalTestContext(allTestsContext, test);

            // Propagate previous state into this, chaining the flow
            propagateStateToContext(c, newc, Timers.Tags.PROPAGATING_TO_THIS_CONTEXT, allTestsBlock);

            State testState = c.getAnalysisLatticeElement().getState(allTestsBlock, newc);

            progress |= c.withState(testState, () -> {
                if (test instanceof FunctionTest && !expansionPolicy.expandTo((FunctionTest) test, this)) {
                    if (DEBUG) System.out.println("Didn't expand to " + test);
                    return false;
                }

                try {
                    if (test.getDependsOn().stream().map(type -> valueHandler.createValue(type, test.getTypeContext())).anyMatch(Value::isNone)) {
                        return false;
                    }
                } catch (Exception e) {
                    exceptionsEncountered.put(test, e);
                    return false;
                }

                performTest(c, test, newc);
                return true;
            });
        }
        return progress;
    }

    private void propagateStateToContext(Solver.SolverInterface c, Context newc, Timers.Tags propagatingToThisContext, BasicBlock allTestsBlock) {
        if (previousTestContext != null) {
            timers.start(propagatingToThisContext);
            State preState = c.getAnalysisLatticeElement().getState(allTestsBlock, previousTestContext).clone();
            c.propagateToBasicBlock(preState, allTestsBlock, newc);
            timers.stop(propagatingToThisContext);
        }
    }

    private void performTest(Solver.SolverInterface c, Test test, Context newc) {
        if (DEBUG) System.out.println("Performing test " + test);

        TajsTypeChecker typeChecker = new TajsTypeChecker(test, c, info);
        TajsTestVisitor visitor = new TajsTestVisitor(c, typeChecker, this, info, valueHandler);

        // attempting to perform the test in the local context
        timers.start(Timers.Tags.TEST_TRANSFER);
        boolean typeChecked = false;
        try {
            typeChecked = test.accept(visitor);
            performed.add(test);
        } catch (Exception e) {
            exceptionsEncountered.put(test, e);
        }

        if (test instanceof FunctionTest && info.options.staticOptions.checkAllPropertiesAreFunctionCall) {
            typeChecked &= checkPropertyReads(test, typeCheckedTests.stream().filter(PropertyReadTest.class::isInstance).map(PropertyReadTest.class::cast).collect(Collectors.toList()), c);
        }

        timers.stop(Timers.Tags.TEST_TRANSFER);

        if (typeChecked || info.options.staticOptions.propagateStateFromFailingTest) {
            previousTestContext = newc; // do propagate the new state.
        }
        if (typeChecked) {
            typeCheckedTests.add(test);
        }
    }

    private boolean checkPropertyReads(Test testToBlame, List<PropertyReadTest> propertyReads, Solver.SolverInterface c) {
        boolean typeChecked = true;
        TajsTypeChecker typeChecker = new TajsTypeChecker(testToBlame, c, info);
        for (PropertyReadTest propertyRead : propertyReads) {
            Value baseValue = valueHandler.findFeedbackValue(new TypeWithContext(propertyRead.getBaseType(),propertyRead.getTypeContext()));
            PropVarOperations pc = c.getAnalysis().getPropVarOperations();
            if (baseValue == null) {
                continue;
            }

            for (ObjectLabel label : baseValue.getObjectLabels()) {
                Value propertyValue = UnknownValueResolver.getRealValue(pc.readPropertyValue(Collections.singletonList(label), Value.makeStr(propertyRead.getProperty()), info.options.staticOptions.killGetters), c.getState());
                TypeWithContext closedType = new TypeWithContext(propertyRead.getPropertyType(), propertyRead.getTypeContext());
                if (propertyValue.isNone()) {
                    continue;
                }
                List<TypeViolation> violations = getViolations(propertyValue, closedType, propertyRead.getPath(), c, typeChecker);

                typeChecked &= violations.isEmpty();

                for (TypeViolation violation : violations) {
                    addViolation(violation.withMessage("Violation after FunctionCall: \"" + violation.toString() + "\"").withPath(testToBlame.getPath()), c);
                }
            }

        }

        return typeChecked;
    }

    private final List<Runnable> endOfInnerLoopCallbacks = new ArrayList<>();
    public void addEndOfInnerLoopCallback(Runnable runner) {
        endOfInnerLoopCallbacks.add(runner);
    }

    private void init(Solver.SolverInterface c) {
        System.out.println("...library is loaded, type-checking begins now");
        allTestsBlock = c.getState().getBasicBlock();
        allTestsContext = c.getState().getContext();
        sensitivity = (TesterContextSensitivity) c.getAnalysis().getContextSensitivityStrategy().getDefaultContextSensitivity();


        timers.start(Timers.Tags.INITIAL_STATE_PROPAGATION_TO_TEST_ENTRY);
        State originalState = c.getState().clone();
        for (Test test : tests) {
            // Generating one local context per test
            Context newc = sensitivity.makeLocalTestContext(allTestsContext, test);

            // and propagating to them the after-load/other state
            if (c.getAnalysisLatticeElement().getState(allTestsBlock, newc) == null) {
                c.propagate(originalState.clone(), new BlockAndContext<>(allTestsBlock, newc), false);
            }
        }
        timers.stop(Timers.Tags.INITIAL_STATE_PROPAGATION_TO_TEST_ENTRY);
        if(valueHandler == null) {
            valueHandler = new TypeValuesHandler(info.typeNames, c, info);
        }
    }

    @Override
    public Value evaluateCallToSymbolicFunction(HostObject hostObject, CallInfo call, Solver.SolverInterface c) {
        return new TypedSymbolicFunctionEvaluator(this, info, valueHandler).evaluateCallToSymbolicFunction(hostObject, call, c);
    }

    /**
     *
     * @return if the value satisfied the type
     */
    public boolean attemptAddValue(Value value, TypeWithContext t, String path, Solver.SolverInterface c, TajsTypeChecker tajsTypeChecker, Test test) {
        value = UnknownValueResolver.getRealValue(value, c.getState());
        if (value.isNone()) {
            return !c.isScanning(); // if not scanning, it is ok. If scanning, it is a type-error.
        }
        List<TypeViolation> violations = getViolations(value, t, path, c, tajsTypeChecker);

        if(violations.isEmpty() && !value.isNone()) {
            boolean newValue = valueHandler.addFeedbackValue(test, t, value);
            if(DEBUG_VALUES && newValue) System.out.println("Value added for type:" + t + " path:" + path + ", value: " + value);
            if(newValue && c.isScanning()) {
                throw new RuntimeException("New values should not appear in scanning!");
            }
        } else {
            if(DEBUG_VALUES) System.out.println("Value " + value + " not added because it violates type " + t + " path:" + path);
            violations.forEach(violation -> addViolation(violation, c));
        }

        return violations.isEmpty();
    }

    List<TypeViolation> getViolations(Value v, TypeWithContext t, String path, Solver.SolverInterface c, TajsTypeChecker tajsTypeChecker) {
        return tajsTypeChecker.typeCheck(UnknownValueResolver.getRealValue(v, c.getState()), t.getType(), t.getTypeContext(), info, path);
    }

    @Override
    public boolean shouldSkipEntry(WorkList<Context>.Entry e) {
        if (sensitivity != null && TesterContextSensitivity.isTestContext(e.getContext())) {
            Test test = sensitivity.getTest(e.getContext());
            return retractionPolicy.isRetracted(test) || retractionPolicy.isTimeout(test) || exceptionsEncountered.containsKey(test);
        }
        return false;
    }

    @Override
    public boolean recoverFrom(Exception e, WorkList<Context>.Entry p) {
        if (sensitivity != null && TesterContextSensitivity.isTestContext(p.getContext())) {
            Test test = sensitivity.getTest(p.getContext());
            assert !exceptionsEncountered.containsKey(test);
            exceptionsEncountered.put(test, e);
            return true;
        }
        return false;
    }

    /*
     * Getters, setters, and adders.
     */

    public void addViolation(TypeViolation violation, Solver.SolverInterface c) {
        if (c.isScanning()) {
            violations.add(violation);
        } else {
            notDoneViolations.add(violation);
        }
    }

    public void addWarning(TypeViolation warning, Solver.SolverInterface c) {
        if (c.isScanning()) {
            warnings.add(warning);
        } else {
            notDoneWarnings.add(warning);
        }
    }

    void addCertificate(TestCertificate testCertificate, Solver.SolverInterface c) {
        if (c.isScanning()) {
            certificates.add(testCertificate);
        } else {
            notDoneCertificates.add(testCertificate);
        }
    }

    public int getTotalTests() {return tests.size();}

    public List<Test> getAllTests() {return tests;}

    public Collection<Test> getPerformedTests() {return performed;}

    public List<TypeViolation> getViolations(boolean timeout) {
        if (timeout) {
            return notDoneViolations;
        } else {
            return violations;
        }
    }

    public List<TypeViolation> getWarnings(boolean timeout) {
        if (timeout) {
            return notDoneWarnings;
        } else {
            return warnings;
        }
    }

    public List<TestCertificate> getCertificates(boolean timeout) {
        if (timeout) {
            return notDoneCertificates;
        } else {
            return certificates;
        }
    }

    public Map<Test, Exception> getExceptionsEncountered() {
        return exceptionsEncountered;
    }

    public TesterContextSensitivity getSensitivity() {return sensitivity; }

    public SuspiciousnessMonitor getSuspiciousMonitor() {return suspiciousMonitor; }

    public TestTransfersMonitor getTransferMonitor() {return transferMonitor; }

    public Set<Test> getRetractedTests() {
        return tests.stream().filter(retractionPolicy::isRetracted).collect(Collectors.toSet());
    }

    public Set<Test> getTimedOutTests() {
        return tests.stream().filter(retractionPolicy::isTimeout).collect(Collectors.toSet());
    }

    public List<Test> getTypeCheckedTests() {
        return typeCheckedTests;
    }

    public RetractionPolicy getRetractionPolicy() {
        return retractionPolicy;
    }

    public TypeValuesHandler getValueHandler() {
        return valueHandler;
    }

    public BenchmarkInfo getBenchmarkInfo() {
        return info;
    }
}
