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
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 16-01-2017.
 */
public class RunSmall {
    public static <T> List<T> runSmallDrivers(Benchmark orgBench, Function<String, T> runner) throws IOException {
        return runSmallDrivers(orgBench, runner, Integer.MAX_VALUE, 1);
    }

    public static <T> List<T> runSmallDrivers(Benchmark orgBench, Function<String, T> runner, int runsLimit, int collectionSizeLimit) throws IOException {
        assert runsLimit > 0;

        SpecReader spec = ParseDeclaration.getTypeSpecification(orgBench.environment, Collections.singletonList(orgBench.dTSFile));

        SpecReader emptySpec = ParseDeclaration.getTypeSpecification(orgBench.environment, new ArrayList<>());

        Set<Type> nativeTypes = TypesUtil.collectNativeTypes(spec, emptySpec);

        Map<Type, String> typeNames = ParseDeclaration.getTypeNamesMap(spec);

        Type typeToTest = Main.getTypeToTest(orgBench, spec);

        TestProgramBuilder.TypeParameterIndexer typeParameterIndexer = new TestProgramBuilder.TypeParameterIndexer(orgBench.options);

        FreeGenericsFinder freeGenericsFinder = new FreeGenericsFinder(typeToTest);

        List<String> allPaths = new TestCreator(nativeTypes, typeNames, typeToTest, orgBench, typeParameterIndexer, freeGenericsFinder).createTests(false).stream().map(Test::getPath).map(TestCreator::simplifyPath).collect(Collectors.toList());

        allPaths = allPaths.stream().filter(path -> !path.contains("[arg")).collect(Collectors.toList());

        Trie trie = Trie.create(allPaths);
        List<String> prefixFixedPaths = allPaths.stream().filter(Util.not(trie::containsChildren)).distinct().collect(Collectors.toList());
        Collections.shuffle(prefixFixedPaths);

        collectionSizeLimit = Math.max(1, Math.min(collectionSizeLimit, prefixFixedPaths.size() / runsLimit));

        Iterator<String> iterator = prefixFixedPaths.iterator();
        List<List<String>> paths = new ArrayList<>();
        while (iterator.hasNext()) {
            ArrayList<String> subCollection = new ArrayList<>();
            paths.add(subCollection);
            for (int i = 0; i < collectionSizeLimit; i++) {
                if (!iterator.hasNext()) {
                    break;
                }
                subCollection.add(iterator.next());
            }
        }

        if (paths.size() > runsLimit) {
            paths = paths.subList(0, runsLimit);
        }

        File dir = new File(Main.getFolderPath(orgBench));
        if (!dir.exists()) {
            boolean created = dir.mkdir();
            assert created;
        }

        List<T> result = new ArrayList<>();

        for (int i = 0; i < paths.size(); i++) {
            List<String> path = paths.get(i);

            Benchmark bench = orgBench.withPathsToTest(path);

            System.out.println("Creating small driver for: " + path + "  " + (i + 1) + "/" + paths.size());

            List<Test> specificTests = new TestCreator(nativeTypes, typeNames, typeToTest, bench, typeParameterIndexer, freeGenericsFinder).createTests();
            specificTests.add(new LoadModuleTest(Main.getRequirePath(bench), typeToTest, bench));

            Statement program = new TestProgramBuilder(bench, nativeTypes, typeNames, specificTests, typeToTest, typeParameterIndexer, freeGenericsFinder).buildTestProgram(null);

            String filePath = Main.getFolderPath(bench) + Main.TEST_FILE_NAME;

            Util.writeFile(filePath, AstToStringVisitor.toString(program));

            try {
                result.add(runner.apply(filePath));
            } catch (Throwable e) {
                System.out.println("Got exception: " + e + ", while running small driver...");
            }
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
                e.printStackTrace();
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