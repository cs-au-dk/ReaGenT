package dk.webbies.tajscheck.tajstester;

import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.TracifierBasicContextSensitivity;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.solver.GenericSolver;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Map;

import static dk.brics.tajs.util.Collections.newMap;

public class TesterContextSensitivity extends TracifierBasicContextSensitivity {

    public static final String TEST_IDENTIFIER = "$_$test";

    private Map<String, Test> contextTest = newMap();

    @Override
    public Context makeFunctionEntryContext(State state, ObjectLabel function, FunctionCalls.CallInfo callInfo, Value this_objs, GenericSolver<State, Context, CallEdge, IAnalysisMonitoring, Analysis>.SolverInterface c) {
        return tagTestContext(c.getState().getContext(), super.makeFunctionEntryContext(state, function, callInfo, this_objs, c));
    }

    private Context tagTestContext(Context sourceContext, Context destinationContext) {
        if ((sourceContext.getLocalContext() != null && sourceContext.getLocalContext().containsKey(TEST_IDENTIFIER))
                || (sourceContext.getFunArgs() != null && sourceContext.getFunArgs().getSelectedClosureVariables() != null && sourceContext.getFunArgs().getSelectedClosureVariables().containsKey(PKey.StringPKey.mk(TEST_IDENTIFIER)))) {
            PKey.StringPKey testSpecialLocation = PKey.StringPKey.mk(TEST_IDENTIFIER);

            Value t1 = sourceContext.getLocalContext() == null ? null : sourceContext.getLocalContext().getOrDefault(TEST_IDENTIFIER, null);
            Value t2 = sourceContext.getFunArgs() == null ? null : sourceContext.getFunArgs().getSelectedClosureVariables().getOrDefault(testSpecialLocation, null);

            // if both locations contains test information they must be equal
            assert (t1 == null || t2 == null || t1.equals(t2));

            Value picked = t1 == null ? t2 : t1;

            // test context is not present or equal to the one we want to insert
            assert (destinationContext.getLocalContext() == null || !destinationContext.getLocalContext().containsKey(TEST_IDENTIFIER) || destinationContext.getLocalContext().get(TEST_IDENTIFIER).equals(picked));
            assert (destinationContext.getFunArgs() == null || destinationContext.getFunArgs().getSelectedClosureVariables() == null || !destinationContext.getFunArgs().getSelectedClosureVariables().containsKey(testSpecialLocation) || destinationContext.getFunArgs().getSelectedClosureVariables().get(testSpecialLocation).equals(picked));

            Map<PKey.StringPKey, Value> newCVars = destinationContext.getFunArgs() == null || destinationContext.getFunArgs().getSelectedClosureVariables() == null ? newMap() : destinationContext.getFunArgs().getSelectedClosureVariables();
            newCVars.putIfAbsent(testSpecialLocation, picked);
            ContextArguments cargs = sourceContext.getFunArgs() == null ?
                    new ContextArguments(null, null, newCVars)
                    : sourceContext.getFunArgs().copyWith(null, newCVars, null, null);

            Context modified = Context.mk(destinationContext.getThisVal(), cargs, destinationContext.getSpecialRegisters(), destinationContext.getLocalContext(), destinationContext.getLocalContextAtEntry());
            return modified;
        }
        return destinationContext;
    }

    public Context makeLocalTestContext(Context from, Test test) {
        Map<String, Value> testPerformed = newMap();
        if (from.getLocalContext() != null) {
            testPerformed.putAll(from.getLocalContext());
        }
        testPerformed.put(TesterContextSensitivity.TEST_IDENTIFIER, Value.makeStr(test.getPath())); //FIXME: Is getPath unique?

        Context newContext = Context.mk(from.getThisVal(), from.getFunArgs(), from.getSpecialRegisters(),
                testPerformed, from.getLocalContextAtEntry());
        contextTest.putIfAbsent(test.getPath(), test);
        return newContext;
    }

    public boolean isLocalTestContext(Context c) {
        return c.getLocalContext() != null
                && c.getLocalContext().keySet().contains(TEST_IDENTIFIER);
    }

    public boolean isFunctionTestContext(Context c) {
        return c.getFunArgs() != null
                && c.getFunArgs().getSelectedClosureVariables() != null
                && c.getFunArgs().getSelectedClosureVariables().containsKey(PKey.StringPKey.mk(TEST_IDENTIFIER));
    }

    public boolean isTestContext(Context c) { return isFunctionTestContext(c) || isLocalTestContext(c); }

    public Test getTest(Context c) {
        if(isLocalTestContext(c)) {
            return contextTest.get(c.getLocalContext().get(TEST_IDENTIFIER).getSpecialStrings().iterator().next());
        }
        else if(isFunctionTestContext(c)) {
            String s = c.getFunArgs().getSelectedClosureVariables().get(PKey.StringPKey.mk(TEST_IDENTIFIER)).getSpecialStrings().iterator().next();
            return contextTest.get(s);
        }

        throw new RuntimeException("Unable to get a test from context " + c);
    }

}
