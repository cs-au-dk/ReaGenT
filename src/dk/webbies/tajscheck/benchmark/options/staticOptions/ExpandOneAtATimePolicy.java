package dk.webbies.tajscheck.benchmark.options.staticOptions;

import dk.webbies.tajscheck.testcreator.test.FunctionTest;

import java.util.HashSet;
import java.util.Set;

public class ExpandOneAtATimePolicy implements ExpansionPolicy {
    private final Set<FunctionTest> whitelist = new HashSet<>();

    boolean clean = true;

    @Override
    public void nextRound() {
        clean = true;
    }

    @Override
    public boolean include(FunctionTest test) {
        if (whitelist.contains(test)) {
            return true;
        }
        if (clean) {
            whitelist.add(test);
            clean = false;
            return true;
        }
        return false;
    }
}
