package dk.webbies.tajscheck.typeutil;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.typeutil.typeContext.OptimizingTypeContext;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by erik1 on 01-11-2016.
 */
public class TypesUtil {

    private BenchmarkInfo info;

    public TypesUtil(BenchmarkInfo info) {
        this.info = info;
    }

    private final Map<ClassType, Pair<InterfaceType, Map<TypeParameterType, Type>>> classToInterfaceCache = new HashMap<>();

    public static ClassType emptyClassType() {
        ClassType clazz = new ClassType();
        clazz.setConstructors(new ArrayList<>());
        clazz.setCallSignatures(new ArrayList<>());
        clazz.setBaseTypes(new ArrayList<>());
        clazz.setStaticProperties(new HashMap<>());
        clazz.setInstanceProperties(new HashMap<>());
        clazz.setTypeParameters(new ArrayList<>());
        clazz.setTypeArguments(new ArrayList<>());
        clazz.setStaticReadonlyProperties(new ArrayList<>());
        clazz.setInstanceReadOnlyProperties(new ArrayList<>());
        clazz.setTarget(clazz);
        return clazz;
    }

    public Pair<InterfaceType, Map<TypeParameterType, Type>> classToInterface(ClassType t) {
        if (classToInterfaceCache.containsKey(t)) {
            return classToInterfaceCache.get(t);
        }
        InterfaceType interfaceType = SpecReader.makeEmptySyntheticInterfaceType();
        info.typeNames.put(interfaceType, info.typeNames.get(t));
        Map<TypeParameterType, Type> extraParams = new HashMap<>();


        for (Signature signature : t.getConstructors()) {
            Signature constructor = createConstructorSignature(t, signature);
            interfaceType.getDeclaredConstructSignatures().add(constructor);
        }
        if (t.getCallSignatures() != null) {
            interfaceType.getDeclaredCallSignatures().addAll(t.getCallSignatures());
        }
        if (t.getConstructors().isEmpty()) {
            Type baseType = t;
            while (true) { // to allow for finding signatures high up in the class hierarchy.
                if (baseType instanceof ReferenceType) {
                    extraParams.putAll(generateParameterMap((ReferenceType) baseType));
                    baseType = ((ReferenceType) baseType).getTarget();
                }
                List<Signature> signatures;
                List<Type> baseTypes;
                if (baseType instanceof ClassType) {
                    ClassType classBase = (ClassType) baseType;
                    signatures = classBase.getConstructors();
                    baseTypes = classBase.getBaseTypes();
                } else {
                    assert baseType instanceof InterfaceType;
                    InterfaceType inter = (InterfaceType) baseType;
                    signatures = inter.getDeclaredConstructSignatures();
                    baseTypes = inter.getBaseTypes();
                }
                if (!signatures.isEmpty()) {
                    interfaceType.setDeclaredConstructSignatures(signatures.stream().map(sig -> createConstructorSignature(t, sig)).collect(Collectors.toList()));
                    break;
                }
                baseTypes = baseTypes.stream().filter(base -> base instanceof ClassType || (base instanceof ReferenceType && ((ReferenceType) base).getTarget() instanceof ClassType)).collect(Collectors.toList());
                if (baseTypes.size() != 1) {
                    interfaceType.getDeclaredConstructSignatures().add(createConstructorSignature(t, emptySignature()));
                    break;
                } else {
                    baseType = baseTypes.get(0);
                }
            }
        }

        interfaceType.setBaseTypes(
                t.getBaseTypes().stream().map(base -> {
                    if (base instanceof ClassType) {
                        Pair<InterfaceType, Map<TypeParameterType, Type>> pair = classToInterface((ClassType) base);
                        if (pair.getRight().isEmpty()) {
                            return pair.getLeft();
                        } else {
                            throw new RuntimeException();
                        }
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

        if (info.freeGenericsFinder.hasThisTypes(t)) {
            info.freeGenericsFinder.addHasThisTypes(interfaceType);
        }

        Pair<InterfaceType, Map<TypeParameterType, Type>> result = new Pair<>(interfaceType, extraParams);
        classToInterfaceCache.put(t, result);

        return result;
    }

    public Map<TypeParameterType, Type> baseTypeParameters(Type t) {
        Map<TypeParameterType, Type> result = new HashMap<>();

        getAllBaseTypes(t).stream().filter(ReferenceType.class::isInstance).map(ReferenceType.class::cast).map(this::generateParameterMap).forEach(map -> {
            for (Map.Entry<TypeParameterType, Type> entry : map.entrySet()) {
                assert !result.containsKey(entry.getKey());
                result.put(entry.getKey(), entry.getValue());
            }
        });

        return result;
    }

    private final Map<ClassType, InterfaceType> classInstanceCache = new HashMap<>();
    public InterfaceType createClassInstanceType(ClassType type) {
        if (classInstanceCache.containsKey(type)) {
            return classInstanceCache.get(type);
        }
        InterfaceType instanceType = SpecReader.makeEmptySyntheticInterfaceType();
        info.typeNames.put(instanceType, info.typeNames.get(type) + ".[instance]");
        classInstanceCache.put(type, instanceType);
        instanceType.setDeclaredNumberIndexType(type.getDeclaredNumberIndexType());
        instanceType.setDeclaredStringIndexType(type.getDeclaredStringIndexType());
        instanceType.setDeclaredProperties(type.getInstanceProperties());
        instanceType.setTypeParameters(type.getTypeParameters());
        instanceType.setReadonlyDeclarations(type.getInstanceReadOnlyProperties());

        instanceType.setBaseTypes(type.getBaseTypes().stream().map(baseType -> {
            if (baseType instanceof DelayedType) {
                baseType = ((DelayedType) baseType).getType();
            }
            if (baseType instanceof ReferenceType) {
                if (((ReferenceType) baseType).getTarget() instanceof DelayedType) {
                    ((ReferenceType) baseType).setTarget(((DelayedType) ((ReferenceType) baseType).getTarget()).getType());
                }
            }
            if (baseType instanceof ClassType) {
                return this.createClassInstanceType(((ClassType) baseType));
            } else if (baseType instanceof ReferenceType && ((ReferenceType) baseType).getTarget() instanceof ClassType) {
                ReferenceType result = new ReferenceType();
                result.setTypeArguments(((ReferenceType) baseType).getTypeArguments());
                result.setTarget(this.createClassInstanceType(((ClassType) ((ReferenceType) baseType).getTarget())));
                return result;
            } else {
                return baseType;
            }
        }).collect(Collectors.toList()));

        return instanceType;
    }

    public static InterfaceType signaturesToInterface(List<Signature> signatures, Map<Type, String> typeNames) {
        if (signatures.isEmpty()) {
            throw new RuntimeException();
        }
        InterfaceType result = SpecReader.makeEmptySyntheticInterfaceType();
        ArrayList<Signature> clonedSignatures = new ArrayList<>(signatures);
        if (clonedSignatures.size() == 1) {
            // To force it into being a type-overloaded signature
            Signature neverSignature = emptySignature();
            neverSignature.setMinArgumentCount(1);
            Signature.Parameter parameter = new Signature.Parameter();
            parameter.setName("x");
            parameter.setType(new SimpleType(SimpleTypeKind.Never));
            neverSignature.setParameters(Collections.singletonList(parameter));
            clonedSignatures.add(neverSignature);
        }
        result.setDeclaredCallSignatures(clonedSignatures);
        typeNames.put(result, "mockFunctionForFirstMatchPolicy");
        return result;
    }

    public static Signature emptySignature() {
        Signature signature = new Signature();
        signature.setHasRestParameter(false);
        signature.setIsolatedSignatureType(null);
        signature.setMinArgumentCount(0);
        signature.setParameters(new ArrayList<>());
        signature.setTarget(null);
        signature.setTypeParameters(new ArrayList<>());
        signature.setUnionSignatures(new ArrayList<>());
        signature.setResolvedReturnType(new SimpleType(SimpleTypeKind.Any));
        return signature;
    }

    public Signature createConstructorSignature(ClassType t, Signature signature) {
        Signature constructor = new Signature();
        constructor.setHasRestParameter(signature.isHasRestParameter());
        constructor.setIsolatedSignatureType(signature.getIsolatedSignatureType());
        constructor.setMinArgumentCount(signature.getMinArgumentCount());
        constructor.setParameters(signature.getParameters());
        constructor.setTarget(signature.getTarget());
        constructor.setTypeParameters(signature.getTypeParameters());
        constructor.setUnionSignatures(signature.getUnionSignatures());
        constructor.setResolvedReturnType(this.createClassInstanceType(t));
        return constructor;
    }

    public Map<TypeParameterType, Type> generateParameterMap(ReferenceType ref) {
        List<Type> arguments = ref.getTypeArguments();
        Map<TypeParameterType, Type> parameterMap = new HashMap<>();

        List<Type> typeParameters = getTypeParameters(ref.getTarget());
        assert typeParameters.size() <= arguments.size();
        List<TypeParameterType> parameters = Util.cast(TypeParameterType.class, typeParameters);
        parameterMap = new HashMap<>(parameterMap);
        for (int i = 0; i < parameters.size(); i++) {
            parameterMap.put(parameters.get(i), arguments.get(i));
        }
        return parameterMap;
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
        } else if (target instanceof ReferenceType) {
            assert ((ReferenceType) target).getTypeArguments().stream().allMatch(TypeParameterType.class::isInstance);
            return ((ReferenceType) target).getTypeArguments();
        } else {
            throw new RuntimeException(target.getClass().getName());
        }
    }

    public TypeContext generateParameterMap(ReferenceType type, TypeContext typeContext) {
        return typeContext.append(generateParameterMap(type));
    }

    public static Set<Type> collectAllTypes(Collection<Type> types, BenchmarkInfo info) {
        CollectAllTypesVisitor visitor = new CollectAllTypesVisitor(info);
        for (Type type : types) {
            visitor.accept(type);
        }
        return visitor.getSeen();
    }

    public static Set<Type> collectAllTypes(Type type, BenchmarkInfo info) {
        return collectAllTypes(Collections.singletonList(type), info);
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
            Type nativeType = inverseNativeNameMap.get(entry.getValue());
            if (nativeType == null) {
                return false;
            }
            Type type = entry.getKey();
            return pseudoEquals(nativeType, type);
        }).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    private static boolean pseudoEquals(Type nativeType, Type type) {
        if (!type.getClass().equals(nativeType.getClass())) {
            return false;
        }
        if (nativeType instanceof ReferenceType) {
            nativeType = ((ReferenceType) nativeType).getTarget();
            type = ((ReferenceType) type).getTarget();
        }
        if (nativeType instanceof GenericType) {
            nativeType = ((GenericType) nativeType).toInterface();
            type = ((GenericType) type).toInterface();
        }
        if (nativeType instanceof InterfaceType) {
            Set<String> nativeProps = ((InterfaceType) nativeType).getDeclaredProperties().keySet();
            Set<String> typeProps = ((InterfaceType) type).getDeclaredProperties().keySet();
            return Util.intersection(nativeProps, typeProps).size() == Math.min(nativeProps.size(), typeProps.size());
        }
        if (nativeType instanceof IndexedAccessType || nativeType instanceof TypeParameterType || nativeType instanceof ThisType) {
            return true;
        }
        if (nativeType instanceof UnionType) {
            return ((UnionType) nativeType).getElements().size() == ((UnionType) type).getElements().size();
        }
        if (nativeType instanceof TupleType) {
            return ((TupleType) nativeType).getElementTypes().size() == ((TupleType) type).getElementTypes().size();
        }
        if (nativeType instanceof IntersectionType) {
            return ((IntersectionType) nativeType).getElements().size() == ((IntersectionType) type).getElements().size();
        }
        if (nativeType instanceof ClassType) {
            Map<String, Type> nativeStatic = ((ClassType) nativeType).getStaticProperties();
            Map<String, Type> typeStatic = ((ClassType) type).getStaticProperties();

            Map<String, Type> nativeInstance = ((ClassType) nativeType).getInstanceProperties();
            Map<String, Type> typeInstance = ((ClassType) type).getInstanceProperties();
            return Util.intersection(nativeStatic.keySet(), typeStatic.keySet()).size() == Math.min(nativeStatic.size(), typeStatic.size()) &&
                    Util.intersection(nativeInstance.keySet(), typeInstance.keySet()).size() == Math.min(nativeInstance.size(), typeInstance.size());
        }
        if (nativeType instanceof ClassInstanceType) {
            return pseudoEquals(((ClassInstanceType) nativeType).getClassType(), ((ClassInstanceType) type).getClassType());
        }
        throw new RuntimeException(type.getClass().getSimpleName());
    }

    public static boolean isEmptyInterface(Type type) {
        if (!(type instanceof InterfaceType)) {
            return false;
        }
        InterfaceType inter = (InterfaceType) type;
        return inter.getDeclaredProperties().isEmpty() &&
                inter.getBaseTypes().isEmpty() &&
                inter.getTypeParameters().isEmpty() &&
                inter.getDeclaredCallSignatures().isEmpty() &&
                inter.getDeclaredConstructSignatures().isEmpty() &&
                inter.getDeclaredStringIndexType() == null &&
                inter.getDeclaredNumberIndexType() == null;
    }

    public List<Signature> splitOptionalSignatures(List<Signature> signatures, boolean isTypeScript) {
        Stream<Signature> stream = signatures.stream();
        if (isTypeScript) {
            stream = stream.map(TypesUtil::makeSureOptionalArgumentsHaveUnionUndef);
        }
        List<Signature> result = stream.map(this::splitOptionalSignature).reduce(addOptionalToUndefArguments(signatures, isTypeScript), Util::reduceList);
        return result;
    }

    private List<Signature> addOptionalToUndefArguments(List<Signature> signatures, boolean isTypeScript) {
        return signatures.stream()
                .map(signature -> {
                    signature = cloneSignature(signature);
                    List<Signature> result = new ArrayList<>();
                    if (signature.isHasRestParameter()) {
                        signature = cloneSignature(signature);
                        signature.getParameters().remove(signature.getParameters().size() - 1);
                        signature.setHasRestParameter(false);
                    }
                    for (int i = signature.getMinArgumentCount(); i < signature.getParameters().size(); i++) {
                        Type type = signature.getParameters().get(i).getType();
                        if (type instanceof UnionType && ((UnionType) type).getElements().stream().anyMatch(element -> element instanceof SimpleType && ((SimpleType) element).getKind() == SimpleTypeKind.Undefined)) {
                            List<Type> elements = ((UnionType) type).getElements().stream().filter(element -> !(element instanceof SimpleType && ((SimpleType) element).getKind() == SimpleTypeKind.Undefined)).collect(Collectors.toList());
                            Type newType;
                            assert !elements.isEmpty();
                            if (elements.size() == 1) {
                                newType = elements.get(0);
                            } else {
                                UnionType newUnion = new UnionType();
                                info.typeNames.put(newUnion, info.typeNames.get(type));
                                newUnion.setElements(elements);
                                newType = newUnion;
                            }
                            signature.getParameters().get(i).setType(newType);
                        }
                    }
                    int minArgs = signature.getMinArgumentCount();
                    signature.setMinArgumentCount(signature.getParameters().size());
                    if (isTypeScript) {
                        for (int i = signature.getParameters().size() - 1; i >= minArgs; i--) {
                            signature = cloneSignature(signature);
                            signature.getParameters().get(i).setType(new SimpleType(SimpleTypeKind.Undefined));
                            result.add(signature);
                        }
                    }

                    return result;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static Signature makeSureOptionalArgumentsHaveUnionUndef(Signature signature) {
        signature = cloneSignature(signature);
        int end = signature.getParameters().size();
        if (signature.isHasRestParameter()) {
            end--;
        }
        for (int i = signature.getMinArgumentCount(); i < end; i++) {
            Signature.Parameter parameter = signature.getParameters().get(i);
            Type type = parameter.getType();
            if (!(type instanceof UnionType)) {
                UnionType union = new UnionType();
                union.setElements(Arrays.asList(type, new SimpleType(SimpleTypeKind.Undefined)));
                parameter.setType(union);
            } else {
                UnionType union = (UnionType) type;
                boolean hasUndef = union.getElements().stream().anyMatch(sub -> sub instanceof SimpleType && ((SimpleType) sub).getKind() == SimpleTypeKind.Undefined);
                if (!hasUndef) {
                    UnionType newUnion = new UnionType();
                    parameter.setType(newUnion);
                    newUnion.setElements(Util.concat(union.getElements(), Collections.singletonList(new SimpleType(SimpleTypeKind.Undefined))));
                }
            }
        }
        return signature;
    }

    private List<Signature> splitOptionalSignature(Signature signature) {
        if (signature.getMinArgumentCount() == signature.getParameters().size()) {
            return Collections.singletonList(signature);
        }

        if (signature.isHasRestParameter() && signature.getMinArgumentCount() + 1 == signature.getParameters().size()) {
            return Collections.singletonList(signature);
        }

        List<Signature> result = new ArrayList<>();

        Signature withNoOptional = cloneSignature(signature);
        withNoOptional.setHasRestParameter(false);
        result.add(withNoOptional);
        for (int i = signature.getParameters().size() - 1; i >= signature.getMinArgumentCount(); i--) {
            withNoOptional.getParameters().remove(i);
        }

        assert withNoOptional.getParameters().size() == withNoOptional.getMinArgumentCount();

        Signature withOptional = cloneSignature(signature);

        Signature.Parameter optionalParameter = withOptional.getParameters().get(signature.getMinArgumentCount());

        if (optionalParameter.getType() instanceof UnionType) {
            optionalParameter.setType(removeUndef((UnionType) optionalParameter.getType()));
        }

        withOptional.setMinArgumentCount(withOptional.getMinArgumentCount() + 1);

        result.addAll(splitOptionalSignature(withOptional));

        return result;
    }

    private Type removeUndef(UnionType union) {
        if (union.getElements().stream().noneMatch(sub -> sub instanceof SimpleType && ((SimpleType) sub).getKind() == SimpleTypeKind.Undefined)) {
            return union;
        }

        ArrayList<Type> elements = new ArrayList<>(union.getElements());
        String name = info.typeNames.get(union);
        union = new UnionType();
        info.typeNames.put(union, name);
        union.setElements(elements);

        union.setElements(union.getElements().stream().filter(sub -> !(sub instanceof SimpleType && ((SimpleType) sub).getKind() == SimpleTypeKind.Undefined)).collect(Collectors.toList()));

        if (union.getElements().size() == 1) {
            return union.getElements().iterator().next();
        }
        if (union.getElements().isEmpty() && elements.size() >= 2) {
            return new SimpleType(SimpleTypeKind.Undefined);
        }

        assert !union.getElements().isEmpty();

        return union;
    }

    public static List<Signature> splitUnionsInSignatures(List<Signature> signatures) {
        return signatures.stream().map(TypesUtil::splitUnionsInSignature).reduce(new ArrayList<>(), Util::reduceList);
    }

    private static List<Signature> splitUnionsInSignature(Signature signature) {
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

                    result.addAll(splitUnionsInSignature(subSignature));
                }
                return result;
            }
        }
        return Collections.singletonList(signature);
    }

    public static Signature cloneSignature(Signature signature) {
        Signature result = new Signature();
        result.setResolvedReturnType(signature.getResolvedReturnType());
        result.setUnionSignatures(signature.getUnionSignatures());
        result.setTarget(signature.getTarget());
        result.setParameters(signature.getParameters().stream().map(TypesUtil::cloneParameter).collect(Collectors.toList()));
        result.setHasRestParameter(signature.isHasRestParameter());
        result.setIsolatedSignatureType(signature.getIsolatedSignatureType());
        result.setMinArgumentCount(signature.getMinArgumentCount());
        result.setTypeParameters(signature.getTypeParameters());
        return result;
    }

    private static Signature.Parameter cloneParameter(Signature.Parameter parameter) {
        Signature.Parameter newParameter = new Signature.Parameter();
        newParameter.setType(parameter.getType());
        newParameter.setName(parameter.getName());
        return newParameter;
    }

    public static List<Signature> removeDuplicateSignatures(List<Signature> signatures) {
        return signatures.stream().map(SignatureComparisonContainer::new).distinct().map(SignatureComparisonContainer::getSignature).collect(Collectors.toList());
    }

    public Type getNativeBase(Type type, Predicate<Type> predicate) {
        Predicate<Type> isNativeType = t -> {
            if (TypesUtil.isEmptyInterface(t)) {
                return false;
            }
            return predicate.test(t);
        };
        List<Type> nativeBaseTypes = getAllBaseTypes(type, new HashSet<>(), Util.not(isNativeType)).stream().filter(isNativeType).collect(Collectors.toList());

        if (nativeBaseTypes.size() == 0) {
            return null;
        } else if (nativeBaseTypes.size() == 1) {
            return nativeBaseTypes.iterator().next();
        }
        return null; // Hard to do better.
    }


    public Set<Type> getAllBaseTypes(Type type) {
        return getAllBaseTypes(type, new HashSet<>());
    }

    public Set<Type> getAllBaseTypes(Type type, Set<Type> acc) {
        return getAllBaseTypes(type, acc, (subType) -> true);
    }

    public Type normalize(Type type) {
        if (type instanceof ClassInstanceType) {
            return normalize(this.createClassInstanceType(((ClassType) ((ClassInstanceType) type).getClassType())));
        }
        if (type instanceof GenericType) {
            return normalize(((GenericType) type).toInterface());
        }
        if (type instanceof ReferenceType) {
            return normalize(((ReferenceType) type).getTarget());
        }
        return type;
    }

    private Set<Type> getAllBaseTypes(Type type, Set<Type> acc, Predicate<Type> shouldContinue) {
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
            type = this.createClassInstanceType(((ClassType) ((ClassInstanceType) type).getClassType()));
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
            Type instanceType = this.createClassInstanceType(((ClassType) ((ClassInstanceType) t).getClassType()));
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
            Type instanceType = this.createClassInstanceType(((ClassType) ((ClassInstanceType) t).getClassType()));
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
            Type instanceType = this.createClassInstanceType(((ClassType) ((ClassInstanceType) t).getClassType()));
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


    private static class CollectAllTypesVisitor extends RecursiveTypeVisitor<Void> {

        private CollectAllTypesVisitor(BenchmarkInfo info) {
            super(info);
        }

        Set<Type> getSeen() {
            return seen;
        }
    }

    public Pair<InterfaceType, Map<TypeParameterType, Type>> constructSyntheticInterfaceWithBaseTypes(InterfaceType inter) {
        return constructSyntheticInterfaceWithBaseTypes(inter, Collections.emptySet());
    }

    public Pair<InterfaceType, Map<TypeParameterType, Type>> constructSyntheticInterfaceWithBaseTypes(InterfaceType inter, Set<Type> baseTypesToIgnore) {
        if (inter.getBaseTypes().isEmpty()) {
            return new Pair<>(inter, Collections.emptyMap());
        }
//        assert inter.getTypeParameters().isEmpty(); // This should only happen when constructed from a generic/reference type, and in that case we have handled the TypeParameters.
        Map<TypeParameterType, Type> newParameters = new HashMap<>();
        InterfaceType result = SpecReader.makeEmptySyntheticInterfaceType();

        result.getDeclaredCallSignatures().addAll(inter.getDeclaredCallSignatures());
        result.getDeclaredConstructSignatures().addAll(inter.getDeclaredConstructSignatures());
        result.setDeclaredNumberIndexType(inter.getDeclaredNumberIndexType());
        result.setDeclaredStringIndexType(inter.getDeclaredStringIndexType());
        result.getDeclaredProperties().putAll(inter.getDeclaredProperties());

        info.typeNames.put(result, info.typeNames.get(inter));
        inter.getBaseTypes().forEach(subType -> {
            if (baseTypesToIgnore.contains(subType)) {
                return;
            }
            if (subType instanceof ReferenceType) {
                newParameters.putAll(generateParameterMap((ReferenceType) subType));
                subType = ((ReferenceType) subType).getTarget();
            }
            if (subType instanceof ClassType) {
                Pair<InterfaceType, Map<TypeParameterType, Type>> pair = info.typesUtil.classToInterface((ClassType) subType);
                newParameters.putAll(pair.getRight());
                subType = pair.getLeft();
            }
            if (subType instanceof GenericType) {
                subType = ((GenericType) subType).toInterface();
            }
            if (subType instanceof ClassInstanceType) {
                subType = this.createClassInstanceType(((ClassType) ((ClassInstanceType) subType).getClassType()));
            }
            Pair<InterfaceType, Map<TypeParameterType, Type>> pair = constructSyntheticInterfaceWithBaseTypes((InterfaceType) subType, baseTypesToIgnore);
            newParameters.putAll(pair.getRight());
            InterfaceType type = pair.getLeft();
            result.getDeclaredCallSignatures().addAll((type.getDeclaredCallSignatures()));
            result.getDeclaredConstructSignatures().addAll(type.getDeclaredConstructSignatures());
            if (result.getDeclaredNumberIndexType() == null) {
                result.setDeclaredNumberIndexType(type.getDeclaredNumberIndexType());
            }
            if (result.getDeclaredStringIndexType() == null) {
                result.setDeclaredStringIndexType(type.getDeclaredStringIndexType());
            }
            result.getDeclaredProperties().putAll(type.getDeclaredProperties());
            for (Map.Entry<String, Type> entry : type.getDeclaredProperties().entrySet()) {
                if (result.getDeclaredProperties().containsKey(entry.getKey())) {
                    continue;
                }
                result.getDeclaredProperties().put(entry.getKey(), entry.getValue());
            }
        });
        return new Pair<>(result, newParameters);
    }

    public void forAllSuperTypes(Type type, TypeContext typeContext, Consumer<TypeWithContext> callback) {
        forAllSuperTypes(type, typeContext, new HashSet<>(), callback);
    }

    private void forAllSuperTypes(Type type, TypeContext typeContext, Set<TypeWithContext> seen, Consumer<TypeWithContext> callback) {
        TypeWithContext seenKey = new TypeWithContext(type, typeContext);
        if (seen.contains(seenKey)) {
            return;
        }
        seen.add(seenKey);

        callback.accept(seenKey);

        if (typeContext.getThisType() != null) {
            forAllSuperTypes(type, typeContext.withThisType(null), seen, callback);
        }
        if (typeContext.getThisType() == null) {
            if (info.freeGenericsFinder.hasThisTypes(type) && !(type instanceof ClassType)) {
                forAllSuperTypes(type, typeContext.withThisType(type), seen, callback);
            }
        }

        {
            TypeContext newContext = typeContext.optimizeTypeParameters(type);
            if (!newContext.equals(typeContext)) {
                forAllSuperTypes(type, newContext, seen, callback);
            }
        }


        forAllSuperTypes(type, typeContext.append(baseTypeParameters(type)), seen, callback);

        if (type instanceof InterfaceType) {
            List<Type> baseTypes = ((InterfaceType) type).getBaseTypes();
            baseTypes.forEach(baseType -> forAllSuperTypes(baseType, typeContext, seen, callback));
        } else if (type instanceof IntersectionType) {
            List<Type> baseTypes = ((IntersectionType) type).getElements();
            baseTypes.forEach(baseType -> forAllSuperTypes(baseType, typeContext, seen, callback));
        } else if (type instanceof ReferenceType) {
            forAllSuperTypes(((ReferenceType) type).getTarget(), this.generateParameterMap((ReferenceType) type, typeContext), seen, callback);
        } else if (type instanceof GenericType) {
            forAllSuperTypes(((GenericType) type).toInterface(), typeContext, seen, callback);
        } else if (type instanceof ClassType) {
            ClassType classType = (ClassType) type;
            Pair<InterfaceType, Map<TypeParameterType, Type>> pair = info.typesUtil.classToInterface(classType);
            TypeContext newContext = typeContext.withThisType(this.createClassInstanceType(classType));

            forAllSuperTypes(pair.getLeft(), newContext, seen, callback);
            forAllSuperTypes(type, newContext, seen, callback);

            newContext = newContext.append(pair.getRight());

            forAllSuperTypes(pair.getLeft(), newContext, seen, callback);
            forAllSuperTypes(type, newContext, seen, callback);
        } else if (type instanceof ClassInstanceType) {
            ClassInstanceType instanceType = (ClassInstanceType) type;

            ClassType classType = (ClassType) instanceType.getClassType();

            Map<TypeParameterType, Type> extraParams = info.typesUtil.classToInterface(classType).getRight();
            forAllSuperTypes(type, typeContext.append(extraParams), seen, callback);

            //noinspection AssertWithSideEffects
            assert instanceType == classType.getInstance();

            {
                Map<TypeParameterType, Type> map = new HashMap<>();
                for (int i = 0; i < Math.min(classType.getTypeParameters().size(), classType.getTypeArguments().size()); i++) {
                    map.put((TypeParameterType) classType.getTypeParameters().get(i), classType.getTypeArguments().get(i));
                }
                map.keySet().removeAll(typeContext.getMap().keySet());
                TypeContext newContext = typeContext.append(map);
                if (!newContext.optimizeTypeParameters(instanceType).equals(typeContext)) {
                    forAllSuperTypes(instanceType, newContext, seen, callback);
                }
            }

            forAllSuperTypes(this.createClassInstanceType(classType), typeContext, seen, callback);
            if (info.freeGenericsFinder.hasThisTypes(instanceType.getClassType())) {
                forAllSuperTypes(type, typeContext.withThisType(instanceType), seen, callback);
                forAllSuperTypes(type, typeContext.withThisType(this.createClassInstanceType(classType)), seen, callback);
            }

            for (Type baseClass : classType.getBaseTypes()) {
                TypeContext subTypeContext = typeContext;
                if (baseClass instanceof ReferenceType) {
                    subTypeContext = this.generateParameterMap((ReferenceType) baseClass, typeContext);
                    baseClass = ((ReferenceType) baseClass).getTarget();
                } else if (baseClass instanceof ClassInstanceType) {
                    baseClass = ((ClassInstanceType) baseClass).getClassType();
                }
                if (baseClass instanceof ClassType) {
                    baseClass = ((ClassType) baseClass).getInstance();
                } else if (!(baseClass instanceof InterfaceType || baseClass instanceof GenericType || baseClass instanceof ClassInstanceType)) {
                    throw new RuntimeException("Not sure about: " + baseClass.getClass().getSimpleName());
                }
                forAllSuperTypes(baseClass, subTypeContext, seen, callback);
            }

        } else if (type instanceof ThisType) {
            Type thisType = typeContext.getThisType();
            forAllSuperTypes(thisType != null ? thisType : ((ThisType) type).getConstraint(), typeContext, seen, callback);
        } else if (type instanceof TypeParameterType) {
            if (typeContext.get((TypeParameterType) type) != null) {
                TypeWithContext lookup = typeContext.get((TypeParameterType) type);
                forAllSuperTypes(lookup.getType(), lookup.getTypeContext(), seen, callback);
            } else {
                // Do nothing
            }
            if (((TypeParameterType) type).getConstraint() != null) {
                forAllSuperTypes(((TypeParameterType) type).getConstraint(), typeContext, callback);
            }
        } else if (type instanceof SimpleType || type instanceof NumberLiteral || type instanceof StringLiteral || type instanceof BooleanLiteral || type instanceof UnionType || type instanceof TupleType || type instanceof IndexedAccessType) {
            // Do nothing.
        } else {
            throw new RuntimeException(type.getClass().getName());
        }
    }

    public static Type extractRestArgsType(List<Type> orgParameterTypes) {
        Type restArgArr = orgParameterTypes.get(orgParameterTypes.size() - 1);
        assert restArgArr instanceof ReferenceType;
        assert ((ReferenceType) restArgArr).getTypeArguments().size() == 1;

        return ((ReferenceType) restArgArr).getTypeArguments().iterator().next();
    }
}
