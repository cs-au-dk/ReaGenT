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
//        register(new Benchmark("dropzone", ParseDeclaration.Environment.ES5Core, "test/flowtyped/dropzone/dropzone.js", "test/flowtyped/dropzone/declaration.js", NODE, options)); // File API?

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
//        register(new Benchmark("checkdigit", ParseDeclaration.Environment.ES5Core, "test/flowtyped/checkdigit/checkdigit.js", "test/flowtyped/checkdigit/declaration.js", NODE, options));
//        register(new Benchmark("chromatism", ParseDeclaration.Environment.ES5Core, "test/flowtyped/chromatism/chromatism.js", "test/flowtyped/chromatism/declaration.js", NODE, options));
//        register(new Benchmark("clampjs", ParseDeclaration.Environment.ES5Core, "test/flowtyped/clampjs/clampjs.js", "test/flowtyped/clampjs/declaration.js", NODE, options));
//        register(new Benchmark("classnames", ParseDeclaration.Environment.ES5Core, "test/flowtyped/classnames/classnames.js", "test/flowtyped/classnames/declaration.js", NODE, options));
//        register(new Benchmark("clone", ParseDeclaration.Environment.ES5Core, "test/flowtyped/clone/clone.js", "test/flowtyped/clone/declaration.js", NODE, options));
//        register(new Benchmark("component-emitter", ParseDeclaration.Environment.ES5Core, "test/flowtyped/component-emitter/component-emitter.js", "test/flowtyped/component-emitter/declaration.js", NODE, options));
//        register(new Benchmark("credit-card-type", ParseDeclaration.Environment.ES5Core, "test/flowtyped/credit-card-type/credit-card-type.js", "test/flowtyped/credit-card-type/declaration.js", NODE, options));
//        register(new Benchmark("cuid", ParseDeclaration.Environment.ES5Core, "test/flowtyped/cuid/cuid.js", "test/flowtyped/cuid/declaration.js", NODE, options));
//        register(new Benchmark("data.either", ParseDeclaration.Environment.ES5Core, "test/flowtyped/data.either/data.either.js", "test/flowtyped/data.either/declaration.js", NODE, options));
//        register(new Benchmark("data.maybe", ParseDeclaration.Environment.ES5Core, "test/flowtyped/data.maybe/data.maybe.js", "test/flowtyped/data.maybe/declaration.js", NODE, options));
//        register(new Benchmark("date-range-array", ParseDeclaration.Environment.ES5Core, "test/flowtyped/date-range-array/date-range-array.js", "test/flowtyped/date-range-array/declaration.js", NODE, options));
//        register(new Benchmark("dateformat", ParseDeclaration.Environment.ES5Core, "test/flowtyped/dateformat/dateformat.js", "test/flowtyped/dateformat/declaration.js", NODE, options));
//        register(new Benchmark("decimal.js-light", ParseDeclaration.Environment.ES5Core, "test/flowtyped/decimal.js-light/decimal.js-light.js", "test/flowtyped/decimal.js-light/declaration.js", NODE, options));
//        register(new Benchmark("dedent", ParseDeclaration.Environment.ES5Core, "test/flowtyped/dedent/dedent.js", "test/flowtyped/dedent/declaration.js", NODE, options));
//        register(new Benchmark("deep-diff", ParseDeclaration.Environment.ES5Core, "test/flowtyped/deep-diff/deep-diff.js", "test/flowtyped/deep-diff/declaration.js", NODE, options));
//        register(new Benchmark("deep-freeze-strict", ParseDeclaration.Environment.ES5Core, "test/flowtyped/deep-freeze-strict/deep-freeze-strict.js", "test/flowtyped/deep-freeze-strict/declaration.js", NODE, options));
//        register(new Benchmark("deep-freeze", ParseDeclaration.Environment.ES5Core, "test/flowtyped/deep-freeze/deep-freeze.js", "test/flowtyped/deep-freeze/declaration.js", NODE, options));
//        register(new Benchmark("deep-merge", ParseDeclaration.Environment.ES5Core, "test/flowtyped/deep-merge/deep-merge.js", "test/flowtyped/deep-merge/declaration.js", NODE, options));
//        register(new Benchmark("double-ended-queue", ParseDeclaration.Environment.ES5Core, "test/flowtyped/double-ended-queue/double-ended-queue.js", "test/flowtyped/double-ended-queue/declaration.js", NODE, options));
//        register(new Benchmark("es6-error", ParseDeclaration.Environment.ES5Core, "test/flowtyped/es6-error/es6-error.js", "test/flowtyped/es6-error/declaration.js", NODE, options));
//        register(new Benchmark("escape-html", ParseDeclaration.Environment.ES5Core, "test/flowtyped/escape-html/escape-html.js", "test/flowtyped/escape-html/declaration.js", NODE, options));
//        register(new Benchmark("escape-string-regexp", ParseDeclaration.Environment.ES5Core, "test/flowtyped/escape-string-regexp/escape-string-regexp.js", "test/flowtyped/escape-string-regexp/declaration.js", NODE, options));
        register(new Benchmark("fast-safe-stringify", ParseDeclaration.Environment.ES5Core, "test/flowtyped/fast-safe-stringify/fast-safe-stringify.js", "test/flowtyped/fast-safe-stringify/declaration.js", NODE, options));



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
