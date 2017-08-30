package dk.webbies.tajscheck.tajstester;

import dk.au.cs.casa.typescript.types.*;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.solver.GenericSolver;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.typeCreator.SpecInstantiator;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class TypeValuesHandler {

    private final Map<Type, String> typeNames;
    private BenchmarkInfo info;
    private final Map<TypeWithContext, Value> savedValues = new HashMap<>();
    private final SpecInstantiator instantiator;

    TypeValuesHandler(Map<Type, String> typeNames, Solver.SolverInterface c, BenchmarkInfo info) {
        this.typeNames = typeNames;
        this.info = info;
        this.instantiator = new SpecInstantiator(info.getSpec(), c, info);
    }

    public Value findFeedbackValue(TypeWithContext t) {
        if (savedValues.containsKey(t)) {
            return savedValues.get(t);
        }
        return null;
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
            if (!typeNames.containsKey(t.getType())) {
                System.out.println();
            }
            assert typeNames.containsKey(t.getType());
            name = typeNames.get(t.getType());
        }

        return instantiator.createValue(t, name);
    }

    public boolean addFeedbackValue(TypeWithContext t, Value v) {
        assert(v != null);
        AtomicBoolean valueWasAdded = new AtomicBoolean(false);
        info.typesUtil.forAllSubTypes(t.getType(), t.getTypeContext(), (subType) -> {
            if (savedValues.containsKey(subType)) {
                Value prevValue = savedValues.get(subType);
                Value joined = v.join(prevValue);
                if (prevValue.equals(joined)) {
                    return;
                }
                savedValues.put(subType, joined);
                valueWasAdded.set(true);
            } else {
                savedValues.put(subType, v);
                valueWasAdded.set(true);
            }
        });
        return valueWasAdded.get();
    }
}

