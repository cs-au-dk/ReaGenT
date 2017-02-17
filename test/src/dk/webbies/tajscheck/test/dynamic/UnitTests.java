package dk.webbies.tajscheck.test.dynamic;

import dk.au.cs.casa.typescript.SpecReader;
import dk.webbies.tajscheck.ExecutionRecording;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.CheckOptions;
import dk.webbies.tajscheck.benchmark.TypeParameterIndexer;
import dk.webbies.tajscheck.buildprogram.TestProgramBuilder;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.OutputParser.*;
import static dk.webbies.tajscheck.benchmark.Benchmark.RUN_METHOD.BOOTSTRAP;
import static dk.webbies.tajscheck.benchmark.Benchmark.RUN_METHOD.BROWSER;
import static dk.webbies.tajscheck.benchmark.Benchmark.RUN_METHOD.NODE;
import static dk.webbies.tajscheck.test.dynamic.UnitTests.ParseResultTester.ExpectType.JSON;
import static dk.webbies.tajscheck.test.dynamic.UnitTests.ParseResultTester.ExpectType.STRING;
import static dk.webbies.tajscheck.test.dynamic.UnitTests.ParseResultTester.ExpectType.TYPEOF;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

/**
 * Created by erik1 on 23-11-2016.
 */
public class UnitTests {
    private SpecReader parseDeclaration(String folderName) {
        Benchmark bench = benchFromFolder(folderName);

        // Only testing that i can parse it, without getting exceptions.
        return ParseDeclaration.getTypeSpecification(bench.environment, Collections.singletonList(bench.dTSFile));
    }

    public static Benchmark benchFromFolder(String folderName) {
        return benchFromFolder(folderName, CheckOptions.defaultOptions());
    }

    private static Benchmark benchFromFolder(String folderName, CheckOptions options) {
        return new Benchmark(ParseDeclaration.Environment.ES5Core, "test/unit/" + folderName + "/implementation.js", "test/unit/" + folderName + "/declaration.d.ts", "module", Benchmark.RUN_METHOD.NODE, options);
    }

    private String runDriver(String folderName, String seed) throws Exception {
        Benchmark bench = benchFromFolder(folderName);

        return runDriver(bench, seed);
    }

    private String runDriver(String folderName, CheckOptions options, String seed) throws Exception {
        Benchmark bench = benchFromFolder(folderName, options);

        return runDriver(bench, seed);
    }

    private String runDriver(Benchmark bench, String seed) throws Exception {
        sanityCheck(bench);

        Main.writeFullDriver(bench, new ExecutionRecording(null, seed));

        String result = Main.runBenchmark(bench);

        System.out.println("Result of running driver: ");
        System.out.println(result);
        return result;
    }

    private static void sanityCheck(Benchmark bench) throws Exception {
        sanityCheck(bench, NODE);
    }

    private static void sanityCheck(Benchmark bench, Benchmark.RUN_METHOD runMethod) throws Exception {
        bench = bench.withOptions(bench.options.getBuilder().setConstructAllTypes(true).build());

        // Performing a soundness check of the benchmark.
        Main.writeFullDriver(bench.withRunMethod(Benchmark.RUN_METHOD.BOOTSTRAP));
        String output = Main.runBenchmark(bench.withRunMethod(runMethod));
        RunResult result = OutputParser.parseDriverResult(output);

        if (result.errors.size() > 0) {
            System.out.println("--- ERRORS ---");
            for (String error : result.errors) {
                System.out.println(error);
            }
        }

        if (!result.typeErrors.isEmpty()) {
            System.out.println(output);
        }

        for (TypeError typeError : result.typeErrors) {
            System.out.println(typeError);
        }

        assertThat(result.typeErrors.size(), is(0));
    }

    private static ParseResultTester expect(RunResult result) {
        assertThat(result.errors.size(), is(0));

        return new ParseResultTester(result.typeErrors);
    }

