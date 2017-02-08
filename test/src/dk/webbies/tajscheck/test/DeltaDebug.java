package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.util.MinimizeArray;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.benchmark.Benchmark.RUN_METHOD.BOOTSTRAP;
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

        String newFile = removeCommentsAndWhitespace(file);
        write(filePath, newFile);

        if (newFile.length() == file.length()) {
            System.out.println("There were no comments or whitespace");
        } else if (test.getAsBoolean()) {
            System.out.println("Successfully remove whitespace and stuff");
            progress = true;
            file = newFile;
        } else {
            System.out.println("Removing whitespace failed");
        }

        progress |= testBracket(filePath, test, file, '{', '}');

        // Removing lines, one by one.
        String[] array = Util.readFile(filePath).split(Pattern.quote("\n"));
        array = Arrays.stream(array).map(str -> str.replace("\r", "")).collect(Collectors.toList()).toArray(new String[]{});

        // Kinda copy-pasted from here: https://github.com/wala/jsdelta/blob/master/src/delta_single.js


        int prevSize = array.length;

        array = MinimizeArray.minimizeArray((testArray) -> {
            try {
                write(filePath, testArray);

                boolean success;
                if (!test.getAsBoolean()) {
                    write(filePath, testArray);
                    success = false;
                } else {
                    write(filePath + ".smallest", testArray);
                    success = true;
                }
                return success;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, array);

        if (prevSize != array.length) {
            progress = true;
        }

        write(filePath, array);

        file = Util.readFile(filePath);
        progress |= testBracket(filePath, test, file, '(', ')');
        file = Util.readFile(filePath);
        progress |= testBracket(filePath, test, file, '[', ']');

        if (progress) {
            debug(filePath, test);
            return;
        }

        System.out.println("Delta debugging complete. ");
    }

    private static String removeCommentsAndWhitespace(String file) {
        int fromIndex = file.indexOf("/*");
        while (fromIndex != -1) {
            int toIndex = file.indexOf("*/", fromIndex);
            file = file.substring(0, fromIndex) + file.substring(toIndex + 2, file.length());
            fromIndex = file.indexOf("/*", fromIndex);
        }
        List<String> lines = Arrays.stream(file.split(Pattern.quote("\n")))
                .map(str -> str.replaceAll("\r", ""))
                .filter(str -> !str.trim().isEmpty())
                .collect(Collectors.toList());

        return String.join("\n", lines);
    }

    private static boolean testBracket(String filePath, BooleanSupplier test, String file, char start, char closing) throws IOException {
        boolean progress = false;
        int fromIndex = 0;
        while (fromIndex != -1) {
            fromIndex = file.indexOf(start, fromIndex + 1);
            int toIndex = findClosingBracket(file, fromIndex, start, closing);
            if (file.indexOf('\n', fromIndex) == -1 || file.indexOf('\n', fromIndex) > toIndex) {
                continue;
            }
            if (toIndex == -1) {
                break;
            }
            String orgFile = file;
            file = file.substring(0, fromIndex + 1) + file.substring(toIndex, file.length());
            if (orgFile.equals(file)) {
                fromIndex = file.indexOf(start, fromIndex + 1);
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
        return progress;
    }

    private static int findClosingBracket(String file, int fromIndex, char start, char closing) {
        int numberOfBrackets = 1;
        for (int i = fromIndex + 1; i < file.length(); i++) {
            char c = file.charAt(i);
            if (c == start) {
                numberOfBrackets++;
            } else if (c == closing) {
                numberOfBrackets--;
            }
            if (numberOfBrackets == 0) {
                return i;
            }
        }
        return -1;
    }

    private static void write(String filePath, String[] file) throws IOException {
        write(filePath, String.join("\n", Arrays.asList(file)));
    }

    private static void write(String filePath, String file) throws IOException {
        Util.writeFile(filePath, file);
    }

    public static void main(String[] args) throws IOException {
        Util.isDeltaDebugging = true;
        Benchmark bench = RunBenchmarks.benchmarks.get("bluebird");
//        Benchmark bench = RunBenchmarks.benchmark.get("AngularJS").withRunMethod(NODE);
        String file = bench.dTSFile;
        debug(file, () -> {
            //noinspection TryWithIdenticalCatches
            try {
                Main.writeFullDriver(bench);

                return Main.generateFullDriver(bench).contains("\"window.Bluebird.nodeify.[arg0].[arg1]\", \"(undefined or (a non null value and Array))\"");
            }catch (NullPointerException e) {
                return false;
            } catch (IllegalArgumentException e) {
                return false;
            } catch (Error | Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    private static boolean testHasError(Benchmark bench, String path) throws Exception {
        Main.writeFullDriver(bench);
        bench = bench.withOptions(bench.options.getBuilder().setMaxIterationsToRun(100).build());
        OutputParser.RunResult result = OutputParser.parseDriverResult(Main.runBenchmark(bench));

        return result.typeErrors.stream().map(OutputParser.TypeError::getPath).filter(str -> str.equals(path)).count() >= 1;
    }

    private static boolean testBiggerWithNoGenerics(Benchmark bench) throws IOException {
        String driverWithGenerics = Main.generateFullDriver(bench);
        int sizeWithGenerics = driverWithGenerics.length();
        String driverWithNoGenerics = Main.generateFullDriver(bench.withOptions(bench.options.getBuilder().setDisableGenerics(true).build()));
        int sizeWithNoGenerics = driverWithNoGenerics.length();
        boolean noGenericsSmaller = sizeWithNoGenerics > sizeWithGenerics;
        if (noGenericsSmaller) {
            Util.writeFile("test/nogenerics.js", driverWithNoGenerics);
            Util.writeFile("test/withgenerics.js", driverWithGenerics);
        }
        return noGenericsSmaller;
    }

    private static boolean testParsing(Benchmark bench) throws Exception {
        try {
            TestParsing.testFile(bench.jsFile);
            return false;
        } catch (AssertionError e) {
            return true;
        }
    }


    private static boolean testSoundness(Benchmark bench) throws Exception {
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
}
