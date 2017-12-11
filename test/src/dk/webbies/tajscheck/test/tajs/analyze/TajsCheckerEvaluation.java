package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.experiments.AutomaticExperiments;
import dk.webbies.tajscheck.test.experiments.Experiment;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class TajsCheckerEvaluation {
    private static final Set<String> benchmarksToEvaluate = new HashSet<>(Arrays.asList(
            "Sortable", // can analyze
            "PleaseJS", // can analyze
            "Knockout" // 48 violations in the top-level object. So no methods are called.
    ));

    @Test
    public void doEvaluation() throws Exception {
        Experiment experiment = new Experiment(benchmarksToEvaluate);

        assert true;

        experiment.addSingleExperiment(AutomaticExperiments.type);
        experiment.addMultiExperiment(experiment());

        String result = experiment.calculate(1).toCSV();


        System.out.println("\n\n\nResult: \n");
        System.out.println(result);

        Util.writeFile("experiment.csv", result);

        System.exit(0); // It would shut down by itself after a little, but I don't wanna wait.
    }

    private Pair<List<String>, Experiment.ExperimentMultiRunner> experiment() {
        return new Pair<>(Arrays.asList("certificates", "violationPaths", "violations", "totalTests", "testsPerformed", "typeCheckedTests", "testSkipped", "timeouts", "retractions"), benchmark -> {
            Benchmark patched = AnalyzeBenchmarks.getPatchedBenchmark(benchmark);
            if (patched != null) {
                benchmark = patched;
            }
            benchmark = benchmark.withOptions(AnalyzeBenchmarks.options());

            TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchmark, 10 * 60);

            assert !result.timedout;

            List<Integer> results = Arrays.asList(
                    result.certificates.size(),
                    result.detectedViolations.size(),
                    result.detectedViolations.asMap().entrySet().stream().reduce(0, (acc, entry) -> entry.getValue().size() + acc, Math::addExact),
                    result.testNot.size() + result.testPerformed.size(),
                    result.testPerformed.size(),
                    result.typeCheckedTests.size(),
                    result.testNot.size(),
                    result.timeoutTests.size(),
                    result.retractedTests.size()

            );
            return results.stream().map(i -> i + "").collect(Collectors.toList());
        });
    }
}
