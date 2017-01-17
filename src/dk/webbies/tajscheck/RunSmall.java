package dk.webbies.tajscheck;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.buildprogram.TestProgramBuilder;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.paser.AST.Statement;
import dk.webbies.tajscheck.paser.AstToStringVisitor;
import dk.webbies.tajscheck.testcreator.TestCreator;
import dk.webbies.tajscheck.testcreator.test.LoadModuleTest;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.typeutil.FreeGenericsFinder;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.util.Util;
import dk.webbies.tajscheck.util.trie.Trie;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.benchmarks.Benchmark.RUN_METHOD.BROWSER;

/**
 * Created by erik1 on 16-01-2017.
 */
public class RunSmall {
    public static final String SMALL_DRIVERS_FOLDER = "smallDrivers";
    public static final String SMALL_DRIVER_FILE_PREFIX = "small_driver_";
    private static final int DEFAULT_THREADS = 4;

    public static void genSmallDrivers(Benchmark orgBench) throws IOException {
        genSmallDrivers(orgBench, DEFAULT_THREADS);
    }

    public static void genSmallDrivers(Benchmark orgBench, int threads) throws IOException {
        // Deleting all existing.
        String smallDriversFolderPath = Main.getFolderPath(orgBench) + SMALL_DRIVERS_FOLDER;
        if (new File(smallDriversFolderPath).exists()) {
            Arrays.stream(new File(smallDriversFolderPath).listFiles()).filter(file -> file.getName().contains(SMALL_DRIVER_FILE_PREFIX)).forEach(File::delete);
        }

        SpecReader spec = ParseDeclaration.getTypeSpecification(orgBench.environment, Collections.singletonList(orgBench.dTSFile));

        SpecReader emptySpec = ParseDeclaration.getTypeSpecification(orgBench.environment, new ArrayList<>());

        Set<Type> nativeTypes = TypesUtil.collectNativeTypes(spec, emptySpec);

        Map<Type, String> typeNames = ParseDeclaration.getTypeNamesMap(spec);

        Type typeToTest = Main.getTypeToTest(orgBench, spec);

        TestProgramBuilder.TypeParameterIndexer typeParameterIndexer = new TestProgramBuilder.TypeParameterIndexer(orgBench.options);

        FreeGenericsFinder freeGenericsFinder = new FreeGenericsFinder(typeToTest);

        List<String> allPaths = new TestCreator(nativeTypes, typeNames, typeToTest, orgBench, typeParameterIndexer, freeGenericsFinder).createTests(false).stream().map(Test::getPath).map(TestCreator::simplifyPath).collect(Collectors.toList());

        allPaths = allPaths.stream().filter(path -> !path.contains("[arg")).collect(Collectors.toList()); // TODO: For now we don't support first-order-functions here, because we are not sure which tests are required for the test to work.

        Trie trie = Trie.create(allPaths);
        List<String> paths = allPaths.stream().filter(Util.not(trie::containsChildren)).collect(Collectors.toList());

        int counter = 0;

        File dir = new File(smallDriversFolderPath);
        if (!dir.exists()) {
            boolean created = dir.mkdir();
            assert created;
        }

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (String path : paths) {
            Benchmark bench = orgBench.withPathsToTest(Collections.singletonList(path));

            int count = counter++;
            pool.submit(() -> {
                try {
                    System.out.println("Creating small driver for: " + path + "  " + (count + 1) + "/" + paths.size());

                    List<Test> specificTests = new TestCreator(nativeTypes, typeNames, typeToTest, bench, typeParameterIndexer, freeGenericsFinder).createTests();
                    specificTests.add(new LoadModuleTest(Main.getRequirePath(bench), typeToTest, bench));

                    Statement program = new TestProgramBuilder(bench, nativeTypes, typeNames, specificTests, typeToTest, typeParameterIndexer, freeGenericsFinder).buildTestProgram(null);

                    String filePath = Main.getFolderPath(bench) + SMALL_DRIVERS_FOLDER + "/" + SMALL_DRIVER_FILE_PREFIX + count + ".js";

                    Util.writeFile(filePath, AstToStringVisitor.toString(program));
                } catch (Throwable e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        }


        pool.shutdown();
        try {
            pool.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

    public static <T> List<T> runSmallDrivers(Benchmark benchmark, Function<String, T> runner) {
        return runSmallDrivers(benchmark, runner, DEFAULT_THREADS);
    }

    public static <T> List<T> runSmallDrivers(Benchmark benchmark, Function<String, T> runner, int threads) {
        if (benchmark.run_method != BROWSER) {
            String jsName = benchmark.jsFile.substring(benchmark.jsFile.lastIndexOf('/') + 1, benchmark.jsFile.length());
            try {
                Util.writeFile(Main.getFolderPath(benchmark) + RunSmall.SMALL_DRIVERS_FOLDER + "/" + jsName, Util.readFile(benchmark.jsFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        File smallFolders = new File(Main.getFolderPath(benchmark) + RunSmall.SMALL_DRIVERS_FOLDER);

        List<String> files = Arrays.stream(smallFolders.list()).filter(path -> path.contains(RunSmall.SMALL_DRIVER_FILE_PREFIX)).collect(Collectors.toList());

        ExecutorService pool = Executors.newFixedThreadPool(threads);

        List<T> result = Collections.synchronizedList(new ArrayList<T>());
        files.forEach(file -> result.add(null));

        int counter = 0;
        for (String file : files) {
            int count = counter++;
            pool.submit(() -> {
                System.out.println("Running " + count + " / " + files.size());

                String path = Main.getFolderPath(benchmark) + RunSmall.SMALL_DRIVERS_FOLDER + "/" + file;

                result.set(count, runner.apply(path));
            });
        }

        pool.shutdown();
        try {
            pool.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }

        return result;
    }

    public static Function<String, CoverageResult> runCoverage(Benchmark bench, int timeout) {
        return (path) -> {
            try {
                path = path.substring(Main.getFolderPath(bench).length());
                Map<String, CoverageResult> coverage = Main.genCoverage(bench, timeout, path);
                return coverage.get(bench.getJSName());
            } catch (TimeoutException e) {
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static Function<String, OutputParser.RunResult> runDriver(Benchmark.RUN_METHOD run_method, int timeout) {
        return (path) -> {
            try {
                return OutputParser.parseDriverResult(Main.runBenchmark(path, run_method, timeout));
            } catch (TimeoutException e) {
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
