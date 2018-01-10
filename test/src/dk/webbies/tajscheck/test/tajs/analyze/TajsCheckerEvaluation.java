package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.experiments.AutomaticExperiments;
import dk.webbies.tajscheck.test.experiments.Experiment;
import dk.webbies.tajscheck.util.Util;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class TajsCheckerEvaluation {
    private static final List<String> benchmarksToEvaluate = Arrays.asList(
            "Hammer.js", // 36,0s
            "PDF.js", // 38,0s
            "lunr.js", // 62,5s

            "PhotoSwipe", // 241,4s
            "accounting.js", // 381,1s
            "pathjs", // 149,3s
            "reveal.js" // 177,2s
            /*
                QUnit patched	BROWSER	false	80	43	43	130	87	56	43	9	6	1	1828,8s
    Hammer.js patched	BROWSER	false	216	185	1646	700	170	144	530	0	14	0	36,0s
    PDF.js patched	BROWSER	false	29	22	22	323	30	20	293	8	0	0	38,0s
    PhotoSwipe	BROWSER	false	3	1	1	133	3	1	130	1	0	0	241,4s
    PleaseJS patched	BROWSER	false	34	4	6	27	27	26	0	0	0	0	3,4s
    Redux	NODE	false	80	26	33	74	58	51	16	0	2	0	38,0s
    Sortable patched	BROWSER	false	48	2	6	54	43	41	11	0	0	0	57,8s
    Swiper	BROWSER	false	3	1	1	144	3	1	141	1	0	0	22,0s
    accounting.js	BROWSER	false	77	26	110	85	81	44	4	0	0	0	381,1s
    axios patched	NODE	false	1	1	1	3	1	0	2	0	0	0	0,9s
    lunr.js patched	BROWSER	false	105	102	140	373	96	61	277	0	1	0	62,5s
    pathjs patched	BROWSER	false	56	2	3	47	45	44	2	5	0	0	149,3s
    reveal.js	BROWSER	false	130	30	31	154	139	110	15	1	0	0	177,2s
    async patched	BROWSER	false	311	108	198	515	270	260	245	5	114	38	2326,7s
             */


//            "PDF.js",
//            "box2dweb",
//            "pathjs",
//            "QUnit",
//            "Redux", // cheap
//            "Hammer.js", // cheap
//            "PleaseJS", // cheap
//            "Sortable", // cheap
//            "accounting.js",
//            "PhotoSwipe", // (timeout in global constructor)
//            "Swiper", // (timeout in global constructor)
//            "Knockout", // cheap
//            "RxJS",
//            "lunr.js", // cheap
//            "axios", // https://github.com/cs-au-dk/TAJS-private/issues/523 / TAJSUnitTests.forInOnPrototypeProperties
//            "async",
//            "Intro.js",
//            "CreateJS", // TODO: Comment in.
//            "Handlebars",
//            "highlight.js",
//            "Medium Editor", // TAJS never terminates on the global constructor
//            "bluebird",

//            "reveal.js"
    );

    /*
    // List of patches where filtering might have helped.
    In knockout, i had to put that KnockoutBindingProvider.nodeHasBindings and KnockoutBindingProvider.getBindings could be undefined, as TAJS reported it.
    In CreateJS, i had to make Ticker.framerate and Ticker.interval as potentially undefined, this might be spurious.
     */

    /*
    Current status (run on my laptop):
    Benchmark	type	timedout	certificates	violationPaths	violations	totalTests	testsPerformed	typeCheckedTests	testSkipped	timeouts	test-exceptions	retractions	time
    CreateJS patched	BROWSER	true	2743	735	881	5821	1836	1752	3985	1	29	84	10829,5s
    Handlebars patched	NODE	true	133	26	26	172	90	87	82	0	10	0	10801,1s
    highlight.js patched	NODE	true	74	1	1	86	60	60	26	1	0	0	10800,6s
    Medium Editor patched	BROWSER	true	2	0	0	86	2	2	84	0	0	0	10801,0s
    bluebird patched	NODE	true	100	0	0	6738	55	55	6683	1	28	0	10801,4s
    QUnit patched	BROWSER	false	80	43	43	130	87	56	43	9	6	1	1828,8s
    Hammer.js patched	BROWSER	false	216	185	1646	700	170	144	530	0	14	0	36,0s
    PDF.js patched	BROWSER	false	29	22	22	323	30	20	293	8	0	0	38,0s
    PhotoSwipe	BROWSER	false	3	1	1	133	3	1	130	1	0	0	241,4s
    PleaseJS patched	BROWSER	false	34	4	6	27	27	26	0	0	0	0	3,4s
    Redux	NODE	false	80	26	33	74	58	51	16	0	2	0	38,0s
    Sortable patched	BROWSER	false	48	2	6	54	43	41	11	0	0	0	57,8s
    Swiper	BROWSER	false	3	1	1	144	3	1	141	1	0	0	22,0s
    accounting.js	BROWSER	false	77	26	110	85	81	44	4	0	0	0	381,1s
    axios patched	NODE	false	1	1	1	3	1	0	2	0	0	0	0,9s
    lunr.js patched	BROWSER	false	105	102	140	373	96	61	277	0	1	0	62,5s
    pathjs patched	BROWSER	false	56	2	3	47	45	44	2	5	0	0	149,3s
    reveal.js	BROWSER	false	130	30	31	154	139	110	15	1	0	0	177,2s
    async patched	BROWSER	false	311	108	198	515	270	260	245	5	114	38	2326,7s
    Intro.js patched	NODE	false	115	6	9	54	49	49	5	12	0	0	1106,0s
    RxJS patched	NODE	false	141	6232	6232	1148	141	35	1007	0	0	0	851,7s
    box2dweb patched	BROWSER	false	3060	878	1147	1765	857	768	908	0	3	0	3729,4s
    Total	-	-	4798	7696	9656	12811	2307	1915	10504	44	178	39	54254,3

     */

    @Test
    @Ignore
    public void tmpStuff() throws Exception {
        new Experiment("PDF.js").addExperiment(experiment()).calculate(null);
    }

    @Test
    public void doEvaluation() throws IOException {
        {
            // warmup.
            new Experiment("Sortable").addExperiment(experiment()).calculate(null);
        }
        Experiment experiment = new Experiment(benchmarksToEvaluate.stream().map(RunBenchmarks.benchmarks::get).map(bench -> {
            if (AnalyzeBenchmarks.getPatchedBenchmark(bench) != null) {
                return bench.withName(bench.name + " patched");
            } else {
                return bench;
            }
        }).collect(Collectors.toList()));

        experiment.addSingleExperiment(AutomaticExperiments.type);
        experiment.addExperiment(experiment());

        experiment.calculate("experiment.csv");

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
                result = TAJSUtil.runNoDriver(benchmark, 3 * 60 * 60);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            long endTime = System.currentTimeMillis();

            String time = Util.toFixed((endTime - startTime) / 1000.0, 1);

            register.accept("timedout", Boolean.toString(result.timedout));
            register.accept("certificates", result.certificates.size() + "");
            register.accept("violationPaths", result.detectedViolations.size() + "");
            register.accept("violations", result.detectedViolations.asMap().entrySet().stream().reduce(0, (acc, entry) -> entry.getValue().size() + acc, Math::addExact) + "");
            register.accept("totalTests", result.testNot.size() + result.testPerformed.size() + "");
            register.accept("testsPerformed", result.testPerformed.size() + "");
            register.accept("typeCheckedTests", result.typeCheckedTests.size() + "");
            register.accept("testSkipped", result.testNot.size() + "");
            register.accept("timeouts", result.timeoutTests.size() + "");
            register.accept("test-exceptions", result.exceptionsEncountered.size() + "");
            register.accept("retractions", result.retractedTests.size() + "");
            register.accept("time", time + "s");

            //noinspection ResultOfMethodCallIgnored
            new File("results").mkdir();

            try {
                Util.writeFile("results/" + benchmark.name + ".txt", result.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
