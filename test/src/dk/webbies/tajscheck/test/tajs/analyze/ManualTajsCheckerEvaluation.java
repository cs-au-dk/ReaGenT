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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class ManualTajsCheckerEvaluation {

    private static final int TIMEOUT = 20 * 60; // in seconds
    private static final boolean DO_DELTA_DEBUGGING = false;

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
            "PleaseJS",
            "Knockout",
//            "RxJS", Has its actual types spread out all in many sub-files.
            "async",
            "Intro.js",
            "CreateJS",
            "Handlebars",
            "highlight.js",
            "bluebird",
            "Medium Editor"
    ));

    @Test
    public void runInALoop() throws Exception {
        List<String> benchmarks = TajsCheckerEvaluation.benchmarksToEvaluate;
        if (benchmarks.size() < 20) {
            throw new RuntimeException("I don't think you meant to do this");
        }
        //noinspection InfiniteLoopStatement
        while (true) {
            findATypeError(benchmarks.get(new Random().nextInt(benchmarks.size())));
        }
    }

    @Test
    public void doASingleEval() throws Exception {
        findATypeError("axios");
    }

    private static final Set<String> cleanBenchmarks = new HashSet<>();

    @SuppressWarnings("SameParameterValue")
    private void findATypeError(String benchmarkName) throws Exception {
        if (cleanBenchmarks.contains(benchmarkName)) {
            return;
        }
        Benchmark bench = RunBenchmarks.benchmarks.get(benchmarkName).withOptions(AnalyzeBenchmarks.options());
        Benchmark patched = AnalyzeBenchmarks.getPatchedBenchmark(bench);
        if (patched != null) {
            bench = patched;
        }

        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(bench, TIMEOUT);

        List<Map.Entry<String, Collection<TypeViolation>>> violationList = new ArrayList<>(result.detectedViolations.asMap().entrySet());

        if (violationList.isEmpty()) {
            cleanBenchmarks.add(benchmarkName);
            System.err.println("Didn't find a violation in benchmark: " + benchmarkName);
            return;
        }
        Collections.shuffle(violationList);
        Map.Entry<String, Collection<TypeViolation>> violation = violationList.get(0);
        ArrayList<TypeViolation> typeViolations = new ArrayList<>(violation.getValue());
        Collections.shuffle(typeViolations);

        TypeViolation typeViolation = typeViolations.iterator().next();
        typeViolation = typeViolation.withPath(Util.simplifyPath(typeViolation.path));
        reportViolation(bench, typeViolation);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
        Util.writeFile("results/manualEval/" + counter + "/" + bench.name + ".d.ts", dTsFile);
        Util.writeFile("results/manualEval/" + counter + "/" + bench.name + ".js.original", jsFile);
        Util.writeFile("results/manualEval/" + counter + "/" + bench.name + ".js", jsFile);

        bench = bench
                .withJsFile("results/manualEval/" + counter + "/" + bench.name + ".js")
                .withDecl("results/manualEval/" + counter + "/" + bench.name + ".d.ts");

        if (DO_DELTA_DEBUGGING) {
            boolean success = deltaDebugViolation(bench, searchViolation);

            if (success) {
                new File("results/manualEval/" + counter + "/" + bench.name + ".js.smallest").delete();
                new File("results/manualEval/" + counter + "/" + bench.name + ".d.ts.smallest").delete();
            }
        } else {
            Util.writeFile("results/manualEval/" + counter + "/skipped_delta.txt", "");
        }
    }

    @Test
    public void doTheMissingStuff() throws IOException {
        continueWithFolder(11);
        continueWithFolder(19);
        continueWithFolder(20);
        continueWithFolder(22);
        continueWithFolder(24);
        continueWithFolder(26);
        continueWithFolder(33);
        continueWithFolder(37);
    }

    @SuppressWarnings("ConstantConditions")
    public void continueWithFolder(int counter) throws IOException {
        List<String> violationNames = Arrays.stream(new File("results/manualEval/" + counter).listFiles()).map(File::getName).filter(name -> name.contains("violation")).collect(Collectors.toList());
        assert violationNames.size() == 1;
        String benchmarkName = Util.removeSuffix(violationNames.iterator().next(), "_violation.txt");

        System.out.println("Benchmark: " + benchmarkName);


        if (new File("results/manualEval/" + counter + "/skipped_delta.txt").exists()) {
            System.out.println("Start delta-debugging.");
            new File("results/manualEval/" + counter + "/skipped_delta.txt").delete();
            Util.copyFile(
                    "results/manualEval/" + counter + "/" + benchmarkName + ".js",
                    "results/manualEval/" + counter + "/" + benchmarkName + ".js.smallest"
            );
            Util.copyFile(
                    "results/manualEval/" + counter + "/" + benchmarkName + ".d.ts",
                    "results/manualEval/" + counter + "/" + benchmarkName + ".d.ts.smallest"
            );
            continueDeltaDebugging(counter, benchmarkName);
            return;
        }

        if (new File("results/manualEval/" + counter + "/" + benchmarkName + ".js.smallest").exists()) {
            System.out.println("Continue delta-debugging");
            continueDeltaDebugging(counter, benchmarkName);
            return;
        }

        System.out.println("Nothing to do here.");
    }

    public void continueDeltaDebugging(int folderNumber, String benchmarkName) throws IOException {
        assert new File("results/manualEval/" + folderNumber + "/" + benchmarkName + ".d.ts.original").exists();

        Benchmark bench = RunBenchmarks.benchmarks.get(benchmarkName)
                .withJsFile("results/manualEval/" + folderNumber + "/" + benchmarkName + ".js")
                .withDecl("results/manualEval/" + folderNumber + "/" + benchmarkName + ".d.ts");

        String smallJs = Util.readFile("results/manualEval/" + folderNumber + "/" + bench.name + ".js.smallest");
        String smallDTS = Util.readFile("results/manualEval/" + folderNumber + "/" + bench.name + ".d.ts.smallest");

        Util.writeFile(bench.jsFile, smallJs);
        Util.writeFile(bench.dTSFile, smallDTS);

        String violationString = Util.readFile("results/manualEval/" + folderNumber + "/" + benchmarkName + "_violation.txt");

        System.out.println(violationString);

        boolean definite = violationString.startsWith("Definite: ");
        violationString = Util.removePrefix(violationString, definite ? "Definite: " : "Maybe: ");
        int inTestIndex = violationString.lastIndexOf("in test");
        String path = violationString.substring(inTestIndex + "in test".length() + 1, violationString.length());
        String message = violationString.substring(0, inTestIndex);

        TypeViolation violation = TypeViolation.definite(message.trim(), path.trim());
        if (!definite) {
            violation = violation.asMaybeViolation();
        }
        boolean success = deltaDebugViolation(bench, violation);
        if (success) {
            new File("results/manualEval/" + folderNumber + "/" + bench.name + ".js.smallest").delete();
            new File("results/manualEval/" + folderNumber + "/" + bench.name + ".d.ts.smallest").delete();
        }

    }

    private boolean deltaDebugViolation(Benchmark bench, TypeViolation searchViolation) throws IOException {
        Util.isDeltaDebugging = true;

        AtomicInteger timeout = new AtomicInteger(TIMEOUT);

        BooleanSupplier predicate = () -> {
            try {
                TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(bench, timeout.get());

                return result.detectedViolations.asMap()
                        .values().stream()
                        .flatMap(Collection::stream)
                        .anyMatch(typeViolation ->
                                Util.simplifyPath(typeViolation.path).equals(searchViolation.path)
                                && typeViolation.message.equals(searchViolation.message)
                                && typeViolation.definite == searchViolation.definite
                        );
            } catch (Throwable e) {
                e.printStackTrace();
                return false;
            }
        };

        if (!predicate.getAsBoolean()) {
            Util.isDeltaDebugging = false;
            if (!predicate.getAsBoolean()) {
                return false;
            }
        }

        do {
            timeout.set(timeout.get() / 2);
        } while (timeout.get() >= 20 && predicate.getAsBoolean());

        timeout.set(timeout.get() * 2 + 15);

        DeltaDebug.debug(bench.dTSFile, predicate);
        DeltaDebug.debug(bench.jsFile, predicate);
        Util.isDeltaDebugging = false;
        return true;
    }
}
