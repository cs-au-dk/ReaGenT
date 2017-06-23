package dk.webbies.tajscheck.tajstester;

import dk.au.cs.casa.typescript.types.Type;
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
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.testcreator.test.*;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newMap;
import static dk.webbies.tajscheck.util.Util.mkString;
import static dk.webbies.tajscheck.util.Util.prettyValue;

public class TajsTypeTester implements TypeTestRunner {
    private static final boolean DEBUG = true;
    private static final boolean DEBUG_VALUES = true;

    private final List<Test> tests;

    private final BenchmarkInfo info;

    final private List<TypeViolation> allViolations = newList();

    final private List<TestCertificate> allCertificates = newList();

    private final List<Test> performed = newList();

    private TypeValuesHandler valueHandler = null;
    private String LOCAL_CONTEXT_MARKER = "TAJSCheckTest";

    public TajsTypeTester(List<Test> tests, BenchmarkInfo info) {
        this.tests = tests;
        this.info = info;
    }

    public int getTotalTests() {return tests.size();}

    public List<Test> getAllTests() {return tests;}

    public List<Test> getPerformedTests() {return performed;}

    public List<TypeViolation> getAllViolations() {return allViolations;}

    public List<TestCertificate> getAllCertificates() {return allCertificates;}

    private boolean isInTestContext(Solver.SolverInterface c) {
        return c.getState().getContext() != null
                && c.getState().getContext().getLocalContext() != null
                && c.getState().getContext().getLocalContext().keySet().contains(LOCAL_CONTEXT_MARKER);
    }

    public void triggerTypeTests(Solver.SolverInterface c) {
        if (valueHandler == null)
            valueHandler = new TypeValuesHandler(info.typeNames, c, info.getSpec());
        TajsTestVisitor visitor = new TajsTestVisitor(c, valueHandler);
        State originalState = c.getState().clone();

        performed.clear();
        for (int i = 0; i < tests.size(); i++) {
            Test test = tests.get(i);
            if (test.getTypeToTest().stream().map(type -> new TypeWithContext(type, test.getTypeContext())).map(valueHandler::findFeedbackValue).anyMatch(Objects::isNull)) {
                if (DEBUG && !c.isScanning())
                    System.out.println("Skipped test " + test);
                continue;
            }
            if (DEBUG && !c.isScanning()) System.out.println("Performing test " + test);

            performed.add(test);

            // Generating one local context per test
            Context currentContext = originalState.getContext();
            Map<String, Value> testPerformed = newMap();
            if (currentContext.getLocalContext() != null) {
                testPerformed.putAll(currentContext.getLocalContext());
            }
            testPerformed.put(LOCAL_CONTEXT_MARKER, Value.makeStr(i + ":" + test.getPath()));

            Context newc = Context.mk(currentContext.getThisVal(), currentContext.getFunArgs(), currentContext.getSpecialRegisters(),
                    testPerformed, currentContext.getLocalContextAtEntry());

            State newState = originalState.clone();
            newState.setContext(newc);

            c.getAnalysisLatticeElement().propagate(newState.clone(), newState.getBasicBlock(), newState.getContext(), false);

            // performing the test in the local context
            c.withState(newState, () -> {
                test.accept(visitor);
            });
        }
        if (DEBUG && !c.isScanning()) System.out.println(" .... finished a round of doable tests, performed " + performed.size() + " tests\n");

        if (DEBUG && c.isScanning() && !isInTestContext(c)) {
            System.out.println("Performed " + performed.size() + "/" + tests.size() + " tests, detected " + allViolations.size() + " violations");
            List<Test> notPerformed = new LinkedList<>();
            notPerformed.addAll(tests);
            notPerformed.removeAll(performed);
            System.out.println("Tests not performed:\n   " + mkString(notPerformed, "\n   "));
            System.out.println("Test details:\n   " + mkString(allCertificates, "\n   "));
            System.out.println("Violations:\n   " + mkString(allViolations, "\n   "));
        }
    }


    public Value evaluateCallToSymbolicFunction(ECMAScriptObjects nativeObject, FunctionCalls.CallInfo call, Solver.SolverInterface c) {
        // Use the type in the context to return the right value
        System.out.println("Called function " + call.getFunctionValue());
        return Value.makeNone();
    }

    public class TajsTestVisitor implements TestVisitor<Boolean> {

        private final Solver.SolverInterface c;
        private final PropVarOperations pv;
        private final TypeValuesHandler typeValuesHandler;
        private final TajsTypeChecker typeChecker;

        TajsTestVisitor(Solver.SolverInterface c, TypeValuesHandler typeValuesHandler) {
            this.pv = c.getAnalysis().getPropVarOperations();
            this.c = c;
            this.typeValuesHandler = typeValuesHandler;
            this.typeChecker = new TajsTypeChecker(c, info);
        }

        private <T> boolean testValues(Collection<T> values, Predicate<T> consumer) {
            boolean progress = false;
            for (T value : values) {
                if (consumer.test(value)) {
                    progress = true;
                }
            }
            return progress;
        }

