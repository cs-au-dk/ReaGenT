package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.benchmarks.Benchmarks;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 22-11-2016.
 */
@RunWith(Parameterized.class)
public class RunAllBenchmarks {

    @SuppressWarnings("WeakerAccess")
    @Parameterized.Parameter
    public Benchmark benchmark = null;

    @Parameterized.Parameters(name = "{0}")
    public static List<Benchmark> getData() {
        return Arrays.stream(Benchmarks.class.getDeclaredFields()).filter(field -> field.getType() == Benchmark.class).map(field -> readField(Benchmark.class, field)).collect(Collectors.toList());
    }

    private static <T> T readField(Class<T> clazz, Field field) {
        try {
            Object value = field.get(null);
            return clazz.cast(value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
