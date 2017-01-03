package dk.webbies.tajscheck.test.dynamic;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.benchmarks.CheckOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.concurrent.TimeoutException;

import static dk.webbies.tajscheck.benchmarks.Benchmark.LOAD_METHOD.BOOTSTRAP;
import static dk.webbies.tajscheck.benchmarks.Benchmark.LOAD_METHOD.BROWSER;
import static dk.webbies.tajscheck.benchmarks.Benchmark.LOAD_METHOD.LOAD_LOCAL;
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
        benchmarks.put("moment", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/moment/moment.js", "test/benchmarks/moment/moment.d.ts", "moment", LOAD_LOCAL, options));
        benchmarks.put("async", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/async/async.js", "test/benchmarks/async/async.d.ts", "async", LOAD_LOCAL, options));
        benchmarks.put("path.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/pathjs/pathjs.js", "test/benchmarks/pathjs/pathjs.d.ts", "Path", BROWSER, options));
        benchmarks.put("accounting.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/accounting/accounting.js", "test/benchmarks/accounting/accounting.d.ts", "accounting", LOAD_LOCAL, options));
        benchmarks.put("lunr.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/lunr/lunr.js", "test/benchmarks/lunr/lunr.d.ts", "lunr", LOAD_LOCAL, options));
//        benchmarks.put("PIXI.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/pixi/pixi.js", "test/benchmarks/pixi/pixi.d.ts", "PIXI", LOAD_LOCAL, options)); // Commented out because big.

        benchmarks.put("fixedMoment", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/fixedMoment/moment.js", "test/benchmarks/fixedMoment/moment.d.ts", "moment", LOAD_LOCAL,
                options
                        .getBuilder()
                        .setCheckDepth(2)
                        .setCheckDepthForUnions(3)
                        .build()
        ));

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
        String out;
        try {
            out = Main.runFullDriver(benchmark, 20 * 1000);
        } catch (TimeoutException e) {
            // this is ok, it happens.
            System.out.println("Timeout!");
            return;
        }
//        System.out.println(out);

        System.out.println(out.split("\n")[0]);

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
        Benchmark bench = this.benchmark.withLoadMethod(BOOTSTRAP);
        Main.writeFullDriver(bench); // No seed specified, in case of failure, the seed can be seen from the output.
        String output = Main.runFullDriver(bench);
        System.out.println(output);
        OutputParser.RunResult result = OutputParser.parseDriverResult(output);

        assertThat(result.typeErrors.size(), is(0));
    }
}
