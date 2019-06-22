package dk.webbies.tajscheck.tajstester;

import dk.au.cs.casa.typescript.types.SimpleType;
import dk.au.cs.casa.typescript.types.SimpleTypeKind;
import dk.au.cs.casa.typescript.types.Type;
import dk.brics.tajs.analysis.*;
import dk.brics.tajs.analysis.js.UserFunctionCalls;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.solver.ICallEdge;
import dk.brics.tajs.util.AnalysisException;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.benchmark.options.staticOptions.filter.CopyObjectInstantiation;
import dk.webbies.tajscheck.tajstester.data.TestCertificate;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
import dk.webbies.tajscheck.testcreator.test.*;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.stream.Collectors;

import static dk.brics.tajs.util.Collections.newList;

public class TajsTestVisitor implements TestVisitor<Boolean> {

    private final Solver.SolverInterface c;
    private final PropVarOperations pv;
    private final TajsTypeChecker typeChecker;
    private TajsTypeTester tajsTypeTester;
    private BenchmarkInfo info;
    private TypeValuesHandler valueHandler;

    public TajsTestVisitor(Solver.SolverInterface c, TajsTypeChecker typeChecker, TajsTypeTester tajsTypeTester, BenchmarkInfo info, TypeValuesHandler valueHandler) {
        this.pv = c.getAnalysis().getPropVarOperations();
        this.c = c;
        this.typeChecker = typeChecker;
        this.tajsTypeTester = tajsTypeTester;
        this.info = info;
        this.valueHandler = valueHandler;
    }

    public Value attemptGetValue(TypeWithContext t) {
        return valueHandler.findFeedbackValue(t);
    }

    @Override
    public Boolean visit(PropertyReadTest test) {
        Value baseValue = attemptGetValue(new TypeWithContext(test.getBaseType(), test.getTypeContext()));
        boolean result = true;
        for (ObjectLabel label : baseValue.getObjectLabels()) {
            Value propertyValue = UnknownValueResolver.getRealValue(pv.readPropertyValue(Collections.singletonList(label), Value.makeStr(test.getProperty())), c.getState());
            TypeWithContext closedType = new TypeWithContext(test.getPropertyType(), test.getTypeContext());
            tajsTypeTester.addCertificate(new TestCertificate(test, "Property " + test.getProperty() + " accessed on [0] has value [1]", new Value[]{baseValue, propertyValue}, c.getState()), c);
            result &= tajsTypeTester.attemptAddValue(propertyValue, closedType, test.getPath(), c, typeChecker, test);
        }
        return result;
    }

    @Override
    public Boolean visit(LoadModuleTest test) {
        Value v;
        switch (info.bench.run_method) {
            case NODE:
                v = UnknownValueResolver.getProperty(InitialStateBuilder.GLOBAL, PKey.StringPKey.make("TAJS_global_exports"), c.getState(), false);
                break;
            case BROWSER:
                ObjectLabel globalObject = InitialStateBuilder.GLOBAL;
                v = UnknownValueResolver.getProperty(globalObject, PKey.StringPKey.make(test.getPath()), c.getState(), false);
                break;
            case BOOTSTRAP:
                v = valueHandler.createValue(test.getModuleType(), test.getTypeContext());
                assert !v.isNone();
                break;
            default:
                throw new RuntimeException("Unknown");
        }
        tajsTypeTester.addCertificate(new TestCertificate(test, "Module has been loaded, its value is: [0]", new Value[]{v}, c.getState()), c);

        if (v.isNone()) {
            tajsTypeTester.addViolation(TypeViolation.definite("Module could not be found", test.getPath()), c);
        }

        return tajsTypeTester.attemptAddValue(v, new TypeWithContext(test.getModuleType(), test.getTypeContext()), test.getPath(), c, typeChecker, test);
    }

