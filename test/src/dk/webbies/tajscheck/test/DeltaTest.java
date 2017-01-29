package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.benchmarks.Benchmark.RUN_METHOD.BOOTSTRAP;

/**
 * Created by erik1 on 10-01-2017.
 */
public class DeltaTest {
    public static void main(String[] args) throws IOException {
        Benchmark bench = RunBenchmarks.benchmarks.get("Modernizr");
        String testPath = "Modernizr.prefixedCSS";
        Main.generateSmallestDriver(bench, testHasTypeError(bench, testPath));
    }

    public static BooleanSupplier testHasTypeError(Benchmark bench, String testPath) {
        return () -> {
            OutputParser.RunResult result;
            try {
                result = OutputParser.parseDriverResult(Main.runBenchmark(bench));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return result.typeErrors.stream().map(OutputParser.TypeError::getPath).anyMatch(str -> str.equals(testPath));
        };
    }
}
