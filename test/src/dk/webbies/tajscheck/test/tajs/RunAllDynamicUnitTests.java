package dk.webbies.tajscheck.test.tajs;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.test.dynamic.UnitTests;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.test.tajs.AssertionResult.BooleanResult.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by erik1 on 15-12-2016.
 */
@RunWith(Parameterized.class)
public class RunAllDynamicUnitTests {

    @SuppressWarnings("WeakerAccess")
    @Parameterized.Parameter
    public String folderName;

    // unit-tests that for some reason should not run.
    private static final Set<String> blackList = new HashSet<>(Arrays.asList(
            // has symbols
            "complexOverloads",
            "symbol",

            "browserMultipleProperties", // incompatible with the NODE assumption used here.

            "unsoundSiblings",
            "mappedTypes",
            "basicMemomizeExample",
            "genericIndexedAccess",
            "exponentialComplexity"
    ));

    @SuppressWarnings("ConstantConditions")
    @Parameterized.Parameters(name = "{0}")
    public static List<String> getSmallDriverPaths() {
        return Arrays.stream(new File("test/unit/").listFiles())
                .filter(File::isDirectory)
                .map(File::getName)
                .filter(Util.not(blackList::contains))
                .filter(folderName ->
                    new File(UnitTests.benchFromFolder(folderName).dTSFile).exists()
        ).collect(Collectors.toList());
    }

    @Test
    public void analyzeUnitTestDriver() throws Exception {
        if (!new File(UnitTests.benchFromFolder(folderName).jsFile).exists()) {
            return;
        }
        MultiMap<String, AssertionResult> result = TAJSUnitTests.run("../unit/" + folderName);

        System.out.println(TAJSUtil.prettyResult(result, (r) -> true));
    }

    @Test
    public void sanityCheckAnalysis() throws Exception {
        // Trying to bootstrap the library with itself, here it is very spurious if any warning is emitted.

        Benchmark bench = UnitTests.benchFromFolder(folderName).withRunMethod(Benchmark.RUN_METHOD.BOOTSTRAP).withOptions(options -> options.setUseAssertTypeFunctions(false).setCheckDepthReport(options.checkDepthUseValue)).useTAJS();

        MultiMap<String, AssertionResult> result = TAJSUtil.run(bench);

        for (Map.Entry<String, Collection<AssertionResult>> entry : result.asMap().entrySet()) {
            for (AssertionResult tajsResult : entry.getValue()) {
                assertThat(tajsResult.result, is(not(DEFINITELY_FALSE)));
            }
        }
    }
}
