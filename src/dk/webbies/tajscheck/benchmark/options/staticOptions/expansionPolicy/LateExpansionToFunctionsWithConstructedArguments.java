package dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy;

import dk.au.cs.casa.typescript.types.*;
import dk.brics.tajs.analysis.Solver;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.tajstester.TajsTypeTester;
import dk.webbies.tajscheck.tajstester.TypeValuesHandler;
import dk.webbies.tajscheck.testcreator.test.FunctionTest;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;

import java.util.*;
import java.util.function.Predicate;

public class LateExpansionToFunctionsWithConstructedArguments implements ExpansionPolicy {

    private Test fallbackExpansion = null;

    @Override
    public void nextRound() {
        fallbackExpansion = null;
    }

    @Override
    public boolean expandTo(FunctionTest test, TajsTypeTester typeTester) {
        if (hasEasilyConstructedArguments(test, typeTester)) {
            return true;
        }
        fallbackExpansion = test;
        return false;
    }

    @Override
    public Collection<Test> getTestsToPerformAnyway(Solver.SolverInterface c) {
        if (!c.getWorklist().isEmpty() || fallbackExpansion == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(fallbackExpansion);
    }

    private boolean hasEasilyConstructedArguments(FunctionTest test, TajsTypeTester typeTester) {
        return test.getParameters().stream()
                .map(type -> this.isEasilyConstructType(type, test.getTypeContext(), typeTester))
                .reduce(true, Boolean::logicalAnd);
    }

    private boolean isEasilyConstructType(Type type, TypeContext typeContext, TajsTypeTester typeTester) {
        if (!typeTester.getBenchmarkInfo().shouldConstructType(type)) {
            return false; // if we cannot construct it, it is not a constructed type.
        }

        return type.accept(new CanEasilyConstructVisitor(typeContext, typeTester));
    }

    private final static class CanEasilyConstructVisitor implements TypeVisitor<Boolean> {
        private final Predicate<Type> hasFeedbackValue;
        private final TypeContext typeContext;
        private final TajsTypeTester typeTester;

        public CanEasilyConstructVisitor(TypeContext typeContext, TajsTypeTester typeTester) {
            TypeValuesHandler valueHandler = typeTester.getValueHandler();
            this.typeContext = typeContext;
            this.typeTester = typeTester;

            this.hasFeedbackValue = type -> valueHandler.findFeedbackValue(new TypeWithContext(type, typeContext)) != null;
        }

        @Override
        public Boolean visit(AnonymousType t) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(ClassType t) {
            return hasFeedbackValue.test(t);
        }

        @Override
        public Boolean visit(GenericType t) {
            return hasFeedbackValue.test(t) || hasFeedbackValue.test(t.toInterface());
        }

        @Override
        public Boolean visit(InterfaceType t) {
            boolean isCallback = t.getDeclaredProperties().isEmpty() && t.getDeclaredStringIndexType() == null && t.getDeclaredNumberIndexType() == null && t.getDeclaredConstructSignatures().isEmpty() && !t.getDeclaredCallSignatures().isEmpty();
            return isCallback || hasFeedbackValue.test(t);
        }

        @Override
        public Boolean visit(ReferenceType t) {
            if (hasFeedbackValue.test(t)) {
                return true;
            }
            if ("Array".equals(typeTester.getBenchmarkInfo().typeNames.get(t.getTarget()))) {
                Type indexType = t.getTypeArguments().iterator().next();
                return indexType.accept(this);
            }
            return t.getTarget().accept(new CanEasilyConstructVisitor(typeTester.getBenchmarkInfo().typesUtil.generateParameterMap(t, typeContext), typeTester));
        }

        @Override
        public Boolean visit(SimpleType t) {
            return true;
        }

        @Override
        public Boolean visit(TupleType t) {
            return t.getElementTypes().stream().map(subType -> subType.accept(this)).reduce(true, Boolean::logicalAnd);
        }

        @Override
        public Boolean visit(UnionType t) {
            return t.getElements().stream().map(subType -> subType.accept(this)).reduce(true, Boolean::logicalAnd);
        }

        @Override
        public Boolean visit(TypeParameterType t) {
            if (!typeContext.containsKey(t)) {
                return true;
            }
            TypeWithContext nested = typeContext.get(t);
            return nested.getType().accept(new CanEasilyConstructVisitor(nested.getTypeContext(), typeTester));
        }

        @Override
        public Boolean visit(StringLiteral t) {
            return true;
        }

        @Override
        public Boolean visit(BooleanLiteral t) {
            return true;
        }

        @Override
        public Boolean visit(NumberLiteral t) {
            return true;
        }

        @Override
        public Boolean visit(IntersectionType t) {
            return true;
        }

        @Override
        public Boolean visit(ClassInstanceType t) {
            return hasFeedbackValue.test(t);
        }

        @Override
        public Boolean visit(ThisType t) {
            return typeContext.getThisType().accept(this);
        }

        @Override
        public Boolean visit(IndexType t) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(IndexedAccessType t) {
            throw new RuntimeException();
        }
    }
}
