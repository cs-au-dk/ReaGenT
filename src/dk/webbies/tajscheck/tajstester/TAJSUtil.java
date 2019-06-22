package dk.webbies.tajscheck.tajstester;

import com.google.gson.Gson;
import dk.au.cs.casa.typescript.types.ClassType;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.flowgraph.FlowGraph;
import dk.brics.tajs.flowgraph.jsnodes.ReadPropertyNode;
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
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.TajsMisc;
import dk.webbies.tajscheck.util.Util;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static TajsAnalysisResults runNoDriverTAJS(
            Benchmark bench,
            int secondsTimeout,
            BenchmarkInfo info,
            List<Test> tests) {
        dk.brics.tajs.Main.reset();

        OptionValues additionalOpts = new OptionValues();
        CmdLineParser parser = new CmdLineParser(additionalOpts);
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

        if (info.bench.run_method == Benchmark.RUN_METHOD.NODE) {
            additionalOpts.enableNodeJS();
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
        additionalOpts.disableLowSeverity();

        additionalOpts.enableTypeCheck();

        additionalOpts.getSoundnessTesterOptions().setTest(false);
//        additionalOpts.enableTypeFilters();

        additionalOpts.getUnsoundness().setIgnoreSomePrototypesDuringDynamicPropertyReads(true);
        additionalOpts.getUnsoundness().setIgnoreUnlikelyPropertyReads(true);
        additionalOpts.getUnsoundness().setIgnoreMissingNativeModels(true);
        additionalOpts.getUnsoundness().setIgnoreEventsAfterExceptions(true);
//        additionalOpts.getUnsoundness().setIgnoreScanExceptionsIfEarlyTermination(true);
        additionalOpts.enableAnalysisLimitationWarnOnly();

        additionalOpts.enableUnevalizer();
        if (bench.options.staticOptions.useInspector) additionalOpts.enableInspector();

        List<IAnalysisMonitoring> optMonitors = new LinkedList<>();

        if (secondsTimeout > 0) { // Timeout
            AnalysisTimeLimiter timeLimiter = new AnalysisTimeLimiter(secondsTimeout, -1, !bench.options.staticOptions.useInspector);
            optMonitors.add(timeLimiter);

            optMonitors.add(new DefaultAnalysisMonitoring() {
                public void visitPhasePost(AnalysisPhase phase) {
                    if (phase == AnalysisPhase.SCAN) {
                        if (!typeTester.getViolationsOracle().isTight()) {
                            System.out.println("The violation oracle used is not tight, remove the following suppressions:\n" +
                                    new ArrayList<>(typeTester.getViolationsOracle().getUnnecessarySuppressions()));
                        }
                    }
                }
            });
        }

        optMonitors.add(new AnalysisMonitor());
        optMonitors.add(typeTester.getSuspiciousMonitor());
        optMonitors.add(typeTester.getTransferMonitor());
        optMonitors.add(typeTester.getCoverageMonitor());
        optMonitors.add(typeTester.getReadFromStdlibMonitor());

        IAnalysisMonitoring monitoring = CompositeMonitor.make(optMonitors);
        initLogging();

        Analysis a = dk.brics.tajs.Main.init(additionalOpts, monitoring, null, new TesterTransfer(), typeTester);
        boolean timedout = false;
        try {
            dk.brics.tajs.Main.run(a);
            TajsMisc.captureSystemOutput();
        } catch (AnalysisLimitationException.AnalysisTimeException e) {
            timedout = true;
        } catch (OutOfMemoryError e) {
            dk.brics.tajs.Main.reset(); // Trying to quickly free some mem.
            timedout = true;
        }

        TajsAnalysisResults results = new TajsAnalysisResults(typeTester, timedout);
        try {
            Util.writeFile("partialResult.txt", results.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    public static TajsAnalysisResults runNoDriver(Benchmark bench, int secondsTimeout) throws Exception {
        BenchmarkInfo info = BenchmarkInfo.create(bench);
        List<Test> tests = new TestCreator(info).createTests();

        TajsAnalysisResults result = runNoDriverTAJS(bench, secondsTimeout, info, tests);

        Gson gson = new Gson();
        Path finalResultPath = Paths.get(info.bench.dTSFile).getParent().resolve("finalResult.json").toAbsolutePath();
        Util.writeFile(finalResultPath.toString(), gson.toJson(result.summary()));
        ResultIndex.singleton.addFinalResult(info.bench.name, finalResultPath);
        ResultIndex.singleton.save();//FIXME: Move to test after-all?

        //System.out.println(prettyResult(result, assertionResult -> assertionResult.result.isSometimesFalse()));

        return result;
    }

    public static class TajsAnalysisResults {
        public final MultiMap<String, TypeViolation> detectedViolations;
        public final MultiMap<String, TypeViolation> detectedViolationsBeforeScan;
        public final MultiMap<String, TypeViolation> detectedWarnings;
        public final Set<ReadPropertyNode> possiblyProblematicReads;
        public final Collection<Test> testPerformed;
        public final List<Test> testNot;
        public final boolean timedout;
        public final List<Test> typeCheckedTests;
        public final Set<Test> retractedTests;
        public final Set<Test> timeoutTests;
        public final Map<Test, Integer> testTranfers;
        public final List<TestCertificate> certificates;
        public final Timers timers;
        public final double statementCoverage;
        public final double branchCoverage;
        public final double functionCoverage;
        public final boolean hasClassesInDec;

        private boolean VERBOSE = false;
        public MultiMap<Test, Exception> exceptionsEncountered;

        public TajsAnalysisResults(MultiMap<String, TypeViolation> detectedViolations,
                                   MultiMap<String, TypeViolation> warnings,
                                   Collection<Test> testPerformed,
                                   List<Test> testNot,
                                   List<TestCertificate> certificates,
                                   Map<Test, Integer> testTranfers,
                                   Timers timers,
                                   boolean timedout,
                                   Set<Test> retractedTests,
                                   Set<Test> timeoutTests,
                                   List<Test> typeCheckedTests,
                                   MultiMap<String, TypeViolation> detectedViolationsBeforeScan,
                                   Set<ReadPropertyNode> possiblyProblematicReads,
                                   double statementCoverage, double branchCoverage, double functionCoverage,
                                   boolean hasClassesInDec) {

            this.detectedViolations = detectedViolations;
            this.detectedViolationsBeforeScan = detectedViolationsBeforeScan;
            this.detectedWarnings = warnings;
            this.testPerformed = testPerformed;
            this.testNot = testNot;
            this.testTranfers = testTranfers;
            this.certificates = certificates;
            this.timers = timers;
            this.timedout = timedout;
            this.retractedTests = retractedTests;
            this.timeoutTests = timeoutTests;
            this.typeCheckedTests = typeCheckedTests;
            this.possiblyProblematicReads = possiblyProblematicReads;
            this.statementCoverage = statementCoverage;
            this.branchCoverage = branchCoverage;
            this.functionCoverage = functionCoverage;
            this.hasClassesInDec = hasClassesInDec;
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

            this.exceptionsEncountered = typeTester.getExceptionsEncountered();

            this.testPerformed = typeTester.getPerformedTests();

            this.testNot = new ArrayList<>(typeTester.getAllTests());
            this.testNot.removeAll(typeTester.getPerformedTests());

            this.retractedTests = typeTester.getRetractedTests();
            this.timeoutTests = typeTester.getTimedOutTests();

            this.certificates = typeTester.getCertificates(timedout);

            this.detectedViolationsBeforeScan = new ArrayListMultiMap<>();
            typeTester.getViolations(true).stream().distinct().forEach(vio -> {
                this.detectedViolationsBeforeScan.put(vio.path, vio);
            });

            this.testTranfers = typeTester.getTransferMonitor().getTestTransfers();

            this.timers = typeTester.getTimers();

            this.typeCheckedTests = typeTester.getTypeCheckedTests();

            this.statementCoverage = typeTester.getCoverageMonitor().statementCoverage();
            this.branchCoverage = typeTester.getCoverageMonitor().branchCoverage();
            this.functionCoverage = typeTester.getCoverageMonitor().functionCoverage();
            this.possiblyProblematicReads = typeTester.getReadFromStdlibMonitor().getPossiblyProblematicReads();
            this.hasClassesInDec = typeTester.getAllTests().stream().flatMap(test -> test.getProduces().stream()).anyMatch(ClassType.class::isInstance);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (this.timedout)
                builder.append("Type-checking timedout!").append("\n");
            builder.append("Actions not performed (").append(testNot.size()).append(")").append("\n");
            for (Test notPerformed : testNot) {
                builder.append("   ").append(notPerformed);
                if (retractedTests.contains(notPerformed)) {
                    builder.append(" (retracted)");
                }
                if (timeoutTests.contains(notPerformed)) {
                    builder.append(" (timeout)");
                }
                if (exceptionsEncountered.containsKey(notPerformed)) {
                    builder.append(" (exception: ").append(exceptionsEncountered.get(notPerformed).stream().map(e -> e + "@@" + Utils.readableStackTrace(e.getStackTrace())).collect(Collectors.toList())).append(")");
                }
                builder.append("\n");
            }

            builder.append("Actions performed (").append(testPerformed.size()).append(")").append("\n");
            for (Test performed : testPerformed) {
                builder.append("   ").append(performed).append("\n");
            }

            printTypeViolations(builder, this.detectedViolations, "Violations");

            printTypeViolations(builder, this.detectedWarnings, "Warnings");

            if (VERBOSE) {
//                builder.append("Test details:\n   ")
//                        .append(mkString(certificates, "\n   "));
                builder.append("\n");
                builder.append("Transfers per action:\n   ")
                        .append(mkString(testTranfers.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).map(e -> {
                            Test test = e.getKey();
                            String result = test + ": " + e.getValue();
                            if (retractedTests.contains(test)) {
                                result += " (retracted)";
                            }
                            if (timeoutTests.contains(test)) {
                                result += " (timeout)";
                            }
                            if (exceptionsEncountered.containsKey(test)) {
                                result += " (exception: " + exceptionsEncountered.get(test).stream().map(ex -> e + "@@" + Utils.readableStackTrace(ex.getStackTrace())).collect(Collectors.toList()) + ")";
                            }
                            return result;
                        }), "\n   "));
                builder.append("\n");
                builder.append(timers.toString());

                if (!exceptionsEncountered.get(null).isEmpty()) {
                    builder.append("Exceptions outside action-context: \n");
                    exceptionsEncountered.get(null).stream().map(Object::toString).map(str -> "   " + str + "\n").distinct().forEach(builder::append);
                }

            }
//            if (!this.possiblyProblematicReads.isEmpty()) {
//                builder.append("Reads performed by the library that could be affected by the client: \n");
//                this.possiblyProblematicReads.stream().map(AbstractNode::getSourceLocation).map(Object::toString).distinct().sorted().forEach(str -> builder.append(str).append("\n"));
//            }
            if (hasClassesInDec) {
                builder.append("The implementation does not currently support inheritance");
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

        public ResultSummary summary() {
            return new ResultSummary(this);
        }
    }

    public static class ResultSummary {
        Map<String, ArrayList<TypeViolation>> violations = new HashMap<>();
        ResultSummary(TajsAnalysisResults result) {
            for(String k : result.detectedViolations.keySet()) {
                ArrayList<TypeViolation> al = new ArrayList<>();
                al.addAll(result.detectedViolations.get(k));
                violations.put(k, al);
            }
        }
    }

    public static class ResultIndex {
        public static ResultIndex singleton = loadOrWipe();

        private static Path defaultIndexPath = Paths.get("out").resolve("index.json").toAbsolutePath();

        private Map<String, String> index = new HashMap<>();

        private ResultIndex() {

        }

        private static ResultIndex loadOrWipe() {
            if (defaultIndexPath == null) {
                return new ResultIndex();
            }
            Gson gson = new Gson();
            try {
                return gson.fromJson(Util.readFile(defaultIndexPath.toString()), ResultIndex.class);
            }
            catch (Exception e) {
                return new ResultIndex();
            }

        }

        public void addFinalResult(String name, Path path) {
            index.put(name, Paths.get("").toAbsolutePath().relativize(path.toAbsolutePath()).toString());
        }

        public void save() throws Exception {
            Gson gson = new Gson();
            Util.writeFile(defaultIndexPath.toString(), gson.toJson(this));
        }
    }
}
