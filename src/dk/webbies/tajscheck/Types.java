package dk.webbies.tajscheck;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.util.Util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by erik1 on 01-11-2016.
 */
public class Types {
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
