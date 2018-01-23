package dk.webbies.tajscheck.benchmark;

import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.benchmark.options.OptionsI;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.tajstester.ViolationsOracle;
import dk.webbies.tajscheck.testcreator.TestCreator;
import dk.webbies.tajscheck.util.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 01-11-2016.
 */
public class Benchmark {
    public final String name;
    public final ParseDeclaration.Environment environment;
    public final String jsFile;
    public final String dTSFile;
    public final String violationsOracleFile;
    public final Set<String> pathsToTest;
    public final RUN_METHOD run_method;
    public final CheckOptions options;
    public final String exportName;
    private final List<Benchmark> dependencies;

    public Benchmark(String name, ParseDeclaration.Environment environment, String jsFile, String dTSFile, RUN_METHOD load_method, CheckOptions options, String exportName, String violationsOracleFile) {
        this(name, environment, jsFile, dTSFile, load_method, null, options, new ArrayList<>(), exportName, violationsOracleFile);
    }

    public Benchmark(String name, ParseDeclaration.Environment environment, String jsFile, String dTSFile, RUN_METHOD load_method, CheckOptions options, String exportName) {
        this(name, environment, jsFile, dTSFile, load_method, null, options, new ArrayList<>(), exportName, null);
    }

    public Benchmark(String name, ParseDeclaration.Environment environment, String jsFile, String dTSFile, RUN_METHOD load_method, CheckOptions options) {
        this(name, environment, jsFile, dTSFile, load_method, options, null, null);
    }

    private Benchmark(String name, ParseDeclaration.Environment environment, String jsFile, String dTSFile, RUN_METHOD load_method, Set<String> pathsToTest, CheckOptions options, List<Benchmark> dependencies, String exportName, String violationsOracleFile) {
        this.name = name;
        this.environment = environment;
        this.dTSFile = dTSFile;
        this.pathsToTest = pathsToTest;
        this.run_method = load_method;
        this.options = options;
        this.dependencies = dependencies;
        this.exportName = exportName;
        if (options.useTracified && !jsFile.contains("tracified")) {
            this.jsFile = Util.removeSuffix(jsFile, ".js") + "-tracified.js";
            assert new File(this.jsFile).exists();
        } else {
            this.jsFile = jsFile;
        }
        if(violationsOracleFile != null) {
            this.violationsOracleFile = violationsOracleFile;
        }
        else {
            this.violationsOracleFile = Paths.get(dTSFile).getParent().resolve("suppressed.json").toString();
        }
    }

    public Benchmark withPathsToTest(Collection<String> pathsToTest) {
        return new Benchmark(
                this.name,
                this.environment,
                this.jsFile,
                this.dTSFile,
                this.run_method,
                Collections.unmodifiableSet(pathsToTest.stream().map(Util::simplifyPath).collect(Collectors.toSet())),
                this.options,
                this.dependencies,
                this.exportName,
                this.violationsOracleFile);
    }

    public Benchmark withRunMethod(RUN_METHOD method) {
        return new Benchmark(
                this.name,
                this.environment,
                this.jsFile,
                this.dTSFile,
                method,
                this.pathsToTest,
                this.options,
                this.dependencies,
                this.exportName,
                this.violationsOracleFile);
    }

    public Benchmark addDependencies(Benchmark... benchmarks) {
        assert this.run_method == RUN_METHOD.BROWSER; // <- Only works for this one.
        if (benchmarks.length == 0) {
            throw new RuntimeException();
        }
        ArrayList<Benchmark> dependencies = new ArrayList<>(this.dependencies);
        for (Benchmark benchmark : benchmarks) {
            if (benchmark == null) {
                throw new RuntimeException();
            }
            dependencies.add(benchmark);
        }

        return this.withDependencies(dependencies);
    }

