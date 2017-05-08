package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.RunSmall;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.tajs.AssertionResult;
import dk.webbies.tajscheck.test.tajs.TAJSUtil;
import dk.webbies.tajscheck.util.MultiMap;
import junit.framework.*;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import java.util.Collections;
import java.util.List;

@RunWith(AllTests.class)
public class SuiteTest {
    public static TestSuite suite() {
        TestSuite bigSuite = new TestSuite("AnalyzeSmall");

        for (String benchmarkName : AnalyzeBenchmarks.simpleBenchmarks) {
            Benchmark benchmark = RunBenchmarks.benchmarks.get(benchmarkName);

            TestSuite benchSuite = new TestSuite(benchmarkName.replace(".", ""));
            bigSuite.addTest(benchSuite);

            List<List<String>> paths = RunSmall.getPathsToTest(benchmark, Integer.MAX_VALUE, 1);

            for (List<String> path : paths) {
                assert path.size() == 1;
                benchSuite.addTest(new InnerTest(benchmark, path.iterator().next()));
            }
        }


        return bigSuite;
    }

    public static class InnerTest extends TestCase {
        private final Benchmark bench;
        private final String path;

        InnerTest(Benchmark bench, String path) {
            this.bench = bench;
            this.path = path;
        }


        @Override
        protected void runTest() throws Throwable {
            Benchmark bench = this.bench.withPathsToTest(Collections.singletonList(path));

            MultiMap<String, AssertionResult> result = TAJSUtil.run(bench);

            System.out.println(TAJSUtil.prettyResult(result, (r) -> true));
        }

        @Override
        public String getName() {
            return bench.name + " - " + path;
        }
    }
}
