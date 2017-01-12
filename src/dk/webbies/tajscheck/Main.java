package dk.webbies.tajscheck;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.buildprogram.TestProgramBuilder;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.paser.AST.Statement;
import dk.webbies.tajscheck.paser.AstToStringVisitor;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.testcreator.test.LoadModuleTest;
import dk.webbies.tajscheck.testcreator.TestCreator;
import dk.webbies.tajscheck.typeutil.FreeGenericsFinder;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.util.IdentityHashSet;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;
import dk.webbies.tajscheck.util.selenium.SeleniumDriver;
import org.apache.http.HttpException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dk.webbies.tajscheck.buildprogram.TestProgramBuilder.*;

/**
 * Created by erik1 on 01-11-2016.
 */
public class Main {
    public static final String TEST_FILE_NAME = "test.js";
    public static final String COVERAGE_FILE_NAME = "coverage.js";

    public static void writeFullDriver(Benchmark bench) throws Exception {
        writeFullDriver(bench, null);
    }

    public static void writeFullDriver(Benchmark bench, ExecutionRecording recording) throws Exception {
        String programString = generateFullDriver(bench, recording);

        Util.writeFile(getTestFilePath(bench, TEST_FILE_NAME), programString);
    }

    public static String createRecordedProgram(Benchmark bench, ExecutionRecording recording) throws Exception {
        String programString = generateFullDriver(bench, recording);

        Util.writeFile(getTestFilePath(bench, "recorded.js"), programString);

        System.out.println(programString);

        return programString;
    }

