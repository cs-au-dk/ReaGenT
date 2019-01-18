package dk.webbies.tajscheck.test.tajs;

import dk.webbies.tajscheck.DynamicMain;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.tajs.analyze.AnalyzeBenchmarks;
import dk.webbies.tajscheck.testcreator.TestCreator;
import dk.webbies.tajscheck.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
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
        List<Benchmark> potentialTests = Util.concat(unitTests, AnalyzeBenchmarks.getBenchmarks());
        return potentialTests.stream()
                .filter(bench -> !blackList.contains(bench.name))
                .collect(Collectors.toList());
    }

    // TODO: Currently failing tests.
    private static final Set<String> blackList = new HashSet<>(Arrays.asList(
            "tajsunit-correctUnion",
            "PhotoSwipe",
            "accounting.js",
            "Swiper",
            "Intro.js", // sometimes fails on CI
            "PeerJS", // sometimes fails on CI
            "Hammer.js" // failed once on CI.
    ));

    @Test
    public void testConsistency() throws Exception {
        BenchmarkInfo info;
        try {
            info = BenchmarkInfo.create(bench);
        } catch (AssertionError e) {
            return; // it crashed because apparently this is a browser test, we just skip those.
        }

        List<dk.webbies.tajscheck.testcreator.test.Test> tests = new TestCreator(info).createTests();

        TAJSUtil.TajsAnalysisResults tajsResult = TAJSUtil.runNoDriverTAJS(bench, 60, info, tests);


        {
            String tstestDriver = DynamicMain.generateFullDriver(info, tests, null);
            Util.writeFile(DynamicMain.getFolderPath(bench) + DynamicMain.TEST_FILE_NAME, tstestDriver);
        }

        OutputParser.RunResult tsTestResult = OutputParser.parseDriverResult(DynamicMain.runBenchmark(bench));

        Set<dk.webbies.tajscheck.testcreator.test.Test> testsCalledByTsTest = tsTestResult.getTestsCalled().stream().map(tests::get).collect(Collectors.toSet());

        tajsResult.testPerformed.forEach(testPerformed -> {
            assertThat(testsCalledByTsTest, hasItem(testPerformed));
        });

        // TODO: All the found errors from TSTest appear is TAJSChecker. (they currently have slightly different layout).
    }
}
