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
            "classnames", // Ok
            "uuid",  //ok
            "semver", // ok-ish, still WIP
            "mime",

            "Medium Editor", // TAJS never terminates on the global constructor

            "pathjs",
            "PDF.js",
            "PhotoSwipe", // (timeout in global constructor)
            "QUnit",
            "reveal.js",
            "RxJS",

//             Benchmarks not from TSTest.
//            "classnames",
//            "uuid",
//            "semver",
//            "mime",
//            "minimist",
            "Zepto.js",  // Call eval very imprecisely. (line 914)
            "PeerJS" // TAJS does not support WebRTC

    );

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
