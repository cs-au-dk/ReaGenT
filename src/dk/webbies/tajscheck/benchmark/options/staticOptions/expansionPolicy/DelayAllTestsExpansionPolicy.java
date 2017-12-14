package dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy;

import dk.brics.tajs.analysis.Solver;
import dk.webbies.tajscheck.tajstester.TajsTypeTester;
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
    public boolean expandTo(FunctionTest test, TajsTypeTester typeTester) {
        toPerformAnyway.add(test);
        return false;
    }

    @Override
    public Collection<Test> getTestsToPerformAnyway(Solver.SolverInterface c) {
        return toPerformAnyway;
    }
}
