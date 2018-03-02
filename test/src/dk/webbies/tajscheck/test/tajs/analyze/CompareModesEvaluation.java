package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions;
import dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy.LateExpansionToFunctionsWithConstructedArguments;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.experiments.AutomaticExperiments;
import dk.webbies.tajscheck.test.experiments.Experiment;
import dk.webbies.tajscheck.util.Util;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompareModesEvaluation {
    static final List<String> benchmarksToEvaluate = Arrays.asList(
            "classnames",
            "mime",
            "pathjs",
            "PleaseJS", // cheap
            "Redux", // cheap
            "uuid"

            /* Excluded: "Swiper","Hammer.js","Sortable","Knockout","lunr.js","accounting.js","async","axios","bluebird",
            "box2dweb","CreateJS","Handlebars","highlight.js","Intro.js","CodeMirror","Moment.js","Medium Editor","PDF.js",
            "PhotoSwipe","QUnit","reveal.js","RxJS","semver", "minimist", "jsyaml"
             */
    );


    @Test
    public void doEvaluation() {
        {
            // warmup.
            new Experiment("uuid").addExperiment(experiment("warmup", builder -> builder.staticOptions)).calculate(null);
        }
        Experiment experiment = new Experiment(benchmarksToEvaluate.stream().map(RunBenchmarks.benchmarks::get).collect(Collectors.toList()));

        experiment.addSingleExperiment(AutomaticExperiments.type);

        // Weak mode is default, so no need to change anything there.
        experiment.addExperiment(experiment("weak", AnalyzeBenchmarks.weakMode()));
        experiment.addExperiment(experiment("strong", AnalyzeBenchmarks.strongMode()));
        experiment.addExperiment(experiment("proper-width", options -> options.staticOptions.setProperWidthSubtyping(true)));
        experiment.addExperiment(experiment("side-effects", options -> options.setWriteAll(true).staticOptions));

        // still waits for feedback-values to be available, but then mixed synthetic into it anyway.
        experiment.addExperiment(experiment("mix-synthetic", options -> options.staticOptions.setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.MIX_FEEDBACK_AND_CONSTRUCTED)));
        experiment.addExperiment(experiment("only-constructed", options -> options.staticOptions.setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.ONLY_CONSTRUCTED)));
        experiment.addExperiment(experiment("only-feedback", options -> options.staticOptions.setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.FEEDBACK_IF_POSSIBLE).setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments(false))));

        experiment.addExperiment(experiment("callbacks-not-rmgc", options -> options.staticOptions.setCallbacksAreMGC(false)));


        experiment.calculate("experiment.csv");

        System.exit(0); // It would shut down by itself after a little while, but I don't wanna wait.
    }

    private static BiConsumer<Benchmark, BiConsumer<String, String>> experiment(String prefix, Function<CheckOptions.Builder, StaticOptions.Builder> optionsTransformer) {
        return (benchmark, register) -> {
//            if (benchmark.patched() != null) {
//                benchmark = benchmark.patched();
//            }

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

            String time = Util.toFixed((endTime - startTime) / 1000.0, 1);
            double totalTests = result.testNot.size() + result.testPerformed.size() * 1.0;
            double finishedTests = result.testPerformed.size() - result.timeoutTests.size();
            String exhaustiveness = Util.toFixed(100 * finishedTests / totalTests, 1) + "%";

            register.accept(prefix + " exhaustiveness", exhaustiveness);
            register.accept(prefix + "statement-coverage", String.format("%.2f", result.statementCoverage) + "");
            register.accept(prefix + " scaleIssues", result.exceptionsEncountered.size() + result.retractedTests.size() + "");
            register.accept(prefix + "violations", result.detectedViolations.asMap().values().stream().reduce(0, (acc, violations) -> violations.size() + acc, Math::addExact) + "");
            register.accept(prefix + "time", time);

            //noinspection ResultOfMethodCallIgnored
            new File("results").mkdir();

            try {
                Util.writeFile("results/" + benchmark.name + ".txt", result.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static void main(String[] args) {
        new CompareModesEvaluation().doEvaluation();
    }
}
