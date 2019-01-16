package dk.webbies.tajscheck.tajstester;

import com.google.gson.Gson;
import dk.brics.tajs.analysis.*;
import dk.brics.tajs.analysis.FunctionCalls.CallInfo;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.monitoring.AnalysisPhase;
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
import dk.webbies.tajscheck.tajstester.monitors.ReadFromStdlibMonitor;
import dk.webbies.tajscheck.tajstester.monitors.SuspiciousnessMonitor;
import dk.webbies.tajscheck.tajstester.monitors.TajsCoverageResult;
import dk.webbies.tajscheck.tajstester.monitors.TestTransfersMonitor;
import dk.webbies.tajscheck.testcreator.test.*;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
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
    private final MultiMap<Test, Exception> exceptionsEncountered = new ArrayListMultiMap<>();

    private BasicBlock allTestsBlock;
    private Context allTestsContext;
    private TesterContextSensitivity sensitivity;

    private SuspiciousnessMonitor suspiciousMonitor;
    private TestTransfersMonitor transferMonitor;
    private TajsCoverageResult coverageMonitor = new TajsCoverageResult();
    private ReadFromStdlibMonitor readFromStdlibMonitor = new ReadFromStdlibMonitor();
    private ViolationsOracle violationsOracle;

    private Timers timers = new Timers();
    private final List<Test> typeCheckedTests = new ArrayList<>();
    private TypedSymbolicFunctionEvaluator typedSymbolicFunctionEvaluator;

    private boolean hasScanned = false;

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
        this.violationsOracle = ViolationsOracle.fromJson(info.bench);
    }

    public Timers getTimers() {return timers; }

    private Context previousTestContext = null;
    private Context prepreviousTestContext = null;


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

        readFromStdlibMonitor.setLibTestingHasStarted();

        if (c.isScanning()) {
            if (hasScanned) {
                return;
            }
            hasScanned = true;
        }

        performed.clear();
        expansionPolicy.nextRound();
        valueHandler.cleanUp();

        typeCheckedTests.clear();

        boolean progress;
        do {
            progress = iterateAllNonPerformedTests(c);

            // If getting values out from side-effects, this is where to do it. new value = new test.

            if (progress) {
                continue;
            }

            for (Test test : expansionPolicy.getTestsToPerformAnyway(c)) {
                if (performed.contains(test)) {
                    continue;
                }
                progress = true;
                valueHandler.clearValuesForTest(test);
                Context newc = sensitivity.makeLocalTestContext(allTestsContext, test);
                propagateStateToContext(c, newc, Timers.Tags.PROPAGATING_TO_THIS_CONTEXT);
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

        propagateStateToContext(c, allTestsContext, Timers.Tags.PROPAGATING_BACK_TO_LOOP_ENTRY);
        previousTestContext = null;
        prepreviousTestContext = null;

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

    public void bipropagate(Solver.SolverInterface c) {
        State allState = c.getAnalysisLatticeElement().getState(allTestsBlock, allTestsContext);
        allState.propagate(c.getState().clone(), true, false);
        c.getState().propagate(allState.clone(), true, false);
    }

    private void enqueueTypeTester(Solver.SolverInterface c) {
        c.addToWorklist(allTestsBlock, allTestsContext); // Making sure the TypeTester runs when the worklist is otherwise empty.
    }

    private boolean iterateAllNonPerformedTests(Solver.SolverInterface c) {
        boolean progress = false;
        for (Test test : tests) {
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

            State previousState = c.getAnalysisLatticeElement().getState(allTestsBlock, previousTestContext == null ? c.getState().getContext() : previousTestContext);
            State prepreviousState = c.getAnalysisLatticeElement().getState(allTestsBlock, prepreviousTestContext == null ? c.getState().getContext() : prepreviousTestContext);

            previousState.propagate(prepreviousState.clone(), true, false);

            progress |= c.withState(previousState, () -> {
                if (test instanceof FunctionTest && !expansionPolicy.expandTo((FunctionTest) test, this)) { // placed before the propagateStateToContext, to avoid an infinite loop.
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

                // Propagate previous state into this, chaining the flow
                propagateStateToContext(c, newc, Timers.Tags.PROPAGATING_TO_THIS_CONTEXT);

                State testState = c.getAnalysisLatticeElement().getState(allTestsBlock, newc);

                return c.withState(testState, () -> {
                    performTest(c, test, newc);
                    return true;
                });
            });
        }
        return progress;
    }

    private void propagateStateToContext(Solver.SolverInterface c, Context newc, Timers.Tags propagatingToThisContext) {
        timers.start(propagatingToThisContext);
        if (prepreviousTestContext != null) {
            State prepreState = c.getAnalysisLatticeElement().getState(allTestsBlock, prepreviousTestContext).clone();
            c.propagateToBasicBlock(prepreState, allTestsBlock, newc);
        }
        if (previousTestContext != null) {
            State preState = c.getAnalysisLatticeElement().getState(allTestsBlock, previousTestContext).clone();
            c.propagateToBasicBlock(preState, allTestsBlock, newc);
        }
        if(previousTestContext == null || prepreviousTestContext == null) {
            State preState = c.getAnalysisLatticeElement().getState(allTestsBlock, allTestsContext).clone();
            c.propagateToBasicBlock(preState, allTestsBlock, newc);
        }
        timers.stop(propagatingToThisContext);
    }

    private void performTest(Solver.SolverInterface c, Test test, Context newc) {
        if (DEBUG) System.out.println("Performing test " + test);

        TajsTypeChecker typeChecker = new TajsTypeChecker(test, c, info, violationsOracle);
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

        if (test instanceof FunctionTest && info.options.staticOptions.checkAllPropertiesAfterFunctionCall) {
            typeChecked &= checkPropertyReads(test, typeCheckedTests.stream().filter(PropertyReadTest.class::isInstance).map(PropertyReadTest.class::cast).collect(Collectors.toList()), c, test.getPath());
        }

        timers.stop(Timers.Tags.TEST_TRANSFER);

        if (typeChecked || info.options.staticOptions.propagateStateFromFailingTest) {
            prepreviousTestContext = previousTestContext;
            previousTestContext = newc; // do propagate the new state.
        }
        if (typeChecked) {
            typeCheckedTests.add(test);
        }
    }

    boolean checkPropertyReads(Test testToBlame, List<PropertyReadTest> propertyReads, Solver.SolverInterface c, String pathToBlame) {
        boolean typeChecked = true;
        TajsTypeChecker typeChecker = new TajsTypeChecker(testToBlame, c, info, violationsOracle);
        for (PropertyReadTest propertyRead : propertyReads) {
            Value baseValue = valueHandler.findFeedbackValue(new TypeWithContext(propertyRead.getBaseType(), propertyRead.getTypeContext()));
            PropVarOperations pc = c.getAnalysis().getPropVarOperations();
            if (baseValue == null) {
                continue;
            }

            for (ObjectLabel label : baseValue.getObjectLabels()) {
                Value propertyValue = UnknownValueResolver.getRealValue(pc.readPropertyValue(Collections.singletonList(label), Value.makeStr(propertyRead.getProperty())), c.getState());
                TypeWithContext closedType = new TypeWithContext(propertyRead.getPropertyType(), propertyRead.getTypeContext());
                if (propertyValue.isNone()) {
                    continue;
                }
                List<TypeViolation> violations = getViolations(propertyValue, closedType, propertyRead.getPath(), c, typeChecker);

                Function<TypeViolation, String> messageGenerator = testToBlame != null ?
                        v -> "Violation after functionCall: " + pathToBlame + " violation: " + v.toString() + "\"" :
                        v -> "Violation while callback is invoked: " + pathToBlame + " violation: " + v.toString() + "\"";

                violations = violations.stream()
                        .map(v -> v.withMessage(messageGenerator.apply(v)))
                        .filter(violationsOracle::canEmit)
                        .collect(Collectors.toList());

                typeChecked &= violations.isEmpty();

                violations.forEach(v -> addViolation(v, c));
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
            this.valueHandler = new TypeValuesHandler(info.typeNames, c, info, this);
            this.typedSymbolicFunctionEvaluator = new TypedSymbolicFunctionEvaluator(this, info, valueHandler);
        }
    }

    @Override
    public Value evaluateCallToSymbolicFunction(HostObject hostObject, CallInfo call, Solver.SolverInterface c) {
        return typedSymbolicFunctionEvaluator.evaluateCallToSymbolicFunction(hostObject, call, c);
    }

    /**
     *
     * @return if the value was added.
     */
    public boolean attemptAddValue(Value value, TypeWithContext t, String path, Solver.SolverInterface c, TajsTypeChecker tajsTypeChecker, Object key) {
        assert key != null;
        value = UnknownValueResolver.getRealValue(value, c.getState());
        if (value.isNone()) {
            return !c.isScanning(); // if not scanning, it is ok. If scanning, it is a type-error.
        }
        List<TypeViolation> violations = getViolations(value, t, path, c, tajsTypeChecker);

        violations.forEach(violation -> addViolation(violation, c));

        boolean wasAdded = false;

        // If we have potential mismatches, then we only want the parts that have no definite mismatches.
        if (!info.options.staticOptions.useValuesWithMismatches) {
            if((violations.isEmpty())) {
                wasAdded = true;
                boolean newValue = valueHandler.addFeedbackValue(key, t, value, c);
                if(DEBUG_VALUES && newValue) System.out.println("Value added for type:" + t + " path:" + path + ", value: " + value);
                if(newValue && c.isScanning()) {
                    throw new RuntimeException("New values should not appear in scanning!");
                }
            } else {
                if(DEBUG_VALUES) System.out.println("Value " + value + " not added because it violates type " + t + " path:" + path);
            }
        } else {
            for (Value v : TajsTypeChecker.split(value)) {
                if (getViolations(v, t, path, c, tajsTypeChecker).stream().noneMatch(violation -> violation.definite)) {
                    wasAdded = true;
                    boolean newValue = valueHandler.addFeedbackValue(key, t, v, c);
                    if(DEBUG_VALUES && newValue) System.out.println("Value added for type:" + t + " path:" + path + ", value: " + value);
                    if(newValue && c.isScanning()) {
                        throw new RuntimeException("New values should not appear in scanning!");
                    }
                }
            }
        }


        return violations.isEmpty() || wasAdded;
    }

    List<TypeViolation> getViolations(Value v, TypeWithContext t, String path, Solver.SolverInterface c, TajsTypeChecker tajsTypeChecker) {
        return tajsTypeChecker.typeCheck(UnknownValueResolver.getRealValue(v, c.getState()), t.getType(), t.getTypeContext(), path)
                .stream()
                .filter(this.violationsOracle::canEmit)
                .filter(violation -> {
                    if (!info.options.staticOptions.ignoreMaybeUndefined) {
                        return true;
                    }
                    return violation.definite || (!violation.message.endsWith("but found Undef") && !violation.message.endsWith("but found Null"));
                })
                .collect(Collectors.toList());
    }

    private Set<BlockAndContext<Context>> entriesToSkip = new HashSet<>();
    @Override
    public boolean shouldSkipEntry(BlockAndContext<Context> e) {
        if (entriesToSkip.contains(e)) {
            return true;
        }
        if (sensitivity != null && TesterContextSensitivity.isTestContext(e.getContext())) {
            Test test = sensitivity.getTest(e.getContext());
            return retractionPolicy.isRetracted(test) || retractionPolicy.isTimeout(test) || exceptionsEncountered.containsKey(test);
        }
        return false;
    }

    @Override
    public boolean recoverFrom(Exception e, BlockAndContext<Context> p) {
        if (sensitivity != null && TesterContextSensitivity.isTestContext(p.getContext())) {
            Test test = sensitivity.getTest(p.getContext());
            assert !exceptionsEncountered.containsKey(test);
            exceptionsEncountered.put(test, e);
            return true;
        }
        exceptionsEncountered.put(null, e);
        entriesToSkip.add(p);
        return true; // Still try to recover.
    }

    /*
     * Getters, setters, and adders.
     */

    public void addViolation(TypeViolation violation, Solver.SolverInterface c) {
        if (!violationsOracle.canEmit(violation)) {
            return;
        }
        if (c.isScanning()) {
            violations.add(violation);
        } else {
            notDoneViolations.add(violation);
        }
    }

    public void addWarning(TypeViolation warning, Solver.SolverInterface c) {
        if (!violationsOracle.canEmit(warning)) {
            return;
        }
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

    @Override
    public void visitPhasePost(AnalysisPhase phase) {
        if(phase == AnalysisPhase.SCAN) {
            if(!this.violationsOracle.isTight()) {
                System.out.println("The violation oracle used is not tight, remove the following suppressions:\n" +
                        new ArrayList<>(this.violationsOracle.getUnnecessarySuppressions()));
            }
        }
    }

    public MultiMap<Test, Exception> getExceptionsEncountered() {
        return exceptionsEncountered;
    }

    public TesterContextSensitivity getSensitivity() {return sensitivity; }

    public SuspiciousnessMonitor getSuspiciousMonitor() {return suspiciousMonitor; }

    public TajsCoverageResult getCoverageMonitor() {return coverageMonitor; }

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

    public BasicBlock getAllTestsBlock() {
        return allTestsBlock;
    }

    public Context getAllTestsContext() {
        return allTestsContext;
    }

    public ViolationsOracle getViolationsOracle() {
        return violationsOracle;
    }

    public ReadFromStdlibMonitor getReadFromStdlibMonitor() {
        return readFromStdlibMonitor;
    }
}
