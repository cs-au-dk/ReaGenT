package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.CheckOptions;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 10-01-2017.
 */
public class DeltaTest {
    public static void main(String[] args) throws IOException {
        Benchmark bench = RunBenchmarks.benchmarks.get("async");
        bench = bench.withOptions(CheckOptions.errorFindingOptions(bench.options.getBuilder().setMaxIterationsToRun(100 * 1000).build()));
        String testPath = "window.async.timesLimit.[arg3].[arg1]";
        String typeof = "object";
//        String expected = "(undefined or (a non null value and Array and (arrayIndex: (null or ([any] and a non null value and a generic type marker (._isUnconstrainedGeneric))))))";

        String driver = Util.readFile(Main.getFolderPath(bench) + Main.TEST_FILE_NAME);
        List<String> paths = Arrays.stream(driver.split(Pattern.quote("\n")))
                .map(String::trim)
                .map(str -> str.replaceAll("\r", ""))
                .filter(line -> line.startsWith("// path:"))
                .map(str -> str.substring("// path: ".length(), str.lastIndexOf(" type: ")))
                .map(String::trim)
                .collect(Collectors.toList());

//        bench = bench.withPathsToTest(paths);

        while (true) {
            try {
                Main.generateSmallestDriver(bench, testHasTypeError(bench, new OutputParser.TypeError(testPath, "", typeof, "", "", "")));
                break;
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
                System.err.println("Trying again!");
                // continue
            }
        }
    }

    public static BooleanSupplier testHasTypeError(Benchmark bench, OutputParser.TypeError typeError) {
        return () -> {
            OutputParser.RunResult result;
            try {
                result = OutputParser.parseDriverResult(Main.runBenchmark(bench));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            result.typeErrors.stream().map(OutputParser.TypeError::getPath).distinct().forEach(System.out::println);
            return result.typeErrors.stream().anyMatch(te -> te.getPath().contains(typeError.getPath()) && te.typeof.equals(typeError.typeof) && te.JSON.contains("null") && te.JSON.contains("isUnconstrained"));
        };
    }
}
