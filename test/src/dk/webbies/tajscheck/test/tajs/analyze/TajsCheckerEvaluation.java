package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.dynamic.UnitTests;
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
    static final List<String> benchmarksToEvaluate = Arrays.asList(
            "accounting.js",
            "async",
            "axios", // https://github.com/cs-au-dk/TAJS-private/issues/523 / TAJSUnitTests.forInOnPrototypeProperties
            "bluebird",
            "box2dweb",
            "CreateJS",
            "Hammer.js", // cheap. A lot fails because of TajsUnitTests.emptyValueException
            "Handlebars",
            "highlight.js",
            "Intro.js",
            "Knockout", // cheap
            "lunr.js", // cheap
//
            "Medium Editor", // TAJS never terminates on the global constructor
//
            "pathjs",
            "PDF.js",
            "PhotoSwipe", // (timeout in global constructor)
            "PleaseJS", // cheap
            "QUnit",
            "Redux", // cheap
            "reveal.js",
            "RxJS",
            "Sortable", // cheap
            "Swiper" // (timeout in global constructor)
    );

    /*
    // List of patches where filtering might have helped.
    In knockout, i had to put that KnockoutBindingProvider.nodeHasBindings and KnockoutBindingProvider.getBindings could be undefined, as TAJS reported it. (spurious?)
    In CreateJS, i had to make Ticker.framerate and Ticker.interval as potentially undefined, this might be spurious.
     */

    /*
    Current status (run on my laptop):
    Benchmark	type	timedout	certificates	violationPaths	violations	totalTests	testsPerformed	typeCheckedTests	testSkipped	timeouts	test-exceptions	retractions	time	tstest:testsPerformed	tsest:violations	tsest:violationPaths	commonViolationPaths
    accounting.js	BROWSER	false	770	26	110	85	81	44	4	0	0	0	458,6s	85	0	0	0
    async patched	BROWSER	true	456	61	84	515	201	196	314	5	113	3	10801,7s	491	74	32	2
    axios patched	NODE	false	10	9	9	1049	1	0	1048	0	0	0	7,6s	1	0	0	0
    bluebird patched	NODE	false	630	4	5	6739	63	62	6676	1	83	0	1331,0s	1	0	0	0
    box2dweb patched	BROWSER	false	31730	878	1145	1765	857	768	908	0	3	0	4879,2s	905	125	100	58
    CreateJS patched	BROWSER	true	2866	570	723	5800	1846	1786	3954	1	29	84	10913,3s	1567	230	163	81
    Hammer.js patched	BROWSER	false	2160	186	2247	700	170	143	530	0	14	0	49,3s	651	205	177	129
    Handlebars patched	NODE	false	1930	17	17	172	138	125	34	0	17	0	266,7s	1	0	0	0
    highlight.js patched	NODE	false	4740	2	2	86	62	61	24	0	0	0	18,1s	1	0	0	0
    Intro.js patched	NODE	false	1150	6	9	54	49	49	5	12	0	0	1389,4s	1	0	0	0
    Knockout patched	NODE	true	662	91	177	1015	403	373	612	3	17	6	10809,4s	1	0	0	0
    lunr.js patched	BROWSER	false	1170	104	168	373	108	72	265	0	1	0	75,7s	172	23	18	10
    Medium Editor patched	BROWSER
    pathjs patched	BROWSER	false	560	2	3	47	45	44	2	5	0	0	163,0s	47	0	0	0
    PDF.js patched	BROWSER	false	340	1	1	323	36	23	287	12	0	0	70,5s	50	17	11	0
    PhotoSwipe	BROWSER	false	30	42	1231	133	3	1	130	1	0	0	315,3s	66	14	13	7
    PleaseJS patched	BROWSER	false	340	4	6	27	27	26	0	0	0	0	6,3s	27	0	0	0
    Redux	NODE	false	800	26	33	74	58	51	16	0	2	0	51,2s	55	128	11	2
    reveal.js	BROWSER	false	1300	30	31	154	139	110	15	1	0	0	245,4s	149	5	4	4
    RxJS patched	NODE
     */

    @Test
    @Ignore
    public void tmpStuff() {
        new Experiment("PDF.js").addExperiment(experiment()).calculate(null);
    }

    @Test
    public void doEvaluation() {
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

    private static final boolean COMPARE_WITH_TSTEST = true;

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
            List<String> tajsCheckerPaths = result.detectedViolations.keySet().stream().map(Util::simplifyPath).collect(Collectors.toList());
            register.accept("violationPaths", tajsCheckerPaths.size() + "");
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

            if (COMPARE_WITH_TSTEST) {
                try {
                    OutputParser.RunResult tstestResult = UnitTests.run(benchmark);
                    register.accept("tstest:testsPerformed", tstestResult.getTestsCalled().size() + "");
                    register.accept("tsest:violations", tstestResult.getTypeErrors().size() + "");
                    List<String> tstestPaths = tstestResult.getTypeErrors().stream().map(OutputParser.TypeError::getPath).map(Util::simplifyPath).distinct().collect(Collectors.toList());
                    register.accept("tsest:violationPaths", tstestPaths.size() + "");
                    List<String> commonPaths = Util.intersection(tstestPaths, tajsCheckerPaths);
                    register.accept("commonViolationPaths", commonPaths.size() + "");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
