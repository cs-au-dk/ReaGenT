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
import java.util.ArrayList;
import java.util.List;

public class Main {
    @Argument(handler = SubCommandHandler.class)
    @SubCommands({
            @SubCommand(name = "quicktest", impl = QuickTest.class),
            @SubCommand(name = "help", impl = Help.class),
            @SubCommand(name = "experiment", impl = Experiments.class),
            @SubCommand(name = "reagent", impl = ReaGenT.class),
            @SubCommand(name = "tstest", impl = TSTest.class)
    })
    public Command cmd;


    public static void main(String[] args) throws Throwable {
        Main main = new Main();
        final CmdLineParser parser = new CmdLineParser(main);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.out.println(e.getMessage());
            return;
        }
        if (main.cmd == null) {
            System.out.println(
                    "./main.sh <command> [<args>] \n" +
                            "\n" +
                            "The following commands are available: \n" +
                            "    help <command>      Prints a help message for a specific command\n" +
                            "    quicktest           Does a quick test of TSTest and ReaGenT. Should print a success message after running.\n" +
                            "    experiment <name>   Performs a named experiment\n" +
                            "    reagent [<args>]    Runs ReaGenT on some library\n" +
                            "    tstest [<args>]     Runs TSTest on some library"
            );
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


    public static class Help implements Command {
        private String helpText =
                "Usage: \n" +
                        "    ./main.sh help <command>\n" +
                        "    \n" +
                        "E.g.\n" +
                        "    ./main help reagent";

        @Argument
        public List<String> arguments = new ArrayList<>();


        @Override
        public void run() throws Throwable {
            if (arguments.isEmpty()) {
                System.out.println(helpText);
                return;
            }
            String command = arguments.get(0);
            switch (command.toLowerCase()) {
                case "reagent":
                    ReaGenT.printHelp();
                    break;
                case "tstest":
                    TSTest.printHelp();
                    break;
                case "help":
                    System.out.println(helpText);
                    return;
                case "experiment":
                    Experiments.printHelp();
                    break;
                case "quicktest":
                    System.out.println("Usage: \n" +
                            "    ./main.sh quicktest\n" +
                            "    \n" +
                            "Should print a success message within a minute.");
                    break;
                default:
                    System.out.println("Could not recognize command with name " + command + ".");
            }
        }
    }


}
