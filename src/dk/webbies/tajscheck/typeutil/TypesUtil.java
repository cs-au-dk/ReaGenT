package dk.webbies.tajscheck.typeutil;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.benchmark.FreeGenericsFinder;
import dk.webbies.tajscheck.benchmark.TypeParameterIndexer;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;
import dk.webbies.tajscheck.util.IdentityHashSet;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 01-11-2016.
 */
public class TypesUtil {

    private BenchmarkInfo info;

    public TypesUtil(BenchmarkInfo info) {
        this.info = info;
    }

    public static InterfaceType classToInterface(ClassType t, FreeGenericsFinder freeGenericsFinder) {
        InterfaceType interfaceType = SpecReader.makeEmptySyntheticInterfaceType();

        for (Signature signature : t.getSignatures()) {
            Signature constructor = createConstructorSignature(t, signature);
            interfaceType.getDeclaredConstructSignatures().add(constructor);
        }
        if (t.getSignatures().isEmpty()) {
            Signature constructor = new Signature();
            constructor.setHasRestParameter(false);
            constructor.setIsolatedSignatureType(null);
            constructor.setMinArgumentCount(0);
            constructor.setParameters(Collections.emptyList());
            constructor.setTarget(constructor);
            constructor.setTypeParameters(Collections.emptyList());
            constructor.setUnionSignatures(Collections.emptyList());
            constructor.setResolvedReturnType(t.getInstanceType());
            interfaceType.getDeclaredConstructSignatures().add(constructor);
        }

        interfaceType.setBaseTypes(
                t.getBaseTypes().stream().map(base -> {
                    if (base instanceof ClassType) {
                        return classToInterface((ClassType) base, freeGenericsFinder);
                    } else {
                        return base;
                    }
                }).collect(Collectors.toList())
        );

        interfaceType.setDeclaredProperties(t.getStaticProperties());

        interfaceType.setDeclaredNumberIndexType(t.getDeclaredNumberIndexType());

        interfaceType.setDeclaredStringIndexType(t.getDeclaredStringIndexType());

        // Target and typeArguments are out. But they are pretty much ignored in GenericType anyway (which is similar).

        interfaceType.setTypeParameters(t.getTypeParameters());

        if (freeGenericsFinder.hasThisTypes(t)) {
            freeGenericsFinder.addHasThisTypes(interfaceType);
        }

        return interfaceType;
    }

    public static Signature createConstructorSignature(ClassType t, Signature signature) {
        Signature constructor = new Signature();
        constructor.setHasRestParameter(signature.isHasRestParameter());
        constructor.setIsolatedSignatureType(signature.getIsolatedSignatureType());
        constructor.setMinArgumentCount(signature.getMinArgumentCount());
        constructor.setParameters(signature.getParameters());
        constructor.setTarget(signature.getTarget());
        constructor.setTypeParameters(signature.getTypeParameters());
        constructor.setUnionSignatures(signature.getUnionSignatures());
        constructor.setResolvedReturnType(t.getInstanceType());
        return constructor;
    }

    public TypeContext generateParameterMap(ReferenceType ref) {
        List<Type> arguments = ref.getTypeArguments();
        Map<TypeParameterType, Type> parameterMap = new HashMap<>();

        List<Type> typeParameters = getTypeParameters(ref.getTarget());
        assert typeParameters.size() == arguments.size();
        List<TypeParameterType> parameters = Util.cast(TypeParameterType.class, typeParameters);
        parameterMap = new HashMap<>(parameterMap);
        for (int i = 0; i < arguments.size(); i++) {
            parameterMap.put(parameters.get(i), arguments.get(i));
        }
        return TypeContext.create(info).append(parameterMap);
    }

    public static List<Type> getTypeParameters(Type target) {
        if (target instanceof GenericType) {
            return ((GenericType) target).getTypeParameters();
        } else if (target instanceof InterfaceType) {
            return ((InterfaceType) target).getTypeParameters();
        } else if (target instanceof ClassType) {
            return ((ClassType) target).getTypeParameters();
        } else if (target instanceof ClassInstanceType) {
            return ((ClassType) ((ClassInstanceType) target).getClassType()).getTypeParameters();
        } else if (target instanceof TupleType) {
            return ((TupleType) target).getElementTypes();
        } else {
            throw new RuntimeException(target.getClass().getName());
        }
    }

