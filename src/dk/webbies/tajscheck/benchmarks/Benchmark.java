package dk.webbies.tajscheck.benchmarks;

import dk.webbies.tajscheck.parsespec.ParseDeclaration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by erik1 on 01-11-2016.
 */
public class Benchmark {
    public final ParseDeclaration.Environment environment;
    public final String jsFile;
    public final String dTSFile;
    public final String module;
    public final Collection<String> pathsToTest;

    public Benchmark(ParseDeclaration.Environment environment, String jsFile, String dTSFile, String module) {
        this(environment, jsFile, dTSFile, module, null);
    }

    private Benchmark(ParseDeclaration.Environment environment, String jsFile, String dTSFile, String module, Collection<String> pathsToTest) {
        this.environment = environment;
        this.jsFile = jsFile;
        this.dTSFile = dTSFile;
        this.module = module;
        this.pathsToTest = pathsToTest;

    }

    public Benchmark withPathsToTest(Collection<String> pathsToTest) {
        return new Benchmark(
                this.environment,
                this.jsFile,
                this.dTSFile,
                this.module,
                Collections.unmodifiableCollection(pathsToTest)
        );
    }
}
