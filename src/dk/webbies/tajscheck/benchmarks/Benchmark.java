package dk.webbies.tajscheck.benchmarks;

import dk.webbies.tajscheck.parsespec.ParseDeclaration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by erik1 on 01-11-2016.
 */
public class Benchmark {
    public final ParseDeclaration.Environment environment;
    public final String jsFile;
    public final String dTSFile;
    public final String module;
    public final Collection<String> pathsToTest;
    public final RUN_METHOD run_method;
    public final boolean useTAJS;
    public final CheckOptions options;
    private final List<Benchmark> dependencies;

    public Benchmark(ParseDeclaration.Environment environment, String jsFile, String dTSFile, String module, RUN_METHOD load_method, CheckOptions options) {
        this(environment, jsFile, dTSFile, module, load_method, null, false, options, new ArrayList<>());
    }

    private Benchmark(ParseDeclaration.Environment environment, String jsFile, String dTSFile, String module, RUN_METHOD load_method, Collection<String> pathsToTest, boolean withTAJS, CheckOptions options, List<Benchmark> dependencies) {
        this.environment = environment;
        this.jsFile = jsFile;
        this.dTSFile = dTSFile;
        this.module = module;
        this.pathsToTest = pathsToTest;
        this.run_method = load_method;
        this.useTAJS = withTAJS;
        this.options = options;
        this.dependencies = dependencies;
    }

    public Benchmark withPathsToTest(Collection<String> pathsToTest) {
        return new Benchmark(
                this.environment,
                this.jsFile,
                this.dTSFile,
                this.module,
                run_method, Collections.unmodifiableCollection(pathsToTest),
                this.useTAJS,
                options, dependencies);
    }

    public Benchmark withRunMethod(RUN_METHOD method) {
        return new Benchmark(
                this.environment,
                this.jsFile,
                this.dTSFile,
                this.module,
                method,
                this.pathsToTest,
                this.useTAJS,
                options, dependencies);
    }

    public Benchmark useTAJS() {
        return new Benchmark(
                this.environment,
                this.jsFile,
                this.dTSFile,
                this.module,
                this.run_method,
                this.pathsToTest,
                true,
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

    public enum RUN_METHOD {
        NODE,
        BROWSER,
        BOOTSTRAP
    }

    @Override
    public String toString() {
        return "Benchmark{" +
                 dTSFile +
                '}';
    }
}
