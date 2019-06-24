package dk.webbies.tajscheck.tajstester;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.analysis.StaticDeterminacyContextSensitivityStrategy;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.jsnodes.BeginForInNode;
import dk.brics.tajs.flowgraph.jsnodes.BeginLoopNode;
import dk.brics.tajs.flowgraph.jsnodes.EndLoopNode;
import dk.brics.tajs.flowgraph.syntaticinfo.SyntacticQueries;
import dk.brics.tajs.lattice.*;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.HashMap;
import java.util.Map;

import static dk.brics.tajs.util.Collections.newMap;

public class TesterContextSensitivity extends StaticDeterminacyContextSensitivityStrategy {

    public static final String TEST_IDENTIFIER = "$_$test";

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
    public Context makeFunctionEntryContext(State state, ObjectLabel function, FunctionCalls.CallInfo callInfo, Solver.SolverInterface c) {
        return tagTestContext(state.getContext(), super.makeFunctionEntryContext(state, function, callInfo, c), true);
    }

    public TesterContextSensitivity(SyntacticQueries syntacticInformation) {
        super(syntacticInformation);
    }

    private static Context tagTestContext(Context sourceContext, Context destinationContext, boolean overwrite) {
        if (isLocalTestContext(sourceContext) || isFunctionTestContext(sourceContext)) {
            Value t1 = sourceContext.getExtraAllocationContexts() == null ? null : sourceContext.getExtraAllocationContexts().getOrDefault(TestQualifier.instance, null);
            Value t2 = sourceContext.getFreeVariables() == null ? null : sourceContext.getFreeVariables().getOrDefault(testSpecialLocation, null);

            // if both locations contains test information they must be equal
            assert (t1 == null || t2 == null || t1.equals(t2));

            Value picked = t1 == null ? t2 : t1;

            // test context is not present or equal to the one we want to insert
            boolean condition1 = destinationContext.getFreeVariables() == null || !destinationContext.getFreeVariables().containsKey(testSpecialLocation) || destinationContext.getFreeVariables().get(testSpecialLocation).equals(picked);

            /*
             * there might be cases where we need to override.
             * For example, determinacy inherit closure variables from the function heap context, since the function might have been allocated
             * in a different context we might end-up here trying to tag a context that is already tagged as the function heap context
             */
            assert (overwrite || condition1);
            boolean condition2 = destinationContext.getFreeVariables() == null || !destinationContext.getFreeVariables().containsKey(testSpecialLocation) || destinationContext.getFreeVariables().get(testSpecialLocation).equals(picked);
            assert (overwrite || condition2);

            return tagContext(destinationContext, picked);
        }
        return destinationContext;
    }

//    public static Context untagTestContext(Context sourceContext) {
//        if (isLocalTestContext(sourceContext) || isFunctionTestContext(sourceContext)) {
//
//            Map<String, Value> newCVars = sourceContext.getFunArgs() == null || sourceContext.getFunArgs().getSelectedClosureVariables() == null ? newMap() : new HashMap<>(sourceContext.getFunArgs().getSelectedClosureVariables());
//            newCVars.remove(testSpecialLocation);
//            ContextArguments cargs = sourceContext.getFunArgs() == null ?
//                    new ContextArguments(null, null, newCVars)
//                    : sourceContext.getFunArgs().copyWith(null, newCVars, null, null);
//            return Context.make(sourceContext.getThisVal(), cargs, sourceContext.getSpecialRegisters(), sourceContext.getLocalContext(), sourceContext.getLocalContextAtEntry());
//        }
//        return sourceContext;
//    }

    private static Context tagContext(Context args, Value tag) {
        Map<String, Value> newCVars = args == null || args.getFreeVariables() == null ? newMap() : new HashMap<>(args.getFreeVariables());
        newCVars.putIfAbsent(testSpecialLocation, tag);
        return args == null ?
            Context.make(null, null, null, newCVars) :
            Context.make(args.getThisVal(), args.getSpecialRegisters(), tagContextAtEntry(args, newCVars), args.getExtraAllocationContexts(), args.getLoopUnrolling(), args.getUnknownArg(), args.getParameterNames(), args.getArguments(), newCVars, args.getFreeVariablePartitioning());
    }