    static final class ParseResultTester {
        private List<TypeError> results;

        private ParseResultTester(List<TypeError> result) {
            this.results = result;
        }

        ParseResultTester forPath(String path) {
            return forPath(Collections.singletonList(containsString(path)));
        }

        ParseResultTester forPath(Matcher<String> path) {
            return forPath(Collections.singletonList(path));
        }

        ParseResultTester forPath(String... paths) {
            return forPath(Arrays.stream(paths).map(CoreMatchers::containsString).collect(Collectors.toList()));
        }

        ParseResultTester forPath(List<Matcher<String>> paths) {
            results = results.stream().filter(candidate -> paths.stream().anyMatch(matcher -> matcher.matches(candidate.path))).collect(Collectors.toList());

            StringBuilder path = new StringBuilder();

            Iterator<Matcher<String>> pathsIterator = paths.iterator();
            while (pathsIterator.hasNext()) {
                path.append(pathsIterator.next());
                if (pathsIterator.hasNext()) {
                    path.append(", ");
                }
            }

            assertThat("expected something on path: " + paths, results.size(),is(not(equalTo(0))));
            return this;
        }

        private ParseResultTester got(ExpectType type, String str) {
            return got(type, is(str));
        }

        private ParseResultTester got(ExpectType type, Matcher<String> matcher) {
            for (TypeError result : results) {
                if (type == ExpectType.JSON) {
                    assertThat(result.JSON, matcher);
                } else if (type == ExpectType.STRING) {
                    assertThat(result.toString, matcher);
                } else if (type == TYPEOF) {
                    assertThat(result.typeof, matcher);
                } else {
                    throw new RuntimeException(type.toString());
                }
            }
            return this;
        }

        public ParseResultTester expected(String type) {
            return expected(is(type));
        }

        public ParseResultTester expected(Matcher<String> type) {
            for (TypeError result : results) {
                assertThat(result.expected, is(type));
            }

            return this;
        }

        enum ExpectType {
            TYPEOF,
            STRING,
            JSON
        }
    }

    private RunResult run(String name, String seed) throws Exception {
        return parseDriverResult(runDriver(name, seed));
    }

    private RunResult run(Benchmark benchmark, String seed) throws Exception {
        return parseDriverResult(runDriver(benchmark, seed));
    }

    private RunResult run(String name, CheckOptions options, String seed) throws Exception {
        return parseDriverResult(runDriver(name, options, seed));
    }

    @Test
    public void testMissingProperty() throws Exception {
        RunResult result = OutputParser.parseDriverResult(runDriver("missingProperty", "mySeed"));

        expect(result)
                .forPath("module.foo.missing")
                .got(TYPEOF, is("undefined"));
    }

    @Test
    public void wrongSimpleType() throws Exception {
        RunResult result = run("wrongSimpleType", "aSeed");

        assertThat(result.typeErrors.size(), is(1));

        expect(result)
                .forPath("module.foo.bar")
                .expected("boolean")
                .got(TYPEOF, is("string"))
                .got(STRING, is("value"))
                .got(JSON, is("\"value\""));
    }

    @Test
    public void everyThingGoesRight() throws Exception {
        RunResult result = run("everythingIsRight", "aSeed");

        assertThat(result.typeErrors.size(), is(0));
    }

    @Test
    public void simpleFunctionArg() throws Exception {
        RunResult result = run("simpleFunctionArg", "someSeed");

        expect(result)
                .forPath("module.foo.[arg0].[arg0].<>.value")
                .expected("string")
                .got(TYPEOF, is("number"));
    }

    @Test
    public void complexUnion() throws Exception {
        RunResult result = run("complexUnion", "foo");

        expect(result)
                .forPath("module.foo().[union2]()")
                .expected("boolean")
                .got(TYPEOF, is("string"));

    }

