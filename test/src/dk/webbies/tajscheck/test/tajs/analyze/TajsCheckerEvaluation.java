package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.dynamic.UnitTests;
import dk.webbies.tajscheck.test.experiments.AutomaticExperiments;
import dk.webbies.tajscheck.test.experiments.Experiment;
import dk.webbies.tajscheck.testcreator.TestCreator;
import dk.webbies.tajscheck.util.Pair;
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
            "Sortable", // cheap
            "Knockout", // cheap
            "lunr.js", // cheap
            "Hammer.js", // cheap. A lot fails because of TajsUnitTests.emptyValueException
            "PleaseJS", // cheap
            "Redux", // cheap
            "accounting.js",
            "async",
            "axios", // https://github.com/cs-au-dk/TAJS-private/issues/523 / TAJSUnitTests.forInOnPrototypeProperties
            "bluebird",
            "box2dweb",
            "CreateJS",
            "Handlebars",
            "highlight.js",
            "Intro.js",
            "CodeMirror",
            "Moment.js",
//
            "Medium Editor", // TAJS never terminates on the global constructor
//
            "pathjs",
            "PDF.js",
            "PhotoSwipe", // (timeout in global constructor)
            "QUnit",
            "reveal.js",
            "RxJS",
            "Swiper", // (timeout in global constructor)

            // Benchmarks not from TSTest.
            "classnames",
            "uuid",
            "semver",
            "mime",
            "minimist",
            "jsyaml"
    );

    /*
    Clean benchmarks (where every violation is suppressed or patched).
    pathjs
    PleaseJS
    Redux


    // In progress.
//    accounting.js // TODO: Not clean.

    // New status of tool from casa04 (weak mode). (not with ignore maybe undef errors).
    Benchmark	type	timedout	certificates	violationPaths	violations	violations-definite	violations-maybe	totalTests	testsPerformed	typeCheckedTests	testSkipped	timeouts	test-exceptions	retractions	statement-coverage	branch-coverage	function-coverage	time	tstest:testsPerformed	tsest:violations	tsest:violationPaths	commonViolationPaths
    accounting.js	BROWSER	false	115	25	85	0	85	121	121	88	0	0	0	0	0.95	0.84	0.91	2554,6s
    async	BROWSER	true	663	72	299	213	86	644	217	212	427	0	224	9	NaN	NaN	NaN	10803,5s
    axios	NODE	false	1	10	10	3	7	1242	1	0	1241	0	0	0	0.34	0.11	0.35	4,5s	756	53	33	1
    bluebird	NODE	false	1	1	1	1	0	7407	1	0	7406	0	0	0	0.24	0.06	0.17	53,5s	168	2	2	1
    box2dweb	BROWSER	false	1	4	4	4	0	1846	1	0	1845	0	0	0	0.16	0.02	0.10	7,4s
    classnames	BROWSER	false	2	0	0	0	0	2	2	2	0	0	0	0	0.91	0.67	0.91	2,9s
    CodeMirror	BROWSER	false	1	2	2	2	0	1033	1	0	1032	0	0	0	0.05	0.01	0.02	10,5s
    CreateJS	BROWSER	false	1	29	29	29	0	9054	1	0	9053	0	0	0	0.14	0.03	0.10	22,7s
    Hammer.js	BROWSER	false	8	127	128	128	0	634	8	0	626	0	0	0	0.16	0.02	0.05	1,5s
    Handlebars	NODE	false	1	9	9	8	1	196	1	0	195	0	0	0	0.38	0.01	0.20	9,9s	186	116	25	8
    highlight.js	NODE	false	1	8	8	8	0	139	1	0	138	0	0	0	0.73	0.02	0.37	2,2s	112	51	31	8
    Intro.js	NODE	false	115	6	9	4	5	54	49	49	5	3	0	0	0.95	0.86	0.97	9093,3s	5	0	0	0
    jsyaml	BROWSER	false	16	9	9	9	0	68	16	7	52	0	0	0	0.19	0.05	0.27	72,3s
    Knockout	NODE	false	1	7	7	5	2	1156	1	0	1155	0	0	0	0.16	0.02	0.11	4,3s	722	208	74	5
    lunr.js	BROWSER	false	1	7	7	7	0	418	1	0	417	0	0	0	0.15	0.00	0.08	1,0s
    Medium Editor	BROWSER	false	1	32	32	32	0	95	1	0	94	0	0	0	0.10	0.02	0.07	3,0s
    mime	BROWSER	false	1	1	1	1	0	11	1	0	10	0	0	0	0.97	0.38	0.86	52,0s
    minimist	NODE	false	7	3	3	2	1	23	7	1	16	0	10	0	1.00	1.00	1.00	52,8s	23	792	4	1
    Moment.js	BROWSER	false	1	1	1	1	0	826	1	0	825	0	0	0	0.21	0.03	0.10	6,3s
    pathjs	BROWSER	false	45	8	8	0	8	44	42	34	2	0	0	0	0.93	0.87	1.00	287,0s
    PDF.js	BROWSER	false	1	8	8	8	0	349	1	0	348	0	0	0	0.10	0.01	0.11	3,7s
    PhotoSwipe	BROWSER	false	3	28	30	12	18	135	3	1	132	0	0	0	0.11	0.03	0.05	2,3s
    PleaseJS	BROWSER	false	64	9	37	0	37	51	47	28	4	0	0	0	0.83	0.81	0.81	10,9s
    QUnit	BROWSER	false	1	17	29	3	26	178	1	0	177	0	0	0	0.18	0.09	0.23	2,7s
    Redux	NODE	false	214	19	39	19	20	94	79	73	15	0	0	0	0.87	0.62	0.88	56,1s	84	666	10	1
    reveal.js	BROWSER	false	141	39	53	37	16	165	150	113	15	0	0	0	0.91	0.86	0.95	1415,7s
    RxJS	NODE	false	1	47	48	48	0	2023	1	0	2022	0	0	0	0.20	0.01	0.20	17,2s	995	613	359	47
    semver	NODE	false	128	54	75	25	50	192	130	97	62	0	0	0	0.97	0.93	0.92	1200,6s	190	181	34	13
    Sortable	BROWSER	false	2	3	3	3	0	114	2	0	112	0	0	0	0.06	0.00	0.09	0,8s
    Swiper	BROWSER	false	3	56	57	57	0	157	3	1	154	1	0	0	0.06	0.05	0.08	521,3s
    uuid	BROWSER	false	21	0	0	0	0	21	21	21	0	0	0	0	0.04	0.01	0.01	21,7s
    Total	-	-	1562	641	1031	669	362	28492	912	727	27580	4	234	9	13,049999999999999	8,440000000000001	11,97	26298,199999999997	3241	2682	572	85

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

    /* (mezzetti mac)
    Benchmark	type	timedout	certificates	violationPaths	violations	totalTests	testsPerformed	typeCheckedTests	testSkipped	timeouts	test-exceptions	retractions	statement-coverage	branch-coverage	function-coverage	time	tstest:testsPerformed	tsest:violations	tsest:violationPaths	commonViolationPaths
    accounting.js	BROWSER	false	770	26	110	85	81	44	4	0	0	0	0.95	0.39	0.91	236,7s	85	0	0	0
    async patched	BROWSER	true	706	107	152	515	276	271	239	23	115	13	NaN	NaN	NaN	10800,9s	493	108	32	5
    axios patched	NODE	false	10	9	9	1049	1	0	1048	0	0	0	0.34	0.32	0.35	52,1s	566	51	35	0
    bluebird patched	NODE	false	630	4	5	6738	63	62	6675	1	83	0	0.38	0.28	0.36	3733,3s	228	1	1	0
    box2dweb patched	BROWSER	false	31570	878	1145	1765	857	768	908	0	3	0	0.30	0.36	0.44	3784,3s	1324	215	179	75
    CreateJS patched	BROWSER	true	2769	567	719	5801	1865	1810	3936	1	29	82	NaN	NaN	NaN	10920,5s	2052	280	200	84
    Hammer.js patched	BROWSER	false	2160	186	2247	700	170	143	530	0	14	0	0.36	0.20	0.41	263,9s	651	199	173	128
    Handlebars patched	NODE	false	1930	17	17	172	138	125	34	0	17	0	0.50	0.18	0.42	234,9s	168	128	11	4
    highlight.js patched	NODE	false	4740	2	2	86	62	61	24	0	0	0	0.74	0.25	0.44	36,5s	62	2	2	1
    Intro.js patched	NODE	false	1150	6	9	54	49	49	5	12	0	0	0.94	0.30	0.97	733,5s	5	0	0	0
    Knockout patched	NODE	true	681	92	199	1015	384	355	631	4	17	6	NaN	NaN	NaN	10818,2s	666	211	68	20
    lunr.js patched	BROWSER	false	1170	104	168	373	108	72	265	0	1	0	0.56	0.39	0.47	131,9s	270	40	29	13
    Medium Editor patched	BROWSER	true	2	0	0	86	2	2	84	0	0	0	NaN	NaN	NaN	10803,1s	80	14	12	0
    pathjs patched	BROWSER	false	560	2	3	47	45	44	2	5	0	0	0.92	0.44	1.00	90,3s	46	0	0	0
    PDF.js patched	BROWSER
    PhotoSwipe	BROWSER	false	30	42	1231	133	3	1	130	1	0	0	0.11	0.01	0.05	148,3s	66	14	13	7
    PleaseJS patched	BROWSER	false	340	4	6	27	27	26	0	0	0	0	0.39	0.18	0.52	2,8s	27	0	0	0
    QUnit patched	BROWSER	false	760	43	43	130	83	56	47	4	10	1	0.38	0.17	0.53	1369,3s	128	11	3	1
    Redux	NODE	false	800	26	33	74	58	51	16	0	2	0	0.82	0.64	0.85	27,4s	55	133	11	2
    reveal.js	BROWSER	false	1300	30	31	154	139	110	15	1	0	0	0.75	0.32	0.83	138,9s	149	5	4	4
    RxJS patched	NODE
    Sortable patched	BROWSER	false	480	2	6	54	43	41	11	0	0	0	0.42	0.18	0.66	40,3s	26	0	0	0
    Swiper	BROWSER	false	30	1	1	144	3	1	141	1	0	0	0.04	0.01	0.05	36,4s	68	55	54	0
    Total	-	-	52588	2148	6136	19202	4457	4092	14745	53	291	102	8,9	4,619999999999999	9,26	54403,50000000002	7215	1467	827	344
     */

    @Test
    @Ignore
    public void tmpStuff() {
        new Experiment("Handlebars").addSingleExperiment(AutomaticExperiments.type).addExperiment(experiment()).calculate(null);
    }

    @Test
    public void doEvaluation() {
        {
            // warmup.
            new Experiment("classnames").addExperiment(experiment()).calculate(null);
        }
        Experiment experiment = new Experiment(benchmarksToEvaluate.stream().map(RunBenchmarks.benchmarks::get).map(bench -> {
            if (bench.patched() != null) {
                return bench.withName(bench.name); // TODO:
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
            benchmark = benchmark.withOptions(AnalyzeBenchmarks.options().andThen(((StaticOptions.Builder builder) -> AnalyzeBenchmarks.weakMode().apply(builder.getOuterBuilder()))));

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
            register.accept("violations", result.detectedViolations.asMap().values().stream().reduce(0, (acc, violations) -> violations.size() + acc, Math::addExact) + "");
            register.accept("violations-definite", result.detectedViolations.asMap().values().stream().flatMap(Collection::stream).filter(violation -> violation.definite).count() + "");
            register.accept("violations-maybe", result.detectedViolations.asMap().values().stream().flatMap(Collection::stream).filter(violation -> !violation.definite).count() + "");
            register.accept("totalTests", result.testNot.size() + result.testPerformed.size() + "");
            register.accept("testsPerformed", result.testPerformed.size() + "");
            register.accept("typeCheckedTests", result.typeCheckedTests.size() + "");
            register.accept("testSkipped", result.testNot.size() + "");
            register.accept("timeouts", result.timeoutTests.size() + "");
            register.accept("test-exceptions", result.exceptionsEncountered.size() + "");
            register.accept("retractions", result.retractedTests.size() + "");
            register.accept("statement-coverage", String.format("%.2f", result.statementCoverage) + "");
            register.accept("branch-coverage", String.format("%.2f", result.branchCoverage) + "");
            register.accept("function-coverage", String.format("%.2f", result.functionCoverage) + "");
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

                    OutputParser.RunResult tstestResult = makeTSTestRunTheSameTests(benchmark, result.testPerformed);

                    register.accept("tstest:testsPerformed", tstestResult.getTestsCalled().size() + "");
                    register.accept("tsest:violations", tstestResult.getTypeErrors().size() + "");
                    List<String> tstestPaths = tstestResult.getTypeErrors().stream().map(OutputParser.TypeError::getPath).map(Util::simplifyPath).distinct().collect(Collectors.toList());
                    register.accept("tsest:violationPaths", tstestPaths.size() + "");
                    List<String> commonPaths = Util.intersection(tstestPaths, tajsCheckerPaths);
                    register.accept("commonViolationPaths", commonPaths.size() + "");
                } catch (Exception e) {
                    System.err.println("TSTest comparison failed with: " + e);
                }
            }
        };
    }

    private static OutputParser.RunResult makeTSTestRunTheSameTests(Benchmark bench, Collection<dk.webbies.tajscheck.testcreator.test.Test> testPerformed) throws IOException {
        BenchmarkInfo info = BenchmarkInfo.create(bench);

        //noinspection Convert2MethodRef
        Set<String> performedPaths = testPerformed.stream().map(test -> test.getPath()).collect(Collectors.toSet());

        List<dk.webbies.tajscheck.testcreator.test.Test> tests = new TestCreator(info).createTests().stream().filter(test -> performedPaths.contains(test.getPath())).collect(Collectors.toList());

        String programString = Main.generateFullDriver(info, tests, null);

        Util.writeFile(Main.getFolderPath(bench) + Main.TEST_FILE_NAME, programString);

        return OutputParser.parseDriverResult(Main.runBenchmark(bench));
    }


    public static void main(String[] args) {
        new TajsCheckerEvaluation().doEvaluation();
    }
}
