package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.benchmark.options.staticOptions.LimitTransfersRetractionPolicy;
import dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions;
import dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy.LateExpansionToFunctionsWithConstructedArguments;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 19-12-2016.
 */
@RunWith(Parameterized.class)
public class AnalyzeBenchmarks extends TestCase {
    private final static int BENCHMARK_TIMEOUT = 60 * 60;
    private final static int INIT_TIMEOUT = 10 * 60;

    @SuppressWarnings("WeakerAccess")
    @Parameterized.Parameter
    public Benchmark benchmark = null;

    // Benchmarks that seem analyzeable.
    static final Set<String> whitelist = new HashSet<>(Arrays.asList(
            "Sortable", // can analyze
            "async", // can analyze, takes a while and most gets retracted/timeouts.
            "PleaseJS", // can analyze
            "PhotoSwipe", // encounters (cannot construct intersectionType) at top-level constructor.
            "Knockout", // 48 violations in the top-level object. So no methods are called.
            "Swiper", // Top level constructor gets retracted (takes way to long).
            "pathjs", // can analyze.
            "reveal.js", // can analyze.
            "accounting.js", // ~4 minutes on my desktop.
            "PDF.js", // can analyze. (but lots of timeouts).
            "Hammer.js", // LOTS of errors in top level object (they should use "typeof [class]" a lot, but they don't).
            "Handlebars", // plenty of bugs still.
            "axios", // Lots of bugs in top level object, seems to be due to weak writes.
            "Medium Editor", // Declaration is very stupid, they have declared an interface, that has a constructed method that returns the interface.
            "CreateJS", // lots of bugs in top-level object.
            "QUnit", // lots of bugs in top-level object.
            "RxJS", // lots of errors in top-level object.
            "Knockout", // lots of errors in top-level code, due to exceptionalFlow.
            "box2dweb", // ~20 minutes on my desktop. But terminates (we are talking 73 constructors, 85 methods, then it terminates).
            "lunr.js", // can analyze. But plenty or errors in top-level constructors, meaning we skip a lot of tests.
            "bluebird", // TODO: Sometimes the initialization crashes.
            "QUnit", // Takes about 40 minutes on my laptop, and has plenty of timeouts.
            "intro.js",
            "Redux", //


            "CodeMirror", // TODO: Crashes (after 6 minutes on my desktop) with "Reading undefined register v10).
            "highlight.js", // Include any of the highlight functions, and it takes forever. Exclude them, done in 15 seconds.

            "Moment.js", // Timeout.
            "Leaflet" // initialization crashes on line 2302, because TAJS thinks it is reading an undefined property.
    ));

    static final Set<String> blackList = new HashSet<>(Arrays.asList(
            "AngularJS", // Includes jQuery
            "jQuery", // is jQuery :)
            "Zepto.js", // Call eval very imprecisely. (line 914)
            "PeerJS", // TAJS does not support WebRTC
            "Underscore.js", // massive amount of overloads and generics, including recursively defined generic types.
            "Ionic", // includes jQuery
            "three.js", // simply massive, even TSTest has a hard time with it.
            "Jasmine", // Nahhh. has a difficult structure, somewhat incompatible with everything else.
            "pickadate.js", // includes jQuery
            "Foundation", // includes jQuery
            "Materialize", // includes jQuery
            "RequireJS", // includes jQuery
            "Ember.js", // includes jQuery
            "Backbone.js", // includes jQuery
            "Lodash", // massive amount of declared overloads and use of generics. Even TSTest has a hard time with it in its full size.
            "PixiJS", // ~40000 lines of JS. Just the loading is a struggle.
            "MathJax", // encounters "Unevalable eval: window" during initialization
            "Backbone.js", // includes underscore AND jQuery
            "Vue.js", // Unsupported Native Object. Proxy.
            "React", // Lots of errors in the top-level object. Even if those are fixed, the full benchmarks is massive.
            "D3.js", // Truly massive. Even TSTest struggles with this one. But i can actually get some partial results for it, if generics are disabled. (Last time i run, i crashed the JVM, likely out of mem).
            "Modernizr", // Currently encounters "This function from WebGLRenderingContext is not yet supported: test/benchmarks/modernizr/modernizr.js:798:115" And will likely encounter many more after that one is fixed.
            "Polymer", // "Too imprecise calls to Function" during initialization
            "Sugar", // Too much mem, and too much time, just for the initialization.
            "q",  // Uses require mechanism.

            // TODO: Try on a proper machine.
            "Ace", //
            "Fabric.js",
            "Video.js",
            "P2.js", // Initialization takes too long.
            "Chart.js" // Initialization alone takes too long (not that i blame TAJS, it is ~15000 lines of JS).
    ));

