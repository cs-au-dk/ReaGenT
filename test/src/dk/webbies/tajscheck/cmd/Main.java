package dk.webbies.tajscheck.cmd;

import dk.webbies.tajscheck.DynamicMain;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;

import java.io.File;

public class Main {
    @Argument(handler = SubCommandHandler.class)
    @SubCommands({
            // TODO: A TSTest command, a ReaGenT command, and a quicktest command.
            @SubCommand(name = "quicktest", impl = QuickTest.class)
    })
    Command cmd;


    public static void main(String[] args) throws Throwable {
        Main main = new Main();
        final CmdLineParser parser = new CmdLineParser(main);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            parser.printUsage(System.out);
            return;
        }
        if (main.cmd == null) {
            parser.printUsage(System.out);
            return;
        }
        main.cmd.run();
    }

    public interface Command {
        void run() throws Throwable;
    }

    public static final class QuickTest implements Command {

        @Override
        public void run() throws Throwable {
            System.out.println("Doing the quicktest");
            Benchmark bench = RunBenchmarks.benchmarks.get("classnames");

            System.out.println("Clearing cache");
            if (new File("cache/").exists()) {
                if (!new File("cache/").isDirectory()) {
                    throw new RuntimeException("Cache was not a directory");
                }
                //noinspection ConstantConditions
                for (File file : new File("cache/").listFiles()) {
                    if (file.isFile()) {
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    }
                }
            }

            System.out.println("Parsing the benchmark");
            BenchmarkInfo info = BenchmarkInfo.create(bench);

            System.out.println("Running TSTest");
            DynamicMain.writeFullDriver(bench);
            String output = DynamicMain.runBenchmark(bench);
            System.out.println(output);

            System.out.println("Running ReaGenT");
            TAJSUtil.TajsAnalysisResults ReaGenTResult = TAJSUtil.runNoDriver(bench, 10 * 60);
            System.out.println(ReaGenTResult);

            System.out.println("Success! Everything seems to work!");
        }
    }



}
