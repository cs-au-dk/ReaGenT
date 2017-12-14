package dk.webbies.tajscheck.tajstester;

import dk.au.cs.casa.typescript.types.SimpleType;
import dk.au.cs.casa.typescript.types.SimpleTypeKind;
import dk.au.cs.casa.typescript.types.Type;
import dk.brics.tajs.analysis.*;
import dk.brics.tajs.analysis.js.UserFunctionCalls;
import dk.brics.tajs.analysis.nativeobjects.ECMAScriptObjects;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.solver.ICallEdge;
import dk.brics.tajs.util.AnalysisException;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.data.TestCertificate;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
import dk.webbies.tajscheck.testcreator.test.*;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dk.brics.tajs.util.Collections.newList;

public class TajsTestVisitor implements TestVisitor<Boolean> {

    private final Solver.SolverInterface c;
    private final PropVarOperations pv;
    private final TypeValuesHandler typeValuesHandler;
    private final TajsTypeChecker typeChecker;
    private TajsTypeTester tajsTypeTester;
    private BenchmarkInfo info;
    private TypeValuesHandler valueHandler;

    TajsTestVisitor(Solver.SolverInterface c, TypeValuesHandler typeValuesHandler, TajsTypeChecker typeChecker, TajsTypeTester tajsTypeTester, BenchmarkInfo info, TypeValuesHandler valueHandler) {
        this.pv = c.getAnalysis().getPropVarOperations();
        this.c = c;
        this.typeValuesHandler = typeValuesHandler;
        this.typeChecker = typeChecker;
        this.tajsTypeTester = tajsTypeTester;
        this.info = info;
        this.valueHandler = valueHandler;
    }

    public Value attemptGetValue(Type t, TypeContext context) {
        return typeValuesHandler.findFeedbackValue(new TypeWithContext(t, context));
    }

    public Value attemptGetValue(TypeWithContext t) {
        return typeValuesHandler.findFeedbackValue(t);
    }

    @Override
    public Boolean visit(PropertyReadTest test) {
        Value baseValue = attemptGetValue(new TypeWithContext(test.getBaseType(),test.getTypeContext()));
        boolean result = true;
        for (ObjectLabel label : baseValue.getObjectLabels()) {
            Value propertyValue = UnknownValueResolver.getRealValue(pv.readPropertyValue(Collections.singletonList(label), Value.makeStr(test.getProperty()), info.options.staticOptions.killGetters), c.getState());
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
                ObjectLabel moduleObject = ObjectLabel.make(ECMAScriptObjects.OBJECT_MODULE, ObjectLabel.Kind.OBJECT);
                v = UnknownValueResolver.getProperty(moduleObject, PKey.StringPKey.make("exports"), c.getState(), false);
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
            tajsTypeTester.addViolation(new TypeViolation("Module could not be found", test.getPath()), c);
        }

        return tajsTypeTester.attemptAddValue(v, new TypeWithContext(test.getModuleType(), test.getTypeContext()), test.getPath(), c, typeChecker, test);
    }

    @Override
    public Boolean visit(MethodCallTest test) {
        final Value receiver = attemptGetValue(new TypeWithContext(test.getObject(), test.getTypeContext()));
        Value function = UnknownValueResolver.getRealValue(pv.readPropertyValue(receiver.getAllObjectLabels(), Value.makeStr(test.getPropertyName())), c.getState());
        return functionTest(test, receiver, function, false);
    }

    private Boolean functionTest(FunctionTest test, Value receiver, Value function, final boolean isConstructorCall) {
        List<Value> arguments = test.getParameters().stream().map(paramType -> typeValuesHandler.createValue(paramType, test.getTypeContext())).collect(Collectors.toList());

        if (arguments.stream().anyMatch(Value::isNone)) {
            // A test for a function that is triggered but it is not able to get to the function call is definitely a problem
            // in theory this should not happen because the test should be skipped in this case, but better safe than sorry
            throw new RuntimeException("Function " + function + " triggered without arguments @" + test.getPath());
        }

        boolean restArgs = test.isRestArgs();

        final Value restArgType = restArgs ? typeValuesHandler.createValue(TypesUtil.extractRestArgsType(test.getParameters()), test.getTypeContext()) : null;
        if (restArgs) {
            arguments.remove(arguments.size() - 1);
        }

        boolean typeChecked = true;

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
            public Value getThis() {
                return tajsTypeTester.getRetractionPolicy().isTimeout(test) ? Value.makeNone() : receiver;
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
            public ICallEdge.Info toEdgeInfo() {
                return isConstructorCall ? ICallEdge.Info.makeImplicitConstructorCall() : ICallEdge.Info.makeImplicitCall();
            }
        };

        for (ObjectLabel l : function.getAllObjectLabels()) {
            Value returnedValue;

            if (l.getHostObject() != null && l.getHostObject().getAPI() == HostAPIs.SPEC) {
                returnedValue = tajsTypeTester.evaluateCallToSymbolicFunction(l.getHostObject(), callinfo, c);
                // this is only needed, because we have an invariant that higher-order methods are called when control is returned to TAJS. Here that is not the case, so if we didn't do this, we wouldn't save all the feedback-values we need (because they are cleared at the end of the inner for-loop).
                tajsTypeTester.addEndOfInnerLoopCallback(() -> tajsTypeTester.evaluateCallToSymbolicFunction(l.getHostObject(), callinfo, c));
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
                TypeViolation violation = new TypeViolation("Function " + function + " always returns exceptionally", test.getPath());
                if (c.isScanning()) {
                    if (!tajsTypeTester.getRetractionPolicy().isTimeout(test)) {
                        tajsTypeTester.addViolation(violation, c); // only a violation if we are sure.
                    }
                } else {
                    tajsTypeTester.addWarning(violation, c);
                }
            }
            typeChecked &= tajsTypeTester.attemptAddValue(returnedValue, new TypeWithContext(test.getReturnType(), test.getTypeContext()), test.getPath(), c, typeChecker, test);
        }

        return typeChecked;
    }

