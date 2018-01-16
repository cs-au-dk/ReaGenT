package dk.webbies.tajscheck.tajstester;

import dk.au.cs.casa.typescript.types.*;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.solver.GenericSolver;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.typeCreator.SpecInstantiator;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TypeValuesHandler {

    private final Map<Type, String> typeNames;
    private BenchmarkInfo info;
    private final SpecInstantiator instantiator;

    private final MultiMap<TypeWithContext, Reference<Value>> typeValueMap = new ArrayListMultiMap<>();
    private MultiMap<Test, Reference<Value>> testValueMap = new ArrayListMultiMap<>();
    private final Map<TypeWithContext, Value> hasBeenUpdatedMap = new HashMap<>();

    TypeValuesHandler(Map<Type, String> typeNames, Solver.SolverInterface c, BenchmarkInfo info, TajsTypeTester tajsTypeTester) {
        this.typeNames = typeNames;
        this.info = info;
        this.instantiator = new SpecInstantiator(c, info, this, tajsTypeTester);
    }

    public Value getTheAny() {
        return instantiator.getTheAny();
    }

    public Value findFeedbackValue(TypeWithContext t) {
        List<Value> result = typeValueMap.get(t).stream().map(Reference::getValue).filter(Objects::nonNull).collect(Collectors.toList());
        if (result.isEmpty()) {
            return null;
        }
        return Value.join(result);
    }

    public Value createValue(Type type, TypeContext context) {
        return createValue(new TypeWithContext(type, context));
    }

    public Value createValue(TypeWithContext t) {
        String name;
        if ((t.getType() instanceof SimpleType)) {
            name = ((SimpleType) t.getType()).getKind().toString() + ".instance";
        } else if (t.getType() instanceof NumberLiteral) {
            name = "number:" + ((NumberLiteral) t.getType()).getValue();
        } else if (t.getType() instanceof BooleanLiteral) {
            name = "boolean:" + ((BooleanLiteral) t.getType()).getValue();
        } else if (t.getType() instanceof StringLiteral) {
            name = "string:" + ((StringLiteral) t.getType()).getText();
        } else {
            assert typeNames.containsKey(t.getType());
            name = typeNames.get(t.getType());
        }

        return instantiator.createValue(t, name);
    }

    public void cleanUp() {
        testValueMap = testValueMap
                .asMap().entrySet().stream()
                .map(entry ->
                        new AbstractMap.SimpleEntry<>(
                                entry.getKey(),
                                entry.getValue().stream()
                                        .filter(ref -> ref.value != null)
                                        .collect(Collectors.toList())
                        )
                ).filter(entry -> !entry.getValue().isEmpty())
                .collect(ArrayListMultiMap.collector());
    }

    public void clearValuesForTest(Test test) {
        testValueMap.get(test).forEach(Reference::setToNull);
        testValueMap.remove(test);
    }

    public boolean addFeedbackValue(Test test, TypeWithContext type, Value v) {
        if (!hasBeenUpdatedMap.containsKey(type)) {
            hasBeenUpdatedMap.put(type, Value.makeNone());
        }
        Value previous = hasBeenUpdatedMap.get(type);
        if (previous.join(v) != previous) {
            hasBeenUpdatedMap.put(type, previous.join(v));
            instantiator.clearValueCache();
        }


        assert(v != null);
        AtomicBoolean valueWasAdded = new AtomicBoolean(false);
        Reference<Value> ref = new Reference<>(v);
        testValueMap.put(test, ref);
        info.typesUtil.forAllSubTypes(type.getType(), type.getTypeContext(), (subType) -> {
            typeValueMap.put(subType, ref);
        });
        return valueWasAdded.get();
    }

    public SpecInstantiator getInstantiator() {
        return instantiator;
    }

    private static final class Reference<T> {
        T value;

        Reference(T value) {
            this.value = value;
        }

        T getValue() {
            return value;
        }

        void setToNull() {
            this.value = null;
        }
    }
}

