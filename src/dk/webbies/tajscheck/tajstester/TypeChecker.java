package dk.webbies.tajscheck.tajstester;

import dk.au.cs.casa.typescript.types.*;
import dk.brics.tajs.analysis.PropVarOperations;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.util.Pair;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.buildprogram.typechecks.TypeCheck;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TypeChecker implements TypeVisitor<List<TypeChecker.TypeViolation>> {

    private final TypeWithContext tc;
    private final Value v;
    private final State s;
    private final PropVarOperations pv;
    private final Test test;

    private final LinkedList<TypeViolation> violations = new LinkedList<TypeViolation>();
    private final Value filteredValue;

    public static class TypeViolation {
        final public String message;
        final public Test test;
        TypeViolation(String message, Test t){
            this.message = message;
            this.test = t;
        }

        @Override
        public String toString() {
            return message + " in test " + test;
        }
    }

    private TypeChecker(TypeWithContext tc, Value v, State s, Solver.SolverInterface c, Test test) {
        this.tc = tc;
        this.v = v;
        this.s = s;
        this.filteredValue = v;
        this.pv = c.getAnalysis().getPropVarOperations();
        this.test = test;
    }

    private void expectOnly() {
        violations.add(new TypeViolation("Expected " + tc + " but found spurious values: " + v, test));
    }

    private void expect() {
        violations.add(new TypeViolation("Expected " + tc + " but found value: " + v, test));
    }

    public static List<TypeViolation> typeCheck(TypeWithContext tc, Value v, State s, Solver.SolverInterface c, Test test){
        return tc.getType().accept(new TypeChecker(tc, v, s, c, test));
    }

    public static Pair<List<TypeViolation>, Value> typeCheckAndFilter(TypeWithContext tc, Value v, State s, Solver.SolverInterface c, Test test){
        TypeChecker checker = new TypeChecker(tc, v, s, c, test);
        List<TypeViolation> computedViolations = tc.getType().accept(checker);
        return Pair.make(computedViolations, checker.filteredValue);
    }

    @Override
    public List<TypeViolation> visit(AnonymousType t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(ClassType t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(GenericType t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(InterfaceType t) {
        //FIXME: Box primitive values ?
        if(!v.restrictToNotObject().isNone() && !v.restrictToObject().isNone()) expectOnly();
        if(v.restrictToObject().isNone()) {
            expect();
        }
        else {
            Set<ObjectLabel> labels = v.getAllObjectLabels();
            for(ObjectLabel l : labels) {
               for(String property : t.getDeclaredProperties().keySet()) {

                Value propertyValue = pv.readPropertyValue(Collections.singletonList(l), property);
                if(propertyValue.isMaybeAbsent()) {
                    violations.add(new TypeViolation("Expected " + tc + ", but found object without properties: " + property + " object: " + s.getObject(l, false), test));
                }

               }
            }
        }
        return violations;
    }

    @Override
    public List<TypeViolation> visit(ReferenceType t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(SimpleType t) {
        switch(t.getKind()) {
            case Any:
                // fine
                break;
            case String:
                if(!v.restrictToNotStr().isNone() && !v.restrictToStr().isNone()) expectOnly();
                if(v.restrictToStr().isNone()) expect();
                break;
            case Null:
                if(!v.restrictToNotNull().isNone() && !v.restrictToNull().isNone()) expectOnly();
                if(v.restrictToNull().isNone()) expect();
                break;
            case Number:
                if(!v.restrictToNotNum().isNone() && !v.restrictToNum().isNone()) expectOnly();
                if(v.restrictToNum().isNone()) expect();
                break;
            case Boolean:
                if(!v.restrictToNotBool().isNone() && !v.restrictToBool().isNone()) expectOnly();
                if(v.restrictToBool().isNone()) expect();
                break;
            case Undefined:
            case Void:
                if(!v.restrictToNotUndef().isNone() && !v.restrictToUndef().isNone()) expectOnly();
                if(v.restrictToUndef().isNone()) expect();
                break;
            case Enum:
            case Never:
            case Symbol:
            case Object:
                //FIXME: What to do ?
                break;
        }
        return violations;
    }

    @Override
    public List<TypeViolation> visit(TupleType t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(UnionType t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(UnresolvedType t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(TypeParameterType t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(StringLiteral t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(BooleanLiteral t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(NumberLiteral t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(IntersectionType t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(ClassInstanceType t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(ThisType t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(IndexType t) {
        return violations;
    }

    @Override
    public List<TypeViolation> visit(IndexedAccessType t) {
        return violations;
    }

}
