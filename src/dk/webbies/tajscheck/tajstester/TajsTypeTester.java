package dk.webbies.tajscheck.tajstester;

import dk.au.cs.casa.typescript.types.*;
import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.analysis.PropVarOperations;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.analysis.js.UserFunctionCalls;
import dk.brics.tajs.analysis.nativeobjects.ECMAScriptObjects;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.monitoring.DefaultAnalysisMonitoring;
import dk.brics.tajs.solver.BlockAndContext;
import dk.brics.tajs.type_testing.TypeTestRunner;
import dk.brics.tajs.util.AnalysisException;
import dk.brics.tajs.util.Pair;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.typeCreator.SpecObjects;
import dk.webbies.tajscheck.testcreator.test.*;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;
import dk.webbies.tajscheck.util.ArrayListMultiMap;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newSet;
import static dk.webbies.tajscheck.util.Util.mkString;
import static dk.webbies.tajscheck.util.Util.prettyValue;

public class TajsTypeTester extends DefaultAnalysisMonitoring implements TypeTestRunner {
    private static final boolean DEBUG = true;

    private static final boolean DEBUG_VALUES = false;

    private final TesterContextSensitivity testerContextSensitivity;

    private final List<Test> tests;

    private final BenchmarkInfo info;

    final private List<TypeViolation> allViolations = newList();

    final private List<TestCertificate> allCertificates = newList();

    private final List<Test> performed = newList();

    private final ArrayListMultiMap<Test, Test> depends;

    private TypeValuesHandler valueHandler = null;

    private BasicBlock allTestsBlock;

    private Context allTestsContext;

    private Set<Test> retractedTests = newSet();

    public TajsTypeTester(List<Test> tests, BenchmarkInfo info, TesterContextSensitivity testerContextSensitivity) {
        this.tests = tests;
        this.info = info;
        this.testerContextSensitivity = testerContextSensitivity;
        this.depends = new ArrayListMultiMap<>();
        for (Test dependsTest : tests) {
            for (Test onTest : tests) {
                if (depends(dependsTest, onTest)) {
                    depends.put(dependsTest, onTest);
                }
            }
        }
    }

    public int getTotalTests() {return tests.size();}

    public List<Test> getAllTests() {return tests;}

    public List<Test> getPerformedTests() {return performed;}

    public List<TypeViolation> getAllViolations() {return allViolations;}

    public List<TestCertificate> getAllCertificates() {return allCertificates;}

    public void triggerTypeTests(Solver.SolverInterface c) {

        if(allTestsBlock == null) {
            init(c);
        }

        if(testerContextSensitivity.isLocalTestContext(c.getState().getContext())) {
            if(!c.isScanning()) {
                if(DEBUG_VALUES) System.out.println("New flow for " + c.getState().getBasicBlock().getIndex() + ", " + c.getState().getContext());
                // Then we can re-run the tests to see if more can be performed
                c.addToWorklist(allTestsBlock, allTestsContext);
            }
            return;
        }

        TajsTestVisitor visitor = new TajsTestVisitor(c, valueHandler);

        performed.clear();
        for (Test test : tests) {
            // Generating one local context per test
            Context newc = testerContextSensitivity.makeLocalTestContext(allTestsContext, test);

            State testState = c.getAnalysisLatticeElement().getState(allTestsBlock, newc);

            // attempting to perform the test in the local context
            c.withState(testState, () -> {

                if (test.getTypeToTest().stream().map(type -> new TypeWithContext(type, test.getTypeContext())).map(valueHandler::findFeedbackValue).anyMatch(Objects::isNull)) {
                    if (DEBUG && !c.isScanning()) {
                        System.out.println("Skipped test " + test);
                    }
                    return;
                }
                if (DEBUG && !c.isScanning()) System.out.println("Performing test " + test);

                performed.add(test);

                test.accept(visitor);
            });
        }

        // we propagate states for each depending states
        for (Map.Entry<Test, Collection<Test>> entry : depends.asMap().entrySet()) {
            Test dependingTest = entry.getKey();
            for (Test on : entry.getValue()) {
                Context dependentContext = testerContextSensitivity.makeLocalTestContext(allTestsContext, dependingTest);
                Context onContext = testerContextSensitivity.makeLocalTestContext(allTestsContext, on);
                State source = c.getAnalysisLatticeElement().getState(allTestsBlock, onContext);
                c.propagateToBasicBlock(source.clone(), allTestsBlock, dependentContext);
            }
        }

        if (DEBUG && !c.isScanning()) System.out.println(" .... finished a round of doable tests, performed " + performed.size() + " tests\n");

        if (DEBUG && c.isScanning()) {
            System.out.println("Performed " + performed.size() + "/" + tests.size() + " tests, detected " + allViolations.size() + " violations");
            List<Test> notPerformed = new LinkedList<>();
            notPerformed.addAll(tests);
            notPerformed.removeAll(performed);
            System.out.println("Tests not performed:\n   " + mkString(notPerformed, "\n   "));
            System.out.println("Test details:\n   " + mkString(allCertificates, "\n   "));
            System.out.println("Violations:\n   " + mkString(allViolations, "\n   "));
        }
    }

