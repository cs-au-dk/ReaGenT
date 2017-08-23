package dk.webbies.tajscheck.tajstester;

import dk.au.cs.casa.typescript.types.Type;
import dk.brics.tajs.analysis.PropVarOperations;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.UnknownValueResolver;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.util.Collections;
import dk.brics.tajs.util.Pair;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.buildprogram.TypeChecker;
import dk.webbies.tajscheck.buildprogram.typechecks.FieldTypeCheck;
import dk.webbies.tajscheck.buildprogram.typechecks.SimpleTypeCheck;
import dk.webbies.tajscheck.buildprogram.typechecks.TypeCheck;
import dk.webbies.tajscheck.paser.AST.*;
import dk.webbies.tajscheck.paser.AstBuilder;
import dk.webbies.tajscheck.paser.ExpressionVisitor;
import dk.webbies.tajscheck.testcreator.test.check.*;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;
import dk.webbies.tajscheck.util.Tuple3;
import dk.webbies.tajscheck.util.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dk.webbies.tajscheck.util.Util.*;

public class TajsTypeChecker {
    private final Solver.SolverInterface c;

    private final PropVarOperations pv;

    private final BenchmarkInfo info;

    private final CheckChecker cc = new CheckChecker();

    public TajsTypeChecker(Solver.SolverInterface c, BenchmarkInfo info) {
        this.c = c;
        this.pv = c.getAnalysis().getPropVarOperations();
        this.info = info;
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


    List<TypeViolation> typeCheckAndFilter(Value v, Type type, TypeContext context, BenchmarkInfo info, String path) {
        List<TypeCheck> typeChecks = TypeChecker.getTypeChecks(type, context, info, info.options.staticOptions.checkDepth);

        List<Value> split = split(v);
        if (split.isEmpty()) {
            return Collections.singletonList(new TypeViolation("No value found", path));
        }
        List<Tuple3<String, Value, TypeCheck>> violations = split.stream().flatMap(splittenValue -> getTypeViolations(splittenValue, typeChecks, path).stream()).collect(Collectors.toList());

        return violations.stream().map(tuple -> {
            String violationPath = tuple.getA();
            Value value = tuple.getB();
            TypeCheck check = tuple.getC();
            return new TypeViolation("Expected " + check.getExpected() + " but found " + value, violationPath);
        }).collect(Collectors.toList());
    }

    private List<Tuple3<String, Value, TypeCheck>> getTypeViolations(Value v, List<TypeCheck> typeChecks, String path) {
        return typeChecks.stream().flatMap(typeCheck -> {

            if(typeCheck instanceof FieldTypeCheck) {
                FieldTypeCheck fieldTypeCheck = (FieldTypeCheck)typeCheck;
                String field = fieldTypeCheck.getField();
                Value propertyValue = UnknownValueResolver.getRealValue(pv.readPropertyValue(v.getAllObjectLabels(), Value.makeStr(field), info.options.staticOptions.killGetters), c.getState());
                if(propertyValue.isMaybeAbsent()) {
                    propertyValue = Value.join(propertyValue, Value.makeUndef());
                }

                List<Value> split = split(propertyValue);
                if (split.isEmpty()) {
                    return Collections.singletonList(new Tuple3<>(path + "." + field, Value.makeNone(), TypeChecker.createIntersection(fieldTypeCheck.getFieldChecks()))).stream();
                }

                return split.stream().flatMap(splittenValue -> getTypeViolations(splittenValue, fieldTypeCheck.getFieldChecks(), path + "." + field).stream());
            }
            else if(typeCheck instanceof SimpleTypeCheck) {
                SimpleTypeCheck simpleTypeCheck = (SimpleTypeCheck)typeCheck;
                if(!simpleTypeCheck.getCheck().accept(cc, v)) {
                    return Collections.singletonList(new Tuple3<>(path, v, typeCheck)).stream();
                } else {
                    return Stream.empty();
                }
            }
            else throw new RuntimeException("Unexpected");
        }).collect(Collectors.toList());
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
                    return o.getAllObjectLabels().iterator().next().getKind() == ObjectLabel.Kind.OBJECT;
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
            Value propertyValue = UnknownValueResolver.getRealValue(pv.readPropertyValue(o.getAllObjectLabels(), Value.makeStr(check.getField()), info.options.staticOptions.killGetters), c.getState());
            if (propertyValue.isMaybeAbsent()) {
                propertyValue = Value.join(propertyValue, Value.makeUndef());
            }

            List<Value> split = split(propertyValue);
            if (split.isEmpty()) {
                return false;
            }

            return split.stream().allMatch(value -> check.getChecks().stream().allMatch(subCheck -> subCheck.accept(this, value)));
        }

        @Override
        public Boolean visit(NumberIndexCheck check, Value o) {
            Value propertyValue = UnknownValueResolver.getRealValue(pv.readPropertyValue(o.getAllObjectLabels(), Value.makeAnyStrUInt()), c.getState());
            return check.getSubCheck().accept(this, propertyValue);
        }

        @Override
        public Boolean visit(StringIndexCheck check, Value o) {
            if (o.isMaybeUndef() && o.restrictToNotUndef().isNone()) {
                return true;
            }
            if (!o.isNotBool()) {
                return false;
            }
            System.err.println("Skipping check" + check + " against " + o);
            return true;
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

    private static class CheckEqualityVisitor implements ExpressionVisitor<Boolean> {
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
            throw new RuntimeException();
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