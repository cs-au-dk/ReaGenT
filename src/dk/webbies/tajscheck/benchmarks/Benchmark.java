package dk.webbies.tajscheck.benchmarks;

import dk.webbies.tajscheck.parsespec.ParseDeclaration;

/**
 * Created by erik1 on 01-11-2016.
 */
public class Benchmark {
    public final ParseDeclaration.Environment environment;
    public final String jsFile;
    public final String dTSFile;
    public final String module;

    public Benchmark(ParseDeclaration.Environment environment, String jsFile, String dTSFile, String module) {
        this.environment = environment;
        this.jsFile = jsFile;
        this.dTSFile = dTSFile;
        this.module = module;
    }
}
