package dk.webbies.tajscheck.tajstester;

import dk.au.cs.casa.typescript.types.*;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.solver.GenericSolver;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
import dk.webbies.tajscheck.tajstester.typeCreator.SpecObjects;
import dk.webbies.tajscheck.typeutil.PrettyTypes;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;
import dk.webbies.tajscheck.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TypedSymbolicFunctionEvaluator {
    private TajsTypeTester tajsTypeTester;
    private BenchmarkInfo info;
    private TypeValuesHandler valueHandler;

    public TypedSymbolicFunctionEvaluator(TajsTypeTester tajsTypeTester, BenchmarkInfo info, TypeValuesHandler valueHandler) {
        this.tajsTypeTester = tajsTypeTester;
        this.info = info;
        this.valueHandler = valueHandler;
    }

    public Value evaluateCallToSymbolicFunction(HostObject hostObject, FunctionCalls.CallInfo call, Solver.SolverInterface c) {
        TypeWithContext typeWithContext = ((SpecObjects.TypedObject) hostObject).getType();
        String path = ((SpecObjects.TypedObject) hostObject).asText();
        TypeContext context = typeWithContext.getTypeContext();

        Type type = typeWithContext.getType();
        if (type instanceof ReferenceType) {
            context = info.typesUtil.generateParameterMap((ReferenceType) type, context);
            type = ((ReferenceType) type).getTarget();
        }
        if (type instanceof GenericType) {
            type = ((GenericType) type).toInterface();
        }

        if (info.freeGenericsFinder.hasThisTypes(type)) {
            context = context.withThisType(type);
        }

        if ("Function".equals(info.typeNames.get(type))) {
            return valueHandler.createValue(new SimpleType(SimpleTypeKind.Any), TypeContext.create(info));
        }

        if (type instanceof SimpleType && ((SimpleType) type).getKind() == SimpleTypeKind.Any) {
//            Exceptions.throwException(c.getState().clone(), valueHandler.getTheAny(), c, c.getNode());
            return valueHandler.getTheAny();
        }

        if (!(type instanceof InterfaceType) && !(type instanceof ClassType)) {
            throw new RuntimeException(type.getClass().getSimpleName());
        }
        List<Signature> signatures;
        if (type instanceof InterfaceType) {
            Pair<InterfaceType, Map<TypeParameterType, Type>> pair = info.typesUtil.constructSyntheticInterfaceWithBaseTypes((InterfaceType) type);
            InterfaceType inter = pair.getLeft();
            context = context.append(pair.getRight());

            signatures = call.isConstructorCall() ? inter.getDeclaredConstructSignatures() : inter.getDeclaredCallSignatures();
        } else {
            ClassType clazz = (ClassType) type;

            Pair<InterfaceType, Map<TypeParameterType, Type>> pair = info.typesUtil.classToInterface(clazz);
            context = context.append(pair.getRight());
            signatures = pair.getLeft().getDeclaredConstructSignatures();
        }

        if (signatures.size() == 0) {
            tajsTypeTester.addViolation(new TypeViolation("Called a non function", path), c);
            return Value.makeNone();
        }

        return evaluateCallToSymbolicFunction(call, c, typeWithContext, path, context, signatures);
    }

    private Value evaluateCallToSymbolicFunction(FunctionCalls.CallInfo call, Solver.SolverInterface c, TypeWithContext typeWithContext, String path, TypeContext context, List<Signature> signatures) {
        TajsTypeChecker tajsTypeChecker = new TajsTypeChecker(null, c, info);

        if (signatures.size() == 1) {
            Signature signature = signatures.get(0);

            Type restArgsType = null;
            List<Signature.Parameter> parameters = signature.getParameters();
            if (signature.isHasRestParameter()) {
                restArgsType = TypesUtil.extractRestArgsType(parameters.stream().map(Signature.Parameter::getType).collect(Collectors.toList()));
                parameters = parameters.subList(0, parameters.size() - 1);
                if (call.isUnknownNumberOfArgs()) {
                    // it is ok for the number of arguments to be unknown if rest args, when we try to add the arguments we will find potential errors anyway.
//                    tajsTypeTester.addViolation(new TypeViolation("Expected a minimum of " + parameters.size() + " arguments, got an unknown number of arguments", path), c);
                } else if (!call.isUnknownNumberOfArgs() && call.getNumberOfArgs() < parameters.size()) {
                    tajsTypeTester.addViolation(new TypeViolation("Expected a minimum of " + parameters.size() + " arguments, got " + call.getNumberOfArgs(), path), c);
                }
            } else {
                if (call.isUnknownNumberOfArgs()) {
                    tajsTypeTester.addViolation(new TypeViolation("Expected  " + parameters.size() + " arguments, got an unknown number of arguments", path), c);
                } else if (parameters.size() != call.getNumberOfArgs()) {
                    tajsTypeTester.addViolation(new TypeViolation("Expected  " + parameters.size() + " arguments, got " + call.getNumberOfArgs(), path), c);
                }
            }
            if (call.isUnknownNumberOfArgs()) {
                if (!signature.isHasRestParameter()) {
                    tajsTypeTester.addViolation(new TypeViolation("Function was called with an unknown number of args, but it doesn't have a restArgs parameter", path), c);
                }
                if (signature.isHasRestParameter()) {
                    // restricting to not undef, because rest-args must be possibly undef.
                    tajsTypeTester.attemptAddValue(call.getUnknownArg().restrictToNotUndef(), new TypeWithContext(restArgsType, context), info.typeNames.get(typeWithContext.getType()) + ".[argUnknown]", c, tajsTypeChecker, null);
                }
            }

            for (int i = 0; i < parameters.size(); i++) {
                Value arg = call.getArg(i);
                if (arg.isMaybePresent()) {
                    tajsTypeTester.attemptAddValue(arg, new TypeWithContext(parameters.get(i).getType(), context), info.typeNames.get(typeWithContext.getType()) + ".[arg" + i + "]", c, tajsTypeChecker, null);
                }
            }
            if (signature.isHasRestParameter()) {
                if (call.isUnknownNumberOfArgs()) {
                    tajsTypeTester.attemptAddValue(call.getArg(parameters.size()), new TypeWithContext(restArgsType, context), info.typeNames.get(typeWithContext.getType()) + ".[arg" + parameters.size() + "]", c, tajsTypeChecker, null);
                } else {
                    for (int i = parameters.size(); i < call.getNumberOfArgs(); i++) {
                        tajsTypeTester.attemptAddValue(call.getArg(i), new TypeWithContext(restArgsType, context), info.typeNames.get(typeWithContext.getType()) + ".[arg" + i + "]", c, tajsTypeChecker, null);
                    }
                }
            }
            assert signature.getResolvedReturnType() != null;
            return valueHandler.createValue(signature.getResolvedReturnType(), context);
        } else {
            List<Signature> matchingSignatures = signatures.stream().filter(sig -> sigMatches(sig, context, call, c, path, tajsTypeChecker)).collect(Collectors.toList());

            if (matchingSignatures.isEmpty()) {
                tajsTypeTester.addViolation(new TypeViolation("None of the overloads matched how the callback was called", path), c);
            }

            if (matchingSignatures.size() < signatures.size()) {
                ArrayList<Signature> nonMatchingSignatures = new ArrayList<>(signatures);
                nonMatchingSignatures.removeAll(matchingSignatures);
                for (Signature nonMatchingSignature : nonMatchingSignatures) {
                    tajsTypeTester.addWarning(new TypeViolation("Signatures with args " + PrettyTypes.parameters(nonMatchingSignature.getParameters()) + " was never called", path), c);
                }
            }

            for (Signature signature : matchingSignatures) {
                List<Signature.Parameter> parameters = signature.getParameters();
                if (signature.isHasRestParameter()) {
                    Type restArgsType = TypesUtil.extractRestArgsType(parameters.stream().map(Signature.Parameter::getType).collect(Collectors.toList()));
                    parameters = parameters.subList(0, parameters.size() - 1);

                    for (int i = parameters.size(); i < call.getNumberOfArgs(); i++) {
                        tajsTypeTester.attemptAddValue(call.getArg(i), new TypeWithContext(restArgsType, context), info.typeNames.get(typeWithContext.getType()) + ".[arg" + i + "]", c, tajsTypeChecker, null);
                    }

                    if (call.isUnknownNumberOfArgs()) {
                        tajsTypeTester.attemptAddValue(call.getUnknownArg().restrictToNotUndef(), new TypeWithContext(restArgsType, context), info.typeNames.get(typeWithContext.getType()) + ".[argUnknown]", c, tajsTypeChecker, null);
                    }
                }
                for (int i = 0; i < Math.min(parameters.size(), call.getNumberOfArgs()); i++) {
                    tajsTypeTester.attemptAddValue(call.getArg(i), new TypeWithContext(parameters.get(i).getType(), context), info.typeNames.get(typeWithContext.getType()) + ".[arg" + i + "]", c, tajsTypeChecker, null);
                }
            }

            assert matchingSignatures.stream().map(Signature::getResolvedReturnType).allMatch(Objects::nonNull);
            return Value.join(matchingSignatures.stream().map(sig -> valueHandler.createValue(sig.getResolvedReturnType(), context)).collect(Collectors.toList()));
        }
    }

    private boolean sigMatches(Signature signature, TypeContext context, FunctionCalls.CallInfo call, GenericSolver<State, Context, CallEdge, IAnalysisMonitoring, Analysis>.SolverInterface c, String path, TajsTypeChecker tajsTypeChecker) {
        List<Signature.Parameter> parameters = signature.getParameters();
        Type restArgsType = null;
        if (call.isUnknownNumberOfArgs()) {
            return false;
        }
        if (signature.isHasRestParameter()) {
            restArgsType = TypesUtil.extractRestArgsType(parameters.stream().map(Signature.Parameter::getType).collect(Collectors.toList()));
            parameters = parameters.subList(0, parameters.size() - 1);
            if (call.getNumberOfArgs() < parameters.size()) {
                return false;
            }
        } else {
            if (parameters.size() != call.getNumberOfArgs()) {
                return false;
            }
        }
        for (int i = 0; i < Math.min(parameters.size(), call.getNumberOfArgs()); i++) {
            Value argValue = call.getArg(i);
            Type argType = parameters.get(i).getType();
            if (argValue.isNone() || !tajsTypeTester.getViolations(argValue, new TypeWithContext(argType, context), path, c, tajsTypeChecker).isEmpty()) {
                return false;
            }
        }
        if (call.isUnknownNumberOfArgs()) {
            if (!signature.isHasRestParameter()) {
                return false;
            }
            if (!tajsTypeTester.getViolations(call.getUnknownArg().restrictToNotUndef(), new TypeWithContext(restArgsType, context), path, c, tajsTypeChecker).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
