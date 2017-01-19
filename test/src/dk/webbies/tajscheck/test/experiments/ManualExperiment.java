package dk.webbies.tajscheck.test.experiments;

import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;
import org.junit.Test;

import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 19-01-2017.
 */
public class ManualExperiment {
    private static final Scanner scanner = new Scanner(System.in);
    private static final int TIMEOUT = 60 * 1000;

    private static void fillWithRandomTypeErrors(BlockingQueue<Pair<String, OutputParser.TypeError>> queue) throws Exception {
        //noinspection InfiniteLoopStatement
        MultiMap<String, OutputParser.TypeError> errors = new ArrayListMultiMap<>();
        List<Pair<String, Benchmark>> benchmarks = RunBenchmarks.benchmarks.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue())).collect(Collectors.toList());

        while (true) {
            Pair<String, Benchmark> benchmarkPair = Util.selectRandom(benchmarks);
            String name = benchmarkPair.getLeft();
            Benchmark benchmark = benchmarkPair.getRight();
            if (errors.containsKey(name)) {
                if (!errors.get(name).isEmpty()) {
                    OutputParser.TypeError result = Util.selectRandom(errors.get(name));
                    if (result != null) {
                        queue.put(new Pair<>(name, result));
                    }
                }
                continue;
            }
            try {
                Main.writeFullDriver(benchmark);
                OutputParser.RunResult result = OutputParser.parseDriverResult(Main.runBenchmark(benchmark, TIMEOUT));
                errors.putAll(name, result.typeErrors);
                if (!result.typeErrors.isEmpty()) {
                    queue.put(new Pair<>(name, Util.selectRandom(result.typeErrors)));
                }
            } catch (Exception e) {
                e.printStackTrace();
                errors.put(name, null);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        BlockingQueue<Pair<String, OutputParser.TypeError>> queue = new LinkedBlockingQueue<>(10);
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
            System.out.println("Press G for good, B for bad, or U for unknown/unclassifiable (and E to exit)");
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
