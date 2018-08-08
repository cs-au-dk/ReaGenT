package dk.webbies.tajscheck.test.flow;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.paser.*;
import dk.webbies.tajscheck.paser.AST.FunctionExpression;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.experiments.Experiment;
import dk.webbies.tajscheck.util.Util;
import org.junit.Test;

import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public class FlowExperiment {

    private static final int TIMEOUT = 3 * 60 * 60;

    public static void main(String[] args) {
        new FlowExperiment().doThaThing();
    }

    @Test
    public void doThaThing() {
        System.out.println(new Experiment(FlowBenchmarks.getBenchmarks())
                .addExperiment((bench, registrator) -> {
                    doThaThingOnBench(bench, registrator, "");
                    if (bench.patched() != null) {
                        doThaThingOnBench(bench.patched(), registrator, "patched-");
                    }
                }).calculate("flowExperiment.csv").toCSV());

    }

    private void doThaThingOnBench(Benchmark bench, BiConsumer<String, String> registrator, String prefix) {
        int tstestErrors = 0;
        try { // TSTest.
            long startTime = System.currentTimeMillis();
            Main.writeFullDriver(bench);
            String output = Main.runBenchmark(bench);
            OutputParser.RunResult result = OutputParser.parseDriverResult(output);
            Util.writeFile(Main.getFolderPath(bench) + prefix + "tstest.txt", output);

            tstestErrors = result.typeErrors.size();
            registrator.accept(prefix + "TSTest-errors", result.typeErrors.size() + "");
            registrator.accept(prefix + "TSTest-time", ((System.currentTimeMillis() - startTime) / 1000) + "s");
        } catch (Exception e) {
            registrator.accept(prefix + "TSTest-exception", e.getClass().getSimpleName());
        }

        int reagentErrors = 0;
        try { // ReaGenT.
            long startTime = System.currentTimeMillis();
            TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(bench, TIMEOUT);
            reagentErrors = result.detectedViolations.size();
            registrator.accept(prefix + "ReaGenT-errors", result.detectedViolations.size() + "");
            Util.writeFile(Main.getFolderPath(bench) + prefix + "reagent.txt", result.toString());
            registrator.accept(prefix + "ReaGenT-time", (result.timedout ? "(timeout)" : "") + ((System.currentTimeMillis() - startTime) / 1000) + "s");

            registrator.accept(prefix + "ReaGenT-retracted", result.retractedTests.size() + "");
        } catch (Exception e) {
            registrator.accept(prefix + "ReaGenT-exception", e.getClass().getSimpleName());
            e.printStackTrace();
        }

        registrator.accept(prefix + "total-errors", (tstestErrors + reagentErrors) + "");

        try {
            String declarationFile = Util.readFile(bench.dTSFile);
            int decLines = declarationFile.split(Pattern.quote("\n")).length + 1;
            registrator.accept(prefix + "declaration-lines", decLines + "");

            String jsFile = Util.readFile(bench.jsFile);
            int jsLines = jsFile.split(Pattern.quote("\n")).length + 1;
            registrator.accept(prefix + "jsFile-lines", jsLines + "");

            CountFunctionsVisitor counter = new CountFunctionsVisitor();
            AstBuilder.stmtFromString(jsFile).accept(counter);
            registrator.accept(prefix + "jsFile-functions", counter.counter + "");
        } catch (Throwable e) {
            registrator.accept("counter-exception", e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }

    private class CountFunctionsVisitor implements StatementTransverse<Void>, ExpressionTransverse<Void> {
        private int counter = 0;
        @Override
        public Void visit(FunctionExpression function) {
            counter++;
            return ExpressionTransverse.super.visit(function);
        }

        @Override
        public StatementVisitor<Void> getStatementVisitor() {
            return this;
        }

        @Override
        public ExpressionVisitor<Void> getExpressionVisitor() {
            return this;
        }
    }
}
