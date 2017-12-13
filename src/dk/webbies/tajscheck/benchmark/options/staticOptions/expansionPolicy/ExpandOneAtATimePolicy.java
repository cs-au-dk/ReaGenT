package dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy;

import dk.brics.tajs.analysis.Solver;
import dk.webbies.tajscheck.testcreator.test.FunctionTest;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.util.Util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ExpandOneAtATimePolicy implements ExpansionPolicy {
    private final Set<String> whitelist = new HashSet<>();

    private boolean clean = true;

    @Override
    public void nextRound() {
        clean = true;
    }

    @Override
    public boolean include(FunctionTest test) {
        String path = Util.simplifyPath(test.getPath());
        if (whitelist.contains(path)) {
            return true;
        }
        if (clean) {
            whitelist.add(path);
            clean = false;
            return true;
        }
        return false;
    }

    @Override
    public Collection<Test> getTestsToPerformAnyway(Solver.SolverInterface c) {
        return Collections.emptyList();
    }
}
