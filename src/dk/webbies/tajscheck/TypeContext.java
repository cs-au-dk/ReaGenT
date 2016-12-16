package dk.webbies.tajscheck;

import dk.au.cs.casa.typescript.types.ClassType;
import dk.au.cs.casa.typescript.types.Type;
import dk.au.cs.casa.typescript.types.TypeParameterType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by erik1 on 14-11-2016.
 */
public class TypeContext {
    private final Map<TypeParameterType, Type> map;
    private final ClassType classType;

    public TypeContext() {
        this.map = Collections.emptyMap();
        this.classType = null;
    }

    private TypeContext(Map<TypeParameterType, Type> map, ClassType classType) {
        this.map = map;
        this.classType = classType;
    }

    public TypeContext append(Map<TypeParameterType, Type> newParameters) {
        Map<TypeParameterType, Type> newMap = new HashMap<>(this.map);
        newMap.putAll(newParameters);
        return new TypeContext(newMap, this.classType);
    }

    public TypeContext withClass(ClassType classType) {
        return new TypeContext(this.map, classType);
    }

    public boolean containsKey(TypeParameterType parameter) {
        return map.containsKey(parameter);
    }

    public Type get(TypeParameterType parameter) {
        return map.get(parameter);
    }

    Set<TypeParameterType> keySet() {
        return map.keySet();
    }

    public Map<TypeParameterType, Type> getMap() {
        return map;
    }

    public ClassType getClassType() {
        return classType;
    }

    public Set<Map.Entry<TypeParameterType, Type>> entrySet() {
        return map.entrySet();
    }

    public TypeContext append(TypeContext other) {
        return append(other.getMap());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeContext that = (TypeContext) o;

        if (!map.equals(that.map)) return false;
        return classType != null ? classType.equals(that.classType) : that.classType == null;
    }

    @Override
    public int hashCode() {
        int result = map.hashCode();
        result = 31 * result + (classType != null ? classType.hashCode() : 0);
        return result;
    }
}