    private Benchmark withDependencies(List<Benchmark> dependencies) {
        return new Benchmark(
                this.name,
                this.environment,
                this.jsFile,
                this.dTSFile,
                this.run_method,
                this.pathsToTest,
                this.options,
                dependencies,
                this.exportName,
                this.violationsOracleFile
        );
    }

    public List<Benchmark> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    public Benchmark withOptions(CheckOptions options) {
        return new Benchmark(
                this.name,
                this.environment,
                this.jsFile,
                this.dTSFile,
                this.run_method,
                this.pathsToTest,
                options,
                this.dependencies,
                this.exportName,
                this.violationsOracleFile
        );
    }

    public Benchmark withOptions(CheckOptions.Builder options) {
        return withOptions(options.build());
    }

    public Benchmark withOptions(Function<CheckOptions.Builder, ? extends OptionsI.Builder> transformer) {
        return new Benchmark(
                this.name,
                this.environment,
                this.jsFile,
                this.dTSFile,
                this.run_method,
                this.pathsToTest,
                transformer.apply(this.options.getBuilder()).build(),
                this.dependencies,
                this.exportName,
                this.violationsOracleFile
        );
    }

    public Benchmark withDecl(String dTSFile) {
        return new Benchmark(
                this.name,
                this.environment,
                this.jsFile,
                dTSFile,
                this.run_method,
                this.pathsToTest,
                this.options,
                this.dependencies,
                this.exportName,
                this.violationsOracleFile
        );
    }

    public Benchmark withName(String name) {
        return new Benchmark(
                name,
                this.environment,
                this.jsFile,
                this.dTSFile,
                this.run_method,
                this.pathsToTest,
                this.options,
                this.dependencies,
                this.exportName,
                this.violationsOracleFile
        );
    }

    public Benchmark withJsFile(String jsFile) {
        return new Benchmark(
                this.name,
                this.environment,
                jsFile,
                this.dTSFile,
                this.run_method,
                this.pathsToTest,
                this.options,
                this.dependencies,
                this.exportName,
                this.violationsOracleFile
        );
    }

    public Benchmark withViolationsOracle(String violationsOracleFile) {
        return new Benchmark(
                this.name,
                this.environment,
                jsFile,
                this.dTSFile,
                this.run_method,
                this.pathsToTest,
                this.options,
                this.dependencies,
                this.exportName,
                violationsOracleFile
        );
    }

    public Benchmark patched() {
        Path dtspath = Paths.get(this.dTSFile);
        Path entryPath = Paths.get(this.jsFile);
        String patched = dtspath.getParent().resolve("patched." + dtspath.getFileName()).toString();
        String patchedSuppressed = dtspath.getParent().resolve("patched.suppressed.json").toString();
        if (!new File(patched).exists()) {
            return null;
        } else {
            String patchedEntry = entryPath.getParent().resolve("patched." + entryPath.getFileName()).toString();
            return this
                    .withDecl(patched)
                    .withViolationsOracle(patchedSuppressed)
                    .withJsFile(patchedEntry);
        }
    }

    public static Benchmark fromTSFile(String tsFile, String name, ParseDeclaration.Environment environment, RUN_METHOD run_method, CheckOptions options) throws IOException {
        Util.runNodeScript(" ts-spec-reader/node_modules/typescript/lib/tsc.js " + tsFile, 60 * 1000);
        Util.runNodeScript(" ts-spec-reader/node_modules/typescript/lib/tsc.js -d " + tsFile, 60 * 1000);
        String baseFileName = Util.removeSuffix(tsFile, ".ts");
        return new Benchmark(name, environment, baseFileName + ".js", baseFileName + ".d.ts", run_method, options);
    }

    public enum RUN_METHOD {
        NODE,
        BROWSER,
        BOOTSTRAP
    }

    public String getJSName() {
        return this.jsFile.substring(this.jsFile.lastIndexOf('/') + 1, this.jsFile.length());
    }

    @Override
    public String toString() {
        return "Benchmark{" +
                 name +
                '}';
    }
}
