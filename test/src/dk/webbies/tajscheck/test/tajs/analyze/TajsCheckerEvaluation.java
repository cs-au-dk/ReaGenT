package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.benchmark.options.staticOptions.ExpandOneAtATimePolicy;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.experiments.AutomaticExperiments;
import dk.webbies.tajscheck.test.experiments.Experiment;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class TajsCheckerEvaluation {
    private static final List<String> benchmarksToEvaluate = Arrays.asList(
            "pathjs", // can analyze
            "Sortable", // can analyze
            "PleaseJS", // can analyze
            "Knockout" // 48 violations in the top-level object. So no methods are called.
    );

    public static void main(String[] args) throws IOException {
        {
            // warmup.
            new Experiment("Sortable").addExperiment(experiment()).calculate();
        }
        Experiment experiment = new Experiment(benchmarksToEvaluate);

        experiment.addSingleExperiment(AutomaticExperiments.type);
        experiment.addExperiment(experiment());

        String result = experiment.calculate().toCSV();


        System.out.println("\n\n\nResult: \n");
        System.out.println(result);

        Util.writeFile("experiment.csv", result);

        System.exit(0); // It would shut down by itself after a little, but I don't wanna wait.
    }

    private static BiConsumer<Benchmark, BiConsumer<String, String>> experiment() {
        return (benchmark, register) -> {
            Benchmark patched = AnalyzeBenchmarks.getPatchedBenchmark(benchmark);

            if (patched != null) {
                benchmark = patched;
            }

            benchmark = benchmark.withOptions(AnalyzeBenchmarks.options());

            BenchmarkInfo.create(benchmark); // <- Just populating cache.

            long startTime = System.currentTimeMillis();

            TAJSUtil.TajsAnalysisResults result;
            try {
                result = TAJSUtil.runNoDriver(benchmark, 10 * 60);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            long endTime = System.currentTimeMillis();

            String time = Util.toFixed((endTime - startTime) / 1000.0, 1);

            assert !result.timedout;

            register.accept("certificates", result.certificates.size() + "");
            register.accept("violationPaths", result.detectedViolations.size() + "");
            register.accept("violations", result.detectedViolations.asMap().entrySet().stream().reduce(0, (acc, entry) -> entry.getValue().size() + acc, Math::addExact) + "");
            register.accept("totalTests", result.testNot.size() + result.testPerformed.size() + "");
            register.accept("testsPerformed", result.testPerformed.size() + "");
            register.accept("typeCheckedTests", result.typeCheckedTests.size() + "");
            register.accept("testSkipped", result.testNot.size() + "");
            register.accept("timeouts", result.timeoutTests.size() + "");
            register.accept("retractions", result.retractedTests.size() + "");
            register.accept("time", time + "s");
        };
    }
}
