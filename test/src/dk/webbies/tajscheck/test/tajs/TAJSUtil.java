package dk.webbies.tajscheck.test.tajs;

import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.analysis.TAJSFunctionsEvaluator;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.lattice.Context;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.monitoring.*;
import dk.brics.tajs.options.OptionValues;
import dk.brics.tajs.options.Options;
import dk.brics.tajs.util.AnalysisLimitationException;
import dk.brics.tajs.util.ExperimentalAnalysisVariables;
import dk.brics.tajs.util.Pair;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.TajsTypeTester;
import dk.webbies.tajscheck.tajstester.TajsTypeChecker;
import dk.webbies.tajscheck.tajstester.TypeViolation;
import dk.webbies.tajscheck.testcreator.TestCreator;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import static dk.brics.tajs.Main.initLogging;
import static dk.webbies.tajscheck.util.Pair.toTAJS;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TAJSUtil {
    public static TajsAnalysisResults runNoDriverTAJS(String file, int secondsTimeout, Benchmark.RUN_METHOD run_method, BenchmarkInfo info, List<Test> tests) throws TimeoutException {
        dk.brics.tajs.Main.reset();

        IAnalysisMonitoring baseMonitoring = new Monitoring();
        OptionValues additionalOpts = new OptionValues();
        CmdLineParser parser = new CmdLineParser(additionalOpts);
        TajsTypeTester typeTester = new TajsTypeTester(tests, info);

        try {
            parser.parseArgument(file);
        } catch (CmdLineException e) {
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
            results.put(vio.test.getPath(), vio);
        }

        List<Test> notPerformed = new LinkedList<>();
        notPerformed.addAll(typeTester.getAllTests());
        notPerformed.removeAll(typeTester.getPerformedTests());

        return new TajsAnalysisResults(results, typeTester.getPerformedTests(), notPerformed);
    }



    public static TajsAnalysisResults runNoDriver(Benchmark bench, int secondsTimeout) throws Exception {
        BenchmarkInfo info = BenchmarkInfo.create(bench);
        List<Test> tests = new TestCreator(info).createTests();

        TajsAnalysisResults result = runNoDriverTAJS(bench.jsFile, secondsTimeout, bench.run_method, info, tests);

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
    }
}
