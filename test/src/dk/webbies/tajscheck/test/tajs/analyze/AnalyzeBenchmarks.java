package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.benchmark.options.staticOptions.ExpandOneAtATimePolicy;
import dk.webbies.tajscheck.benchmark.options.staticOptions.LimitTransfersRetractionPolicy;
import dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions;
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
    // TODO: Most of these remarks are before the rebase to extended.
    static final Set<String> whitelist = new HashSet<>(Arrays.asList(
            "Sortable", // can analyze
            "async", // currently loops/takes to long.
            "PleaseJS", // can analyze
            "PhotoSwipe", // encounters (cannot construct intersectionType) at top-level constructor.
            "Knockout", // 48 violations in the top-level object. So no methods are called.
            "Swiper", // Top level constructor gets retracted (takes way to long).

            "Moment.js", // can analyze (requires lots of memory)
            "pathjs", // can analyze. (TODO: infinite loop?)
            "Zepto.js", // can analyze. (TODO: Try to run with a lot of mem, it after rebase it seems different) (Before: Gets a useless spurious result after few minutes, because: We analyze the global object, is fine, we analyze some methods get some state, doing this a spurious write is performed on the global object, this causes everything except global object to be removed from type-to-test, and the single spurious error is reported.)
            "reveal.js", // can analyze. But takes a while. (TODO: Java crashed while running it last time, likely out-of-mem).
            "CodeMirror", // TODO: EVERYTHING was retracted? (even LoadModuleTest)
            "Jasmine", // has a lot of globals that it cannot find (because they aren't registered).
            "box2dweb", // TODO: Big, takes a lot of mem.
            "accounting.js", // TODO: NullPointerException in TAJS.
            "lunr.js", // can analyze. But plenty or errors in top-level constructors, meaning we skip a lot of tests.
            "PDF.js", // can analyze. But top-level constructor gets retracted.
            "Medium Editor", // TODO: Top level object not found.
            "Handlebars", // TODO: Error in top-level object.
            "Redux", // TODO: Top level object not found (try to not have an exports object)
            "axios", // TODO: Module not found (node?)
            "PeerJS", // TODO: Top level constructor always returns exceptionally.
            "Hammer.js", // TODO: Seemingly have some false positives (like Hammer.TouchAction.preventDefaults).
            "intro.js", // currently get a timeout (but is real close).
            "QUnit", // TODO: Takes a long time
            "highlight.js", // TODO: Takes a long time
            "Leaflet" // initialization crashes on line 2302, because TAJS thinks it is reading an undefined property.
    ));

    static final Set<String> blackList = new HashSet<>(Arrays.asList(
            "AngularJS",
            "MathJax",
            "Chart.js",
            "PixiJS",
            "P2.js",
            "bluebird",
            "Foundation",
            "Materialize",
            "Backbone.js",
            "Vue.js",
            "D3.js",
            "Modernizr",
            "Fabric.js",
            "Video.js",
            "q",
            "RequireJS",
            "CreateJS",
            "Lodash",
            "Sugar",
            "Ace",
            "Ember",
            "QUnit",
            "Polymer",
            "Backbone.js",
            "React",
            "Knockout",
            "q",
            "jQuery",
            "Underscore.js",
            "RxJS",
            "three.js",
            "Ember.js",
            "Ionic"
    ));

    @Parameterized.Parameters(name = "{0}")
    public static List<Benchmark> getBenchmarks() {
        return RunBenchmarks.getBenchmarks().stream()
                .filter(bench -> whitelist.contains(bench.name))
                .collect(Collectors.toList());
    }

    public static Function<CheckOptions.Builder, StaticOptions.Builder> options() {
        return options -> options
                .setCombineNullAndUndefined(true) // because no-one cares.
                .staticOptions
                    .setKillGetters(true) // because getters currently causes the analysis to loop.
                    .setRetractionPolicy(new LimitTransfersRetractionPolicy(10000, 0));
    }

    @Test(timeout = (int)(BENCHMARK_TIMEOUT * 1000 * 1.3))
    public void analyzeBenchmark() throws Exception {
        Benchmark benchmark = this.benchmark.withOptions(options());
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchmark, BENCHMARK_TIMEOUT);
        System.out.println(result);
    }

    @Test(timeout = (int)(BENCHMARK_TIMEOUT * 1000 * 1.3))
    public void analyzeBenchmarkPatched() throws Exception {
        Benchmark benchmark = getPatchedBenchmark(this.benchmark).withOptions(options());
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchmark, Integer.MAX_VALUE);
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
        Benchmark benchmark = this.benchmark.withOptions(options -> options().apply(options).getOuterBuilder().setOnlyInitialize(true));
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchmark, INIT_TIMEOUT);
        System.out.println(result);
        assert(!result.timedout);
    }
}
