package dk.webbies.tajscheck.benchmark.options.staticOptions;

import dk.webbies.tajscheck.testcreator.test.FunctionTest;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Collection;
import java.util.Collections;

public class ExpandImmediatelyPolicy implements ExpansionPolicy {
    @Override
    public void nextRound() {

    }

    @Override
    public boolean include(FunctionTest test) {
        return true;
    }

    @Override
    public Collection<Test> getTestsToPerformAnyway() {
        return Collections.emptyList();
    }
}