    private void init(Solver.SolverInterface c) {
        allTestsBlock = c.getState().getBasicBlock();
        allTestsContext = c.getState().getContext();

        State originalState = c.getState().clone();
        for (Test test : tests) {
            // Generating one local context per test
            Context newc = testerContextSensitivity.makeLocalTestContext(allTestsContext, test);

            // and propagating to them the after-load state
            if (c.getAnalysisLatticeElement().getState(allTestsBlock, newc) == null) {
                c.propagate(originalState.clone(), new BlockAndContext<>(allTestsBlock, newc), false);
            }
        }
        if(valueHandler == null)
            valueHandler = new TypeValuesHandler(info.typeNames, c, info);
    }

    // returns true if "dependent" depends on "on".
    private boolean depends(Test dependent, Test on) {
        if (!info.options.staticOptions.limitSideEffects) {
            return true;
        }
        AtomicBoolean result = new AtomicBoolean(false);

        Set<TypeWithContext> consumes = new HashSet<>();
        for (Type toTest : dependent.getTypeToTest()) {
            consumes.add(new TypeWithContext(toTest, dependent.getTypeContext()));
        }

        for (Type produces : on.getProduces()) {
            new TypesUtil(info).forAllSubTypes(produces, on.getTypeContext(), produce -> {
                if (consumes.contains(produce)) {
                    result.set(true);
                }
            });
        }

        if (result.get()) {
            System.out.println(on.getPath() + " -> " + dependent.getPath());
        }

        return result.get();
    }


    @Override
    public Value evaluateCallToSymbolicFunction(HostObject hostObject, FunctionCalls.CallInfo call, Solver.SolverInterface c) {
        TypeWithContext typeWithContext = ((SpecObjects.FullPath) hostObject).getType();
        String path = ((SpecObjects.FullPath) hostObject).asText();

        if (call.isUnknownNumberOfArgs() || call.isConstructorCall()) {
            throw new RuntimeException(call.isUnknownNumberOfArgs() + " - " + call.isConstructorCall());
        }

        if (typeWithContext.getType() instanceof InterfaceType) {
            dk.webbies.tajscheck.util.Pair<InterfaceType, TypeContext> pair = new TypesUtil(info).constructSyntheticInterfaceWithBaseTypes((InterfaceType) typeWithContext.getType(), info.typeNames, info.freeGenericsFinder);
            InterfaceType inter = pair.getLeft();
            TypeContext context = typeWithContext.getTypeContext().append(pair.getRight());

            List<Signature> signatures = inter.getDeclaredCallSignatures();
            if (signatures.size() != 1) {
                throw new RuntimeException();
            }

            Signature signature = signatures.get(0);

            if (signature.isHasRestParameter()) {
                throw new RuntimeException();
            }
            if (signature.getParameters().size() != call.getNumberOfArgs()) {
                if (c.isScanning()) {
                    allViolations.add(new TypeViolation("Expected  " + signature.getParameters().size() + " args, got " + call.getNumberOfArgs(), path));
                }
            }

            for (int i = 0; i < Math.min(signature.getParameters().size(), call.getNumberOfArgs()); i++) {
                attemptAddValue(call.getArg(i), new TypeWithContext(signature.getParameters().get(i).getType(), context), null, c);
            }
            return valueHandler.createValue(signature.getResolvedReturnType(), context);
        } else {
            throw new RuntimeException(typeWithContext.getType().getClass().getSimpleName());
        }
    }


