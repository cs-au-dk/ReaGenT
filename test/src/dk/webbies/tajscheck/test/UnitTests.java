package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.ExecutionRecording;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;
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
    private String runDriver(String folderName, String seed) throws IOException {
        Benchmark bench = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/unit/" + folderName + "/implementation.js", "test/unit/" + folderName + "/declaration.d.ts", "module", Benchmark.LOAD_METHOD.REQUIRE);

        Main.writeFullDriver(bench, new ExecutionRecording(null, seed));

        String result = Main.runFullDriver(bench);

        System.out.println("Result of running driver: ");
        System.out.println(result);
        return result;
    }

    private static ParseResultTester expect(List<TypeError> result) {
        return new ParseResultTester(result);
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

    @Test
    public void testMissingProperty() throws Exception {
        RunResult result = OutputParser.parseDriverResult(runDriver("missingProperty", "mySeed"));

        expect(result)
                .forPath("module.foo.missing")
                .got(TYPEOF, is("undefined"));
    }

    @Test
    public void wrongSimpleType() throws Exception {
        RunResult result = parseDriverResult(runDriver("wrongSimpleType", "aSeed"));

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
        RunResult result = parseDriverResult(runDriver("everythingIsRight", "aSeed"));

        assertThat(result.typeErrors.size(), is(0));
    }

    @Test
    public void simpleFunctionArg() throws Exception {
        RunResult result = parseDriverResult(runDriver("simpleFunctionArg", "someSeed"));

        expect(result)
                .forPath("module.foo.[arg0].[arg0].<>.value")
                .expected("string")
                .got(TYPEOF, is("number"));
    }

    @Test
    public void testComplexUnion() throws Exception {
        RunResult result = parseDriverResult(runDriver("complexUnion", "foo"));

        expect(result)
                .forPath("module.foo().[union2]()")
                .expected("boolean")
                .got(TYPEOF, is("string"));

    }

    @Test
    public void optionalParameters() throws Exception {
        RunResult result = parseDriverResult(runDriver("optionalParameters", "foo"));

        expect(result)
                .forPath("module.foo()")
                .expected("number");
    }

    @Test
    public void simpleOverloads() throws Exception {
        RunResult result = parseDriverResult(runDriver("simpleOverloads", "foo"));

        assertThat(result.typeErrors.size(), is(0));
    }

    @Test
    public void genericClass() throws Exception {
        RunResult result = parseDriverResult(runDriver("genericClass", "mySeed"));

        assertThat(result.typeErrors.size(), is(1));

        expect(result)
                .forPath("module.Container.create().<>.value")
                .expected(startsWith("a generic type marker"))
                .got(JSON, is("\"foo\""));
    }

    @Test
    public void generics() throws Exception {
        RunResult result = parseDriverResult(runDriver("generics", "someSeed"));

        expect(result)
                .forPath("module.foo().<>.value.foo")
                .expected("string")
                .got(JSON, is("123"));
    }

    @Test
    public void genericClass2() throws Exception {
        RunResult result = parseDriverResult(runDriver("genericClass2", "mySeed"));

        expect(result)
                .forPath("module.Index.new().store.<>.value")
                .expected("string")
                .got(JSON, "123");
    }

    @Test
    public void tuple() throws Exception {
        RunResult result = parseDriverResult(runDriver("tuple", "seed"));

        expect(result)
                .forPath("module.foo().<>.2")
                .expected("3.0")
                .got(TYPEOF, "string")
                .got(STRING, "3");
    }

    @Test
    public void tupleLength() throws Exception {
        RunResult result = parseDriverResult(runDriver("tupleLength", "seed"));

        expect(result)
                .forPath("module.foo()")
                .expected("tuple of 3 elements")
                .got(STRING, "1,2,3,4");
    }

    // TODO: This types
    // TODO: Look for other TODO's in ts-spec-reader.
}
