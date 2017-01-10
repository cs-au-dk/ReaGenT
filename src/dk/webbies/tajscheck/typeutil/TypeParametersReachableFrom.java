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
            type.accept(new FindReachableTypeParameters(type, result), Collections.emptySet());
        }


        return result;
    }

    // TODO: Use mapped, use it to make sure that nothing that is already mapped by a reference, is added to the set of visible types.
    // TODO: Maybe rename the resulting map to freeTypeParameters.

    // TODO: Combine thisTypesVisible (and cache it), and freeTypeParameters, into a single class that can answer the questions.
    private static final class FindReachableTypeParameters implements TypeVisitorWithArgument<Void, Set<TypeParameterType>> {
        final Set<Type> seen = new HashSet<>();
        private Type baseType;
        private MultiMap<Type, TypeParameterType> result;

        private FindReachableTypeParameters(Type baseType, MultiMap<Type, TypeParameterType> result) {
            this.baseType = baseType;
            this.result = result;
        }

        @Override
        public Void visit(AnonymousType t, Set<TypeParameterType> mapped) {
            return null;
        }

        @Override
        public Void visit(ClassType t, Set<TypeParameterType> mapped) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (addExisting(t, mapped)) return null;

            t.getInstanceType().accept(this, mapped);

            for (Signature signature : t.getSignatures()) {
                assert signature.getResolvedReturnType() == null;
                for (Signature.Parameter parameter : signature.getParameters()) {
                    parameter.getType().accept(this, mapped);
                }
            }

            t.getBaseTypes().forEach(base -> base.accept(this, mapped));
            t.getStaticProperties().values().forEach(prop -> prop.accept(this, mapped));
            t.getTarget().accept(this, mapped);
            t.getTypeArguments().forEach(arg -> arg.accept(this, mapped));

            return null;
        }

        @Override
        public Void visit(GenericType t, Set<TypeParameterType> mapped) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            if (addExisting(t, mapped)) return null;

            t.toInterface().accept(this, mapped);

            t.getTarget().accept(this, mapped);

            t.getTypeArguments().forEach(arg -> arg.accept(this, mapped));

            return null;
        }

        @Override
        public Void visit(InterfaceType t, Set<TypeParameterType> mapped) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (addExisting(t, mapped)) return null;

            t.getTypeParameters().forEach(par -> par.accept(this, mapped));
            t.getBaseTypes().forEach(base -> base.accept(this, mapped));
            t.getDeclaredProperties().values().forEach(prop -> prop.accept(this, mapped));
            for (Signature signature : Util.concat(t.getDeclaredConstructSignatures(), t.getDeclaredCallSignatures())) {
                signature.getResolvedReturnType().accept(this, mapped);
                signature.getParameters().forEach(par -> par.getType().accept(this, mapped));
            }

            if (t.getDeclaredNumberIndexType() != null) {
                t.getDeclaredNumberIndexType().accept(this, mapped);
            }
            if (t.getDeclaredStringIndexType() != null) {
                t.getDeclaredStringIndexType().accept(this, mapped);
            }

            return null;
        }

        private boolean addExisting(Type t, Set<TypeParameterType> mapped) {
            if (!result.containsKey(t)) {
                return false;
            }
            for (TypeParameterType parameterType : result.get(t)) {
                if (!mapped.contains(parameterType)) {
                    result.put(baseType, parameterType);
                }
            }

            return true;
        }

        @Override
        public Void visit(ReferenceType t, Set<TypeParameterType> orgMapped) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);

            Set<TypeParameterType> mapped = Util.concatSet(orgMapped, Util.cast(TypeParameterType.class, TypesUtil.getTypeParameters(t.getTarget())));

            if (addExisting(t, mapped)) return null;

            t.getTarget().accept(this, mapped);

            t.getTypeArguments().forEach(arg -> arg.accept(this, mapped));

            return null;
        }

        @Override
        public Void visit(SimpleType t, Set<TypeParameterType> mapped) {
            return null;
        }

        @Override
        public Void visit(TupleType t, Set<TypeParameterType> mapped) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (addExisting(t, mapped)) return null;

            t.getElementTypes().forEach(type -> type.accept(this, mapped));

            return null;
        }

        @SuppressWarnings("Duplicates")
        @Override
        public Void visit(UnionType t, Set<TypeParameterType> mapped) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (addExisting(t, mapped)) return null;

            t.getElements().forEach(element -> element.accept(this, mapped));

            return null;
        }

        @Override
        public Void visit(UnresolvedType t, Set<TypeParameterType> mapped) {
            throw new RuntimeException();
        }

        @Override
        public Void visit(TypeParameterType t, Set<TypeParameterType> mapped) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (addExisting(t, mapped)) return null;


            if (!mapped.contains(t)) {
                result.put(baseType, t);
            }

            if (t.getConstraint() != null) {
                t.getConstraint().accept(this, mapped);
            }

            return null;
        }

        @Override
        public Void visit(SymbolType t, Set<TypeParameterType> mapped) {
            return null;
        }

        @Override
        public Void visit(StringLiteral t, Set<TypeParameterType> mapped) {
            return null;
        }

        @Override
        public Void visit(BooleanLiteral t, Set<TypeParameterType> mapped) {
            return null;
        }

        @Override
        public Void visit(NumberLiteral t, Set<TypeParameterType> mapped) {
            return null;
        }

        @SuppressWarnings("Duplicates")
        @Override
        public Void visit(IntersectionType t, Set<TypeParameterType> mapped) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (addExisting(t, mapped)) return null;

            t.getElements().forEach(element -> element.accept(this, mapped));

            return null;
        }

        @Override
        public Void visit(ClassInstanceType t, Set<TypeParameterType> mapped) {
            ((ClassType)t.getClassType()).getInstanceType().accept(this, mapped);

            return null;
        }

        @Override
        public Void visit(NeverType t, Set<TypeParameterType> mapped) {
            return null;
        }

        @Override
        public Void visit(ThisType t, Set<TypeParameterType> mapped) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (addExisting(t, mapped)) return null;

            t.getConstraint().accept(this, mapped);

            return null;
        }

        @Override
        public Void visit(IndexType t, Set<TypeParameterType> mapped) {
            throw new RuntimeException();
        }

        @Override
        public Void visit(IndexedAccessType t, Set<TypeParameterType> mapped) {
            if (seen.contains(t)) {
                return null;
            }
            seen.add(t);
            if (addExisting(t, mapped)) return null;

            t.getIndexType().accept(this, mapped);
            t.getObjectType().accept(this, mapped);

            return null;
        }
    }
}