    @Override
    public void visitBlockTransfer(BasicBlock b, State s) {
        super.visitBlockTransfer(b, s);
        if(testerContextSensitivity.isTestContext(s.getContext())) {
            Test t = testerContextSensitivity.getTest(s.getContext());
            if(retractedTests.contains(t)) {
                s.setToNone();
            }
        }
    }

    /**
     *
     * @param v the abstract value
     * @param t the type
     * @param path the Path from which the value is added.
     * @return if there was progress, which happens if any state changes (a new value is added).
     */
    public void attemptAddValue(Value v, TypeWithContext t, String path, Solver.SolverInterface c) {
        if (v.isNone()) {
            return;
        }
        State s = c.getState();
        v = UnknownValueResolver.getRealValue(v, s);
        Pair<Value, List<TypeViolation>> tcResult = new TajsTypeChecker(c, info).typeCheckAndFilter(v, t.getType(), t.getTypeContext(), info, 2, path);

        Value filteredValue = tcResult.getFirst();
        List<TypeViolation> violations = tcResult.getSecond();

        if(violations.isEmpty() && !filteredValue.isNone()) {
            boolean newValue = valueHandler.addFeedbackValue(t, filteredValue);
            if(DEBUG_VALUES && newValue) System.out.println("Value added for type:" + t + " path:" + path + ", value: " + filteredValue);
            if(newValue && c.isScanning()) throw new RuntimeException("New values should not appear in scanning!");
        } else {
            if(DEBUG_VALUES) System.out.println("Value " + v + " not added because it violates type " + t + " path:" + path);
            if(c.isScanning()) {
                allViolations.addAll(violations);
            }
        }
    }

    public void retractTest(Test t) {
        retractedTests.add(t);
    }

    public class TajsTestVisitor implements TestVisitor<Void> {

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

        public Value attemptGetValue(Type t, TypeContext context) {
            return typeValuesHandler.findFeedbackValue(new TypeWithContext(t, context));
        }

        public Value attemptGetValue(TypeWithContext t) {
            return typeValuesHandler.findFeedbackValue(t);
        }

        @Override
        public Void visit(PropertyReadTest test) {
            State s = c.getState();
            Value baseValue = attemptGetValue(new TypeWithContext(test.getBaseType(),test.getTypeContext()));
            baseValue.getObjectLabels().forEach(label -> {
                Value propertyValue = UnknownValueResolver.getProperty(label, PKey.mk(test.getProperty()), c.getState(), false);
                TypeWithContext closedType = new TypeWithContext(test.getPropertyType(), test.getTypeContext());
                if(c.isScanning()) {
                    allCertificates.add(new TestCertificate(test, "Property " + test.getProperty() + " accessed on [0] has value [1]", new Value[]{baseValue, propertyValue}, s));
                }
                attemptAddValue(propertyValue, closedType, test.getPath(), c);
            });
            return null;
        }

        @Override
        public Void visit(LoadModuleTest test) {
            Value v;
            switch (info.bench.run_method) {
                case NODE:
                    ObjectLabel moduleObject = ObjectLabel.mk(ECMAScriptObjects.OBJECT_MODULE, ObjectLabel.Kind.OBJECT);
                    v = UnknownValueResolver.getProperty(moduleObject, PKey.mk("exports"), c.getState(), false);
                    break;
                case BROWSER:
                    ObjectLabel globalObject = InitialStateBuilder.GLOBAL;
                    v = UnknownValueResolver.getProperty(globalObject, PKey.mk(test.getPath()), c.getState(), false);
                    break;
                case BOOTSTRAP:
                    v = valueHandler.createValue(test.getModuleType(), test.getTypeContext());
                    assert !v.isNone();
                    break;
                default:
                    throw new RuntimeException("Unknown");
            }
            if (c.isScanning()) {
                allCertificates.add(new TestCertificate(test, "Module has been loaded, its value is: [0]", new Value[]{v}, c.getState()));
            }

            attemptAddValue(v, new TypeWithContext(test.getModuleType(), test.getTypeContext()), test.getPath(), c);
            return null;
        }

