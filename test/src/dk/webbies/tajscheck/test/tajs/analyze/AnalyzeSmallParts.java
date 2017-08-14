package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.RunSmall;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.tajs.TAJSUtil;
import dk.webbies.tajscheck.testcreator.test.LoadModuleTest;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 19-12-2016.
 */
@RunWith(Parameterized.class)
public class AnalyzeSmallParts extends TestCase {
    private final Benchmark benchmark;
    private final Set<String> failedBenchmarks;

    public static List<Benchmark> getBenchmarks() {
        return RunBenchmarks.getBenchmarks().stream()
//                .filter(bench -> blacklist.contains(bench.name))
//                .filter(bench -> timeouts.contains(bench.name))
                .filter(bench -> AnalyzeBenchmarks.whitelist.contains(bench.name))
                .collect(Collectors.toList());
    }

    @Parameterized.Parameters(name = "{0} - {1}")
    public static List<Object[]> data() {
        List<Object[]> result = new ArrayList<>();
        Set<String> failedBenchmarks = new HashSet<>();
        for (Benchmark benchmark : getBenchmarks()) {
            result.add(new Object[]{benchmark, null, failedBenchmarks});
            List<List<String>> pathsToTest = RunSmall.getPathsToTest(benchmark, Integer.MAX_VALUE, 1);
            for (List<String> paths : pathsToTest) {
                assert paths.size() == 1;
                String path = paths.iterator().next();
                result.add(new Object[]{benchmark, path, failedBenchmarks});
            }
        }

        return result;
    }

    public AnalyzeSmallParts(Benchmark benchmark, String pathToAnalyze, Set<String> failedInitializations) {
        if (pathToAnalyze != null) {
            this.benchmark = benchmark.withPathsToTest(Collections.singletonList(pathToAnalyze));
        } else {
            this.benchmark = benchmark.withOptions(options -> options.setOnlyInitialize(true));
        }
        this.failedBenchmarks = failedInitializations;
    }

    @Test
    public void analyze() throws Exception {
        if (failedBenchmarks.contains(benchmark.name)) {
            System.err.println("This benchmark has either timed out, or failed initialization before, I'm skipping it.");
            return;
        }
        try {
            TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchmark, 90);
            System.out.println(result);
            if (result.testPerformed.stream().allMatch(LoadModuleTest.class::isInstance) && result.detectedViolations.asMap().keySet().size() > 0) {
                failedBenchmarks.add(benchmark.name);
            }

        } catch (TimeoutException ignored) {
            failedBenchmarks.add(benchmark.name);
            System.err.println("Timeout");
        }
    }
}
