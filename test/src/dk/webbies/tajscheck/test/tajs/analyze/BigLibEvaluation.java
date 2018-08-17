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
import dk.webbies.tajscheck.util.Util;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("Duplicates")
public class BigLibEvaluation {
    static List<String> benchmarksToEvaluate = Arrays.asList(
            "accounting.js",
            "async",
            "axios",
            "bluebird",
            "box2dweb",
            "CodeMirror",
            "CreateJS",
            "Hammer.js",
            "Handlebars",
            "highlight.js",
            "Intro.js",
            "Knockout",
            "lunr.js",
            "Medium Editor",
            "Moment.js",
            "pathjs",
            "PeerJS",
            "PDF.js",
            "PhotoSwipe",
            "QUnit",
            "Redux",
            "reveal.js",
            "RxJS",
            "Sortable",
            "Zepto.js",
            "classnames",
            "mime",
            "minimist",
            "semver",
            "uuid",
            "PleaseJS"
    );


    @Test
    public void doEvaluation() {
        Experiment experiment = new Experiment(benchmarksToEvaluate.stream().map(RunBenchmarks.benchmarks::get).collect(Collectors.toList()));

//        experiment.addSingleExperiment(AutomaticExperiments.type);

        // Weak mode is default, so no need to change anything there. strong mode doesn't exist anymore.
        experiment.addExperiment(experiment("all-assumptions", options -> options.staticOptions));

        System.out.println(experiment.calculate("biglib.csv").print("|", "\n"));
    }

    private static BiConsumer<Benchmark, BiConsumer<String, String>> experiment(String prefix, Function<CheckOptions.Builder, StaticOptions.Builder> optionsTransformer) {
        return (benchmark, register) -> {
            benchmark = benchmark.withOptions(AnalyzeBenchmarks.options().andThen(builder -> optionsTransformer.apply(builder.getOuterBuilder())));

            BenchmarkInfo.create(benchmark); // <- Just populating cache.

            long startTime = System.currentTimeMillis();

            TAJSUtil.TajsAnalysisResults result;
            try {
                result = TAJSUtil.runNoDriver(benchmark, 3 * 60 * 60);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            long endTime = System.currentTimeMillis();

            String time = Util.toFixed((endTime - startTime) / 1000.0, 1) + "s";
            double totalTests = result.testNot.size() + result.testPerformed.size() * 1.0;
            double finishedTests = result.testPerformed.size() - result.timeoutTests.size();
            String exhaustiveness = Util.toFixed(100 * finishedTests / totalTests, 1) + "%";

            register.accept(prefix + " statement-coverage", String.format("%.2f", result.statementCoverage) + "");
            register.accept(prefix + " action-coverage", exhaustiveness);
            boolean scales = result.exceptionsEncountered.isEmpty() && result.exceptionsEncountered.isEmpty() && !result.timedout && result.timeoutTests.isEmpty() && result.retractedTests.isEmpty();
            register.accept(prefix + " scales", scales ? "YES": "NO");
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
        System.out.println(Arrays.toString(args));
        if (args.length > 0 && args[0].toLowerCase().trim().equals("limited")) {
            benchmarksToEvaluate = Arrays.asList(
                    "classnames",
                    "mime",
                    "pathjs",
                    "PleaseJS",
                    "PeerJS",
                    "uuid"
            );
        } else if (args.length > 0) {
            benchmarksToEvaluate = Arrays.asList(args);
        }
        long startTime = System.currentTimeMillis();
        new BigLibEvaluation().doEvaluation();
        System.out.println("Took: " + Util.toFixed((System.currentTimeMillis() - startTime) / 1000d, 2) + "s");
    }
}
