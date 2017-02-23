package dk.webbies.tajscheck.test.experiments;

import dk.webbies.tajscheck.CoverageResult;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.RunSmall;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 16-01-2017.
 */
public class AutomaticExperiments {
    private static final int THREADS = 4;
    private static int SMALL_DRIVER_RUNS_LIMIT = 100;

    private static final Pair<String, Experiment.ExperimentSingleRunner> runSmall = new Pair<>("runSmall", (bench) -> {
        bench = bench.withOptions(bench.options.getBuilder().setCheckDepth(bench.options.checkDepth).setMaxIterationsToRun(1000).build());
        List<OutputParser.RunResult> results = RunSmall.runSmallDrivers(bench, RunSmall.runDriver(bench), SMALL_DRIVER_RUNS_LIMIT, Integer.MAX_VALUE);

        long paths = OutputParser.combine(results).typeErrors.stream().map(OutputParser.TypeError::getPath).distinct().count();

        return Long.toString(paths);
    });

    private static final Pair<String, Experiment.ExperimentSingleRunner> type = new Pair<>("type", (bench) -> bench.run_method.toString());

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> smallCoverage = new Pair<>(Arrays.asList("small-coverage(stmt)", "small-coverage(func)", "small-coverage(branches)"), (bench) -> {
        bench = bench.withOptions(bench.options.getBuilder().setCheckDepth(bench.options.checkDepth).setMaxIterationsToRun(1000).build());
        List<CoverageResult> results = RunSmall.runSmallDrivers(bench, RunSmall.runCoverage(bench), SMALL_DRIVER_RUNS_LIMIT, Integer.MAX_VALUE);

        CoverageResult result = CoverageResult.combine(results);
        if (result != null) {
            return Arrays.asList(Util.toPercentage(result.statementCoverage()), Util.toPercentage(result.functionCoverage()), Util.toPercentage(result.branchCoverage()));
        } else {
            return Arrays.asList(null, null, null);
        }
    });

    private static final Pair<String, Experiment.ExperimentSingleRunner> uniquePaths = new Pair<>("uniquePaths", (bench) -> {
        Main.writeFullDriver(bench);
        OutputParser.RunResult result = OutputParser.parseDriverResult(Main.runBenchmark(bench));
        long paths = result.typeErrors.stream().map(OutputParser.TypeError::getPath).distinct().count();
        return Long.toString(paths);
    });


    private static final Pair<String, Experiment.ExperimentSingleRunner> uniquePathsUnlimitedIterations = new Pair<>("uniquePathsUnlimited", (bench) -> {
        bench = bench.withOptions(bench.options.getBuilder().setMaxIterationsToRun(-1).build());
        Main.writeFullDriver(bench);
        OutputParser.RunResult result = OutputParser.parseDriverResult(Main.runBenchmark(bench));
        long paths = result.typeErrors.stream().map(OutputParser.TypeError::getPath).distinct().count();
        return Long.toString(paths);
    });

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsAndTime = new Pair<>(Arrays.asList("uniquePaths", "time"), (bench) -> {
        long start = System.currentTimeMillis();
        Main.writeFullDriver(bench);
        OutputParser.RunResult result = OutputParser.parseDriverResult(Main.runBenchmark(bench));
        long paths = result.typeErrors.stream().map(OutputParser.TypeError::getPath).distinct().count();
        long end = System.currentTimeMillis();
        double time = (end - start) / 1000.0;
        return Arrays.asList(Long.toString(paths), Util.toFixed(time, 1) + "s");
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

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsAndCoverage = new Pair<>(Arrays.asList("uniquePaths", "coverage(stmt)", "coverage(functions)", "coverage(branches)"), (bench) -> {
        String uniquePaths = AutomaticExperiments.uniquePaths.getRight().run(bench);
        if (uniquePaths == null) {
            return Arrays.asList(null, null, null, null);
        }

        Map<String, CoverageResult> out;
        try {
            out = Main.genCoverage(bench.withOptions(bench.options.getBuilder().setMaxTime(bench.options.maxTime * 5).build())); // <- More timeout
        } catch (Exception e) {
            System.out.println("Exception: " + e.getClass().getSimpleName() + " while doing coverage.");
            return Arrays.asList(uniquePaths, null, null, null);
        }

        assert out.containsKey(bench.getJSName());

        CoverageResult coverage = out.get(bench.getJSName());
        if (coverage == null) {
            return Arrays.asList(uniquePaths, null, null, null);
        }
        return Arrays.asList(uniquePaths, Util.toPercentage(coverage.statementCoverage()), Util.toPercentage(coverage.functionCoverage()), Util.toPercentage(coverage.branchCoverage()));
    });

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

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> driverSizes = new Pair<>(Arrays.asList("size", "size-no-generics"), (bench) -> {
        double DIVIDE_BY = 1000 * 1000;
        String SUFFIX = "mb";
        int DECIMALS = 1;

        String fullSize = null;
        if (!bench.options.disableGenerics) {
            fullSize = Util.toFixed(Main.generateFullDriver(bench).length() / DIVIDE_BY, DECIMALS) + SUFFIX;
        }

        double noGenerics = Main.generateFullDriver(bench.withOptions(bench.options.getBuilder().setDisableGenerics(true).build())).length() / DIVIDE_BY;

        return Arrays.asList(fullSize, Util.toFixed(noGenerics, DECIMALS) + SUFFIX);
    });

    private static final Pair<String, Experiment.ExperimentSingleRunner> jsFileSize = new Pair<>("jsFileSize", (bench) -> {
        double DIVIDE_BY = 1000 * 1000;
        String SUFFIX = "mb";
        int DECIMALS = 1;

        return Util.toFixed(Util.readFile(bench.jsFile).length() / DIVIDE_BY, DECIMALS) + SUFFIX;
    });

    public static void main(String[] args) throws Exception {
//        Experiment experiment = new Experiment(RunBenchmarks.benchmarks.entrySet().stream().filter(bench -> bench.getValue().run_method == Benchmark.RUN_METHOD.NODE).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
//        Experiment experiment = new Experiment(RunBenchmarks.benchmarks.entrySet().stream().filter(pair -> !done.contains(pair.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        Experiment experiment = new Experiment();

        experiment.addSingleExperiment(type);

        experiment.addMultiExperiment(driverSizes);
        experiment.addSingleExperiment(jsFileSize);

        experiment.addSingleExperiment(uniquePaths);
        experiment.addMultiExperiment(uniquePathsAndTime);
        experiment.addSingleExperiment(uniquePathsUnlimitedIterations);

        experiment.addMultiExperiment(uniquePathsAndCoverage);
        experiment.addMultiExperiment(uniquePathsAnd5Coverage);
        experiment.addMultiExperiment(uniquePathsConvergence);

//        experiment.addMultiExperiment(smallCoverage);
//        experiment.addSingleExperiment(runSmall);


        String result = experiment.calculate(THREADS).toCSV();
        System.out.println("\n\n\nResult: \n");
        System.out.println(result);

        Util.writeFile("experiment.csv", result);
    }
}
