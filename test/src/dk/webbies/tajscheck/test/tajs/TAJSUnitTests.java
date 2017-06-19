package dk.webbies.tajscheck.test.tajs;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.CheckOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

import static dk.webbies.tajscheck.util.Util.mkString;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class TAJSUnitTests {
    private static TAJSUtil.TajsAnalysisResults run(String folderName) throws Exception {
        Benchmark bench = benchFromFolder(folderName);

        return TAJSUtil.runNoDriver(bench, 60);
    }

    private static Benchmark benchFromFolder(String folderName) {
        CheckOptions options = CheckOptions.builder().setCheckDepthReport(0).setCheckDepthUseValue(0).build();
        return new Benchmark("tajsunit-" + folderName, ParseDeclaration.Environment.ES5Core, "test/tajsUnit/" + folderName + "/implementation.js", "test/tajsUnit/" + folderName + "/declaration.d.ts", Benchmark.RUN_METHOD.NODE, options);
    }

    @SuppressWarnings("UnusedReturnValue")
    private class TAJSResultTester {
        private TAJSUtil.TajsAnalysisResults results;

        private TAJSResultTester(TAJSUtil.TajsAnalysisResults result) {
            this.results = result;
        }

        private TAJSUnitTests.TAJSResultTester toPaths(String path) {
            results = results.with(results.detectedViolations.asMap().entrySet().stream().filter(entry -> entry.getKey().equals(path)).collect(ArrayListMultiMap.collector()));
            MatcherAssert.assertThat("expected some violation on path: " + path, results.detectedViolations.size(), is(not(equalTo(0))));
            return this;
        }

        private TAJSUnitTests.TAJSResultTester performed(String path) {
            MatcherAssert.assertThat("test for " + path + " hasn't been performed", results.testPerformed.stream().anyMatch(test -> test.getPath().equals(path)));
            return this;
        }

        private TAJSUnitTests.TAJSResultTester performedAllTests() {
            MatcherAssert.assertThat("some tests were not performed: " + mkString(results.testNot.stream(), ", "), results.testNot.isEmpty());
            return this;
        }

        TAJSUnitTests.TAJSResultTester hasNoViolations() {
            MatcherAssert.assertThat("there are no violations", results.detectedViolations.size() == 0);
            return this;
        }

        TAJSUnitTests.TAJSResultTester hasViolations() {
            MatcherAssert.assertThat("there are violations", results.detectedViolations.size() != 0);
            return this;
        }
    }

    private TAJSUnitTests.TAJSResultTester expect(TAJSUtil.TajsAnalysisResults result) {
        return new TAJSResultTester(result);
    }

    // TODO: Test for all kinds of primitives. Incl as arguments.
    // TODO: Test that optional arguments are completely "resolved", as in a function is never called with "undefined".

    @Test
    @Ignore // TODO: Maybe for later.
    public void unionMightFail() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("unionMightFail");

        expect(result)
                .performedAllTests()
                .performed("module.foo()");

        expect(result)
                .toPaths("module.foo()")
                .hasViolations();
    }

    @Test
    public void getter() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("getter");

        expect(result)
                .performed("module.foo()");
    }

    @Test
    public void simpleUnion() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("simpleUnion");

        expect(result)
                .performed("module.foo()");
    }

    @Test
    public void primitiveOrObject() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("primitiveOrObject");

        expect(result)
                .performed("module.foo()");
    }

    @Test
    public void objectWithNumberProps() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("objectWithNumberProps");

        expect(result)
                .performed("module.foo()");

        expect(result)
                .performed("module.bar()");
    }

    @Test
    public void everythingIsRight() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("everythingIsRight");

        expect(result).hasNoViolations();

        expect(result)
                .performed("module.foo.bar");

        expect(result)
                .performed("module.foo");

        expect(result)
                .performed("module.foo.foo");

        expect(result)
                .performed("module");

    }

    @Test
    public void recursiveObject() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("recursiveObject");

        expect(result)
                .performed("module.foo.rec");
    }

    @Test
    public void functionAndObject() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("functionAndObject");

        expect(result)
                .performed("module.foo.foo");
    }

    /**
     * This test exposes how a sound static analysis can produce an unsound result.
     * Because what the analysis does, is say, if this code is reachable, then the following is true.
     * But if that code isn't reachable,
     */
    @Test
    @Ignore // TODO: maybe for later.
    public void baitingTajsUnion() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("baitingTajsUnion");

        expect(result)
                .performed("module.foo().[union0].bar.baz");

        expect(result)
                .performed("module.foo().[union1].bar.baz");
    }

    @Test
    @Ignore // TODO: Maybe for later.
    public void spuriousUnion() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("spuriousUnion");

        expect(result)
                .toPaths("module.foo()");
                /*FIXME: .expected("maybe string")
                .toFail();*/
    }

    @Test
    @Ignore // TODO: maybe for later.
    public void spuriousOverload() throws Exception {
        // I wanted to make a more complicated test, but since TAJS cannot see that (typeof [bool/number] !== "string"), it has to be quite simple.
        TAJSUtil.TajsAnalysisResults result = run("spuriousOverload");

        expect(result)
                .performed("Foo");
                /*FIXME:.expected("overload (a: string) to be called")
                .toFail();*/

    }

    @Test
    @Ignore // TODO: Maybe for layer.
    public void splitSignatures() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("splitSignatures");

        expect(result)
                .performed("Foo");
                /*FIXME:.expected("overload (a: string) to be called")
                .toFail();*/
    }

}


