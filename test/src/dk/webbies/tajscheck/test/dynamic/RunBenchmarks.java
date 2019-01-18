package dk.webbies.tajscheck.test.dynamic;

import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.CoverageResult;
import dk.webbies.tajscheck.DynamicMain;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.RunSmall;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.test.TestParsing;
import dk.webbies.tajscheck.test.experiments.CountUniques;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Pair;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dk.webbies.tajscheck.benchmark.Benchmark.RUN_METHOD.*;
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

    private static void register(Benchmark benchmark) {
        assert !benchmarks.containsKey(benchmark.name);
        benchmarks.put(benchmark.name, benchmark);
    }

    static {
        CheckOptions options = CheckOptions.builder()
                .setSplitUnions(false) // Because some of these benchmarks use an insane amount of overloads, so this can cause the size of the generated program to explode (about a factor 400x for moment).
                .setCompactOutput(true)
                .build();

        register(new Benchmark("Moment.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/moment/moment.js", "test/benchmarks/moment/moment.d.ts", BROWSER, options));
        register(new Benchmark("async", ParseDeclaration.Environment.ES5Core, "test/benchmarks/async/async.js", "test/benchmarks/async/async.d.ts", BROWSER, options));
        register(new Benchmark("pathjs", ParseDeclaration.Environment.ES5Core, "test/benchmarks/pathjs/pathjs.js", "test/benchmarks/pathjs/pathjs.d.ts", BROWSER, options));
        register(new Benchmark("accounting.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/accounting/accounting.js", "test/benchmarks/accounting/accounting.d.ts", BROWSER, options));
        register(new Benchmark("lunr.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/lunr/lunr.js", "test/benchmarks/lunr/lunr.d.ts", BROWSER, options));
        register(new Benchmark("PixiJS", ParseDeclaration.Environment.ES5Core, "test/benchmarks/pixi/pixi.js", "test/benchmarks/pixi/pixi.d.ts", BROWSER, options));

        /*"fixedMoment", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/fixedMoment/moment.js", "test/benchmarks/fixedMoment/moment.d.ts", "moment", NODE, options));*/


        register(new Benchmark("Ace", ParseDeclaration.Environment.ES5Core, "test/benchmarks/ace/ace.js", "test/benchmarks/ace/ace.d.ts", BROWSER, options));
        Benchmark jQuery = new Benchmark("jQuery", ParseDeclaration.Environment.ES5Core, "test/benchmarks/jquery/jquery.js", "test/benchmarks/jquery/jquery.d.ts", BROWSER, options);
        register(jQuery);


        Benchmark angular = new Benchmark("AngularJS", ParseDeclaration.Environment.ES5Core, "test/benchmarks/angular1/angular1.js", "test/benchmarks/angular1/angular1.d.ts", BROWSER, options).addDependencies(jQuery);
        register(angular);


        register(new Benchmark("box2dweb", ParseDeclaration.Environment.ES5Core, "test/benchmarks/box2dweb/box2dweb.js", "test/benchmarks/box2dweb/box2dweb.d.ts", BROWSER, options));

        Benchmark underscore = new Benchmark("Underscore.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/underscore/underscore.js", "test/benchmarks/underscore/underscore.d.ts", NODE,
                options.getBuilder()
                .setFirstMatchSignaturePolicy(false)
                .build()
        );
        register(underscore);

        Benchmark handlebars = new Benchmark("Handlebars", ParseDeclaration.Environment.ES6DOM, "test/benchmarks/handlebars/handlebars.js", "test/benchmarks/handlebars/handlebars.d.ts", NODE, options);
        register(handlebars);

        register(new Benchmark("Hammer.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/hammer/hammer.js", "test/benchmarks/hammer/hammer.d.ts", BROWSER, options));

        register(new Benchmark("Jasmine", ParseDeclaration.Environment.ES5Core, "test/benchmarks/jasmine/jasmine.js", "test/benchmarks/jasmine/jasmine.d.ts", NODE, options));

        register(new Benchmark("Knockout", ParseDeclaration.Environment.ES5Core, "test/benchmarks/knockout/knockout.js", "test/benchmarks/knockout/knockout.d.ts", NODE, options));

        register(new Benchmark("Fabric.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/fabric/fabric.js", "test/benchmarks/fabric/fabric.d.ts", BROWSER, options));

        register(new Benchmark("Ember.js", ParseDeclaration.Environment.ES5DOM, "test/benchmarks/ember/ember.js", "test/benchmarks/ember/ember.d.ts", BROWSER, options)
            .addDependencies(jQuery, handlebars)
        );

        register(new Benchmark("D3.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/d3/d3.js", "test/benchmarks/d3/d3.d.ts", NODE, options.getBuilder()
                .setDisableGenerics(true)
                .build(), "\"d3\""));

        register(new Benchmark("MathJax", ParseDeclaration.Environment.ES5Core, "test/benchmarks/mathjax/mathjax.js", "test/benchmarks/mathjax/mathjax.d.ts", BROWSER, options));

        register(new Benchmark("PeerJS", ParseDeclaration.Environment.ES5Core, "test/benchmarks/peerjs/peerjs.js", "test/benchmarks/peerjs/peerjs.d.ts", BROWSER, options));
        register(new Benchmark("PleaseJS", ParseDeclaration.Environment.ES5Core, "test/benchmarks/pleasejs/please.js", "test/benchmarks/pleasejs/please.d.ts", BROWSER, options));
        Benchmark webcomponents = new Benchmark("webcomponents.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/webcomponents/webcomponents.js", "test/benchmarks/webcomponents/webcomponents.d.ts", BROWSER, options); // Doesn't really directly expose an API, so I'm just keeping it as dependency only.
        register(new Benchmark("Polymer", ParseDeclaration.Environment.ES5Core, "test/benchmarks/polymer/polymer.js", "test/benchmarks/polymer/polymer.d.ts", BROWSER, options).addDependencies(webcomponents));
        register(new Benchmark("q", ParseDeclaration.Environment.ES5Core, "test/benchmarks/q/q.js", "test/benchmarks/q/q.d.ts", NODE, options));
        register(new Benchmark("QUnit", ParseDeclaration.Environment.ES5Core, "test/benchmarks/qunit/qunit.js", "test/benchmarks/qunit/qunit.d.ts", BROWSER, options));
        register(new Benchmark("React", ParseDeclaration.Environment.ES5Core, "test/benchmarks/react/react.js", "test/benchmarks/react/react.d.ts", NODE, options));
        register(new Benchmark("RequireJS", ParseDeclaration.Environment.ES5Core, "test/benchmarks/requirejs/require.js", "test/benchmarks/requirejs/requirejs.d.ts", BROWSER, options).addDependencies(jQuery));
        register(new Benchmark("Sugar", ParseDeclaration.Environment.ES6DOM, "test/benchmarks/sugar/sugar.js", "test/benchmarks/sugar/sugar.d.ts", NODE,
                options.getBuilder()
                .setDisableGenerics(true)
                .build()

        ));

        register(new Benchmark("PhotoSwipe", ParseDeclaration.Environment.ES5Core, "test/benchmarks/photoswipe/photoswipe.js", "test/benchmarks/photoswipe/photoswipe.d.ts", BROWSER, options));
        register(new Benchmark("CreateJS", ParseDeclaration.Environment.ES5Core, "test/benchmarks/createjs/createjs.js", "test/benchmarks/createjs/createjs.d.ts", BROWSER, options));
        register(new Benchmark("Vue.js", ParseDeclaration.Environment.ES6DOM, "test/benchmarks/vue/vue.js", "test/benchmarks/vue/index.d.ts", BROWSER, options));
        register(new Benchmark("three.js", ParseDeclaration.Environment.ES6DOM, "test/benchmarks/three/three.js", "test/benchmarks/three/three.d.ts", BROWSER,
                options.getBuilder()
                .setDisableGenerics(true)
                .build()
        ));
        register(new Benchmark("Leaflet", ParseDeclaration.Environment.ES5Core, "test/benchmarks/leaflet/leaflet.js", "test/benchmarks/leaflet/leaflet.d.ts", BROWSER, options));

        register(new Benchmark("Backbone.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/backbone/backbone.js", "test/benchmarks/backbone/backbone.d.ts", BROWSER, options)
                .addDependencies(underscore)
                .addDependencies(jQuery)
        );

        register(new Benchmark("Lodash", ParseDeclaration.Environment.ES5Core, "test/benchmarks/lodash/lodash.js", "test/benchmarks/lodash/lodash.d.ts", NODE,
                options.getBuilder()
                        .setDisableGenerics(true)
                        .build()
        ));

        register(new Benchmark("P2.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/p2/p2.js", "test/benchmarks/p2/p2.d.ts", BROWSER, options));

        register(new Benchmark("Zepto.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/zepto/zepto.js", "test/benchmarks/zepto/zepto.d.ts", BROWSER, options));

        register(new Benchmark("Redux", ParseDeclaration.Environment.ES5Core, "test/benchmarks/redux/redux.js", "test/benchmarks/redux/redux.d.ts", NODE, options, "redux"));

        register(new Benchmark("Ionic", ParseDeclaration.Environment.ES5Core, "test/benchmarks/ionic/ionic.js", "test/benchmarks/ionic/ionic.d.ts", BROWSER, options)
            .addDependencies(jQuery, angular)
        );

        register(new Benchmark("Foundation", ParseDeclaration.Environment.ES5Core, "test/benchmarks/foundation/foundation.js", "test/benchmarks/foundation/foundation.d.ts", BROWSER, options)
                .addDependencies(jQuery)
        );

        register(new Benchmark("Chart.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/chartjs/chart.js", "test/benchmarks/chartjs/chart.d.ts", BROWSER, options));

        register(new Benchmark("Video.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/video/video.js", "test/benchmarks/video/video.d.ts", BROWSER, options));

        register(new Benchmark("reveal.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/reveal/reveal.js", "test/benchmarks/reveal/reveal.d.ts", BROWSER, options));

        Benchmark pickadate = new Benchmark("pickadate.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/pickadate/picker.js", "test/benchmarks/pickadate/pickadate.d.ts", BROWSER, options).addDependencies(jQuery); // Just a jQuery plugin, I therefore don't test it.
        register(new Benchmark("Materialize", ParseDeclaration.Environment.ES5Core, "test/benchmarks/materialize/materialize.js", "test/benchmarks/materialize/materialize.d.ts", BROWSER, options)
                .addDependencies(jQuery, pickadate)
        );

        register(new Benchmark("CodeMirror", ParseDeclaration.Environment.ES5Core, "test/benchmarks/codemirror/codemirror.js", "test/benchmarks/codemirror/codemirror.d.ts", BROWSER, options));

        register(new Benchmark("bluebird", ParseDeclaration.Environment.ES5Core, "test/benchmarks/bluebird/bluebird.js", "test/benchmarks/bluebird/bluebird.d.ts", NODE, options));

        register(new Benchmark("Modernizr", ParseDeclaration.Environment.ES5Core, "test/benchmarks/modernizr/modernizr.js", "test/benchmarks/modernizr/modernizr.d.ts", BROWSER, options));

        register(new Benchmark("RxJS", ParseDeclaration.Environment.ES5Core, "test/benchmarks/rx/Rx.js", "test/benchmarks/rx/types/rx/index.d.ts", NODE, options.getBuilder().setDisableGenerics(true).build(), "\"rx\""));

        register(new Benchmark("PDF.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/pdf/pdf.js", "test/benchmarks/pdf/pdf.d.ts", BROWSER, options));

        register(new Benchmark("highlight.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/highlight/highlight.js", "test/benchmarks/highlight/highlight.d.ts", NODE, options));

        register(new Benchmark("Intro.js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/intro/intro.js", "test/benchmarks/intro/intro.d.ts", NODE, options));

        register(new Benchmark("Swiper", ParseDeclaration.Environment.ES5Core, "test/benchmarks/swiper/swiper.js", "test/benchmarks/swiper/swiper.d.ts", BROWSER, options)); // TODO: Actually depends on a jQuery like library.

        register(new Benchmark("axios", ParseDeclaration.Environment.ES5Core, "test/benchmarks/axios/axios.js", "test/benchmarks/axios/axios.d.ts", NODE, options));

        register(new Benchmark("Medium Editor", ParseDeclaration.Environment.ES5Core, "test/benchmarks/medium-editor/medium-editor.js", "test/benchmarks/medium-editor/medium-editor.d.ts", BROWSER, options));

        register(new Benchmark("Sortable", ParseDeclaration.Environment.ES5Core, "test/benchmarks/sortable/sortable.js", "test/benchmarks/sortable/sortable.d.ts", BROWSER, options));

        register(new Benchmark("minimist", ParseDeclaration.Environment.ES5Core, "test/benchmarks/minimist/minimist.js", "test/benchmarks/minimist/minimist.d.ts", NODE, options));

        register(new Benchmark("classnames", ParseDeclaration.Environment.ES5Core, "test/benchmarks/classnames/classnames.js", "test/benchmarks/classnames/classnames.d.ts", NODE, options));

        register(new Benchmark("uuid", ParseDeclaration.Environment.ES5Core, "test/benchmarks/uuid/uuid.js", "test/benchmarks/uuid/uuid.d.ts", BROWSER, options));

        register(new Benchmark("jsyaml", ParseDeclaration.Environment.ES5Core, "test/benchmarks/jsyaml/jsyaml.js", "test/benchmarks/jsyaml/jsyaml.d.ts", BROWSER, options));

        register(new Benchmark("semver", ParseDeclaration.Environment.ES5Core, "test/benchmarks/semver/semver.js", "test/benchmarks/semver/semver.d.ts", NODE, options));

        register(new Benchmark("mime", ParseDeclaration.Environment.ES5Core, "test/benchmarks/mime/mime.js", "test/benchmarks/mime/mime.d.ts", BROWSER, options));

        // If need more benchmarks, get some from here: https://www.javascripting.com/?p=5

        // benchmarks originally from flow.

        // on break.
        register(new Benchmark("credit-card-type", ParseDeclaration.Environment.ES5Core, "test/benchmarks/credit-card-type/credit-card-type.js", "test/benchmarks/credit-card-type/declaration.d.ts", NODE, options)); // Found errors using TSTest. ReaGenT has some false positives due to imprecision. After pacthing TSTest is clean (not ReaGenT).

        // don't support the features in this.
        register(new Benchmark("deep-freeze", ParseDeclaration.Environment.ES5Core, "test/benchmarks/deep-freeze/deep-freeze.js", "test/benchmarks/deep-freeze/declaration.d.ts", BROWSER, options)); // TSTest clean. ReaGenT single false positive.

        // requires feature ReaGenT doesn't support to fix.
        register(new Benchmark("throttle-debounce", ParseDeclaration.Environment.ES5Core, "test/benchmarks/throttle-debounce/throttle-debounce.js", "test/benchmarks/throttle-debounce/declaration.d.ts", NODE, options)); // Had two very real errors. Found both by TSTest and ReaGenT. Both clean after patch.

        // fixed.
        register(new Benchmark("loglevel", ParseDeclaration.Environment.ES5Core, "test/benchmarks/loglevel/loglevel.js", "test/benchmarks/loglevel/declaration.d.ts", NODE, options)); // TSTest Clean. ReaGenT has false positives due to maybe undef and a single due to imprecision (missing Set of numbers abstraction).
        register(new Benchmark("component-emitter", ParseDeclaration.Environment.ES5Core, "test/benchmarks/component-emitter/component-emitter.js", "test/benchmarks/component-emitter/declaration.d.ts", BROWSER, options)); // TSTest clean. Some "maybe undef" false positives in ReaGenT. (When maybe undefs are removed, only thing left is a method that ReaGenT thinks returns exceptionally).
        register(new Benchmark("pluralize", ParseDeclaration.Environment.ES5Core, "test/benchmarks/pluralize/pluralize.js", "test/benchmarks/pluralize/declaration.d.ts", BROWSER, options)); // TSTest clean. ReaGenT has a bug in detecting where the value for the initialized library is.
        register(new Benchmark("js-cookie", ParseDeclaration.Environment.ES5Core, "test/benchmarks/js-cookie/js-cookie.js", "test/benchmarks/js-cookie/declaration.d.ts", BROWSER, options)); // Had small errors. 1 Benign error left with TSTest. ReaGenT has a single false positive after patch.
        register(new Benchmark("platform", ParseDeclaration.Environment.ES5Core, "test/benchmarks/platform/platform.js", "test/benchmarks/platform/declaration.d.ts", NODE, options)); // ReaGenT found an error that TSTest missed (the library is platform detection, TSTest only tests on Node, ReaGenT is abstract).


        // TODO: Add an "ignore function arg-types" option in StaticOptions.

        // If we need more fixed declaration files, pick from there:
        register(new Benchmark("deep-merge", ParseDeclaration.Environment.ES5Core, "test/benchmarks/deep-merge/deep-merge.js", "test/benchmarks/deep-merge/declaration.d.ts", BROWSER, options)); // A single benign error (array empty, output also empty...), found by both TSTest and ReaGenT. ReaGenT additionally has a single false positive.
        register(new Benchmark("clampjs", ParseDeclaration.Environment.ES5Core, "test/benchmarks/clampjs/clampjs.js", "test/benchmarks/clampjs/declaration.d.ts", BROWSER, options)); // clean
        register(new Benchmark("cuid", ParseDeclaration.Environment.ES5Core, "test/benchmarks/cuid/cuid.js", "test/benchmarks/cuid/declaration.d.ts", BROWSER, options)); // TSTest clean. ReaGenT has some false positives related to "always returns exceptionally".
        register(new Benchmark("json-stringify-safe", ParseDeclaration.Environment.ES5Core, "test/benchmarks/json-stringify-safe/json-stringify-safe.js", "test/benchmarks/json-stringify-safe/declaration.d.ts", BROWSER, options)); // clean
        register(new Benchmark("pretty-bytes", ParseDeclaration.Environment.ES5Core, "test/benchmarks/pretty-bytes/pretty-bytes.js", "test/benchmarks/pretty-bytes/declaration.d.ts", BROWSER, options)); // clean
        register(new Benchmark("dateformat", ParseDeclaration.Environment.ES5Core, "test/benchmarks/dateformat/dateformat.js", "test/benchmarks/dateformat/declaration.d.ts", BROWSER, options)); // clean.
        register(new Benchmark("mersenne-twister", ParseDeclaration.Environment.ES5Core, "test/benchmarks/mersenne-twister/mersenne-twister.js", "test/benchmarks/mersenne-twister/declaration.d.ts", BROWSER, options)); // clean
        register(new Benchmark("random-js", ParseDeclaration.Environment.ES5Core, "test/benchmarks/random-js/random-js.js", "test/benchmarks/random-js/declaration.d.ts", BROWSER, options)); // plenty of real errors.
        register(new Benchmark("filesize", ParseDeclaration.Environment.ES5Core, "test/benchmarks/filesize/filesize.js", "test/benchmarks/filesize/declaration.d.ts", BROWSER, options)); // clean.
        register(new Benchmark("qs", ParseDeclaration.Environment.ES5Core, "test/benchmarks/qs/qs.js", "test/benchmarks/qs/declaration.d.ts", BROWSER, options)); // Had a real error related to the any type. TSTest clean afterwards. ReaGenT still has some false positives.
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Benchmark> getBenchmarks() {
        return new ArrayList<>(benchmarks.values());
    }

    @Test
    public void genFullDriver() throws Exception {
        DynamicMain.writeFullDriver(benchmark);
    }

    @Test
    @Ignore
    public void runSmallDrivers() throws Exception {
        OutputParser.RunResult result = OutputParser.combine(RunSmall.runSmallDrivers(benchmark, RunSmall.runDriver(benchmark), 3, Integer.MAX_VALUE));

        for (OutputParser.TypeError typeError : result.typeErrors) {
            System.out.println(typeError);
        }
    }

    @Test
    public void runFullDriver() throws Exception {
        // Write the driver
        Benchmark b = benchmark
                .withOptions(CheckOptions::errorFindingOptions)
                .withOptions(options -> options.setConstructAllTypes(true));
//                .withOptions(CheckOptions::monitorUnknownPropertyAccesses);

        if (b.patched() != null) {
            b = b.patched();
        }

        DynamicMain.writeFullDriver(b);

        String out = DynamicMain.runBenchmark(b);
//        System.out.println(out);

        // Parse and print the result
        OutputParser.RunResult result = OutputParser.parseDriverResult(out);

        printErrors(b, result);

        for (String error: result.errors) {
            if (error.contains("isUTCOffset")) {
                continue;
            }
            System.out.println(error);
        }

        System.out.println();

        assert !out.trim().isEmpty();
    }

    @Test
    @Ignore // Coverage fails sometimes.
    public void coverage() throws Exception {
        if (Stream.of("underscore.d.ts", "fabric", "d3.d.ts", "backbone.d.ts", "three.d.ts").anyMatch(benchmark.dTSFile::contains)) {
            return; // Too big, node runs out of memory generating the instrumented version.
        }
        Map<String, CoverageResult> out = DynamicMain.genCoverage(benchmark);
        System.out.println("Coverage for " + benchmark.dTSFile);

        System.out.println(out);
    }

    @Test
    public void soundnessTest() throws Exception {
        Benchmark benchmark = this.benchmark.withRunMethod(BOOTSTRAP).withOptions(options -> options.setMaxIterationsToRun(100 * 1000).setConstructAllTypes(true).setCheckDepthReport(0).setCompactOutput(true));
        if (
                benchmark.dTSFile.contains("box2dweb.d.ts") ||// box2dweb uses bivariant function arguments, which is unsound, and causes this soundness-test to fail. (demonstrated in complexSanityCheck3)
                benchmark.dTSFile.contains("leaflet.d.ts") || // same unsoundness in leaflet. (Demonstrated in complexSanityCheck9)
                benchmark.dTSFile.contains("jquery.d.ts") || // Exactly the same thing, the two then methods of JQueryGenericPromise are being overridden in an unsound way.
                benchmark.dTSFile.contains("fabric.d.ts") || // Unsoundness in the noTransform argument of the render method (and that is it!).
                benchmark.dTSFile.contains("p2.d.ts") || // Has a class, that has a static length() function, this is not possible. (The class contains only static methods, go figure).
                benchmark.dTSFile.contains("ember.d.ts") || // It includes jQuery, therefore it fails.
                benchmark.dTSFile.contains("materialize.d.ts") || // Includes jQuery.
                benchmark.dTSFile.contains("three.d.ts") || // bivariant function-arguments in the addGroup() method (the last argument is optional in the base class, but non-optional in the sub class).
                benchmark.dTSFile.contains("backbone.d.ts")  || // Includes jQuery.
                benchmark.dTSFile.contains("foundation.d.ts")  || // Includes jQuery.
                benchmark.dTSFile.contains("angular1.d.ts") ||  // Includes jQuery.
                benchmark.dTSFile.contains("ionic.d.ts")  || // Includes angular, which includes jQuery.
                benchmark.dTSFile.contains("jasmine.d.ts")  || // ExpectationFailed overrides stack (string) with the any type, which is an unsound use of the any type.
                benchmark.dTSFile.contains("d3.d.ts")  || // TODO: Don't yet know why this fails.
                benchmark.dTSFile.contains("sugar.d.ts") // has known unsoundness, demonstrated in complexSanityCheck23
        ) {
            System.out.println("Is a benchmark which i know to fail. ");
            return;
        }

        DynamicMain.writeFullDriver(benchmark); // No seed specified, in case of failure, the seed can be seen from the output.
        System.out.println("Driver written");
        String output = DynamicMain.runBenchmark(benchmark);
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
        TestParsing.testFile(benchmark.jsFile, true);
        TestParsing.testFile(benchmark.jsFile, false);
    }

    public static void printErrors(Benchmark bench, OutputParser.RunResult result) {
        MultiMap<Pair<Type, String>, OutputParser.TypeError> groups = CountUniques.groupWarnings(result.typeErrors, bench);

        for (Map.Entry<Pair<Type, String>, Collection<OutputParser.TypeError>> entry : groups.asMap().entrySet()) {
            Collection<OutputParser.TypeError> errors = entry.getValue();

            if (errors.size() == 1) {
                System.out.println(errors.iterator().next());
                System.out.println();
            } else {
                System.out.println("Group of " + errors.size() + " similar errors");
                for (OutputParser.TypeError error : errors) {
                    String errorIndented = String.join("\n", Arrays.stream(error.toString().split(Pattern.quote("\n"))).map(line -> "   " + line).collect(Collectors.toList()));
                    System.out.println(errorIndented);
                    System.out.println();
                }
                System.out.println();

            }
        }
    }
}
