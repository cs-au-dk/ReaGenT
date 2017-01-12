package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.ExecutionRecording;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.benchmarks.CheckOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.benchmarks.Benchmark.RUN_METHOD.BOOTSTRAP;
import static dk.webbies.tajscheck.benchmarks.Benchmark.RUN_METHOD.BROWSER;
import static dk.webbies.tajscheck.benchmarks.Benchmark.RUN_METHOD.NODE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by erik1 on 10-01-2017.
 */
public class DeltaDebug {
    public static void debug(String filePath, BooleanSupplier test) throws IOException {

        if (!test.getAsBoolean()) {
            throw new RuntimeException("Doesn't satisfy condition initially");
        }

        String[] array = Util.readFile(filePath).split(Pattern.quote("\n"));
        array = Arrays.stream(array).map(str -> str.replace("\r", "")).collect(Collectors.toList()).toArray(new String[]{});

        // Kinda copy-pasted from here: https://github.com/wala/jsdelta/blob/master/src/delta_single.js

        write(filePath + ".smallest", array);

        boolean progress = false;
        for (int sz = array.length >>> 1; sz > 0; sz >>>= 1) {
            System.out.println("  chunk size " + sz);
            int nchunks = (int) Math.floor(array.length / sz);
            for (int i = nchunks - 1; i >= 0; --i) {
                // try removing chunk i
                System.out.println("    chunk #" + i + " size(" + sz + ") arr(" + array.length + ")");
                int lo = i * sz;
                int hi = i == nchunks - 1 ? array.length : (i + 1) * sz;

                // avoid creating empty array if nonempty is set
                if (lo > 0 || hi < array.length) {
                    String[] orgArray = array.clone();
                    array = deleteFromArray(array, lo, hi - lo);
                    write(filePath, array);

                    if (!test.getAsBoolean()) {
                        // didn't work, need to put it back
                        array = orgArray;
                        write(filePath, array);
                    } else {
                        write(filePath + ".smallest", array);
                        progress = true;
                    }
                }
            }
        }

        write(filePath, array);

        if (progress) {
            debug(filePath, test);
            return;
        }

        System.out.println("Delta debugging complete. ");
    }

    private static String[] deleteFromArray(String[] array, int from, int length) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < Math.min(from, array.length); i++) {
            result.add(array[i]);
        }
        for (int i = from + length; i < array.length; i++) {
            result.add(array[i]);
        }

        return result.toArray(new String[]{});
    }

    private static void write(String filePath, String[] file) throws IOException {
        Util.writeFile(filePath, String.join("\n", Arrays.asList(file)));
    }

    // TODO: Something with curly-brackets, next time i use this.
    public static void main(String[] args) throws IOException {
        String file = "test/benchmarks/knockout/knockout.d.ts";
        debug(file, () -> {
            //noinspection TryWithIdenticalCatches
            try {
                return test();
            } catch (IllegalArgumentException | StackOverflowError e) {
                e.printStackTrace();
                return true;
            } catch (Error | Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                throw new RuntimeException();
            }
        });
    }

    private static boolean test() throws Exception {
        Benchmark bench = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/knockout/knockout.js", "test/benchmarks/knockout/knockout.d.ts", "ko", BOOTSTRAP, CheckOptions.builder().setSplitUnions(false).build());
        Main.generateFullDriver(bench);
        return false;
    }



    // TODO: At some point, find whatever is happening here.
    private static final class TestReact {
        public static void main(String[] args) throws Exception {
            while (true) {
                if (test()) {
                    System.out.println("FOUND SOUNDNESS ERROR");
                    break;
                }
            }
        }

        private static boolean test() throws Exception {
            Benchmark bench = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/react/react.js", "test/benchmarks/react/reactdelta.d.ts", "React", BOOTSTRAP, CheckOptions.builder().setSplitUnions(false).setIterationsToRun(1000000).build());
            Main.writeFullDriver(bench);
            System.out.println("Driver written");
            String output;
            try {
                output = Main.runBenchmark(bench, 2 * 60 * 1000);
            } catch (TimeoutException e) {
                System.out.println("Timeout");
                return false;
            }
            OutputParser.RunResult result = OutputParser.parseDriverResult(output);

            for (OutputParser.TypeError typeError : result.typeErrors) {
                System.out.println(typeError);
            }


            return result.typeErrors.size() > 0;
        }
    }
}
