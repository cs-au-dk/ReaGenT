package dk.webbies.tajscheck;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.buildprogram.TestProgramBuilder;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 01-11-2016.
 */
public class TypesUtil {
    public static ParameterMap generateParameterMap(ReferenceType ref) {
        GenericType target = (GenericType) ref.getTarget();

        Map<TypeParameterType, Type> parameterMap = new HashMap<>();
        List<Type> arguments = ref.getTypeArguments();
        assert target.getTypeParameters().equals(target.getTypeArguments());
        assert target.getTarget() == target;
        List<TypeParameterType> parameters = Util.cast(TypeParameterType.class, target.getTypeParameters());
        parameterMap = new HashMap<>(parameterMap);
        for (int i = 0; i < arguments.size(); i++) {
            parameterMap.put(parameters.get(i), arguments.get(i));
        }
        return new ParameterMap(parameterMap);
    }

    public static ParameterMap generateParameterMap(ReferenceType type, ParameterMap parameterMap) {
        return parameterMap.append(generateParameterMap(type).getMap());
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

    public static ParameterMap filterParameterMap(ParameterMap parameterMap, Type type) {
        return filterParameterMap(parameterMap, Collections.singletonList(type));
    }

    public static ParameterMap filterParameterMap(ParameterMap parameterMap, Collection<Type> types) {
        CollectAllTypesVisitor visitor = new CollectAllTypesVisitor();
        types.forEach(visitor::accept);
        Set<Type> reachable = visitor.getSeen();

        List<TypeParameterType> reachableTypeParameters = reachable.stream().filter(TypeParameterType.class::isInstance).map(typeparameter -> (TypeParameterType)typeparameter).collect(Collectors.toList());

        List<TypeParameterType> keys = Util.intersection(reachableTypeParameters, parameterMap.keySet());

        keys.stream().map(parameterMap::get).forEach(visitor::accept);

        reachableTypeParameters = reachable.stream().filter(TypeParameterType.class::isInstance).map(typeparameter -> (TypeParameterType)typeparameter).collect(Collectors.toList());

        List<TypeParameterType> keys2 = Util.intersection(reachableTypeParameters, parameterMap.keySet());

        assert keys2.size() >= keys.size();

        if (keys2.size() > keys.size()) {
            List<Type> reachableTypes = new ArrayList<>();
            reachableTypes.addAll(types);
            reachableTypes.addAll(keys2);
            return filterParameterMap(parameterMap, reachableTypes);
        } else {
            assert new HashSet<>(keys).equals(new HashSet<>(keys2));
            Map<TypeParameterType, Type> map = keys.stream().collect(Collectors.toMap(Function.identity(), parameterMap::get));
            return new ParameterMap(map);
        }
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

    public static List<Type> findRecursiveDefinition(TypeParameterType firstType, ParameterMap parameterMap, TestProgramBuilder.TypeParameterIndexer typeParameterIndexer) {
        List<Type> constraints = new ArrayList<>();

        constraints.add(firstType.getConstraint());

        {
            String markerField = typeParameterIndexer.getMarkerField(firstType);
            InterfaceType markerConstraint = SpecReader.makeEmptySyntheticInterfaceType();
            markerConstraint.getDeclaredProperties().put(markerField, new BooleanLiteral(true));
            constraints.add(markerConstraint);
        }

        Type type = parameterMap.get(firstType);

        Set<Type> seen = new HashSet<>();

        while (type instanceof TypeParameterType && parameterMap.containsKey((TypeParameterType)type)) {
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
            type = parameterMap.get((TypeParameterType)type);
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
            throw new RuntimeException();
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
        public Void visit(UnresolvedType t) {
            throw new RuntimeException();
        }

        @Override
        public Void visit(TypeParameterType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            t.getConstraint().accept(this);
            if (t.getTarget() != null) {
                t.getTarget().accept(this);
            }
            return null;
        }

        @Override
        public Void visit(SymbolType t) {
            throw new RuntimeException();
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
