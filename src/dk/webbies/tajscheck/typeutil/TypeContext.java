package dk.webbies.tajscheck.typeutil;

import dk.au.cs.casa.typescript.types.ClassType;
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
    private final ClassType classType;
    public final Benchmark bench;

    public TypeContext(Benchmark bench) {
        this.bench = bench;
        this.map = Collections.emptyMap();
        this.persistent = Collections.emptySet();
        this.classType = null;
    }

    private TypeContext(Map<TypeParameterType, Type> map, Set<TypeParameterType> persistent, ClassType classType, Benchmark bench) {
        this.map = map;
        this.persistent = persistent;
        this.classType = classType;
        this.bench = bench;
    }

    public TypeContext append(Map<TypeParameterType, Type> newParameters) {
        Map<TypeParameterType, Type> newMap = new HashMap<>(this.map);
        newMap.putAll(newParameters);
        return new TypeContext(newMap, persistent, this.classType, bench);
    }

    public TypeContext withClass(ClassType classType) {
        return new TypeContext(this.map, persistent, classType, bench);
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

    public ClassType getClassType() {
        return classType;
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

        return new TypeContext(this.map, persistent, this.classType, bench);
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

    public TypeContext remove(TypeParameterType typeParameter) {
        Map<TypeParameterType, Type> newMap = new HashMap<>(this.map);
        assert newMap.remove(typeParameter) != null;
        return new TypeContext(newMap, persistent, this.classType, bench);
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

        /*Set<TypeParameterType> expensiveReachable = TypesUtil.collectAllTypes(baseType).stream().filter(TypeParameterType.class::isInstance).map(TypeParameterType.class::cast).collect(Collectors.toSet());

        if (!reachable.equals(expensiveReachable)) {
//            System.out.println(); // TODO: Test this.
        }*/

        clone.map.keySet().retainAll(reachable);

        return clone;
    }

}