    @Test
    public void optionalParameters() throws Exception {
        RunResult result = run("optionalParameters", "foo");

        expect(result)
                .forPath("module.foo(boolean, undefined, undefined)", "module.foo(boolean, string, undefined)")
                .expected("number")
                .got(TYPEOF, "undefined");
    }

    @Test
    public void simpleOverloads() throws Exception {
        RunResult result = run("simpleOverloads", "foo");

        assertThat(result.typeErrors.size(), is(0));
    }

    @Test
    public void genericClass() throws Exception {
        RunResult result = run("genericClass", "mySeed");

        assertThat(result.typeErrors.size(), is(1));

        expect(result)
                .forPath("module.Container.create().<>.value", "module.Container.new().value")
                .expected(startsWith("a generic type marker"))
                .got(JSON, is("\"foo\""));
    }

    @Test
    public void generics() throws Exception {
        RunResult result = run("generics", "someSeed");

        expect(result)
                .forPath("module.foo().<>.value.foo")
                .expected("string")
                .got(JSON, is("123"));
    }

    @Test
    public void genericClass2() throws Exception {
        RunResult result = run("genericClass2", "mySeed");

        expect(result)
                .forPath("module.Index.new().store.<>.value")
                .expected("string")
                .got(JSON, "123");
    }

    @Test
    public void tuple() throws Exception {
        RunResult result = run("tuple", "seed");

        expect(result)
                .forPath("module.foo().<>.2")
                .expected("3.0")
                .got(TYPEOF, "string")
                .got(STRING, "3");
    }

    @Test
    public void tupleLength() throws Exception {
        RunResult result = run("tupleLength", "seed");

        expect(result)
                .forPath("module.foo()")
                .expected("tuple of 3 elements")
                .got(STRING, "1,2,3,4");
    }

    @Test
    public void never() throws Exception {
        RunResult result = run("never", "seed");

        expect(result)
                .forPath("module.foo()")
                .expected("never")
                .got(STRING, "1");

    }

    @Test
    public void thisTypes() throws Exception {
        RunResult result = run("thisTypes", "seed");

        expect(result)
                .forPath("module.Bar.new().bar")
                .expected("function")
                .got(TYPEOF, "undefined");
    }

    @Test
    public void symbols() throws Exception {
        RunResult result = run("symbol", "seed");

        assertThat(result.typeErrors.size(), is(equalTo(1)));

        expect(result)
                .forPath("module.bar()")
                .expected("symbol")
                .got(TYPEOF, "string");
    }

    @Test
    public void constructClass() throws Exception {
        RunResult result = run("constructClass", CheckOptions.builder().setConstructAllTypes(true).build(), "seed");

        expect(result)
                .forPath("module.foo(class)")
                .expected("\"foo\"")
                .got(STRING, "fooBar");
    }

    @Test
    public void arrayType() throws Exception {
        RunResult result = run(benchFromFolder("arrayType", CheckOptions.builder().setCheckDepth(2).build()), "foo");

        expect(result)
                .forPath("module.foo()")
                .expected("(arrayIndex: number)")
                .got(JSON, "[1,2,3,\"4\"]");
    }

    @Test
    public void arrayTypeCorrect() throws Exception {
        RunResult result = run("correctArrayType", "bar");

        assertThat(result.typeErrors.size() + result.errors.size(), is(0));

    }

    @Test
    public void numberIndexer() throws Exception {
        RunResult result = run("numberIndexer", "foo");

        assertThat(result.typeErrors.size(), is(1));

        expect(result)
                .forPath("module.foo().[numberIndexer]")
                .expected("number")
                .got(TYPEOF, "string");
    }

    @Test
    public void deepNumberIndexer() throws Exception {
        CheckOptions options = CheckOptions.builder()
                .setCheckDepth(1)
                .build();

        RunResult result = run("numberIndexer", options, "foo");

        assertThat(result.typeErrors.size(), is(1));

        expect(result)
                .forPath("module.foo()")
                .expected("(numberIndexer: number)")
                .got(JSON, "{\"1\":1,\"3\":4,\"7\":1,\"10\":\"blah\"}");
    }