    @Override
    public Boolean visit(MethodCallTest test) {
//        final Value receiver = valueHandler.getInstantiator().getFeedbackValue(test.getObject(), test.getTypeContext());
        final Value receiver = valueHandler.findFeedbackValue(new TypeWithContext(test.getObject(), test.getTypeContext()));
        assert receiver != null;

        //noinspection AssertWithSideEffects
//        assert receiver.restrictToNotObject().isNone();

        boolean typeChecked = true;
        for (ObjectLabel receiverLabel : receiver.getObjectLabels()) {
            Value function = UnknownValueResolver.getRealValue(pv.readPropertyValue(Collections.singleton(receiverLabel), Value.makeStr(test.getPropertyName())), c.getState());
            typeChecked &= functionTest(test, Value.makeObject(receiverLabel), function, false);
        }

        return typeChecked;
    }

    public boolean functionTest(FunctionTest test, Value receiver, Value function, final boolean isConstructorCall) {
        List<Value> arguments;
        boolean restArgs;
        final Value restArgType;
        if (info.options.staticOptions.ignoreTypeDecs) {
            arguments = Collections.emptyList();
            restArgs = true;
            restArgType = valueHandler.createValue(new TypeWithContext(new SimpleType(SimpleTypeKind.Any), TypeContext.create(info)));
        } else {
            arguments = test.getParameters().stream().map(paramType -> valueHandler.createValue(paramType, test.getTypeContext())).collect(Collectors.toList());

            if (arguments.stream().anyMatch(Value::isNone)) {
                // A test for a function that is triggered but it is not able to get to the function call is definitely a problem
                // in theory this should not happen because the test should be skipped in this case, but better safe than sorry
                throw new RuntimeException("Function " + function + " triggered without arguments @" + test.getPath());
            }

            restArgs = test.isRestArgs();

            restArgType = restArgs ? valueHandler.createValue(TypesUtil.extractRestArgsType(test.getParameters()), test.getTypeContext()) : null;
            if (restArgs) {
                arguments.remove(arguments.size() - 1);
            }
        }

        boolean typeChecked = true;

        FunctionCalls.CallInfo callinfo = createCallInfo(receiver, isConstructorCall, arguments, restArgs, restArgType, c.getNode());

        Set<ObjectLabel> labels = function.getAllObjectLabels().stream().map(l -> {
            if (l.getHostObject() instanceof CopyObjectInstantiation.CopiedObjectLabel) {
                return ((CopyObjectInstantiation.CopiedObjectLabel) l.getHostObject()).getOrgLabels();
            } else {
                return Collections.singleton(l);
            }
        }).reduce(new HashSet<>(), Util::reduceSet);

        for (ObjectLabel l : labels) {
            Value returnedValue = callFunction(test, receiver, function, isConstructorCall, callinfo, l);
            typeChecked &= tajsTypeTester.attemptAddValue(returnedValue, new TypeWithContext(test.getReturnType(), test.getTypeContext()), test.getPath(), c, typeChecker, test);
        }

        return typeChecked;
    }

