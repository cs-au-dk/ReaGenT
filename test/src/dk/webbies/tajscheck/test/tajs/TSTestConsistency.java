package dk.webbies.tajscheck.test.tajs;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.dynamic.UnitTests;
import dk.webbies.tajscheck.test.tajs.analyze.AnalyzeBenchmarks;
import dk.webbies.tajscheck.testcreator.TestCreator;
import dk.webbies.tajscheck.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;

@RunWith(Parameterized.class)
public class TSTestConsistency {
    @Parameterized.Parameter
    public Benchmark bench;

    @Parameterized.Parameters(name = "{0}")
    public static List<Benchmark> getBenchmarks() {
        List<Benchmark> unitTests = TAJSCheckerSoundness.getBenchmarks().stream()
                .filter(bench -> new File(bench.jsFile).exists())
                .map(bench -> {
                    if (bench.jsFile.contains("benchmarks")) {
                        return bench;
                    } else {
                        return bench.withOptions(options -> options.setCheckDepthReport(2).setCheckDepthUseValue(2));
                    }
                })
                .collect(Collectors.toList());
        return Util.concat(unitTests, AnalyzeBenchmarks.getBenchmarks());
    }

    // TODO: "unit-arrayType", "unit-never", "unit-numberIndexer", "unit-stringIndexer", "unit-testClass", "tajsunit-correctUnion", "tajsunit-numberIndexerFails", "PhotoSwipe", "accounting.js", "PleaseJS", "intro.js", "reveal.js", "Leaflet", "Backbone.js", "async", "Swiper", "PhotoSwipe", "accounting.js", "PleaseJS", "intro.js", "reveal.js", "Leaflet", "Backbone.js", "async", "Swiper"

    @Test
    public void testConsistency() throws Exception {
        BenchmarkInfo info;
        try {
            info = BenchmarkInfo.create(bench);
        } catch (AssertionError e) {
            return; // it crashed because apparently this is a browser test, we just skip those.
        }

        List<dk.webbies.tajscheck.testcreator.test.Test> tests = new TestCreator(info).createTests();

        TAJSUtil.TajsAnalysisResults tajsResult = TAJSUtil.runNoDriverTAJS(bench.jsFile, 60, info, tests);


        {
            String tstestDriver = Main.generateFullDriver(info, tests, null);
            Util.writeFile(Main.getFolderPath(bench) + Main.TEST_FILE_NAME, tstestDriver);
        }

        OutputParser.RunResult tsTestResult = OutputParser.parseDriverResult(Main.runBenchmark(bench));

        Set<dk.webbies.tajscheck.testcreator.test.Test> testsCalledByTsTest = tsTestResult.getTestsCalled().stream().map(tests::get).collect(Collectors.toSet());

        tajsResult.testPerformed.forEach(testPerformed -> {
            assertThat(testsCalledByTsTest, hasItem(testPerformed));
        });

        // TODO: All the found errors from TSTest appear is TAJSChecker.
    }
}
