package dk.webbies.tajscheck.benchmarks;

import dk.webbies.tajscheck.parsespec.ParseDeclaration;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by erik1 on 01-11-2016.
 */
public class Benchmark {
    public final ParseDeclaration.Environment environment;
    public final String jsFile;
    public final String dTSFile;
    public final String module;
    public final Collection<String> pathsToTest;
    public final LOAD_METHOD load_method;

    public Benchmark(ParseDeclaration.Environment environment, String jsFile, String dTSFile, String module, LOAD_METHOD load_method) {
        this(environment, jsFile, dTSFile, module, load_method, null);
    }

    private Benchmark(ParseDeclaration.Environment environment, String jsFile, String dTSFile, String module, LOAD_METHOD load_method, Collection<String> pathsToTest) {
        this.environment = environment;
        this.jsFile = jsFile;
        this.dTSFile = dTSFile;
        this.module = module;
        this.pathsToTest = pathsToTest;
        this.load_method = load_method;
    }

    public Benchmark withPathsToTest(Collection<String> pathsToTest) {
        return new Benchmark(
                this.environment,
                this.jsFile,
                this.dTSFile,
                this.module,
                load_method, Collections.unmodifiableCollection(pathsToTest)
        );
    }

    public Benchmark withLoadMethod(LOAD_METHOD method) {
        return new Benchmark(
                this.environment,
                this.jsFile,
                this.dTSFile,
                this.module,
                method,
                this.pathsToTest
        );
    }

    public enum LOAD_METHOD {
        REQUIRE,
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
