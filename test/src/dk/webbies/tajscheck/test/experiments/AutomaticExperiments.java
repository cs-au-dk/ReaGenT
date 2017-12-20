package dk.webbies.tajscheck.test.experiments;

import dk.webbies.tajscheck.CoverageResult;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.RunSmall;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.benchmark.options.OptionsI;
import dk.webbies.tajscheck.paser.AST.BinaryExpression;
import dk.webbies.tajscheck.paser.AST.BlockStatement;
import dk.webbies.tajscheck.paser.AST.NodeTransverse;
import dk.webbies.tajscheck.paser.AST.Operator;
import dk.webbies.tajscheck.paser.AstBuilder;
import dk.webbies.tajscheck.paser.AstToStringVisitor;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 16-01-2017.
 */
public class AutomaticExperiments {
    private static final int THREADS = 3;
    private static int SMALL_DRIVER_RUNS_LIMIT = 100;

    private static final Pair<String, Experiment.ExperimentSingleRunner> runSmall = new Pair<>("runSmall", (bench) -> {
        bench = bench.withOptions(bench.options.getBuilder().setCheckDepthUseValue(bench.options.dynamicOptions.checkDepthUseValue).setMaxIterationsToRun(1000));
        List<OutputParser.RunResult> results = RunSmall.runSmallDrivers(bench, RunSmall.runDriver(bench), SMALL_DRIVER_RUNS_LIMIT, Integer.MAX_VALUE);

        List<OutputParser.TypeError> paths = OutputParser.combine(results).typeErrors.stream().collect(Collectors.toList());

        int warnings = CountUniques.uniqueWarnings(paths, bench);

        return Integer.toString(warnings);
    });

    public static final Pair<String, Experiment.ExperimentSingleRunner> type = new Pair<>("type", (bench) -> bench.run_method.toString());

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> smallCoverage = new Pair<>(Arrays.asList("small-coverage(stmt)", "small-coverage(func)", "small-coverage(branches)"), (bench) -> {
        bench = bench.withOptions(bench.options.getBuilder().setCheckDepthUseValue(bench.options.dynamicOptions.checkDepthUseValue).setMaxIterationsToRun(1000));
        List<CoverageResult> results = RunSmall.runSmallDrivers(bench, RunSmall.runCoverage(bench), SMALL_DRIVER_RUNS_LIMIT, Integer.MAX_VALUE);

        CoverageResult result = CoverageResult.combine(results);
        if (result != null) {
            return Arrays.asList(Util.toPercentage(result.statementCoverage()), Util.toPercentage(result.functionCoverage()), Util.toPercentage(result.branchCoverage()));
        } else {
            return Arrays.asList(null, null, null);
        }
    });

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> onlyFoundWhenConstructingClasses = new Pair<>(Arrays.asList("onlyClasses"), (Benchmark bench) -> {
        bench = bench.withOptions(options -> options.setConstructClassInstances(true).setConstructClassTypes(true));

        Main.generateFullDriver(bench);

        MultiMap<String, OutputParser.TypeError> mismatchCount = new ArrayListMultiMap<>();
        for (int i = 0; i < 10; i++) {
            OutputParser.RunResult subResult = OutputParser.parseDriverResult(Main.runBenchmark(bench));
            for (OutputParser.TypeError typeError : subResult.typeErrors) {
                mismatchCount.put(typeError.getPath(), typeError);
            }
        }

        bench = bench.withOptions(options -> options.setConstructClassInstances(false).setConstructClassTypes(false));
        Main.generateFullDriver(bench);

        for (Map.Entry<String, Collection<OutputParser.TypeError>> entry : new HashMap<>(mismatchCount.asMap()).entrySet()) {
            if (entry.getValue().size() < 5) {
                mismatchCount.remove(entry.getKey());
            }
        }


        for (int i = 0; i < 10; i++) {
            if (mismatchCount.isEmpty()) {
                break;
            }
            OutputParser.RunResult subResult = OutputParser.parseDriverResult(Main.runBenchmark(bench));
            for (OutputParser.TypeError typeError : subResult.typeErrors) {
                mismatchCount.remove(typeError.getPath());
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(bench.getJSName()).append("\n");

        for (Collection<OutputParser.TypeError> errors : mismatchCount.asMap().values()) {
            OutputParser.TypeError error = errors.iterator().next();
            stringBuilder.append(error.toString()).append("\n");
        }

        stringBuilder.append("\n\n");

        synchronized (Util.class) {
            Util.append("classMismatches.txt", stringBuilder.toString());
        }

        int uniques = CountUniques.uniqueWarnings(mismatchCount.asMap().values().stream().map(ArrayList::new).reduce(new ArrayList<>(), Util::reduceList), bench);

        return Arrays.asList(Integer.toString(uniques));
    });

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> hasInstanceOf = new Pair<>(Arrays.asList("hasInstanceOf", "instanceOfCount"), (Benchmark bench) -> {
        BlockStatement stmt = AstBuilder.stmtFromString(Util.readFile(bench.jsFile));

        AtomicInteger hasInstanceOf = new AtomicInteger(0);

        List<String> blacklist = Arrays.asList("RegExp", "Element", "Object", "SVGElement", "Promise", "Error", "TypeError", "window.DOMException", "Array", "Date", "HTML", "Function");
        List<String> whileList = Arrays.asList("Texture", "partialFn", "React", "bound", "MiniSignalBinding", "RC4Random", "Socket", "Application", "able", "Subscriber", "Peer", "MediaConnection", "DataConnection", "Lodash", "Dom7", "core", "Assert", "PDF", "Descriptor", "Mixin", "Hsl", "VNode", "Url", "_", "fn", "class", "Class", "Klass", "Reliable", "L.", "Swiper", "$", "CodeMirror", "Doc", "Pos", "ember", "Lazy", "List", "Ember", "this", "Constructor", "Map", "Set", "Duration", "JQ", "Model", "Color", "color", "Hcl", "Lab", "Cubehelix", "Rgb", "Transition");

        stmt.accept(new NodeTransverse<Void>() {
            @Override
            public Void visit(BinaryExpression expression) {
                if (expression.getOperator() == Operator.INSTANCEOF) {
                    String right = AstToStringVisitor.toString(expression.getRhs(), true);
                    if (blacklist.stream().anyMatch(right::contains)) {
                        return null;
                    }
                    if (whileList.stream().anyMatch(right::contains)) {
                        hasInstanceOf.incrementAndGet();
                        return null;
                    }
                    hasInstanceOf.incrementAndGet();
                }
                return null;
            }
        });

        return Arrays.asList(hasInstanceOf.get() > 0 ? "1" : "0", Integer.toString(hasInstanceOf.get()));
    });


    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePaths = uniquePathsWithOptions("", 1, false, opt -> opt);

    private static Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsWithOptions(String suffix, int repetitions, boolean reportTime, Function<CheckOptions.Builder, OptionsI.Builder> func) {
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

            List<OutputParser.TypeError> errors = result.typeErrors;

            int warnings = CountUniques.uniqueWarnings(errors, bench);

            if (reportTime) {
                return Arrays.asList(Integer.toString(warnings), Util.toFixed(time / 1000.0, 2, ',') + "s");
            } else {
                return Collections.singletonList(Integer.toString(warnings));
            }
        });
    }

