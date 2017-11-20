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

    private final static int BENCHMARK_TIMEOUT = 4 * 60;
    private final static int INIT_TIMEOUT = 2 * 60;

    @SuppressWarnings("WeakerAccess")
    @Parameterized.Parameter
    public Benchmark benchmark = null;

    // Benchmarks that seem analyzeable.
    static final Set<String> whitelist = new HashSet<>(Arrays.asList(
            "async",
            "Leaflet",
            "reveal.js",
            "intro.js",
            "PleaseJS",
            "highlight.js",
            "Zepto.js",
            "pathjs",
            "CodeMirror",
            "PhotoSwipe",
            "Jasmine",
            "Swiper",
            "box2dweb",
            "Sortable",
            "accounting.js",
            "lunr.js",
            "PDF.js",
            "Moment.js",
            "Medium Editor",
            "Handlebars",
            "Redux",
            "QUnit",
            "Knockout",
            "axios",
            "PeerJS",
            "Hammer.js"
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
            "async",
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
        Path dtspath = Paths.get(this.benchmark.dTSFile);
        Path entryPath = Paths.get(this.benchmark.jsFile);
        String patched = dtspath.getParent().resolve("patched." + dtspath.getFileName()).toString();
        if (!new File(patched).exists()) {
            return;
        }
        String patchedEntry = entryPath.getParent().resolve("patched." + entryPath.getFileName()).toString();
        Benchmark benchmark = this.benchmark.withOptions(options())
                .withDecl(patched)
                .withJsFile(patchedEntry);
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchmark, Integer.MAX_VALUE);
        System.out.println(result);
        assert(!result.timedout);
    }

    @Test(timeout = (int)(INIT_TIMEOUT * 1000 * 1.3))
    public void initialize() throws Exception {
        Benchmark benchmark = this.benchmark.withOptions(options -> options().apply(options).getOuterBuilder().setOnlyInitialize(true));
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchmark, INIT_TIMEOUT);
        System.out.println(result);
        assert(!result.timedout);
    }
}
