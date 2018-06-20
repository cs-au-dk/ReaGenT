package dk.webbies.tajscheck.test.flow;

import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.CoverageResult;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.RunSmall;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.TestParsing;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
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
import static dk.webbies.tajscheck.test.dynamic.RunBenchmarks.printErrors;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by erik1 on 22-11-2016.
 */
@RunWith(Parameterized.class)
public class FlowBenchmarks {
    @SuppressWarnings("WeakerAccess")
    @Parameterized.Parameter
    public Benchmark benchmark = null;

    @SuppressWarnings("WeakerAccess")
    public static final Map<String, Benchmark> benchmarks = new HashMap<>();

    static {
        CheckOptions options = CheckOptions.builder()
                .setSplitUnions(false) // Because some of these benchmarks use an insane amount of overloads, so this can cause the size of the generated program to explode (about a factor 400x for moment).
                .setCompactOutput(true)
                .build();

        // TODO: Seems not to work
//        register(new Benchmark("Chromcast-caf-receiver", ParseDeclaration.Environment.ES5Core, "test/flowtyped/chromecast-caf-receiver/cast_receiver_framework.js", "test/flowtyped/chromecast-caf-receiver/declaration.js", BROWSER, options));
//        register(new Benchmark("amplitude-js", ParseDeclaration.Environment.ES5Core, "test/flowtyped/amplitude-js/amplitude.js", "test/flowtyped/amplitude-js/declaration.js", NODE, options));
//        register(new Benchmark("axios", ParseDeclaration.Environment.ES5Core, "test/flowtyped/axios/axios.js", "test/flowtyped/axios/declaration.js", NODE, options));

        // Might work, not sure.
//        register(new Benchmark("base64url", ParseDeclaration.Environment.ES5Core, "test/flowtyped/base64url/base64url.js", "test/flowtyped/base64url/declaration.js", NODE, options));

        // Works.
//        register(new Benchmark("aphrodite.js", ParseDeclaration.Environment.ES5Core, "test/flowtyped/aphrodite/aphrodite.js", "test/flowtyped/aphrodite/declaration.js", NODE, options));
//        register(new Benchmark("balanced-match", ParseDeclaration.Environment.ES5Core, "test/flowtyped/balanced-match/balanced-match.js", "test/flowtyped/balanced-match/declaration.js", NODE, options));
//        register(new Benchmark("big.js", ParseDeclaration.Environment.ES5Core, "test/flowtyped/big.js/big.js", "test/flowtyped/big.js/declaration.js", NODE, options));
//        register(new Benchmark("bigi", ParseDeclaration.Environment.ES5Core, "test/flowtyped/bigi/bigi.js", "test/flowtyped/bigi/declaration.js", NODE, options));
//        register(new Benchmark("blacklist", ParseDeclaration.Environment.ES5Core, "test/flowtyped/blacklist/blacklist.js", "test/flowtyped/blacklist/declaration.js", NODE, options));
//        register(new Benchmark("bound-points", ParseDeclaration.Environment.ES5Core, "test/flowtyped/bound-points/bound-points.js", "test/flowtyped/bound-points/declaration.js", NODE, options));
//        register(new Benchmark("bunyan", ParseDeclaration.Environment.ES5Core, "test/flowtyped/bunyan/bunyan.js", "test/flowtyped/bunyan/declaration.js", NODE, options));
//        register(new Benchmark("camelcase", ParseDeclaration.Environment.ES5Core, "test/flowtyped/camelcase/camelcase.js", "test/flowtyped/camelcase/declaration.js", NODE, options));
//        register(new Benchmark("canvas-starfield", ParseDeclaration.Environment.ES5Core, "test/flowtyped/canvas-starfield/canvas-starfield.js", "test/flowtyped/canvas-starfield/declaration.js", BROWSER, options));
//        register(new Benchmark("chance", ParseDeclaration.Environment.ES5Core, "test/flowtyped/chance/chance.js", "test/flowtyped/chance/declaration.js", NODE, options));
        register(new Benchmark("checkdigit", ParseDeclaration.Environment.ES5Core, "test/flowtyped/checkdigit/checkdigit.js", "test/flowtyped/checkdigit/declaration.js", NODE, options));


    }

    private static void register(Benchmark benchmark) {
        assert !benchmarks.containsKey(benchmark.name);
        benchmarks.put(benchmark.name, benchmark);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Benchmark> getBenchmarks() {
        return new ArrayList<>(benchmarks.values());
    }

    @Test
    public void genTSTestDriver() throws Exception {
        Main.writeFullDriver(benchmark);
    }

    @Test
    public void runTSTest() throws Exception {
        Benchmark b = this.benchmark;
        Main.writeFullDriver(b);

        String out = Main.runBenchmark(this.benchmark);
        System.out.println(out);

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
    public void RunReaGenT() throws Exception {
        System.out.println(TAJSUtil.runNoDriver(this.benchmark, 2 * 60));
    }
}