    private Value callFunction(FunctionTest test, Value receiver, Value function, boolean isConstructorCall, FunctionCalls.CallInfo callinfo, ObjectLabel l) {
        Value returnedValue;

        if (l.getHostObject() != null && l.getHostObject().getAPI() == HostAPIs.SPEC) {
            returnedValue = tajsTypeTester.evaluateCallToSymbolicFunction(l.getHostObject(), callinfo, c);
            // this is only needed, because we have an invariant that higher-order methods are called when control is returned to TAJS. Here that is not the case, so if we didn't do this, we wouldn't save all the feedback-values we need (because they are cleared at the end of the inner for-loop).
            State capturedState = c.getState();
            tajsTypeTester.addEndOfInnerLoopCallback(() -> {
                c.withState(capturedState, () -> {
                    tajsTypeTester.evaluateCallToSymbolicFunction(l.getHostObject(), callinfo, c);
                });
            });
        } else if (l.isHostObject()) {
            returnedValue = HostAPIs.evaluate(l.getHostObject(), callinfo, c);
        } else {
            BasicBlock implicitAfterCall = UserFunctionCalls.implicitUserFunctionCall(l, callinfo, c);

            returnedValue = UserFunctionCalls.implicitUserFunctionReturn(newList(), true, implicitAfterCall, c);
        }

        returnedValue = UnknownValueResolver.getRealValue(returnedValue, c.getState());

        if (isConstructorCall) {
            tajsTypeTester.addCertificate(new TestCertificate(test, "Function [0] has been called as constructor and returned [1]", new Value[]{function, returnedValue}, c.getState()), c);
        } else {
            tajsTypeTester.addCertificate(new TestCertificate(test, "Function [0] has been called as method with receiver [1] and returned [2]", new Value[]{function, receiver, returnedValue}, c.getState()), c);
        }

        if (returnedValue.isNone() && !(test.getReturnType() instanceof SimpleType && ((SimpleType) test.getReturnType()).getKind() == SimpleTypeKind.Never)) {
            TypeViolation violation = TypeViolation.definite("Function " + function + "(" + function.getObjectSourceLocations() + ") always returns exceptionally", test.getPath());
            if (c.isScanning()) {
                if (!tajsTypeTester.getRetractionPolicy().isTimeout(test)) {
                    tajsTypeTester.addViolation(violation, c); // only a violation if we are sure.
                }
            } else {
                tajsTypeTester.addWarning(violation, c);
            }
        }
        return returnedValue;
    }

