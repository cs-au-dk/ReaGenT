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

    @Test
    public void doEval() throws Exception {
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
        deltaDebugToFindViolation(bench, TypeViolation.definite(typeViolation.message, Util.simplifyPath(typeViolation.path)));
    }

    private void deltaDebugToFindViolation(Benchmark bench, TypeViolation searchViolation) throws IOException {
        BooleanSupplier predicate = () -> {
            try {
                TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(bench, TIMEOUT);

                return result.detectedViolations.asMap().values().stream().flatMap(Collection::stream).anyMatch(typeViolation -> Util.simplifyPath(typeViolation.path).equals(searchViolation.path) && typeViolation.message.equals(searchViolation.message));
            } catch (Throwable e) {
                e.printStackTrace();
                return false;
            }
        };
        String dTsFile = Util.readFile(bench.dTSFile);
        String jsFile = Util.readFile(bench.jsFile);

        DeltaDebug.debug(bench.dTSFile, predicate);
        DeltaDebug.debug(bench.jsFile, predicate);

        new File("manualEval").mkdir();

        int counter = 0;
        while (new File("manualEval/" + counter).exists()) {
            counter++;
        }

        new File("manualEval/" + counter).mkdir();

        Util.writeFile("manualEval/" + counter + "/" + bench.name + ".d.ts", Util.readFile(bench.dTSFile));
        Util.writeFile("manualEval/" + counter + "/" + bench.name + ".d.ts.original", dTsFile);
        Util.writeFile("manualEval/" + counter + "/" + bench.name + ".js", Util.readFile(bench.jsFile));
        Util.writeFile("manualEval/" + counter + "/" + bench.name + ".js.original", jsFile);
        Util.writeFile("manualEval/" + counter + "/violation.txt", searchViolation.toString());

        Util.writeFile(bench.dTSFile, dTsFile);
        Util.writeFile(bench.jsFile, jsFile);
    }
}
