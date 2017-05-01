package dk.webbies.tajscheck.benchmark;

import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.testcreator.TestCreator;

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
    public final String module;
    public final Set<String> pathsToTest;
    public final RUN_METHOD run_method;
    public final CheckOptions options;
    private final List<Benchmark> dependencies;

    public Benchmark(String name, ParseDeclaration.Environment environment, String jsFile, String dTSFile, String module, RUN_METHOD load_method, CheckOptions options) {
        this(name, environment, jsFile, dTSFile, module, load_method, null, options, new ArrayList<>());
    }

    private Benchmark(String name, ParseDeclaration.Environment environment, String jsFile, String dTSFile, String module, RUN_METHOD load_method, Set<String> pathsToTest, CheckOptions options, List<Benchmark> dependencies) {
        this.name = name;
        this.environment = environment;
        this.jsFile = jsFile;
        this.dTSFile = dTSFile;
        this.module = module;
        this.pathsToTest = pathsToTest;
        this.run_method = load_method;
        this.options = options;
        this.dependencies = dependencies;
    }

    public Benchmark withPathsToTest(Collection<String> pathsToTest) {
        return new Benchmark(
                name, this.environment,
                this.jsFile,
                this.dTSFile,
                this.module,
                run_method,
                Collections.unmodifiableSet(pathsToTest.stream().map(TestCreator::simplifyPath).collect(Collectors.toSet())),
                options, dependencies);
    }

    public Benchmark withRunMethod(RUN_METHOD method) {
        return new Benchmark(
                name, this.environment,
                this.jsFile,
                this.dTSFile,
                this.module,
                method,
                this.pathsToTest,
                options, dependencies);
    }

    public Benchmark useTAJS() {
        return new Benchmark(
                name, this.environment,
                this.jsFile,
                this.dTSFile,
                this.module,
                this.run_method,
                this.pathsToTest,
                options, dependencies);
    }

    public Benchmark addDependencies(Benchmark... benchmarks) {
        assert this.run_method == RUN_METHOD.BROWSER; // <- Only works for this one.
        if (benchmarks.length == 0) {
            throw new RuntimeException();
        }
        Benchmark clone = withRunMethod(this.run_method);// <- Clone
        for (Benchmark benchmark : benchmarks) {
            if (benchmark == null) {
                throw new RuntimeException();
            }
            clone.dependencies.add(benchmark);
        }

        return clone;
    }

    public List<Benchmark> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    public Benchmark withOptions(CheckOptions options) {
        return new Benchmark(
                name, this.environment,
                this.jsFile,
                this.dTSFile,
                this.module,
                this.run_method,
                this.pathsToTest,
                options,
                this.dependencies
        );
    }

    public Benchmark withOptions(Function<CheckOptions, CheckOptions> transformer) {
        return new Benchmark(
                name, this.environment,
                this.jsFile,
                this.dTSFile,
                this.module,
                this.run_method,
                this.pathsToTest,
                transformer.apply(this.options),
                this.dependencies
        );
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
