package dk.webbies.tajscheck.test.tajs;

import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.analysis.WorkListStrategy;
import dk.brics.tajs.flowgraph.FlowGraph;
import dk.brics.tajs.lattice.Context;
import dk.brics.tajs.monitoring.*;
import dk.brics.tajs.options.OptionValues;
import dk.brics.tajs.solver.NodeAndContext;
import dk.brics.tajs.util.AnalysisLimitationException;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.*;
import dk.webbies.tajscheck.testcreator.TestCreator;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.TajsMisc;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static dk.brics.tajs.Main.initLogging;
import static dk.brics.tajs.util.Collections.newList;
import static dk.webbies.tajscheck.util.Util.mkString;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TAJSUtil {

    public static TajsAnalysisResults runNoDriverTAJS(Benchmark bench, int secondsTimeout, BenchmarkInfo info, List<Test> tests) throws TimeoutException {
        return runNoDriverTAJS(bench, secondsTimeout, info, tests, false);
    }

    public static TajsAnalysisResults runNoDriverTAJS(
            Benchmark bench,
            int secondsTimeout,
            BenchmarkInfo info,
            List<Test> tests,
            boolean useInspector) throws TimeoutException {
        dk.brics.tajs.Main.reset();

        OptionValues additionalOpts = new OptionValues();
        CmdLineParser parser = new CmdLineParser(additionalOpts);
        OptionValues.ContextStrategyConstructor contextStrategy = (FlowGraph fg) -> new TesterContextSensitivity(fg.getSyntacticInformation());
        TajsTypeTester typeTester = new TajsTypeTester(tests, info);

        if (info.bench.run_method == Benchmark.RUN_METHOD.BOOTSTRAP) {
            try {
                parser.parseArgument("resources/empty.js");
            } catch (CmdLineException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                List<String> jsFiles = bench.getDependencies().stream().map(dependency -> dependency.jsFile).collect(Collectors.toList());
                jsFiles.add(bench.jsFile);
                parser.parseArgument(jsFiles.toArray(new String[jsFiles.size()]));
            } catch (CmdLineException e) {
                throw new RuntimeException(e);
            }
        }

        additionalOpts.enableTest();
        additionalOpts.enableDeterminacy();

        additionalOpts.enablePolyfillMDN();
        additionalOpts.enablePolyfillTypedArrays();
        additionalOpts.enablePolyfillES6Collections();
        additionalOpts.enablePolyfillES6Promises();
        additionalOpts.enableConsoleModel();
        additionalOpts.enableNoHybridCollections();
        additionalOpts.enableIncludeDom();

        additionalOpts.setTypeTestRunner(typeTester);
        additionalOpts.setCustomContextSensitivityStrategy(contextStrategy);
        additionalOpts.enableTypeChecks();

        additionalOpts.getSoundnessTesterOptions().setTest(false);
        //additionalOpts.enableTypeFilters();

        additionalOpts.getUnsoundness().setIgnoreSomePrototypesDuringDynamicPropertyReads(true);
        additionalOpts.getUnsoundness().setIgnoreUnlikelyPropertyReads(true);
        additionalOpts.getUnsoundness().setIgnoreUnlikelyPropertyWrites(true);

        additionalOpts.enableUnevalizer();
        if (useInspector) additionalOpts.enableInspector();

        List<IAnalysisMonitoring> optMonitors = new LinkedList<>();

        if (secondsTimeout > 0) { // Timeout
            AnalysisTimeLimiter timeLimiter = new AnalysisTimeLimiter(secondsTimeout, -1, !useInspector);
            optMonitors.add(timeLimiter);
        }

        optMonitors.add(Monitoring.make());
        optMonitors.add(typeTester);
        optMonitors.add(typeTester.getSuspiciousMonitor());
        optMonitors.add(typeTester.getTransferMonitor());

        IAnalysisMonitoring monitoring = CompositeMonitoring.buildFromList(optMonitors);
        initLogging();

        Analysis a = dk.brics.tajs.Main.init(additionalOpts, monitoring, null);
        boolean timedout = false;
        try {
            dk.brics.tajs.Main.run(a);
            TajsMisc.captureSystemOutput();
        } catch (AnalysisLimitationException.AnalysisTimeException e) {
            timedout = true;
        }

        MultiMap<String, TypeViolation> violations = new ArrayListMultiMap<>();
        typeTester.getAllViolations().stream().distinct().forEach(vio -> {
            violations.put(vio.path, vio);
        });

        MultiMap<String, TypeViolation> warnings = new ArrayListMultiMap<>();
        typeTester.getAllWarnings().stream().distinct().forEach(vio -> {
            warnings.put(vio.path, vio);
        });

        List<Test> notPerformed = new LinkedList<>();
        notPerformed.addAll(typeTester.getAllTests());
        notPerformed.removeAll(typeTester.getPerformedTests());

        return new TajsAnalysisResults(violations, warnings, typeTester.getPerformedTests(), notPerformed, typeTester.getAllCertificates(), typeTester.getTransferMonitor().getTestTransfers(), typeTester.getSuspiciousMonitor().getSuspiciousLocations(), typeTester.getTimers(), timedout);
    }

    public static TajsAnalysisResults runNoDriver(Benchmark bench, int secondsTimeout, boolean useInspector) throws Exception {
        BenchmarkInfo info = BenchmarkInfo.create(bench);
        List<Test> tests = new TestCreator(info).createTests();

        TajsAnalysisResults result = runNoDriverTAJS(bench, secondsTimeout, info, tests, useInspector);

        //System.out.println(prettyResult(result, assertionResult -> assertionResult.result.isSometimesFalse()));

        return result;
    }

    public static TajsAnalysisResults runNoDriver(Benchmark bench, int secondsTimeout) throws Exception {
        return runNoDriver(bench, secondsTimeout, false);
    }

    public static class TajsAnalysisResults {
        public final MultiMap<String, TypeViolation> detectedViolations;
        public final MultiMap<String, TypeViolation> detectedWarnings;
        public final Collection<Test> testPerformed;
        public final List<Test> testNot;
        public final boolean timedout;
        public final Map<Test, Integer> testTranfers;
        public final Map<Test, Set<NodeAndContext<Context>>> suspiciousLocations;
        public final List<TajsTypeTester.TestCertificate> certificates;
        public final Timers timers;

        private boolean VERBOSE = true;

        TajsAnalysisResults(MultiMap<String, TypeViolation> detectedViolations,
                            MultiMap<String, TypeViolation> warnings, Collection<Test> testPerformed,
                            List<Test> testNot,
                            List<TajsTypeTester.TestCertificate> certificates,
                            Map<Test, Integer> testTranfers,
                            Map<Test, Set<NodeAndContext<Context>>> suspiciousLocations,
                            Timers timers,
                            boolean timedout) {

            this.detectedViolations = detectedViolations;
            this.detectedWarnings = warnings;
            this.testPerformed = testPerformed;
            this.testNot = testNot;
            this.testTranfers = testTranfers;
            this.suspiciousLocations = suspiciousLocations;
            this.certificates = certificates;
            this.timers = timers;
            this.timedout = timedout;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (this.timedout)
                builder.append("Type-checking timedout!").append("\n");
            builder.append("Tests not performed (").append(testNot.size()).append(")").append("\n");
            for (Test notPerformed : testNot) {
                builder.append("   ").append(notPerformed).append("\n");
            }

            builder.append("Tests performed (").append(testPerformed.size()).append(")").append("\n");
            for (Test performed : testPerformed) {
                builder.append("   ").append(performed).append("\n");
            }

            printTypeViolations(builder, this.detectedViolations, "Violations");

            printTypeViolations(builder, this.detectedWarnings, "Warnings");

            if (VERBOSE) {
                builder.append("Test details:\n   ")
                        .append(mkString(certificates, "\n   "));
                builder.append("\n");
                builder.append("Transfers per test:\n   ")
                        .append(mkString(testTranfers.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).map(e -> e.getKey() + ": " + e.getValue()), "\n   "));
                builder.append("\n");
                builder.append("Suspicious locations per test:\n   ")
                        .append(mkString(suspiciousLocations.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue()), "\n   "));
                builder.append("\n");
                builder.append(timers.toString());
            }
            return builder.toString();
        }

        private void printTypeViolations(StringBuilder builder, MultiMap<String, TypeViolation> violations, String name) {
            builder.append(name).append(" (").append(violations.size()).append(")").append("\n");
            for (Map.Entry<String, Collection<TypeViolation>> entry : violations.asMap().entrySet()) {
                builder.append("   for path: ").append(entry.getKey()).append(" (").append(entry.getValue().size()).append(")\n");
                for (TypeViolation violation : entry.getValue()) {
                    builder.append("      ").append(violation).append("\n");
                }
            }
        }
    }
}
