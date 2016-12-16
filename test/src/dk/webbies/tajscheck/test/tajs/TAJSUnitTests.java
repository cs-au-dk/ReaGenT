package dk.webbies.tajscheck.test.tajs;

import dk.brics.tajs.analysis.nativeobjects.JSGlobal;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.lattice.Context;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.monitoring.CompositeMonitoring;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.monitoring.Monitoring;
import dk.brics.tajs.monitoring.OrdinaryExitReachableChecker;
import dk.brics.tajs.options.Options;
import dk.brics.tajs.test.Misc;
import dk.brics.tajs.util.ExperimentalAnalysisVariables;
import dk.brics.tajs.util.Pair;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.benchmarks.CheckOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.test.tajs.AssertionResult.BooleanResult;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Util;
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
    public static MultiMap<String, AssertionResult> runTAJS(String file) {
        dk.brics.tajs.Main.reset();

        Options.get().enableTest();
        Options.get().enableDeterminacy();
        Options.get().enablePolyfillES6Collections();
        Options.get().enableEs6MiscPolyfill();
        Options.get().enableConsoleModel();
        Options.get().enableIncludeDom();

        IAnalysisMonitoring monitoring = CompositeMonitoring.buildFromList(new Monitoring(), new OrdinaryExitReachableChecker());

//        Misc.init();
//        Misc.captureSystemOutput();

        Misc.run(new String[]{file}, monitoring);

        Map<Pair<AbstractNode, Context>, Set<Pair<String, Value>>> recordings = ExperimentalAnalysisVariables.get().get(JSGlobal.TAJSRecordKey.instance);

        return createResult(recordings.values());
    }

    private static MultiMap<String, AssertionResult> createResult(Collection<Set<Pair<String, Value>>> values) {
        ArrayListMultiMap<String, Value> collectedValues = new ArrayListMultiMap<>();

        for (Set<Pair<String, Value>> value : values) {
            for (Pair<String, Value> pair : value) {
                collectedValues.put(pair.getFirst(), pair.getSecond());
            }
        }

        // Left string is path, right string is expected
        Map<Pair<String, String>, AssertionResult> result = new HashMap<>();

        for (Map.Entry<String, Collection<Value>> entry : collectedValues.toMap().entrySet()) {
            String key = entry.getKey();
            String[] split = key.split(" \\| ");

            assertThat(split.length, anyOf(is(2), is(3)));

            String path = split[0];
            String expected = split[1];

            Pair<String, String> pairKey = Pair.make(path, expected);
            if (!result.containsKey(pairKey)) {
                result.put(pairKey, new AssertionResult());
            }

            AssertionResult partResult = result.get(pairKey);
            partResult.expected = expected;
            if (split.length == 2) {
                partResult.result = BooleanResult.parse(entry.getValue());
            } else {
                partResult.actual = Value.join(entry.getValue());
            }
        }

        MultiMap<String, AssertionResult> actualResult = new ArrayListMultiMap<>();

        for (Map.Entry<Pair<String, String>, AssertionResult> entry : result.entrySet()) {
            actualResult.put(entry.getKey().getFirst(), entry.getValue());
        }

        return actualResult;
    }

    private static Benchmark benchFromFolder(String folderName) {
        CheckOptions options = CheckOptions.defaultOptions();
        return new Benchmark(ParseDeclaration.Environment.ES5Core, "test/tajsUnit/" + folderName + "/implementation.js", "test/tajsUnit/" + folderName + "/declaration.d.ts", "module", Benchmark.LOAD_METHOD.REQUIRE, options).withTAJS();
    }

    static MultiMap<String, AssertionResult> run(String folderName) throws Exception {
        Benchmark bench = benchFromFolder(folderName);

        String fullDriver = Main.generateFullDriver(bench);

        String filePath = Main.getTestFilePath(bench, Main.TEST_FILE_NAME);

        Util.writeFile(filePath, fullDriver);

        MultiMap<String, AssertionResult> result = runTAJS(filePath);

        System.out.println(prettyResult(result));

        return result;
    }

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

    static String prettyResult(MultiMap<String, AssertionResult> result) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Collection<AssertionResult>> entry : result.toMap().entrySet()) {
            for (AssertionResult tajsResult : entry.getValue()) {
                if (tajsResult.result.isSometimesFalse()) {
                    builder
                            .append("Found assertion error on path ").append(entry.getKey()).append("\n")
                            .append("    Assertion failed with: ").append(tajsResult.result.pretty).append("\n")
                            .append("    Expected: ").append(tajsResult.expected).append("\n")
                            .append("    But got: ").append(tajsResult.actual.toString()).append("\n");
                    if (tajsResult.result == BooleanResult.SOMETIMES_TRUE_SOMETIMES_FALSE) {
                        System.out.println("    This is however not the case for some other executions of the same assert.\n");
                    }
                    builder.append("\n");
                }
            }
        }
        return builder.toString();
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

}
