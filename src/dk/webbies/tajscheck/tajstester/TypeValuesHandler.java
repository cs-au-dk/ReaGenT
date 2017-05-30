package dk.webbies.tajscheck.tajstester;

import com.google.common.collect.BiMap;
import dk.au.cs.casa.typescript.types.*;
import dk.brics.tajs.analysis.PropVarOperations;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.StateExtras;
import dk.brics.tajs.lattice.Value;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;

public class TypeValuesHandler {

    private final BiMap<TypeWithContext, String> typeNames;
    private final StateExtras se;
    private final Solver.SolverInterface c;
    private final PropVarOperations pv;

    TypeValuesHandler(BiMap<TypeWithContext, String> typeNames, StateExtras se, Solver.SolverInterface c) {
        this.typeNames = typeNames;
        this.se = se;
        this.c = c;
        this.pv = c.getAnalysis().getPropVarOperations();
    }

    public Value findValueForType(TypeWithContext t) {
        Value saved = t.getType().accept(new TypeFinderVisitor(t.getTypeContext()));
        if (saved.isNone()) {
            Value generated = t.getType().accept(new TypeCreatorVisitor(t.getTypeContext()));
            return generated;
        }
        return saved;
    }

    public void addValueForType(TypeWithContext t, Value v) {
        se.addToMaySet(getName(t), v.getObjectLabels());
    }

    private String getName(TypeWithContext t) {
        typeNames.putIfAbsent(t, "T" + typeNames.size());
        return typeNames.get(t);
    }

    private class TypeFinderVisitor implements TypeVisitor<Value> {
        private final TypeContext context;

        TypeFinderVisitor(TypeContext context) {
            this.context = context;
        }

        @Override
        public Value visit(AnonymousType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(ClassType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(GenericType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(InterfaceType t) {
            return Value.makeObject(se.getFromMaySet(getName(new TypeWithContext(t, context))));
            //TODO: and base types ?
        }

        @Override
        public Value visit(ReferenceType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(SimpleType t) {
            return Value.makeNone();
        }

        @Override
        public Value visit(TupleType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(UnionType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(TypeParameterType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(StringLiteral t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(BooleanLiteral t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(NumberLiteral t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(IntersectionType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(ClassInstanceType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(ThisType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(IndexType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(IndexedAccessType t) {
            throw new RuntimeException("Implement me");
        }
    }

    private class TypeCreatorVisitor implements TypeVisitor<Value> {
        private final TypeContext context;

        TypeCreatorVisitor(TypeContext context) {
            this.context = context;
        }

        @Override
        public Value visit(AnonymousType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(ClassType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(GenericType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(InterfaceType t) {

            /*
            if(!t.getDeclaredCallSignatures().isEmpty()) {

                for(Signature s : t.getDeclaredCallSignatures()) {

                    //FIXME: Introduce a specific heapContext in the funLabel, differentiating every label by its type
                    ObjectLabel funLabel = ObjectLabel.mk(c.getNode().getBlock().getFirstNode(), ObjectLabel.Kind.FUNCTION);

                    InitialStateBuilder.createPrimitiveFunction(
                            funLabel,
                            InitialStateBuilder.FUNCTION_PROTOTYPE,
                            ECMAScriptObjects.CUSTOM_TYPE_CHECKER,
                            PKey.StringPKey.mk(""),
                            s.getMinArgumentCount(),
                            c
                    );


                }
            }
            */
            return Value.makeNone();

        }

        @Override
        public Value visit(ReferenceType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(SimpleType t) {
            switch (t.getKind()) {
                case Any:
                    throw new RuntimeException("Implement me"); //FIXME: Entry point for unknown value resolver ? Value.makeUnknown();
                case String:
                    return Value.makeAnyStr();
                case Null:
                    return Value.makeNull();
                case Number:
                    return Value.makeAnyNum();
                case Boolean:
                    return Value.makeAnyBool();
                case Undefined:
                    return Value.makeUndef();
                case Void:
                    return Value.makeUndef();
                case Enum:
                case Never:
                case Symbol:
                case Object:
                    throw new RuntimeException("Implement me");
                default:
                    throw new RuntimeException("Unexpected");
            }
        }

        @Override
        public Value visit(TupleType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(UnionType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(TypeParameterType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(StringLiteral t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(BooleanLiteral t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(NumberLiteral t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(IntersectionType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(ClassInstanceType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(ThisType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(IndexType t) {
            throw new RuntimeException("Implement me");
        }

        @Override
        public Value visit(IndexedAccessType t) {
            throw new RuntimeException("Implement me");
        }
    }
}

