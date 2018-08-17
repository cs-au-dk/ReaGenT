package dk.webbies.tajscheck.benchmark.options.staticOptions.filter;

import dk.au.cs.casa.typescript.types.*;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.analysis.HostAPIs;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.solver.GenericSolver;
import dk.brics.tajs.util.Pair;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.buildprogram.TypeChecker;
import dk.webbies.tajscheck.buildprogram.typechecks.TypeCheck;
import dk.webbies.tajscheck.tajstester.TajsTypeChecker;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
import dk.webbies.tajscheck.tajstester.typeCreator.SpecInstantiator;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;

import java.util.*;
import java.util.stream.Collectors;

public class CopyObjectInstantiation implements SpecInstantiator.InstantiationFilter, TypeVisitorWithArgument<Value, CopyObjectInstantiation.Arg> {
    @Override
    public Value filter(TypeWithContext type, Value value, Solver.SolverInterface c, BenchmarkInfo info) {
        if (value == null) {
            return null;
        }
        List<Value> results = TajsTypeChecker.split(value).stream().map(v -> {
            try {
                return type.getType().accept(this, new Arg(type.getTypeContext(), v, c, info));
            } catch (NoSuchTypePossible e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (results.isEmpty()) {
            throw new NoSuchTypePossible();
        }
        return Value.join(results);
    }

    public static final class NoSuchTypePossible extends RuntimeException {}


    public Value filter(Type type, Arg arg) {
        return filter(new TypeWithContext(type, arg.context), arg.value, arg.c, arg.info);
    }

    private List<TypeViolation> typeCheck(Type t, Arg arg) {
        TajsTypeChecker typeChecker = new TajsTypeChecker(null, arg.c, arg.info, null);
        List<TypeCheck> typeChecks = TypeChecker.getTypeChecks(t, arg.context, arg.info, 0);
        return typeChecker.getTypeViolations(new TypeWithContext(t, arg.context), arg.value, typeChecks, "");
    }

    @Override
    public Value visit(AnonymousType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(ClassType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(GenericType t, Arg arg) {
        throw new RuntimeException();
    }

    private final Map<Pair<ObjectLabel, TypeWithContext>, ObjectLabel> copiedObjects = new HashMap<>();

    @Override
    public Value visit(InterfaceType t, Arg arg) {
        Value value = arg.value;
        TypeContext context = arg.context;
        Solver.SolverInterface c = arg.c;

        assert value.restrictToNotObject().isNone();
        assert t.getBaseTypes().isEmpty(); // TypesUtil.constructSyntheticInterfaceWithBaseTypes

        if (!t.getDeclaredCallSignatures().isEmpty() || !t.getDeclaredConstructSignatures().isEmpty()) {
            assert t.getDeclaredNumberIndexType() == null;
            assert t.getDeclaredStringIndexType() == null;
            assert t.getDeclaredProperties().isEmpty();
            return arg.value;
        }

        assert t.getDeclaredNumberIndexType() == null;
        assert t.getDeclaredStringIndexType() == null;

        if (!value.isMaybeObject()) {
            throw new NoSuchTypePossible();
        }

        Set<ObjectLabel> newLabels = value.getObjectLabels().stream().map(orgLabel -> {
            if (orgLabel.getHostObject() != null && orgLabel.getHostObject() instanceof CopiedObjectLabel) {
                if (((CopiedObjectLabel) orgLabel.getHostObject()).type.equals(new TypeWithContext(t, arg.context))) {
                    return orgLabel;
                }
            }

            Pair<ObjectLabel, TypeWithContext> labelKey = Pair.make(orgLabel, new TypeWithContext(t, arg.context));
            if (!copiedObjects.containsKey(labelKey)) {
                ObjectLabel label = ObjectLabel.make(new CopiedObjectLabel(orgLabel, new TypeWithContext(t, arg.context)), ObjectLabel.Kind.OBJECT);
                c.getState().newObject(label);
                copiedObjects.put(labelKey, label);
            }
            ObjectLabel label = copiedObjects.get(labelKey);

            Obj object = c.getState().getObject(label, true);
            {
                Value internalPrototype = UnknownValueResolver.getInternalPrototype(orgLabel, c.getState(), false);
                Value previous = UnknownValueResolver.getInternalPrototype(label, c.getState(), false);
                if (!valueEquals(internalPrototype, previous, c.getState())) {
                    object.setInternalPrototype(internalPrototype);
                }
            }


            assert !UnknownValueResolver.getDefaultNonArrayProperty(orgLabel, c.getState()).isMaybePresent();
            assert !UnknownValueResolver.getDefaultArrayProperty(orgLabel, c.getState()).isMaybePresent();
            assert !UnknownValueResolver.getInternalValue(orgLabel, c.getState(), false).isMaybePresent();

            Map<String, Type> propDecs = t.getDeclaredProperties();
            UnknownValueResolver.getProperties(orgLabel, c.getState()).forEach((PKey propKey, Value propValue) -> {
                assert propKey instanceof PKey.StringPKey;
                String propName = ((PKey.StringPKey) propKey).getStr();
                Value previousPropValue = object.getProperty(propKey);
                Value newPropValue;
                if (propDecs.containsKey(propName)) {
                    newPropValue = filter(new TypeWithContext(propDecs.get(propName), arg.context), propValue, c, arg.info);
                } else {
                    newPropValue = propValue;
                }
                previousPropValue = previousPropValue == null ? null : UnknownValueResolver.getRealValue(previousPropValue, c.getState());
                if (!valueEquals(newPropValue, previousPropValue, c.getState())) {
                    object.setProperty(propKey, newPropValue.setAttributes(propValue));
                }
            });
            return label;
        }).collect(Collectors.toSet());


        return Value.makeObject(newLabels);
    }

    private boolean valueEquals(Value newValue, Value previous, State state) {
        if (newValue == null || previous == null) {
            return newValue == null && previous == null;
        }
        newValue = allLabelsToSingleton(UnknownValueResolver.getRealValue(newValue, state)).restrictToNonAttributes();
        previous = allLabelsToSingleton(UnknownValueResolver.getRealValue(previous, state)).restrictToNonAttributes();
        return newValue.equals(previous);
    }

    public static Value allLabelsToSingleton(Value value) {
        value = Value.makeNone().join(value);
        Set<ObjectLabel> labels = value.getObjectLabels();
        value = value.restrictToNotObject();
        value = value.join(Value.makeObject(labels.stream().map(label -> {
            if (label.isSingleton()) {
                return label;
            } else {
                return label.makeSingleton();
            }
        }).collect(Collectors.toSet())));
        return value;
    }

    @Override
    public Value visit(ReferenceType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(SimpleType t, Arg arg) {
        List<TypeViolation> typeViolations = typeCheck(t, arg);

        if (typeViolations.isEmpty()) {
            return arg.value;
        } else {
            throw new NoSuchTypePossible();
        }
    }

    @Override
    public Value visit(TupleType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(UnionType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(TypeParameterType t, Arg arg) {
        if (arg.context.get(t) == null) {
            return arg.value;
        }
        throw new RuntimeException();
    }

    @Override
    public Value visit(StringLiteral t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(BooleanLiteral t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(NumberLiteral t, Arg arg) {
        if (arg.value.isMaybeNum(t.getValue())) {
            return Value.makeNum(t.getValue());
        } else {
            throw new NoSuchTypePossible();
        }
    }

    @Override
    public Value visit(IntersectionType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(ClassInstanceType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(ThisType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(IndexType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(IndexedAccessType t, Arg arg) {
        throw new RuntimeException();
    }


    public static final class Arg {
        private final TypeContext context;
        private final Value value;
        private final Solver.SolverInterface c;
        private final BenchmarkInfo info;

        public Arg(TypeContext context, Value value, GenericSolver<State, Context, CallEdge, IAnalysisMonitoring, Analysis>.SolverInterface c, BenchmarkInfo info) {
            this.context = context;
            this.value = value;
            this.c = c;
            this.info = info;
        }
    }

    public static class CopiedObjectLabel implements HostObject {
        @Override
        public HostAPI getAPI() {
            return HostAPIs.SPEC;
        }

        private final ObjectLabel orgLabel;
        private final TypeWithContext type;

        public CopiedObjectLabel(ObjectLabel orgLabel, TypeWithContext type) {
            this.orgLabel = orgLabel;
            this.type = type;
        }

        @Override
        public String toString() {
            return "CopiedObjectLabel{" +
                    "orgLabel=" + orgLabel +
                    ", type=" + type +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CopiedObjectLabel that = (CopiedObjectLabel) o;
            return Objects.equals(orgLabel, that.orgLabel) &&
                    Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(orgLabel, type);
        }
    }

}
