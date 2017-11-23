package dk.webbies.tajscheck.tajstester;

import dk.au.cs.casa.typescript.types.Type;
import dk.brics.tajs.analysis.HostAPIs;
import dk.brics.tajs.analysis.PropVarOperations;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.analysis.nativeobjects.ECMAScriptObjects;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.util.Collections;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.buildprogram.TypeChecker;
import dk.webbies.tajscheck.buildprogram.typechecks.FieldTypeCheck;
import dk.webbies.tajscheck.buildprogram.typechecks.SimpleTypeCheck;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.util.Util.prettyValue;

public class TajsTypeChecker {
    private final Solver.SolverInterface c;

    private final PropVarOperations pv;

    private final BenchmarkInfo info;

    private final CheckChecker cc = new CheckChecker();

    private final Map<Tuple3<Check, TypeWithContext, Value>, List<Tuple3<String, Value, TypeCheck>>> cache = new HashMap<>();

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final Test test; // Not used for anything, but this way we enforce that a TajsTypeChecker is constructed for every test, and thereby the cache's aren't mixed.

    public TajsTypeChecker(Test test, Solver.SolverInterface c, BenchmarkInfo info) {
        this.c = c;
        this.pv = c.getAnalysis().getPropVarOperations();
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

        return vr;
    }


    List<TypeViolation> typeCheck(Value v, Type type, TypeContext context, BenchmarkInfo info, String path) {
        List<TypeCheck> typeChecks = TypeChecker.getTypeChecks(type, context, info, 1);

        List<Value> split = split(v);
        if (split.isEmpty()) {
            return Collections.singletonList(new TypeViolation("No value found", path));
        }
        List<Tuple3<String, Value, TypeCheck>> violations = split.stream().flatMap(splittenValue -> getTypeViolations(new TypeWithContext(type, context), splittenValue, typeChecks, path).stream()).collect(Collectors.toList());

        return violations.stream().map(tuple -> {
            String violationPath = tuple.getA();
            Value value = tuple.getB();
            TypeCheck check = tuple.getC();
            return new TypeViolation("Expected " + check.getExpected() + " but found " + Util.prettyValue(value, c.getState()), violationPath);
        }).collect(Collectors.toList());
    }

    private List<Tuple3<String, Value, TypeCheck>> getTypeViolations(TypeWithContext typeWithContext, Value v, List<TypeCheck> typeChecks, String path) {
        return typeChecks.stream()
            .sorted((testA, testB) -> {
                // Making sure all CanHaveSubTypeCheck comes last.
                if (testA instanceof CanHaveSubTypeCheck && !(testB instanceof CanHaveSubTypeCheck)) {
                    return 1;
                } else if (!(testA instanceof CanHaveSubTypeCheck) && testB instanceof CanHaveSubTypeCheck) {
                    return -1;
                } else {
                    return 0;
                }
            })
            .map(typeCheck -> {
                Check check = typeCheck.getCheck();
                Supplier<List<Tuple3<String, Value, TypeCheck>>> getForCache = () -> {
                    if (check instanceof CanHaveSubTypeCheck) {
                        if (check instanceof FieldCheck) {
                            FieldCheck fieldCheck = (FieldCheck) check;
                            String field = fieldCheck.getField();

                            String newPath = path + "." + field;

                            List<TypeCheck> subTypeChecks;
                            if (typeCheck instanceof FieldTypeCheck) {
                                subTypeChecks = ((FieldTypeCheck) typeCheck).getFieldChecks();
                            } else {
                                AndCheck andSubChecks = Check.and(fieldCheck.getChecks());
                                subTypeChecks = Collections.singletonList(new SimpleTypeCheck(andSubChecks, andSubChecks.toString()));
                            }

                            return performSubTypeCheck(v, fieldCheck, newPath, subTypeChecks, Value.makeStr(field));
                        } else if (check instanceof NumberIndexCheck) {
                            NumberIndexCheck numberIndexCheck = (NumberIndexCheck) check;
                            String newPath = path + "." + "[numberIndexer]";

                            String expected;
                            if (typeCheck.getExpected().startsWith("(arrayIndex: ")) {
                                expected = Util.removeSuffix(Util.removePrefix(typeCheck.getExpected(), "(arrayIndex: "), ")");
                            } else {
                                expected = Util.removeSuffix(Util.removePrefix(typeCheck.getExpected(), "(numberIndexer: "), ")");
                            }

                            List<TypeCheck> subTypeChecks = Collections.singletonList(new SimpleTypeCheck(numberIndexCheck.getSubCheck(), expected));

                            return performSubTypeCheck(v, numberIndexCheck, newPath, subTypeChecks, Value.makeAnyStrUInt());
                        } else if (check instanceof StringIndexCheck) {
                            StringIndexCheck stringIndexCheck = (StringIndexCheck) check;
                            if (v.isMaybeUndef() && v.restrictToNotUndef().isNone()) {
                                return java.util.Collections.emptyList();
                            }
                            if (v.isMaybePrimitive()) {
                                return Collections.singletonList(new Tuple3<>(path, v, typeCheck));
                            }
                            System.err.println("Skipping check" + check + " against " + v); // TODO: What do.
                            return java.util.Collections.emptyList();
                        } else {
                            throw new RuntimeException(check.getClass().getSimpleName());
                        }
                    } else {
                        if (!check.accept(cc, v)) {
                            return Collections.singletonList(new Tuple3<>(path, v, typeCheck));
                        } else {
                            return java.util.Collections.emptyList();
                        }
                    }
                };

                Tuple3<Check, TypeWithContext, Value> key = new Tuple3<>(check, typeWithContext, v);
                if (cache.containsKey(key)) {
                    return cache.get(key);
                } else {
                    cache.put(key, java.util.Collections.emptyList()); // coinductive assumption, if we hit the same check, it must be true.
                    List<Tuple3<String, Value, TypeCheck>> result = getForCache.get();
                    cache.put(key, result);
                    return result;
                }
            })
            .filter(Util.not(List::isEmpty))
            .findFirst()
            .orElse(java.util.Collections.emptyList());
    }

