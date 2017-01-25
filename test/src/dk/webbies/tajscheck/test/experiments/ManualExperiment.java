package dk.webbies.tajscheck.test.experiments;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.RunSmall;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 19-01-2017.
 */
public class ManualExperiment {
    private static final Scanner scanner = new Scanner(System.in);
    private static final int TIMEOUT = 60 * 1000;

    private static void fillWithRandomTypeErrors(BlockingQueue<Pair<String, OutputParser.TypeError>> queue) throws Exception {
        //noinspection InfiniteLoopStatement
        List<Pair<String, Benchmark>> benchmarks = RunBenchmarks.benchmarks.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue())).collect(Collectors.toList());

        while (true) {
            Pair<String, Benchmark> benchmarkPair = Util.selectRandom(benchmarks);
            String name = benchmarkPair.getLeft();
            Benchmark benchmark = benchmarkPair.getRight();
            try {
                OutputParser.RunResult result = getSomeResult(benchmark);
                if (!result.typeErrors.isEmpty()) {
                    queue.put(new Pair<>(name, Util.selectRandom(result.typeErrors)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static OutputParser.RunResult getSomeResult(Benchmark benchmark) throws Exception {
        if (new Random().nextBoolean()) {
            Main.writeFullDriver(benchmark);
            return OutputParser.parseDriverResult(Main.runBenchmark(benchmark, TIMEOUT));
        } else {
            List<OutputParser.RunResult> results = RunSmall.runSmallDrivers(benchmark, RunSmall.runDriver(benchmark.run_method, TIMEOUT), 5);
            return OutputParser.combine(results);
        }

    }

    public static void main(String[] args) throws Exception {
        BlockingQueue<Pair<String, OutputParser.TypeError>> queue = new LinkedBlockingQueue<>(100);
        Thread fillerThread = new Thread(() -> {
            try {
                fillWithRandomTypeErrors(queue);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

        fillerThread.start();

        int good = 0;
        int bad = 0;
        int unknown = 0;
        int nullChecks = 0;
        boolean exit = false;

        System.out.println("Setting up, now waiting for first type-error in the queue. ");
        while (true) {
            Pair<String, OutputParser.TypeError> error = queue.poll(30, TimeUnit.MINUTES);
            System.out.println("Got a type-error, because multi-threading, I recommend waiting a bit");
            System.out.println("Press enter to continue");
            scanner.nextLine();

            System.out.println();
            System.out.println("TypeError from " + error.getLeft());
            System.out.println(error.getRight());
            System.out.println();
            System.out.println("Press G for good, B for bad, U for unknown/unclassifiable, N for failure due to strict-null-checks (and E to exit)");
            while (true) {
                String line = scanner.nextLine().toUpperCase();
                switch (line) {
                    case "G":
                        good++;
                        break;
                    case "B":
                        bad++;
                        break;
                    case "U":
                        unknown++;
                        break;
                    case "N":
                        nullChecks++;
                        break;
                    case "E":
                        exit = true;
                        break;
                    default:
                        System.out.println("Unknown command: " + line + ", try again!");
                        continue;
                }
                break;
            }
            System.out.println("Results so far: ");
            System.out.println("Good: " + good);
            System.out.println("Bad: " + bad);
            System.out.println("Nulls: " + nullChecks);
            System.out.println("Unknown: " + unknown);
            System.out.println();
            if (exit) {
                fillerThread.interrupt();
                fillerThread.stop();
                break;
            }

        }
    }
}
