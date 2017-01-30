package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

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
                result = OutputParser.parseDriverResult(Main.runBenchmark(bench, 2 * 60 * 1000));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                return false;
            }
            return result.typeErrors.stream().map(OutputParser.TypeError::getPath).anyMatch(str -> str.equals(testPath));
        };
    }
}
