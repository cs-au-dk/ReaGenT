package dk.webbies.tajscheck.test.experiments;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.util.Pair;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
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
    private final List<BiConsumer<Benchmark, BiConsumer<String, String>>> experiments = new ArrayList<>();

    public Experiment(List<Benchmark> benchmarks) {
        this.benchmarks = benchmarks.stream().map(bench -> new Pair<>(bench.name, bench)).sorted(Comparator.comparing(Pair::getLeft)).collect(Collectors.toList());
    }

    public Experiment(Collection<String> names) {
        this(
                names.stream()
                        .peek(name -> {assert RunBenchmarks.benchmarks.containsKey(name);})
                        .map(RunBenchmarks.benchmarks::get)
                        .collect(Collectors.toList())
        );
    }

    public Experiment(String... names) {
        this(Arrays.asList(names));
    }

    public Experiment() {
        this(new ArrayList<Benchmark>(RunBenchmarks.benchmarks.values()));
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

    public Experiment addExperiment(BiConsumer<Benchmark, BiConsumer<String, String>> experiment) {
        this.experiments.add(experiment);
        return this;
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
        this.experiments.add((benchmark, register) -> {
            try {
                List<String> result = experiment.right.run(benchmark);
                List<String> names = experiment.left;
                assert result.size() == names.size();
                for (int i = 0; i < names.size(); i++) {
                    register.accept(names.get(i), result.get(i));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }



    public Table calculate() {
        return calculate(1);
    }

    public Table calculate(int threads) {
        Table table = new Table();

        List<String> header = new ArrayList<>();
        header.add("Benchmark");
        Map<String, Integer> headerIndexes = new HashMap<>();

        table.addRow(header);

        ExecutorService pool = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < benchmarks.size(); i++) {
            int rowIndex = i + 1;
            Pair<String, Benchmark> benchmark = benchmarks.get(i);

            pool.submit(() -> {
                List<String> row = Collections.synchronizedList(new ArrayList<>());
                row.add(benchmark.getLeft());

                table.setRow(rowIndex, row);

                System.out.println("Running benchmark: " + benchmark.getLeft() + " (" + rowIndex + "/" + benchmarks.size() + ")");
                try {
                    for (BiConsumer<Benchmark, BiConsumer<String, String>> experiment : experiments) {
                        int tries = 0;
                        while (true) {
                            try {
                                experiment.accept(benchmark.getRight(), (name, result) -> {
                                    int index;
                                    if (headerIndexes.containsKey(name)) {
                                        index = headerIndexes.get(name);
                                    } else {
                                        index = headerIndexes.size() + 1;
                                        headerIndexes.put(name, index);
                                        header.add(name);
                                    }
                                    while (row.size() <= index) {
                                        row.add("");
                                    }
                                    row.set(index, result);
                                });
                                break;
                            } catch (Throwable e) {
                                if (tries == 5) {
                                    throw new RuntimeException(e);
                                } else {
                                    tries++;
                                    System.out.println("Had an exception while running a benchmark (for the " + tries + ". time)");
                                    e.printStackTrace();
                                    System.out.println("Trying again");
                                }
                            }
                        }

                        System.out.println("\nSub result ready:");
                        System.out.println(table.toCSV());
                        System.out.println();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        }

        pool.shutdown();
        try {
            pool.awaitTermination(30, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        return table;
    }
}
