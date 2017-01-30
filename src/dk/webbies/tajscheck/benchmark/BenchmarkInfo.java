package dk.webbies.tajscheck.benchmark;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.ClassType;
import dk.au.cs.casa.typescript.types.GenericType;
import dk.au.cs.casa.typescript.types.InterfaceType;
import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.buildprogram.TestProgramBuilder;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.typeutil.TypesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 29-01-2017.
 */
public class BenchmarkInfo {
    public final Benchmark bench;
    public final Type typeToTest;
    public final Set<Type> nativeTypes;
    public final FreeGenericsFinder freeGenericsFinder;
    public final Map<Type, String> typeNames;
    public final TypeParameterIndexer typeParameterIndexer;

    private BenchmarkInfo(Benchmark bench, Type typeToTest, Set<Type> nativeTypes, FreeGenericsFinder freeGenericsFinder, Map<Type, String> typeNames, TypeParameterIndexer typeParameterIndexer) {
        this.bench = bench;
        this.typeToTest = typeToTest;
        this.nativeTypes = nativeTypes;
        this.freeGenericsFinder = freeGenericsFinder;
        this.typeNames = typeNames;
        this.typeParameterIndexer = typeParameterIndexer;
    }

    public static BenchmarkInfo create(Benchmark bench) {
        SpecReader spec = ParseDeclaration.getTypeSpecification(bench.environment, Collections.singletonList(bench.dTSFile));

        SpecReader emptySpec = ParseDeclaration.getTypeSpecification(bench.environment, new ArrayList<>());

        Set<Type> nativeTypes = TypesUtil.collectNativeTypes(spec, emptySpec);

        Map<Type, String> typeNames = ParseDeclaration.getTypeNamesMap(spec);

        Type typeToTest = getTypeToTest(bench, spec);

        FreeGenericsFinder freeGenericsFinder = new FreeGenericsFinder(typeToTest);

        TypeParameterIndexer typeParameterIndexer = new TypeParameterIndexer(bench.options);

        return new BenchmarkInfo(bench, typeToTest, nativeTypes, freeGenericsFinder, typeNames, typeParameterIndexer);
    }

    private static Type getTypeToTest(Benchmark bench, SpecReader spec) {
        Type result = ((InterfaceType) spec.getGlobal()).getDeclaredProperties().get(bench.module);

        if (result == null) {
            throw new RuntimeException("Module: " + bench.module + " not found in benchmark");
        }

        for (Type type : TypesUtil.collectAllTypes(result)) {
            if (bench.options.splitUnions) {
                if (type instanceof InterfaceType) {
                    InterfaceType inter = (InterfaceType) type;
                    inter.setDeclaredCallSignatures(TypesUtil.splitSignatures(inter.getDeclaredCallSignatures()));
                    inter.setDeclaredConstructSignatures(TypesUtil.splitSignatures(inter.getDeclaredConstructSignatures()));
                } else if (type instanceof GenericType) {
                    GenericType inter = (GenericType) type;
                    inter.setDeclaredCallSignatures(TypesUtil.splitSignatures(inter.getDeclaredCallSignatures()));
                    inter.setDeclaredConstructSignatures(TypesUtil.splitSignatures(inter.getDeclaredConstructSignatures()));
                }
            }

            if (type instanceof InterfaceType) {
                ((InterfaceType) type).setDeclaredProperties(fixUnderscoreNames(((InterfaceType) type).getDeclaredProperties()));
            } else if (type instanceof GenericType) {
                ((GenericType) type).setDeclaredProperties(fixUnderscoreNames(((GenericType) type).getDeclaredProperties()));
            }
        }

        if (result instanceof InterfaceType) {
            InterfaceType inter = (InterfaceType) result;
            if (inter.getDeclaredCallSignatures().size() + inter.getDeclaredConstructSignatures().size() > 0) {
                if (inter.getDeclaredProperties().keySet().contains("prototype") && inter.getDeclaredProperties().get("prototype") instanceof ClassType) {
                    return inter.getDeclaredProperties().get("prototype");
                }
            }
        }

        return result;
    }

    private static Map<String, Type> fixUnderscoreNames(Map<String, Type> declaredProperties) {
        return declaredProperties.entrySet().stream().collect(Collectors.toMap(
                entry -> fixUnderscoreName(entry.getKey()),
                Map.Entry::getValue
        ));
    }

    private static String fixUnderscoreName(String key) {
        // For some reason, everything with two or more underscore in the beginning, gets an extra underscore. I have a test that fails if this behaviour changes.
        if (key.startsWith("___")) {
            return key.substring(1, key.length());
        }
        return key;
    }

    public BenchmarkInfo withBench(Benchmark bench) {
        return new BenchmarkInfo(
                bench,
                this.typeToTest,
                this.nativeTypes,
                this.freeGenericsFinder,
                this.typeNames,
                this.typeParameterIndexer
        );
    }
}
