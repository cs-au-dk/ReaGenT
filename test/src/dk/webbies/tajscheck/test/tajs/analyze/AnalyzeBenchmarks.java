package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.tajs.TAJSUtil;
import dk.webbies.tajscheck.util.Util;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
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
            "D3.js",
            "q",
            "async",
            "Redux",
            "Leaflet",
            "reveal.js",
            "intro.js",
            "PleaseJS",
            "highlight.js",
            "RxJS",
            "Zepto.js",
            "pathjs",
            "Moment.js",
            "CodeMirror",
            "PhotoSwipe",
            "Jasmine",
            "Swiper",
            "box2dweb",
            "Sortable",
            "accounting.js",
            "CreateJS",
            "lunr.js",
            "jQuery",
            "Knockout",

            // Below timed out before the tracifier branch
            "Chart.js",
            "PeerJS",
            "PixiJS",
            "Handlebars",
            "axios",

            // Crashed before the tracifier branch
            "Medium Editor"
    ));


    // Benchmarks that does not invoke any DOM functions, and are on the whitelist
    static final Set<String> simpleBenchmarks = new HashSet<>(Arrays.asList(
            "async",
            "Redux",
            "PleaseJS",
//            "Moment.js",
//            "box2dweb",
            "accounting.js",
            "lunr.js"
    ));

    // Benchmarks that for various reasons are unanalyzeable.
    static final Set<String> blacklist = new HashSet<>(Arrays.asList(
            // because it has getters/setters, which TAJS does not support
            "PDF.js",
            "Vue.js",
            "three.js",
            "Ember.js",
            "Polymer", // <- because webcomponents has getter.

            "Ace", // Calls unmodelled Object.freeze
            "RequireJS", // weird error, replicated in TestMicro
            "QUnit", // weird error with arrays.
            "React", // No transfer function for Object.freeze.
            "Modernizr", // Run a WebGL function that is unsupported. (and sometimes it timeouts)
            "Hammer.js", // Object.assign crashes TAJS
            "MathJax" // "Unevalable eval: window"
    ));

    // Benchmarks where just the initialization reaches a timeout
    static final Set<String> timeouts = new HashSet<>(Arrays.asList(
            "AngularJS",
            "Foundation",
            "Materialize",
            "P2.js",
            "bluebird",
            "Fabric.js",
            "Ionic",
            "Video.js",
            "Backbone.js",
            "Lodash",
            "Sugar", // sometimes within the timeout, but far from always, so it ends up here. 
            "Underscore.js"
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

    @Test
    public void analyzeBenchmark() throws Exception {
        // TODO: Run with the new noDriver

    }

    @Test
    public void measureBenchmark() throws Exception {
        Benchmark benchmark = this.benchmark.withOptions(options -> options.setMeasurePrecision(true));
        //TAJSUtil.runNoDriver(benchmark, 300);
    }

    @Test
    public void initialize() throws Exception {
        Benchmark benchmark = this.benchmark.withOptions(options -> options.setOnlyInitialize(true));
        //TAJSUtil.runNoDriver(benchmark, 300);
    }
}
