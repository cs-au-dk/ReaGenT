package dk.webbies.tajscheck.test.dynamic;

import dk.webbies.tajscheck.CoverageResult;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.RunSmall;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.benchmarks.CheckOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.test.TestParsing;
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

        benchmarks.put("Moment.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/moment/moment.js", "test/benchmarks/moment/moment.d.ts", "moment", NODE, options));
        benchmarks.put("async", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/async/async.js", "test/benchmarks/async/async.d.ts", "async", NODE, options));
        benchmarks.put("pathjs", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/pathjs/pathjs.js", "test/benchmarks/pathjs/pathjs.d.ts", "Path", BROWSER, options));
        benchmarks.put("accounting.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/accounting/accounting.js", "test/benchmarks/accounting/accounting.d.ts", "accounting", NODE, options));
        benchmarks.put("lunr.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/lunr/lunr.js", "test/benchmarks/lunr/lunr.d.ts", "lunr", NODE, options));
        benchmarks.put("PixiJS", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/pixi/pixi.js", "test/benchmarks/pixi/pixi.d.ts", "PIXI", BROWSER, options));

        /*benchmarks.put("fixedMoment", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/fixedMoment/moment.js", "test/benchmarks/fixedMoment/moment.d.ts", "moment", NODE, options));*/


        benchmarks.put("Ace", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/ace/ace.js", "test/benchmarks/ace/ace.d.ts", "ace", BROWSER, options));
        Benchmark jQuery = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/jquery/jquery.js", "test/benchmarks/jquery/jquery.d.ts", "jQuery", BROWSER, options);
        benchmarks.put("jQuery", jQuery);


        Benchmark angular = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/angular1/angular1.js", "test/benchmarks/angular1/angular1.d.ts", "angular", BROWSER, options)
                .addDependencies(jQuery);
        benchmarks.put("AngularJS", angular);


        benchmarks.put("box2dweb", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/box2dweb/box2dweb.js", "test/benchmarks/box2dweb/box2dweb.d.ts", "Box2D", BROWSER, options));

        Benchmark underscore = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/underscore/underscore.js", "test/benchmarks/underscore/underscore.d.ts", "_", NODE, options);
        benchmarks.put("Underscore.js", underscore);

        Benchmark handlebars = new Benchmark(ParseDeclaration.Environment.ES6DOM, "test/benchmarks/handlebars/handlebars.js", "test/benchmarks/handlebars/handlebars.d.ts", "Handlebars", BROWSER, options);
        benchmarks.put("Handlebars.js", handlebars);

        benchmarks.put("Hammer.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/hammer/hammer.js", "test/benchmarks/hammer/hammer.d.ts", "Hammer", BROWSER, options));

        benchmarks.put("Jasmine", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/jasmine/jasmine.js", "test/benchmarks/jasmine/jasmine.d.ts", "jasmine", BROWSER, options));

        benchmarks.put("Knockout", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/knockout/knockout.js", "test/benchmarks/knockout/knockout.d.ts", "ko", BROWSER, options));

        benchmarks.put("Fabric.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/fabric/fabric.js", "test/benchmarks/fabric/fabric.d.ts", "fabric", BROWSER, options));

        benchmarks.put("Ember.js", new Benchmark(ParseDeclaration.Environment.ES5DOM, "test/benchmarks/ember/ember.js", "test/benchmarks/ember/ember.d.ts", "Ember", BROWSER, options)
            .addDependencies(jQuery, handlebars)
        );

        benchmarks.put("D3.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/d3/d3.js", "test/benchmarks/d3/d3.d.ts", "d3", BROWSER, options));

        Benchmark webcomponents = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/webcomponents/webcomponents.js", "test/benchmarks/webcomponents/webcomponents.d.ts", "webcomponents", BROWSER, options);
        benchmarks.put("webcomponents.js", webcomponents);

        benchmarks.put("MathJax", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/mathjax/mathjax.js", "test/benchmarks/mathjax/mathjax.d.ts", "MathJax", BROWSER, options));

        benchmarks.put("PeerJS", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/peerjs/peerjs.js", "test/benchmarks/peerjs/peerjs.d.ts", "PeerJs", BROWSER, options));
        Benchmark pickadate = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/pickadate/picker.js", "test/benchmarks/pickadate/pickadate.d.ts", "Pickadate", BROWSER, options).addDependencies(jQuery);
        benchmarks.put("pickadate.js", pickadate);
        benchmarks.put("PleaseJS", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/pleasejs/please.js", "test/benchmarks/pleasejs/please.d.ts", "Please", NODE, options));
        benchmarks.put("Polymer", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/polymer/polymer.js", "test/benchmarks/polymer/polymer.d.ts", "Polymer", BROWSER, options).addDependencies(webcomponents));
        benchmarks.put("q", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/q/q.js", "test/benchmarks/q/q.d.ts", "Q", BROWSER, options));
        benchmarks.put("QUnit", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/qunit/qunit.js", "test/benchmarks/qunit/qunit.d.ts", "QUnit", BROWSER, options));
        benchmarks.put("React", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/react/react.js", "test/benchmarks/react/react.d.ts", "React", BROWSER, options));
        benchmarks.put("RequireJS", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/requirejs/require.js", "test/benchmarks/requirejs/requirejs.d.ts", "requirejs", BROWSER, options));
        benchmarks.put("Sugar", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/sugar/sugar.js", "test/benchmarks/sugar/sugar.d.ts", "sugarjs", BROWSER, options));

        benchmarks.put("PhotoSwipe", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/photoswipe/photoswipe.js", "test/benchmarks/photoswipe/photoswipe.d.ts", "PhotoSwipe", BROWSER, options));
        benchmarks.put("CreateJS", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/createjs/createjs.js", "test/benchmarks/createjs/createjs.d.ts", "createjs", BROWSER, options));
        benchmarks.put("Vue.js", new Benchmark(ParseDeclaration.Environment.ES6DOM, "test/benchmarks/vue/vue.js", "test/benchmarks/vue/index.d.ts", "Vue", BROWSER, options));
        benchmarks.put("three.js", new Benchmark(ParseDeclaration.Environment.ES6DOM, "test/benchmarks/three/three.js", "test/benchmarks/three/three.d.ts", "THREE", BROWSER, options));
        benchmarks.put("Leaflet", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/leaflet/leaflet.js", "test/benchmarks/leaflet/leaflet.d.ts", "L", BROWSER, options));

        benchmarks.put("Backbone.js",
                new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/backbone/backbone.js", "test/benchmarks/backbone/backbone.d.ts", "Backbone", BROWSER, options)
                .addDependencies(underscore)
        );

        benchmarks.put("Lodash", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/lodash/lodash.js", "test/benchmarks/lodash/lodash.d.ts", "_", NODE,
                options.getBuilder()
                        .setDisableGenerics(true)
                        .build()
        ));

        benchmarks.put("P2.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/p2/p2.js", "test/benchmarks/p2/p2.d.ts", "p2", BROWSER, options));

        benchmarks.put("Zepto.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/zepto/zepto.js", "test/benchmarks/zepto/zepto.d.ts", "$", BROWSER, options));

        benchmarks.put("Redux", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/redux/redux.js", "test/benchmarks/redux/reduxModule.d.ts", "redux", NODE, options));

        benchmarks.put("Ionic", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/ionic/ionic.js", "test/benchmarks/ionic/ionic.d.ts", "ionic", BROWSER, options)
            .addDependencies(jQuery)
            .addDependencies(angular)
        );

        benchmarks.put("Foundation", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/foundation/foundation.js", "test/benchmarks/foundation/foundation.d.ts", "Foundation", BROWSER, options));

        benchmarks.put("Chart.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/chartjs/chart.js", "test/benchmarks/chartjs/chart.d.ts", "Chart", BROWSER, options));

        benchmarks.put("Video.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/video/video.js", "test/benchmarks/video/video.d.ts", "videojs", BROWSER, options));

        benchmarks.put("reveal.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/reveal/reveal.js", "test/benchmarks/reveal/reveal.d.ts", "Reveal", BROWSER, options));

        benchmarks.put("Materialize", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/materialize/materialize.js", "test/benchmarks/materialize/materialize.d.ts", "Materialize", BROWSER, options)
                .addDependencies(jQuery)
                .addDependencies(pickadate)
        );

        benchmarks.put("CodeMirror", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/codemirror/codemirror.js", "test/benchmarks/codemirror/codemirror.d.ts", "CodeMirror", BROWSER, options));

        benchmarks.put("bluebird", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/bluebird/bluebird.js", "test/benchmarks/bluebird/bluebird.d.ts", "Bluebird", NODE, options));

        benchmarks.put("Modernizr", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/modernizr/modernizr.js", "test/benchmarks/modernizr/modernizr.d.ts", "Modernizr", BROWSER, options));

        benchmarks.put("RxJS", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/rx/Rx.js", "test/benchmarks/rx/types/rx/index.d.ts", "Rx", NODE, options.getBuilder().setDisableGenerics(true).build()));

        Benchmark when = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/when/when.js", "test/benchmarks/when/when.d.ts", "When", NODE, options);
        benchmarks.put("When.js", when);

        benchmarks.put("Autobahn|JS", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/autobahn/autobahn.js", "test/benchmarks/autobahn/autobahn.d.ts", "autobahn", BROWSER, options)
            .addDependencies(when)
        );

        benchmarks.put("PDF.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/pdf/pdf.js", "test/benchmarks/pdf/pdf.d.ts", "PDFJS", BROWSER, options));

        benchmarks.put("highlight.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/highlight/highlight.js", "test/benchmarks/highlight/highlight.d.ts", "hljs", BROWSER, options));

        // If need more benchmarks, get some from here: https://www.javascripting.com/?p=4
        // TODO: After this latests round of optimization, try to enable generics on stuff.
        // TODO: Fabric and Ember has soundness-issues (try to search for target: and currentTarget:)
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
//    @Ignore
    public void runSmallDrivers() throws Exception {
        OutputParser.RunResult result = OutputParser.combine(RunSmall.runSmallDrivers(benchmark, RunSmall.runDriver(benchmark.run_method, 60 * 1000)));

        for (OutputParser.TypeError typeError : result.typeErrors) {
            System.out.println(typeError);
        }
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
        Benchmark benchmark = this.benchmark.withRunMethod(BOOTSTRAP);
        if (
                benchmark.dTSFile.contains("box2dweb.d.ts") ||// box2dweb uses bivariant function arguments, which is unsound, and causes this soundness-test to fail.
                        benchmark.dTSFile.contains("leaflet.d.ts") || // same unsoundness in leaflet. (Demonstrated in complexSanityCheck9)
                        benchmark.dTSFile.contains("jquery.d.ts") || // Exactly the same thing, the two then methods of JQueryGenericPromise are being overridden in an unsound way.
                        benchmark.dTSFile.contains("ember.d.ts") || // It includes jQuery, therefore it fails.
                        benchmark.dTSFile.contains("fabric.d.ts") || // Unsoundness in the noTransform argument of the render method (and that is it!).
                        benchmark.dTSFile.contains("materialize.d.ts") || // Includes jQuery.
                        benchmark.dTSFile.contains("backbone.d.ts")  // Includes jQuery.
                ) {
            System.out.println("Is a benchmark which i know to fail. ");
            return;
        }

        Main.writeFullDriver(benchmark); // No seed specified, in case of failure, the seed can be seen from the output.
        System.out.println("Driver written");
        String output;
        try {
            output = Main.runBenchmark(benchmark, 60 * 1000);
        } catch (TimeoutException e) {
            System.out.println("Timeout");
            return;
        }
        System.out.println(output);
        OutputParser.RunResult result = OutputParser.parseDriverResult(output);

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