        @Override
        public Void visit(MethodCallTest test) {
            final Value receiver = attemptGetValue(new TypeWithContext(test.getObject(), test.getTypeContext()));
            //TODO: Filter this value ! ::  propertyValue = new TypeValuesFilter(propertyValue, propertyType)
            //Value function = receiver.getAllObjectLabels().stream().map(l -> UnknownValueResolver.getProperty(l, PKey.mk(test.getPropertyName()), c.getState(), false)).reduce(Value.makeNone(), (x,y) -> UnknownValueResolver.join(x, y, c.getState()));
            Value function = UnknownValueResolver.getRealValue(pv.readPropertyValue(receiver.getAllObjectLabels(), Value.makePKeyValue(PKey.mk(test.getPropertyName()))), c.getState());
            return functionTest(test, receiver, function, false);
        }

        private Void functionTest(FunctionTest test, Value receiver, Value function, final boolean isConstructorCall) {
            List<Value> arguments = test.getParameters().stream().map(paramType -> typeValuesHandler.createValue(paramType, test.getTypeContext())).collect(Collectors.toList());

            if (arguments.stream().anyMatch(Value::isNone)) {
                return null;
            }

            if (test.isRestArgs()) {
                throw new RuntimeException();
            }

            function.getAllObjectLabels().forEach(l -> {
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
                        return isConstructorCall;
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

                Value returnedValue = UserFunctionCalls.implicitUserFunctionReturn(newList(), true, implicitAfterCall, c);

                if (c.isScanning()) {
                    if (isConstructorCall) {
                        allCertificates.add(new TestCertificate(test, "Function [0] has been called as constructor and returned [1]", new Value[]{function, returnedValue}, c.getState()));
                    } else {
                        allCertificates.add(new TestCertificate(test, "Function [0] has been called as method with receiver [1] and returned [2]", new Value[]{function, receiver, returnedValue}, c.getState()));
                    }

                    if (returnedValue.isNone() && !(test.getReturnType() instanceof SimpleType && ((SimpleType) test.getReturnType()).getKind() == SimpleTypeKind.Never)) {
                        allViolations.add(new TypeViolation("Function " + function + " always returns exceptionally", test.getPath()));
                    }
                }
                attemptAddValue(returnedValue, new TypeWithContext(test.getReturnType(), test.getTypeContext()), test.getPath(), c);
            });

            return null;
        }

        @Override
        public Void visit(ConstructorCallTest test) {
            Value function = attemptGetValue(test.getFunction(), test.getTypeContext());
            return functionTest(test, null, function, true); // receiver is ignored, since it is a constructor-call.
        }

        @Override
        public Void visit(FunctionCallTest test) {
            Value receiver = Value.makeObject(InitialStateBuilder.GLOBAL).joinUndef();
            Value function = attemptGetValue(test.getFunction(), test.getTypeContext());
            return functionTest(test, receiver, function, false);
        }

        @Override
        public Void visit(FilterTest test) {
            throw new RuntimeException();
        }

        @Override
        public Void visit(UnionTypeTest test) {
            Value value = attemptGetValue(test.getGetUnionType(), test.getTypeContext());

            List<Type> matchingTypes = test.getGetUnionType().getElements().stream().filter(subType ->
                    typeChecker.typeCheckAndFilter(value, subType, test.getTypeContext(), info, 2, test.getPath()).getSecond().isEmpty()
            ).collect(Collectors.toList());

            if (matchingTypes.isEmpty()) {
                if(c.isScanning()) {
                    allViolations.addAll(Collections.singletonList(new TypeViolation("Values matched none of the unions", test.getPath())));
                }
            }

            matchingTypes.forEach(subType -> attemptAddValue(value, new TypeWithContext(subType, test.getTypeContext()), test.getPath(), c));
            return null;
        }

        @Override
        public Void visit(NumberIndexTest test) {
            throw new RuntimeException();
        }

        @Override
        public Void visit(StringIndexTest test) {
            throw new RuntimeException();
        }

        @Override
        public Void visit(PropertyWriteTest test) {
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
