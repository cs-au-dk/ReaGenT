package dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy;

import dk.au.cs.casa.typescript.types.*;
import dk.brics.tajs.analysis.Solver;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.TajsTypeTester;
import dk.webbies.tajscheck.tajstester.typeCreator.SpecInstantiator;
import dk.webbies.tajscheck.testcreator.test.FunctionTest;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LateExpansionToFunctionsWithConstructedArguments implements ExpansionPolicy {
    private final boolean expandToSyntheticallyCreatedArguments;

    private FunctionTest fallbackExpansion = null;

    private Set<TypeWithContext> argumentsThatAreConstructedAnyway = new HashSet<>();

    public LateExpansionToFunctionsWithConstructedArguments(boolean performTestAnyway) {
        this.expandToSyntheticallyCreatedArguments = performTestAnyway;
    }

    public LateExpansionToFunctionsWithConstructedArguments() {
        this(true);
    }

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
        if (!c.getWorklist().isEmpty() || fallbackExpansion == null || !expandToSyntheticallyCreatedArguments) {
            return Collections.emptyList();
        }

        argumentsThatAreConstructedAnyway.addAll(fallbackExpansion.getParameters().stream().map(arg -> new TypeWithContext(arg, fallbackExpansion.getTypeContext().optimizeTypeParameters(arg))).collect(Collectors.toList()));

        return Collections.singletonList(fallbackExpansion);
    }

    private boolean hasEasilyConstructedArguments(FunctionTest test, TajsTypeTester typeTester) {
        return test.getParameters().stream()
                .map(type -> isEasilyConstructType(type, test.getTypeContext(), typeTester, argumentsThatAreConstructedAnyway))
                .reduce(true, Boolean::logicalAnd);
    }

    private static boolean isEasilyConstructType(Type type, TypeContext typeContext, TajsTypeTester typeTester, Set<TypeWithContext> isConstructable) {
        if (!typeTester.getBenchmarkInfo().shouldConstructType(type)) {
            return false; // if we cannot construct it, it is not a constructed type.
        }
        SpecInstantiator instantiator = typeTester.getValueHandler().getInstantiator();
        Predicate<TypeWithContext> whiteList = subType ->
                instantiator.getNativesInstantiator().shouldConstructAsNative(subType.getType()) ||
                isConstructable.contains(subType) ||
                instantiator.getFeedbackValue(subType.getType(), subType.getTypeContext()) != null;

        return type.accept(new CanEasilyConstructVisitor(typeContext, typeTester.getBenchmarkInfo(), whiteList));
    }

    public static final class CanEasilyConstructVisitor implements TypeVisitor<Boolean> {
        private final Predicate<Type> inWhiteList;
        private final TypeContext typeContext;
        private final Predicate<TypeWithContext> whiteList;
        private BenchmarkInfo info;


        public CanEasilyConstructVisitor(TypeContext typeContext, BenchmarkInfo info, Predicate<TypeWithContext> whileList) {
            this.typeContext = typeContext;
            this.info = info;
            this.whiteList = whileList;

            this.inWhiteList = type -> whileList.test(new TypeWithContext(type, typeContext)) || whileList.test(new TypeWithContext(type, typeContext.optimizeTypeParameters(type)));
        }

        @Override
        public Boolean visit(AnonymousType t) {
            throw new RuntimeException();
        }

        @Override
        public Boolean visit(ClassType t) {
            return inWhiteList.test(t);
        }

        @Override
        public Boolean visit(GenericType t) {
            return inWhiteList.test(t) || t.toInterface().accept(this);
        }

        @Override
        public Boolean visit(InterfaceType t) {
            boolean isCallback = t.getDeclaredProperties().isEmpty() && t.getDeclaredStringIndexType() == null && t.getDeclaredNumberIndexType() == null && t.getDeclaredConstructSignatures().isEmpty() && !t.getDeclaredCallSignatures().isEmpty();
            return isCallback || inWhiteList.test(t);
        }

        @Override
        public Boolean visit(ReferenceType t) {
            if ("Array".equals(info.typeNames.get(t.getTarget()))) {
                Type indexType = t.getTypeArguments().iterator().next();
                return indexType.accept(this);
            }
            if (inWhiteList.test(t)) {
                return true;
            }
            return t.getTarget().accept(new CanEasilyConstructVisitor(info.typesUtil.generateParameterMap(t, typeContext), info, whiteList));
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
            if (nested.getType().equals(t)) {
                return true;
            }
            if (Thread.currentThread().getStackTrace().length > 1000) {
                System.out.println();
            }
            return inWhiteList.test(t) || nested.getType().accept(new CanEasilyConstructVisitor(nested.getTypeContext(), info, (innerT) -> innerT.getType() == t || whiteList.test(innerT)));
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
            return inWhiteList.test(t);
        }

        @Override
        public Boolean visit(ThisType t) {
            return inWhiteList.test(t) || typeContext.getThisType().accept(this);
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