    @Test
    public void stringIndexer() throws Exception {
        RunResult result = run("stringIndexer", "foo");

        assertThat(result.typeErrors.size(), is(1));

        expect(result)
                .forPath("module.foo().[stringIndexer]")
                .expected("number")
                .got(TYPEOF, "string");
    }

    @Test
    public void createNumberIndexer() throws Exception {
        RunResult result = run("createNumberIndexer", "bar");

        expect(result)
                .forPath("module.foo().[numberIndexer]")
                .expected("number")
                .got(TYPEOF, "string");
    }

    @Test
    public void createStringIndexer() throws Exception {
        RunResult result = run("createStringIndexer", "bar");

        expect(result)
                .forPath("module.foo().[stringIndexer]")
                .expected("number")
                .got(TYPEOF, "string");
    }

    @Test
    public void simpleClass() throws Exception {
        run("simpleClass", "foo"); // Just pass the sanity check.
    }

    @Test
    public void keyOf() throws Exception {
        RunResult result = run("keyOf", "foo");

        assertThat(result.typeErrors.size(), is(1));

        expect(result)
                .forPath("module.foo()")
                .expected("(\"name\" or \"age\" or \"location\")")
                .got(STRING, "notAProp");
    }

    @Test
    public void indexedAccess() throws Exception {
        RunResult result = run("indexedAccess", "foo");

        assertThat(result.typeErrors.size(), is(1));

        expect(result)
                .forPath("module.foo()")
                .expected("(string or number)")
                .got(TYPEOF, "boolean");
    }

    @Test
    public void genericIndexedAccess() throws Exception {
        SpecReader spec = parseDeclaration("genericIndexedAccess");
        assertThat(spec, is(notNullValue()));
    }

    @Test
    public void mappedTypes() throws Exception {
        SpecReader spec = parseDeclaration("mappedTypes");
        assertThat(spec, is(notNullValue()));
    }

    @Test
    public void differentSizeOverloads() throws Exception {
        RunResult result = run("differentSizeOverloads", "foo");

        assertThat(result.typeErrors.size(), is(0));
        assertThat(result.errors.size(), is(0));
    }

    @Test
    public void complexOverloads() throws Exception {
        RunResult result = run("complexOverloads", "foo");

        assertThat(result.typeErrors.size(), is(0));
        assertThat(result.errors.size(), is(0));
    }

    @Test
    public void overloadsWithOptionalParameters() throws Exception {
        RunResult result = run("overloadsWithOptionalParameters", "foo");

        assertThat(result.typeErrors.size(), is(0));
        assertThat(result.errors.size(), is(0));
    }

    @Test
    public void deepUnions() throws Exception {
        CheckOptions options = CheckOptions.builder()
                .setCheckDepthForUnions(2)
                .build();

        RunResult result = run("deepUnion", options, "foo");

        expect(result)
                .forPath("module.foo()")
                .expected("(((function or object) and field[foo]:(((function or object) and field[bar]:(boolean)))) or ((function or object) and field[foo]:(((function or object) and field[bar]:(string)))))")
                .got(JSON, "{\"foo\":{\"bar\":123}}");
    }

    @Test
    public void typeInArray() throws Exception {
        RunResult result = run("typeInArray", "foo");

        expect(result)
                .forPath("module.foo().<>.[numberIndexer].bar.baz")
                .expected("true")
                .got(STRING, "false");
    }

    @Test
    public void genRestArgs() throws Exception {
        RunResult result = run("genRestArgs", "foo");

        expect(result)
                .forPath("Foo.[restArgs]")
                .expected("valid rest-args")
                .got(STRING, "string,1,4,7,false");
    }

    @Test
    public void genRestArgsWithOverloads() throws Exception {
        RunResult result = run("genRestArgsWithOverloads", "foo");

        assertThat(result.typeErrors.size(), is(1));

        expect(result)
                .forPath("Foo")
                .expected("A valid overload")
                .got(STRING, "string,1,4,7,false");
    }

