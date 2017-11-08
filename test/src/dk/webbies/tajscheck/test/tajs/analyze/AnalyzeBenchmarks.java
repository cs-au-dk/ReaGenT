package dk.webbies.tajscheck.test.tajs.analyze;

import dk.brics.tajs.options.Options;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.RunSmall;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.tajs.TAJSUtil;
import dk.webbies.tajscheck.util.Util;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dk.webbies.tajscheck.test.Matchers.emptyMap;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

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
            "q",
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
            "Foundation",
            "Materialize",
            "Backbone.js",
            "Moment.js",
            "Medium Editor",
            "CreateJS",
            "Handlebars",
            "Redux",
            "QUnit",
            "Knockout",
            "axios",
            "D3.js",
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
            "Vue.js",
            "Modernizr",
            "Fabric.js",
            "Video.js",
            "RequireJS",
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

    public static Function<CheckOptions.Builder, CheckOptions.Builder> options() {
        return options -> options
                .setCombineNullAndUndefined(true) // because no-one cares.
                .staticOptions
                    .setKillGetters(true) // because getters currently causes the analysis to loop.
                .build().getBuilder();
    }

    @Test(timeout = (int)(BENCHMARK_TIMEOUT * 1000 * 1.3))
    public void analyzeBenchmark() throws Exception {
        Benchmark benchmark = this.benchmark.withOptions(options());
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchmark, BENCHMARK_TIMEOUT);
        System.out.println(result);
        assert(!result.timedout);
    }

    @Test(timeout = (int)(BENCHMARK_TIMEOUT * 1000 * 1.3))
    public void analyzeBenchmarkPatched() throws Exception {
        Path dtspath = Paths.get(this.benchmark.dTSFile);
        Path entryPath = Paths.get(this.benchmark.jsFile);
        String patched = dtspath.getParent().resolve("patched." + dtspath.getFileName()).toString();
        String patchedEnty = entryPath.getParent().resolve("patched." + entryPath.getFileName()).toString();
        Benchmark benchmark = this.benchmark.withOptions(options())
                .withDecl(patched)
                .withJsFile(patchedEnty);
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchmark, BENCHMARK_TIMEOUT);
        System.out.println(result);
        assert(!result.timedout);
    }

    @Test(timeout = (int)(INIT_TIMEOUT * 1000 * 1.3))
    public void initialize() throws Exception {
        Benchmark benchmark = this.benchmark.withOptions(options -> options().apply(options).setOnlyInitialize(true));
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchmark, INIT_TIMEOUT);
        System.out.println(result);
        assert(!result.timedout);
    }
}
