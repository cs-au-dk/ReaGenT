package dk.webbies.tajscheck.benchmark;

import dk.au.cs.casa.typescript.types.TypeParameterType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by erik1 on 30-01-2017.
 */
public class TypeParameterIndexer {
    private final boolean combineAllUnconstrainedGenerics;
    private final Map<TypeParameterType, Integer> map = new HashMap<>();
    public static final String IS_UNSTRAINED_GENERIC_MARKER = "_isUnconstrainedGeneric";

    public TypeParameterIndexer(CheckOptions options) {
        this.combineAllUnconstrainedGenerics = options.combineAllUnconstrainedGenerics;
    }

    public String getMarkerField(TypeParameterType t) {
        if (combineAllUnconstrainedGenerics) {
            return IS_UNSTRAINED_GENERIC_MARKER;
        }
        if (map.containsKey(t)) {
            return "typeParameterMarker_" + map.get(t);
        } else {
            map.put(t, map.size());
            return getMarkerField(t);
        }
    }
}
