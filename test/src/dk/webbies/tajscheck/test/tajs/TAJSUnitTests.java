package dk.webbies.tajscheck.test.tajs;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.tajstester.TypeViolation;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.util.Util.mkString;
import static org.hamcrest.CoreMatchers.*;

public class TAJSUnitTests {
    private static TAJSUtil.TajsAnalysisResults run(String folderName) throws Exception {
        return run(benchFromFolder(folderName));
    }

    private static TAJSUtil.TajsAnalysisResults run(Benchmark bench) throws Exception {
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

        private TAJSUnitTests.TAJSResultTester performed(String path) {
            MatcherAssert.assertThat("test for " + path + " hasn't been performed", results.testPerformed.stream().anyMatch(test -> test.getPath().equals(path)));
            return this;
        }

        private TAJSUnitTests.TAJSResultTester performedAllTests() {
            MatcherAssert.assertThat("some tests were not performed: " + mkString(results.testNot, ", "), results.testNot.isEmpty());
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

        TAJSResultTester forPath(String path) {
            MultiMap<String, TypeViolation> detectedViolations = results.detectedViolations.asMap().entrySet().stream().filter(entry -> entry.getKey().startsWith(path)).collect(ArrayListMultiMap.collector());
            List<dk.webbies.tajscheck.testcreator.test.Test> performedTest = results.testPerformed.stream().filter(test -> test.getPath().startsWith(path)).collect(Collectors.toList());
            List<dk.webbies.tajscheck.testcreator.test.Test> testsNot = results.testNot.stream().filter(test -> test.getPath().startsWith(path)).collect(Collectors.toList());

            return new TAJSResultTester(new TAJSUtil.TajsAnalysisResults(detectedViolations, performedTest, testsNot));
        }
    }

    private TAJSUnitTests.TAJSResultTester expect(TAJSUtil.TajsAnalysisResults result) {
        return new TAJSResultTester(result);
    }

    @Test
    public void unionMightFail() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("unionMightFail");

        expect(result)
                .performed("module.foo()");

        expect(result)
                .forPath("module.foo()")
                .hasViolations();
    }

    @Test
    @Ignore // TODO: When we start supporting callbacks.
    public void callbacks() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("callbacks");

        expect(result)
                .performed("module.foo()");

        expect(result)
                .forPath("module.foo()")
                .hasViolations();
    }

    @Test(timeout = 30 * 1000)
    @Ignore // TODO: Calling "pv.readPropertyValue" with a getter property, seems to go wrong (it seems to mix in the base object).
    public void getter() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("getter");

        expect(result)
                .hasNoViolations();
    }

    @Test(timeout = 30 * 1000)
    @Ignore // TODO: Goes in an infinite loop.
    public void getterInfiniteLoop() throws Exception {
        run("getterInfiniteLoop"); // smoke test, is good if it ever terminates.
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
                .forPath("module.foo()");
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

    @Test
    public void multipleFunctions() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("multipleFunctions");

        expect(result)
                .hasNoViolations();
    }

    @Test
    public void numbers() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("numbers");

        expect(result)
                .performed("module.id(number)")
                .hasNoViolations();
    }

    @Test
    public void strings() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("strings");

        expect(result)
                .performed("module.id(string)")
                .hasNoViolations();
    }

    @Test
    public void booleans() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("booleans");

        expect(result)
                .performed("module.id(boolean)")
                .hasNoViolations();
    }

    @Test
    public void numberLit() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("numberLit");

        expect(result)
                .performed("module.id(3)")
                .hasNoViolations();
    }

    @Test
    public void stringLit() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("stringLit");

        expect(result)
                .performed("module.id(\"str\")")
                .hasNoViolations();
    }

    @Test
    public void boolLit() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("boolLit");

        expect(result)
                .performed("module.id(true)")
                .hasNoViolations();
    }

    @Test
    public void primitivesFail() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("primitivesFail");

        expect(result)
                .performed("module.id(number)")
                .hasViolations();
    }

    @Test
    public void samePathFunctions() throws Exception {
        expect(run(benchFromFolder("samePathFunctions").withOptions(options -> options.setSplitUnions(false))))
                .hasNoViolations();
    }

    @Test
    public void function() throws Exception {
        expect(run("function"))
                .performedAllTests()
                .forPath("foo()")
                .hasViolations();
    }

    @Test
    public void constructor() throws Exception {
        expect(run("constructor"))
                .performedAllTests()
                .forPath("construct.new()")
                .hasViolations();
    }

    @Test
    public void nestedFunctions() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("nestedFunctions");
        expect(result)
                .performedAllTests();

        expect(result)
                .forPath("module.foo().foo")
                .hasViolations();

        expect(result)
                .forPath("module.foo().bar")
                .hasNoViolations();
    }
}


