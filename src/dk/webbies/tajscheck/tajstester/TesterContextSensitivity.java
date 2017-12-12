package dk.webbies.tajscheck.tajstester;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.analysis.StaticDeterminacyContextSensitivityStrategy;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.jsnodes.BeginForInNode;
import dk.brics.tajs.flowgraph.jsnodes.BeginLoopNode;
import dk.brics.tajs.flowgraph.jsnodes.EndLoopNode;
import dk.brics.tajs.flowgraph.syntaticinfo.SyntacticQueries;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.solver.GenericSolver;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.HashMap;
import java.util.Map;

import static dk.brics.tajs.util.Collections.newMap;
import static dk.brics.tajs.util.Collections.singleton;

public class TesterContextSensitivity extends StaticDeterminacyContextSensitivityStrategy {

    public static final String TEST_IDENTIFIER = "$_$test";

    public static final String WIDEN_IDENTIFIER = "$_$widen";

    private BiMap<String, Test> contextTest = HashBiMap.create();

    private int testIds = 0;

    private static final String testSpecialLocation = TEST_IDENTIFIER;

    @Override
    public Context makeForInEntryContext(Context currentContext, BeginForInNode n, Value v) {
        return tagTestContext(currentContext, super.makeForInEntryContext(currentContext, n, v), false);
    }

    @Override
    public Context makeNextLoopUnrollingContext(Context currentContext, BeginLoopNode node) {
        return tagTestContext(currentContext, super.makeNextLoopUnrollingContext(currentContext, node), false);
    }

    @Override
    public Context makeLoopExitContext(Context currentContext, EndLoopNode node) {
        return tagTestContext(currentContext, super.makeLoopExitContext(currentContext, node), false);
    }

    @Override
    public Context makeSplitExitContext(Context context, String id) {
        return tagTestContext(context, super.makeSplitExitContext(context, id), false);
    }

    @Override
    public Context makeSplitContext(Context context, String id, Value qualifier) {
        return tagTestContext(context, super.makeSplitContext(context, id, qualifier), false);
    }

    public TesterContextSensitivity(SyntacticQueries syntacticInformation) {
        super(syntacticInformation);
    }


    @Override
    public Context makeFunctionEntryContext(State state, ObjectLabel function, FunctionCalls.CallInfo callInfo, Value this_objs, GenericSolver<State, Context, CallEdge, IAnalysisMonitoring, Analysis>.SolverInterface c) {
        return tagTestContext(state.getContext(), super.makeFunctionEntryContext(state, function, callInfo, this_objs, c), true);
    }

    private static Context tagTestContext(Context sourceContext, Context destinationContext, boolean overwrite) {
        if (isLocalTestContext(sourceContext) || isFunctionTestContext(sourceContext)) {
            Value t1 = sourceContext.getLocalContext() == null ? null : sourceContext.getLocalContext().getQualifiers().getOrDefault(TestQualifier.instance, null);
            Value t2 = sourceContext.getFunArgs() == null ? null : sourceContext.getFunArgs().getSelectedClosureVariables().getOrDefault(testSpecialLocation, null);

            // if both locations contains test information they must be equal
            assert (t1 == null || t2 == null || t1.equals(t2));

            Value picked = t1 == null ? t2 : t1;

            // test context is not present or equal to the one we want to insert
            boolean condition1 = destinationContext.getFunArgs() == null || destinationContext.getFunArgs().getSelectedClosureVariables() == null || !destinationContext.getFunArgs().getSelectedClosureVariables().containsKey(testSpecialLocation) || destinationContext.getFunArgs().getSelectedClosureVariables().get(testSpecialLocation).equals(picked);

            /*
             * there might be cases where we need to override.
             * For example, determinacy inherit closure variables from the function heap context, since the function might have been allocated
             * in a different context we might end-up here trying to tag a context that is already tagged as the function heap context
             */
            assert (overwrite || condition1);
            boolean condition2 = destinationContext.getFunArgs() == null || destinationContext.getFunArgs().getSelectedClosureVariables() == null || !destinationContext.getFunArgs().getSelectedClosureVariables().containsKey(testSpecialLocation) || destinationContext.getFunArgs().getSelectedClosureVariables().get(testSpecialLocation).equals(picked);
            assert (overwrite || condition2);

            ContextArguments cargs = tagContextArguments(sourceContext.getFunArgs(), destinationContext.getFunArgs(), picked);

            return Context.make(destinationContext.getThisVal(), cargs, destinationContext.getSpecialRegisters(), destinationContext.getLocalContext(), destinationContext.getLocalContextAtEntry());
        }
        return destinationContext;
    }

