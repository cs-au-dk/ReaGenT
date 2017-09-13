package dk.webbies.tajscheck.test.tajs.analyze;

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
            "Backbone.js"
    ));


    // Benchmarks that does not invoke any DOM functions, and are on the whitelist
    static final Set<String> simpleBenchmarks = new HashSet<>(Arrays.asList(
            "async",
            "PleaseJS",
//            "Moment.js",
//            "box2dweb",
            "accounting.js",
            "lunr.js"
    ));

    // Benchmarks that for various reasons are unanalyzeable.
    static final Set<String> blacklist = new HashSet<>(Arrays.asList(
            // because it has getters/setters, which TAJS does not support
            "Vue.js",
            "three.js",
            "Ember.js",
            "Polymer", // <- because webcomponents has getter.

            "Ace", // Calls unmodelled Object.freeze
            "jQuery", // Too imprecise calls to Function
            "RequireJS", // weird error, replicated in TestMicro
            "QUnit", // weird error with arrays.
            "React", // No transfer function for Object.freeze.
            "axios", // Empty value
            "Modernizr", // Run a WebGL function that is unsupported. (and sometimes it timeouts)
            "Hammer.js", // Object.assign crashes TAJS
            "RxJS", // Unexpected polymorphic value
            "Redux", // TypeError, call to non-function (DOM): HTMLTextAreaElement constructor
            "Knockout", // Trying to call toString for Array with redefined join-property.
            "MathJax" // "Unevalable eval: window"
    ));

    // Benchmarks where just the initialization reaches a timeout
    static final Set<String> timeouts = new HashSet<>(Arrays.asList(
            "Chart.js",
            "AngularJS",
            "P2.js",
            "bluebird",
            "Fabric.js",
            "Ionic",
            "Video.js",
            "PeerJS",
            "CreateJS",
            "Handlebars",
            "D3.js",
            "Moment.js",
            "PixiJS",
            "Lodash",
            "Sugar", // sometimes within the timeout, but far from always, so it ends up here. 
            "Underscore.js",
            "Medium Editor"
    ));

    // Sanity checks on the above lists.
    static {
        if (!Util.concat(whitelist, blacklist, timeouts, simpleBenchmarks).stream().allMatch(RunBenchmarks.benchmarks.keySet()::contains)) {
            System.err.println("AnalyzeBenchmarks: A benchmark was misspelled");
        }
        if (!simpleBenchmarks.stream().allMatch(whitelist::contains)) {
            System.err.println("AnalyzeBenchmarks: A benchmark in SimpleBenchmarks wasn't in the whiteList");
        }
        RunBenchmarks.benchmarks.keySet().stream().filter(Util.not(whitelist::contains)).filter(Util.not(blacklist::contains)).filter(Util.not(timeouts::contains)).forEach(forgotten -> System.err.println("AnalyzeBenchmarks: " + forgotten + " was forgotten!"));
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Benchmark> getBenchmarks() {
        return RunBenchmarks.getBenchmarks().stream()
//                .filter(bench -> blacklist.contains(bench.name))
//                .filter(bench -> timeouts.contains(bench.name))
                .filter(bench -> whitelist.contains(bench.name))
                .collect(Collectors.toList());
    }

    private Function<CheckOptions.Builder, CheckOptions.Builder> options() {
        return options -> options
                .setCombineNullAndUndefined(true) // because no-one cares.
                .staticOptions
                    .setKillGetters(true) // because getters currently causes the analysis to loop.
                .build().getBuilder();
    }

    @Test
    public void analyzeBenchmark() throws Exception {
        Benchmark benchmark = this.benchmark.withOptions(options());
        try {
            System.out.println(TAJSUtil.runNoDriver(benchmark, 180));
        } catch (TimeoutException ignored) {
            System.err.println("Timeout");
        }
    }

    @Test
    public void analyzeBenchmarkPatched() throws Exception {
        Path dtspath = Paths.get(this.benchmark.dTSFile);
        String patched = dtspath.getParent().resolve("patched." + dtspath.getFileName()).toString();
        Benchmark benchmark = this.benchmark.withOptions(options()).withDecl(patched);
        try {
            System.out.println(TAJSUtil.runNoDriver(benchmark,180));
        } catch (TimeoutException ignored) {
            System.err.println("Timeout");
        }
    }

    @Test
    public void analyzeBenchmarkLimitedSideEffects() throws Exception {
        Benchmark benchmark = this.benchmark.withOptions(options -> options().apply(options).staticOptions.setLimitSideEffects(true));
        try {
            System.out.println(TAJSUtil.runNoDriver(benchmark, 90));

        } catch (TimeoutException ignored) {
            System.err.println("Timeout");
        }
    }

    @Test
    public void initialize() throws Exception {
        Benchmark benchmark = this.benchmark.withOptions(options -> options().apply(options).setOnlyInitialize(true));
        try {
            System.out.println(TAJSUtil.runNoDriver(benchmark, 30));
        } catch (TimeoutException ignored) {
            System.err.println("Timeout");
        }
    }
}
