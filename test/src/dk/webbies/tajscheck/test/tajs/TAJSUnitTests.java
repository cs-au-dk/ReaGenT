package dk.webbies.tajscheck.test.tajs;

import dk.brics.tajs.lattice.Value;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.benchmarks.CheckOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by erik1 on 12-12-2016.
 */
public class TAJSUnitTests {
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

    static MultiMap<String, AssertionResult> run(String folderName) throws Exception {
        Benchmark bench = benchFromFolder(folderName);

        return TAJSUtil.run(bench);
    }

    private static Benchmark benchFromFolder(String folderName) {
        CheckOptions options = CheckOptions.defaultOptions();
        return new Benchmark(ParseDeclaration.Environment.ES5Core, "test/tajsUnit/" + folderName + "/implementation.js", "test/tajsUnit/" + folderName + "/declaration.d.ts", "module", Benchmark.LOAD_METHOD.LOAD_LOCAL, options).useTAJS();
    }

    private class TAJSResultTester {
        private MultiMap<String, AssertionResult> results;

        private TAJSResultTester(MultiMap<String, AssertionResult> result) {
            this.results = result;
        }

        private TAJSResultTester forPath(String path) {
            results = results.toMap().entrySet().stream().filter(entry -> entry.getKey().equals(path)).collect(ArrayListMultiMap.collector());

            MatcherAssert.assertThat("expected something on path: " + path, results.size(),is(not(equalTo(0))));
            return this;
        }

        private TAJSResultTester got(Predicate<Value> matcher) {
            for (Collection<AssertionResult> values : results.toMap().values()) {
                for (AssertionResult value : values) {
                    assertTrue(matcher.test(value.actual));
                }
            }

            return this;
        }

        TAJSResultTester expected(String type) {
            return expected(is(type));
        }

        TAJSResultTester expected(Matcher<String> matcher) {
            results = results.toMap().entrySet().stream().map(entry -> {
                Collection<AssertionResult> value = entry.getValue().stream().filter(result ->
                        matcher.matches(result.expected)
                ).collect(Collectors.toList());

                return new SimpleEntry<>(entry.getKey(), value);
            }).filter(entry -> !entry.getValue().isEmpty()).collect(ArrayListMultiMap.collector());

            assertFalse(results.isEmpty());

            return this;
        }

        TAJSResultTester toPass() {
            for (Collection<AssertionResult> values : results.toMap().values()) {
                for (AssertionResult value : values) {
                    assertTrue(value.result.isSometimesTrue());
                }
            }

            return this;
        }

        TAJSResultTester toFail() {
            for (Collection<AssertionResult> values : results.toMap().values()) {
                for (AssertionResult value : values) {
                    assertTrue(value.result.isSometimesFalse());
                }
            }

            return this;
        }
    }

    private TAJSResultTester expect(MultiMap<String, AssertionResult> result) {
        return new TAJSResultTester(result);
    }


    @Test
    public void everythingIsRight() throws Exception {
        MultiMap<String, AssertionResult> result = run("everythingIsRight");

        assertThat(result.size(), is(4));

        expect(result)
                .forPath("module.foo.bar")
                .toPass()
                .expected("boolean")
                .got((value) -> value.isMaybeTrue() && !value.isMaybeFalse());

        expect(result)
                .forPath("module.foo")
                .toPass();

        expect(result)
                .forPath("module.foo.foo")
                .toPass();

        expect(result)
                .forPath("require(\"./implementation.js\")")
                .toPass();

    }

    /**
     * This test exposes how a sound static analysis can produce an unsound result.
     * Because what the analysis does, is say, if this code is reachable, then the following is true.
     * But if that code isn't reachable,
     */
    @Test
    @Ignore
    public void baitingTajsUnion() throws Exception {
        MultiMap<String, AssertionResult> result = run("baitingTajsUnion");

        expect(result)
                .forPath("module.foo().[union0].bar.baz")
                .toPass();

        expect(result)
                .forPath("module.foo().[union1].bar.baz")
                .toPass();
    }

    @Test
    public void spuriousUnion() throws Exception {
        MultiMap<String, AssertionResult> result = run("spuriousUnion");

        expect(result)
                .forPath("module.foo()")
                .expected("maybe string")
                .toFail();
    }

    @Test
    public void spuriousOverload() throws Exception {
        // I wanted to make a more complicated test, but since TAJS cannot see that (typeof [bool/number] !== "string"), it has to be quite simple.
        MultiMap<String, AssertionResult> result = run("spuriousOverload");

        expect(result)
                .forPath("Foo")
                .expected("overload (a: string) to be called")
                .toFail();

    }

    @Test
    public void splitSignatures() throws Exception {
        MultiMap<String, AssertionResult> result = run("splitSignatures");

        expect(result)
                .forPath("Foo")
                .expected("overload (a: string) to be called")
                .toFail();

    }

}
