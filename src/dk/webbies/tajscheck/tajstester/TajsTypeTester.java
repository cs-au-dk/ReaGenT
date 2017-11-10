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
import dk.webbies.tajscheck.benchmark.options.staticOptions.RetractionPolicy;
import dk.webbies.tajscheck.testcreator.test.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newSet;
import static dk.webbies.tajscheck.util.Util.prettyValue;

public class TajsTypeTester extends DefaultAnalysisMonitoring implements TypeTestRunner {
    private static final boolean DEBUG = true;
    private static final boolean DEBUG_VALUES = false;

    private final List<Test> tests;
    private final BenchmarkInfo info;
    private TypeValuesHandler valueHandler = null;

    final private List<TypeViolation> allViolations = newList();
    final private List<TypeViolation> allWarnings = newList();
    final private List<TestCertificate> allCertificates = newList();

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
        this.transferMonitor = new TestTransfersMonitor(this, retractionPolicy::notifyTestTransfer);
        this.suspiciousMonitor = new SuspiciousnessMonitor(this, retractionPolicy::notifySuspiciousLocation);
    }

    public Timers getTimers() {return timers; }

    private Context previousTestContext = null;

    public Solver.SolverInterface c;
    public void triggerTypeTests(Solver.SolverInterface c) {
        if(allTestsBlock == null) {
            this.c = c;
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
        for (Test test : tests) {
            // Generating one local context per test
            Context newc = sensitivity.makeLocalTestContext(allTestsContext, test);

            if (retractionPolicy.isRetracted(test)) {
                continue;
            }

            // Propagate previous state into this, chaining the flow
            if(previousTestContext != null) {
                timers.start(Timers.Tags.PROPAGATING_TO_THIS_CONTEXT);
                State preState = c.getAnalysisLatticeElement().getState(allTestsBlock, previousTestContext).clone();
                if(Options.get().isNewFlowEnabled()) {
                    System.out.println("Propagating to this test context");
                }
                c.propagateToBasicBlock(preState, allTestsBlock, newc);
                if(Options.get().isNewFlowEnabled()) {
                    System.out.println("Done propagating to this test context");
                }
                timers.stop(Timers.Tags.PROPAGATING_TO_THIS_CONTEXT);
            }

            State testState = c.getAnalysisLatticeElement().getState(allTestsBlock, newc);
            // attempting to perform the test in the local context
            timers.start(Timers.Tags.TEST_TRANSFER);
            c.withState(testState, () -> {
                if (test.getTypeToTest().stream().map(type -> new TypeWithContext(type, test.getTypeContext())).map(valueHandler::findFeedbackValue).anyMatch(Objects::isNull)) {
                    if (DEBUG && !c.isScanning()) {
                        System.out.println("Skipped test " + test);
                    }
                    if (DEBUG && c.isScanning()) {
                        System.out.println("Never performed test " + test);
                    }
                    return;
                }
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

        timers.start(Timers.Tags.PROPAGATING_BACK_TO_LOOP_ENTRY);
        State finalChainingState = c.getAnalysisLatticeElement().getState(allTestsBlock, previousTestContext).clone();
        c.propagateToBasicBlock(finalChainingState, allTestsBlock, allTestsContext);
        timers.stop(Timers.Tags.PROPAGATING_BACK_TO_LOOP_ENTRY);

        if (DEBUG && !c.isScanning()) System.out.println(" .... finished a round of doable tests, performed " + performed.size() + " tests\n");
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
     * @param value the abstract value
     * @param t the type
     * @param path the Path from which the value is added.
     * @return if the value satisfied the type
     */
    public boolean attemptAddValue(Value value, TypeWithContext t, String path, Solver.SolverInterface c, TajsTypeChecker tajsTypeChecker) {
        if (value.isNone()) {
            return true;
        }
        List<TypeViolation> violations = getViolations(value, t, path, c, tajsTypeChecker);

        if(violations.isEmpty() && !value.isNone()) {
            boolean newValue = valueHandler.addFeedbackValue(t, value);
            if(DEBUG_VALUES && newValue) System.out.println("Value added for type:" + t + " path:" + path + ", value: " + value);
            if(newValue && c.isScanning()) {
                throw new RuntimeException("New values should not appear in scanning!");
//                System.err.println("New values should not appear in scanning"); // TODO: Get this to work again.
            }
        } else {
            if(DEBUG_VALUES) System.out.println("Value " + UnknownValueResolver.getRealValue(value, c.getState()) + " not added because it violates type " + t + " path:" + path);
            if(c.isScanning()) {
                allViolations.addAll(violations);
            }
        }

        return violations.isEmpty();
    }

    List<TypeViolation> getViolations(Value v, TypeWithContext t, String path, GenericSolver<State, Context, CallEdge, IAnalysisMonitoring, Analysis>.SolverInterface c, TajsTypeChecker tajsTypeChecker) {
        return tajsTypeChecker.typeCheck(UnknownValueResolver.getRealValue(v, c.getState()), t.getType(), t.getTypeContext(), info, path);
    }

    // TODO: Create a retraction-policy
    // TODO: Test that we can retract a test, and that side-effects from such a test are ignored.
    // TODO: Make an expansion-policy, to add only one method to the universe at a time. Test it on moment.
    // TODo: Overhaul the result toString.
    // TODO: Figure out why all the not-executed tests seemingly have some transfers.

    // TODO: Make sure all violations, warning and certificates are always saved (even if not scanning), and then the latest set of results are reported back in case of a timeout.
    // TODO: Write the latest set of results to a file.

    @Override
    public boolean shouldSkipEntry(WorkList<Context>.Entry e) {
        return sensitivity != null &&
                sensitivity.isTestContext(e.getContext()) &&
                retractionPolicy.isRetracted(sensitivity.getTest(e.getContext()));
    }

    /*
     * Getters, setters, and adders.
     */

    public void addViolation(TypeViolation violation) {
        allViolations.add(violation);
    }

    public void addWarning(TypeViolation warning) {
        allWarnings.add(warning);
    }

    void addCertificate(TestCertificate testCertificate) {
        allCertificates.add(testCertificate);
    }

    public int getTotalTests() {return tests.size();}

    public List<Test> getAllTests() {return tests;}

    public Collection<Test> getPerformedTests() {return performed;}

    public List<TypeViolation> getAllViolations() {return allViolations;}

    public List<TypeViolation> getAllWarnings() {
        return allWarnings;
    }

    public List<TestCertificate> getAllCertificates() {return allCertificates;}

    public TesterContextSensitivity getSensitivity() {return sensitivity; }

    public SuspiciousnessMonitor getSuspiciousMonitor() {return suspiciousMonitor; }

    public TestTransfersMonitor getTransferMonitor() {return transferMonitor; }

    public static class TestCertificate {
        final String message;
        final Value[] usedValues;
        final Test test;
        final State ownerState;

        TestCertificate(Test test, String message, Value[] usedValues, State ownerState) {
            this.test = test;
            this.message = message;
            this.usedValues = usedValues;
            this.ownerState = ownerState;
        }

        @Override
        public String toString() {
            String patternString = "\\[[0-9]+\\]";
            Pattern pattern = Pattern.compile(patternString);

            Matcher matcher = pattern.matcher(message);
            int total = 0;
            while (matcher.find())
                total++;

            String m = message;
            for(int i = 0; i < total; i++) {
                m = m.replace("[" + i + "]", prettyValue(usedValues[i], ownerState));
            }
            return test.getClass().getSimpleName() + "(" + test.getPath() +"):" + m;
        }
    }
}
