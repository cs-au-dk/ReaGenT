package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.RunSmall;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.tajs.AssertionResult;
import dk.webbies.tajscheck.test.tajs.TAJSUtil;
import dk.webbies.tajscheck.util.MultiMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 19-12-2016.
 */
@RunWith(Parameterized.class)
public class AnalyzeBenchmarksSmall {

    @Parameterized.Parameters(name = "{0} - {1}")
    public static Collection<Object[]> createParameters(){
        List<String> benchmarks = RunBenchmarks.benchmarks.keySet().stream()
//                .filter(bench -> AnalyzeBenchmarks.blacklist.contains(bench.name))
//                .filter(bench -> AnalyzeBenchmarks.timeouts.contains(bench.name))
//                .filter(bench -> AnalyzeBenchmarks.whitelist.contains(bench.name))
                .filter(AnalyzeBenchmarks.simpleBenchmarks::contains)
                .collect(Collectors.toList());


        ArrayList<Object[]> result = new ArrayList<>();
        for (String benchmarkName : benchmarks) {
            Benchmark benchmark = RunBenchmarks.benchmarks.get(benchmarkName).useTAJS();

            List<List<String>> paths = RunSmall.getPathsToTest(benchmark, Integer.MAX_VALUE, 1);

            for (List<String> path : paths) {
                assert path.size() == 1;
                result.add(new Object[]{benchmarkName, path.iterator().next()});
            }
        }

        return result;
    }

    private final Benchmark bench;
    private final String path;

    public AnalyzeBenchmarksSmall(String benchmarkName, String path){
        this.bench = RunBenchmarks.benchmarks.get(benchmarkName).useTAJS();
        this.path = path;
    }

    @Test
    public void run() throws Exception {
        Benchmark bench = this.bench.withPathsToTest(Collections.singletonList(path));

        MultiMap<String, AssertionResult> result = TAJSUtil.run(bench);

        System.out.println(TAJSUtil.prettyResult(result, (r) -> true));
    }
}
