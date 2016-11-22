package dk.webbies.tajscheck;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.benchmarks.Benchmarks;
import dk.webbies.tajscheck.buildprogram.TestProgramBuilder;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.paser.AST.Statement;
import dk.webbies.tajscheck.paser.AstToStringVisitor;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.testcreator.test.LoadModuleTest;
import dk.webbies.tajscheck.testcreator.TestCreator;
import dk.webbies.tajscheck.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by erik1 on 01-11-2016.
 */
public class Main {
    public static final int CHECK_DEPTH = 0;
    public static final boolean CHECK_HEAP = false;
    public static final LOAD_TYPE loadType = LOAD_TYPE.RELATIVE;

    private enum LOAD_TYPE {
        ABSOLUTE_WINDOWS,
        ABSOLUTE_WSL,
        RELATIVE
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        try {
//            generateTestProgram(Benchmarks.test);

            generateTestProgram(Benchmarks.moment);
            genSmallDrivers(Benchmarks.moment);

            generateTestProgram(Benchmarks.async);
            genSmallDrivers(Benchmarks.async);

            /*createRecordedProgram(Benchmarks.moment, new ExecutionRecording(
                    new int[]{92,129,7,266,291,59,355,433,35,216,157,163,212,422,155,118,271,148,23,176,234,240,6,268,154,321,145,391,59,386,357,357,386,138,244,197,388,195,155,148,352,379,394,151,328,405,391,322,343},
                    "0.36510822415053457"
            ));*/

//            generateTestProgram(Benchmarks.async);
//            generateTestProgram(Benchmarks.underscore);
        } finally {
            System.out.println("In: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        }
    }

    private static void generateTestProgram(Benchmark bench) throws IOException {
        System.out.println("Generating test program for " + bench.dTSFile);
        String programString = generateProgramForBench(bench, null);

        Util.writeFile(getTestFilePath(bench, "test.js"), programString);

//        System.out.println(programString);
    }

    private static String createRecordedProgram(Benchmark bench, ExecutionRecording recording) throws IOException {
        String programString = generateProgramForBench(bench, recording);

        Util.writeFile(getTestFilePath(bench, "recorded.js"), programString);

        System.out.println(programString);

        return programString;
    }

    private static void genSmallDrivers(Benchmark orgBench) throws IOException, InterruptedException {
        SpecReader spec = ParseDeclaration.getTypeSpecification(orgBench.environment, Collections.singletonList(orgBench.dTSFile));

        SpecReader emptySpec = ParseDeclaration.getTypeSpecification(orgBench.environment, new ArrayList<>());

        Set<Type> nativeTypes = TypesUtil.collectNativeTypes(spec, emptySpec);

        Map<Type, String> typeNames = ParseDeclaration.getTypeNamesMap(spec);

        Type typeToTest = ((InterfaceType) spec.getGlobal()).getDeclaredProperties().get(orgBench.module);

        List<Test> tests = TestCreator.createTests(nativeTypes, typeToTest, orgBench);

        int counter = 0;

        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (Test test : tests) {
            String path = test.getPath();

            Benchmark bench = orgBench.withPathsToTest(Collections.singletonList(path));

            int count = counter++;
            pool.submit(() -> {
                try {
                    System.out.println("Creating small driver for: " + path + "  " + (count + 1) + "/" + tests.size());

                    List<Test> specificTests = TestCreator.createTests(nativeTypes, typeToTest, bench);
                    specificTests.add(new LoadModuleTest(Main.getRequirePath(bench), typeToTest));

                    Statement program = new TestProgramBuilder(bench, nativeTypes, typeNames, specificTests, typeToTest).buildTestProgram(null);

                    String filePath = getTestFilePath(bench, "small_driver_" + count + ".js");

                    Util.writeFile(filePath, AstToStringVisitor.toString(program));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.MINUTES);

    }

    private static String generateProgramForBench(Benchmark bench, ExecutionRecording recording) throws IOException {
        SpecReader spec = ParseDeclaration.getTypeSpecification(bench.environment, Collections.singletonList(bench.dTSFile));

        SpecReader emptySpec = ParseDeclaration.getTypeSpecification(bench.environment, new ArrayList<>());

        Set<Type> nativeTypes = TypesUtil.collectNativeTypes(spec, emptySpec);

        Map<Type, String> typeNames = ParseDeclaration.getTypeNamesMap(spec);

        Type typeToTest = ((InterfaceType) spec.getGlobal()).getDeclaredProperties().get(bench.module);

        List<Test> tests = TestCreator.createTests(nativeTypes, typeToTest, bench);
        tests.add(new LoadModuleTest(Main.getRequirePath(bench), typeToTest));

        Statement program = new TestProgramBuilder(bench, nativeTypes, typeNames, tests, typeToTest).buildTestProgram(recording);

        return AstToStringVisitor.toString(program);
    }


    private static String getTestFilePath(Benchmark bench, String fileName) {
        String jsPath = bench.jsFile;
        int lastIndex = jsPath.lastIndexOf('/');

        String folder = jsPath.substring(0, lastIndex + 1);

        return folder + fileName;
    }

    private static String getRequirePath(Benchmark bench) {
        String jsPath = new File(bench.jsFile).getAbsolutePath();

        if (loadType == LOAD_TYPE.RELATIVE) {
            int lastIndex = jsPath.lastIndexOf('\\');
            String jsFile = jsPath.substring(lastIndex + 1, jsPath.length());

            return "./" + jsFile;
        }

        String jsName = Util.removeSuffix(jsPath, ".js");

        if (loadType == LOAD_TYPE.ABSOLUTE_WSL) {
            jsName = jsName.replace("C:\\", "/mnt/c/").replace("\\", "/");
        } else {
            assert loadType == LOAD_TYPE.ABSOLUTE_WINDOWS;
            jsName = jsName.replace("\\", "\\\\");
        }

        return jsName + ".js";
    }

}
