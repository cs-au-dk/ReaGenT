package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;

import java.util.function.Function;

public class RunASingleBenchmark {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new RuntimeException("Accepts only 2 arguments: [benchmarkName], [mode]");
        }
        Benchmark benchmark = RunBenchmarks.benchmarks.get(args[0]);
        if (benchmark == null) {
            throw new RuntimeException("Library: " + args[0] + " not found");
        }

        Function<CheckOptions.Builder, StaticOptions.Builder> mode = CompareModesEvaluation.modes.get(args[1]);
        if (mode == null) {
            throw new RuntimeException("Mode: " + args[1] + " not found. Possible modes are: " + CompareModesEvaluation.modes.keySet().toString());
        }

        benchmark = benchmark.withOptions(mode);
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchmark, 3 * 60 * 60);
        for (int i = 0; i < 10; i++) {
            System.out.println();
        }
        System.out.println(result);
    }
}