    private static Context tagContextAtEntry(Context ctx, Map<String, Value> newCVars) {
        if (ctx == ctx.getContextAtEntry())
            return null;
        Context oldContextAtEntry = ctx.getContextAtEntry();
        return Context.make(oldContextAtEntry.getThisVal(), oldContextAtEntry.getSpecialRegisters(), tagContextAtEntry(oldContextAtEntry, newCVars), oldContextAtEntry.getExtraAllocationContexts(), oldContextAtEntry.getLoopUnrolling(), oldContextAtEntry.getUnknownArg(), oldContextAtEntry.getParameterNames(), oldContextAtEntry.getArguments(), newCVars, oldContextAtEntry.getFreeVariablePartitioning());
    }

    @Override
    public Context makeHeapContext(AbstractNode location, Context arguments, Solver.SolverInterface c) {
        Context hc = super.makeHeapContext(location, arguments, c);
        if(isFunctionTestContext(c.getState().getContext()) || isLocalTestContext(c.getState().getContext())) {
            String tag = getTag(c.getState().getContext());
            return tagContext(hc, Value.makeStr(tag));
        }
        return hc;
    }

    public Context makeLocalTestContext(Context from, Test test) {
        if(!contextTest.containsValue(test)) {
            contextTest.put(test.getPath() + "_" + testIds++, test);
        }
        String testId = contextTest.inverse().get(test);

        Map<Context.Qualifier, Value> testPerformed = newMap();
        if (from.getExtraAllocationContexts() != null) {
            testPerformed.putAll(from.getExtraAllocationContexts());
        }
        testPerformed.put(TestQualifier.instance, Value.makeStr(testId));

        Context newContext = Context.make(from.getThisVal(), from.getSpecialRegisters(), from.getContextAtEntry(), testPerformed, from.getLoopUnrolling(), from.getUnknownArg(), from.getParameterNames(), from.getArguments(), from.getFreeVariables(), from.getFreeVariablePartitioning());
        contextTest.putIfAbsent(testId, test);
        return newContext;
    }

//    public Context makeWideningLocalTestContext(Context from) {
//
//        Map<LocalContext.Qualifier, Value> testPerformed = newMap();
//        if (from.getLocalContext() != null) {
//            testPerformed.putAll(from.getLocalContext().getQualifiers());
//        }
//        testPerformed.put(WidenQualifier.instance, Value.makeStr("yes"));
//
//        Context newContext = Context.make(from.getThisVal(), from.getFunArgs(), from.getSpecialRegisters(), LocalContext.make(testPerformed), from.getLocalContextAtEntry());
//        return newContext;
//    }

    public static boolean isLocalTestContext(Context c) {
        return c.getExtraAllocationContexts() != null
                && c.getExtraAllocationContexts().containsKey(TestQualifier.instance);
    }

    public static boolean isFunctionTestContext(Context c) {
        return c.getFreeVariables() != null
                && c.getFreeVariables().containsKey(TEST_IDENTIFIER);
    }

    public static boolean isTestContext(Context c) { return c != null && (isFunctionTestContext(c) || isLocalTestContext(c)); }

    private static String getTag(Context c) {
        if (isLocalTestContext(c)) {
            return c.getExtraAllocationContexts().get(TestQualifier.instance).getStr();
        } else if (isFunctionTestContext(c)) {
            return c.getFreeVariables().get(TEST_IDENTIFIER).getStr();
        }

        throw new RuntimeException("Unable to get a test from context " + c);
    }

    public Test getTest(Context c) {
        return contextTest.get(getTag(c));
    }

    /**
     * Qualifier for tests.
     */
    public static class TestQualifier implements Context.Qualifier {

        private static TestQualifier instance = new TestQualifier();

        private TestQualifier() {
        }

        public static TestQualifier getInstance() {
            return instance;
        }

        @Override
        public String toString() {
            return "test-local-context";
        }
    }
//
//    /**
//     * Qualifier for widened tests.
//     */
//    public static class WidenQualifier implements LocalContext.Qualifier {
//
//        private static WidenQualifier instance = new WidenQualifier();
//
//        private WidenQualifier() {
//        }
//
//        @Override
//        public String toString() {
//            return "test-widen-context";
//        }
//    }

}
