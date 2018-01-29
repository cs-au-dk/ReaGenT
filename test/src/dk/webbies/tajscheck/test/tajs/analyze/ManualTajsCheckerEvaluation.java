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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class ManualTajsCheckerEvaluation {

    private static final int TIMEOUT = 20 * 60; // in seconds
    private static final boolean DO_DELTA_DEBUGGING = true;
    private static String outputDir = "Intro.js";

    private static final Set<String> CAN_DELTA_DEBUG = new HashSet<>(Arrays.asList(
            "Hammer.js",
            "PDF.js",
            "PhotoSwipe",
            "Swiper",
            "pathjs",
            "Redux",
            "Sortable",
            "accounting.js",
            "axios",
            "lunr.js",
            "CodeMirror", // TODO: Get some bugs from this one.
            "Moment.js", // TODO: Get some bugs from this one.

            "reveal.js",
            "box2dweb",
            "QUnit",
            "PleaseJS",
            "Knockout",
//            "RxJS", Has its actual types spread out in many sub-files.
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
        while(true) {
            findATypeError("Intro.js");
        }
    }

    private static final Set<String> cleanBenchmarks = new HashSet<>();

    private void findATypeError(String benchmarkName) throws Exception {
        findATypeError(benchmarkName, -1);
    }

    @SuppressWarnings("SameParameterValue")
    private void findATypeError(String benchmarkName, int index) throws Exception {
        if (cleanBenchmarks.contains(benchmarkName)) {
            return;
        }
        Benchmark bench = RunBenchmarks.benchmarks.get(benchmarkName).withOptions(AnalyzeBenchmarks.options());
        Benchmark patched = bench.patched();
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

        TypeViolation typeViolation;
        if (index < 0) {
            Collections.shuffle(typeViolations);
            typeViolation = typeViolations.get(0);
        } else {
            typeViolation = typeViolations.get(index);
        }

        TypeViolation fullViolation = typeViolation;

        typeViolation = typeViolation.withPath(Util.simplifyPath(typeViolation.path));
        reportViolation(bench, typeViolation, fullViolation);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void reportViolation(Benchmark bench, TypeViolation searchViolation, TypeViolation fullViolation) throws IOException {
        new File("results").mkdir();
        new File("results/" + outputDir).mkdir();

        int counter = 0;
        while (new File("results/" + outputDir + "/" + counter).exists()) {
            counter++;
        }

        new File("results/" + outputDir + "/" + counter).mkdir();

        Util.writeFile("results/" + outputDir + "/" + counter + "/" + bench.name + "_violation.txt", searchViolation.toString());
        Util.writeFile("results/" + outputDir + "/" + counter + "/" + bench.name + "_violation_full.txt", fullViolation.toString(Integer.MAX_VALUE));

        String dTsFile = Util.readFile(bench.dTSFile);
        String jsFile = Util.readFile(bench.jsFile);

        if (!CAN_DELTA_DEBUG.contains(bench.name)) {
            return;
        }

        Util.writeFile("results/" + outputDir + "/" + counter + "/" + bench.name + ".d.ts.original", dTsFile);
        Util.writeFile("results/" + outputDir + "/" + counter + "/" + bench.name + ".d.ts", dTsFile);
        Util.writeFile("results/" + outputDir + "/" + counter + "/" + bench.name + ".js.original", jsFile);
        Util.writeFile("results/" + outputDir + "/" + counter + "/" + bench.name + ".js", jsFile);


        if (DO_DELTA_DEBUGGING) {
            bench = bench
                    .withJsFile("results/" + outputDir + "/" + counter + "/" + bench.name + ".js")
                    .withDecl("results/" + outputDir + "/" + counter + "/" + bench.name + ".d.ts");

            boolean success = deltaDebugViolation(bench, searchViolation);

            if (success) {
                new File("results/" + outputDir + "/" + counter + "/" + bench.name + ".js.smallest").delete();
                new File("results/" + outputDir + "/" + counter + "/" + bench.name + ".d.ts.smallest").delete();
            }
        } else {
            Util.writeFile("results/" + outputDir + "/" + counter + "/skipped_delta.txt", "");
        }
    }

    @Test
    public void deltaDebugSuppressed() throws IOException {

    }

    @Test
    public void doTheMissingStuff() throws IOException {
        // Wouldn't delta-debug
        continueWithFolder(19);
        continueWithFolder(79);
        continueWithFolder(100);
        continueWithFolder(95);

        continueWithFolder(143); // TODO: I don't know. Is delta-debugged.

        // redux.
//        continueWithFolder(59);
//        continueWithFolder(73);
//        continueWithFolder(60);
//        continueWithFolder(55);
//        continueWithFolder(74);
//        continueWithFolder(151);

//        continueWithFolder(98); // might help with 133, 81, 121
//        continueWithFolder(120); // might help 155, 61, 93, 22, 64, 108
//        continueWithFolder(86); // same answer for 116, 26, 20, 21 // TODO: This one not done.
//        continueWithFolder(103);
//        continueWithFolder(68);
//        continueWithFolder(124);
//        continueWithFolder(96);
//        continueWithFolder(158);
//        continueWithFolder(80);
//        continueWithFolder(71);
//        continueWithFolder(72); // same answer for 84, 136
//        continueWithFolder(107); // same answer for 87, 152

//        continueWithFolder(134); // same answer for 118

    }

    @Test
    public void tmpStuff() throws IOException {

    }

    @SuppressWarnings({"ConstantConditions", "Duplicates"})
    public void continueWithFolder(int counter) throws IOException {
        List<String> violationNames = Arrays.stream(new File("results/" + outputDir + "/" + counter).listFiles()).map(File::getName).filter(name -> name.contains("violation")).collect(Collectors.toList());
        assert violationNames.size() == 1;
        String benchmarkName = Util.removeSuffix(violationNames.iterator().next(), "_violation.txt");

        System.out.println("Benchmark: " + benchmarkName);


        if (new File("results/" + outputDir + "/" + counter + "/skipped_delta.txt").exists()) {
            System.out.println("Start delta-debugging.");
            new File("results/" + outputDir + "/" + counter + "/skipped_delta.txt").delete();
            Util.copyFile(
                    "results/" + outputDir + "/" + counter + "/" + benchmarkName + ".js",
                    "results/" + outputDir + "/" + counter + "/" + benchmarkName + ".js.smallest"
            );
            Util.copyFile(
                    "results/" + outputDir + "/" + counter + "/" + benchmarkName + ".d.ts",
                    "results/" + outputDir + "/" + counter + "/" + benchmarkName + ".d.ts.smallest"
            );
            continueDeltaDebugging(counter, benchmarkName);
            return;
        }

        if (new File("results/" + outputDir + "/" + counter + "/" + benchmarkName + ".js.smallest").exists()) {
            System.out.println("Continue delta-debugging");
            continueDeltaDebugging(counter, benchmarkName);
            return;
        }

        System.out.println("Nothing to do here. Testing the predicate.");
        Benchmark benchmark = getBenchmark(counter, benchmarkName);
        TypeViolation violation = getViolation(counter, benchmarkName);
        BooleanSupplier predicate = makePredicate(benchmark, violation, TIMEOUT);
        if (predicate.getAsBoolean()) {
            System.out.println("Success");
            System.err.println("Success");
        } else {
            System.out.println("Fail");
            System.err.println("Fail");
        }
    }

    public void continueDeltaDebugging(int folderNumber, String benchmarkName) throws IOException {
        assert new File("results/" + outputDir + "/" + folderNumber + "/" + benchmarkName + ".d.ts.original").exists();

        Benchmark bench = getBenchmark(folderNumber, benchmarkName);

        String smallJs = Util.readFile("results/" + outputDir + "/" + folderNumber + "/" + bench.name + ".js.smallest");
        String smallDTS = Util.readFile("results/" + outputDir + "/" + folderNumber + "/" + bench.name + ".d.ts.smallest");

        Util.writeFile(bench.jsFile, smallJs);
        Util.writeFile(bench.dTSFile, smallDTS);

        TypeViolation violation = getViolation(folderNumber, benchmarkName);
        boolean success = deltaDebugViolation(bench, violation);
        if (success) {
            new File("results/" + outputDir + "/" + folderNumber + "/" + bench.name + ".js.smallest").delete();
            new File("results/" + outputDir + "/" + folderNumber + "/" + bench.name + ".d.ts.smallest").delete();
        }

    }

    private TypeViolation getViolation(int folderNumber, String benchmarkName) throws IOException {
        String violationString = Util.readFile("results/" + outputDir + "/" + folderNumber + "/" + benchmarkName + "_violation.txt");

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
        return violation;
    }

    private Benchmark getBenchmark(int folderNumber, String benchmarkName) {
        return RunBenchmarks.benchmarks.get(benchmarkName).withOptions(AnalyzeBenchmarks.options())
                    .withJsFile("results/" + outputDir + "/" + folderNumber + "/" + benchmarkName + ".js")
                    .withDecl("results/" + outputDir + "/" + folderNumber + "/" + benchmarkName + ".d.ts");
    }

    private boolean deltaDebugViolation(Benchmark bench, TypeViolation searchViolation) throws IOException {
        Util.isDeltaDebugging = true;

        AtomicInteger timeout = new AtomicInteger(TIMEOUT);

        BooleanSupplier predicate = makePredicate(bench, searchViolation, timeout.get());

        if (!predicate.getAsBoolean()) {
            Util.isDeltaDebugging = false;
            if (!predicate.getAsBoolean()) {
                return false;
            }
        }

        do {
            timeout.set(timeout.get() / 2);
            predicate = makePredicate(bench, searchViolation, timeout.get());
        } while (timeout.get() >= 20 && predicate.getAsBoolean());

        timeout.set(timeout.get() * 2 + 15);
        predicate = makePredicate(bench, searchViolation, timeout.get());

        DeltaDebug.debug(bench.dTSFile, predicate);
        DeltaDebug.debug(bench.jsFile, predicate);
        predicate.getAsBoolean();
        Util.isDeltaDebugging = false;
        return true;
    }

    private BooleanSupplier makePredicate(Benchmark bench, TypeViolation searchViolation, int timeout) {
        return () -> {
                try {
                    TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(bench, timeout);

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
    }

    public static void main(String[] args) {
        ManualTajsCheckerEvaluation o = new ManualTajsCheckerEvaluation();
        ManualTajsCheckerEvaluation.outputDir = args[0];
        int index = -1;
        try { index = Integer.parseInt(args[1]); } catch(Exception e){}
        try {
            while (true) {
                o.findATypeError(args[0], index);
            }
        } catch(Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}