    @Test
    public void testRestArgs() throws Exception {
        RunResult result = run("testRestArgs", "foo");

        assertThat(result.typeErrors.size(), is(0));

    }

    @Test
    public void propertyWithUnderscore() throws Exception {
        RunResult result = run("propertyWithUnderscore", "foo");

        assertThat(result.typeErrors.size(), is(0));
    }

    @Test
    public void myFixedMomentHasNoError() throws Exception {
        Benchmark benchmark = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/benchmarks/fixedMoment/moment.js", "test/benchmarks/fixedMoment/moment.d.ts", "moment", NODE, CheckOptions.builder().setSplitUnions(false).build());

        RunResult result = run(benchmark, null);

        assertThat(result.typeErrors.size(), is(0));
    }

    @Test
    public void testIfDriverIsTooBig() throws Exception {
        String driver = Main.generateFullDriver(benchFromFolder("unnecessaryBigDriver"));

        System.out.println(driver);

        assertThat(driver, not(containsString("\"module.b2World.new().RayCastAll().<>.[numberIndexer].GetDensity()\"")));

    }

    @Test
    public void genericClass3() throws Exception {
        RunResult result = run("genericClass3", "foo");

        expect(result)
                .forPath("module.createNumberContainer().<>.value")
                .expected("number")
                .got(STRING, "a string");
    }

    @Test
    public void genericClass4() throws Exception {
        RunResult result = run("genericClass4", "foo");

        assertThat(result.errors.size(), is(0));

    }

    @Test
    public void complexSanityCheck() throws Exception {
        sanityCheck(benchFromFolder("complexSanityCheck"));

    }

    @Test
    public void complexSanityCheck2() throws Exception {
        sanityCheck(benchFromFolder("complexSanityCheck2"));
    }

    @Test(expected = AssertionError.class)
    public void complexSanityCheck3() throws Exception {
        // The TypeScript type system is unsound, this is a test of that.
        sanityCheck(benchFromFolder("complexSanityCheck3"));
    }

    @Test(expected = AssertionError.class)
    public void complexSanityCheck9() throws Exception {
        // The TypeScript type system is unsound, this is a test of that.
        sanityCheck(benchFromFolder("complexSanityCheck9"));
    }

    @Test
    public void complexSanityCheck10() throws Exception {
        sanityCheck(benchFromFolder("complexSanityCheck10"));
    }

    @Test
    public void complexSanityCheck11() throws Exception {
        sanityCheck(benchFromFolder("complexSanityCheck11"));
    }

    @Test
    public void extendsError() throws Exception {
        sanityCheck(benchFromFolder("extendsError"));
    }

    @Test
    public void extendsEvent() throws Exception {
        sanityCheck(benchFromFolder("extendsEvent"), BROWSER);
    }

    @Test
    public void extendsEvent2() throws Exception {
        sanityCheck(benchFromFolder("extendsEvent2"), BROWSER);
    }

    @Test(expected = AssertionError.class)
    public void overrideNumberOfArguments() throws Exception {
        // Actually just bivariance on the function arguments.
        // When a function (with e.g. 2 parameters) is overridden, with a function that takes 1 parameter.
        // Then the second parameter kind-of gets the bottom type.
        // TypeScript allows this, but it is unsound (just like complexSanityCheck9
        sanityCheck(benchFromFolder("overrideNumberOfArguments", CheckOptions.builder().setConstructAllTypes(false).build()), BROWSER);
    }

    @Test
    public void classesAndNamespaces() throws Exception {
        RunResult result = run("classesAndNamespaces", "foo");

        assertThat(result.typeErrors.size(), is(0));
        assertThat(result.errors.size(), is(0));
    }

    @Test
    public void complexSanityCheck4() throws Exception {
        sanityCheck(benchFromFolder("complexSanityCheck4"));
    }

