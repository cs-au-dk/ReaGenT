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
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.test.tajs.TAJSUnitTests.*;

@RunWith(Parameterized.class)
public class TAJSCheckerSoundness {
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

        return result.stream()
                .filter(bench -> !bench.name.equals("unit-exponentialComplexity"))
                .filter(bench -> !createsIntersection.contains(bench.name))
                .filter(bench -> !blackList.contains(bench.name))
                .collect(Collectors.toList());
//        return result;
    }

    @Test
    public void testSoundness() throws Exception {
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(bench.withRunMethod(Benchmark.RUN_METHOD.BOOTSTRAP).withOptions(options -> options.setConstructAllTypes(true)), 60);
        System.out.println(result);
        expect(result).hasNoViolations();
    }

    private static final List<String> createsIntersection = Arrays.asList(
            "unit-canHaveError",
            "unit-canHaveErrorBrowser",
            "unit-complexSanityCheck20",
            "unit-complexSanityCheck23",
            "unit-genericExtendMethod",
            "unit-genericsBustStack",
            "unit-genericsBustStack2",
            "unit-genericsBustStackRuntime",
            "unit-intersectionTypes",
            "unit-intersectionWithFunction",
            "unit-thisTypesInInterfaces3",
            "unit-valueCantBeTrueAndFalse"
    );

    // the ones that currently fails for various reasons.
    private static final List<String> blackList = Arrays.asList(
            "unit-complexSanityCheck14",
            "unit-complexSanityCheck18",
            "unit-complexSanityCheck3",
            "unit-complexSanityCheck9",
            "unit-complexThisTypes",
            "unit-exponentialComplexity",
            "unit-extendsArray",
            "unit-extendsArray2",
            "unit-extendsArray3",
            "unit-extendsArray4",
            "unit-firstMatchPolicy",
            "unit-genericClassFeedbackWithConstraint",
            "unit-genericIndexedAccess",
            "unit-mappedTypes",
            "unit-optionalDoesNotMeanUndefinedWithRestArg",
            "unit-overrideNumberOfArguments",
            "unit-testRestArgs",
            "unit-unsoundSiblings",
            "tajsunit-testRestArgs"
    );
}