        /**
         *
         * @param v the abstract value
         * @param t the type
         * @param test the test that was performed.
         * @return if there was progress, which happens if any state changes (a new value is added).
         */
        public boolean attemptAddValue(Value v, TypeWithContext t, Test test) {
            if (v.isNone()) {
                return false;
            }
            State s = c.getState();
            v = UnknownValueResolver.getRealValue(v, s);
            Pair<Value, List<TypeViolation>> tcResult = typeChecker.typeCheckAndFilter(v, t.getType(), t.getTypeContext(), info, 2, test);

            Value filteredValue = tcResult.getFirst();
            List<TypeViolation> violations = tcResult.getSecond();

            if(violations.isEmpty() && !filteredValue.isNone()) {
                boolean newValue = typeValuesHandler.addFeedbackValue(t, filteredValue);
                if(DEBUG_VALUES && newValue) System.out.println("Value added for type:" + t + " in test " + test + ", value: " + filteredValue);
                if(newValue && c.isScanning()) throw new RuntimeException("New values should not appear in scanning!");
                return newValue;
            } else {
                if(DEBUG_VALUES) System.out.println("Value " + v + " not added because it violates type " + t + " in test " + test);
                if(c.isScanning()) {
                    allViolations.addAll(violations);
                }
                return false;
            }
        }

        public Value attemptGetValue(Type t, TypeContext context) {
            return typeValuesHandler.findFeedbackValue(new TypeWithContext(t, context));
        }

        public Value attemptGetValue(TypeWithContext t) {
            return typeValuesHandler.findFeedbackValue(t);
        }


        @Override
        public Boolean visit(PropertyReadTest test) {
            State s = c.getState();
            Value baseValue = attemptGetValue(new TypeWithContext(test.getBaseType(),test.getTypeContext()));

            return testValues(baseValue.getObjectLabels(), (label) -> {
                Value propertyValue = pv.readPropertyValue(Collections.singletonList(label), Value.makePKeyValue(PKey.mk(test.getProperty())));
                TypeWithContext closedType = new TypeWithContext(test.getPropertyType(), test.getTypeContext());
                if(c.isScanning()) {
                    allCertificates.add(new TestCertificate(test, "Property " + test.getProperty() + " accessed on [0] has value [1]", new Value[]{baseValue, propertyValue}, s));
                }
                return attemptAddValue(propertyValue, closedType, test);
            });
        }

        @Override
        public Boolean visit(LoadModuleTest test) {
            return c.withState(c.getState(), () -> {
                if (info.bench.run_method == Benchmark.RUN_METHOD.NODE) {
                    ObjectLabel moduleObject = ObjectLabel.mk(ECMAScriptObjects.OBJECT_MODULE, ObjectLabel.Kind.OBJECT);
                    Value v = pv.readPropertyDirect(moduleObject, Value.makeStr("exports"));
                    if(c.isScanning()) {
                        allCertificates.add(new TestCertificate(test, "Module has been loaded, its value is: [0]", new Value[]{v}, c.getState()));
                    }
                    return attemptAddValue(v, new TypeWithContext(test.getModuleType(), test.getTypeContext()), test);
                } else {
                    throw new RuntimeException("Not supported as of yet.");
                }
            });
        }

        @Override
        public Boolean visit(MethodCallTest test) {
            Value receiver = attemptGetValue(new TypeWithContext(test.getObject(), test.getTypeContext()));

            List<Value> arguments = test.getParameters().stream().map(paramType -> typeValuesHandler.createValue(paramType, test.getTypeContext())).collect(Collectors.toList());

            Value propertyValue = pv.readPropertyValue(receiver.getAllObjectLabels(), Value.makePKeyValue(PKey.mk(test.getPropertyName())));
            //TODO: Filter this value ! ::  propertyValue = new TypeValuesFilter(propertyValue, propertyType)

            if (test.isRestArgs()) {
                throw new RuntimeException();
            }

            return testValues(propertyValue.getAllObjectLabels(), l -> {
                FunctionCalls.CallInfo callinfo = new FunctionCalls.CallInfo() {

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
                        return receiver; // TODO: synthesize the receiver?
                    }

                    @Override
                    public Value getArg(int i) {
                        return arguments.get(i);
                    }

                    @Override
                    public int getNumberOfArgs() {
                        return arguments.size();
                    }

                    @Override
                    public Value getUnknownArg() {
                        throw new AnalysisException();
                    }

                    @Override
                    public boolean isUnknownNumberOfArgs() {
                        return false;
                    }

                    @Override
                    public int getResultRegister() {
                        throw new AnalysisException();
                    }

                    @Override
                    public ExecutionContext getExecutionContext() {
                        throw new AnalysisException();
                    }

                };
                BasicBlock implicitAfterCall = UserFunctionCalls.implicitUserFunctionCall(l, callinfo, c);

                Value returnedValue = UserFunctionCalls.implicitUserFunctionReturn(newList(), false, implicitAfterCall, c);


                if (c.isScanning()) {
                    allCertificates.add(new TestCertificate(test, "Function [0] has been called as method with receiver [1] and returned [2]", new Value[]{propertyValue, receiver, returnedValue}, c.getState()));
                }
                return attemptAddValue(returnedValue, new TypeWithContext(test.getReturnType(), test.getTypeContext()), test);
            });
        }

        @Override
        public Boolean visit(ConstructorCallTest test) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(FunctionCallTest test) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(FilterTest test) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(UnionTypeTest test) {
            Value value = attemptGetValue(test.getGetUnionType(), test.getTypeContext());

            List<Type> matchingTypes = test.getGetUnionType().getElements().stream().filter(subType ->
                    typeChecker.typeCheckAndFilter(value, subType, test.getTypeContext(), info, 2, test).getSecond().isEmpty()
            ).collect(Collectors.toList());

            if (matchingTypes.isEmpty()) {
                if(c.isScanning()) {
                    allViolations.addAll(Collections.singletonList(new TypeViolation("Values matched none of the unions", test)));
                }
            }

            return testValues(matchingTypes, subType -> attemptAddValue(value, new TypeWithContext(subType, test.getTypeContext()), test));
        }

        @Override
        public Boolean visit(NumberIndexTest test) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(StringIndexTest test) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(PropertyWriteTest test) {
            throw new RuntimeException();
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