    public static void genSmallDrivers(Benchmark orgBench) throws IOException, InterruptedException {
        SpecReader spec = ParseDeclaration.getTypeSpecification(orgBench.environment, Collections.singletonList(orgBench.dTSFile));

        SpecReader emptySpec = ParseDeclaration.getTypeSpecification(orgBench.environment, new ArrayList<>());

        Set<Type> nativeTypes = TypesUtil.collectNativeTypes(spec, emptySpec);

        Map<Type, String> typeNames = ParseDeclaration.getTypeNamesMap(spec);

        Type typeToTest = getTypeToTest(orgBench, spec);

        TypeParameterIndexer typeParameterIndexer = new TypeParameterIndexer(orgBench.options);

        FreeGenericsFinder freeGenericsFinder = new FreeGenericsFinder(typeToTest);

        List<Test> tests = new TestCreator(nativeTypes, typeNames, typeToTest, orgBench, typeParameterIndexer, freeGenericsFinder).createTests(false);

        if (tests.size() > 100) {
            tests.removeAll(new IdentityHashSet<>(tests.subList(100, tests.size())));
            System.err.println("Artifically limiting the amount of small drivers to 100");
        }

        int counter = 0;

        File dir = new File(getTestFilePath(orgBench, "smallDrivers"));
        if (!dir.exists()) {
            boolean created = dir.mkdir();
            assert created;
        }

        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (Test test : tests) {
            String path = test.getPath();

            Benchmark bench = orgBench.withPathsToTest(Collections.singletonList(path));

            int count = counter++;
            pool.submit(() -> {
                try {
                    System.out.println("Creating small driver for: " + path + "  " + (count + 1) + "/" + tests.size());

                    List<Test> specificTests = new TestCreator(nativeTypes, typeNames, typeToTest, bench, typeParameterIndexer, freeGenericsFinder).createTests();
                    specificTests.add(new LoadModuleTest(Main.getRequirePath(bench), typeToTest, bench));

                    Statement program = new TestProgramBuilder(bench, nativeTypes, typeNames, specificTests, typeToTest, typeParameterIndexer, freeGenericsFinder).buildTestProgram(null);

                    String filePath = getTestFilePath(bench, "smallDrivers/small_driver_" + count + ".js");

                    Util.writeFile(filePath, AstToStringVisitor.toString(program));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.MINUTES);

    }

    public static String generateFullDriver(Benchmark bench) throws IOException {
        return generateFullDriver(bench, null);
    }

    public static String generateFullDriver(Benchmark bench, ExecutionRecording recording) throws IOException {
        SpecReader spec = ParseDeclaration.getTypeSpecification(bench.environment, Collections.singletonList(bench.dTSFile));

        SpecReader emptySpec = ParseDeclaration.getTypeSpecification(bench.environment, new ArrayList<>());

        Set<Type> nativeTypes = TypesUtil.collectNativeTypes(spec, emptySpec);

        Map<Type, String> typeNames = ParseDeclaration.getTypeNamesMap(spec);

        Type typeToTest = getTypeToTest(bench, spec);

        FreeGenericsFinder freeGenericsFinder = new FreeGenericsFinder(typeToTest);

        TypeParameterIndexer typeParameterIndexer = new TypeParameterIndexer(bench.options);

        List<Test> tests = new TestCreator(nativeTypes, typeNames, typeToTest, bench, typeParameterIndexer, freeGenericsFinder).createTests();
        tests.add(new LoadModuleTest(Main.getRequirePath(bench), typeToTest, bench));

        Statement program = new TestProgramBuilder(bench, nativeTypes, typeNames, tests, typeToTest, typeParameterIndexer, freeGenericsFinder).buildTestProgram(recording);

        return AstToStringVisitor.toString(program);
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


    public static String getTestFilePath(Benchmark bench, String fileName) {
        String folder = getFolderPath(bench);

        return folder + fileName;
    }

    private static String getFolderPath(Benchmark bench) {
        String jsPath = bench.jsFile;
        int lastIndex = jsPath.lastIndexOf('/');

        return jsPath.substring(0, lastIndex + 1);
    }

    private static String getRequirePath(Benchmark bench) {
        String jsPath = new File(bench.jsFile).getAbsolutePath();

        int lastIndex = jsPath.lastIndexOf('\\');
        String jsFile = jsPath.substring(lastIndex + 1, jsPath.length());

        return "./" + jsFile;
    }

    public static String runBenchmark(Benchmark bench, int timeout) throws IOException, TimeoutException {
        String testFilePath = getTestFilePath(bench, TEST_FILE_NAME);
        Benchmark.RUN_METHOD run_method = bench.run_method;
        return runBenchmark(testFilePath, run_method, timeout);
    }

    private static String runBenchmark(String testFilePath, Benchmark.RUN_METHOD run_method) throws IOException {
        try {
            return runBenchmark(testFilePath, run_method, -1);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private static String runBenchmark(String testFilePath, Benchmark.RUN_METHOD run_method, int timeout) throws IOException, TimeoutException {
        switch (run_method) {
            case NODE:
            case BOOTSTRAP:
                return Util.runNodeScript(testFilePath, timeout);
            case BROWSER:
                try {
                    String rawResult = SeleniumDriver.executeScript(Util.readFile(testFilePath), timeout);
                    JSONObject json = new JSONObject("{res: " + rawResult + "}"); // Ugly, but works.
                    return json.getString("res");
                } catch (HttpException | JSONException e) {
                    throw new RuntimeException(e);
                }
            default:
                throw new RuntimeException("Unknown run method: " + run_method);
        }
    }

    public static Map<String, CoverageResult> genCoverage(Benchmark bench) throws IOException {
        try {
            return genCoverage(bench, -1);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, CoverageResult> genCoverage(Benchmark bench, int timeout) throws IOException, TimeoutException {
        if (bench.run_method != Benchmark.RUN_METHOD.BROWSER) {
            StringBuilder prefix = new StringBuilder();
            int foldersDeep = getFolderPath(bench).split("/").length;
            for (int i = 0; i < foldersDeep; i++) {
                prefix.append("../");
            }

            Util.runNodeScript(prefix + "node_modules/istanbul/lib/cli.js cover " + Main.TEST_FILE_NAME, new File(getFolderPath(bench)), timeout);

            return CoverageResult.parse(Util.readFile(getTestFilePath(bench, "coverage/coverage.json")));
        }


        String instrumented = Util.runNodeScript("node_modules/istanbul/lib/cli.js instrument " + getTestFilePath(bench, TEST_FILE_NAME), timeout);

        String coverageFileName = getTestFilePath(bench, COVERAGE_FILE_NAME);
        Util.writeFile(coverageFileName, instrumented);

        String coverageResult = runBenchmark(coverageFileName, bench.run_method, timeout);

        Map<String, CoverageResult> result = CoverageResult.parse(coverageResult);
        assert result.size() == 1;

        String[] testFile = Util.readFile(getTestFilePath(bench, TEST_FILE_NAME)).split("\n");
        List<Integer> splitLines = Util.withIndex(Stream.of(testFile)).filter(pair -> pair.getLeft().contains(START_OF_FILE_MARKER)).map(Pair::getRight).collect(Collectors.toList());

        Map<String, Pair<Integer, Integer>> splitRules = new HashMap<>();
        for (int i = 0; i < splitLines.size(); i++) {
            int splitLine = splitLines.get(i);
            String jsName = testFile[splitLine].substring(("// " + START_OF_FILE_MARKER).length(), testFile[splitLine].length());
            if (i != splitLines.size() - 1) {
                splitRules.put(jsName, new Pair<>(splitLine, splitLines.get(i + 1)));
            } else {
                splitRules.put(jsName, new Pair<>(splitLine, testFile.length));
            }
        }

        assert result.size() == 1;

        return result.get(TEST_FILE_NAME).split(splitRules);
    }

    public static String runBenchmark(Benchmark bench) throws IOException {
        try {
            return runBenchmark(bench, -1);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
