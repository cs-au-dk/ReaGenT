package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmark.Benchmark;
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
        Benchmark bench = RunBenchmarks.benchmarks.get("PixiJS");
        String testPath = ".renderer.CONTEXT_UID";
        String typeof = "undefined";

        String driver = Util.readFile(Main.getFolderPath(bench) + Main.TEST_FILE_NAME);
        List<String> paths = Arrays.stream(driver.split(Pattern.quote("\n")))
                .map(String::trim)
                .map(str -> str.replaceAll("\r", ""))
                .filter(line -> line.startsWith("// path:"))
                .map(str -> str.substring("// path: ".length(), str.lastIndexOf(" type: ")))
                .map(String::trim)
                .collect(Collectors.toList());

        bench = bench.withPathsToTest(paths);

        Main.generateSmallestDriver(bench, testHasTypeError(bench, new OutputParser.TypeError(testPath, "", typeof, "", "")));
    }

    public static BooleanSupplier testHasTypeError(Benchmark bench, OutputParser.TypeError typeError) {
        return () -> {
            OutputParser.RunResult result;
            try {
                result = OutputParser.parseDriverResult(Main.runBenchmark(bench));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return result.typeErrors.stream().anyMatch(te -> te.getPath().contains(typeError.getPath()) && te.typeof.equals(typeError.typeof));
        };
    }
}