    @Override
    public Boolean visit(ConstructorCallTest test) {
        Value function = attemptGetValue(test.getFunction(), test.getTypeContext());
        return functionTest(test, null, function, true); // receiver is ignored, since it is a constructor-call.
    }

    @Override
    public Boolean visit(FunctionCallTest test) {
        Value receiver = Value.makeObject(InitialStateBuilder.GLOBAL).joinUndef();
        Value function = attemptGetValue(test.getFunction(), test.getTypeContext());
        return functionTest(test, receiver, function, false);
    }

    @Override
    public Boolean visit(UnionTypeTest test) {
        Value value = attemptGetValue(test.getGetUnionType(), test.getTypeContext());

        Set<Type> nonMatchedTypes = new HashSet<>(test.getGetUnionType().getElements());

        boolean typeChecked = true;

        for (Value splitValue : TajsTypeChecker.split(value)) {
            List<Type> matchingTypes = test.getGetUnionType().getElements().stream().filter(subType -> {
                boolean matched = typeChecker.typeCheck(splitValue, subType, test.getTypeContext(), info, test.getPath()).isEmpty();
                if (matched) {
                    nonMatchedTypes.remove(subType);
                }
                return matched;
            }).collect(Collectors.toList());

            if (matchingTypes.isEmpty()) {
                tajsTypeTester.addViolation(new TypeViolation("Values matched none of the unions", test.getPath()), c);
                typeChecked = false;
            }

            matchingTypes.forEach(subType -> tajsTypeTester.attemptAddValue(splitValue, new TypeWithContext(subType, test.getTypeContext()), test.getPath(), c, typeChecker, test));
        }

        for (Type nonMatchedType : nonMatchedTypes) {
            tajsTypeTester.addWarning(new TypeViolation("No value matches the type: " + nonMatchedType + " in union " + test.getGetUnionType(), test.getPath()), c);
        }

        return typeChecked;
    }

    @Override
    public Boolean visit(NumberIndexTest test) {
        Value baseValue = attemptGetValue(new TypeWithContext(test.getObj(),test.getTypeContext()));
        Value propertyValue = UnknownValueResolver.getRealValue(pv.readPropertyValue(baseValue.getAllObjectLabels(), Value.makeAnyStrUInt()), c.getState());
        tajsTypeTester.addCertificate(new TestCertificate(test, "numberIndexer accessed on [0] has value [1]", new Value[]{baseValue, propertyValue}, c.getState()), c);
        TypeWithContext resultType = new TypeWithContext(test.getReturnType(), test.getTypeContext());
        return tajsTypeTester.attemptAddValue(propertyValue, resultType, test.getPath(), c, typeChecker, test);
    }

    @Override
    public Boolean visit(StringIndexTest test) {
        Value baseValue = attemptGetValue(new TypeWithContext(test.getObj(),test.getTypeContext()));
        Value propertyValue = UnknownValueResolver.getRealValue(pv.readPropertyValue(baseValue.getAllObjectLabels(), Value.makeAnyStr()), c.getState());
        tajsTypeTester.addCertificate(new TestCertificate(test, "stringIndexer accessed on [0] has value [1]", new Value[]{baseValue, propertyValue}, c.getState()), c);
        TypeWithContext resultType = new TypeWithContext(test.getReturnType(), test.getTypeContext());
        return tajsTypeTester.attemptAddValue(propertyValue, resultType, test.getPath(), c, typeChecker, test);
    }

    @Override
    public Boolean visit(PropertyWriteTest test) {
        throw new RuntimeException();
    }
}
