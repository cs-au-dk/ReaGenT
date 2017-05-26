package dk.webbies.tajscheck.tajstester;

import dk.au.cs.casa.typescript.types.*;
import dk.brics.tajs.analysis.PropVarOperations;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.util.Pair;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.util.Util.prettyValue;

public class TajsTypeChecker implements TypeVisitor<List<TypeViolation>> {

    private final TypeWithContext tc;
    private final Value v;
    private final State s;
    private final PropVarOperations pv;
    private final Test test;

    private final LinkedList<TypeViolation> violations = new LinkedList<TypeViolation>();
    private final Value filteredValue;

    private TajsTypeChecker(TypeWithContext tc, Value v, State s, Solver.SolverInterface c, Test test) {
        this.tc = tc;
        this.v = v;
        this.s = s;
        this.filteredValue = v;
        this.pv = c.getAnalysis().getPropVarOperations();
        this.test = test;
    }

    private void expectOnly(Value badValues) {
        violations.add(new TypeViolation("Expected " + tc + " but found spurious values: " + prettyValue(badValues, s), test));
    }

    private void expect() {
        violations.add(new TypeViolation("Expected " + tc + " but found value: " + prettyValue(v, s), test));
    }

    public static List<TypeViolation> typeCheck(TypeWithContext tc, Value v, State s, Solver.SolverInterface c, Test test){
        return tc.getType().accept(new TajsTypeChecker(tc, v, s, c, test));
    }

    public static Pair<List<TypeViolation>, Value> typeCheckAndFilter(TypeWithContext tc, Value v, State s, Solver.SolverInterface c, Test test){
        TajsTypeChecker checker = new TajsTypeChecker(tc, v, s, c, test);
        List<TypeViolation> computedViolations = tc.getType().accept(checker);
        return Pair.make(computedViolations, checker.filteredValue);
    }

    @Override
    public List<TypeViolation> visit(AnonymousType t) {
        throw new RuntimeException("Implement me");
    }

    @Override
    public List<TypeViolation> visit(ClassType t) {
        throw new RuntimeException("Implement me");

    }

    @Override
    public List<TypeViolation> visit(GenericType t) {
        throw new RuntimeException("Implement me");

    }

    @Override
    public List<TypeViolation> visit(InterfaceType t) {
        genericChecker(t);
        return violations;
    }

    @Override
    public List<TypeViolation> visit(ReferenceType t) {
        throw new RuntimeException("Implement me");

    }

    @Override
    public List<TypeViolation> visit(SimpleType t) {
        genericChecker(t);
        throw new RuntimeException("Implement me");

    }

    @Override
    public List<TypeViolation> visit(TupleType t) {
        throw new RuntimeException("Implement me");

    }

    @Override
    public List<TypeViolation> visit(UnionType t) {
        genericChecker(t);
        return violations;
    }

    @Override
    public List<TypeViolation> visit(UnresolvedType t) {
        throw new RuntimeException("Implement me");

    }

    @Override
    public List<TypeViolation> visit(TypeParameterType t) {
        throw new RuntimeException("Implement me");

    }

    @Override
    public List<TypeViolation> visit(StringLiteral t) {
        throw new RuntimeException("Implement me");

    }

    @Override
    public List<TypeViolation> visit(BooleanLiteral t) {
        throw new RuntimeException("Implement me");

    }

    @Override
    public List<TypeViolation> visit(NumberLiteral t) {
        throw new RuntimeException("Implement me");

    }

    @Override
    public List<TypeViolation> visit(IntersectionType t) {
        throw new RuntimeException("Implement me");

    }

    @Override
    public List<TypeViolation> visit(ClassInstanceType t) {
        throw new RuntimeException("Implement me");

    }

    @Override
    public List<TypeViolation> visit(ThisType t) {
        throw new RuntimeException("Implement me");

    }

    @Override
    public List<TypeViolation> visit(IndexType t) {
        throw new RuntimeException("Implement me");

    }

    @Override
    public List<TypeViolation> visit(IndexedAccessType t) {
        throw new RuntimeException("Implement me");

    }


    private void genericChecker(Type t) {
        Value restricted = filter(v, t, tc.getTypeContext(), false);
        Value restrictedNot = filter(v, t, tc.getTypeContext(), true);

        if(!restrictedNot.isNone() && !restricted.isNone()) expectOnly(restrictedNot);
        if(restricted.isNone()) expect();
    }

    private Value filter(Value v, Type t, TypeContext tc, boolean negative) {
        return t.accept(new TypeFilter(v, tc, negative));
    }

    private class TypeFilter implements TypeVisitor<Value> {
        final private Value v;
        final private TypeContext tc;
        final boolean negative;

        TypeFilter(Value v, TypeContext tc, boolean negative) {
            this.v = v;
            this.tc = tc;
            this.negative = negative;
        }


        @Override
        public Value visit(AnonymousType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(ClassType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(GenericType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(InterfaceType t) {
            //FIXME: Boxed primitive values ?
            //FIXME: Should this be a recursive procedure on the types of properties
            Value resv = Value.makeNone();
            if (negative) resv = v.restrictToNotObject();

            Set<ObjectLabel> labels = v.getAllObjectLabels();
            for (ObjectLabel l : labels) {
                boolean notmatching = false;

                for (String property : t.getDeclaredProperties().keySet()) {
                    Value propertyValue = pv.readPropertyValue(Collections.singletonList(l), property);
                    notmatching |= propertyValue.isMaybeAbsent();
                }

                if(!t.getDeclaredCallSignatures().isEmpty()) {
                    notmatching |= (l.getKind() != ObjectLabel.Kind.FUNCTION);
                }

                if (negative == notmatching) {
                    resv = resv.joinObject(l);
                }
            }
            return resv;
        }

        @Override
        public Value visit(ReferenceType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(SimpleType t) {
            switch(t.getKind()) {
                case Any:
                    return v;
                case String:
                    if(negative) return v.restrictToNotStr();
                    else return v.restrictToStr();
                case Null:
                    if(negative) return v.restrictToNotNull();
                    else return v.restrictToNull();
                case Number:
                    if(negative) return v.restrictToNotNum();
                    else return v.restrictToNum();
                case Boolean:
                    if(negative) return v.restrictToBool();
                    else return v.restrictToBool();
                case Undefined:
                case Void:
                    if(negative) return v.restrictToNotUndef();
                    else return v.restrictToUndef();
                case Enum:
                case Never:
                case Symbol:
                case Object:
                    throw new RuntimeException("Implement me for:" + t);
                default:
                    throw new RuntimeException("Unexpected");
            }
        }

        @Override
        public Value visit(TupleType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(UnionType t) {
            if(negative) {
                // values that don't match any of the types
                List<Value> notInEachType = t.getElements().stream().map(ct -> filter(v, ct, tc, negative)).collect(Collectors.toList());
                Value meet = notInEachType.stream().reduce(notInEachType.get(0), (a, c) -> a.meet(c));
                return meet;
            }
            else {
                // values that match some of the types
                List<Value> inEachType = t.getElements().stream().map(ct -> filter(v, ct, tc, negative)).collect(Collectors.toList());
                Value join = inEachType.stream().reduce(Value.makeNone(), (a, c) -> a.join(c));
                return join;
            }
        }

        @Override
        public Value visit(UnresolvedType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(TypeParameterType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(StringLiteral t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(BooleanLiteral t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(NumberLiteral t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(IntersectionType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(ClassInstanceType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(ThisType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(IndexType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(IndexedAccessType t) {
            throw new RuntimeException("Implement me");
        }

    }

}
