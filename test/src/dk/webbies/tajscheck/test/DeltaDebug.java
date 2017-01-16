package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.benchmarks.CheckOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
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

        boolean progress = false;
        // Removing things between curly brackets.
        String file = Util.readFile(filePath);
        write(filePath + ".smallest", file);

        int fromIndex = 0;
        while (fromIndex != -1) {
            fromIndex = file.indexOf('{', fromIndex + 1);
            int toIndex = findClosingCurlyBracket(file, fromIndex);
            if (file.indexOf('\n', fromIndex) == -1 || file.indexOf('\n', fromIndex) > toIndex) {
                continue;
            }
            if (toIndex == -1) {
                break;
            }
            String orgFile = file;
            file = file.substring(0, fromIndex + 1) + file.substring(toIndex, file.length());
            if (orgFile.equals(file)) {
                fromIndex = file.indexOf('{', fromIndex + 1);
                continue;
            }
            write(filePath, file);

            if (!test.getAsBoolean()) {
                // didn't work, need to put it back
                System.out.println("Bad minification (" + file.length() + ")");
                file = orgFile;
                write(filePath, file);
            } else {
                System.out.println("GOOD minification (" + file.length() + ")");
                write(filePath + ".smallest", file);
                progress = true;
            }
        }

        // Removing lines, one by one.
        String[] array = Util.readFile(filePath).split(Pattern.quote("\n"));
        array = Arrays.stream(array).map(str -> str.replace("\r", "")).collect(Collectors.toList()).toArray(new String[]{});

        // Kinda copy-pasted from here: https://github.com/wala/jsdelta/blob/master/src/delta_single.js


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

    private static int findClosingCurlyBracket(String file, int fromIndex) {
        int numberOfBrackets = 1;
        for (int i = fromIndex + 1; i < file.length(); i++) {
            char c = file.charAt(i);
            switch (c) {
                case '{':
                    numberOfBrackets++;
                    break;
                case '}':
                    numberOfBrackets--;
                    break;
            }
            if (numberOfBrackets == 0) {
                return i;
            }
        }
        return -1;
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
        write(filePath, String.join("\n", Arrays.asList(file)));
    }

    private static void write(String filePath, String file) throws IOException {
        Util.writeFile(filePath, file);
    }

    // Current fix jQuery procedure: comment out currentTarget and target of BaseJQueryEventObject.
    //                               comment out the two then methods of JQueryGenericPromise.
    public static void main(String[] args) throws IOException {
        Benchmark bench = RunBenchmarks.benchmarks.get("bluebird");
        String file = bench.jsFile;
        debug(file, () -> {
            //noinspection TryWithIdenticalCatches
            try {
                return testParsing(bench);
            }catch (IllegalArgumentException | StackOverflowError e) {
                e.printStackTrace();
                return false;
            } catch (Error | Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    private static boolean testParsing(Benchmark bench) throws Exception {
        try {
            TestParsing.testFile(bench.jsFile);
            return false;
        } catch (AssertionError e) {
            return true;
        }
    }


    private static boolean testSanity(Benchmark bench) throws Exception {
        bench = bench.withRunMethod(BOOTSTRAP);

        Main.writeFullDriver(bench); // No seed specified, in case of failure, the seed can be seen from the output.
        System.out.println("Driver written");
        String output;
        try {
            output = Main.runBenchmark(bench, 60 * 1000);
        } catch (TimeoutException e) {
            System.out.println("Timeout");
            return false;
        }
        System.out.println(output);
        OutputParser.RunResult result = OutputParser.parseDriverResult(output);

        for (OutputParser.TypeError typeError : result.typeErrors) {
            System.out.println(typeError);
        }


        return result.typeErrors.size() > 0;
    }



    // TODO: At some point, find whatever is happening here. (Something might also be happening with angular)
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
