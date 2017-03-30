package dk.webbies.tajscheck.test.experiments;

import dk.webbies.tajscheck.CoverageResult;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.RunSmall;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.CheckOptions;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by erik1 on 16-01-2017.
 */
public class AutomaticExperiments {
    private static final int THREADS = 4;
    private static int SMALL_DRIVER_RUNS_LIMIT = 100;

    private static final Pair<String, Experiment.ExperimentSingleRunner> runSmall = new Pair<>("runSmall", (bench) -> {
        bench = bench.withOptions(bench.options.getBuilder().setCheckDepthUseValue(bench.options.checkDepthUseValue).setMaxIterationsToRun(1000).build());
        List<OutputParser.RunResult> results = RunSmall.runSmallDrivers(bench, RunSmall.runDriver(bench), SMALL_DRIVER_RUNS_LIMIT, Integer.MAX_VALUE);

        List<String> paths = OutputParser.combine(results).typeErrors.stream().map(OutputParser.TypeError::getPath).collect(Collectors.toList());

        int warnings = CountUniques.uniqueWarnings(paths, bench);

        return Integer.toString(warnings);
    });

    private static final Pair<String, Experiment.ExperimentSingleRunner> type = new Pair<>("type", (bench) -> bench.run_method.toString());

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> smallCoverage = new Pair<>(Arrays.asList("small-coverage(stmt)", "small-coverage(func)", "small-coverage(branches)"), (bench) -> {
        bench = bench.withOptions(bench.options.getBuilder().setCheckDepthUseValue(bench.options.checkDepthUseValue).setMaxIterationsToRun(1000).build());
        List<CoverageResult> results = RunSmall.runSmallDrivers(bench, RunSmall.runCoverage(bench), SMALL_DRIVER_RUNS_LIMIT, Integer.MAX_VALUE);

        CoverageResult result = CoverageResult.combine(results);
        if (result != null) {
            return Arrays.asList(Util.toPercentage(result.statementCoverage()), Util.toPercentage(result.functionCoverage()), Util.toPercentage(result.branchCoverage()));
        } else {
            return Arrays.asList(null, null, null);
        }
    });

    private static final Pair<String, Experiment.ExperimentSingleRunner> uniquePaths = uniquePathsWithOptions("", Function.identity());

    private static Pair<String, Experiment.ExperimentSingleRunner> uniquePathsWithOptions(String suffix, Function<CheckOptions, CheckOptions> func) {
        return new Pair<>("uniquePaths" + suffix, (bench) -> {
            bench = bench.withOptions(func);
            Main.writeFullDriver(bench);
            OutputParser.RunResult result = OutputParser.parseDriverResult(Main.runBenchmark(bench));

            List<String> paths = result.typeErrors.stream().map(OutputParser.TypeError::getPath).collect(Collectors.toList());

            int warnings = CountUniques.uniqueWarnings(paths, bench);

            return Integer.toString(warnings);
        });
    }

    private static final Pair<String, Experiment.ExperimentSingleRunner> soundnessTest = new Pair<>("sound", (bench) -> {
        Benchmark.RUN_METHOD runMethod = bench.run_method;
        bench = bench.withOptions(bench.options.getBuilder().setConstructAllTypes(true).setFailOnAny(false).build());

        // Performing a soundness check of the benchmark.
        Main.writeFullDriver(bench.withRunMethod(Benchmark.RUN_METHOD.BOOTSTRAP));
        String output = Main.runBenchmark(bench.withRunMethod(runMethod));
        OutputParser.RunResult result = OutputParser.parseDriverResult(output);
        return Boolean.toString(result.typeErrors.size() == 0);
    });

