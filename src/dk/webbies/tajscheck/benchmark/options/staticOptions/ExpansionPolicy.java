package dk.webbies.tajscheck.benchmark.options.staticOptions;

import dk.webbies.tajscheck.testcreator.test.FunctionTest;

public interface ExpansionPolicy {
    void nextRound();

    boolean include(FunctionTest test);
}
