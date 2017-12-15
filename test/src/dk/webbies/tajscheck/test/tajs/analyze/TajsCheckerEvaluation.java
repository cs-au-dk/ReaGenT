package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.experiments.AutomaticExperiments;
import dk.webbies.tajscheck.test.experiments.Experiment;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

public class TajsCheckerEvaluation {
    private static final List<String> benchmarksToEvaluate = Arrays.asList(
            "pathjs", // can analyze
            "Sortable", // can analyze
            "PleaseJS", // can analyze
            "reveal.js", // can analyze.
            "accounting.js", // ~4 minutes on my desktop.
            "lunr.js", // can analyze. But plenty or errors in top-level constructors, meaning we skip a lot of tests.
            "intro.js", // Takes about 11 minutes.
            "PDF.js", // can analyze. (but lots of timeouts).
            "Hammer.js", // Seemingly have some false positives (like Hammer.TouchAction.preventDefaults).
            "box2dweb", // ~20 minutes on my desktop. But terminates (we are talking 73 constructors, 85 methods, then it terminates).
            "Knockout" // 48 violations in the top-level object. So no methods are called.
    );

    /*
    Current status:
    Benchmark	type	certificates	violationPaths	violations	totalTests	testsPerformed	typeCheckedTests	testSkipped	timeouts	retractions	time
    Hammer.js	BROWSER	8	25	26	618	8	0	610	0	0	2,9s
    Knockout	BROWSER	1	48	48	1016	1	0	1015	0	0	7,4s
    PDF.js	BROWSER	34	9	9	323	36	23	287	9	0	63,2s
    PleaseJS	BROWSER	34	2	2	27	27	26	0	0	0	5,1s
    Sortable	BROWSER	48	2	6	54	43	41	11	0	0	84,3s
    accounting.js	BROWSER	77	26	110	85	81	44	4	0	0	515,8s
    box2dweb	BROWSER	1266	728	804	1765	370	297	1395	0	0	1753,1s
    intro.js	BROWSER	113	6	9	53	47	47	6	12	0	997,2s
    lunr.js	BROWSER	98	56	88	373	64	47	309	0	0	36,0s
    pathjs	BROWSER	56	2	3	47	45	44	2	5	0	155,9s
    reveal.js	BROWSER	128	27	27	154	137	110	17	1	0	163,3s
    Total	-	1863	931	1132	4515	859	679	3656	27	-	3784,2000000000003

     */

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
                result = TAJSUtil.runNoDriver(benchmark, 60 * 60);
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
