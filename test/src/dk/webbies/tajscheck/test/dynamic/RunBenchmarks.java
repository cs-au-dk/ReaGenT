package dk.webbies.tajscheck.test.dynamic;

import dk.webbies.tajscheck.CoverageResult;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.benchmarks.CheckOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.paser.AST.Statement;
import dk.webbies.tajscheck.paser.AstToStringVisitor;
import dk.webbies.tajscheck.paser.JavaScriptParser;
import dk.webbies.tajscheck.test.TestParsing;
import dk.webbies.tajscheck.util.Util;
import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static dk.webbies.tajscheck.benchmarks.Benchmark.RUN_METHOD.BOOTSTRAP;
import static dk.webbies.tajscheck.benchmarks.Benchmark.RUN_METHOD.BROWSER;
import static dk.webbies.tajscheck.benchmarks.Benchmark.RUN_METHOD.NODE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by erik1 on 22-11-2016.
 */
@RunWith(Parameterized.class)
public class RunBenchmarks {

    @SuppressWarnings("WeakerAccess")
    @Parameterized.Parameter
    public Benchmark benchmark = null;

    @SuppressWarnings("WeakerAccess")
    public static final Map<String, Benchmark> benchmarks = new HashMap<>();

    static {
        CheckOptions options = CheckOptions.builder()
                .setSplitUnions(false) // Because some of these benchmarks use an insane amount of overloads, so this can cause the size of the generated program to explode (about a factor 400x for moment).
                .build();

        benchmarks.put("moment", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/moment/moment.js", "test/benchmarks/moment/moment.d.ts", "moment", NODE, options));
        benchmarks.put("async", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/async/async.js", "test/benchmarks/async/async.d.ts", "async", NODE, options));
        benchmarks.put("path.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/pathjs/pathjs.js", "test/benchmarks/pathjs/pathjs.d.ts", "Path", BROWSER, options));
        benchmarks.put("accounting.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/accounting/accounting.js", "test/benchmarks/accounting/accounting.d.ts", "accounting", NODE, options));
        benchmarks.put("lunr.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/lunr/lunr.js", "test/benchmarks/lunr/lunr.d.ts", "lunr", NODE, options));
        benchmarks.put("PIXI.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/pixi/pixi.js", "test/benchmarks/pixi/pixi.d.ts", "PIXI", BROWSER, options));

        benchmarks.put("fixedMoment", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/fixedMoment/moment.js", "test/benchmarks/fixedMoment/moment.d.ts", "moment", NODE,
                options
                        .getBuilder()
                        .setCheckDepth(2)
                        .setCheckDepthForUnions(3)
                        .build()
        ));


        benchmarks.put("ace.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/ace/ace.js", "test/benchmarks/ace/ace.d.ts", "ace", BROWSER, options));
        Benchmark jQuery = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/jquery/jquery.js", "test/benchmarks/jquery/jquery.d.ts", "jQuery", BROWSER, options);
        benchmarks.put("jQuery.js", jQuery);


        Benchmark angular = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/angular1/angular1.js", "test/benchmarks/angular1/angular1.d.ts", "angular", BROWSER, options)
                .addDependencies(jQuery);
        benchmarks.put("angular.js",
                angular
        );


        benchmarks.put("box2dweb", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/box2dweb/box2dweb.js", "test/benchmarks/box2dweb/box2dweb.d.ts", "Box2D", BROWSER, options));

        Benchmark underscore = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/underscore/underscore.js", "test/benchmarks/underscore/underscore.d.ts", "_", NODE, options);
        benchmarks.put("underscore", underscore);

        benchmarks.put("handlebars", new Benchmark(ParseDeclaration.Environment.ES6DOM, "test/benchmarks/handlebars/handlebars.js", "test/benchmarks/handlebars/handlebars.d.ts", "Handlebars", BROWSER, options));

        benchmarks.put("Hammer.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/hammer/hammer.js", "test/benchmarks/hammer/hammer.d.ts", "Hammer", BROWSER, options));

        benchmarks.put("jasmine", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/jasmine/jasmine.js", "test/benchmarks/jasmine/jasmine.d.ts", "jasmine", BROWSER, options));

        benchmarks.put("knockout", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/knockout/knockout.js", "test/benchmarks/knockout/knockout.d.ts", "ko", BROWSER, options));

        benchmarks.put("fabric.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/fabric/fabric.js", "test/benchmarks/fabric/fabricModule.d.ts", "fabric", BROWSER, options));

        // TODO: include jQuery and handlebars.
        benchmarks.put("ember", new Benchmark(ParseDeclaration.Environment.ES5DOM, "test/benchmarks/ember/ember.js", "test/benchmarks/ember/ember.d.ts", "Ember", BROWSER, options));

        benchmarks.put("d3", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/d3/d3.js", "test/benchmarks/d3/d3.d.ts", "d3", BROWSER, options));

        Benchmark webcomponents = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/webcomponents/webcomponents.js", "test/benchmarks/webcomponents/webcomponents.d.ts", "webcomponents", BROWSER, options);
        benchmarks.put("webcomponents", webcomponents);

        benchmarks.put("mathjax", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/mathjax/mathjax.js", "test/benchmarks/mathjax/mathjax.d.ts", "MathJax", BROWSER, options));

        benchmarks.put("peerjs", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/peerjs/peerjs.js", "test/benchmarks/peerjs/peerjs.d.ts", "PeerJs", BROWSER, options));
        benchmarks.put("pickadate", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/pickadate/picker.js", "test/benchmarks/pickadate/pickadate.d.ts", "Pickadate", BROWSER, options).addDependencies(jQuery));
        benchmarks.put("pleasejs", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/pleasejs/please.js", "test/benchmarks/pleasejs/please.d.ts", "Please", NODE, options));
        benchmarks.put("polymer", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/polymer/polymer.js", "test/benchmarks/polymer/polymer.d.ts", "polymer", BROWSER, options).addDependencies(webcomponents));
        benchmarks.put("q", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/q/q.js", "test/benchmarks/q/q.d.ts", "Q", BROWSER, options));
        benchmarks.put("qunit", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/qunit/qunit.js", "test/benchmarks/qunit/qunit.d.ts", "QUnit", BROWSER, options));
        benchmarks.put("react", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/react/react.js", "test/benchmarks/react/react.d.ts", "React", BROWSER, options));
        benchmarks.put("requirejs", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/requirejs/require.js", "test/benchmarks/requirejs/requirejs.d.ts", "requirejs", BROWSER, options));
        benchmarks.put("sugar", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/sugar/sugar.js", "test/benchmarks/sugar/sugar.d.ts", "sugarjs", BROWSER, options));

        benchmarks.put("photoswipe", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/photoswipe/photoswipe.js", "test/benchmarks/photoswipe/photoswipe.d.ts", "PhotoSwipe", BROWSER, options));
        benchmarks.put("createjs", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/createjs/createjs.js", "test/benchmarks/createjs/createjs.d.ts", "createjs", BROWSER, options));
        benchmarks.put("vue", new Benchmark(ParseDeclaration.Environment.ES6DOM, "test/benchmarks/vue/vue.js", "test/benchmarks/vue/index.d.ts", "Vue", BROWSER, options));
        benchmarks.put("three", new Benchmark(ParseDeclaration.Environment.ES6DOM, "test/benchmarks/three/three.js", "test/benchmarks/three/three.d.ts", "THREE", BROWSER, options));
        benchmarks.put("leaflet", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/leaflet/leaflet.js", "test/benchmarks/leaflet/leaflet.d.ts", "L", BROWSER, options));

        benchmarks.put("backbone",
                new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/backbone/backbone.js", "test/benchmarks/backbone/backbone.d.ts", "Backbone", BROWSER, options)
                .addDependencies(underscore)
        );

        benchmarks.put("lodash", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/lodash/lodash.js", "test/benchmarks/lodash/lodash.d.ts", "_", NODE,
                options.getBuilder()
                        .setDisableGenerics(true)
                        .build()
        ));

        benchmarks.put("p2", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/p2/p2.js", "test/benchmarks/p2/p2.d.ts", "p2", BROWSER, options));

        benchmarks.put("zepto", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/zepto/zepto.js", "test/benchmarks/zepto/zepto.d.ts", "Zepto", BROWSER, options));

        benchmarks.put("redux", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/redux/redux.js", "test/benchmarks/redux/reduxModule.d.ts", "redux", NODE, options));

        benchmarks.put("ionic", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/ionic/ionic.js", "test/benchmarks/ionic/ionic.d.ts", "ionic", BROWSER, options)
            .addDependencies(jQuery)
            .addDependencies(angular)
        );

        benchmarks.put("Foundation", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/foundation/foundation.js", "test/benchmarks/foundation/foundation.d.ts", "Foundation", BROWSER, options));

        benchmarks.put("chartjs", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/chartjs/chart.js", "test/benchmarks/chartjs/chart.d.ts", "Chart", BROWSER, options));

    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Benchmark> getBenchmarks() {
        return new ArrayList<>(benchmarks.values());
    }

    @Test
    public void genFullDriver() throws Exception {
        Main.writeFullDriver(benchmark);
    }

    @Test
    @Ignore // I currently have no use for this.
    public void genSmallDrivers() throws Exception {
        Main.genSmallDrivers(benchmark);
    }

    @Test
    public void runFullDriver() throws Exception {
        // Write the driver
        Main.writeFullDriver(benchmark);

        // Run the driver
        String out;
        try {
            out = Main.runBenchmark(benchmark, 20 * 1000);
        } catch (TimeoutException e) {
            // this is ok, it happens.
            System.out.println("Timeout!");
            return;
        }
//        System.out.println(out);

        System.out.println(out.split("\n")[0]);

        // Parse and print the result
        OutputParser.RunResult result = OutputParser.parseDriverResult(out);

        result.typeErrors.sort(Comparator.comparing(o -> o.path));

        for (OutputParser.TypeError typeError : result.typeErrors) {
            System.out.println(typeError.toString());
            System.out.println();
        }

        if (result.errors.size() > 0) {
            System.out.println();
            System.out.println("---- ERRORS ----");
            for (String error : result.errors) {
                System.out.println(error);
            }
        }


        assert !out.trim().isEmpty();
    }

    @Test
    public void coverage() throws Exception {
        if (Stream.of("underscore.d.ts", "fabric", "d3.d.ts", "backbone.d.ts", "three.d.ts").anyMatch(benchmark.dTSFile::contains)) {
            return; // Too big, node runs out of memory generating the instrumented version.
        }
        Main.writeFullDriver(benchmark);
        System.out.println("Wrote driver");
        Map<String, CoverageResult> out;
        try {
            out = Main.genCoverage(benchmark, 60 * 1000);
        } catch (TimeoutException e) {
            // this is ok, it happens.
            System.out.println("Timeout!");
            return;
        }
        System.out.println("Coverage for " + benchmark.dTSFile);

        System.out.println(out);
    }

    @Test
    public void soundnessTest() throws Exception {
        Benchmark bench = this.benchmark.withRunMethod(BOOTSTRAP);
        Main.writeFullDriver(bench); // No seed specified, in case of failure, the seed can be seen from the output.
        System.out.println("Driver written");
        String output;
        try {
            output = Main.runBenchmark(bench, 60 * 1000);
        } catch (TimeoutException e) {
            System.out.println("Timeout");
            return;
        }
        System.out.println(output);
        OutputParser.RunResult result = OutputParser.parseDriverResult(output);

        if (result.typeErrors.size() > 0) {
            if (
                    bench.dTSFile.contains("box2dweb.d.ts") ||// box2dweb uses bivariant function arguments, which is unsound, and causes this soundness-test to fail.
                    bench.jsFile.contains("leaflet.d.ts") // same unsoundness in leaflet.
            ) {
                System.out.println("Is a benchmark which i know to fail. ");
                return;
            }
        }

        for (OutputParser.TypeError typeError : result.typeErrors) {
            System.out.println(typeError);
        }


        assertThat(result.typeErrors.size(), is(0));
    }

    @Test
    public void testParsing() throws Exception {
        // A sanitycheck that JavaScript parsing+printing is idempotent.
        System.out.println(benchmark.jsFile);
        TestParsing.testFile(benchmark.jsFile);
    }
}
