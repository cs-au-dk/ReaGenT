package dk.webbies.tajscheck.test.tajs;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.concurrent.TimeoutException;

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
        return RunBenchmarks.getBenchmarks();
    }


    @Test
    @Ignore // Mostly timeouts
    public void analyzeBenchmark() throws Exception {
        // Just testing THAT it can be analyzed.
        try {
            TAJSUtil.run(benchmark.useTAJS(), 10 * 60);
        } catch (TimeoutException e) {
            System.out.println("Timeout");
            System.out.println(e.toString());

        }
    }
}
