package dk.webbies.tajscheck.tajstester;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.PropVarOperations;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.analysis.js.UserFunctionCalls;
import dk.brics.tajs.analysis.nativeobjects.ECMAScriptObjects;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.type_testing.TypeTestRunner;
import dk.brics.tajs.util.AnalysisException;
import dk.brics.tajs.util.Pair;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.testcreator.test.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dk.brics.tajs.util.Collections.newList;
import static dk.webbies.tajscheck.util.Util.mkString;
import static dk.webbies.tajscheck.util.Util.prettyValue;

public class TajsTypeTester implements TypeTestRunner {
    private static final boolean DEBUG = true;

    private final List<Test> tests;

    private final BenchmarkInfo info;

    private final BiMap<TypeWithContext, String> typeNames = HashBiMap.create();

    final private List<TypeViolation> allViolations = newList();

    final private List<TestCertificate> allCertificates = newList();

    private final List<Test> performed = newList();

    public TajsTypeTester(List<Test> tests, BenchmarkInfo info) {
        this.tests = tests;
        this.info = info;
    }

    public int getTotalTests() {return tests.size();}

    public List<Test> getAllTests() {return tests;}

    public List<Test> getPerformedTests() {return performed;}

    public List<TypeViolation> getAllViolations() {return allViolations;}

    public List<TestCertificate> getAllCertificates() {return allCertificates;}

    public void triggerTypeTests(Solver.SolverInterface c) {
        State callState = c.getState().clone();

        TajsTestVisitor visitor = new TajsTestVisitor(callState.getExtras(), c, callState);

        performed.clear();
        for (Test t : tests) {
            // TODO: Until fixpoint.
            if(t.accept(visitor)){
                performed.add(t);
            }
        }

        if(DEBUG && c.isScanning()) {
            System.out.println("Performed " + performed.size() + "/" + tests.size() + " tests, detected " + allViolations.size() + " violations");
            List<Test> notPerformed = new LinkedList<>();
            notPerformed.addAll(tests);
            notPerformed.removeAll(performed);
            System.out.println("Tests not performed:\n   " + mkString(notPerformed.stream(), "\n   "));
            System.out.println("Test details:\n   " + mkString(allCertificates.stream(), "\n   "));
            System.out.println("Violations:\n   " + mkString(allViolations.stream(), "\n   "));
        }
    }


    public Value evaluateCallToSymbolicFunction(ECMAScriptObjects nativeObject, FunctionCalls.CallInfo call, Solver.SolverInterface c) {
        // Use the type in the context to return the right value
        System.out.println("Called function " + call.getFunctionValue());
        return Value.makeNone();
    }

    public class TajsTestVisitor implements TestVisitor<Boolean> {

        private final StateExtras se;
        private final Solver.SolverInterface c;
        private final PropVarOperations pv;
        private final State s;
        private final TypeValuesHandler typeValuesHandler;
        private final TajsTypeChecker typeChecker;

        TajsTestVisitor(StateExtras se, Solver.SolverInterface c, State s) {
            this.se = se;
            this.s = s;
            this.pv = c.getAnalysis().getPropVarOperations();
            this.c = c;
            this.typeValuesHandler = new TypeValuesHandler(typeNames, se, c);
            this.typeChecker = new TajsTypeChecker(c, info);
        }

        public boolean attemptAddValue(Value v, TypeWithContext t, Test test) {
            v = UnknownValueResolver.getRealValue(v, s);
            Pair<Value, List<TypeViolation>> tcResult = typeChecker.typeCheckAndFilter(v, t.getType(), t.getTypeContext(), info, 2, test);

            Value filteredValue = tcResult.getFirst();
            List<TypeViolation> violations = tcResult.getSecond();

            if(violations.isEmpty() && !filteredValue.isNone()) {
                typeValuesHandler.addValueForType(t, filteredValue);
                if(DEBUG) System.out.println("Value added for type:" + t + " in test " + test);
                return true;
            }
            else {
                if(c.isScanning()) {
                    allViolations.addAll(violations);
                }
                return false;
            }
        }

        public Value attemptGetValue(TypeWithContext t, Test test) {
            return typeValuesHandler.findValueForType(t);
        }


