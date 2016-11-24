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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by erik1 on 23-11-2016.
 */
public class UnitTests {
    private String runDriver(String folderName, String seed) throws IOException {
        Benchmark bench = new Benchmark(ParseDeclaration.Environment.ES5Core, "test/unit/" + folderName + "/implementation.js", "test/unit/" + folderName + "/declaration.d.ts", "module", Benchmark.LOAD_METHOD.NODE);

        Main.writeFullDriver(bench, new ExecutionRecording(null, seed));

        String result = Main.runFullDriver(bench);

        System.out.println("Result of running driver: ");
        System.out.println(result);
        return result;
    }

    private static ParseResultTester expect(List<ParseResult> result) {
        return new ParseResultTester(result);
    }

    static final class ParseResultTester {
        private List<ParseResult> results;

        private ParseResultTester(List<ParseResult> result) {
            this.results = result;
        }

        private ParseResultTester forPath(String path) {
            results = results.stream().filter(result -> result.path.equals(path)).collect(Collectors.toList());

            assertThat(results.size(),is(not(equalTo(0))));
            return this;
        }

        private ParseResultTester got(ExpectType type, Matcher<String> matcher) {
            for (ParseResult result : results) {
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
            for (ParseResult result : results) {
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
        List<ParseResult> result = OutputParser.parseDriverResult(runDriver("missingProperty", "mySeed"));

        expect(result)
                .forPath("module.foo.missing")
                .got(TYPEOF, is("undefined"));
    }

    @Test
    public void wrongSimpleType() throws Exception {
        List<ParseResult> result = parseDriverResult(runDriver("wrongSimpleType", "aSeed"));

        assertThat(result.size(), is(1));

        expect(result)
                .forPath("module.foo.bar")
                .expected("boolean")
                .got(TYPEOF, is("string"))
                .got(STRING, is("value"))
                .got(JSON, is("\"value\""));
    }

    @Test
    public void everyThingGoesRight() throws Exception {
        List<ParseResult> result = parseDriverResult(runDriver("everythingIsRight", "aSeed"));

        assertThat(result.size(), is(0));
    }

    @Test
    public void simpleFunctionArg() throws Exception {
        List<ParseResult> result = parseDriverResult(runDriver("simpleFunctionArg", "someSeed"));

        expect(result)
                .forPath("module.foo.[arg0].[arg0].<>.value")
                .expected("string")
                .got(TYPEOF, is("number"));
    }
}
