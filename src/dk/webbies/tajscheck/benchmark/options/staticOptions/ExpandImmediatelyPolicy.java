package dk.webbies.tajscheck.benchmark.options.staticOptions;

import dk.webbies.tajscheck.testcreator.test.FunctionTest;

public class ExpandImmediatelyPolicy implements ExpansionPolicy {
    @Override
    public void nextRound() {

    }

    @Override
    public boolean include(FunctionTest test) {
        return true;
    }
}
