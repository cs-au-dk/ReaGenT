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

/**
 * Created by erik1 on 01-11-2016.
 */
public class Main {
    private static final boolean TO_WSL_PATHS = true;

	// TODO: Try to make it so only "executeable" tests are attempted. So that ALL checkDependencies calls are successful.
    // TODO; Make sure no constructed object is tested
    // TODO: Multi-level asserts?
    public static void main(String[] args) throws IOException {
//        generateTestProgram(Benchmarks.test);

        generateTestProgram(Benchmarks.moment);

        /*createRecordedProgram(Benchmarks.moment, new ExecutionRecording(
                new int[]{92,129,7,266,291,59,355,433,35,216,157,163,212,422,155,118,271,148,23,176,234,240,6,268,154,321,145,391,59,386,357,357,386,138,244,197,388,195,155,148,352,379,394,151,328,405,391,322,343},
                "0.36510822415053457"
        ));*/

//        generateTestProgram(Benchmarks.async);
//        generateTestProgram(Benchmarks.underscore);
    }

    private static void generateTestProgram(Benchmark bench) throws IOException {
        String programString = generateProgramForBench(bench, null);

        Util.writeFile(getTestFilePath(bench, "test.js"), programString);

        System.out.println(programString);
    }

    private static String createRecordedProgram(Benchmark bench, ExecutionRecording recording) throws IOException {
        String programString = generateProgramForBench(bench, recording);

        Util.writeFile(getTestFilePath(bench, "recorded.js"), programString);

        System.out.println(programString);

        return programString;
    }

    private static String generateProgramForBench(Benchmark bench, ExecutionRecording recording) throws IOException {
        SpecReader spec = ParseDeclaration.getTypeSpecification(bench.environment, Collections.singletonList(bench.dTSFile));

        SpecReader emptySpec = ParseDeclaration.getTypeSpecification(bench.environment, new ArrayList<>());

        Set<Type> nativeTypes = TypesUtil.collectNativeTypes(spec, emptySpec);

        Map<Type, String> typeNames = ParseDeclaration.getTypeNamesMap(spec);

        Type typeToTest = ((InterfaceType) spec.getGlobal()).getDeclaredProperties().get(bench.module);

        List<Test> tests = TestCreator.createTests(nativeTypes, typeToTest, bench.module);
        tests.add(new LoadModuleTest(Main.getRequirePath(bench), typeToTest));

        Statement program = new TestProgramBuilder(bench, nativeTypes, typeNames, tests).buildTestProgram(recording);

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
        int lastIndex = jsPath.lastIndexOf('/');
        String jsFile = jsPath.substring(lastIndex + 1, jsPath.length());

        String jsName = Util.removeSuffix(jsFile, ".js");

        if (TO_WSL_PATHS) {
            jsName = jsName.replace("C:\\", "/mnt/c/").replace("\\", "/");
        } else {
            jsName = jsName.replace("\\", "\\\\");
        }

        return jsName + ".js";
    }

}
