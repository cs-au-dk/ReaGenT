package dk.webbies.tajscheck.tajstester;

import dk.brics.tajs.analysis.*;
import dk.brics.tajs.analysis.FunctionCalls.CallInfo;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.monitoring.DefaultAnalysisMonitoring;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.options.Options;
import dk.brics.tajs.solver.BlockAndContext;
import dk.brics.tajs.solver.GenericSolver;
import dk.brics.tajs.solver.WorkList;
import dk.brics.tajs.type_testing.TypeTestRunner;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.benchmark.options.staticOptions.ExpansionPolicy;
import dk.webbies.tajscheck.benchmark.options.staticOptions.RetractionPolicy;
import dk.webbies.tajscheck.tajstester.data.TestCertificate;
import dk.webbies.tajscheck.tajstester.data.Timers;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
import dk.webbies.tajscheck.tajstester.monitors.SuspiciousnessMonitor;
import dk.webbies.tajscheck.tajstester.monitors.TestTransfersMonitor;
import dk.webbies.tajscheck.testcreator.test.*;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newSet;

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

    private BasicBlock allTestsBlock;
    private Context allTestsContext;
    private TesterContextSensitivity sensitivity;

    private Set<TestBlockEntryObserver> observers = newSet();

    private SuspiciousnessMonitor suspiciousMonitor;
    private TestTransfersMonitor transferMonitor;

    private Timers timers = new Timers();

    public TajsTypeTester(List<Test> tests, BenchmarkInfo info) {
        this.tests = tests;
        this.info = info;
        this.retractionPolicy = this.info.options.staticOptions.retractionPolicy;
        this.expansionPolicy = this.info.options.staticOptions.expansionPolicy;
        this.transferMonitor = new TestTransfersMonitor(this, retractionPolicy::notifyTestTransfer);
        this.suspiciousMonitor = new SuspiciousnessMonitor(this, retractionPolicy::notifySuspiciousLocation);
    }

    public Timers getTimers() {return timers; }

    private Context previousTestContext = null;

    public void triggerTypeTests(Solver.SolverInterface c) {
        if(allTestsBlock == null) {
            init(c);
        }

        if(sensitivity.isLocalTestContext(c.getState().getContext())) {
            if(!c.isScanning()) {
                if(DEBUG_VALUES) System.out.println("New flow for " + c.getState().getBasicBlock().getIndex() + ", " + c.getState().getContext());
                // Then we can re-run the tests to see if more can be performed
                c.addToWorklist(allTestsBlock, allTestsContext);
            }
            return;
        }

        for(TestBlockEntryObserver obs : observers) {
            obs.onTestBlockEntry(c);
        }

        TajsTypeChecker typeChecker = new TajsTypeChecker(c, info);

        TajsTestVisitor visitor = new TajsTestVisitor(c, valueHandler, typeChecker, this, info, valueHandler);

        performed.clear();
        expansionPolicy.nextRound();
        valueHandler.cleanUp();

        for (Test test : tests) {
            if (retractionPolicy.isRetracted(test)) {
                continue;
            }

            valueHandler.clearValuesForTest(test);

            if (test.getTypeToTest().stream().map(type -> new TypeWithContext(type, test.getTypeContext())).map(valueHandler::findFeedbackValue).anyMatch(Objects::isNull)) {
                if (DEBUG && !c.isScanning()) {
                    System.out.println("Skipped test " + test);
                }
                if (DEBUG && c.isScanning()) {
                    System.out.println("Never performed test " + test);
                }
                continue;
            }

            if (test instanceof FunctionTest && !expansionPolicy.include((FunctionTest) test)) {
                if (DEBUG) {
                    System.out.println("Didn't expand to " + test);
                }
                continue;
            }

            // Generating one local context per test
            Context newc = sensitivity.makeLocalTestContext(allTestsContext, test);

            // Propagate previous state into this, chaining the flow
            if(previousTestContext != null) {
                timers.start(Timers.Tags.PROPAGATING_TO_THIS_CONTEXT);
                State preState = c.getAnalysisLatticeElement().getState(allTestsBlock, previousTestContext).clone();
                c.propagateToBasicBlock(preState, allTestsBlock, newc);
                timers.stop(Timers.Tags.PROPAGATING_TO_THIS_CONTEXT);
            }

            State testState = c.getAnalysisLatticeElement().getState(allTestsBlock, newc);
            // attempting to perform the test in the local context
            timers.start(Timers.Tags.TEST_TRANSFER);
            c.withState(testState, () -> {
                if (DEBUG && !c.isScanning()){
                    System.out.println("Performing test " + test);
                }

                performed.add(test);

                test.accept(visitor);
            });
            timers.stop(Timers.Tags.TEST_TRANSFER);

            if(performed.contains(test)) {
                previousTestContext = newc;
            }
        }

        valueHandler.clearValuesForTest(null); // null is the special test used for saved arguments from higher-order-functions.

        timers.start(Timers.Tags.PROPAGATING_BACK_TO_LOOP_ENTRY);
        State finalChainingState = c.getAnalysisLatticeElement().getState(allTestsBlock, previousTestContext).clone();
        c.propagateToBasicBlock(finalChainingState, allTestsBlock, allTestsContext);
        timers.stop(Timers.Tags.PROPAGATING_BACK_TO_LOOP_ENTRY);

        if (DEBUG && !c.isScanning()) System.out.println(" .... finished a round of doable tests, performed " + performed.size() + " tests\n");

        // Recording partial results.
        this.notDoneCertificates = this.notDoneCertificates.stream().distinct().collect(Collectors.toList());
        this.notDoneWarnings = this.notDoneWarnings.stream().distinct().collect(Collectors.toList());
        this.notDoneViolations = this.notDoneViolations.stream().distinct().collect(Collectors.toList());

        TAJSUtil.TajsAnalysisResults partialResults = new TAJSUtil.TajsAnalysisResults(this, !c.isScanning());

        try {
            Util.writeFile("partialResult.txt", partialResults.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

            // and propagating to them the after-load state
            if (c.getAnalysisLatticeElement().getState(allTestsBlock, newc) == null) {
                c.propagate(originalState.clone(), new BlockAndContext<>(allTestsBlock, newc), false);
            }
        }
        timers.stop(Timers.Tags.INITIAL_STATE_PROPAGATION_TO_TEST_ENTRY);
        if(valueHandler == null) {
            valueHandler = new TypeValuesHandler(info.typeNames, c, this, info);
        }
    }

    public void registerTestEntryObserver(TestBlockEntryObserver obs) {
        observers.add(obs);
    }

    @Override
    public Value evaluateCallToSymbolicFunction(HostObject hostObject, CallInfo call, Solver.SolverInterface c) {
        return new TypedSymbolicFunctionEvaluator(this, info, valueHandler).evaluateCallToSymbolicFunction(hostObject, call, c);
    }

    /**
     *
     * @param tajsTypeChecker
     * @param test
     * @param value the abstract value
     * @param t the type
     * @param path the Path from which the value is added.
     * @return if the value satisfied the type
     */
    public boolean attemptAddValue(Value value, TypeWithContext t, String path, Solver.SolverInterface c, TajsTypeChecker tajsTypeChecker, Test test) {
        value = UnknownValueResolver.getRealValue(value, c.getState());
        if (value.isNone()) {
            return true;
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

    List<TypeViolation> getViolations(Value v, TypeWithContext t, String path, GenericSolver<State, Context, CallEdge, IAnalysisMonitoring, Analysis>.SolverInterface c, TajsTypeChecker tajsTypeChecker) {
        return tajsTypeChecker.typeCheck(UnknownValueResolver.getRealValue(v, c.getState()), t.getType(), t.getTypeContext(), info, path);
    }

    // TODO: Overhaul the result toString.
    // TODO: Make sure all violations, warning and certificates are always saved (even if not scanning), and then the latest set of results are reported back in case of a timeout.
    // TODO: (Write the latest set of results to a file.)

    @Override
    public boolean shouldSkipEntry(WorkList<Context>.Entry e) {
        return sensitivity != null &&
                sensitivity.isTestContext(e.getContext()) &&
                retractionPolicy.isRetracted(sensitivity.getTest(e.getContext()));
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

    public TesterContextSensitivity getSensitivity() {return sensitivity; }

    public SuspiciousnessMonitor getSuspiciousMonitor() {return suspiciousMonitor; }

    public TestTransfersMonitor getTransferMonitor() {return transferMonitor; }

    public Set<Test> getRetractedTests() {
        return tests.stream().filter(retractionPolicy::isRetracted).collect(Collectors.toSet());
    }

}
