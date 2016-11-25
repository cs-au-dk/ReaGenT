package dk.webbies.tajscheck;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.benchmarks.Benchmark;
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

import static dk.webbies.tajscheck.buildprogram.TestProgramBuilder.*;

/**
 * Created by erik1 on 01-11-2016.
 */
public class Main {
    public static final int CHECK_DEPTH = 0;
    public static final boolean CHECK_HEAP = false;
    private static final String TEST_FILE_NAME = "test.js";

    public static void writeFullDriver(Benchmark bench) throws IOException {
        writeFullDriver(bench, null);
    }

    public static void writeFullDriver(Benchmark bench, ExecutionRecording recording) throws IOException {
        System.out.println("Generating test program for " + bench.dTSFile);
        String programString = generateFullDriver(bench, recording);

        Util.writeFile(getTestFilePath(bench, TEST_FILE_NAME), programString);

//        System.out.println(programString);
    }

    public static String createRecordedProgram(Benchmark bench, ExecutionRecording recording) throws IOException {
        String programString = generateFullDriver(bench, recording);

        Util.writeFile(getTestFilePath(bench, "recorded.js"), programString);

        System.out.println(programString);

        return programString;
    }

    public static void genSmallDrivers(Benchmark orgBench) throws IOException, InterruptedException {
        SpecReader spec = ParseDeclaration.getTypeSpecification(orgBench.environment, Collections.singletonList(orgBench.dTSFile));

        SpecReader emptySpec = ParseDeclaration.getTypeSpecification(orgBench.environment, new ArrayList<>());

        Set<Type> nativeTypes = TypesUtil.collectNativeTypes(spec, emptySpec);

        Map<Type, String> typeNames = ParseDeclaration.getTypeNamesMap(spec);

        Type typeToTest = ((InterfaceType) spec.getGlobal()).getDeclaredProperties().get(orgBench.module);

        TypeParameterIndexer typeParameterIndexer = new TypeParameterIndexer();

        List<Test> tests = new TestCreator(nativeTypes, typeToTest, orgBench, typeParameterIndexer, typeNames, typeParameterIndexer).createTests();

        int counter = 0;

        File dir = new File(getTestFilePath(orgBench, "smallDrivers"));
        if (!dir.exists()) {
            boolean created = dir.mkdir();
            assert created;
        }

        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (Test test : tests) {
            String path = test.getPath();

            Benchmark bench = orgBench.withPathsToTest(Collections.singletonList(path));

            int count = counter++;
            pool.submit(() -> {
                try {
                    System.out.println("Creating small driver for: " + path + "  " + (count + 1) + "/" + tests.size());

                    List<Test> specificTests = new TestCreator(nativeTypes, typeToTest, bench, typeParameterIndexer, typeNames, typeParameterIndexer).createTests();
                    specificTests.add(new LoadModuleTest(Main.getRequirePath(bench), typeToTest));

                    Statement program = new TestProgramBuilder(bench, nativeTypes, typeNames, specificTests, typeToTest, typeParameterIndexer).buildTestProgram(null);

                    String filePath = getTestFilePath(bench, "smallDrivers/small_driver_" + count + ".js");

                    Util.writeFile(filePath, AstToStringVisitor.toString(program));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.MINUTES);

    }

    public static String generateFullDriver(Benchmark bench) throws IOException {
        return generateFullDriver(bench, null);
    }

    public static String generateFullDriver(Benchmark bench, ExecutionRecording recording) throws IOException {
        SpecReader spec = ParseDeclaration.getTypeSpecification(bench.environment, Collections.singletonList(bench.dTSFile));

        SpecReader emptySpec = ParseDeclaration.getTypeSpecification(bench.environment, new ArrayList<>());

        Set<Type> nativeTypes = TypesUtil.collectNativeTypes(spec, emptySpec);

        Map<Type, String> typeNames = ParseDeclaration.getTypeNamesMap(spec);

        Type typeToTest = ((InterfaceType) spec.getGlobal()).getDeclaredProperties().get(bench.module);

        TypeParameterIndexer typeParameterIndexer = new TypeParameterIndexer();

        List<Test> tests = new TestCreator(nativeTypes, typeToTest, bench, typeParameterIndexer, typeNames, typeParameterIndexer).createTests();
        tests.add(new LoadModuleTest(Main.getRequirePath(bench), typeToTest));

        Statement program = new TestProgramBuilder(bench, nativeTypes, typeNames, tests, typeToTest, typeParameterIndexer).buildTestProgram(recording);

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

        int lastIndex = jsPath.lastIndexOf('\\');
        String jsFile = jsPath.substring(lastIndex + 1, jsPath.length());

        return "./" + jsFile;
    }

    public static String runFullDriver(Benchmark bench) throws IOException {
        String path = getTestFilePath(bench, TEST_FILE_NAME);

        return Util.runNodeScript(path);
    }

}
