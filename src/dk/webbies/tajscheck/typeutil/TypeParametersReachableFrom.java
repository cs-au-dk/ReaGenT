package dk.webbies.tajscheck.typeutil;

import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.util.HashSetMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Util;

import java.util.*;

/**
 * Created by erik1 on 04-01-2017.
 */
public class TypeParametersReachableFrom {
    public static MultiMap<Type, TypeParameterType> createMap(Type global) {
        List<Type> allTypes = new ArrayList<>(TypesUtil.collectAllTypes(global));
        Collections.shuffle(allTypes);

        MultiMap<Type, TypeParameterType> result = new HashSetMultiMap<>();

        for (Type type : allTypes) {
            type.accept(new FindReachableTypeParameters(type, result));
        }


        return result;
    }

    private static final class FindReachableTypeParameters implements TypeVisitor<Void> {
        final Set<Type> seen = new HashSet<>();
        private Type baseType;
        private MultiMap<Type, TypeParameterType> result;

        private FindReachableTypeParameters(Type baseType, MultiMap<Type, TypeParameterType> result) {
            this.baseType = baseType;
            this.result = result;
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
            if (result.containsKey(t)) {
                result.putAll(baseType, result.get(t));
                return null;
            }

            t.getInstanceType().accept(this);

            for (Signature signature : t.getSignatures()) {
                assert signature.getResolvedReturnType() == null;
                for (Signature.Parameter parameter : signature.getParameters()) {
                    parameter.getType().accept(this);
                }
            }

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
            if (result.containsKey(t)) {
                result.putAll(baseType, result.get(t));
                return null;
            }

            t.toInterface().accept(this);

            t.getTarget().accept(this);

            t.getTypeArguments().forEach(this::accept);

            return null;
        }

        @Override
        public Void visit(InterfaceType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (result.containsKey(t)) {
                result.putAll(baseType, result.get(t));
                return null;
            }


            t.getTypeParameters().forEach(this::accept);
            t.getBaseTypes().forEach(this::accept);
            t.getDeclaredProperties().values().forEach(this::accept);
            for (Signature signature : Util.concat(t.getDeclaredConstructSignatures(), t.getDeclaredCallSignatures())) {
                signature.getResolvedReturnType().accept(this);
                for (Signature.Parameter parameter : signature.getParameters()) {
                    parameter.getType().accept(this);
                }
            }

            if (t.getDeclaredNumberIndexType() != null) {
                t.getDeclaredNumberIndexType().accept(this);
            }
            if (t.getDeclaredStringIndexType() != null) {
                t.getDeclaredStringIndexType().accept(this);
            }

            return null;
        }

        private void accept(Type type) {
            type.accept(this);
        }

        @Override
        public Void visit(ReferenceType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (result.containsKey(t)) {
                result.putAll(baseType, result.get(t));
                return null;
            }

            t.getTarget().accept(this);

            t.getTypeArguments().forEach(this::accept);

            return null;
        }

        @Override
        public Void visit(SimpleType t) {
            return null;
        }

        @Override
        public Void visit(TupleType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (result.containsKey(t)) {
                result.putAll(baseType, result.get(t));
                return null;
            }

            t.getElementTypes().forEach(this::accept);

            return null;
        }

        @SuppressWarnings("Duplicates")
        @Override
        public Void visit(UnionType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (result.containsKey(t)) {
                result.putAll(baseType, result.get(t));
                return null;
            }

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
            if (result.containsKey(t)) {
                result.putAll(baseType, result.get(t));
                return null;
            }


            result.put(baseType, t);

            if (t.getConstraint() != null) {
                t.getConstraint().accept(this);
            }

            return null;
        }

        @Override
        public Void visit(SymbolType t) {
            return null;
        }

        @Override
        public Void visit(StringLiteral t) {
            return null;
        }

        @Override
        public Void visit(BooleanLiteral t) {
            return null;
        }

        @Override
        public Void visit(NumberLiteral t) {
            return null;
        }

        @SuppressWarnings("Duplicates")
        @Override
        public Void visit(IntersectionType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (result.containsKey(t)) {
                result.putAll(baseType, result.get(t));
                return null;
            }

            t.getElements().forEach(this::accept);

            return null;
        }

        @Override
        public Void visit(ClassInstanceType t) {
            t.getClassType().accept(this);

            return null;
        }

        @Override
        public Void visit(NeverType t) {
            return null;
        }

        @Override
        public Void visit(ThisType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (result.containsKey(t)) {
                result.putAll(baseType, result.get(t));
                return null;
            }

            t.getConstraint().accept(this);

            return null;
        }

        @Override
        public Void visit(IndexType t) {
            throw new RuntimeException();
        }

        @Override
        public Void visit(IndexedAccessType t) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (result.containsKey(t)) {
                result.putAll(baseType, result.get(t));
                return null;
            }

            t.getIndexType().accept(this);
            t.getObjectType().accept(this);

            return null;
        }
    }
}
