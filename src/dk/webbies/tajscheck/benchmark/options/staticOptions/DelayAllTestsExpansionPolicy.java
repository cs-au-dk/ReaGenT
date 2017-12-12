package dk.webbies.tajscheck.benchmark.options.staticOptions;

import dk.webbies.tajscheck.testcreator.test.FunctionTest;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DelayAllTestsExpansionPolicy implements ExpansionPolicy {

    private List<Test> toPerformAnyway = new ArrayList<>();

    @Override
    public void nextRound() {
        toPerformAnyway.clear();
    }

    @Override
    public boolean include(FunctionTest test) {
        toPerformAnyway.add(test);
        return false;
    }

    @Override
    public Collection<Test> getTestsToPerformAnyway() {
        return toPerformAnyway;
    }
}