    @Parameterized.Parameters(name = "{0}")
    public static List<Benchmark> getBenchmarks() {
        RunBenchmarks.getBenchmarks().stream().map(bench -> bench.name).forEach(name -> {
            if (!whitelist.contains(name) && !blackList.contains(name)) {
                throw new RuntimeException("Benchmark: " + name + " not contained in blacklist or whitelist. ");
            }
            if (whitelist.contains(name) && blackList.contains(name)) {
                System.out.println("Benchmark: " + name + " contained in both blacklist and whitelist");
            }
        });
        return RunBenchmarks.getBenchmarks().stream()
                .filter(bench -> whitelist.contains(bench.name))
                .collect(Collectors.toList());
    }

    public static Function<CheckOptions.Builder, StaticOptions.Builder> options() {
        return options -> options
                .setCombineNullAndUndefined(true) // because no-one cares.

                // same as default, but just to be explicit about it.
                .setConstructClassInstances(false)
                .setConstructClassTypes(false)
                .setConstructAllTypes(false)

                .staticOptions
                    .setKillGetters(true) // because getters currently causes the analysis to loop. // TODO: Still?

                    .setRetractionPolicy(new LimitTransfersRetractionPolicy(10000, 0))

                    .setCheckAllPropertiesAreFunctionCall(true)
                    .setPropagateStateFromFailingTest(false)

                    .setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.FEEDBACK_IF_POSSIBLE)
                    .setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments())
                ;
    }

    @Test(timeout = (int)(BENCHMARK_TIMEOUT * 1000 * 1.3))
    public void analyzeBenchmark() throws Exception {
        Benchmark benchmark = this.benchmark.withOptions(options().andThen(options -> options.setUseInspector(false)));
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchmark, BENCHMARK_TIMEOUT);
        System.out.println(result);
    }

    @Test(timeout = (int)(BENCHMARK_TIMEOUT * 1000 * 1.3))
    public void analyzeBenchmarkPatched() throws Exception {
        Benchmark benchmark = getPatchedBenchmark(this.benchmark);
        if (benchmark == null) {
            return;
        }
        benchmark = benchmark.withOptions(options());
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchmark, BENCHMARK_TIMEOUT);
        System.out.println(result);
        assert(!result.timedout);
    }

    public static Benchmark getPatchedBenchmark(Benchmark benchmark) {
        Path dtspath = Paths.get(benchmark.dTSFile);
        Path entryPath = Paths.get(benchmark.jsFile);
        String patched = dtspath.getParent().resolve("patched." + dtspath.getFileName()).toString();
        if (!new File(patched).exists()) {
            return null;
        } else {
            String patchedEntry = entryPath.getParent().resolve("patched." + entryPath.getFileName()).toString();
            return benchmark
                    .withDecl(patched)
                    .withJsFile(patchedEntry);
        }
    }

    @Test(timeout = (int)(INIT_TIMEOUT * 1000 * 1.3))
    public void initialize() throws Exception {
        Benchmark benchmark = this.benchmark.withOptions(options().andThen(options -> options.getOuterBuilder().setOnlyInitialize(true)));
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchmark, INIT_TIMEOUT);
        System.out.println(result);
        assert(!result.timedout);
    }
}
