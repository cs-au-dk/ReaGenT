package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions;
import dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy.ExpandImmediatelyPolicy;
import dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy.LateExpansionToFunctionsWithConstructedArguments;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.experiments.AutomaticExperiments;
import dk.webbies.tajscheck.test.experiments.Experiment;
import dk.webbies.tajscheck.test.experiments.Table;
import dk.webbies.tajscheck.test.flow.FlowBenchmarks;
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
            "mime",
            "pathjs",
            "PleaseJS",
            "uuid",
            "platform",
            "loglevel",
            "component-emitter",
            "pluralize",
            "js-cookie"
    );

    public static final Map<String, Function<CheckOptions.Builder, StaticOptions.Builder>> modes = new LinkedHashMap<>(){{
        put("all-assumptions", AnalyzeBenchmarks.weakMode());
        put("no-check-types", AnalyzeBenchmarks.strongMode());
        put("width-subtyping", AnalyzeBenchmarks.weakMode().andThen(options -> options.setProperWidthSubtyping(true)));
        put("writes", AnalyzeBenchmarks.weakMode().andThen(options -> options.getOuterBuilder().setWriteAll(true).staticOptions));
        put("no-prefer-lib-values", AnalyzeBenchmarks.weakMode().andThen(options -> options.setExpansionPolicy(new ExpandImmediatelyPolicy()).setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.MIX_FEEDBACK_AND_CONSTRUCTED)));
        //experiment.addExperiment(experiment("only-constructed", options -> options.staticOptions.setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.ONLY_CONSTRUCTED)));
        //experiment.addExperiment(experiment("only-feedback", options -> options.staticOptions.setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.FEEDBACK_IF_POSSIBLE).setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments(false))));
        //experiment.addExperiment(experiment("callbacks-not-rmgc", options -> options.staticOptions.setCallbacksAreMGC(false)));
        put("no-safe-strings", AnalyzeBenchmarks.weakMode().andThen(options -> options.setBetterAnyString(false)));

        put("no-assumptions",
                AnalyzeBenchmarks.strongMode().andThen(options -> // no-check-type
                        options
                                .setProperWidthSubtyping(true) // width-subtyping
                                .getOuterBuilder().setWriteAll(true).staticOptions // writes
                                .setExpansionPolicy(new ExpandImmediatelyPolicy()).setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.MIX_FEEDBACK_AND_CONSTRUCTED) // no-prefer-lib-values
                                .setBetterAnyString(false) // no-safe-strings.
                )
        );

        new HashSet<>(entrySet()).forEach(entry -> {
            Function<CheckOptions.Builder, StaticOptions.Builder> value = entry.getValue();
            put(entry.getKey(), AnalyzeBenchmarks.options().andThen(options -> value.apply(options.getOuterBuilder())));
        });
    }};


    @Test
    public void doEvaluationOnAllFlowBenchmarks() { // TODO: RUN!
        Experiment experiment = new Experiment(FlowBenchmarks.getBenchmarks());

        modes.forEach((name, options) -> {
            experiment.addExperiment(experiment(name, options));
        });

        Table table = experiment.calculate("compareModesFlow.csv");

        printPaperTable(table);
    }


    @Test
    public void doEvaluation() {
        Experiment experiment = new Experiment(benchmarksToEvaluate.stream().map(RunBenchmarks.benchmarks::get).collect(Collectors.toList()));

        modes.forEach((name, options) -> {
            experiment.addExperiment(experiment(name, options));
        });

        Table table = experiment.calculate("compareModes.csv");

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
                List<Double> values = Stream.of(cell.split(Pattern.quote("/"))).map(String::trim).map(str -> str.replace("%", "").replace(",", ".")).map(Double::parseDouble).collect(Collectors.toList());
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

    private static BiConsumer<Benchmark, BiConsumer<String, String>> experiment(String prefix, Function<CheckOptions.Builder, StaticOptions.Builder> optionsTransformer) {
        return (benchmark, register) -> {
            benchmark = benchmark.withOptions(optionsTransformer);

            BenchmarkInfo.create(benchmark); // <- Just populating cache.

            long startTime = System.currentTimeMillis();

            TAJSUtil.TajsAnalysisResults result;
            try {
                result = TAJSUtil.runNoDriver(benchmark, 20 * 60); // Could up to 3 hours, but that doesn't change that PathJS times out.
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
        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("flow")) {
                System.out.println("Using flow benchmarks");
                long startTime = System.currentTimeMillis();
                new CompareModesEvaluation().doEvaluationOnAllFlowBenchmarks();
                System.out.println("Took: " + (System.currentTimeMillis() - startTime) + "ms");
                return;
            }
        }
        long startTime = System.currentTimeMillis();
        new CompareModesEvaluation().doEvaluation();
        System.out.println("Took: " + (System.currentTimeMillis() - startTime) + "ms");
    }
}
