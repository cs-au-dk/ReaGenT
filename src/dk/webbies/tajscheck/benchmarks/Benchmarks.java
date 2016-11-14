package dk.webbies.tajscheck.benchmarks;

import dk.webbies.tajscheck.parsespec.ParseDeclaration;

/**
 * Created by erik1 on 01-11-2016.
 */
public class Benchmarks {
    public static Benchmark test = new Benchmark(ParseDeclaration.Environment.ES5Core, "tests/test/module.js", "tests/test/module.d.ts", "moment");

    public static Benchmark moment = new Benchmark(ParseDeclaration.Environment.ES5Core, "tests/moment/moment.js", "tests/moment/moment.d.ts", "moment");

    public static Benchmark async = new Benchmark(ParseDeclaration.Environment.ES5Core, "tests/async/async.js", "tests/async/async.d.ts", "async");

    public static Benchmark underscore = new Benchmark(ParseDeclaration.Environment.ES5Core, "tests/underscore/underscore.js", "tests/underscore/underscore.d.ts", "_");
}