        @Override
        public Boolean visit(PropertyReadTest test) {
            Value baseValuesValue = attemptGetValue(new TypeWithContext(test.getBaseType(),test.getTypeContext()), test);
            Set<ObjectLabel> splittenObjectLabels = baseValuesValue.getObjectLabels();
            boolean toPerform = !splittenObjectLabels.isEmpty();
            if(toPerform) {
                for (ObjectLabel l : splittenObjectLabels) {
                    Value propertyValue = pv.readPropertyDirect(l, Value.makePKeyValue(PKey.mk(test.getProperty())));
                    TypeWithContext closedType = new TypeWithContext(test.getPropertyType(), test.getTypeContext());
                    if(c.isScanning()) {
                        allCertificates.add(new TestCertificate(test, "Property " + test.getProperty() + " accessed on [0] has value [1]", new Value[]{baseValuesValue, propertyValue}, s));
                    }
                    attemptAddValue(propertyValue, closedType, test);
                }
            }

            return toPerform;
        }

        @Override
        public Boolean visit(LoadModuleTest test) {
            c.withState(c.getState(), () -> {
                ObjectLabel moduleObject = ObjectLabel.mk(ECMAScriptObjects.OBJECT_MODULE, ObjectLabel.Kind.OBJECT);
                Value v = pv.readPropertyDirect(moduleObject, Value.makeStr("exports"));
                if(c.isScanning()) {
                    allCertificates.add(new TestCertificate(test, "Module has been loaded, its value is: [0]", new Value[]{v}, s));
                }
                attemptAddValue(v, new TypeWithContext(test.getModuleType(), test.getTypeContext()), test);
            });
            return true;
        }

        @Override
        public Boolean visit(MethodCallTest test) {
            Value receiverValue = attemptGetValue(new TypeWithContext(test.getObject(), test.getTypeContext()), test);
            List<Value> argumentsValues = test.getParameters().stream().map(paramType -> attemptGetValue(new TypeWithContext(paramType, test.getTypeContext()), test)).collect(Collectors.toList());

            Value propertyValue = pv.readPropertyValue(receiverValue.getAllObjectLabels(), Value.makePKeyValue(PKey.mk(test.getPropertyName())));
            //TODO: Filter this value ! ::  propertyValue = new TypeValuesFilter(propertyValue, propertyType)

            boolean toPerform = !propertyValue.isNone() && argumentsValues.stream().allMatch(x -> !x.isNone());

            if(toPerform) {
                List<Value> returnedValues = propertyValue.getAllObjectLabels().stream().map(l -> {
                    BasicBlock implicitAfterCall = UserFunctionCalls.implicitUserFunctionCall(l, new FunctionCalls.CallInfo() {

                        @Override
                        public AbstractNode getSourceNode() {
                            return c.getNode();
                        }

                        @Override
                        public AbstractNode getJSSourceNode() {
                            return c.getNode();
                        }

                        @Override
                        public boolean isConstructorCall() {
                            return false;
                        }

                        @Override
                        public Value getFunctionValue() {
                            throw new AnalysisException();
                        }

                        @Override
                        public Value getThis(State caller_state, State callee_state) {
                            return receiverValue;
                        }

                        @Override
                        public Value getArg(int i) {
                            return Value.makeUndef();
                        }

                        @Override
                        public int getNumberOfArgs() {
                            return 0;
                        }

                        @Override
                        public Value getUnknownArg() {
                            return Value.makeUndef();
                        }

                        @Override
                        public boolean isUnknownNumberOfArgs() {
                            return false;
                        }

                        @Override
                        public int getResultRegister() {
                            return AbstractNode.NO_VALUE;
                        }

                        @Override
                        public ExecutionContext getExecutionContext() {
                            return c.getState().getExecutionContext();
                        }
                    }, c);

                    Value returnedValue = UserFunctionCalls.implicitUserFunctionReturn(newList(), false, implicitAfterCall, c);
                    if(c.isScanning()) {
                        allCertificates.add(new TestCertificate(test, "Function [0] has been called as method with receiver [1] and returned [2]", new Value[]{propertyValue, receiverValue, returnedValue}, s));
                    }
                    return returnedValue;
                }).collect(Collectors.toList());

                returnedValues.stream().forEach(v -> attemptAddValue(v, new TypeWithContext(test.getReturnType(), test.getTypeContext()), test));
            }
            return toPerform;
        }

        @Override
        public Boolean visit(ConstructorCallTest test) {
            return false;
        }

        @Override
        public Boolean visit(FunctionCallTest test) {
            return false;
        }

        @Override
        public Boolean visit(FilterTest test) {
            return false;
        }

        @Override
        public Boolean visit(UnionTypeTest test) {return true; }//FIXME: (mez) I don't understand this kind of test

        @Override
        public Boolean visit(NumberIndexTest test) {
            return false;
        }

        @Override
        public Boolean visit(StringIndexTest test) {
            return false;
        }

        @Override
        public Boolean visit(PropertyWriteTest test) {
            return false;
        }
    }

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
