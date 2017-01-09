package dk.webbies.tajscheck.typeutil;

import dk.au.cs.casa.typescript.types.ThisType;
import dk.au.cs.casa.typescript.types.Type;
import dk.au.cs.casa.typescript.types.TypeParameterType;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Util;

import java.util.*;

/**
 * Created by erik1 on 14-11-2016.
 */
public class TypeContext {
    private final Map<TypeParameterType, Type> map;
    private final Set<TypeParameterType> persistent;
    private final Type thisType;
    public final Benchmark bench;

    public TypeContext(Benchmark bench) {
        this.bench = bench;
        this.map = Collections.emptyMap();
        this.persistent = Collections.emptySet();
        this.thisType = null;
    }

    private TypeContext(Map<TypeParameterType, Type> map, Set<TypeParameterType> persistent, Type classType, Benchmark bench) {
        this.map = map;
        this.persistent = persistent;
        this.thisType = classType;
        this.bench = bench;
    }

    public TypeContext append(Map<TypeParameterType, Type> newParameters) {
        Map<TypeParameterType, Type> newMap = new HashMap<>(this.map);
        newMap.putAll(newParameters);
        return new TypeContext(newMap, persistent, this.thisType, bench);
    }

    public TypeContext withThisType(Type thisType) {
        if (thisType == null || this.thisType == null) {
            return new TypeContext(this.map, persistent, thisType, bench);
        }
        Set<Type> baseTypes = TypesUtil.getAllBaseTypes(this.thisType, new HashSet<>());

        if (baseTypes.contains(thisType)) {
            return this;
        } else {
            return new TypeContext(this.map, persistent, thisType, bench);
        }
    }

    public boolean containsKey(TypeParameterType parameter) {
        return map.containsKey(parameter);
    }

    public TypeWithContext get(TypeParameterType parameter) {
        Type type = map.get(parameter);
        if (type == null) {
            return null;
        }
        return new TypeWithContext(type, this.addPersistent(parameter));
    }

    private TypeContext addPersistent(TypeParameterType parameterType) {
        return addPersistent(Collections.singletonList(parameterType));
    }

    Set<TypeParameterType> keySet() {
        return map.keySet();
    }

    public Map<TypeParameterType, Type> getMap() {
        return map;
    }

    public Type getThisType() {
        return thisType;
    }

    public Set<Map.Entry<TypeParameterType, Type>> entrySet() {
        return map.entrySet();
    }

    public TypeContext append(TypeContext other) {
        return append(other.getMap()).addPersistent(other.persistent);
    }

    private TypeContext addPersistent(Collection<TypeParameterType> newPersistent) {
        Set<TypeParameterType> persistent = new HashSet<>(this.persistent);
        persistent.addAll(newPersistent);

        return new TypeContext(this.map, persistent, this.thisType, bench);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeContext that = (TypeContext) o;

        if (map != null ? !map.equals(that.map) : that.map != null) return false;
        if (persistent != null ? !persistent.equals(that.persistent) : that.persistent != null) return false;
        if (thisType != null ? !thisType.equals(that.thisType) : that.thisType != null) return false;
        return bench != null ? bench.equals(that.bench) : that.bench == null;
    }

    @Override
    public int hashCode() {
        int result = map != null ? map.hashCode() : 0;
        result = 31 * result + (persistent != null ? persistent.hashCode() : 0);
        result = 31 * result + (thisType != null ? thisType.hashCode() : 0);
        result = 31 * result + (bench != null ? bench.hashCode() : 0);
        return result;
    }

    public TypeContext remove(TypeParameterType typeParameter) {
        Map<TypeParameterType, Type> newMap = new HashMap<>(this.map);
        assert newMap.remove(typeParameter) != null;
        return new TypeContext(newMap, persistent, this.thisType, bench);
    }

    public TypeContext cleanTypeParameters(Type baseType, MultiMap<Type, TypeParameterType> reachableTypeParameterMap) {
        if (bench.options.disableSizeOptimization) {
            return this;
        }
        TypeContext clone = this.append(Collections.emptyMap());

        Set<TypeParameterType> reachable = new HashSet<>();

        ArrayList<Type> typesToTest = new ArrayList<>(Util.concat(Util.cast(Type.class, persistent), Collections.singletonList(baseType), map.values()));

        for (Type type : typesToTest) {
            reachable.addAll(reachableTypeParameterMap.get(type));
        }

        clone.map.keySet().retainAll(reachable);

        if (clone.thisType != null) {
            if (!TypesUtil.isThisTypeVisible(baseType) && clone.map.values().stream().noneMatch(ThisType.class::isInstance)) {
                clone = clone.withThisType(null);
            }
        }

        return clone;
    }
}
