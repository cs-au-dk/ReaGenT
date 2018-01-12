package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
import dk.webbies.tajscheck.test.DeltaDebug;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.util.Util;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BooleanSupplier;

public class ManualTajsCheckerEvaluation {

    private static final int TIMEOUT = 20 * 60; // in seconds

    private static final Set<String> CAN_DELTA_DEBUG = new HashSet<>(Arrays.asList(
            "Hammer.js",
            "PDF.js",
            "PhotoSwipe",
            "Swiper",
            "pathjs",
//            "Redux", // has a .d.ts file that imports another.
            "Sortable",
            "accounting.js",
            "axios",
            "lunr.js",

            "reveal.js",
            "box2dweb",
            "QUnit",
            "PleaseJS", // cheap
            "Knockout", // cheap
//            "RxJS", Has its actual types spread out all in many sub-files.
            "async",
            "Intro.js",
            "CreateJS",
            "Handlebars",
            "highlight.js",
            "bluebird",
            "Medium Editor" // TAJS never terminates on the global constructor
    ));

    @Test
    public void runInALoop() throws Exception {
        List<String> benchmarks = TajsCheckerEvaluation.benchmarksToEvaluate;
        //noinspection InfiniteLoopStatement
        while (true) {
            findATypeError(benchmarks.get(new Random().nextInt(benchmarks.size())));
        }
    }

    @Test
    public void doASingleEval() throws Exception {
        findATypeError("lunr.js");
    }

    @SuppressWarnings("SameParameterValue")
    private void findATypeError(String benchmarkName) throws Exception {
        Benchmark bench = RunBenchmarks.benchmarks.get(benchmarkName);
        Benchmark patched = AnalyzeBenchmarks.getPatchedBenchmark(bench);
        if (patched != null) {
            bench = patched;
        }

        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(bench, TIMEOUT);

        List<Map.Entry<String, Collection<TypeViolation>>> violationList = new ArrayList<>(result.detectedViolations.asMap().entrySet());

        if (violationList.isEmpty()) {
            System.err.println("Didn't find a violation in benchmark: " + benchmarkName);
            return;
        }
        Collections.shuffle(violationList);
        Map.Entry<String, Collection<TypeViolation>> violation = violationList.get(0);
        ArrayList<TypeViolation> typeViolations = new ArrayList<>(violation.getValue());
        Collections.shuffle(typeViolations);

        TypeViolation typeViolation = typeViolations.iterator().next();
        reportViolation(bench, TypeViolation.definite(typeViolation.message, Util.simplifyPath(typeViolation.path)));
    }

    private void reportViolation(Benchmark bench, TypeViolation searchViolation) throws IOException {
        new File("results").mkdir();
        new File("results/manualEval").mkdir();

        int counter = 0;
        while (new File("results/manualEval/" + counter).exists()) {
            counter++;
        }

        new File("results/manualEval/" + counter).mkdir();

        Util.writeFile("results/manualEval/" + counter + "/" + bench.name + "_violation.txt", searchViolation.toString());

        String dTsFile = Util.readFile(bench.dTSFile);
        String jsFile = Util.readFile(bench.jsFile);

        if (!CAN_DELTA_DEBUG.contains(bench.name)) {
            return;
        }

        Util.writeFile("results/manualEval/" + counter + "/" + bench.name + ".d.ts.original", dTsFile);
        Util.writeFile("results/manualEval/" + counter + "/" + bench.name + ".js.original", jsFile);


        deltaDebugViolation(bench, searchViolation);

        Util.writeFile("results/manualEval/" + counter + "/" + bench.name + ".d.ts", Util.readFile(bench.dTSFile));
        Util.writeFile("results/manualEval/" + counter + "/" + bench.name + ".js", Util.readFile(bench.jsFile));


        Util.writeFile(bench.dTSFile, dTsFile);
        Util.writeFile(bench.jsFile, jsFile);
    }

    private void deltaDebugViolation(Benchmark bench, TypeViolation searchViolation) throws IOException {
        BooleanSupplier predicate = () -> {
            try {
                TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(bench, TIMEOUT);

                return result.detectedViolations.asMap().values().stream().flatMap(Collection::stream).anyMatch(typeViolation -> Util.simplifyPath(typeViolation.path).equals(searchViolation.path) && typeViolation.message.equals(searchViolation.message));
            } catch (Throwable e) {
                e.printStackTrace();
                return false;
            }
        };

        DeltaDebug.debug(bench.dTSFile, predicate);
        DeltaDebug.debug(bench.jsFile, predicate);
    }
}
