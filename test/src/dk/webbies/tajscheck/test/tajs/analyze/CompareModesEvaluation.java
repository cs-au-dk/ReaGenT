package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions;
import dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy.ExpandImmediatelyPolicy;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.experiments.Experiment;
import dk.webbies.tajscheck.test.experiments.Table;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompareModesEvaluation {
    static final List<String> benchmarksToEvaluate = Arrays.asList(
            "classnames",
            "component-emitter",
            "js-cookie",
            "loglevel",
            "mime",
            "platform",
            "pathjs",
            "PleaseJS",
            "pluralize",
            "uuid"
    );

    public static final Map<String, Pair<Function<Benchmark, Benchmark>, Function<CheckOptions.Builder, StaticOptions.Builder>>> modes = new LinkedHashMap<>(){{

        put("all-assumptions", new Pair<>(Function.identity(), options -> options.staticOptions));

        put("width-subtyping", new Pair<>(Function.identity(), options -> options.staticOptions.setProperWidthSubtyping(true).setWidthSubtpyingIncludesAllObjects(true)));
        put("no-safe-strings", new Pair<>(Function.identity(), options -> options.staticOptions.setBetterAnyString(false)));
        put("no-prefer-lib-values", new Pair<>(Function.identity(), options -> options.staticOptions.setExpansionPolicy(new ExpandImmediatelyPolicy()).setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.MIX_FEEDBACK_AND_CONSTRUCTED)));


        put("no-assumptions", new Pair<>(Function.identity(),
                options ->
                        options.staticOptions
                                .setProperWidthSubtyping(true)
                                .setBetterAnyString(false)
                                .setExpansionPolicy(new ExpandImmediatelyPolicy()).setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.MIX_FEEDBACK_AND_CONSTRUCTED)
                )
        );
        put("MGC", new Pair<>(Function.identity(), options ->
                options
                        .setWriteAll(true) // this is part of required assumption. Now we break it.
                        .staticOptions
                        .setWidthSubtpyingIncludesAllObjects(true) // destroy everything!
                        .setProperWidthSubtyping(true)
                        .setBetterAnyString(false)
                        .setExpansionPolicy(new ExpandImmediatelyPolicy()).setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.MIX_FEEDBACK_AND_CONSTRUCTED)
                        .setIgnoreTypeDecs(true) // making it quite close to an MGC.
                )
        );

        put("all-assumptions-fixed", new Pair<>(Benchmark::possilyPatched, options -> options.staticOptions));
        put("no-assumptions-fixed", new Pair<>(Benchmark::possilyPatched,
                        options ->
                                options.staticOptions
                                        .setProperWidthSubtyping(true)
                                        .setBetterAnyString(false)
                                        .setExpansionPolicy(new ExpandImmediatelyPolicy()).setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.MIX_FEEDBACK_AND_CONSTRUCTED)
                )
        );
        put("MGC-fixed", new Pair<>(Benchmark::possilyPatched, options ->
                        options
                                .setWriteAll(true) // this is part of required assumption. Now we break it.
                                .staticOptions
                                .setWidthSubtpyingIncludesAllObjects(true) // destroy everything!
                                .setProperWidthSubtyping(true)
                                .setBetterAnyString(false)
                                .setExpansionPolicy(new ExpandImmediatelyPolicy()).setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.MIX_FEEDBACK_AND_CONSTRUCTED)
                                .setIgnoreTypeDecs(true) // making it quite close to an MGC.
                )
        );
    }};


    @Test
    public void doEvaluation() {
        Experiment experiment = new Experiment(benchmarksToEvaluate.stream().map(RunBenchmarks.benchmarks::get).collect(Collectors.toList()));

        modes.forEach((name, options) -> {
            experiment.addExperiment(experiment(name, options));
        });

        Table table = experiment.calculate("compareModes.csv");

        printPaperTable(table);
    }

    @Test
    public void allAssumptions() {
        Experiment experiment = new Experiment(benchmarksToEvaluate.stream().map(RunBenchmarks.benchmarks::get).collect(Collectors.toList()));

        experiment.addExperiment(experiment("all-assumptions", modes.get("all-assumptions")));

        Table table = experiment.calculate("fixed.csv");

        printPaperTable(table);
    }

    @Test
    public void allAssumptionsOnFixed() {
        Experiment experiment = new Experiment(benchmarksToEvaluate.stream().map(RunBenchmarks.benchmarks::get).map(Benchmark::possilyPatched).collect(Collectors.toList()));

        experiment.addExperiment(experiment("all-assumptions", modes.get("all-assumptions")));

        Table table = experiment.calculate("fixed.csv");

        printPaperTable(table);
    }

    /*
    Comparison of RMGC variants. Each column contains: action-coverage / violations / time / scales
    Library|no-assumptions
    Classnames|50,0% / 0 / 9,4 / NO
    component-emitter|47,8% / 0 / 3,1 / NO
    js-cookie|100,0% / 2 / 66,2 / YES
    loglevel|100,0% / 7 / 40,4 / YES
    Mime|100,0% / 0 / 118,8 / YES
    PathJS|91,5% / 5 / 1644,8 / NO
    Platform.js|100,0% / 0 / 181,3 / YES
    PleaseJS|100,0% / 6 / 154,4 / YES
    pluralize|100,0% / 0 / 34,1 / YES
    uuid|100,0% / 6 / 109,0 / YES
    Average|88.92999999999999 / 2.6 / 236.15 / 0.0
     */
    @Test
    public void noAssumptionsOnFixed() {
        Experiment experiment = new Experiment(benchmarksToEvaluate.stream().map(RunBenchmarks.benchmarks::get).map(Benchmark::possilyPatched).collect(Collectors.toList()));

        experiment.addExperiment(experiment("no-assumptions", modes.get("no-assumptions")));

        Table table = experiment.calculate("fixed-no-assumptions.csv");

        printPaperTable(table);
    }


    @Test // no extra violations.
    public void noPreferLibValueOnFixed() {
        Experiment experiment = new Experiment(benchmarksToEvaluate.stream().map(RunBenchmarks.benchmarks::get).map(Benchmark::possilyPatched).collect(Collectors.toList()));

        experiment.addExperiment(experiment("no-prefer-lib-values", modes.get("no-prefer-lib-values")));

        Table table = experiment.calculate("fixed-no-prefer-lib.csv");

        printPaperTable(table);
    }

    @Test
    public void mgcOnFixed() {
        Experiment experiment = new Experiment(benchmarksToEvaluate.stream().map(RunBenchmarks.benchmarks::get).map(Benchmark::possilyPatched).collect(Collectors.toList()));

        experiment.addExperiment(experiment("MGC", modes.get("MGC")));

        Table table = experiment.calculate("fixed-MGC.csv");

        printPaperTable(table);
    }

    private void printPaperTable(Table table) {
        List<List<String>> result = new ArrayList<>();

        writeColumn(result, getColumn(table.getRaw(), 0), 0);

        List<String> variants = new ArrayList<>();
        List<String> metrics = new ArrayList<>();
        List<String> topRow = table.getRaw().get(0);
        for (int i = 1; i < topRow.size(); i++) {
            String columnName = topRow.get(i);
            List<String> split = Arrays.asList(columnName.split(Pattern.quote(" ")));
            assert split.size() == 2;
            if (!variants.contains(split.get(0))) {
                variants.add(split.get(0));
            }
            if (!metrics.contains(split.get(1))) {
                metrics.add(split.get(1));
            }
        }

        for (String variant : variants) {
            List<String> column = new ArrayList<>();
            column.add("");
            for (int row = 1; row < table.getRaw().size(); row++) {
                List<String> cellContents = new ArrayList<>();
                for (String metric : metrics) {
                    int columnIndex = topRow.indexOf(variant + " " + metric);
                    cellContents.add(table.getRaw().get(row).get(columnIndex));
                }
                column.add(String.join(" / ", cellContents));
            }
            writeColumn(result, column, result.get(0).size());
        }


        List<String> resultTopRow = new ArrayList<>();
        resultTopRow.add("Library");
        resultTopRow.addAll(variants);
        result.set(0, resultTopRow);

        List<String> averages = new ArrayList<>();
        averages.add("Average");
        for (int columnIndex = 1; columnIndex < variants.size() + 1; columnIndex++) {
            List<Double> sums = new ArrayList<>();
            for (int i = 0; i < metrics.size(); i++) {
                sums.add(0d);
            }
            List<String> column = getColumn(result, columnIndex);
            column = column.subList(1, column.size());
            for (String cell : column) {
                List<Double> values = Stream.of(cell.split(Pattern.quote("/"))).map(String::trim).map(str -> str.replace("%", "").replace(",", ".")).map(str -> {try {
                    return Double.parseDouble(str);
                } catch (Exception e) {
                    return 0d;
                }}).collect(Collectors.toList());
                for (int i = 0; i < values.size(); i++) {
                    sums.set(i, sums.get(i) + values.get(i));
                }
            }
            List<String> resultSums = sums.stream().map(value -> value / (table.getRaw().size() - 1)).map(val -> val + "").collect(Collectors.toList());
            averages.add(String.join(" / ", resultSums));
        }

        result.add(averages);


        for (int i = 0; i < 10; i++) {
            System.out.println();
        }
        System.out.println("Comparison of RMGC variants. Each column contains: " + String.join(" / ", metrics));

        System.out.println(
                String.join("\n", result.stream()
                    .filter(Objects::nonNull)
                    .map(row -> String.join("|", Util.replaceNulls(row, "-")))
                    .collect(Collectors.toList())
                )
        );

        System.out.println();
    }

    public void writeColumn(List<List<String>> table, List<String> column, int columnIndex) {
        while (table.size() < column.size()) {
            table.add(new ArrayList<>());
        }
        for (int row = 0; row < table.size(); row++) {
            List<String> columns = table.get(row);
            while (columns.size() <= columnIndex) {
                columns.add("");
            }
            columns.set(columnIndex, columns.get(columnIndex) + column.get(row));
        }

    }

    private List<String> getColumn(List<List<String>> table, int column) {
        List<String> result = new ArrayList<>();
        for (List<String> columns : table) {
            result.add(columns.get(column));
        }
        return result;
    }

    private static BiConsumer<Benchmark, BiConsumer<String, String>> experiment(String prefix, Pair<Function<Benchmark, Benchmark>, Function<CheckOptions.Builder, StaticOptions.Builder>> optionsTransformer) {
        return (benchmark, register) -> {
            benchmark = optionsTransformer.getLeft().apply(benchmark);
            benchmark = benchmark.withOptions(AnalyzeBenchmarks.options().andThen(options -> optionsTransformer.getRight().apply(options.getOuterBuilder())));

            BenchmarkInfo.create(benchmark); // <- Just populating cache.

            long startTime = System.currentTimeMillis();

            TAJSUtil.TajsAnalysisResults result;
            try {
                result = TAJSUtil.runNoDriver(benchmark, 3 * 60 * 60);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            long endTime = System.currentTimeMillis();

            String time = Util.toFixed((endTime - startTime) / 1000.0, 1);
            double totalTests = result.testNot.size() + result.testPerformed.size() * 1.0;
            double finishedTests = result.testPerformed.size() - result.timeoutTests.size();
            String exhaustiveness = Util.toFixed(100 * finishedTests / totalTests, 1) + "%";

            register.accept(prefix + " action-coverage", exhaustiveness);
//            register.accept(prefix + " statement-coverage", String.format("%.2f", result.statementCoverage) + "");
//            register.accept(prefix + " scale-issues", result.exceptionsEncountered.size() + result.retractedTests.size() + "");
            register.accept(prefix + " violations", result.detectedViolations.keySet().size() + "");
            register.accept(prefix + " time", time);
            boolean scales = result.exceptionsEncountered.isEmpty() && result.exceptionsEncountered.isEmpty() && !result.timedout && result.timeoutTests.isEmpty() && result.retractedTests.isEmpty();
            register.accept(prefix + " scales", scales ? "YES": "NO");

            //noinspection ResultOfMethodCallIgnored
            new File("results").mkdir();

            try {
                Util.writeFile("results/" + benchmark.name + ".txt", result.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        new CompareModesEvaluation().doEvaluation();
        System.out.println("Took: " + (System.currentTimeMillis() - startTime) + "ms");
    }
}