    public TypeContext generateParameterMap(ReferenceType type, TypeContext typeContext) {
        return typeContext.append(generateParameterMap(type));
    }

    public static Set<Type> collectAllTypes(Collection<Type> types) {
        CollectAllTypesVisitor visitor = new CollectAllTypesVisitor();
        for (Type type : types) {
            visitor.accept(type);
        }
        return visitor.getSeen();
    }

    public static Set<Type> collectAllTypes(Type type) {
        return collectAllTypes(Collections.singletonList(type));
    }

    public static Set<Type> collectNativeTypes(SpecReader spec, SpecReader emptySpec) {
        Map<Type, String> specNames = ParseDeclaration.getTypeNamesMap(spec);

        Map<Type, String> nativeNameMap = ParseDeclaration.getTypeNamesMap(emptySpec);
        Set<String> nativeNames = new HashSet<>(nativeNameMap.values());

        Map<String, Type> inverseNativeNameMap = new HashMap<>();
        for (Map.Entry<Type, String> entry : nativeNameMap.entrySet()) {
            if (inverseNativeNameMap.containsKey(entry.getValue())) {
                inverseNativeNameMap.put(entry.getValue(), null);
            } else {
                inverseNativeNameMap.put(entry.getValue(), entry.getKey());
            }
        }

        return specNames.entrySet().stream().filter(entry -> {
            if (!nativeNames.contains(entry.getValue())) {
                return false;
            }
            Type type = inverseNativeNameMap.get(entry.getValue());
            return type != null && entry.getKey().getClass().equals(type.getClass());
        }).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    public static boolean isEmptyInterface(InterfaceType type) {
        return type.getDeclaredProperties().isEmpty() &&
                type.getBaseTypes().isEmpty() &&
                type.getTypeParameters().isEmpty() &&
                type.getDeclaredCallSignatures().isEmpty() &&
                type.getDeclaredConstructSignatures().isEmpty() &&
                type.getDeclaredStringIndexType() == null &&
                type.getDeclaredNumberIndexType() == null;
    }

    public static List<Type> findRecursiveDefinition(TypeParameterType firstType, TypeContext typeContext, TypeParameterIndexer typeParameterIndexer) {
        List<Type> constraints = new ArrayList<>();

        constraints.add(firstType.getConstraint());

        {
            String markerField = typeParameterIndexer.getMarkerField(firstType);
            InterfaceType markerConstraint = SpecReader.makeEmptySyntheticInterfaceType();
            markerConstraint.getDeclaredProperties().put(markerField, new BooleanLiteral(true));
            constraints.add(markerConstraint);
        }

        // The TypeContexts are not used for anything here, so it is ok to ignore them.
        TypeWithContext lookup = typeContext.get(firstType);
        Type type = lookup != null ? lookup.getType() : null;

        Set<Type> seen = new HashSet<>();

        while (type instanceof TypeParameterType && typeContext.containsKey((TypeParameterType)type)) {
            if (type.equals(firstType)) {
                return constraints; // There is an infinite loop, starting with firstType, return something satisfying the constraints.
            }
            if (seen.contains(type)) { // There is some infinite loop, later, it isn't handled now.
                return new ArrayList<>();
            }
            seen.add(type);
            constraints.add(((TypeParameterType) type).getConstraint());
            {
                String markerField = typeParameterIndexer.getMarkerField((TypeParameterType) type);
                InterfaceType markerConstraint = SpecReader.makeEmptySyntheticInterfaceType();
                markerConstraint.getDeclaredProperties().put(markerField, new BooleanLiteral(true));
                constraints.add(markerConstraint);
            }
            type = typeContext.get((TypeParameterType)type).getType();
        }

        return new ArrayList<>();
    }

    public static List<Signature> splitSignatures(List<Signature> signatures) {
        return signatures.stream().map(TypesUtil::splitSignature).reduce(new ArrayList<>(), Util::reduceList);
    }

    private static List<Signature> splitSignature(Signature signature) {
        for (int i = 0; i < signature.getParameters().size(); i++) {
            Signature.Parameter parameter = signature.getParameters().get(i);
            if (parameter.getType() instanceof UnionType) {
                List<Type> elements = ((UnionType) parameter.getType()).getElements();

                List<Signature> result = new ArrayList<>();

                for (Type element : elements) {
                    Signature subSignature = cloneSignature(signature);
                    Signature.Parameter newParameter = new Signature.Parameter();
                    newParameter.setName(parameter.getName());
                    newParameter.setType(element);

                    subSignature.getParameters().set(i, newParameter);

                    result.addAll(splitSignature(subSignature));
                }
                return result;
            }
        }
        return Collections.singletonList(signature);
    }

    private static Signature cloneSignature(Signature signature) {
        Signature result = new Signature();
        result.setResolvedReturnType(signature.getResolvedReturnType());
        result.setUnionSignatures(signature.getUnionSignatures());
        result.setTarget(signature.getTarget());
        result.setParameters(new ArrayList<>(signature.getParameters()));
        result.setHasRestParameter(signature.isHasRestParameter());
        result.setIsolatedSignatureType(signature.getIsolatedSignatureType());
        result.setMinArgumentCount(signature.getMinArgumentCount());
        result.setTypeParameters(signature.getTypeParameters());
        return result;
    }

    public static List<Signature> removeDuplicateSignatures(List<Signature> signatures) {
        return signatures.stream().map(SignatureComparisonContainer::new).distinct().map(SignatureComparisonContainer::getSignature).collect(Collectors.toList());
    }

    public static Type getNativeBase(Type type, Set<Type> nativeTypes, Map<Type, String> typeNames) {
        Predicate<Type> isNativeType = t -> {
            if (t instanceof InterfaceType && TypesUtil.isEmptyInterface((InterfaceType) t)) {
                return false;
            }
            if (nativeTypes.contains(t)) {
                return true;
            }
            if (t instanceof ReferenceType && nativeTypes.contains(((ReferenceType) t).getTarget())) {
                return true;
            }
            return false;
        };
        List<Type> nativeBaseTypes = getAllBaseTypes(type, new HashSet<>(), Util.not(isNativeType)).stream().filter(isNativeType).collect(Collectors.toList());

        if (nativeBaseTypes.size() == 0) {
            return null;
        } else if (nativeBaseTypes.size() == 1) {
            return nativeBaseTypes.iterator().next();
        }
        return null; // Hard to do better.
    }



    public static Set<Type> getAllBaseTypes(Type type, Set<Type> acc) {
        return getAllBaseTypes(type, acc, (subType) -> true);
    }

    private static Set<Type> getAllBaseTypes(Type type, Set<Type> acc, Predicate<Type> shouldContinue) {
        if (acc.contains(type)) {
            return acc;
        }
        acc.add(type);
        if (type instanceof ReferenceType) {
            type = ((ReferenceType) type).getTarget();
        }
        if (type instanceof GenericType) {
            if (shouldContinue.test(type)) {
                for (Type base : ((GenericType) type).getBaseTypes()) {
                    getAllBaseTypes(base, acc, shouldContinue);
                }
            }
        }
        if (type instanceof ClassInstanceType) {
            type = ((ClassType) ((ClassInstanceType) type).getClassType()).getInstanceType();
        }
        if (type instanceof InterfaceType) {
            if (shouldContinue.test(type)) {
                for (Type base : ((InterfaceType) type).getBaseTypes()) {
                    getAllBaseTypes(base, acc, shouldContinue);
                }
            }
        }
        if (type instanceof ClassType) {
            if (shouldContinue.test(type)) {
                for (Type base : ((ClassType) type).getBaseTypes()) {
                    getAllBaseTypes(base, acc, shouldContinue);
                }
            }
        }

        return acc;
    }

    public Set<TypeWithContext> getAllStringIndexerTypes(Type t, TypeContext context) {
        Set<TypeWithContext> res = new HashSet<>();
        getAllStringIndexerTypes(t, context, res);
        return res.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private void getAllStringIndexerTypes(Type t, TypeContext context, Set<TypeWithContext> acc) {
        if (info.nativeTypes.contains(t)) {
            return;
        }
        if (t instanceof InterfaceType) {
            if (((InterfaceType) t).getDeclaredStringIndexType() != null) {
                acc.add(new TypeWithContext(((InterfaceType) t).getDeclaredStringIndexType(), context));
            }
            ((InterfaceType) t).getBaseTypes().forEach(type ->
                    getAllStringIndexerTypes(type, context, acc)
            );
        } else if (t instanceof ClassInstanceType) {
            InterfaceType instanceType = ((ClassType) ((ClassInstanceType) t).getClassType()).getInstanceType();
            getAllStringIndexerTypes(instanceType, context, acc);
        } else if (t instanceof ClassType) {
            if (((ClassType) t).getDeclaredStringIndexType() != null) {
                acc.add(new TypeWithContext(((ClassType) t).getDeclaredStringIndexType(), context));
            }
            ((ClassType) t).getBaseTypes().forEach(type -> {
                getAllStringIndexerTypes(type, context, acc);
            });
        } else if (t instanceof GenericType) {
            getAllStringIndexerTypes(((GenericType) t).toInterface(), context, acc);
        } else if (t instanceof ReferenceType) {
            getAllStringIndexerTypes(((ReferenceType) t).getTarget(), generateParameterMap((ReferenceType) t, context), acc);
        } else {
            throw new RuntimeException(t.getClass().getSimpleName());
        }
    }


    public Set<TypeWithContext> getAllNumberIndexerTypes(Type t, TypeContext context) {
        Set<TypeWithContext> res = new HashSet<>();
        getAllNumberIndexerTypes(t, context, res);
        return res.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private void getAllNumberIndexerTypes(Type t, TypeContext context, Set<TypeWithContext> acc) {
        if (info.nativeTypes.contains(t)) {
            return;
        }
        if (t instanceof InterfaceType) {
            if (((InterfaceType) t).getDeclaredNumberIndexType() != null) {
                acc.add(new TypeWithContext(((InterfaceType) t).getDeclaredNumberIndexType(), context));
            }
            ((InterfaceType) t).getBaseTypes().forEach(type ->
                    getAllNumberIndexerTypes(type, context, acc)
            );
        } else if (t instanceof ClassInstanceType) {
            InterfaceType instanceType = ((ClassType) ((ClassInstanceType) t).getClassType()).getInstanceType();
            getAllNumberIndexerTypes(instanceType, context, acc);
        } else if (t instanceof ClassType) {
            if (((ClassType) t).getDeclaredStringIndexType() != null) {
                acc.add(new TypeWithContext(((ClassType) t).getDeclaredStringIndexType(), context));
            }
            ((ClassType) t).getBaseTypes().forEach(type -> {
                getAllNumberIndexerTypes(type, context, acc);
            });
        } else if (t instanceof GenericType) {
            getAllNumberIndexerTypes(((GenericType) t).toInterface(), context, acc);
        } else if (t instanceof ReferenceType) {
            getAllNumberIndexerTypes(((ReferenceType) t).getTarget(), generateParameterMap((ReferenceType) t, context), acc);
        } else {
            throw new RuntimeException(t.getClass().getSimpleName());
        }
    }


    public Set<Pair<TypeContext, Map<String, Type>>> getAllPropertyDeclarations(Type t, TypeContext context) {
        Set<Pair<TypeContext, Map<String, Type>>> res = new HashSet<>();
        getAllPropertyDeclarations(t, context, res);
        return res.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private void getAllPropertyDeclarations(Type t, TypeContext context, Set<Pair<TypeContext, Map<String, Type>>> acc) {
        if (info.nativeTypes.contains(t)) {
            return;
        }
        if (t instanceof InterfaceType) {
            acc.add(new Pair<>(context, ((InterfaceType) t).getDeclaredProperties()));
            ((InterfaceType) t).getBaseTypes().forEach(type ->
                    getAllPropertyDeclarations(type, context, acc)
            );
        } else if (t instanceof ClassInstanceType) {
            InterfaceType instanceType = ((ClassType) ((ClassInstanceType) t).getClassType()).getInstanceType();
            getAllPropertyDeclarations(instanceType, context, acc);
        } else if (t instanceof ClassType) {
            acc.add(new Pair<>(context, ((ClassType) t).getStaticProperties()));
            ((ClassType) t).getBaseTypes().forEach(type -> {
                getAllPropertyDeclarations(type, context, acc);
            });
        } else if (t instanceof GenericType) {
            getAllPropertyDeclarations(((GenericType) t).toInterface(), context, acc);
        } else if (t instanceof ReferenceType) {
            getAllPropertyDeclarations(((ReferenceType) t).getTarget(), generateParameterMap((ReferenceType) t, context), acc);
        } else {
            throw new RuntimeException(t.getClass().getSimpleName());
        }
    }


    private static final class SignatureComparisonContainer {
        private final Signature signature;

        private SignatureComparisonContainer(Signature signature) {
            this.signature = signature;
        }

        public Signature getSignature() {
            return signature;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SignatureComparisonContainer that = (SignatureComparisonContainer) o;

            Signature sig1 = this.signature;
            Signature sig2 = that.signature;
            if (sig1.getTypeParameters().size() != sig2.getTypeParameters().size()) {
                return false;
            }
            for (int i = 0; i < sig1.getTypeParameters().size(); i++) {
                if (!sig1.getTypeParameters().get(i).equals(sig2.getTypeParameters().get(i))) {
                    return false;
                }
            }

            if (sig1.getParameters().size() != sig2.getParameters().size()) {
                return false;
            }
            for (int i = 0; i < sig1.getParameters().size(); i++) {
                if (!sig1.getParameters().get(i).equals(sig2.getParameters().get(i))) {
                    return false;
                }
            }

            if (!Objects.equals(sig1.getResolvedReturnType(), sig2.getResolvedReturnType())) {
                return false;
            }

            if (sig1.getMinArgumentCount() != sig2.getMinArgumentCount()) {
                return false;
            }

            if (sig1.isHasRestParameter() != sig2.isHasRestParameter()) {
                return false;
            }

            if (!Objects.equals(sig1.getTarget(), sig2.getTarget())) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return 1337;
        }
    }


    private static class CollectAllTypesVisitor implements TypeVisitor<Void> {
        private final Set<Type> seen = new IdentityHashSet<>();

        Set<Type> getSeen() {
            return seen;
        }

        @Override
        public Void visit(AnonymousType t) {
            return null;
        }

        @Override
        public Void visit(ClassType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            t.getSignatures().forEach(this::acceptSignature);

            t.getInstanceType().accept(this);

            t.getBaseTypes().forEach(this::accept);

            t.getStaticProperties().values().forEach(this::accept);

            t.getTarget().accept(this);

            t.getTypeArguments().forEach(this::accept);

            return null;
        }

        @Override
        public Void visit(GenericType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            t.getTypeArguments().forEach(this::accept);
            t.getBaseTypes().forEach(this::accept);
            t.getDeclaredProperties().values().forEach(this::accept);
            Util.concat(t.getDeclaredCallSignatures(), t.getDeclaredConstructSignatures()).forEach(this::acceptSignature);
            if (t.getDeclaredStringIndexType() != null) {
                t.getDeclaredStringIndexType().accept(this);
            }
            if (t.getDeclaredNumberIndexType() != null) {
                t.getDeclaredNumberIndexType().accept(this);
            }
            t.getTarget().accept(this);
            t.getTypeArguments().forEach(this::accept);
            t.toInterface().accept(this);
            return null;
        }

        private void accept(Type type) {
            type.accept(this);
        }

        @Override
        public Void visit(InterfaceType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            t.getTypeParameters().forEach(this::accept);
            t.getBaseTypes().forEach(this::accept);
            t.getDeclaredProperties().values().forEach(this::accept);
            Util.concat(t.getDeclaredCallSignatures(), t.getDeclaredConstructSignatures()).forEach(this::acceptSignature);
            if (t.getDeclaredStringIndexType() != null) {
                t.getDeclaredStringIndexType().accept(this);
            }
            if (t.getDeclaredNumberIndexType() != null) {
                t.getDeclaredNumberIndexType().accept(this);
            }
            return null;
        }

        private void acceptSignature(Signature sig) {
            if (sig.getResolvedReturnType() != null) {
                sig.getResolvedReturnType().accept(this);
            }
            sig.getParameters().stream().map(Signature.Parameter::getType).forEach(this::accept);
            if (sig.getTarget() != null) {
                acceptSignature(sig.getTarget());
            }
            sig.getUnionSignatures().forEach(this::acceptSignature);
            if (sig.getIsolatedSignatureType() != null) {
                sig.getIsolatedSignatureType().accept(this);
            }
        }

        @Override
        public Void visit(ReferenceType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            t.getTarget().accept(this);
            t.getTypeArguments().forEach(this::accept);
            return null;
        }

        @Override
        public Void visit(SimpleType t) {
            seen.add(t);
            return null;
        }

        @Override
        public Void visit(TupleType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            t.getElementTypes().forEach(this::accept);

            return null;
        }

        @Override
        public Void visit(UnionType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            t.getElements().forEach(this::accept);

            return null;
        }

        @Override
        public Void visit(IntersectionType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            t.getElements().forEach(this::accept);

            return null;
        }

        @Override
        public Void visit(ClassInstanceType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            t.getClassType().accept(this);

            return null;
        }

        @Override
        public Void visit(ThisType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            t.getConstraint().accept(this);

            return null;
        }

        @Override
        public Void visit(IndexType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            t.getType().accept(this);

            return null;
        }

        @Override
        public Void visit(IndexedAccessType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            t.getObjectType().accept(this);
            t.getIndexType().accept(this);

            return null;
        }

        @Override
        public Void visit(UnresolvedType t) {
            throw new RuntimeException();
        }

        @Override
        public Void visit(TypeParameterType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            if (t.getConstraint() != null) {
                t.getConstraint().accept(this);
            }
            return null;
        }

        @Override
        public Void visit(StringLiteral t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            return null;
        }

        @Override
        public Void visit(BooleanLiteral t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            return null;
        }

        @Override
        public Void visit(NumberLiteral t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            return null;
        }
    }

    public Pair<InterfaceType, TypeContext> constructSyntheticInterfaceWithBaseTypes(InterfaceType inter, Map<Type, String> typeNames, FreeGenericsFinder freeGenericsFinder) {
        if (inter.getBaseTypes().isEmpty()) {
            return new Pair<>(inter, TypeContext.create(info));
        }
//        assert inter.getTypeParameters().isEmpty(); // This should only happen when constructed from a generic/reference type, and in that case we have handled the TypeParameters.
        Map<TypeParameterType, Type> newParameters = new HashMap<>();
        InterfaceType result = SpecReader.makeEmptySyntheticInterfaceType();

        result.getDeclaredCallSignatures().addAll(inter.getDeclaredCallSignatures());
        result.getDeclaredConstructSignatures().addAll(inter.getDeclaredConstructSignatures());
        result.setDeclaredNumberIndexType(inter.getDeclaredNumberIndexType());
        result.setDeclaredStringIndexType(inter.getDeclaredStringIndexType());

        typeNames.put(result, typeNames.get(inter));
        inter.getBaseTypes().forEach(subType -> {
            if (subType instanceof ReferenceType) {
                newParameters.putAll(generateParameterMap((ReferenceType) subType).getMap());
                subType = ((ReferenceType) subType).getTarget();
            }
            if (subType instanceof ClassType) {
                subType = TypesUtil.classToInterface((ClassType) subType, freeGenericsFinder);
            }
            if (subType instanceof GenericType) {
                subType = ((GenericType) subType).toInterface();
            }
            if (subType instanceof ClassInstanceType) {
                subType = ((ClassType) ((ClassInstanceType) subType).getClassType()).getInstanceType();
            }
            Pair<InterfaceType, TypeContext> pair = constructSyntheticInterfaceWithBaseTypes((InterfaceType) subType, typeNames, freeGenericsFinder);
            newParameters.putAll(pair.getRight().getMap());
            InterfaceType type = pair.getLeft();
            result.getDeclaredCallSignatures().addAll((type.getDeclaredCallSignatures()));
            result.getDeclaredConstructSignatures().addAll(type.getDeclaredConstructSignatures());
            if (result.getDeclaredNumberIndexType() == null) {
                result.setDeclaredNumberIndexType(type.getDeclaredNumberIndexType());
            }
            if (result.getDeclaredStringIndexType() == null) {
                result.setDeclaredStringIndexType(type.getDeclaredStringIndexType());
            }
            result.getDeclaredProperties().putAll(inter.getDeclaredProperties());
            for (Map.Entry<String, Type> entry : type.getDeclaredProperties().entrySet()) {
                if (result.getDeclaredProperties().containsKey(entry.getKey())) {
                    continue;
                }
                result.getDeclaredProperties().put(entry.getKey(), entry.getValue());
            }
        });
        return new Pair<>(result, TypeContext.create(info).append(newParameters));
    }


}
