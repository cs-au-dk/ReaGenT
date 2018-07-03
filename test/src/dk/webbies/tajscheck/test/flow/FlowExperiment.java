package dk.webbies.tajscheck.test.flow;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.experiments.Experiment;
import dk.webbies.tajscheck.util.Util;
import org.junit.Test;

import java.util.function.BiConsumer;

public class FlowExperiment {
    @Test
    public void doThaThing() {
        System.out.println(new Experiment(FlowBenchmarks.getBenchmarks())
                .addExperiment((bench, registrator) -> {
                    doThaThingOnBench(bench, registrator, "");
                    if (bench.patched() != null) {
                        doThaThingOnBench(bench.patched(), registrator, "patched-");
                    }
                }).calculate().toCSV());

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
            TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(bench, 2 * 60);
            reagentErrors = result.detectedViolations.size();
            registrator.accept(prefix + "ReaGenT-errors", result.detectedViolations.size() + "");
            Util.writeFile(Main.getFolderPath(bench) + prefix + "reagent.txt", result.toString());
            registrator.accept(prefix + "ReaGenT-time", ((System.currentTimeMillis() - startTime) / 1000) + "s");
        } catch (Exception e) {
            registrator.accept(prefix + "ReaGenT-exception", e.getClass().getSimpleName());
        }

        registrator.accept(prefix + "total-errors", (tstestErrors + reagentErrors) + "");
    }
}
