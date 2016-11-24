package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import org.junit.Ignore;
import org.junit.Test;

import static dk.webbies.tajscheck.benchmarks.Benchmark.LOAD_METHOD.NODE;

/**
 * Created by erik1 on 22-11-2016.
 */
public class TestUnderscore {

    @Ignore // TODO: Get this to work, first remove runtimeExceptions which are thrown, then limit the number of tests created, then re-introduce the runtimeExceptions.
    @Test
    public void genFullUnderscoreDriver() throws Exception {
        Benchmark underscore = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/underscore/underscore.js", "test/underscore/underscore.d.ts", "_", NODE);

        String program = Main.generateFullDriver(underscore);

    }
}
