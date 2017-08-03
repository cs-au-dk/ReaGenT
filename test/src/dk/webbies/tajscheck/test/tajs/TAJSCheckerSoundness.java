package dk.webbies.tajscheck.test.tajs;


import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.test.dynamic.UnitTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dk.webbies.tajscheck.test.tajs.TAJSUnitTests.*;

@RunWith(Parameterized.class)
public class TAJSCheckerSoundness { // TODO: A lot of things fails here.
    @Parameterized.Parameter
    public Benchmark bench;

    @SuppressWarnings("ConstantConditions")
    @Parameterized.Parameters(name = "{0}")
    public static List<Benchmark> getBenchmarks() {
        List<Benchmark> result = new ArrayList<>();

        Arrays.stream(new File("test/unit/").listFiles())
                .filter(File::isDirectory)
                .map(File::getName)
//                    .filter(Util.not(blackList::contains))
                .map(UnitTests::benchFromFolder)
                .filter(bench ->
                    new File(bench.dTSFile).exists()
                )
                .forEach(result::add);

        Arrays.stream(new File("test/tajsUnit/").listFiles())
                .filter(File::isDirectory)
                .map(File::getName)
//                    .filter(Util.not(blackList::contains))
                .map(TAJSUnitTests::benchFromFolder)
                .filter(bench ->
                    new File(bench.dTSFile).exists()
                )
                .forEach(result::add);

        return result;
    }

    @Test
    public void testSoundness() throws Exception {
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(bench.withRunMethod(Benchmark.RUN_METHOD.BOOTSTRAP).withOptions(options -> options.setConstructAllTypes(true)), 60);

        expect(result).hasNoViolations();
    }
}


