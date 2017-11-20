package dk.webbies.tajscheck.test.tajs;


import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.benchmark.options.OptionsI;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.dynamic.UnitTests;
import dk.webbies.tajscheck.test.tajs.analyze.AnalyzeBenchmarks;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
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

        result.addAll(AnalyzeBenchmarks.getBenchmarks()); // TODO: Add these.
//        TODO: (typeNames): Foundation, Materialize, highlight.js, Jasmine, reveal.js, Leaflet
        // TODO: Soundness: PleaseJS

        result = result.stream()
                .filter(bench -> !bench.name.equals("unit-exponentialComplexity"))
                .filter(bench -> !createsIntersection.contains(bench.name))
                .filter(bench -> !intentionallyUnsound.contains(bench.name))
                .filter(bench -> !unsupportedFeatures.contains(bench.name))
                .filter(bench -> !blackList.contains(bench.name))
                .collect(Collectors.toList());
        Collections.shuffle(result);
        return result;
//        return result;
    }

    @Test
    @Ignore
    public void fillCache() throws Exception {
        BenchmarkInfo info = BenchmarkInfo.create(bench.withRunMethod(Benchmark.RUN_METHOD.BOOTSTRAP));
    }

    @Test(timeout = 70 * 1000)
    public void hasNoViolations() throws Exception {
        Benchmark bench = this.bench.withRunMethod(Benchmark.RUN_METHOD.BOOTSTRAP).withOptions(options());
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(bench, 60);
        System.out.println(result);
        expect(result)
                .hasNoViolations();
    }

    private Function<CheckOptions.Builder, OptionsI.Builder> options() {
        return options -> options
                .setConstructAllTypes(true)
                .staticOptions.setCreateSingletonObjects(true); // TODO: Remove?
    }

    @Test(timeout = 70 * 1000)
    public void performsAllTests() throws Exception {
        Benchmark bench = this.bench.withRunMethod(Benchmark.RUN_METHOD.BOOTSTRAP).withOptions(options());
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(bench, 60);
        System.out.println(result);
        expect(result)
                .performedAllTests();
    }

    private static final List<String> createsIntersection = Arrays.asList(
            "unit-canHaveError",
            "unit-canHaveErrorBrowser",
            "unit-complexSanityCheck20",
            "unit-complexSanityCheck23",
            "unit-complexSanityCheck19",
            "unit-findSimpleErrorChromeWithErrors",
            "unit-genericExtendMethod",
            "unit-genericsBustStack",
            "unit-genericsBustStack2",
            "unit-genericsBustStackRuntime",
            "unit-intersectionTypes",
            "unit-intersectionWithFunction",
            "unit-thisTypesInInterfaces3",
            "unit-valueCantBeTrueAndFalse",
            "unit-genericClassFeedbackWithConstraint",
            "PhotoSwipe",
            "Redux",
            "unit-loopingType"
    );

    private static final List<String> unsupportedFeatures = Arrays.asList(
            "unit-genericIndexedAccess", // creating a signature that returns an index-type is not supported.
            "unit-mappedTypes", // I don't support mapped types in general.
            "unit-asyncGenerator"
    );

    // demonstrations of unsound types in TypeScript
    private static final List<String> intentionallyUnsound = Arrays.asList(
            "unit-complexSanityCheck3",
            "unit-complexSanityCheck9",
            "unit-unsoundSiblings",
            "PleaseJS", // multiple signatures with same parameters returns different types.
            "Zepto.js", // really a failure of enforcing first-match-policy on ZeptoStatic#each.
            "accounting.js", // also first-match-policy.
            "async", // more first match policy
            "Jasmine" // more first match policy
    );

    // TODO: the ones that currently fails for various reasons.
    private static final List<String> blackList = Arrays.asList(
            // wait.
            "unit-firstMatchPolicy", // seems to be insufficient context-sensitivity.

            // something about generics.
            "unit-simpleFunctionArg",
            "unit-complexSanityCheck12",
            "tajsunit-soundness1",
            "unit-complexSanityCheck15",

            // Somehting about numberIndexer.
            "QUnit",
            "lunr.js",
            "unit-genRestArgsWithOverloads",
            "tajsunit-testRestArgs",
            "tajsunit-smokeTest1",
            "tajsunit-createRestArgs",
            "Sortable",
            "Knockout",
            "unit-optionalDoesNotMeanUndefinedWithRestArg",
            "unit-complexGenerics2",
            "unit-complexSanityCheck16",
            "unit-testRestArgs",
            "unit-stringIndexer",
            "unit-genRestArgs",
            "unit-differentSizeOverloads",
            "unit-correctArrayType",

            "tajsunit-readProperty", // a callback not being called.

            // Unmodelled native object
            "Swiper",
            "CodeMirror",
            "unit-extendsEvent",
            "unit-extendsArray4",
            "unit-extendsArray2",
            "unit-complexSanityCheck22",
            "Handlebars",
            "PDF.js",
            "unit-extendsEvent3",
            "unit-extendsEvent2",
            "unit-complexSanityCheck26",
            "unit-complexSanityCheck25",
            "Moment.js",
            "Hammer.js",
            "unit-extendsArray3",
            "axios",
            "Medium Editor",
            "PeerJS",
            "unit-booleans",
            "highlight.js",
            "unit-classAndClassInstances",
            "box2dweb",
            "unit-extendsArray",
            "Leaflet",

            "unit-complexOverloads", // symbol
            "unit-symbol", // symbol
            "tajsunit-createUnionsOfDateAndFunction", // a higher-order function is never called.

            "reveal.js", // something about overloads not being called correctly.
            "pathjs", // multiple things, likely the above numberIndex and generic thing.
            "intro.js", // multiple things, like the same as reveal.js (fix reveal.js first).
            "unit-thisTypesAreOptimized", // likely the above numberIndexer and something else.
            "unit-thisTypesAreOptimized2", // likely the above numberIndexer and something else.


            // should be possible.
            "unit-complexThisTypes", // looks like a this-type getting overwritten.
            "unit-complexUnion", // currently does not support union between function and Date.
            "unit-overrideNumberOfArguments", // none of the overloads matched...

            // impossible, forget them
            "unit-complexSanityCheck18", // you cannot at runtime distinguish the different signatures.
            "unit-exponentialComplexity", // too big.

            // Recursive generics, but this shouldn't give an error.
            "unit-complexSanityCheck24",
            "unit-complexSanityCheck21"
    );
}