    private List<Tuple3<String, Value, TypeCheck>> performSubTypeCheck(Value v, CanHaveSubTypeCheck hasSubType, String newPath, List<TypeCheck> subTypeChecks, Value field) {
        Value propertyValue = UnknownValueResolver.getRealValue(pv.readPropertyValue(v.getAllObjectLabels(), field, info.options.staticOptions.killGetters), c.getState());
        if(propertyValue.isMaybeAbsent()) {
            propertyValue = Value.join(propertyValue, Value.makeUndef());
        }

        List<Value> split = split(propertyValue);
        if (split.isEmpty()) {
            return Collections.singletonList(new Tuple3<>(newPath, Value.makeNone(), TypeChecker.createIntersection(subTypeChecks)));
        }

        List<TypeCheck> subChecks;
        if (hasSubType.getSubType() != null) {
            subChecks = TypeChecker.getTypeChecks(hasSubType.getSubType().getType(), hasSubType.getSubType().getTypeContext(), info, 1);
        } else {
            subChecks = subTypeChecks;
        }

        return split.stream().flatMap(splittenValue -> getTypeViolations(hasSubType.getSubType(), splittenValue, subChecks, newPath).stream()).collect(Collectors.toList());
    }

    private String prettyValues(List<Value> vs, State s) {
        if(vs.isEmpty()) return prettyValue(Value.makeNone(), s);
        return prettyValue(vs.stream().reduce(vs.get(0), (a, v) -> a.join(v)), s);
    }


    private class CheckChecker implements CheckVisitorWithArgument<Boolean, Value> {
        @Override
        public Boolean visit(OrCheck check, Value o) {
            return check.getChecks().stream().map(c -> c.accept(this, o)).reduce(false, (x, y) -> x || y);
        }

        @Override
        public Boolean visit(TypeOfCheck check, Value o) {
            switch(check.getTypeString()) {
                case "object":
                    if(o.getAllObjectLabels().isEmpty()) return false;
                    ObjectLabel.Kind kind = o.getAllObjectLabels().iterator().next().getKind();
                    switch (kind) {
                        case OBJECT:
                        case ARRAY:
                        case ERROR:
                        case DATE:
                            return true;
                        case FUNCTION:
                        case SYMBOL:
                        case BOOLEAN:
                            return false;
                        default:
                            throw new RuntimeException("Didn't consider: " + kind);
                    }
                case "string":
                    return !o.restrictToStr().isNone();
                case "undefined":
                    return !o.restrictToUndef().isNone();
                case "function":
                    if(o.getAllObjectLabels().isEmpty()) return false;
                    return o.getAllObjectLabels().iterator().next().getKind() == ObjectLabel.Kind.FUNCTION;
                case "number":
                    return !o.restrictToNum().isNone();
                case "boolean":
                    return !o.restrictToBool().isNone();
                case "symbol":
                    if (o.getAllObjectLabels().isEmpty()) return false;
                    return o.getAllObjectLabels().iterator().next().getKind() == ObjectLabel.Kind.SYMBOL;
                default:
                    throw new RuntimeException("Unexpected " + check.getTypeString());
            }
        }

        @Override
        public Boolean visit(NotCheck check, Value o) {
            return !check.getCheck().accept(this, o);
        }

        @Override
        public Boolean visit(AndCheck check, Value o) {
            return check.getChecks().stream().map(c -> c.accept(this, o)).reduce(true, (x, y) -> x && y);
        }

        @Override
        public Boolean visit(EqualityCheck check, Value o) {
            Boolean result = check.getExpression().accept(new CheckEqualityVisitor(o));
            assert result != null;
            return result;
        }

