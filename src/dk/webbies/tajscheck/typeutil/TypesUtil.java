package dk.webbies.tajscheck.typeutil;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.buildprogram.TestProgramBuilder;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 01-11-2016.
 */
public class TypesUtil {
    private final Benchmark bench;

    public TypesUtil(Benchmark bench) {
        this.bench = bench;
    }

    public static InterfaceType classToInterface(ClassType t, Set<Type> hasThisTypes) {
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
                        return classToInterface((ClassType) base, hasThisTypes);
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

        if (hasThisTypes.contains(t)) {
            hasThisTypes.add(interfaceType);
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

        assertAboutTarget(ref.getTarget());

        List<Type> typeParameters = getTypeParameters(ref.getTarget());
        assert typeParameters.size() == arguments.size();
        List<TypeParameterType> parameters = Util.cast(TypeParameterType.class, typeParameters);
        parameterMap = new HashMap<>(parameterMap);
        for (int i = 0; i < arguments.size(); i++) {
            parameterMap.put(parameters.get(i), arguments.get(i));
        }
        return new TypeContext(bench).append(parameterMap);
    }

    private static void assertAboutTarget(Type untypedTarget) {
        if (untypedTarget instanceof GenericType) {
            GenericType target = (GenericType) untypedTarget;
            assert target.getTypeParameters().equals(target.getTypeArguments());
            assert target.getTarget() == target;
        } else if (untypedTarget instanceof ClassType) {
            ClassType target = (ClassType) untypedTarget;
            assert target.getTypeParameters().equals(target.getTypeArguments());
            assert target.getTarget() == target || (target.getTarget() instanceof ClassInstanceType && ((ClassInstanceType) target.getTarget()).getClassType().equals(target));
        } else if (untypedTarget instanceof ClassInstanceType || untypedTarget instanceof TupleType) {
            // nothing.
        } else {
            assert untypedTarget instanceof InterfaceType;
        }
    }

    private static List<Type> getTypeParameters(Type target) {
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
        return typeContext.append(generateParameterMap(type).getMap());
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

        Set<String> nativeNames = new HashSet<>(ParseDeclaration.getTypeNamesMap(emptySpec).values());

        return specNames.entrySet().stream().filter(entry -> nativeNames.contains(entry.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
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

    public static List<Type> findRecursiveDefinition(TypeParameterType firstType, TypeContext typeContext, TestProgramBuilder.TypeParameterIndexer typeParameterIndexer) {
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
        getAllBaseTypes(type, new HashSet<>(), Util.not(isNativeType));
        throw new RuntimeException(nativeBaseTypes.stream().map(typeNames::get).collect(Collectors.toList()).toString());
    }

    public static Set<Type> getAllBaseTypes(Type type, Set<Type> acc) {
        return getAllBaseTypes(type, acc, (subType) -> true);
    }

    public static Set<Type> getAllBaseTypes(Type type, Set<Type> acc, Predicate<Type> shouldContinue) {
        if (acc.contains(type)) {
            return acc;
        }
        acc.add(type);
        if (type instanceof ReferenceType) {
            type = ((ReferenceType) type).getTarget();
        }
        if (type instanceof GenericType) {
            type = ((GenericType) type).toInterface();
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

    public static boolean isThisTypeVisible(Type baseType) {
        return isThisTypeVisible(baseType, true);
    }

    public static boolean isThisTypeVisible(Type baseType, boolean deep) {
        assert !(baseType instanceof ClassType); // <- this shouldn't happen. It could, and it would be sound, but it is better optimized if it never happens.
        if (baseType instanceof ClassInstanceType) {
            baseType = ((ClassType) ((ClassInstanceType) baseType).getClassType()).getInstanceType();
        }
        if (baseType instanceof ReferenceType) {
            if (((ReferenceType) baseType).getTypeArguments().stream().anyMatch(ThisType.class::isInstance)) {
                return true;
            }
            baseType = ((ReferenceType) baseType).getTarget();
        }
        if (baseType instanceof GenericType) {
            baseType = ((GenericType) baseType).toInterface();
        }
        if (baseType instanceof ClassInstanceType) {
            baseType = ((ClassType) ((ClassInstanceType) baseType).getClassType()).getInstanceType();
        }
        if (baseType instanceof InterfaceType) {
            InterfaceType inter = (InterfaceType) baseType;

            for (Signature signature : Util.concat(inter.getDeclaredCallSignatures(), inter.getDeclaredConstructSignatures())) {
                if (signature.getParameters().stream().map(Signature.Parameter::getType).anyMatch(par -> isThisTypeVisible(par, false))) {
                    return true;
                }
                if (isThisTypeVisible(signature.getResolvedReturnType(), false)) {
                    return true;
                }
            }


            if (!deep) {
                return false;
            }
            for (Type type : inter.getDeclaredProperties().values()) {
                if (isThisTypeVisible(type, false)) {
                    return true;
                }
            }
            return false;
        }
        if (baseType instanceof SimpleType || baseType instanceof BooleanLiteral || baseType instanceof StringLiteral || baseType instanceof NumberLiteral || baseType instanceof TypeParameterType) {
            return false;
        }

        if (baseType instanceof ThisType) {
            return true;
        }
        if (baseType instanceof UnionType) {
            return ((UnionType) baseType).getElements().stream().anyMatch(element -> isThisTypeVisible(element, deep));
        }
        if (baseType instanceof TupleType) {
            return ((TupleType) baseType).getElementTypes().stream().anyMatch(element -> isThisTypeVisible(element, deep));
        }
        if (baseType instanceof IndexedAccessType) {
            return isThisTypeVisible(((IndexedAccessType) baseType).getIndexType(), deep) || isThisTypeVisible(((IndexedAccessType) baseType).getObjectType(), deep);
        }
        throw new RuntimeException(baseType.getClass().getSimpleName());
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
        private final Set<Type> seen = new HashSet<>();

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
        public Void visit(NeverType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

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
        public Void visit(SymbolType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

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

        void acceptTypeTree(SpecReader.TypeNameTree value) {
            if (value instanceof SpecReader.Leaf) {
                ((SpecReader.Leaf) value).getType().accept(this);
            } else if (value instanceof SpecReader.Node) {
                ((SpecReader.Node) value).getChildren().values().forEach(this::acceptTypeTree);
            }
        }
    }

    public Pair<InterfaceType, TypeContext> constructSyntheticInterfaceWithBaseTypes(InterfaceType inter, Map<Type, String> typeNames) {
        if (inter.getBaseTypes().isEmpty()) {
            return new Pair<>(inter, new TypeContext(bench));
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
            if (subType instanceof GenericType) {
                subType = ((GenericType) subType).toInterface();
            }
            if (subType instanceof ClassInstanceType) {
                subType = ((ClassType) ((ClassInstanceType) subType).getClassType()).getInstanceType();
            }
            Pair<InterfaceType, TypeContext> pair = constructSyntheticInterfaceWithBaseTypes((InterfaceType) subType, typeNames);
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
        return new Pair<>(result, new TypeContext(bench).append(newParameters));
    }


    public static Set<Type> findHasThisTypes(Type global) {
        Set<Type> allTypes = TypesUtil.collectAllTypes(global);

        MultiMap<Type, Type> reverseBaseTypeMap = new ArrayListMultiMap<>();

        for (Type type : allTypes) {
            if (type instanceof GenericType) {
                for (Type baseType : ((GenericType) type).getBaseTypes()) {
                    reverseBaseTypeMap.put(baseType, type);
                }
                reverseBaseTypeMap.put(((GenericType) type).toInterface(), type);
            } else if (type instanceof InterfaceType) {
                for (Type baseType : ((InterfaceType) type).getBaseTypes()) {
                    reverseBaseTypeMap.put(baseType, type);
                }
            } else if (type instanceof ClassType) {
                for (Type baseType : ((ClassType) type).getBaseTypes()) {
                    reverseBaseTypeMap.put(baseType, type);
                }
                reverseBaseTypeMap.put(((ClassType) type).getInstanceType(), type);
            } else if (type instanceof ReferenceType) {
                reverseBaseTypeMap.put(((ReferenceType) type).getTarget(), type);
            } else if (type instanceof ClassInstanceType) {
                InterfaceType instanceType = ((ClassType) ((ClassInstanceType) type).getClassType()).getInstanceType();
                reverseBaseTypeMap.put(instanceType, type);
            }
        }

        Set<Type> result = new HashSet<>();

        List<Type> addQueue = allTypes.stream().filter(ThisType.class::isInstance).map(type -> ((ThisType)type).getConstraint()).collect(Collectors.toList());

        while (!addQueue.isEmpty()) {
            List<Type> copy = new ArrayList<>(addQueue);
            addQueue.clear();
            for (Type type : copy) {
                if (result.contains(type)) {
                    continue;
                }
                if (type instanceof ClassInstanceType) {
                    addQueue.add(((ClassInstanceType) type).getClassType());
                } else if (type instanceof ReferenceType) {
                    addQueue.add(((ReferenceType) type).getTarget());
                } else if (type instanceof ClassType) {
                    addQueue.add(((ClassType) type).getInstanceType());
                } else if (type instanceof GenericType) {
                    addQueue.add(((GenericType) type).toInterface());
                }
                result.add(type);
                addQueue.addAll(reverseBaseTypeMap.get(type));
            }
        }


        return result;
    }

}
