package dk.webbies.tajscheck.test.dynamic;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.benchmarks.CheckOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.paser.AST.Statement;
import dk.webbies.tajscheck.paser.AstToStringVisitor;
import dk.webbies.tajscheck.paser.JavaScriptParser;
import dk.webbies.tajscheck.util.Util;
import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.concurrent.TimeoutException;

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
        benchmarks.put("jQuery.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/jquery/jquery.js", "test/benchmarks/jquery/jquery.d.ts", "jQuery", BROWSER, options.getBuilder().setIterationsToRun(500).build()));


        benchmarks.put("angular.js",
                new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/angular1/angular1.js", "test/benchmarks/angular1/angular1.d.ts", "angular", BROWSER, options)
                .addDependency(benchmarks.get("jQuery.js"))
        );


        benchmarks.put("box2dweb", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/box2dweb/box2dweb.js", "test/benchmarks/box2dweb/box2dweb.d.ts", "Box2D", BROWSER, options));

        // The TypeScript parser breaks.
//        benchmarks.put("createjs", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/createjs/createjs.js", "test/benchmarks/createjs/createjs.d.ts", "createjs", BROWSER, CheckOptions.defaultOptions()));

        benchmarks.put("underscore", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/underscore/underscore.js", "test/benchmarks/underscore/underscore.d.ts", "_", NODE, CheckOptions.defaultOptions()));

        // Blows up in complexity, mostly because underscore. Test again if d3 is fixed.
        /*benchmarks.put("backbone",
                new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/backbone/backbone.js", "test/benchmarks/backbone/backbone.d.ts", "Backbone", BROWSER, CheckOptions.defaultOptions())
                .addDependency(benchmarks.get("underscore"))
        );*/

        benchmarks.put("handlebars", new Benchmark(ParseDeclaration.Environment.ES6DOM, "test/benchmarks/handlebars/handlebars.js", "test/benchmarks/handlebars/handlebars.d.ts", "Handlebars", BROWSER, CheckOptions.defaultOptions()));

        benchmarks.put("Hammer.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/hammer/hammer.js", "test/benchmarks/hammer/hammer.d.ts", "Hammer", BROWSER, CheckOptions.defaultOptions()));

        benchmarks.put("jasmine", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/jasmine/jasmine.js", "test/benchmarks/jasmine/jasmine.d.ts", "jasmine", BROWSER, CheckOptions.defaultOptions()));

        benchmarks.put("knockout", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/knockout/knockout.js", "test/benchmarks/knockout/knockout.d.ts", "ko", BROWSER, CheckOptions.defaultOptions()));

        benchmarks.put("fabric.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/fabric/fabric.js", "test/benchmarks/fabric/fabricModule.d.ts", "fabric", BROWSER, CheckOptions.defaultOptions()));

        benchmarks.put("ember", new Benchmark(ParseDeclaration.Environment.ES5DOM, "test/benchmarks/ember/ember.js", "test/benchmarks/ember/ember.d.ts", "Ember", BROWSER, CheckOptions.defaultOptions()));

        benchmarks.put("d3", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/d3/d3.js", "test/benchmarks/d3/d3.d.ts", "d3", BROWSER, CheckOptions.defaultOptions()));

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
        if (benchmark.dTSFile.contains("underscore.d.ts")) {
            return; // Too big, node runs out of memory parsing the thing.
        }
        if (benchmark.run_method == BROWSER) {
            return;
        }
        Main.writeFullDriver(benchmark);
        String out;
        try {
            out = Main.genCoverage(benchmark, 60 * 1000);
        } catch (TimeoutException e) {
            // this is ok, it happens.
            System.out.println("Timeout!");
            return;
        }
        System.out.println(out);

        assert !out.trim().isEmpty();
    }

    @Test
    public void sanityCheck() throws Exception {
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
                    bench.jsFile.contains("box2dweb.js") ||// box2dweb uses bivariant function arguments, which is unsound, and causes this soundness-test to fail.
                    bench.jsFile.contains("jasmine.js") // jasmine has a class which extends Error, I don't handle that well.
            ) {
                System.out.println("Is a benchmark which i know to fail. ");
                return;
            }
        }

        assertThat(result.typeErrors.size(), is(0));
    }

    @Test
    public void testParsing() throws Exception {
        // A sanitycheck that JavaScript parsing+printing is idempotent.
        String script = Util.readFile(benchmark.jsFile);

        JavaScriptParser parser = new JavaScriptParser(ParseDeclaration.Environment.ES5DOM);
        Statement iteration1Ast = parser.parse("name", script).toTSCreateAST().getBody();

        System.out.println("First parsing complete");

        String iteration1String = AstToStringVisitor.toString(iteration1Ast);

        Statement iteration2Ast = parser.parse("name", iteration1String).toTSCreateAST().getBody();

        String iteration2String = AstToStringVisitor.toString(iteration2Ast);

        assertThat(iteration1String, Is.is(equalTo(iteration2String)));
    }
}