    public static Context untagTestContext(Context sourceContext) {
        if (isLocalTestContext(sourceContext) || isFunctionTestContext(sourceContext)) {

            Map<String, Value> newCVars = sourceContext.getFunArgs() == null || sourceContext.getFunArgs().getSelectedClosureVariables() == null ? newMap() : new HashMap<>(sourceContext.getFunArgs().getSelectedClosureVariables());
            newCVars.remove(testSpecialLocation);
            ContextArguments cargs = sourceContext.getFunArgs() == null ?
                    new ContextArguments(null, null, newCVars)
                    : sourceContext.getFunArgs().copyWith(null, newCVars, null, null);
            return Context.make(sourceContext.getThisVal(), cargs, sourceContext.getSpecialRegisters(), sourceContext.getLocalContext(), sourceContext.getLocalContextAtEntry());
        }
        return sourceContext;
    }

    private static ContextArguments tagContextArguments(ContextArguments sourceArgs, ContextArguments args, Value tag) {
        Map<String, Value> newCVars = args == null || args.getSelectedClosureVariables() == null ? newMap() : new HashMap<>(args.getSelectedClosureVariables());
        newCVars.putIfAbsent(testSpecialLocation, tag);
        return sourceArgs == null ?
                new ContextArguments(null, null, newCVars)
                : sourceArgs.copyWith(null, newCVars, null, null);
    }

    @Override
    public HeapContext makeHeapContext(AbstractNode location, ContextArguments arguments, Solver.SolverInterface c) {
        HeapContext hc = super.makeHeapContext(location, arguments, c);
        if(isFunctionTestContext(c.getState().getContext())) {
            String tag = getTag(c.getState().getContext());
            return hc.copyWith(tagContextArguments(arguments, hc.getFunctionArguments(), Value.makeSpecialStrings(singleton(tag))), null);
        }
        return hc;
    }

    public Context makeLocalTestContext(Context from, Test test) {
        if(!contextTest.containsValue(test)) {
            contextTest.put(test.getPath() + "_" + testIds++, test);
        }
        String testId = contextTest.inverse().get(test);

        Map<LocalContext.Qualifier, Value> testPerformed = newMap();
        if (from.getLocalContext() != null) {
            testPerformed.putAll(from.getLocalContext().getQualifiers());
        }
        testPerformed.put(TestQualifier.instance, Value.makeSpecialStrings(singleton(testId)));

        Context newContext = Context.make(from.getThisVal(), from.getFunArgs(), from.getSpecialRegisters(), LocalContext.make(testPerformed), from.getLocalContextAtEntry());
        contextTest.putIfAbsent(testId, test);
        return newContext;
    }

    public Context makeWideningLocalTestContext(Context from) {

        Map<LocalContext.Qualifier, Value> testPerformed = newMap();
        if (from.getLocalContext() != null) {
            testPerformed.putAll(from.getLocalContext().getQualifiers());
        }
        testPerformed.put(WidenQualifier.instance, Value.makeSpecialStrings(singleton("yes")));

        Context newContext = Context.make(from.getThisVal(), from.getFunArgs(), from.getSpecialRegisters(), LocalContext.make(testPerformed), from.getLocalContextAtEntry());
        return newContext;
    }

    public static boolean isLocalTestContext(Context c) {
        return c.getLocalContext() != null
                && c.getLocalContext().getQualifiers().containsKey(TestQualifier.instance);
    }

    public static boolean isFunctionTestContext(Context c) {
        return c.getFunArgs() != null
                && c.getFunArgs().getSelectedClosureVariables() != null
                && c.getFunArgs().getSelectedClosureVariables().containsKey(TEST_IDENTIFIER);
    }

    public static boolean isTestContext(Context c) { return c != null && (isFunctionTestContext(c) || isLocalTestContext(c)); }

    public static String getTag(Context c) {
        if(isLocalTestContext(c)) {
            return c.getLocalContext().getQualifiers().get(TestQualifier.instance).getSpecialStrings().iterator().next();
        }
        else if(isFunctionTestContext(c)) {
            return c.getFunArgs().getSelectedClosureVariables().get(TEST_IDENTIFIER).getSpecialStrings().iterator().next();
        }

        throw new RuntimeException("Unable to get a test from context " + c);
    }

    public Test getTest(Context c) {
        return contextTest.get(getTag(c));
    }

    /**
     * Qualifier for tests.
     */
    public static class TestQualifier implements LocalContext.Qualifier {

        private static TestQualifier instance = new TestQualifier();

        private TestQualifier() {
        }

        @Override
        public String toString() {
            return "test-local-context";
        }
    }

    /**
     * Qualifier for widened tests.
     */
    public static class WidenQualifier implements LocalContext.Qualifier {

        private static WidenQualifier instance = new WidenQualifier();

        private WidenQualifier() {
        }

        @Override
        public String toString() {
            return "test-widen-context";
        }
    }

}