    public static FunctionCalls.CallInfo createCallInfo(Value receiver, boolean isConstructorCall, List<Value> arguments, boolean restArgs, Value restArgType, AbstractNode node) {
        return new FunctionCalls.CallInfo() {
            @Override
            public AbstractNode getSourceNode() {
                return node;
            }

            @Override
            public AbstractNode getJSSourceNode() {
                return node;
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
            public Value getThis() {
                return receiver;
            }

            @Override
            public Value getArg(int i) {
                if (i >= arguments.size()) {
                    if (restArgs) {
                        return restArgType;
                    } else {
                        return Value.makeUndef();
                    }
                }
                return arguments.get(i);
            }

            @Override
            public int getNumberOfArgs() {
                return arguments.size();
            }

            @Override
            public Value getUnknownArg() {
                assert restArgs;
                return restArgType.join(Value.makeUndef());
            }

            @Override
            public boolean isUnknownNumberOfArgs() {
                return restArgs;
            }

            @Override
            public int getResultRegister() {
                throw new AnalysisException();
            }

            @Override
            public ExecutionContext getExecutionContext() {
                throw new AnalysisException();
            }

            @Override
            public boolean assumeFunction() {
                return false;
            }

            @Override
            public FreeVariablePartitioning getFreeVariablePartitioning() {
                return null;
            }
        };
    }

    @Override
    public Boolean visit(ConstructorCallTest test) {
        Value function = attemptGetValue(new TypeWithContext(test.getFunction(), test.getTypeContext()));
        return functionTest(test, null, function, true); // receiver is ignored, since it is a constructor-call.
    }

    @Override
    public Boolean visit(FunctionCallTest test) {
        Value receiver = Value.makeObject(InitialStateBuilder.GLOBAL).joinUndef();
        Value function = attemptGetValue(new TypeWithContext(test.getFunction(), test.getTypeContext()));
        return functionTest(test, receiver, function, false);
    }

    @Override
    public Boolean visit(UnionTypeTest test) {
        Value value = attemptGetValue(new TypeWithContext(test.getGetUnionType(), test.getTypeContext()));

        Set<Type> nonMatchedTypes = new HashSet<>(test.getGetUnionType().getElements());

        boolean typeChecked = true;

        List<TypeViolation> violationsAdded = new ArrayList<>();
        boolean definiteViolation = true;
        for (Value splitValue : TajsTypeChecker.split(value)) {
            List<Type> matchingTypes = test.getGetUnionType().getElements().stream().filter(subType -> {
                boolean matched = typeChecker.typeCheck(splitValue, subType, test.getTypeContext(), test.getPath()).stream().noneMatch(violation -> violation.definite);
                if (matched) {
                    nonMatchedTypes.remove(subType);
                }
                return matched;
            }).collect(Collectors.toList());

            if (matchingTypes.isEmpty()) {
                TypeViolation violation = TypeViolation.definite("Values (" + Util.prettyValue(value, c.getState()) + ") matched none of the unions", test.getPath());
                violationsAdded.add(violation);
                typeChecked = false;
            } else {
                definiteViolation = false;
            }

            matchingTypes.forEach(subType -> tajsTypeTester.attemptAddValue(splitValue, new TypeWithContext(subType, test.getTypeContext()), test.getPath(), c, typeChecker, test));
        }
        violationsAdded = violationsAdded.stream().filter(tajsTypeTester.getViolationsOracle()::canEmit).collect(Collectors.toList());
        if (definiteViolation) {
            violationsAdded.forEach(violation -> tajsTypeTester.addViolation(violation, c));
        } else {
            violationsAdded.stream().map(TypeViolation::asMaybeViolation).forEach(violation -> tajsTypeTester.addViolation(violation, c));
        }

        for (Type nonMatchedType : nonMatchedTypes) {
            tajsTypeTester.addWarning(TypeViolation.definite("No value matches the type: " + nonMatchedType + " in union " + test.getGetUnionType(), test.getPath()), c);
        }

        return typeChecked;
    }

    @Override
    public Boolean visit(NumberIndexTest test) {
        Value baseValue = attemptGetValue(new TypeWithContext(test.getObj(), test.getTypeContext()));
        Value propertyValue = UnknownValueResolver.getRealValue(pv.readPropertyValue(baseValue.getAllObjectLabels(), Value.makeAnyStrUInt()), c.getState());
        tajsTypeTester.addCertificate(new TestCertificate(test, "numberIndexer accessed on [0] has value [1]", new Value[]{baseValue, propertyValue}, c.getState()), c);
        TypeWithContext resultType = new TypeWithContext(test.getReturnType(), test.getTypeContext());
        return tajsTypeTester.attemptAddValue(propertyValue, resultType, test.getPath(), c, typeChecker, test);
    }

    @Override
    public Boolean visit(StringIndexTest test) {
        Value baseValue = attemptGetValue(new TypeWithContext(test.getObj(), test.getTypeContext()));
        Value propertyValue = UnknownValueResolver.getRealValue(pv.readPropertyValue(baseValue.getAllObjectLabels(), Value.makeAnyStr()), c.getState());
        tajsTypeTester.addCertificate(new TestCertificate(test, "stringIndexer accessed on [0] has value [1]", new Value[]{baseValue, propertyValue}, c.getState()), c);
        TypeWithContext resultType = new TypeWithContext(test.getReturnType(), test.getTypeContext());
        return tajsTypeTester.attemptAddValue(propertyValue, resultType, test.getPath(), c, typeChecker, test);
    }

    @Override
    public Boolean visit(PropertyWriteTest test) {
        Value baseValue = attemptGetValue(new TypeWithContext(test.getBaseType(), test.getTypeContext()));
        for (ObjectLabel label : baseValue.getObjectLabels()) {
            Value previous = UnknownValueResolver.getProperty(label, PKey.StringPKey.make(test.getProperty()), c.getState(), false);
            Value newValue = valueHandler.createValue(test.getToWrite(), test.getTypeContext());
            if (CopyObjectInstantiation.hasSomethingNew(newValue, previous, c.getState())) {
                pv.writeProperty(label, test.getProperty(), newValue);
            }
        }
        return true;
    }
}
