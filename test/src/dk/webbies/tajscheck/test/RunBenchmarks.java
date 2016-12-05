package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static dk.webbies.tajscheck.benchmarks.Benchmark.LOAD_METHOD.BOOTSTRAP;
import static dk.webbies.tajscheck.benchmarks.Benchmark.LOAD_METHOD.BROWSER;
import static dk.webbies.tajscheck.benchmarks.Benchmark.LOAD_METHOD.REQUIRE;
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

    public static final Map<String, Benchmark> benchmarks = new HashMap<>();
    static {
//        benchmarks.put("moment", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/moment/moment.js", "test/benchmarks/moment/moment.d.ts", "moment", REQUIRE));
//        benchmarks.put("async", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/async/async.js", "test/benchmarks/async/async.d.ts", "async", REQUIRE));
//        benchmarks.put("path.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/pathjs/pathjs.js", "test/benchmarks/pathjs/pathjs.d.ts", "Path", BROWSER));
//        benchmarks.put("accounting.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/accounting/accounting.js", "test/benchmarks/accounting/accounting.d.ts", "accounting", REQUIRE));

        benchmarks.put("lunr.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/lunr/lunr.js", "test/benchmarks/lunr/lunr.d.ts", "lunr", REQUIRE)); // TODO: Get this to work, after unit-tests.
//        benchmarks.put("PIXI.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/pixi/pixi.js", "test/benchmarks/pixi/pixi.d.ts", "PIXI", REQUIRE)); // TODO: get "this" types to work.

        // TODO: npm packages, including typings. Have a test (that is @Ignored by default), that sets up all npm packages.
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
    public void genSmallDrivers() throws Exception {
        Main.genSmallDrivers(benchmark);
    }

    @Test
    public void runFullDriver() throws Exception {
        Main.writeFullDriver(benchmark);
        String out = Main.runFullDriver(benchmark);
        System.out.println(out);

        assert !out.trim().isEmpty();
    }

    @Test
    public void sanityCheck() throws Exception {
        Benchmark bench = this.benchmark.withLoadMethod(BOOTSTRAP);
        Main.writeFullDriver(bench); // No seed specified, in case of failure, the seed can be seen from the output.
        String output = Main.runFullDriver(bench);
        System.out.println(output);
        OutputParser.RunResult result = OutputParser.parseDriverResult(output);

        assertThat(result.typeErrors.size(), is(0));
    }
}