        @Override
        public Boolean visit(InstanceOfCheck check, Value o) {
            if (!o.restrictToNotObject().isNone()) {
                return false;
            }
            //TODO: The checks should carry a value! (maybe not, instanceof checks are only used against values defined by the standard library, never for types defined by the library under test).
//            Operators.instof(o, v2, c);
            if (check.getExp() instanceof Identifier) {
                String name = ((Identifier) check.getExp()).getName();
                switch (name) {
                    case "Array":
                        return o.getObjectLabels().stream().allMatch(label -> label.getKind() == ObjectLabel.Kind.ARRAY);
                    case "RegExp":
                        return o.getObjectLabels().stream().allMatch(label -> label.getKind() == ObjectLabel.Kind.REGEXP);
                    case "Element":
                        System.err.println("Skipping instanceof check on  Element against " + o);
                    default:
                        System.err.println("Skipping instanceof check on " + name + " against " + o);
                }
            }
            System.err.println("Skipping instance of check" + check + " against " + o);
            return true;
        }

        @Override
        public Boolean visit(FieldCheck check, Value o) {
            String field = check.getField();

            List<TypeCheck> subTypeChecks = check.getChecks().stream().map(subCheck -> new SimpleTypeCheck(subCheck, "expected!")).collect(Collectors.toList());

            return performSubTypeCheck(o, check, "fakeFieldPath", subTypeChecks, Value.makeStr(field)).isEmpty();
        }

        @Override
        public Boolean visit(NumberIndexCheck check, Value o) {
            List<TypeCheck> subTypeChecks = Collections.singletonList(new SimpleTypeCheck(check.getSubCheck(), "foo"));

            return performSubTypeCheck(o, check, "fakeNumberIndexPath", subTypeChecks, Value.makeAnyStrUInt()).isEmpty();
        }

        @Override
        public Boolean visit(StringIndexCheck check, Value o) {
            if (o.isMaybeUndef() && o.restrictToNotUndef().isNone()) {
                return true;
            }
            if (!o.isNotBool()) {
                return false;
            }
            List<TypeCheck> subTypeChecks = Collections.singletonList(new SimpleTypeCheck(check.getSubCheck(), "foo"));

            return performSubTypeCheck(o, check, "fakeStringIndexPath", subTypeChecks, Value.makeAnyStr()).isEmpty();
        }

        @Override
        public Boolean visit(ExpressionCheck check, Value o) { // TODO: This is very rarely used, and we should be able to switch-case us out of every case where it is used.
            Expression exp = check.getGenerator().apply(AstBuilder.number(1337));
            if (exp instanceof BooleanLiteral) {
                return ((BooleanLiteral) exp).getBooleanValue();
            }
            System.err.println("Skipping check" + check + " against " + o);
            return true;
        }


    }

    private class CheckEqualityVisitor implements ExpressionVisitor<Boolean> {
        private Value o;

        public CheckEqualityVisitor(Value o) {
            this.o = o;
        }

        @Override
        public Boolean visit(BinaryExpression binOp) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(BooleanLiteral bool) {
            if (o.isMaybeAnyBool()) {
                return false;
            }
            if (bool.getBooleanValue()) {
                return o.isMaybeTrue();
            } else {
                return o.isMaybeFalse();
            }
        }

        @Override
        public Boolean visit(CallExpression callExpression) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(CommaExpression commaExpression) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(ConditionalExpression conditionalExpression) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(FunctionExpression functionExpression) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(Identifier identifier) {
            switch (identifier.getName()) {
                case "Array":
                case "String": {
                    if (!o.isMaybeObject()) {
                        return false;
                    }
                    ObjectLabel label = o.getObjectLabels().iterator().next();
                    if (label.getHostObject().getAPI() != HostAPIs.ECMASCRIPT_NATIVE) {
                        return false;
                    }
                    ECMAScriptObjects nativeObj = (ECMAScriptObjects) label.getHostObject();
                    return nativeObj.toString().equals(identifier.getName());
                }
            }
            throw new RuntimeException("Identifier: " + identifier.getName());
        }

        @Override
        public Boolean visit(MemberExpression memberExpression) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(DynamicAccessExpression memberLookupExpression) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(MethodCallExpression methodCallExpression) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(NewExpression newExpression) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(NullLiteral nullLiteral) {
            return o.isMaybeNull();
        }

        @Override
        public Boolean visit(NumberLiteral numberLiteral) {
            return !o.isMaybeAnyNum() && o.isMaybeNum(numberLiteral.getNumber());
        }

        @Override
        public Boolean visit(ObjectLiteral objectLiteral) {
            if (objectLiteral.getProperties().isEmpty()) {
                return Check.typeOf("object").accept(new CheckChecker(), this.o);
            }
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(StringLiteral stringLiteral) {
            return !o.isMaybeAnyStr() && o.isMaybeStr(stringLiteral.getString());
        }

        @Override
        public Boolean visit(ThisExpression thisExpression) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(UnaryExpression unaryExpression) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(UndefinedLiteral undefinedLiteral) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(GetterExpression getter) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(SetterExpression setter) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(ArrayLiteral arrayLiteral) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(RegExpExpression regExp) {
            throw new RuntimeException();
        }
    }
}
