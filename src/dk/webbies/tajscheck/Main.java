package dk.webbies.tajscheck;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.buildprogram.DriverProgramBuilder;
import dk.webbies.tajscheck.paser.AST.Statement;
import dk.webbies.tajscheck.paser.AstToStringVisitor;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.testcreator.test.LoadModuleTest;
import dk.webbies.tajscheck.testcreator.TestCreator;
import dk.webbies.tajscheck.util.MinimizeArray;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;
import dk.webbies.tajscheck.util.chromeRunner.SeleniumDriver;
import dk.webbies.tajscheck.util.chromeRunner.SimpleMessageReceivingHTTPServer;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dk.webbies.tajscheck.buildprogram.DriverProgramBuilder.*;

/**
 * Created by erik1 on 01-11-2016.
 */
public class Main {
    public static final String TEST_FILE_NAME = "test.js";
    public static final String COVERAGE_FILE_NAME = "coverage.js";

    public static Pair<BenchmarkInfo, String> writeFullDriver(Benchmark bench) throws Exception {
        return writeFullDriver(bench, null);
    }

    public static Pair<BenchmarkInfo, String> writeFullDriver(Benchmark bench, ExecutionRecording recording) throws Exception {
        Pair<BenchmarkInfo, String> result = generateFullDriver(bench, recording);
        String programString = result.getRight();

        Util.writeFile(getFolderPath(bench) + TEST_FILE_NAME, programString);

        return result;
    }

    public static String createRecordedProgram(Benchmark bench, ExecutionRecording recording) throws Exception {
        String programString = generateFullDriver(bench, recording).getRight();

        Util.writeFile(getFolderPath(bench) + "recorded.js", programString);

        System.out.println(programString);

        return programString;
    }

    public static Pair<BenchmarkInfo, String> generateFullDriver(Benchmark bench) throws IOException {
        return generateFullDriver(bench, null);
    }

    public static Pair<BenchmarkInfo, String> generateFullDriver(Benchmark bench, ExecutionRecording recording) throws IOException {
        BenchmarkInfo info = BenchmarkInfo.create(bench);

        List<Test> tests = new TestCreator(info).createTests();
        tests.add(new LoadModuleTest(Main.getRequirePath(bench), info.typeToTest, info, bench.module));

        Statement program = new DriverProgramBuilder(tests, info).buildDriver(recording);

        return new Pair<>(info, AstToStringVisitor.toString(program));
    }

