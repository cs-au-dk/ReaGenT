package dk.webbies.tajscheck;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.benchmarks.Benchmarks;
import dk.webbies.tajscheck.buildprogram.TestProgramBuilder;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.paser.AST.Statement;
import dk.webbies.tajscheck.paser.AstToStringVisitor;
import dk.webbies.tajscheck.testcreator.Test.Test;
import dk.webbies.tajscheck.testcreator.Test.LoadModuleTest;
import dk.webbies.tajscheck.testcreator.TestCreator;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.util.*;

/**
 * Created by erik1 on 01-11-2016.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        runForBench(Benchmarks.test);
        runForBench(Benchmarks.moment);
    }

    private static void runForBench(Benchmark bench) throws IOException {
        // TODO: Maybe print as part of the path, how the dependencies where created (function arguments and the like).
        SpecReader spec = ParseDeclaration.getTypeSpecification(bench.environment, Collections.singletonList(bench.dTSFile));

        SpecReader emptySpec = ParseDeclaration.getTypeSpecification(bench.environment, new ArrayList<>());

        Set<Type> nativeTypes = Types.collectNativeTypes(spec, emptySpec);

        Map<Type, String> typeNames = ParseDeclaration.getTypeNamesMap(spec);

        Type typeToTest = ((InterfaceType) spec.getGlobal()).getDeclaredProperties().get(bench.module);

        List<Test> tests = new ArrayList<>(TestCreator.createTests(nativeTypes, typeToTest, bench.module));
        Collections.reverse(tests);
        tests.add(new LoadModuleTest(getRequirePath(bench), typeToTest));

        Statement program = new TestProgramBuilder(bench, tests, nativeTypes, typeNames).buildTestProgram();

        String programString = AstToStringVisitor.toString(program);

        Util.writeFile(getTestFilePath(bench), programString);

        System.out.println(programString);
    }

    private static String getTestFilePath(Benchmark bench) {
        String jsPath = bench.jsFile;
        int lastIndex = jsPath.lastIndexOf('/');

        String folder = jsPath.substring(0, lastIndex + 1);

        return folder + "test.js";
    }

    private static String getRequirePath(Benchmark bench) {
        String jsPath = bench.jsFile;
        int lastIndex = jsPath.lastIndexOf('/');
        String jsFile = jsPath.substring(lastIndex + 1, jsPath.length());

        String jsName = Util.removeSuffix(jsFile, ".js");

        return "./" + jsName;
    }


}
