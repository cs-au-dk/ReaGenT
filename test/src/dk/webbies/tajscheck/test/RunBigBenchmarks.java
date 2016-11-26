package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Field;
import java.util.*;

import static dk.webbies.tajscheck.benchmarks.Benchmark.LOAD_METHOD.BROWSER;
import static dk.webbies.tajscheck.benchmarks.Benchmark.LOAD_METHOD.NODE;

/**
 * Created by erik1 on 22-11-2016.
 */
@RunWith(Parameterized.class)
public class RunBigBenchmarks {

    @SuppressWarnings("WeakerAccess")
    @Parameterized.Parameter
    public Benchmark benchmark = null;

    public static final Map<String, Benchmark> benchmarks = new HashMap<>();
    static {
        benchmarks.put("module", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/test/module.js", "test/test/module.d.ts", "moment", NODE));
        benchmarks.put("moment", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/moment/moment.js", "test/moment/moment.d.ts", "moment", NODE));
        benchmarks.put("async", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/async/async.js", "test/async/async.d.ts", "async", NODE));
        benchmarks.put("path.js", new Benchmark(ParseDeclaration.Environment.ES5Core, "test/pathjs/pathjs.js", "test/pathjs/pathjs.d.ts", "Path", BROWSER));
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
}