    @Test
    public void complexSanityCheck5() throws Exception {
        sanityCheck(benchFromFolder("complexSanityCheck5"));
    }

    @Test
    public void complexSanityCheck6() throws Exception {
        sanityCheck(benchFromFolder("complexSanityCheck6"));
    }

    @Test
    public void thisTypes2() throws Exception {
        // This is just a test that it is able to generate an application, without crashing.
        String program = Main.generateFullDriver(benchFromFolder("thisTypes2"));

        assertThat(program, is(not(equalTo(""))));
    }

    @Test
    public void complexThisTypes() throws Exception {
        RunResult result = run("complexThisTypes", "foo");

        assertThat(result.typeErrors.size(), is(greaterThan(0)));
    }

    @Test
    public void thisTypesInInterfaces() throws Exception {
        RunResult result = run("thisTypesInInterfaces", "foo");

        expect(result)
                .forPath("module.baz().bar")
                .expected("(function or object)")
                .got(TYPEOF, "undefined");
    }

    @Test
    public void complexThisTypes2() throws Exception {
        // Actually just a test that i don't get a null-pointer while constructing the sanity-driver.
        sanityCheck(benchFromFolder("complexThisTypes2"));
    }

    @Test
    public void genericsAreOptimized() throws Exception {
        CheckOptions options = CheckOptions.builder().setDisableSizeOptimization(false).build();
        RunResult optimized = run(benchFromFolder("genericsAreOptimized", options), "seed");

        assertThat(optimized.typeErrors.size(), is(1));


        options = CheckOptions.builder().setDisableSizeOptimization(true).build();
        RunResult unOptimzed = run(benchFromFolder("genericsAreOptimized", options), "seed");

        assertThat(unOptimzed.typeErrors.size(), is(2));
    }

    @Test
    public void genericsWithNoOptimization2() throws Exception {
        CheckOptions options = CheckOptions.builder().setDisableSizeOptimization(true).build();
        RunResult result = run(benchFromFolder("genericsWithNoOptimization2", options), "seed");

        assertThat(result.typeErrors.size(), is(greaterThan(0)));
    }


    @Test
    public void genericsWithNoOptimization() throws Exception {
        CheckOptions options = CheckOptions.builder().setDisableSizeOptimization(true).build();
        RunResult result = run(benchFromFolder("genericsWithNoOptimization", options), "seed");

        assertThat(result.typeErrors.size(), is(1));
    }

    @Test
    public void thisTypesInInterfaces2() throws Exception {
        RunResult result = run("thisTypesInInterfaces2", "foo");

        assertThat(result.typeErrors.size(), is(greaterThan(0)));
    }

    @Test
    public void thisTypesInInterfaces3() throws Exception {
        Main.writeFullDriver(benchFromFolder("thisTypesInInterfaces3"));
    }

    @Test
    public void thisTypesAreOptimized() throws Exception {
        RunResult result = run("thisTypesAreOptimized", "foo");

        assertThat(result.typeErrors.size(), is(1));
    }

    @Test
    public void extendsArray() throws Exception {
        RunResult result = run("extendsArray", "foo");

        assertThat(result.typeErrors.size(), is(0));
    }

    @Test
    public void extendsArray2() throws Exception {
        RunResult result = run("extendsArray2", "foo");

        assertThat(result.typeErrors.size(), is(0));
    }

    @Test
    public void staticFieldsInheritedInClass() throws Exception {
        RunResult result = run("staticFieldsInheritedInClass", "foo");

        assertThat(result.typeErrors.size(), is(0)); // It actually contains an error, according to the TypeScript language, it is just an error we choose not to check for.
    }

    @Test
    public void intersectionTypes() throws Exception {
        RunResult result = run("intersectionTypes", "foo");

        expect(result)
                .forPath("module.foo(intersection)")
                .expected("false")
                .got(STRING, "true");
    }

