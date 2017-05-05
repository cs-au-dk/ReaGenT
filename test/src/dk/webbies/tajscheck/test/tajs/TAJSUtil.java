package dk.webbies.tajscheck.test.tajs;

import dk.brics.tajs.analysis.TAJSFunctionsEvaluator;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.lattice.Context;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.meta.monitors.MonitorFactory;
import dk.brics.tajs.monitoring.*;
import dk.brics.tajs.options.OptionValues;
import dk.brics.tajs.options.Options;
import dk.brics.tajs.test.Misc;
import dk.brics.tajs.util.AnalysisLimitationException;
import dk.brics.tajs.util.ExperimentalAnalysisVariables;
import dk.brics.tajs.util.Pair;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.buildprogram.TAJSTypeChecker;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import static dk.webbies.tajscheck.util.Pair.toTAJS;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by erik1 on 19-12-2016.
 */
public class TAJSUtil {
    public static MultiMap<String, AssertionResult> runTAJS(String file, int secondsTimeout, Benchmark.RUN_METHOD run_method, BenchmarkInfo info) throws TimeoutException {
        dk.brics.tajs.Main.reset();

        OptionValues options = Options.get();
        options.enableTest();
        options.enableDeterminacy();
        options.enablePolyfillES6Collections();
        options.enableEs6MiscPolyfill();
        options.enableConsoleModel();
        options.enableUnevalizer();
        options.setCustomFunction(TAJSTypeChecker.get(info).getCustomFunction());
        if (run_method == Benchmark.RUN_METHOD.BROWSER || run_method == Benchmark.RUN_METHOD.BOOTSTRAP) {
            options.enableIncludeDom();
        } else if (run_method == Benchmark.RUN_METHOD.NODE) {
            // nothing.
        } else {
            throw new RuntimeException("Definitely cannot do this!");
        }


        List<IAnalysisMonitoring> monitors = new ArrayList<>(Arrays.asList(new Monitoring(), new OrdinaryExitReachableChecker()));

        if (true) { // TODO: Opion: LineAnalysis.
            monitors.add(MonitorFactory.createLineAnalysis2Monitor());
            options.enableAbortGracefullyOnAnalysisLimitations();
        }


        IAnalysisMonitoring monitoring = CompositeMonitoring.buildFromList(monitors);

        Misc.init();
        Misc.captureSystemOutput();



        if (secondsTimeout > 0) {
            AnalysisTimeLimiter analysisLimiter = new AnalysisTimeLimiter(secondsTimeout, true);

            long startTime = System.currentTimeMillis();

            try {
                Misc.run(new String[]{file}, CompositeMonitoring.buildFromList(monitoring, analysisLimiter));
            } catch (AnalysisLimitationException.AnalysisTimeException e) {
                throw new TimeoutException(e.toString());
            }

        } else {
            Misc.run(new String[]{file}, monitoring);
        }



        // Assertion results comes from two sources: Calls to assert, and direct calls to our assertType functions.

        Map<Pair<AbstractNode, Context>, Set<Pair<String, Value>>> recordings = ExperimentalAnalysisVariables.get().get(TAJSFunctionsEvaluator.TAJSRecordKey.instance);

        if (recordings != null) {
            return createResult(recordings.values());
        } else {
            return new ArrayListMultiMap<>();
        }
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

        for (Map.Entry<String, Collection<Value>> entry : collectedValues.asMap().entrySet()) {
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
                partResult.result = AssertionResult.BooleanResult.parse(entry.getValue());
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

    public static MultiMap<String, AssertionResult> run(Benchmark bench) throws Exception {
        return run(bench, -1);
    }

    public static MultiMap<String, AssertionResult> run(Benchmark bench, int secondsTimeout) throws Exception {
        Pair<BenchmarkInfo, String> driver = toTAJS(Main.writeFullDriver(bench));

        String filePath = Main.getFolderPath(bench) + Main.TEST_FILE_NAME;

        MultiMap<String, AssertionResult> result = runTAJS(filePath, secondsTimeout, bench.run_method, driver.getFirst());

        System.out.println(prettyResult(result, assertionResult -> assertionResult.result.isSometimesFalse()));

        return result;
    }

    public static String prettyResult(MultiMap<String, AssertionResult> result, Predicate<AssertionResult> predicate) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Collection<AssertionResult>> entry : result.asMap().entrySet()) {
            for (AssertionResult tajsResult : entry.getValue()) {
                if (predicate.test(tajsResult)) {
                    builder
                            .append("Path ").append(entry.getKey()).append("\n")
                            .append("    The assertion is: ").append(tajsResult.result.pretty).append("\n")
                            .append("    Expected: ").append(tajsResult.expected).append("\n")
                            .append("    But got: ").append(tajsResult.actual.toString()).append("\n");
                    builder.append("\n");
                }
            }
        }
        return builder.toString();
    }
}
