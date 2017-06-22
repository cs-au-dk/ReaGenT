package dk.webbies.tajscheck.tajstester;

import dk.au.cs.casa.typescript.types.Type;
import dk.brics.tajs.analysis.PropVarOperations;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.analysis.nativeobjects.concrete.TAJSSplitConcreteSemantics;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.util.Pair;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.buildprogram.TypeChecker;
import dk.webbies.tajscheck.buildprogram.typechecks.FieldTypeCheck;
import dk.webbies.tajscheck.buildprogram.typechecks.SimpleTypeCheck;
import dk.webbies.tajscheck.buildprogram.typechecks.TypeCheck;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.testcreator.test.check.*;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;
import dk.webbies.tajscheck.util.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private List<Value> split(Value v) {

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

    boolean typeCheck(Value v, Type type, TypeContext context, BenchmarkInfo info, int depth) {
        List<TypeViolation> violations = new LinkedList<>();
        List<TypeCheck> typeChecks = TypeChecker.getTypeChecks(type, context, info, depth);
        TypeWithContext tc = new TypeWithContext(type, context);

        List<Value> split = split(v);
        List<Pair<Value, List<TypeCheck>>> zip = Util.zip(split.stream(),
                split.stream().map(splittenValue -> getTypeViolations(splittenValue, typeChecks)),
                Pair::make).collect(Collectors.toList());

        List<Value> filter = zip.stream().filter(p -> p.getSecond().isEmpty()).map(Pair::getFirst).collect(Collectors.toList());
        List<Value> filterNot = zip.stream().filter(p -> !p.getSecond().isEmpty()).map(Pair::getFirst).collect(Collectors.toList());

        return filterNot.isEmpty() && !filter.isEmpty();
    }


    Pair<Value,List<TypeViolation>> typeCheckAndFilter(Value v, Type type, TypeContext context, BenchmarkInfo info, int depth, Test test) {
        List<TypeViolation> violations = new LinkedList<>();
        List<TypeCheck> typeChecks = TypeChecker.getTypeChecks(type, context, info, depth);
        TypeWithContext tc = new TypeWithContext(type, context);

        List<Value> split = split(v);
        List<Pair<Value, List<TypeCheck>>> zip = Util.zip(split.stream(),
                split.stream().map(splittenValue -> getTypeViolations(splittenValue, typeChecks)),
                Pair::make).collect(Collectors.toList());

        List<Value> filter = zip.stream().filter(p -> p.getSecond().isEmpty()).map(Pair::getFirst).collect(Collectors.toList());
        List<Value> filterNot = zip.stream().filter(p -> !p.getSecond().isEmpty()).map(Pair::getFirst).collect(Collectors.toList());
        Set<TypeCheck> filterNotViolations = zip.stream().filter(p -> !p.getSecond().isEmpty()).flatMap(p -> p.getSecond().stream()).collect(Collectors.toSet());

        if(!filterNot.isEmpty() && !filter.isEmpty()) {
            violations.add(new TypeViolation("Expected " + tc + " but found spurious values: " + prettyValues(filterNot, c.getState()) + " violating " + mkString(filterNotViolations.stream(), ","), test));
        }
        if(filter.isEmpty()) {
            violations.add(new TypeViolation("Expected " + tc + " but found value: " + prettyValue(v, c.getState()) + " violating " + mkString(filterNotViolations.stream(), ","), test));
        }
        return Pair.make(filter.stream().reduce(Value.makeNone(), (a, b) -> a.join(b)), violations);
    }

    private List<TypeCheck> getTypeViolations(Value v, List<TypeCheck> typeChecks) {
        return typeChecks.stream().flatMap(typeCheck -> {

            if(typeCheck instanceof FieldTypeCheck) {
                FieldTypeCheck fieldTypeCheck = (FieldTypeCheck)typeCheck;
                Value propertyValue = pv.readPropertyValue(v.getAllObjectLabels(), fieldTypeCheck.getField());
                if(propertyValue.isMaybeAbsent()) {
                    propertyValue = Value.join(propertyValue, Value.makeUndef());
                }

                return split(propertyValue).stream().flatMap(splittenValue -> getTypeViolations(splittenValue, fieldTypeCheck.getFieldChecks()).stream());
            }
            else if(typeCheck instanceof SimpleTypeCheck) {
                SimpleTypeCheck simpleTypeCheck = (SimpleTypeCheck)typeCheck;
                if(!simpleTypeCheck.getCheck().accept(cc, v)) {
                    return singletonList(typeCheck).stream();
                } else {
                    return new LinkedList<TypeCheck>().stream();
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

            try {
                Value v = TAJSSplitConcreteSemantics.eval(check.getExpression().toString());
                if(!v.isNone()) return v.equals(o);
            } catch(Exception e) {
                System.err.println("Skipping equality check " + check + " against " + o);
            }

            return true;
        }

        @Override
        public Boolean visit(InstanceOfCheck check, Value o) {
            //TODO: The checks should carry a value! (maybe not, instanceof checks are only used against values defined by the standard library, never for types defined by the library under test).
//            Operators.instof(o, v2, c);
            System.err.println("Skipping instance of check" + check + " against " + o);
            return true;
        }

        @Override
        public Boolean visit(FieldCheck check, Value o) {
            Value propertyValue = pv.readPropertyValue(o.getAllObjectLabels(), check.getField());
            if (propertyValue.isMaybeAbsent()) {
                propertyValue = Value.join(propertyValue, Value.makeUndef());
            }

            List<Value> split = split(propertyValue);

            return split.stream().allMatch(value -> check.getChecks().stream().allMatch(subCheck -> subCheck.accept(this, value)));
        }

        @Override
        public Boolean visit(NumberIndexCheck check, Value o) {
            System.err.println("Skipping check" + check + " against " + o);
            return true;
        }

        @Override
        public Boolean visit(StringIndexCheck check, Value o) {
            System.err.println("Skipping check" + check + " against " + o);
            return true;
        }

        @Override
        public Boolean visit(ExpressionCheck check, Value o) { // TODO: This is very rarely used, and we should be able to switch-case us out of every case where it is used.
            System.err.println("Skipping check" + check + " against " + o);
            return true;
        }
    }
}