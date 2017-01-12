package dk.webbies.tajscheck.typeutil.typeContext;

import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.util.MultiMap;

import java.util.*;

/**
 * Created by erik1 on 14-11-2016.
 */
public interface TypeContext {
    public TypeContext append(Map<TypeParameterType, Type> newParameters);

    public TypeContext withThisType(Type thisType);

    public boolean containsKey(TypeParameterType parameter);

    public TypeWithContext get(TypeParameterType parameter);

    public Map<TypeParameterType, Type> getMap();

    public Type getThisType();

    public TypeContext append(TypeContext other);

    public TypeContext cleanTypeParameters(Type baseType, MultiMap<Type, TypeParameterType> reachableTypeParameterMap);

    public Benchmark getBenchmark();

    public static TypeContext create(Benchmark benchmark) {
        if (benchmark.options.disableGenerics) {
            return new NullTypeContext(benchmark);
        } else {
            return new OptimizingTypeContext(benchmark);
        }
    }
}
