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
            "pathjs",
            "Sortable",
            "async",
            "axios",
            "Medium Editor",
            "Redux",
            "Handlebars",
            "PleaseJS",
            "reveal.js",
            "accounting.js",
            "lunr.js",
            "intro.js",
            "PDF.js",
            "Hammer.js",
            "box2dweb",
            "Knockout",
            "PhotoSwipe",
            "Swiper",
            "CreateJS",
            "QUnit",
            "RxJS",
            "bluebird",
            "highlight.js"
    );

    /*
    Current status:
    Benchmark	type	certificates	violationPaths	violations	totalTests	testsPerformed	typeCheckedTests	testSkipped	timeouts	retractions	time
    CreateJS	BROWSER	6746	24	24	6745	1	0	6744	0	0	853,3s
    Hammer.js	BROWSER	4952	25	26	618	8	0	610	0	0	36,2s
    Handlebars	NODE	26469	29	29	172	98	92	74	0	0	217,3s
    Knockout	NODE	1017	48	48	1016	1	0	1015	0	0	40,3s
    Medium Editor	NODE	167	32	32	166	1	0	165	0	0	6,0s
    PDF.js	BROWSER	11016	21	21	323	36	23	287	12	0	87,8s
    PhotoSwipe	BROWSER	268	2	2	133	2	0	131	0	0	1,5s
    PleaseJS	BROWSER	28	1	1	27	1	0	26	0	0	0,9s
    QUnit	BROWSER	10480	32	32	130	87	56	43	13	1	3141,0s
    Redux	NODE	20	1	1	4	4	4	0	0	0	2,1s
    RxJS	NODE	1728	47	48	1727	1	0	1726	0	0	103,1s
    Sortable	BROWSER	110	2	2	54	2	0	52	0	0	1,1s
    Swiper	BROWSER	290	1	1	144	2	1	142	0	0	2,2s
    accounting.js	BROWSER	86	1	1	85	1	0	84	0	0	1,0s
    async	BROWSER	516	1	1	515	1	0	514	0	0	6,0s
    axios	NODE	1050	10	10	1049	1	0	1048	0	0	27,1s
    bluebird	NODE	6741	37	37	6740	1	0	6739	0	0	761,0s
    box2dweb	BROWSER	2135074	619	655	1765	370	296	1395	0	0	17092,0s
    highlight.js	NODE	41238	2	2	86	62	61	24	0	0	24,7s
    intro.js	NODE	2	0	0	1	1	1	0	0	0	1,3s
    lunr.js	BROWSER	374	1	1	373	1	0	372	0	0	3,6s
    pathjs	BROWSER	2688	2	3	47	45	44	2	5	0	150,5s
    reveal.js	BROWSER	155	1	1	154	1	0	153	0	0	1,7s
    Total	-	2251215	939	978	22074	728	578	21346	30	1	22561,7

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
