package dk.webbies.tajscheck.tajstester;

import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.flowgraph.FlowGraph;
import dk.brics.tajs.monitoring.*;
import dk.brics.tajs.options.OptionValues;
import dk.brics.tajs.util.AnalysisLimitationException;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.data.TestCertificate;
import dk.webbies.tajscheck.tajstester.data.Timers;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
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

        return new TajsAnalysisResults(typeTester, timedout);
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
        public Set<Test> retractedTests;
        public final Map<Test, Integer> testTranfers;
        public final List<TestCertificate> certificates;
        public final Timers timers;

        private boolean VERBOSE = true;

        public TajsAnalysisResults(MultiMap<String, TypeViolation> detectedViolations,
                            MultiMap<String, TypeViolation> warnings,
                            Collection<Test> testPerformed,
                            List<Test> testNot,
                            List<TestCertificate> certificates,
                            Map<Test, Integer> testTranfers,
                            Timers timers,
                            boolean timedout,
                            Set<Test> retractedTests) {

            this.detectedViolations = detectedViolations;
            this.detectedWarnings = warnings;
            this.testPerformed = testPerformed;
            this.testNot = testNot;
            this.testTranfers = testTranfers;
            this.certificates = certificates;
            this.timers = timers;
            this.timedout = timedout;
            this.retractedTests = retractedTests;
        }

        public TajsAnalysisResults(TajsTypeTester typeTester, boolean timedout) {
            this.timedout = timedout;

            this.detectedViolations = new ArrayListMultiMap<>();
            typeTester.getViolations(timedout).stream().distinct().forEach(vio -> {
                this.detectedViolations.put(vio.path, vio);
            });

            this.detectedWarnings = new ArrayListMultiMap<>();
            typeTester.getWarnings(timedout).stream().distinct().forEach(vio -> {
                this.detectedWarnings.put(vio.path, vio);
            });

            testPerformed = typeTester.getPerformedTests();

            this.testNot = new ArrayList<>(typeTester.getAllTests());
            this.testNot.removeAll(typeTester.getPerformedTests());

            this.retractedTests = typeTester.getRetractedTests();

            this.certificates = typeTester.getCertificates(timedout);

            this.testTranfers = typeTester.getTransferMonitor().getTestTransfers();

            this.timers = typeTester.getTimers();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (this.timedout)
                builder.append("Type-checking timedout!").append("\n");
            builder.append("Tests not performed (").append(testNot.size()).append(")").append("\n");
            for (Test notPerformed : testNot) {
                builder.append("   ").append(notPerformed);
                if (retractedTests.contains(notPerformed)) {
                    builder.append(" (retracted)");
                }
                builder.append("\n");
            }

            builder.append("Tests performed (").append(testPerformed.size()).append(")").append("\n");
            for (Test performed : testPerformed) {
                builder.append("   ").append(performed).append("\n");
                if (retractedTests.contains(performed)) {
                    throw new RuntimeException();
                }
            }

            printTypeViolations(builder, this.detectedViolations, "Violations");

            printTypeViolations(builder, this.detectedWarnings, "Warnings");

            if (VERBOSE) {
//                builder.append("Test details:\n   ")
//                        .append(mkString(certificates, "\n   "));
                builder.append("\n");
                builder.append("Transfers per test:\n   ")
                        .append(mkString(testTranfers.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).map(e -> e.getKey() + ": " + e.getValue() + (retractedTests.contains(e.getKey()) ? " (retracted)" : "")), "\n   "));
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
