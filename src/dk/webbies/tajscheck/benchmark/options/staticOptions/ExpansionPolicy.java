package dk.webbies.tajscheck.benchmark.options.staticOptions;

import dk.webbies.tajscheck.testcreator.test.FunctionTest;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Collection;

public interface ExpansionPolicy {
    void nextRound();

    boolean include(FunctionTest test);

    Collection<Test> getTestsToPerformAnyway();
}
