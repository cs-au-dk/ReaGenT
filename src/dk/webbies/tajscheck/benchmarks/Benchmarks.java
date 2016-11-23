package dk.webbies.tajscheck.benchmarks;

import dk.webbies.tajscheck.parsespec.ParseDeclaration;

import static dk.webbies.tajscheck.benchmarks.Benchmark.LOAD_METHOD.BROWSER;
import static dk.webbies.tajscheck.benchmarks.Benchmark.LOAD_METHOD.NODE;

/**
 * Created by erik1 on 01-11-2016.
 */
public class Benchmarks {
    public static Benchmark test = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/test/module.js", "test/test/module.d.ts", "moment", NODE);

    public static Benchmark moment = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/moment/moment.js", "test/moment/moment.d.ts", "moment", NODE);

    public static Benchmark async = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/async/async.js", "test/async/async.d.ts", "async", NODE);

//    public static Benchmark underscore = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/underscore/underscore.js", "test/underscore/underscore.d.ts", "_", NODE);

    public static Benchmark pathjs = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/pathjs/pathjs.js", "test/pathjs/pathjs.d.ts", "Path", BROWSER);
}
