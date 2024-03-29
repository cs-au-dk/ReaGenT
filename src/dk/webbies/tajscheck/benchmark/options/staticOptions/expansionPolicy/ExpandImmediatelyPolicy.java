package dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy;

import dk.brics.tajs.analysis.Solver;
import dk.webbies.tajscheck.tajstester.TajsTypeTester;
import dk.webbies.tajscheck.testcreator.test.FunctionTest;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Collection;
import java.util.Collections;

public class ExpandImmediatelyPolicy implements ExpansionPolicy {
    @Override
    public void nextRound() {

    }

    @Override
    public boolean expandTo(FunctionTest test, TajsTypeTester typeTester) {
        return true;
    }

    @Override
    public Collection<Test> getTestsToPerformAnyway(Solver.SolverInterface c) {
        return Collections.emptyList();
    }
}
