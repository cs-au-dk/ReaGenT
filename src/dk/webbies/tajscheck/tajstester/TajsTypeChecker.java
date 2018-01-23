package dk.webbies.tajscheck.tajstester;

import dk.au.cs.casa.typescript.types.SimpleType;
import dk.au.cs.casa.typescript.types.Type;
import dk.brics.tajs.analysis.HostAPIs;
import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.analysis.PropVarOperations;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.analysis.js.Operators;
import dk.brics.tajs.analysis.nativeobjects.ECMAScriptObjects;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.util.Collections;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.buildprogram.TypeChecker;
import dk.webbies.tajscheck.buildprogram.typechecks.TypeCheck;
import dk.webbies.tajscheck.paser.AST.*;
import dk.webbies.tajscheck.paser.AstBuilder;
import dk.webbies.tajscheck.paser.ExpressionVisitor;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.testcreator.test.check.*;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;
import dk.webbies.tajscheck.util.Tuple3;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TajsTypeChecker {
    private final Solver.SolverInterface c;

    private final PropVarOperations pv;

    private final BenchmarkInfo info;

    private final CheckChecker cc = new CheckChecker();

    private final Map<Tuple3<Check, TypeWithContext, Value>, List<TypeViolation>> cache = new HashMap<>();

    private final ViolationsOracle violationsOracle;

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final Test test; // Not used for anything, but this way we enforce that a TajsTypeChecker is constructed for every test, and thereby the cache's aren't mixed.

    public TajsTypeChecker(Test test, Solver.SolverInterface c, BenchmarkInfo info, ViolationsOracle violationsOracle) {
        this.c = c;
        this.pv = c.getAnalysis().getPropVarOperations();
        this.violationsOracle = violationsOracle;
        this.info = info;
        this.test = test;
    }

    public static List<Value> split(Value v) {

        List<Value> vr = v.restrictToObject().getAllObjectLabels().stream().map(Value::makeObject).collect(Collectors.toList());
        if(v.isMaybeUndef()) vr.add(Value.makeUndef());

        Value vcur;

        vcur = v.restrictToUndef();
        if(!vcur.isNone()) vr.add(vcur);

        vcur = v.restrictToBool();
        if(!vcur.isNone()) vr.add(vcur);

        vcur = v.restrictToStr();
        if(!vcur.isNone()) vr.add(vcur);

        vcur = v.restrictToNum();
        if(!vcur.isNone()) vr.add(vcur);

        if(v.isMaybeNull()) vr.add(Value.makeNull());

        if (vr.isEmpty()) {
            return Collections.singletonList(Value.makeUndef());
        }

        return vr;
    }


    List<TypeViolation> typeCheck(Value v, Type type, TypeContext context, BenchmarkInfo info, String path) {
        List<TypeCheck> typeChecks = TypeChecker.getTypeChecks(type, context, info, 1);

        List<Value> split = split(v);

        List<List<TypeViolation>> violationsForEachValue = split.stream().map(splittenValue -> getTypeViolations(new TypeWithContext(type, context), splittenValue, typeChecks, path)).collect(Collectors.toList());

        boolean definiteViolation = violationsForEachValue.stream().allMatch(Util.not(Collection::isEmpty));

        List<TypeViolation> violations = violationsForEachValue.stream().flatMap(Collection::stream).collect(Collectors.toList());

        if (definiteViolation) {
            return violations;
        } else {
            return violations.stream().map(TypeViolation::asMaybeViolation).collect(Collectors.toList());
        }
    }

    private TypeViolation definiteViolation(String violationPath, Value value, TypeCheck check) {
        return TypeViolation.definite("Expected " + check.getExpected() + " but found " + Util.prettyValue(value, c.getState()), violationPath);
    }

    private List<TypeViolation> getTypeViolations(TypeWithContext typeWithContext, Value v, List<TypeCheck> typeChecks, String path) {

        Function<TypeCheck, List<TypeViolation>> findTypeViolations = typeCheck -> {
            Check check = typeCheck.getCheck();
            Supplier<List<TypeViolation>> getForCache = () -> {
                if (check instanceof CanHaveSubTypeCheck) {
                    if (check instanceof FieldCheck) {
                        FieldCheck fieldCheck = (FieldCheck) check;
                        String field = fieldCheck.getField();

                        String newPath = path + "." + field;
                        return performSubTypeCheck(v, fieldCheck, newPath, Value.makeStr(field));
                    } else if (check instanceof NumberIndexCheck) {
                        NumberIndexCheck numberIndexCheck = (NumberIndexCheck) check;
                        String newPath = path + "." + "[numberIndexer]";

                        return performSubTypeCheck(v, numberIndexCheck, newPath, Value.makeAnyStrUInt());
                    } else if (check instanceof StringIndexCheck) {
                        if (v.isMaybeUndef() && v.restrictToNotUndef().isNone()) {
                            return java.util.Collections.emptyList();
                        }
                        if (v.isMaybePrimitive()) {
                            TypeViolation violation = definiteViolation(path, v, typeCheck);
                            if (this.violationsOracle.canEmit(violation)) {
                                return Collections
                                        .singletonList(violation);
                            }
                        }
                        return performSubTypeCheck(v, (StringIndexCheck)check, path + ".[stringIndexer]", Value.makeAnyStrUInt());
                    } else {
                        throw new RuntimeException(check.getClass().getSimpleName());
                    }
                } else {
                    Bool resultBool = check.accept(cc, v);
                    if (resultBool.isMaybeFalse()) {
                        TypeViolation violation = definiteViolation(path, v, typeCheck);
                        if (resultBool.isMaybeAnyBool()) {
                            violation = violation.asMaybeViolation();
                        }
                        if(this.violationsOracle.canEmit(violation)) {
                            return Collections.singletonList(violation);
                        }
                        return java.util.Collections.emptyList();
                    } else {
                        return java.util.Collections.emptyList();
                    }
                }
            };

            Tuple3<Check, TypeWithContext, Value> key = new Tuple3<>(check, typeWithContext, v);
            if (cache.containsKey(key) && !(typeWithContext.getType() instanceof SimpleType || typeWithContext.getType() instanceof BooleanLiteral || typeWithContext.getType() instanceof NumberLiteral || typeWithContext.getType() instanceof StringLiteral)) {
                return cache.get(key);
            } else {
                cache.put(key, java.util.Collections.emptyList()); // coinductive assumption, if we hit the same check, it must be true.
                List<TypeViolation> result = getForCache.get();
                if (result.stream().noneMatch(violation -> violation.path.startsWith(">"))) { // our magic marker that it was just a check if there was a violation, and that the reported violation is meaningless.
                    cache.put(key, result);
                }
                return result;
            }
        };

        List<TypeViolation> baseErrors = typeChecks.stream()
                .filter(typeCheck -> !CanHaveSubTypeCheck.class.isInstance(typeCheck.getCheck()))
                .map(findTypeViolations)
                .reduce(new ArrayList<>(), Util::reduceList);
        if (!baseErrors.isEmpty()) {
            return baseErrors;
        }

        return typeChecks.stream()
                .filter(typeCheck -> CanHaveSubTypeCheck.class.isInstance(typeCheck.getCheck()))
                .map(findTypeViolations)
                .reduce(new ArrayList<>(), Util::reduceList);
    }

    private List<TypeViolation> performSubTypeCheck(Value v, CanHaveSubTypeCheck hasSubType, String newPath, Value field) {
        Value propertyValue = UnknownValueResolver.getRealValue(pv.readPropertyValue(v.getAllObjectLabels(), field, info.options.staticOptions.killGetters), c.getState());
        if(propertyValue.isMaybeAbsent()) {
            propertyValue = Value.join(propertyValue, Value.makeUndef());
        }

        List<Value> split = split(propertyValue);

        List<TypeCheck> subChecks = TypeChecker.getTypeChecks(hasSubType.getSubType().getType(), hasSubType.getSubType().getTypeContext(), info, 1);

        List<List<TypeViolation>> violationsForEachValue = split.stream().map(splittenValue -> getTypeViolations(hasSubType.getSubType(), splittenValue, subChecks, newPath)).collect(Collectors.toList());

        boolean definiteViolation = violationsForEachValue.stream().allMatch(Util.not(Collection::isEmpty));

        List<TypeViolation> violations = violationsForEachValue.stream().flatMap(Collection::stream).collect(Collectors.toList());

        if (!definiteViolation) {
            violations = violations.stream().map(TypeViolation::asMaybeViolation).collect(Collectors.toList());
        }

        return violations;
    }

    private static Bool or(Bool a, Bool b) {
        if (a.isMaybeTrueButNotFalse() || b.isMaybeTrueButNotFalse()) {
            return Value.makeBool(true);
        }
        if (a.isMaybeFalseButNotTrue() && b.isMaybeFalseButNotTrue()) {
            return Value.makeBool(false);
        }
        return Value.makeAnyBool();
    }

    private static Bool not(Bool x) {
        if (x.isMaybeTrueButNotFalse()) {
            return Value.makeBool(false);
        }
        if (x.isMaybeFalseButNotTrue()) {
            return Value.makeBool(true);
        }
        return Value.makeAnyBool();
    }

    private static Bool and(Bool a, Bool b) {
        if (a.isMaybeFalseButNotTrue() || b.isMaybeFalseButNotTrue()) {
            return Value.makeBool(false);
        }
        if (a.isMaybeTrueButNotFalse() && b.isMaybeTrueButNotFalse()) {
            return Value.makeBool(true);
        }
        return Value.makeAnyBool();
    }

    private class CheckChecker implements CheckVisitorWithArgument<Bool, Value> {


        @Override
        public Bool visit(OrCheck check, Value o) {
            return check.getChecks().stream().map(c -> c.accept(this, o)).reduce(Value.makeBool(false), TajsTypeChecker::or);
        }

        @Override
        public Bool visit(TypeOfCheck check, Value o) {
            switch(check.getTypeString()) {
                case "object":
                    if (o.isMaybeNull() && o.restrictToNotNull().isNone()) return Value.makeBool(true);
                    if (o.getAllObjectLabels().isEmpty()) return Value.makeBool(false);
                    ObjectLabel.Kind kind = o.getAllObjectLabels().iterator().next().getKind();
                    switch (kind) {
                        case OBJECT:
                        case ARRAY:
                        case ERROR:
                        case DATE:
                        case MATH:
                        case ARGUMENTS:
                            return Value.makeBool(true);
                        case FUNCTION:
                        case SYMBOL:
                        case BOOLEAN:
                        case STRING:
                        case NUMBER:
                            return Value.makeBool(false);
                        default:
                            throw new RuntimeException("Didn't consider: " + kind);
                    }
                case "string":{
                    return Value.makeBool(!o.restrictToStr().isNone());
                }
                case "undefined": {
                    return Value.makeBool(!o.restrictToUndef().isNone());
                }
                case "function": {
                    if(o.getAllObjectLabels().isEmpty()) return Value.makeBool(false);
                    return Value.makeBool(o.getAllObjectLabels().iterator().next().getKind() == ObjectLabel.Kind.FUNCTION);
                }
                case "number":
                    return Value.makeBool(!o.restrictToNum().isNone());
                case "boolean":
                    return Value.makeBool(!o.restrictToBool().isNone());
                case "symbol":
                    if (o.getAllObjectLabels().isEmpty()) return Value.makeBool(false);
                    return Value.makeBool(o.getAllObjectLabels().iterator().next().getKind() == ObjectLabel.Kind.SYMBOL);
                default:
                    throw new RuntimeException("Unexpected " + check.getTypeString());
            }
        }

        @Override
        public Bool visit(NotCheck check, Value o) {
            return not(check.getCheck().accept(this, o));
        }

        @Override
        public Bool visit(AndCheck check, Value o) {
            return check.getChecks().stream().map(c -> c.accept(this, o)).reduce(Value.makeBool(true), TajsTypeChecker::and);
        }

        @Override
        public Bool visit(EqualityCheck check, Value o) {
            Bool result = check.getExpression().accept(new CheckEqualityVisitor(o));
            assert result != null;
            return result;
        }

        @Override
        public Bool visit(InstanceOfCheck check, Value o) {
            if (!o.restrictToNotObject().isNone()) {
                return Value.makeBool(false);
            }
            assert o.getObjectLabels().size() == 1;
            if (check.getExp() instanceof Identifier) {
                String name = ((Identifier) check.getExp()).getName();
                if (name.contains(".")) {
                    throw new RuntimeException();
                }
                switch (name) {
                    case "Array":
                        return Value.makeBool(o.getObjectLabels().iterator().next().getKind() == ObjectLabel.Kind.ARRAY);
                    case "RegExp":
                        return Value.makeBool(o.getObjectLabels().iterator().next().getKind() == ObjectLabel.Kind.REGEXP);
                    case "Function":
                        return Value.makeBool(o.getObjectLabels().iterator().next().getKind() == ObjectLabel.Kind.FUNCTION);
                    default:
                        Value clazz = UnknownValueResolver.getProperty(InitialStateBuilder.GLOBAL, PKey.make(Value.makeStr(name)), c.getState(), false);
                        if (!clazz.isNotAbsent()) {
                            System.err.println("Cannot check prototype of: " + name); // TODO: At Least MouseEvent and similar.
                            return Value.makeBool(true);
                        }
                        if (clazz.getObjectLabels().size() != 1) {
                            System.out.println();
                        }
                        assert clazz.getObjectLabels().size() == 1;
                        return Operators.instof(o, clazz, c);
                }
            }
            throw new RuntimeException("Instanceof check" + check + " against " + o);
        }

        @Override
        public Bool visit(FieldCheck check, Value o) {
            String field = check.getField();

            List<TypeViolation> subViolations = performSubTypeCheck(o, check, ">fakeFieldPath", Value.makeStr(field));
            if (subViolations.isEmpty()) {
                return Value.makeBool(true);
            }
            if (subViolations.stream().anyMatch(violation -> !violation.definite)) {
                return Value.makeAnyBool();
            }
            return Value.makeBool(false);
        }

        @Override
        public Bool visit(NumberIndexCheck check, Value o) {
            List<TypeViolation> subViolations = performSubTypeCheck(o, check, ">fakeNumberIndexPath", Value.makeAnyStrUInt());
            if (subViolations.isEmpty()) {
                return Value.makeBool(true);
            }
            if (subViolations.stream().anyMatch(violation -> !violation.definite)) {
                return Value.makeAnyBool();
            }
            return Value.makeBool(false);
        }

        @Override
        public Bool visit(StringIndexCheck check, Value o) {
            if (o.isMaybeUndef()) {
                return Value.makeBool(true);
            }
            if (!o.isNotBool()) {
                return Value.makeBool(false);
            }
            List<TypeViolation> subViolations = performSubTypeCheck(o, check, ">fakeStringIndexPath", Value.makeAnyStr());
            if (subViolations.isEmpty()) {
                return Value.makeBool(true);
            }
            if (subViolations.stream().anyMatch(violation -> !violation.definite)) {
                return Value.makeAnyBool();
            }
            return Value.makeBool(false);
        }

        @Override
        public Bool visit(ExpressionCheck check, Value o) { // TODO: This is very rarely used, and we should be able to switch-case us out of every case where it is used.
            Expression exp = check.getGenerator().apply(AstBuilder.number(1337));
            if (exp instanceof BooleanLiteral) {
                return Value.makeBool(((BooleanLiteral) exp).getBooleanValue());
            }
            System.err.println("Skipping check" + check + " against " + o);
            return Value.makeBool(true);
        }


    }

    private class CheckEqualityVisitor implements ExpressionVisitor<Bool> {
        private Value o;

        public CheckEqualityVisitor(Value o) {
            this.o = o;
        }

        @Override
        public Bool visit(BinaryExpression binOp) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(BooleanLiteral bool) {
            if (o.isNotBool()) {
                return Value.makeBool(false);
            }
            if (o.isMaybeAnyBool()) {
                return Value.makeAnyBool();
            }
            return Value.makeBool(bool.getBooleanValue() && o.isMaybeTrueButNotFalse() || !bool.getBooleanValue() && o.isMaybeFalseButNotTrue());
        }

        @Override
        public Bool visit(CallExpression callExpression) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(CommaExpression commaExpression) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(ConditionalExpression conditionalExpression) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(FunctionExpression functionExpression) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(Identifier identifier) {
            switch (identifier.getName()) {
                case "Array":
                case "String": {
                    if (!o.isMaybeObject()) {
                        return Value.makeBool(false);
                    }
                    ObjectLabel label = o.getObjectLabels().iterator().next();
                    if (label.getHostObject().getAPI() != HostAPIs.ECMASCRIPT_NATIVE) {
                        return Value.makeBool(false);
                    }
                    ECMAScriptObjects nativeObj = (ECMAScriptObjects) label.getHostObject();
                    return Value.makeBool(nativeObj.toString().equals(identifier.getName()));
                }
            }
            throw new RuntimeException("Identifier: " + identifier.getName());
        }

        @Override
        public Bool visit(MemberExpression memberExpression) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(DynamicAccessExpression memberLookupExpression) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(MethodCallExpression methodCallExpression) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(NewExpression newExpression) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(NullLiteral nullLiteral) {
            return Value.makeBool(o.isMaybeNull());
        }

        @Override
        public Bool visit(NumberLiteral numberLiteral) {
            if (o.isNotNum()) {
                return Value.makeBool(false);
            }
            if (!o.isMaybeNum(numberLiteral.getNumber())) {
                return Value.makeBool(false);
            }
            if (!o.isMaybeFuzzyNum()) {
                return Value.makeBool(true);
            }
            return Value.makeAnyBool();
        }

        @Override
        public Bool visit(ObjectLiteral objectLiteral) {
            if (objectLiteral.getProperties().isEmpty()) {
                return Check.typeOf("object").accept(new CheckChecker(), this.o);
            }
            throw new RuntimeException();
        }

        @Override
        public Bool visit(StringLiteral stringLiteral) {
            if (o.isNotStr()) {
                return Value.makeBool(false);
            }
            if (!o.isMaybeStr(stringLiteral.getString())) {
                return Value.makeBool(false);
            }
            if (!o.isMaybeFuzzyStr()) {
                return Value.makeBool(true);
            }
            return Value.makeAnyBool();
        }

        @Override
        public Bool visit(ThisExpression thisExpression) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(UnaryExpression unaryExpression) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(UndefinedLiteral undefinedLiteral) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(GetterExpression getter) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(SetterExpression setter) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(ArrayLiteral arrayLiteral) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(RegExpExpression regExp) {
            throw new RuntimeException();
        }
    }
}
