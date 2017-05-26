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

public class TAJSNoDriverUnitTests {
    private static class SimpleEntry<K, T> implements Map.Entry<K, T> {
        private final K key;
        private T value;

        private SimpleEntry(K key, T value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public T setValue(T value) {
            T old = this.value;
            this.value = value;
            return old;
        }
    }

    static TAJSUtil.TajsAnalysisResults runNoDriver(String folderName) throws Exception {
        Benchmark bench = benchFromFolder(folderName);

        return TAJSUtil.runNoDriver(bench, 60);
    }

    private static Benchmark benchFromFolder(String folderName) {
        CheckOptions options = CheckOptions.builder().setCheckDepthReport(0).setCheckDepthUseValue(0).build();
        return new Benchmark("tajsunit-" + folderName, ParseDeclaration.Environment.ES5Core, "test/tajsUnit/" + folderName + "/implementation.js", "test/tajsUnit/" + folderName + "/declaration.d.ts", Benchmark.RUN_METHOD.NODE, options).useTAJS();
    }

    @SuppressWarnings("UnusedReturnValue")
    private class TAJSResultTester {
        private TAJSUtil.TajsAnalysisResults results;

        private TAJSResultTester(TAJSUtil.TajsAnalysisResults result) {
            this.results = result;
        }

        private dk.webbies.tajscheck.test.tajs.TAJSNoDriverUnitTests.TAJSResultTester toPaths(String path) {
            results = results.with(results.detectedViolations.asMap().entrySet().stream().filter(entry -> entry.getKey().equals(path)).collect(ArrayListMultiMap.collector()));
            MatcherAssert.assertThat("expected some violation on path: " + path, results.detectedViolations.size(), is(not(equalTo(0))));
            return this;
        }

        private dk.webbies.tajscheck.test.tajs.TAJSNoDriverUnitTests.TAJSResultTester performed(String path) {
            MatcherAssert.assertThat("test for " + path + " hasn't been performed", results.testPerformed.stream().anyMatch(test -> test.getPath().equals(path)));
            return this;
        }

        private dk.webbies.tajscheck.test.tajs.TAJSNoDriverUnitTests.TAJSResultTester performedAllTests() {
            MatcherAssert.assertThat("some tests were not performed: " + mkString(results.testNot.stream(), ", "), results.testNot.isEmpty());
            return this;
        }

        dk.webbies.tajscheck.test.tajs.TAJSNoDriverUnitTests.TAJSResultTester hasNoViolations() {
            MatcherAssert.assertThat("there are no violations", results.detectedViolations.size() == 0);
            return this;
        }

        dk.webbies.tajscheck.test.tajs.TAJSNoDriverUnitTests.TAJSResultTester hasViolations() {
            MatcherAssert.assertThat("there are violations", results.detectedViolations.size() != 0);
            return this;
        }
    }

    private dk.webbies.tajscheck.test.tajs.TAJSNoDriverUnitTests.TAJSResultTester expect(TAJSUtil.TajsAnalysisResults result) {
        return new TAJSResultTester(result);
    }

    @Test
    public void driverDoesNotHaveAssertTypeFunctions() throws Exception {
        String driver = Main.writeFullDriver(benchFromFolder("everythingIsRight")).getRight();

        assertThat(driver, not(containsString("assertType_0")));
    }

    @Test
    public void unionMightFail() throws Exception {
        TAJSUtil.TajsAnalysisResults result = runNoDriver("unionMightFail");

        expect(result)
                .performedAllTests()
                .performed("module.foo()");

        expect(result)
                .toPaths("module.foo()")
                .hasViolations();
    }

    @Test
    public void getter() throws Exception {
        TAJSUtil.TajsAnalysisResults result = runNoDriver("getter");

        expect(result)
                .performed("module.foo()");
    }

    @Test
    @Ignore
    // TODO: Make a second step of this test, where the assertType functions of the dynamic analysis is used.
    public void simpleUnion() throws Exception {
        TAJSUtil.TajsAnalysisResults result = runNoDriver("simpleUnion");

        expect(result)
                .performed("module.foo()");
    }

    @Test
    public void primitiveOrObject() throws Exception {
        TAJSUtil.TajsAnalysisResults result = runNoDriver("primitiveOrObject");

        expect(result)
                .performed("module.foo()");
    }

    @Test
    public void objectWithNumberProps() throws Exception {
        TAJSUtil.TajsAnalysisResults result = runNoDriver("objectWithNumberProps");

        expect(result)
                .performed("module.foo()");

        expect(result)
                .performed("module.bar()");
    }

    @Test
    public void everythingIsRight() throws Exception {
        TAJSUtil.TajsAnalysisResults result = runNoDriver("everythingIsRight");

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
        TAJSUtil.TajsAnalysisResults result = runNoDriver("recursiveObject");

        expect(result)
                .performed("module.foo.rec");
    }

    @Test
    public void functionAndObject() throws Exception {
        TAJSUtil.TajsAnalysisResults result = runNoDriver("functionAndObject");

        expect(result)
                .performed("module.foo.foo");
    }

    /**
     * This test exposes how a sound static analysis can produce an unsound result.
     * Because what the analysis does, is say, if this code is reachable, then the following is true.
     * But if that code isn't reachable,
     */
    @Test
    @Ignore
    public void baitingTajsUnion() throws Exception {
        TAJSUtil.TajsAnalysisResults result = runNoDriver("baitingTajsUnion");

        expect(result)
                .performed("module.foo().[union0].bar.baz");

        expect(result)
                .performed("module.foo().[union1].bar.baz");
    }

    @Test
    public void spuriousUnion() throws Exception {
        TAJSUtil.TajsAnalysisResults result = runNoDriver("spuriousUnion");

        expect(result)
                .toPaths("module.foo()");
                /*FIXME: .expected("maybe string")
                .toFail();*/
    }

    @Test
    public void spuriousOverload() throws Exception {
        // I wanted to make a more complicated test, but since TAJS cannot see that (typeof [bool/number] !== "string"), it has to be quite simple.
        TAJSUtil.TajsAnalysisResults result = runNoDriver("spuriousOverload");

        expect(result)
                .performed("Foo");
                /*FIXME:.expected("overload (a: string) to be called")
                .toFail();*/

    }

    @Test
    public void splitSignatures() throws Exception {
        TAJSUtil.TajsAnalysisResults result = runNoDriver("splitSignatures");

        expect(result)
                .performed("Foo");
                /*FIXME:.expected("overload (a: string) to be called")
                .toFail();*/
    }

}


