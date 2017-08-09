package dk.webbies.tajscheck.test.tajs;

import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.monitoring.*;
import dk.brics.tajs.options.OptionValues;
import dk.brics.tajs.options.Options;
import dk.brics.tajs.util.AnalysisLimitationException;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.TajsTypeTester;
import dk.webbies.tajscheck.tajstester.TesterContextSensitivity;
import dk.webbies.tajscheck.tajstester.TypeViolation;
import dk.webbies.tajscheck.testcreator.TestCreator;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.util.*;
import java.util.concurrent.TimeoutException;

import static dk.brics.tajs.Main.initLogging;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TAJSUtil {

    public static TajsAnalysisResults runNoDriverTAJS(String file, int secondsTimeout, BenchmarkInfo info, List<Test> tests) throws TimeoutException {
        dk.brics.tajs.Main.reset();

        IAnalysisMonitoring baseMonitoring = new Monitoring();
        OptionValues additionalOpts = new OptionValues();
        CmdLineParser parser = new CmdLineParser(additionalOpts);
        TesterContextSensitivity contextStrategy = new TesterContextSensitivity();
        TajsTypeTester typeTester = new TajsTypeTester(tests, info, contextStrategy);

        if (info.bench.run_method == Benchmark.RUN_METHOD.BOOTSTRAP) {
            try {
                parser.parseArgument("resources/empty.js");
            } catch (CmdLineException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                parser.parseArgument(file);
            } catch (CmdLineException e) {
                throw new RuntimeException(e);
            }
        }

        additionalOpts.enableTest();
        additionalOpts.enableDeterminacy();

        additionalOpts.enablePolyfillMDN();
        additionalOpts.enablePolyfillTypedArrays();
        additionalOpts.enablePolyfillES6Collections();
        additionalOpts.enablePolyfillES6Promise();
        additionalOpts.enableConsoleModel();
        additionalOpts.enableNoHybridCollections();
        additionalOpts.enableAbortGracefullyOnAssertions();
        additionalOpts.enableUseStrict();
        additionalOpts.enableEs6MiscPolyfill();
        additionalOpts.enableIncludeDom();

        additionalOpts.enableTypeChecks(typeTester);
        additionalOpts.setContextSensitivityStrategy(contextStrategy);


        additionalOpts.enableUnevalizer();

        if (info.bench.options.useTracified) {
            additionalOpts.setTracifierContextSensitivity(true);
            additionalOpts.setTracifierMessagePriorities(true);
        }


        Options.set(additionalOpts);
        List<IAnalysisMonitoring> optMonitors = new LinkedList<>();
        optMonitors.add(baseMonitoring);

        if (secondsTimeout > 0) { // Timeout
            AnalysisTimeLimiter timeLimiter = new AnalysisTimeLimiter(secondsTimeout, true);
            optMonitors.add(timeLimiter);
        }

        IAnalysisMonitoring monitoring = CompositeMonitoring.buildFromList(optMonitors);
        initLogging();

        Analysis a = dk.brics.tajs.Main.init(new String[0], monitoring, null);
        try {
            dk.brics.tajs.Main.run(a);
            MiscTajsUtils.captureSystemOutput();
        } catch (AnalysisLimitationException.AnalysisTimeException e) {
            throw new TimeoutException(e.toString());
        }

        List<TypeViolation> violations = typeTester.getAllViolations();
        MultiMap<String, TypeViolation> results =  new ArrayListMultiMap<>();
        for(TypeViolation vio : violations) {
            results.put(vio.path, vio);
        }

        List<Test> notPerformed = new LinkedList<>();
        notPerformed.addAll(typeTester.getAllTests());
        notPerformed.removeAll(typeTester.getPerformedTests());

        return new TajsAnalysisResults(results, typeTester.getPerformedTests(), notPerformed);
    }

    public static TajsAnalysisResults runNoDriver(Benchmark bench, int secondsTimeout) throws Exception {
        BenchmarkInfo info = BenchmarkInfo.create(bench);
        List<Test> tests = new TestCreator(info).createTests();

        TajsAnalysisResults result = runNoDriverTAJS(bench.jsFile, secondsTimeout, info, tests);

        //System.out.println(prettyResult(result, assertionResult -> assertionResult.result.isSometimesFalse()));

        return result;
    }

    public static class TajsAnalysisResults {
        MultiMap<String, TypeViolation> detectedViolations;
        List<Test> testPerformed;
        List<Test> testNot;

        TajsAnalysisResults(MultiMap<String, TypeViolation> detectedViolations,
                            List<Test> testPerformed,
                            List<Test> testNot) {

            this.detectedViolations = detectedViolations;
            this.testPerformed = testPerformed;
            this.testNot = testNot;
        }

        TajsAnalysisResults with(MultiMap<String, TypeViolation> newViolations) {
            return new TajsAnalysisResults(newViolations, testPerformed, testNot);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Tests not performed (").append(testNot.size()).append(")").append("\n");
            for (Test notPerformed : testNot) {
                builder.append("   ").append(notPerformed).append("\n");
            }

            builder.append("Tests performed (").append(testPerformed.size()).append(")").append("\n");
            for (Test performed : testPerformed) {
                builder.append("   ").append(performed).append("\n");
            }

            builder.append("Violations (").append(detectedViolations.size()).append(")").append("\n");
            for (Map.Entry<String, Collection<TypeViolation>> entry : detectedViolations.asMap().entrySet()) {
                builder.append("   for path: ").append(entry.getKey()).append(" (").append(entry.getValue().size()).append(")\n");
                for (TypeViolation violation : entry.getValue()) {
                    builder.append("      ").append(violation).append("\n");
                }
            }

            return builder.toString();
        }
    }
}