    public static String generateSmallestDriver(Benchmark bench, BooleanSupplier test) throws IOException {
        BenchmarkInfo info = BenchmarkInfo.create(bench);

        List<Test> tests = new TestCreator(info).createTests();
        tests.add(new LoadModuleTest(Main.getRequirePath(bench), info.typeToTest, info, bench.module));

        Test[] testsArray = tests.toArray(new Test[]{});
        int prevSize = -1;

        Util.writeFile(getFolderPath(bench) + TEST_FILE_NAME, AstToStringVisitor.toString(new DriverProgramBuilder(Arrays.asList(testsArray), info).buildDriver(null)));

        if (!test.getAsBoolean()) {
            throw new RuntimeException("Does not initially satisfy condition!");
        }

        while (prevSize != testsArray.length) {
            prevSize = testsArray.length;
            testsArray = MinimizeArray.minimizeArray((testsToTest) -> {
                try {
                    Util.writeFile(getFolderPath(bench) + TEST_FILE_NAME, AstToStringVisitor.toString(new DriverProgramBuilder(Arrays.asList(testsToTest), info).buildDriver(null)));

                    return test.getAsBoolean();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, testsArray);
        }

        Statement program = new DriverProgramBuilder(Arrays.asList(testsArray), info).buildDriver(null);

        Util.writeFile(getFolderPath(bench) + TEST_FILE_NAME, AstToStringVisitor.toString(program));

        return AstToStringVisitor.toString(program);
    }


    public static String getFolderPath(Benchmark bench) {
        String jsPath = bench.jsFile;
        int lastIndex = jsPath.lastIndexOf('/');

        return jsPath.substring(0, lastIndex + 1);
    }

    public static String getRequirePath(Benchmark bench) {
        String jsPath = new File(bench.jsFile).getAbsolutePath();

        int lastIndex = jsPath.lastIndexOf('\\');
        String jsFile = jsPath.substring(lastIndex + 1, jsPath.length());

        return "./" + jsFile;
    }

    public static String runBenchmark(Benchmark bench) throws IOException {
        String testFilePath = getFolderPath(bench) + TEST_FILE_NAME;
        return runBenchmark(testFilePath, bench);
    }

    public static String runBenchmark(String testFilePath, Benchmark bench) throws IOException {
        int timeout = bench.options.maxTime + Math.min(10 * 1000, bench.options.maxTime);
        switch (bench.run_method) {
            case NODE:
                return Util.runNodeScript(testFilePath, timeout);
            case BOOTSTRAP:
            case BROWSER:
                try {
                    return SeleniumDriver.executeScript(new File(getFolderPath(bench)), Util.readFile(testFilePath), timeout);
                } catch (ConnectionClosedException | HttpException e) {
                    throw new RuntimeException(e);
                }
            default:
                throw new RuntimeException("Unknown run method: " + bench.run_method);
        }
    }

    public static Map<String, CoverageResult> genCoverage(Benchmark bench) throws IOException {
        return genCoverage(bench, Main.TEST_FILE_NAME, true);
    }

    public static Map<String, CoverageResult> genCoverage(Benchmark bench, boolean writeDriver) throws IOException {
        return genCoverage(bench, Main.TEST_FILE_NAME, writeDriver);
    }

    public static Map<String, CoverageResult> genCoverage(Benchmark bench, String testFileName) throws IOException {
        return genCoverage(bench, testFileName, true);
    }

    public static Map<String, CoverageResult> genCoverage(Benchmark bench, String testFileName, boolean writeDriver) throws IOException {
        if (writeDriver) {
            try {
                writeFullDriver(bench.withOptions(options -> options.getBuilder().setCheckDepthReport(options.checkDepthUseValue).build()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        int timeout = bench.options.maxTime + Math.min(10 * 1000, bench.options.maxTime);

        if (bench.run_method == Benchmark.RUN_METHOD.NODE) {
            StringBuilder prefix = new StringBuilder();
            int foldersDeep = getFolderPath(bench).split("/").length;
            for (int i = 0; i < foldersDeep; i++) {
                prefix.append("../");
            }
            String coverageJsonPath = getFolderPath(bench) + "coverage/coverage.json";

            Util.deleteFile(coverageJsonPath);

            String testScript = Util.readFile(getFolderPath(bench) + testFileName);

            ServerSocket socket = new ServerSocket(0);
            socket.setSoTimeout(timeout + 10 * 1000);
            SimpleMessageReceivingHTTPServer server = new SimpleMessageReceivingHTTPServer(new File(""), Collections.emptyMap(), socket);
            new Thread(server::start).start();

            int port = socket.getLocalPort();
            String portString = Util.integerOfFixedLength(port, 6);
            testScript = testScript.replace("ISTANBUL_PORT_FOR_PARTIAL_RESULTS = 0", "ISTANBUL_PORT_FOR_PARTIAL_RESULTS = " + portString);
            Util.writeFile(getFolderPath(bench) + testFileName, testScript);

            Util.runNodeScript(prefix + "node_modules/istanbul/lib/cli.js cover " + testFileName, new File(getFolderPath(bench)), timeout);

            if (new File(coverageJsonPath).exists()) {
                return CoverageResult.parse(Util.readFile(coverageJsonPath));
            } else {
                socket.close();
                List<String> messages = server.awaitMessages();

                if (messages.isEmpty()) {
                    return new HashMap<>();
                }

                assert messages.size() == 1;
                String coverageResult = messages.get(0);
                coverageResult = Util.removeSuffix(Util.removePrefix(coverageResult, "::COVERAGE::"), "::/COVERAGE::");

                genCoverageReport(coverageResult, bench);

                return CoverageResult.parse(coverageResult);
            }
        }


        String instrumented = Util.runNodeScript("node_modules/istanbul/lib/cli.js instrument " + getFolderPath(bench) + testFileName, timeout);

        if (instrumented.isEmpty()) {
            return new HashMap<>();
        }

        String coverageFileName = getFolderPath(bench) + COVERAGE_FILE_NAME;
        Util.writeFile(coverageFileName, instrumented);

        String coverageResult = runBenchmark(coverageFileName, bench);

        assert coverageResult.startsWith("::COVERAGE::");
        assert coverageResult.endsWith("::/COVERAGE::");

        coverageResult = Util.removeSuffix(Util.removePrefix(coverageResult, "::COVERAGE::"), "::/COVERAGE::");

        Map<String, CoverageResult> result = CoverageResult.parse(coverageResult);
        assert result.size() == 1;

        String[] testFile = Util.readFile(getFolderPath(bench) + testFileName).split("\n");
        List<Integer> splitLines = Util.withIndex(Stream.of(testFile)).filter(pair -> pair.getLeft().contains(START_OF_FILE_MARKER)).map(Pair::getRight).collect(Collectors.toList());

        Map<String, Pair<Integer, Integer>> splitRules = new HashMap<>();
        for (int i = 0; i < splitLines.size(); i++) {
            int splitLine = splitLines.get(i);
            String jsName = testFile[splitLine].substring(("// " + START_OF_FILE_MARKER).length(), testFile[splitLine].length());
            if (i != splitLines.size() - 1) {
                splitRules.put(jsName, new Pair<>(splitLine, splitLines.get(i + 1)));
            } else {
                splitRules.put(jsName, new Pair<>(splitLine, testFile.length));
            }
        }

        assert result.size() == 1;

        genCoverageReport(coverageResult, bench);

        return result.get(testFileName.substring(testFileName.lastIndexOf('/') + 1)).split(splitRules);
    }

    private static void genCoverageReport(String coverageResult, Benchmark bench) throws IOException {
        Util.writeFile(getFolderPath(bench) + "coverage.json", coverageResult);

        StringBuilder prefix = new StringBuilder();
        int foldersDeep = getFolderPath(bench).split("/").length;
        for (int i = 0; i < foldersDeep; i++) {
            prefix.append("../");
        }

        Util.runNodeScript(prefix + "node_modules/istanbul/lib/cli.js report --dir coverage", new File(getFolderPath(bench)));
    }
}
