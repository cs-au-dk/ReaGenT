package dk.webbies.tajscheck;

import dk.au.cs.casa.typescript.types.Type;
import dk.au.cs.casa.typescript.types.TypeParameterType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by erik1 on 14-11-2016.
 */
public class ParameterMap {
    private final Map<TypeParameterType, Type> map;

    public ParameterMap() {
        this.map = new HashMap<>();
    }

    ParameterMap(Map<TypeParameterType, Type> map) {
        this.map = map;
    }

    public ParameterMap append(Map<TypeParameterType, Type> newParameters) {
        Map<TypeParameterType, Type> newMap = new HashMap<>(this.map);
        newMap.putAll(newParameters);
        return new ParameterMap(newMap);
    }


    public boolean containsKey(TypeParameterType parameter) {
        return map.containsKey(parameter);
    }

    public Type get(TypeParameterType parameter) {
        return map.get(parameter);
    }

    public Set<TypeParameterType> keySet() {
        return map.keySet();
    }

    public Map<TypeParameterType, Type> getMap() {
        return map;
    }

    public Set<Map.Entry<TypeParameterType, Type>> entrySet() {
        return map.entrySet();
    }

    public ParameterMap append(ParameterMap other) {
        return append(other.getMap());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParameterMap that = (ParameterMap) o;

        return map.equals(that.map);

    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    public boolean isSubSetOf(ParameterMap larger) {
        return this.map.entrySet().stream().allMatch(entry ->
                larger.containsKey(entry.getKey()) && larger.get(entry.getKey()).equals(this.get(entry.getKey()))
        );
    }
}