    @Test
    public void complexSanityCheck7() throws Exception {
        sanityCheck(benchFromFolder("complexSanityCheck7"));
    }

    @Test
    public void complexSanityCheck8() throws Exception {
        sanityCheck(benchFromFolder("complexSanityCheck8"));
    }

    @Test
    public void genericsBustStack() throws Exception {
        Main.generateFullDriver(benchFromFolder("genericsBustStack"));
    }

    @Test
    public void genericsBustStack2() throws Exception {
        Main.generateFullDriver(benchFromFolder("genericsBustStack2"));
    }

    @Test
    public void genericsBustStack3() throws Exception {
        Main.generateFullDriver(benchFromFolder("genericsBustStack3").withRunMethod(BOOTSTRAP));
    }

    @Test
    public void genericsBustStack4() throws Exception {
        Main.generateFullDriver(benchFromFolder("genericsBustStack4").withRunMethod(BOOTSTRAP));
    }

    @Test
    public void genericsBustStackRuntime() throws Exception {
        RunResult result = run("genericsBustStackRuntime", "foo");

        assertThat(result.typeErrors.size(), is(greaterThan(0)));
    }

    @Test
    public void intersectionWithFunction() throws Exception {
        RunResult result = run(benchFromFolder("intersectionWithFunction", CheckOptions.builder().setConstructAllTypes(true).build()).withRunMethod(BOOTSTRAP), "foo");

        assertThat(result.typeErrors.size(), is(0));
        assertThat(result.errors, everyItem(is(equalTo("RuntimeError Cannot construct this IntersectionType")))); // <- this happens, it is ok, i cannot at runtime construct a type which is the intersection of two types.
    }

    @Test
    public void extendsArray3() throws Exception {
        RunResult result = run("extendsArray3", "foo");

        assertThat(result.typeErrors.size(), is(equalTo(1)));

        expect(result)
                .forPath("module.bar().<>.[numberIndexer].<>.[numberIndexer]")
                .expected("string")
                .got(TYPEOF, "number");
    }

    @Test
    public void extendsArray4() throws Exception {
        RunResult result = run("extendsArray4", "foo");

        assertThat(result.typeErrors.size(), is(0));
    }

    @Test
    public void unconstrainedGenericsAreNotDuplicated() throws Exception {
        RunResult result = run("unconstrainedGenericsAreNotDuplicated", "foo");

        assertThat(result.typeErrors.size(), is(lessThanOrEqualTo(1)));

    }

    @Test
    public void complexGenerics() throws Exception {
        Main.generateFullDriver(benchFromFolder("complexGenerics")); // Just a test that no null-pointer.
    }

    @Test
    public void wrongSignaturePropagates() throws Exception {
        RunResult result = run("wrongSignaturePropagates", "foo");

        expect(result)
                .forPath("window.module.foo.[arg0].[arg0]")
                .expected("boolean")
                .got(TYPEOF, "undefined");

        assertThat(result.typeErrors.size(), is(1));
    }

    @Test
    public void thisTypesAreOptimized2() throws Exception {
        RunResult result = run("thisTypesAreOptimized2", CheckOptions.builder().setConstructAllTypes(false).build(), "foo");

        assertThat(result.typeErrors.size(), is(1));
    }

    @Test
    public void genericsAreOptimized2() throws Exception {
        String driver = Main.generateFullDriver(benchFromFolder("genericsAreOptimized2"));

        assertThat(driver, not(containsString("module.CatmullRomCurve3.<>.getPoint().setFromSpherical().multiplyVector3Array(any)")));
        assertThat(driver, not(containsString("module.CatmullRomCurve3.<>.getPoint().setFromSpherical().multiplyVector3Array()")));

    }

    @Test
    public void exponentialComplexity() throws Exception {
        Main.writeFullDriver(benchFromFolder("exponentialComplexity"));
    }

    @Test
    public void veryComplexThisType() throws Exception {
        Main.generateFullDriver(benchFromFolder("veryComplexThisType"));
    }