    private static final Pair<String, Experiment.ExperimentSingleRunner> uniquePaths5Minutes = uniquePathsWithOptions("(5minutes)", options -> options.getBuilder().setMaxTime(5 * 60 * 1000).build());

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsUnlimitedIterations = new Pair<>(Arrays.asList("uniquePaths(unlimited)", "time(unlimited)", "testsCalled(unlimited)", "totalTests(unlimited)", "testCoverage(unlimited)"), (bench) -> {
        bench = bench.withOptions(bench.options.getBuilder().setMaxIterationsToRun(-1).build());
        long start = System.currentTimeMillis();
        Main.writeFullDriver(bench);
        OutputParser.RunResult result = OutputParser.parseDriverResult(Main.runBenchmark(bench));

        List<String> paths = result.typeErrors.stream().map(OutputParser.TypeError::getPath).collect(Collectors.toList());

        int warnings = CountUniques.uniqueWarnings(paths, bench);

        long end = System.currentTimeMillis();
        double time = (end - start) / 1000.0;
//        return Arrays.asList(Long.toString(paths), Util.toFixed(time, 1) + "s");

        return Arrays.asList(
                Integer.toString(warnings),
                Util.toFixed(time, 1) + "s",
                Integer.toString(result.getTestsCalled().size()),
                Integer.toString(result.getTotalTests()),
                Util.toPercentage((result.getTestsCalled().size() * 1.0) / result.getTotalTests()
        ));
    });

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsAndTime = new Pair<>(Arrays.asList("uniquePaths", "time"), (bench) -> {
        long start = System.currentTimeMillis();
        Main.writeFullDriver(bench);
        OutputParser.RunResult result = OutputParser.parseDriverResult(Main.runBenchmark(bench));
        List<String> paths = result.typeErrors.stream().map(OutputParser.TypeError::getPath).collect(Collectors.toList());

        int warnings = CountUniques.uniqueWarnings(paths, bench);

        long end = System.currentTimeMillis();
        double time = (end - start) / 1000.0;
        return Arrays.asList(Integer.toString(warnings), Util.toFixed(time, 1) + "s");
    });


    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsTestCoverage = new Pair<>(Arrays.asList("uniquePaths", "testsRun", "totalTests", "testsCoverage"), (bench) -> {
        Main.writeFullDriver(bench);
        OutputParser.RunResult result = OutputParser.parseDriverResult(Main.runBenchmark(bench));
        List<String> paths = result.typeErrors.stream().map(OutputParser.TypeError::getPath).collect(Collectors.toList());

        int warnings = CountUniques.uniqueWarnings(paths, bench);

        return Arrays.asList(
                Integer.toString(warnings),
                Integer.toString(result.getTestsCalled().size()),
                Integer.toString(result.getTotalTests()),
                Util.toPercentage((result.getTestsCalled().size() * 1.0) / result.getTotalTests())
        );
    });

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePaths5TestCoverage = new Pair<>(Arrays.asList("uniquePaths(5)", "testsRun(5)", "totalTests(5)", "testsCoverage(5)"), (bench) -> {
        Main.writeFullDriver(bench);
        List<OutputParser.RunResult> results = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            results.add(OutputParser.parseDriverResult(Main.runBenchmark(bench)));
        }
        OutputParser.RunResult result = OutputParser.combine(results);
        List<String> paths = result.typeErrors.stream().map(OutputParser.TypeError::getPath).collect(Collectors.toList());

        int warnings = CountUniques.uniqueWarnings(paths, bench);

        return Arrays.asList(
                Long.toString(warnings),
                Integer.toString(result.getTestsCalled().size()),
                Integer.toString(result.getTotalTests()),
                Util.toPercentage((result.getTestsCalled().size() * 1.0) / result.getTotalTests())
        );
    });

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsConvergence = new Pair<>(Arrays.asList("uniquePaths", "uniquePathsConvergence", "iterationsUntilConvergence"), (bench) -> {
        Main.writeFullDriver(bench);
        OutputParser.RunResult result = OutputParser.parseDriverResult(Main.runBenchmark(bench));
        Set<String> paths = result.typeErrors.stream().map(OutputParser.TypeError::getPath).collect(Collectors.toSet());
        long firstPathCount = paths.size();
        System.out.println("Counted " + firstPathCount + " paths, trying to test again, to see if i get more");

        long prevCount = 0;
        long count = firstPathCount;
        int runs = 0;
        while (prevCount != count) {
            prevCount = count;
            result = OutputParser.parseDriverResult(Main.runBenchmark(bench));
            runs++;
            result.typeErrors.stream().map(OutputParser.TypeError::getPath).forEach(paths::add);
            count = paths.size();
            System.out.println("Previously had " + prevCount + " paths, now i have seen " + count);
        }


        return Arrays.asList(Long.toString(firstPathCount), Long.toString(count), Integer.toString(runs));
    });

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsAndCoverage = uniquePathsAndCoverage("", Function.identity());

    private static Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsAndCoverage(String suffix, Function<CheckOptions, CheckOptions> transformer) {
        return new Pair<>(Arrays.asList("uniquePaths" + suffix, "coverage(stmt)" + suffix, "coverage(functions)" + suffix, "coverage(branches)" + suffix), (bench) -> {
            bench = bench.withOptions(transformer);
            String uniquePaths = AutomaticExperiments.uniquePaths.getRight().run(bench);
            if (uniquePaths == null) {
                return Arrays.asList(null, null, null, null);
            }

            Map<String, CoverageResult> out;
            try {
                out = Main.genCoverage(bench.withOptions(bench.options.getBuilder().setMaxTime(bench.options.maxTime * 5).build())); // <- More timeout
                if (out.isEmpty()) {
                    return Arrays.asList(uniquePaths, null, null, null);
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getClass().getSimpleName() + " while doing coverage.");
                e.printStackTrace();
                return Arrays.asList(uniquePaths, null, null, null);
            }

            assert out.containsKey(bench.getJSName());

            CoverageResult coverage = out.get(bench.getJSName());
            if (coverage == null) {
                return Arrays.asList(uniquePaths, null, null, null);
            }
            return Arrays.asList(uniquePaths, Util.toPercentage(coverage.statementCoverage()), Util.toPercentage(coverage.functionCoverage()), Util.toPercentage(coverage.branchCoverage()));
        });
    };

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsAnd5Coverage = new Pair<>(Arrays.asList("uniquePaths", "coverage(stmt)", "coverage(functions)", "coverage(branches)", "5coverage(stmt)", "5coverage(functions)", "5coverage(branches)"), (bench) -> {
        String uniquePaths = AutomaticExperiments.uniquePaths.getRight().run(bench);
        if (uniquePaths == null) {
            return Arrays.asList(null, null, null, null, null, null, null);
        }

        Map<String, CoverageResult> out = new HashMap<>();

        CoverageResult firstCoverage = null;
        for (int i = 0; i < 5; i++) {
            try {
                Map<String, CoverageResult> subResult = Main.genCoverage(bench.withOptions(bench.options.getBuilder().setMaxTime(bench.options.maxTime * 5).build())); // <- More timeout
                out = CoverageResult.combine(out, subResult);
                if (out.get(bench.getJSName()) == null) {
                    return Arrays.asList(uniquePaths, null, null, null, null, null, null);
                }

                if (firstCoverage == null) {
                    firstCoverage = out.get(bench.getJSName());
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getClass().getSimpleName());
                if (firstCoverage != null) {
                    return Arrays.asList(uniquePaths, Util.toPercentage(firstCoverage.statementCoverage()), Util.toPercentage(firstCoverage.functionCoverage()), Util.toPercentage(firstCoverage.branchCoverage()), null, null, null);
                } else {
                    return Arrays.asList(uniquePaths, null, null, null, null, null, null);
                }
            }
        }

        assert out.containsKey(bench.getJSName());

        CoverageResult coverage = out.get(bench.getJSName());
        if (coverage == null) {
            if (firstCoverage == null) {
                return Arrays.asList(uniquePaths,
                        null, null, null,
                        null, null, null
                );
            }
            return Arrays.asList(uniquePaths,
                    Util.toPercentage(firstCoverage.statementCoverage()), Util.toPercentage(firstCoverage.functionCoverage()), Util.toPercentage(firstCoverage.branchCoverage()),
                    null, null, null
            );
        }
        return Arrays.asList(uniquePaths,
                Util.toPercentage(firstCoverage.statementCoverage()), Util.toPercentage(firstCoverage.functionCoverage()), Util.toPercentage(firstCoverage.branchCoverage()),
                Util.toPercentage(coverage.statementCoverage()), Util.toPercentage(coverage.functionCoverage()), Util.toPercentage(coverage.branchCoverage())
        );
    });

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> driverSizes(String suffix, Function<CheckOptions, CheckOptions> transformer) {
        return new Pair<>(Arrays.asList("size" + suffix, "size-no-generics" + suffix), (bench) -> {
            bench = bench.withOptions(transformer);
            double DIVIDE_BY = 1000 * 1000;
            String SUFFIX = "mb";
            int DECIMALS = 1;

            String fullSize = Util.toFixed(Main.generateFullDriver(bench).length() / DIVIDE_BY, DECIMALS) + SUFFIX;

            double noGenerics = Main.generateFullDriver(bench.withOptions(bench.options.getBuilder().setDisableGenerics(true).build())).length() / DIVIDE_BY;

            return Arrays.asList(fullSize, Util.toFixed(noGenerics, DECIMALS) + SUFFIX);
        });
    };

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> driverSizes = driverSizes("", Function.identity());



    private static final Pair<String, Experiment.ExperimentSingleRunner> jsFileSize = new Pair<>("jsFileSize", (bench) -> {
        double DIVIDE_BY = 1000 * 1000;
        String SUFFIX = "mb";
        int DECIMALS = 1;

        return Util.toFixed(Util.readFile(bench.jsFile).length() / DIVIDE_BY, DECIMALS) + SUFFIX;
    });

    public static void main(String[] args) throws Exception {
        /*Experiment experiment = new Experiment(RunBenchmarks.benchmarks.entrySet().stream()
//                .filter(bench -> bench.getValue().run_method == Benchmark.RUN_METHOD.NODE)
                        .filter(bench -> Stream.of("" )
                        .noneMatch(str -> str.equals(bench.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));*/
        Experiment experiment = new Experiment();

        experiment.addSingleExperiment(type);

//        experiment.addMultiExperiment(driverSizes);

        experiment.addSingleExperiment(uniquePaths);

//        experiment.addMultiExperiment(driverSizes("-0", options -> options.getBuilder().setCheckDepthUseValue(0).build()));
//        experiment.addMultiExperiment(driverSizes("-1", options -> options.getBuilder().setCheckDepthUseValue(1).build()));
//        experiment.addMultiExperiment(driverSizes("-2", options -> options.getBuilder().setCheckDepthUseValue(2).build()));
//        experiment.addMultiExperiment(driverSizes("-3", options -> options.getBuilder().setCheckDepthUseValue(3).build()));
//        experiment.addMultiExperiment(driverSizes("-4", options -> options.getBuilder().setCheckDepthUseValue(4).build()));
//        experiment.addSingleExperiment(jsFileSize);

//        experiment.addSingleExperiment(uniquePaths);
//        experiment.addSingleExperiment(uniquePathsWithOptions("prim", options -> options.getBuilder().setWritePrimitives(true).build()));
//        experiment.addSingleExperiment(uniquePathsWithOptions("all", options -> options.getBuilder().setWriteAll(true).build()));
/*
        experiment.addMultiExperiment(uniquePathsAndCoverage("(1)", options -> options.getBuilder().setMaxIterationsToRun(1).build()));
        experiment.addMultiExperiment(uniquePathsAndCoverage);
        experiment.addMultiExperiment(uniquePathsAndCoverage("(prim)", options -> options.getBuilder().setWritePrimitives(true).build()));
        experiment.addMultiExperiment(uniquePathsAndCoverage("(all)", options -> options.getBuilder().setWriteAll(true).build()));*/


//        experiment.addSingleExperiment(soundnessTest);

//        experiment.addSingleExperiment(uniquePaths5Minutes);

//        experiment.addMultiExperiment(uniquePathsDepth(0));
//        experiment.addMultiExperiment(uniquePathsDepth(1));
//        experiment.addMultiExperiment(uniquePathsDepth(2));
//        experiment.addMultiExperiment(uniquePathsDepth(3));

        /*experiment.addSingleExperiment(uniquePaths);
        experiment.addMultiExperiment(uniquePathsTestCoverage);
        experiment.addMultiExperiment(uniquePaths5TestCoverage);
        experiment.addMultiExperiment(uniquePathsUnlimitedIterations);
        experiment.addMultiExperiment(uniquePathsAndTime);

        experiment.addMultiExperiment(uniquePathsConvergence);

        experiment.addMultiExperiment(uniquePathsAndCoverage);
        experiment.addMultiExperiment(uniquePathsAnd5Coverage);*/

//        experiment.addMultiExperiment(smallCoverage);
//        experiment.addSingleExperiment(runSmall);


        String result = experiment.calculate(THREADS).toCSV();
        System.out.println("\n\n\nResult: \n");
        System.out.println(result);

        Util.writeFile("experiment.csv", result);

        System.exit(0); // It would shut down by itself after a little, but I don't wanna wait.
    }
}
