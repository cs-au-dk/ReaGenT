package dk.webbies.tajscheck.test;

import dk.au.cs.casa.typescript.SpecReader;
import dk.webbies.tajscheck.ExecutionRecording;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.benchmarks.CheckOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.OutputParser.*;
import static dk.webbies.tajscheck.test.UnitTests.ParseResultTester.ExpectType.JSON;
import static dk.webbies.tajscheck.test.UnitTests.ParseResultTester.ExpectType.STRING;
import static dk.webbies.tajscheck.test.UnitTests.ParseResultTester.ExpectType.TYPEOF;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by erik1 on 23-11-2016.
 */
public class UnitTests {
    private SpecReader parseDeclaration(String folderName) {
        Benchmark bench = benchFromFolder(folderName);

        // Only testing that i can parse it, without getting exceptions.
        return ParseDeclaration.getTypeSpecification(bench.environment, Collections.singletonList(bench.dTSFile));
    }

    static Benchmark benchFromFolder(String folderName) {
        return benchFromFolder(folderName, CheckOptions.defaultOptions());
    }

    static Benchmark benchFromFolder(String folderName, CheckOptions options) {
        return new Benchmark(ParseDeclaration.Environment.ES5Core, "test/unit/" + folderName + "/implementation.js", "test/unit/" + folderName + "/declaration.d.ts", "module", Benchmark.LOAD_METHOD.REQUIRE, options);
    }

    private String runDriver(String folderName, String seed) throws Exception {
        Benchmark bench = benchFromFolder(folderName);

        return runDriver(bench, seed);
    }

    private String runDriver(Benchmark bench, String seed) throws Exception {
        sanityCheck(bench);

        Main.writeFullDriver(bench, new ExecutionRecording(null, seed));

        String result = Main.runFullDriver(bench);

        System.out.println("Result of running driver: ");
        System.out.println(result);
        return result;
    }

    static void sanityCheck(Benchmark bench) throws Exception {
        // Performing a soundness check of the benchmark.
        Main.writeFullDriver(bench.withLoadMethod(Benchmark.LOAD_METHOD.BOOTSTRAP));
        String output = Main.runFullDriver(bench);
        RunResult result = OutputParser.parseDriverResult(output);

        if (!result.typeErrors.isEmpty()) {
            System.out.println(output);
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

        private ParseResultTester forPath(String path) {
            results = results.stream().filter(result -> result.path.equals(path)).collect(Collectors.toList());

            assertThat("expected something on path: " + path, results.size(),is(not(equalTo(0))));
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
                .forPath("module.foo(boolean, union, union)")
                .expected("number");
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
                .forPath("module.Container.create().<>.value")
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
        RunResult result = run("constructClass", "seed");

        expect(result)
                .forPath("module.foo(obj)")
                .expected("\"foo\"")
                .got(STRING, "fooBar");
    }

    @Test
    public void arrayType() throws Exception {
        RunResult result = run("arrayType", "foo");

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

        RunResult result = parseDriverResult(runDriver(benchFromFolder("deepUnion", options), "foo"));

        expect(result)
                .forPath("module.foo()")
                .expected("(((function or object) and field[foo]:(((function or object) and field[bar]:(boolean)))) or ((function or object) and field[foo]:(((function or object) and field[bar]:(string)))))")
                .got(JSON, "{\"foo\":{\"bar\":123}}");
    }

}