    @Test
    public void genericsAreNotTooOptimized() throws Exception {
        Benchmark bench = benchFromFolder("genericsAreNotTooOptimized", CheckOptions.builder().setCombineAllUnconstrainedGenerics(true).build());
        String driver = Main.generateFullDriver(bench);
        Main.writeFullDriver(bench);

        assertThat(driver, not(containsString(TypeParameterIndexer.IS_UNSTRAINED_GENERIC_MARKER)));
    }

    @Test
    public void classAndClassInstances() throws Exception {
        RunResult result = run("classAndClassInstances", "foo");

        assertThat(result.typeErrors.size(), is(0));
    }

    @Test
    public void typeofParsing() throws Exception {
        String driver = Main.generateFullDriver(benchFromFolder("typeofParsing"));

        assertThat(driver, not(containsString("module.getNewLibraryCopy.prototype")));

    }

    @Test
    public void testClass() throws Exception {
        RunResult result = run("testClass", "foo");

        assertThat(result.typeErrors.size(), is(1));

    }

    @Test
    public void interfacesAndObjectsParsing() throws Exception {
        String driver = Main.generateFullDriver(benchFromFolder("interfacesAndObjectsParsing"));

        Main.writeFullDriver(benchFromFolder("interfacesAndObjectsParsing"));

        assertThat(driver, not(containsString("// path: module.Observable.selectMany")));

    }

    @Test
    public void undefinedOnObject() throws Exception {
        RunResult result = run("undefinedOnObject", "foo");

        assertThat(result.typeErrors.size(), is(0));
    }

    @Test
    public void namespacesAndClassWithNestedClass() throws Exception {
        RunResult result = run("namespacesAndClassWithNestedClass", "foo");

        assertThat(result.typeErrors.size(), is(0));

    }

    @Test
    public void complexGenerics2() throws Exception {
        RunResult result = run("complexGenerics2", "foo");

        assertThat(result.typeErrors.size(), is(0));

    }

    @Test
    public void canFindErrorsEvenWhenTimeout() throws Exception {
        RunResult result = run(benchFromFolder("canFindErrorsEvenWhenTimeout", CheckOptions.builder().setMaxTime(5 * 1000).build()), "foo");

        assertThat(result.typeErrors.size(), is(1));

    }

    @Test
    public void canFindErrorsEvenWhenTimeoutChrome() throws Exception {
        RunResult result = run(benchFromFolder("canFindErrorsEvenWhenTimeoutChrome", CheckOptions.builder().setMaxTime(5 * 1000).build()).withRunMethod(BROWSER), "foo");

        assertThat(result.typeErrors.size(), is(1));

    }

    @Test // This should be stopped by the amount of iterations.
    public void findSimpleErrorChrome() throws Exception {
        long startTime = System.currentTimeMillis();

        RunResult result = run(benchFromFolder("findSimpleErrorChrome", CheckOptions.builder().setMaxTime(60 * 1000).build()).withRunMethod(BROWSER), "foo");

        assertThat(result.typeErrors.size(), is(1));

        long time = System.currentTimeMillis() - startTime;

        System.out.println("Time taken: " + (time / 1000.0) + "s");

        assertThat(time, is(lessThan((long)60 * 1000)));
    }

    @Test // This should be stopped by the amount of iterations.
    public void findSimpleErrorChromeWithErrors() throws Exception {
        long startTime = System.currentTimeMillis();

        RunResult result = run(benchFromFolder("findSimpleErrorChromeWithErrors", CheckOptions.builder().setMaxTime(60 * 1000).build()).withRunMethod(BROWSER), "foo");

        assertThat(result.typeErrors.size(), is(1));

        long time = System.currentTimeMillis() - startTime;

        System.out.println("Time taken: " + (time / 1000.0) + "s");

        assertThat(time, is(lessThan((long)60 * 1000)));
    }
}
