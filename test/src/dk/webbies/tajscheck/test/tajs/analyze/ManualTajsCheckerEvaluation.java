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
    private static String mode;

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
            "CodeMirror",
            "Moment.js",
            "semver",

            "reveal.js",
            "box2dweb",
            "mime",
            "QUnit",
            "PleaseJS",
            "Knockout",
//            "RxJS", Has its actual types spread out in many sub-files.
            "async",
            "uuid",
            "Intro.js",
            "CreateJS",
            "Handlebars",
            "highlight.js",
            "bluebird",
            "Medium Editor"
    ));

    @Test
    public void runInALoop() throws Exception {
        List<String> benchmarks = Arrays.asList("Hammer.js");//BigLibEvaluation.benchmarksToEvaluate;
        if (benchmarks.size() < 20) {
//            throw new RuntimeException("I don't think you meant to do this " + benchmarks.size());
        }
        //noinspection InfiniteLoopStatement
        while (true) {
            findATypeError(benchmarks.get(new Random().nextInt(benchmarks.size())));
        }
    }

    @Test
    public void doASingleEval() throws Exception {
        findATypeError("uuid");
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
        Benchmark bench = CompareModesEvaluation.modes.get(mode).getLeft().apply(RunBenchmarks.benchmarks.get(benchmarkName));
        bench = bench.withOptions(CompareModesEvaluation.modes.get(mode).getRight());

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
        new File("results/" + mode).mkdir();

        int counter = 0;
        while (new File("results/" + mode + "/" + counter).exists()) {
            counter++;
        }

        new File("results/" + mode + "/" + counter).mkdir();

        Util.writeFile("results/" + mode + "/" + counter + "/" + bench.name + "_violation.txt", searchViolation.toString());
        Util.writeFile("results/" + mode + "/" + counter + "/" + bench.name + "_violation_full.txt", fullViolation.toString(Integer.MAX_VALUE));

        String dTsFile = Util.readFile(bench.dTSFile);
        String jsFile = Util.readFile(bench.jsFile);

        if (!CAN_DELTA_DEBUG.contains(bench.name)) {
            return;
        }

        Util.writeFile("results/" + mode + "/" + counter + "/" + bench.name + ".d.ts.original", dTsFile);
        Util.writeFile("results/" + mode + "/" + counter + "/" + bench.name + ".d.ts", dTsFile);
        Util.writeFile("results/" + mode + "/" + counter + "/" + bench.name + ".js.original", jsFile);
        Util.writeFile("results/" + mode + "/" + counter + "/" + bench.name + ".js", jsFile);


        if (DO_DELTA_DEBUGGING) {
            bench = bench
                    .withJsFile("results/" + mode + "/" + counter + "/" + bench.name + ".js")
                    .withDecl("results/" + mode + "/" + counter + "/" + bench.name + ".d.ts");

            boolean success = deltaDebugViolation(bench, searchViolation);

            if (success) {
                new File("results/" + mode + "/" + counter + "/" + bench.name + ".js.smallest").delete();
                new File("results/" + mode + "/" + counter + "/" + bench.name + ".d.ts.smallest").delete();
            }
        } else {
            Util.writeFile("results/" + mode + "/" + counter + "/skipped_delta.txt", "");
        }
    }

    @SuppressWarnings({"ConstantConditions", "Duplicates"})
    public void continueWithFolder(int counter) throws IOException {
        List<String> violationNames = Arrays.stream(new File("results/" + mode + "/" + counter).listFiles()).map(File::getName).filter(name -> name.contains("violation_full.txt")).collect(Collectors.toList());
        assert violationNames.size() == 1;
        String benchmarkName = Util.removeSuffix(violationNames.iterator().next(), "_violation_full.txt");

        System.out.println("Benchmark: " + benchmarkName);


        if (new File("results/" + mode + "/" + counter + "/skipped_delta.txt").exists()) {
            System.out.println("Start delta-debugging.");
            new File("results/" + mode + "/" + counter + "/skipped_delta.txt").delete();
            Util.copyFile(
                    "results/" + mode + "/" + counter + "/" + benchmarkName + ".js",
                    "results/" + mode + "/" + counter + "/" + benchmarkName + ".js.smallest"
            );
            Util.copyFile(
                    "results/" + mode + "/" + counter + "/" + benchmarkName + ".d.ts",
                    "results/" + mode + "/" + counter + "/" + benchmarkName + ".d.ts.smallest"
            );
            continueDeltaDebugging(counter, benchmarkName);
            return;
        }

        if (new File("results/" + mode + "/" + counter + "/" + benchmarkName + ".js.smallest").exists()) {
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
        assert new File("results/" + mode + "/" + folderNumber + "/" + benchmarkName + ".d.ts.original").exists();

        Benchmark bench = getBenchmark(folderNumber, benchmarkName);

        String smallJs = Util.readFile("results/" + mode + "/" + folderNumber + "/" + bench.name + ".js.smallest");
        String smallDTS = Util.readFile("results/" + mode + "/" + folderNumber + "/" + bench.name + ".d.ts.smallest");

        Util.writeFile(bench.jsFile, smallJs);
        Util.writeFile(bench.dTSFile, smallDTS);

        TypeViolation violation = getViolation(folderNumber, benchmarkName);
        boolean success = deltaDebugViolation(bench, violation);
        if (success) {
            new File("results/" + mode + "/" + folderNumber + "/" + bench.name + ".js.smallest").delete();
            new File("results/" + mode + "/" + folderNumber + "/" + bench.name + ".d.ts.smallest").delete();
        }

    }

    private TypeViolation getViolation(int folderNumber, String benchmarkName) throws IOException {
        String violationString = Util.readFile("results/" + mode + "/" + folderNumber + "/" + benchmarkName + "_violation.txt");

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
                    .withJsFile("results/" + mode + "/" + folderNumber + "/" + benchmarkName + ".js")
                    .withDecl("results/" + mode + "/" + folderNumber + "/" + benchmarkName + ".d.ts");
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

                    System.out.println(result);

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

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new RuntimeException("You did not specify the mode");
        }
        ManualTajsCheckerEvaluation.mode = args[0];
        new ManualTajsCheckerEvaluation().runInALoop();
    }
}
