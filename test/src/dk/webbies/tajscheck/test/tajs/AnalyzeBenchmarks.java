package dk.webbies.tajscheck.test.tajs;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.util.Util;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by erik1 on 19-12-2016.
 */
@RunWith(Parameterized.class)
public class AnalyzeBenchmarks {

    @SuppressWarnings("WeakerAccess")
    @Parameterized.Parameter
    public Benchmark benchmark = null;


    @Parameterized.Parameters(name = "{0}")
    public static List<Benchmark> getBenchmarks() {
        // pdf.js excluded because it has getters.
        return RunBenchmarks.getBenchmarks().stream().filter(bench -> Stream.of(
                // because it has getters/setters, which TAJS does not support
                "pdf.d.ts",
                "vue/index.d.ts",
                "three.d.ts",
                "ember.d.ts",
                "polymer.d.ts", // <- because webcomponents has getter.

                "underscore.d.ts", // hits an AnalysisLimitation / timeout.
                "requirejs.d.ts", // weird error, replicated in TestMicro
                "qunit.d.ts", // weird error with arrays.
                "react.d.ts", // No transfer function for Object.freeze.
                "modernizr.d.ts", // Run a WebGL function that is unsupported.
                "hammer.d.ts", // Object.assign crashes TAJS
                "medium-editor.d.ts", // "crashes" TAJS when calling Node.contains().
                "mathjax.d.ts" // runs into heavy problem doing init
        ).noneMatch(bench.dTSFile::contains)).collect(Collectors.toList());
    }

    // TODO: re-introduce the "small drivers", and analyze those. Use a parametarized test case.

    @Test
//    @Ignore // Mostly timeouts
    public void analyzeBenchmark() throws Exception {
        // Just testing that it CAN be analyzed.

        Main.writeFullDriver(benchmark);

        double size = Util.readFile(Main.getFolderPath(benchmark) + Main.TEST_FILE_NAME).length() / 1024.0;

        System.out.println(Util.toFixed(size, 2) + "kb driver");

        if (size > 2000) {
            return; // Currently ignoring when the driver is too big.
        }

        try {
            TAJSUtil.run(benchmark.useTAJS(), 10 * 60);
        } catch (TimeoutException e) {
            System.out.println("Timeout");
            System.out.println(e.toString());

        }
    }

    // Analyzeable without DOM enabled:
    /* TODO: See which of the benchmarks use DOM.
    rx
    box2dweb
    photoswipe
    accounting
    highlight
    please
    intro
    lunr
    jasmine
    knockout
    q
    async (takes slightly more than 10 seconds)
     */
    @Test
    public void initialize() throws Exception {
        try {
            TAJSUtil.run(benchmark.useTAJS().withOptions(options -> options.getBuilder().setOnlyInitialize(true).build()), 60);
        } catch (TimeoutException e) {
            System.out.println("Timeout");
            System.out.println(e.toString());
            throw e;
        }
    }
}
