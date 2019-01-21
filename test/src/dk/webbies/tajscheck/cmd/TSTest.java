package dk.webbies.tajscheck.cmd;

import dk.webbies.tajscheck.DynamicMain;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.paser.AST.DynamicAccessExpression;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.tajs.analyze.AnalyzeBenchmarks;
import dk.webbies.tajscheck.test.tajs.analyze.CompareModesEvaluation;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TSTest implements Main.Command {
    @Option(name = "-jsfile", aliases = { "-js" },
            usage = "The JavaScript file that implements the library.")
    private String jsFile = null;

    @Option(name = "-dtsfile", aliases = { "-ts" },
            usage = "The TypeScript declaration file that describes the library.")
    private String dTsFile = null;

    @Option(name = "-benchmark", aliases = { "-bench" },
            usage = "The name of the benchmark to analyze (overrides -jsfile, -dtsfile and -env).")
    private String bench = null;

    @Option(name = "-env", aliases = { "-e" },
            usage = "Selects if the library runs in a library or node environment.")
    private ReaGenT.Env environment = ReaGenT.Env.NODE;
    public enum Env {
        BROWSER,
        NODE
    }

    public static void printHelp() {
        System.out.println("./main.sh tstest [options] \n" +
                "\n" +
                "The available options are listed below.  \n" +
                "Either a benchmark or a declaration file + JavaScript file must be specified.");
        System.out.println();
        new CmdLineParser(new ReaGenT()).printUsage(System.out);
    }

    @Override
    public void run() throws Throwable {
        if (bench != null) {
            List<Map.Entry<String, Benchmark>> matchingBench = RunBenchmarks.benchmarks.entrySet().stream().filter(entry -> entry.getKey().equalsIgnoreCase(bench)).collect(Collectors.toList());
            if (matchingBench.isEmpty()) {
                System.out.println("No benchmark found named: " + bench);
                return;
            }
            Benchmark benchmark = matchingBench.get(0).getValue();
            runBench(benchmark);
            return;
        }
        if (jsFile == null || dTsFile == null) {
            System.out.println("You must specify either a benchmark to run, or both a JavaScript file and TypeScript declaration file.");
            System.out.println();
            printHelp();
            return;
        }

        if (!new File(jsFile).exists()) {
            System.out.println("Could not find the JavaScript implementation on " + jsFile);
            return;
        }
        if (!new File(dTsFile).exists()) {
            System.out.println("Could not find the TypeScript declaration on " + dTsFile);
            return;
        }

        Benchmark benchmark = new Benchmark(bench != null ? bench : jsFile, ParseDeclaration.Environment.ES6DOM, jsFile, dTsFile, environment == ReaGenT.Env.NODE ? Benchmark.RUN_METHOD.NODE : Benchmark.RUN_METHOD.BROWSER, CheckOptions.builder()
                // The defaults from RunBenchmarks.
                .setSplitUnions(false)
                .setCompactOutput(true).build()
        );

        runBench(benchmark);
    }

    private void runBench(Benchmark benchmark) throws Exception {
        DynamicMain.writeFullDriver(benchmark);
        String result = DynamicMain.runBenchmark(benchmark);
        System.out.println(result);
        System.exit(0);
    }
}
