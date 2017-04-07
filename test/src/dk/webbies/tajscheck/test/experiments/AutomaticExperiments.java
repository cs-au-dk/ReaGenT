package dk.webbies.tajscheck.test.experiments;

import dk.webbies.tajscheck.CoverageResult;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.RunSmall;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.CheckOptions;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePaths = uniquePathsWithOptions("", 1, false, Function.identity());

    private static Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsWithOptions(String suffix, int repetitions, boolean reportTime, Function<CheckOptions, CheckOptions> func) {
        List<String> names;
        if (reportTime) {
            names = Arrays.asList("uniquePaths" + suffix, "time" + suffix);
        } else {
            names = Collections.singletonList("uniquePaths" + suffix);
        }
        return new Pair<>(names, (bench) -> {
            bench = bench.withOptions(func);
            Main.writeFullDriver(bench);
            List<OutputParser.RunResult> results = new ArrayList<>();

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < repetitions; i++) {
                results.add(OutputParser.parseDriverResult(Main.runBenchmark(bench)));
            }

            long time = System.currentTimeMillis() - startTime;

            OutputParser.RunResult result = OutputParser.combine(results);

            List<String> paths = result.typeErrors.stream().map(OutputParser.TypeError::getPath).collect(Collectors.toList());

            int warnings = CountUniques.uniqueWarnings(paths, bench);

            if (reportTime) {
                return Arrays.asList(Integer.toString(warnings), Util.toFixed(time / 1000.0, 2, ',') + "s");
            } else {
                return Collections.singletonList(Integer.toString(warnings));
            }
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

    private static Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsTestCoverage(int runs) {
        return new Pair<>(Arrays.asList("uniquePaths(" + runs + ")", "testsRun(" + runs + ")", "totalTests(" + runs + ")", "testsCoverage(" + runs + ")"), (bench) -> {
            Main.writeFullDriver(bench);
            List<OutputParser.RunResult> results = new ArrayList<>();
            for (int i = 0; i < runs; i++) {
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
    };

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

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> coverage = coverage("", Function.identity());

    private static Pair<List<String>, Experiment.ExperimentMultiRunner> coverage(String suffix, Function<CheckOptions, CheckOptions> transformer) {
        return new Pair<>(Arrays.asList("coverage(stmt)" + suffix, "coverage(functions)" + suffix, "coverage(branches)" + suffix), (bench) -> {
            bench = bench.withOptions(transformer);

            Map<String, CoverageResult> out;
            try {
                out = Main.genCoverage(bench.withOptions(bench.options.getBuilder().setMaxTime(bench.options.maxTime * 5).build())); // <- More timeout, instrumented code is slower.
                if (out.isEmpty()) {
                    return Arrays.asList(null, null, null);
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getClass().getSimpleName() + " while doing coverage.");
                e.printStackTrace();
                return Arrays.asList(null, null, null);
            }

            assert out.containsKey(bench.getJSName());

            CoverageResult coverage = out.get(bench.getJSName());
            if (coverage == null) {
                return Arrays.asList(null, null, null);
            }
            return Arrays.asList(Util.toPercentage(coverage.statementCoverage()), Util.toPercentage(coverage.functionCoverage()), Util.toPercentage(coverage.branchCoverage()));
        });
    };

    private static Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsAndCoverage(int runs) {
        return new Pair<>(Arrays.asList("uniquePaths", "coverage(stmt)", "coverage(functions)", "coverage(branches)", runs + "coverage(stmt)", runs + "coverage(functions)", runs + "coverage(branches)"), (bench) -> {
            String uniquePaths = AutomaticExperiments.uniquePaths.getRight().run(bench).get(0);
            if (uniquePaths == null) {
                return Arrays.asList(null, null, null, null, null, null, null);
            }

            Map<String, CoverageResult> out = new HashMap<>();

            String driver = Main.generateFullDriver(bench.withOptions(options -> options.getBuilder().setCheckDepthReport(options.checkDepthUseValue).build()));

            CoverageResult firstCoverage = null;
            for (int i = 0; i < runs; i++) {
                try {
                    Util.writeFile(Main.getFolderPath(bench) + Main.TEST_FILE_NAME, driver);

                    Map<String, CoverageResult> subResult = Main.genCoverage(bench.withOptions(bench.options.getBuilder().setMaxTime(bench.options.maxTime * 5).build()), false); // <- more timeout
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
    }

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
        if (args.length > 0) {
            String experimentToRun = args[0];

            Experiment experiment = new Experiment();

            System.out.println("Running experiments for " + experimentToRun.toUpperCase());

            switch (experimentToRun.toUpperCase()) {
                case "RQ1": // How much does it find, and how long does it take.
                    experiment.addMultiExperiment(uniquePathsWithOptions("(10s)", 1, false, options -> options.getBuilder().setMaxIterationsToRun(-1).setMaxTime(10 * 1000).build()));

                    experiment.addMultiExperiment(uniquePathsWithOptions("(5x:10s)", 5, false, options -> options.getBuilder().setMaxIterationsToRun(-1).setMaxTime(10 * 1000).build()));
                    experiment.addMultiExperiment(uniquePathsWithOptions("(10x:10s)", 10, false, options -> options.getBuilder().setMaxIterationsToRun(-1).setMaxTime(10 * 1000).build()));
                    experiment.addMultiExperiment(uniquePathsWithOptions("(20x:10s)", 20, false, options -> options.getBuilder().setMaxIterationsToRun(-1).setMaxTime(10 * 1000).build()));


                    experiment.addMultiExperiment(uniquePathsWithOptions("(1min/10000)", 1, false, options -> options.getBuilder().setMaxIterationsToRun(10000).setMaxTime(60 * 1000).build()));

//                    experiment.addMultiExperiment(uniquePathsWithOptions("(5 times: 1 minute timeout, or 10.000 tests)", 5, false, Function.identity()));

                    experiment.addMultiExperiment(uniquePathsWithOptions("(1min)", 1, false, options -> options.getBuilder().setMaxIterationsToRun(-1).setMaxTime(60 * 1000).build()));

                    experiment.addMultiExperiment(uniquePathsWithOptions("(5x:1min)", 5, false, options -> options.getBuilder().setMaxIterationsToRun(-1).setMaxTime(60 * 1000).build()));

                    experiment.addMultiExperiment(uniquePathsWithOptions("(5min)", 1, false, options -> options.getBuilder().setMaxIterationsToRun(-1).setMaxTime(5 * 60 * 1000).build()));

                    break;
                case "RQ2": // Which configuration works the best.

                    experiment.addMultiExperiment(uniquePathsWithOptions("(standard config)", 1, false, Function.identity()));
                    experiment.addMultiExperiment(uniquePathsWithOptions("(5x:standard config)", 1, false, Function.identity()));

                    experiment.addMultiExperiment(uniquePathsWithOptions("(not combining type parameters)", 1, false, options -> options.getBuilder().setCombineAllUnboundGenerics(false).build()));
                    experiment.addMultiExperiment(uniquePathsWithOptions("(5x:not combining type parameters)", 5, false, options -> options.getBuilder().setCombineAllUnboundGenerics(false).build()));

                    experiment.addMultiExperiment(uniquePathsWithOptions("(no feedback with partially incorrect values)", 1, false, options -> options.getBuilder().setCheckDepthUseValue(options.checkDepthReport).build()));
                    experiment.addMultiExperiment(uniquePathsWithOptions("(5xno feedback with partially incorrect values)", 5, false, options -> options.getBuilder().setCheckDepthUseValue(options.checkDepthReport).build()));

                    experiment.addMultiExperiment(uniquePathsWithOptions("(writing primitive properties)", 1, false, options -> options.getBuilder().setWritePrimitives(true).build()));
                    experiment.addMultiExperiment(uniquePathsWithOptions("(5x:writing primitive properties)", 5, false, options -> options.getBuilder().setWritePrimitives(true).build()));

                    experiment.addMultiExperiment(uniquePathsWithOptions("(writing all properties)", 1, false, options -> options.getBuilder().setWriteAll(true).build()));
                    experiment.addMultiExperiment(uniquePathsWithOptions("(5x:writing all properties)", 5, false, options -> options.getBuilder().setWriteAll(true).build()));
                    break;
                case "RQ5": // Coverage tests.
                    experiment.addMultiExperiment(coverage("-init", options -> options.getBuilder().setMaxIterationsToRun(1).build())); // Runs exactly one test, which can only be the test that initializes the library.

                    experiment.addMultiExperiment(uniquePathsAndCoverage(5));

                    experiment.addMultiExperiment(uniquePathsAndCoverage(10));

                    experiment.addMultiExperiment(uniquePathsAndCoverage(20));

                    experiment.addMultiExperiment(uniquePathsTestCoverage(1));
                    experiment.addMultiExperiment(uniquePathsTestCoverage(5));
                    experiment.addMultiExperiment(uniquePathsTestCoverage(10));
                    experiment.addMultiExperiment(uniquePathsTestCoverage(20));

                    break;
                default:
                    throw new RuntimeException("Unknown experiment: " + experimentToRun);
            }

            String result = experiment.calculate(THREADS).toCSV();
            System.out.println("\n\n\nResult: \n");
            System.out.println(result);

            Util.writeFile("experiment.csv", result);

            System.out.println("\nThe results have also been written to the file experiment.csv");
            System.exit(0);
            return;
        }


/*        Experiment experiment = new Experiment(RunBenchmarks.benchmarks.entrySet().stream()
                .filter(bench -> bench.getValue().run_method == Benchmark.RUN_METHOD.NODE)
                        .filter(bench -> Stream.of("" )
                        .noneMatch(str -> str.equals(bench.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));*/
        Experiment experiment = new Experiment();

//        experiment.addSingleExperiment(type);

        experiment.addMultiExperiment(uniquePaths);
//        experiment.addMultiExperiment(uniquePathsAnd5Coverage);
//        experiment.addMultiExperiment(uniquePathsTestCoverage);
//        experiment.addMultiExperiment(uniquePaths5TestCoverage);

        String result = experiment.calculate(THREADS).toCSV();
        System.out.println("\n\n\nResult: \n");
        System.out.println(result);

        Util.writeFile("experiment.csv", result);

        System.exit(0); // It would shut down by itself after a little, but I don't wanna wait.
    }
}
