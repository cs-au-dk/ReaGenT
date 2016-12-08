package dk.webbies.tajscheck;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.buildprogram.TestProgramBuilder;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 01-11-2016.
 */
public class TypesUtil {
    public static InterfaceType classToInterface(ClassType t) {
        InterfaceType interfaceType = SpecReader.makeEmptySyntheticInterfaceType();

        for (Signature signature : t.getSignatures()) {
            Signature constructor = new Signature();
            constructor.setHasRestParameter(signature.isHasRestParameter());
            constructor.setIsolatedSignatureType(signature.getIsolatedSignatureType());
            constructor.setMinArgumentCount(signature.getMinArgumentCount());
            constructor.setParameters(signature.getParameters());
            constructor.setTarget(signature.getTarget());
            constructor.setTypeParameters(signature.getTypeParameters());
            constructor.setUnionSignatures(signature.getUnionSignatures());
            constructor.setResolvedReturnType(t.getInstanceType());
        }

        interfaceType.setBaseTypes(
                t.getBaseTypes().stream().map(base -> {
                    if (base instanceof ClassType) {
                        return classToInterface((ClassType) base);
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

        return interfaceType;
    }

    public static TypeContext generateParameterMap(ReferenceType ref) {
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
        return new TypeContext().append(parameterMap);
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

    public static TypeContext generateParameterMap(ReferenceType type, TypeContext typeContext) {
        return typeContext.append(generateParameterMap(type).getMap());
    }

    static Set<Type> collectNativeTypes(SpecReader spec, SpecReader emptySpec) {
        CollectAllTypesVisitor nativeCollector = new CollectAllTypesVisitor();
        Map<String, Type> declaredProperties = ((InterfaceType) spec.getGlobal()).getDeclaredProperties();

        Set<String> nativeProperties = ((InterfaceType) emptySpec.getGlobal()).getDeclaredProperties().keySet();

        for (Map.Entry<String, Type> entry : declaredProperties.entrySet()) {
            if (!nativeProperties.contains(entry.getKey())) {
                continue;
            }
            entry.getValue().accept(nativeCollector);
        }

        Set<String> nativeTypes = ((SpecReader.Node) emptySpec.getNamedTypes()).getChildren().keySet();

        Map<String, SpecReader.TypeNameTree> children = ((SpecReader.Node) spec.getNamedTypes()).getChildren();
        for (Map.Entry<String, SpecReader.TypeNameTree> entry : children.entrySet()) {
            if (!nativeTypes.contains(entry.getKey())) {
                continue;
            }
            nativeCollector.acceptTypeTree(entry.getValue());
        }

        return nativeCollector.getSeen();
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

        Type type = typeContext.get(firstType);

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
            type = typeContext.get((TypeParameterType)type);
        }

        return new ArrayList<>();
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
            throw new RuntimeException();
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
            sig.getResolvedReturnType().accept(this);
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
}