    private static final Pair<String, Experiment.ExperimentSingleRunner> soundnessTest = new Pair<>("sound", (bench) -> {
        Benchmark.RUN_METHOD runMethod = bench.run_method;
        bench = bench.withOptions(bench.options.getBuilder().setConstructAllTypes(true).setFailOnAny(false));

        // Performing a soundness check of the benchmark.
        Main.writeFullDriver(bench.withRunMethod(Benchmark.RUN_METHOD.BOOTSTRAP));
        String output = Main.runBenchmark(bench.withRunMethod(runMethod));
        OutputParser.RunResult result = OutputParser.parseDriverResult(output);
        return Boolean.toString(result.typeErrors.size() == 0);
    });

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsUnlimitedIterations = new Pair<>(Arrays.asList("uniquePaths(unlimited)", "time(unlimited)", "testsCalled(unlimited)", "totalTests(unlimited)", "testCoverage(unlimited)"), (bench) -> {
        bench = bench.withOptions(bench.options.getBuilder().setMaxIterationsToRun(-1));
        long start = System.currentTimeMillis();
        Main.writeFullDriver(bench);
        OutputParser.RunResult result = OutputParser.parseDriverResult(Main.runBenchmark(bench));

        List<OutputParser.TypeError> errors = result.typeErrors;

        int warnings = CountUniques.uniqueWarnings(errors, bench);

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
            List<OutputParser.TypeError> errors = result.typeErrors;

            int warnings = CountUniques.uniqueWarnings(errors, bench);

            return Arrays.asList(
                    Long.toString(warnings),
                    Integer.toString(result.getTestsCalled().size()),
                    Integer.toString(result.getTotalTests()),
                    Util.toPercentage((result.getTestsCalled().size() * 1.0) / result.getTotalTests())
            );
        });
    }

    ;

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

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> coverage = coverage("", opt -> opt);

    private static Pair<List<String>, Experiment.ExperimentMultiRunner> coverage(String suffix, Function<CheckOptions.Builder, OptionsI.Builder> transformer) {
        return new Pair<>(Arrays.asList("coverage(stmt)" + suffix, "coverage(functions)" + suffix, "coverage(branches)" + suffix), (bench) -> {
            bench = bench.withOptions(transformer);

            Map<String, CoverageResult> out;
            try {
                out = Main.genCoverage(bench.withOptions(bench.options.getBuilder().setMaxTime(bench.options.dynamicOptions.maxTime * 5))); // <- More timeout, instrumented code is slower.
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
    }

    ;

    private static Pair<List<String>, Experiment.ExperimentMultiRunner> uniquePathsAndCoverage(int runs) {
        return new Pair<>(Arrays.asList("uniquePaths", "coverage(stmt)", "coverage(functions)", "coverage(branches)", runs + "coverage(stmt)", runs + "coverage(functions)", runs + "coverage(branches)"), (bench) -> {
            String uniquePaths = AutomaticExperiments.uniquePaths.getRight().run(bench).get(0);
            if (uniquePaths == null) {
                return Arrays.asList(null, null, null, null, null, null, null);
            }
            bench = bench.withOptions(options -> options.setCompactOutput(false));

            Map<String, CoverageResult> out = new HashMap<>();

            String driver = Main.generateFullDriver(bench.withOptions(options -> options.setCheckDepthReport(options.dynamicOptions.checkDepthUseValue))).getRight();

            CoverageResult firstCoverage = null;
            for (int i = 0; i < runs; i++) {
                try {
                    Util.writeFile(Main.getFolderPath(bench) + Main.TEST_FILE_NAME, driver);

                    Map<String, CoverageResult> subResult = Main.genCoverage(bench.withOptions(bench.options.getBuilder().setMaxTime(bench.options.dynamicOptions.maxTime * 5)), false); // <- more timeout
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

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> driverSizes(String suffix, Function<CheckOptions.Builder, OptionsI.Builder> transformer) {
        return new Pair<>(Arrays.asList("size" + suffix, "size-no-generics" + suffix), (bench) -> {
            bench = bench.withOptions(transformer);
            double DIVIDE_BY = 1000 * 1000;
            String SUFFIX = "mb";
            int DECIMALS = 1;

            String fullSize = Util.toFixed(Main.generateFullDriver(bench).getRight().length() / DIVIDE_BY, DECIMALS) + SUFFIX;

            double noGenerics = Main.generateFullDriver(bench.withOptions(bench.options.getBuilder().setDisableGenerics(true))).getRight().length() / DIVIDE_BY;

            return Arrays.asList(fullSize, Util.toFixed(noGenerics, DECIMALS) + SUFFIX);
        });
    }

    private static final Pair<List<String>, Experiment.ExperimentMultiRunner> driverSizes = driverSizes("", opt -> opt);


    private static final Pair<String, Experiment.ExperimentSingleRunner> jsFileSize = new Pair<>("jsFileSize", (bench) -> {
        double DIVIDE_BY = 1000 * 1000;
        String SUFFIX = "mb";
        int DECIMALS = 1;

        return Util.toFixed(Util.readFile(bench.jsFile).length() / DIVIDE_BY, DECIMALS) + SUFFIX;
    });

    private static final Pair<String, Experiment.ExperimentSingleRunner> usesUnsoundness = new Pair<>("usesUnsoundness", (bench) -> {
        bench = bench.withOptions(options -> options.setDisableGenerics(false));
        if (bench.options.disableGenerics) {
            return "-";
        }
        try {
            Main.generateFullDriver(bench);
            return "false";
        } catch (IllegalArgumentException e) {
            return "true"; // TODO: Doesn't throw this exception currently.
        }
    });

    public static void main(String[] args) throws Exception {
        /*Experiment experiment = new Experiment(RunBenchmarks.benchmarks.entrySet().stream()
//                .filter(bench -> bench.getValue().run_method == Benchmark.RUN_METHOD.NODE)
//                .filter(bench -> Stream.of("")
//                    .noneMatch(str -> str.equals(bench.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));*/
        Experiment experiment = new Experiment();

        experiment.addSingleExperiment(type);

        String result = experiment.calculate(THREADS, "experiment.csv").toCSV();
        System.out.println("\n\n\nResult: \n");
        System.out.println(result);

        System.exit(0); // It would shut down by itself after a little, but I don't wanna wait.
    }
}
