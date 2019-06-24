package dk.webbies.tajscheck.cmd;

import dk.webbies.tajscheck.test.tajs.analyze.CompareModesEvaluation;
import org.kohsuke.args4j.Argument;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class Experiments implements Main.Command {
    private static List<Experiment> experiments = new ArrayList<>();
    private static void experiment(String name, String description, Runnable doExperiment) {
        experiments.add(new Experiment(name, description, doExperiment));
    }
    private static void experiment(String name, String description, Consumer<String[]> doExperiment) {
        experiments.add(new Experiment(name, description, doExperiment));
    }
    static {
        //language=TEXT
        experiment(
                "compareVariants",
                "Compares different variants of RMGC. \n" +
                "Reproduces the results found in Table II in the ReaGenT paper. \n" +
                "Optionally pass extra arguments to define which benchmarks the evaluation should run on",
                (args) -> new CompareModesEvaluation().doEvaluation(args)
        );
        //language=TEXT
        experiment(
                "compareVariantsQuick",
                "Compares the 3 variants of the RMGC that should not encounter timeouts. \n" +
                "Reproduces the results found in Table II in the ReaGenT paper.",
                new CompareModesEvaluation()::doEvaluationQuick
        );
        //language=TEXT
        experiment(
                "compareVariantsAll",
                "Compares the RMGC variants on ALL relevant ReaGenT benchmarks",
                new CompareModesEvaluation()::doEvaluationBig
        );

        //language=TEXT
        experiment(
                "reagentAllAssumptions",
                "Runs ReaGenT with all assumptions on the benchmarks from Table II in the ReaGenT paper.\n" +
                "This will recreate the results from the first column in Table II.",
                new CompareModesEvaluation()::allAssumptions
        );

        //language=TEXT
        experiment(
                "reagentOnFixed",
                "Runs ReaGenT with all assumptions enabled on the fixed version of the benchmarks from Table I of the ReaGenT paper.\n" +
                "This recreates half of the results from Table I.\n" +
                "The result should be that all benchmarks have 100% action coverage and 0 violations. ",
                new CompareModesEvaluation()::allAssumptionsOnFixed
        );

        experiment(
                "tstestOnReagentBenchmarks",
                "Runs TSTest on the benchmarks used for the ReaGenT evaluation, and prints out a short summary of the results.",
                new CompareModesEvaluation()::tsTest
        );

        experiment(
                "tstestOnFixedReagentBenchmarks",
                "Runs TSTest on the fixed versions of the benchmarks used for the ReaGenT evaluation, and prints out a short summary of the results. \\n\n" +
                "The result should be that no violations are found in any of the benchmarks.",
                new CompareModesEvaluation()::tsTestFixed
        );
    }




    public static final class Experiment {
        private final String name;
        private final String description;
        private final Consumer<String[]> doExperiment;

        public Experiment(String name, String description, Runnable doExperiment) {
            this.name = name;
            this.description = description;
            this.doExperiment = args -> doExperiment.run();
        }

        public Experiment(String name, String description, Consumer<String[]> doExperiment) {
            this.name = name;
            this.description = description;
            this.doExperiment = doExperiment;
        }
    }

    @Argument
    public List<String> arguments = new ArrayList<>();

    public static void printHelp() {
        System.out.println("Usage: ./main.sh experiment <experiment>\n" +
                "E.g: ./main.sh experiment compareVariants\n" +
                "\n" +
                "Experiments: \n" +
                "");

        for (Experiment experiment : experiments) {
            System.out.println(experiment.name);
            System.out.println(experiment.description.replaceAll("(?m)^", "\t")); // ident all lines
            System.out.println();
        }

    }

    @Override
    public void run() throws Throwable {
        if (arguments.isEmpty()) {
            printHelp();
            return;
        }
        String command = arguments.get(0);
        Optional<Experiment> experiment = experiments.stream().filter(exp -> exp.name.equalsIgnoreCase(command)).findAny();
        if (!experiment.isPresent()) {
            printHelp();
            return;
        }
        String[] args = arguments.subList(1, arguments.size()).toArray(new String[0]);
        experiment.get().doExperiment.accept(args);
    }
}
