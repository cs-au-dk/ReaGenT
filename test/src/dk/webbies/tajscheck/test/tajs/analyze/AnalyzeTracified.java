package dk.webbies.tajscheck.test.tajs.analyze;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class AnalyzeTracified {

    @SuppressWarnings("WeakerAccess")
    @Parameterized.Parameter
    public Benchmark benchmark = null;

    @Parameterized.Parameters(name = "{0}")
    public static List<Benchmark> getBenchmarks() {
        return RunBenchmarks.getBenchmarks().stream()
        .filter(bench ->
            new File(Util.removeSuffix(bench.jsFile, ".js") + "-tracified.js").exists()
        )
        .collect(Collectors.toList());
    }

    @Test
    public void initializeNormal() throws Exception {
        AnalyzeBenchmarks test = new AnalyzeBenchmarks();
        test.benchmark = this.benchmark;
        test.initialize();
    }

    @Test
    public void initializeTracified() throws Exception {
        AnalyzeBenchmarks test = new AnalyzeBenchmarks();
        test.benchmark = this.benchmark.withOptions(options -> options.setUseTracified(true));
        test.initialize();
    }
}
