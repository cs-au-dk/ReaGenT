package dk.webbies.tajscheck.test.experiments;

import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 16-01-2017.
 */
public class Experiment {
    @FunctionalInterface
    public interface ExperimentSingleRunner {
        String run(Benchmark benchmark) throws Exception;
    }

    @FunctionalInterface
    public interface ExperimentMultiRunner {
        List<String> run(Benchmark benchmark) throws Exception;
    }

    private final List<Pair<String, Benchmark>> benchmarks;
    private final List<Pair<List<String>, ExperimentMultiRunner>> experiments = new ArrayList<>();

    public Experiment(List<Pair<String, Benchmark>> benchmarks) {
        this.benchmarks = benchmarks.stream().sorted(Comparator.comparing(Pair::getLeft)).collect(Collectors.toList());
    }

    public Experiment(String... names) {
        this(
                Arrays.stream(names)
                    .map(name -> {assert RunBenchmarks.benchmarks.containsKey(name); return name;})
                    .map(name -> new Pair<>(name, RunBenchmarks.benchmarks.get(name)))
                    .collect(Collectors.toList())
        );
    }

    public Experiment() {
        this(RunBenchmarks.benchmarks);
    }

    public Experiment(Map<String, Benchmark> benchmarks) {
        this(benchmarks.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
    }

    public void addSingleExperiment(String name, ExperimentSingleRunner calculator) {
        addSingleExperiment(new Pair<>(name, calculator));
    }

    public void addSingleExperiment(Pair<String,ExperimentSingleRunner> experiment) {
        addMultiExperiment(new Pair<>(
                Collections.singletonList(experiment.getLeft()),
                (bench) -> Collections.singletonList(experiment.getRight().run(bench))
        ));
    }

    public void addMultiExperiment(String name1, String name2, ExperimentMultiRunner calculator) {
        addMultiExperiment(Arrays.asList(name1, name2), calculator);
    }

    public void addMultiExperiment(String name1, String name2, String name3, ExperimentMultiRunner calculator) {
        addMultiExperiment(Arrays.asList(name1, name2, name3), calculator);
    }

    public void addMultiExperiment(List<String> names, ExperimentMultiRunner calculator) {
        addMultiExperiment(new Pair<>(names, calculator));
    }

    public void addMultiExperiment(Pair<List<String>, ExperimentMultiRunner> experiment) {
        this.experiments.add(experiment);
    }

    public Table calculate() {
        return calculate(1);
    }

    public Table calculate(int threads) {
        Table table = new Table();

        List<String> header = new ArrayList<>();
        header.add("Benchmark");
        experiments.stream().map(Pair::getLeft).reduce(new ArrayList<>(), Util::reduceList).forEach(header::add);

        table.addRow(header);

        ExecutorService pool = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < benchmarks.size(); i++) {
            int rowIndex = i + 1;
            Pair<String, Benchmark> benchmark = benchmarks.get(i);

            List<String> row = Collections.synchronizedList(new ArrayList<>());
            row.add(benchmark.getLeft());

            pool.submit(() -> {
                System.out.println("Running benchmark: " + benchmark.getLeft() + " (" + rowIndex + "/" + benchmarks.size() + ")");
                for (Pair<List<String>, ExperimentMultiRunner> pair : experiments) {
                    List<String> subResult;
                    try {
                        subResult = pair.getRight().run(benchmark.getRight());
                    } catch (Throwable e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    row.addAll(subResult);
                }
                try {
                    table.setRow(rowIndex, row);
                    System.out.println("\nSub result ready:");
                    System.out.println(table.toCSV());
                    System.out.println();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        }

        pool.shutdown();
        try {
            pool.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        return table;
    }
}
