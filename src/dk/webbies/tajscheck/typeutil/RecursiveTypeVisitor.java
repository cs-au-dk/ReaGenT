package dk.webbies.tajscheck.typeutil;

import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.util.IdentityHashSet;
import dk.webbies.tajscheck.util.Util;

import java.util.Set;

abstract public class RecursiveTypeVisitor<T> implements TypeVisitor<T> {

    protected final Set<Type> seen = new IdentityHashSet<>();
    private final BenchmarkInfo info;

    protected RecursiveTypeVisitor(BenchmarkInfo info) {
        this.info = info;
    }

    @Override
    public T visit(AnonymousType t) {
        return null;
    }

    @Override
    public T visit(ClassType t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        t.getSignatures().forEach(this::acceptSignature);

        accept(info.typesUtil.createClassInstanceType(t));

        t.getBaseTypes().forEach(this::accept);

        t.getStaticProperties().values().forEach(this::accept);

        accept(t.getTarget());

        t.getTypeArguments().forEach(this::accept);

        return null;
    }

    @Override
    public T visit(GenericType t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        t.getTypeArguments().forEach(this::accept);
        t.getBaseTypes().forEach(this::accept);
        t.getDeclaredProperties().values().forEach(this::accept);
        Util.concat(t.getDeclaredCallSignatures(), t.getDeclaredConstructSignatures()).forEach(this::acceptSignature);
        if (t.getDeclaredStringIndexType() != null) {
            accept(t.getDeclaredStringIndexType());
        }
        if (t.getDeclaredNumberIndexType() != null) {
            accept(t.getDeclaredNumberIndexType());
        }
        accept(t.getTarget());
        t.getTypeArguments().forEach(this::accept);
        accept(t.toInterface());
        return null;
    }

    protected void accept(Type type) {
        if (type instanceof DelayedType) {
            type = ((DelayedType) type).getType();
        }
        type.accept(this);
    }

    @Override
    public T visit(InterfaceType t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        t.getTypeParameters().forEach(this::accept);
        t.getBaseTypes().forEach(this::accept);
        t.getDeclaredProperties().values().forEach(this::accept);
        Util.concat(t.getDeclaredCallSignatures(), t.getDeclaredConstructSignatures()).forEach(this::acceptSignature);
        if (t.getDeclaredStringIndexType() != null) {
            accept(t.getDeclaredStringIndexType());
        }
        if (t.getDeclaredNumberIndexType() != null) {
            accept(t.getDeclaredNumberIndexType());
        }
        return null;
    }

    private void acceptSignature(Signature sig) {
        if (sig.getResolvedReturnType() != null) {
            accept(sig.getResolvedReturnType());
        }
        sig.getParameters().stream().map(Signature.Parameter::getType).forEach(this::accept);
        if (sig.getTarget() != null) {
            acceptSignature(sig.getTarget());
        }
        sig.getUnionSignatures().forEach(this::acceptSignature);
        if (sig.getIsolatedSignatureType() != null) {
            accept(sig.getIsolatedSignatureType());
        }
        sig.getTypeParameters().forEach(this::accept);
    }

    @Override
    public T visit(ReferenceType t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        accept(t.getTarget());
        t.getTypeArguments().forEach(this::accept);
        return null;
    }

    @Override
    public T visit(SimpleType t) {
        seen.add(t);
        return null;
    }

    @Override
    public T visit(TupleType t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        t.getElementTypes().forEach(this::accept);

        return null;
    }

    @Override
    public T visit(UnionType t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        t.getElements().forEach(this::accept);

        return null;
    }

    @Override
    public T visit(IntersectionType t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        t.getElements().forEach(this::accept);

        return null;
    }

    @Override
    public T visit(ClassInstanceType t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        accept(t.getClassType());

        return null;
    }

    @Override
    public T visit(ThisType t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        accept(t.getConstraint());

        return null;
    }

    @Override
    public T visit(IndexType t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        accept(t.getType());

        return null;
    }

    @Override
    public T visit(IndexedAccessType t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        accept(t.getObjectType());
        accept(t.getIndexType());

        return null;
    }

    @Override
    public T visit(TypeParameterType t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        if (t.getConstraint() != null) {
            accept(t.getConstraint());
        }
        return null;
    }

    @Override
    public T visit(StringLiteral t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        return null;
    }

    @Override
    public T visit(BooleanLiteral t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        return null;
    }

    @Override
    public T visit(NumberLiteral t) {
        if (seen.contains(t)) {
            return null;
        }
        seen.add(t);

        return null;
    }
}
