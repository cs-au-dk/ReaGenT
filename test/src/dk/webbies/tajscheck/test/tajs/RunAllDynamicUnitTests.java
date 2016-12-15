package dk.webbies.tajscheck.test.tajs;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.test.dynamic.UnitTests;
import dk.webbies.tajscheck.util.MultiMap;
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

import static org.junit.Assert.assertTrue;

/**
 * Created by erik1 on 15-12-2016.
 */
@RunWith(Parameterized.class)
public class RunAllDynamicUnitTests {

    @SuppressWarnings("WeakerAccess")
    @Parameterized.Parameter
    public String folderName;

    private static final Set<String> blackList = new HashSet<>(Arrays.asList("genericIndexedAccess", "mappedTypes"));

    @SuppressWarnings("ConstantConditions")
    @Parameterized.Parameters(name = "{0}")
    public static List<String> getSmallDriverPaths() {
        return Arrays.stream(new File("test/unit/").listFiles()).filter(File::isDirectory).map(File::getName).filter(Util.not(blackList::contains)).collect(Collectors.toList());
    }

    @Test
    public void runSmallDriver() throws Exception {
        MultiMap<String, AssertionResult> result = TAJSUnitTests.run("../unit/" + folderName);

        System.out.println(TAJSUnitTests.prettyResult(result));
    }

    @Test
    public void santityCheckAnalysis() throws Exception {
        // Trying to bootstrap the library with itself, here it is very spurious if any warning is emitted.

        Benchmark bench = UnitTests.benchFromFolder(folderName).withLoadMethod(Benchmark.LOAD_METHOD.BOOTSTRAP).withTAJS();
        Main.writeFullDriver(bench);

        MultiMap<String, AssertionResult> result = TAJSUnitTests.runTAJS(Main.getTestFilePath(bench, Main.TEST_FILE_NAME));

        String printedResult = TAJSUnitTests.prettyResult(result);

        System.out.println(printedResult);;

        assertTrue(printedResult.isEmpty());
    }
}
