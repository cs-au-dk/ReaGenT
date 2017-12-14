package dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy;

import dk.brics.tajs.analysis.Solver;
import dk.webbies.tajscheck.tajstester.TajsTypeTester;
import dk.webbies.tajscheck.testcreator.test.FunctionTest;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.*;

public class ExpandOneAtATimeWhenWorkListEmpty implements ExpansionPolicy {

    private Test nextFunction = null;

    @Override
    public void nextRound() {
        this.nextFunction = null;
    }

    @Override
    public boolean expandTo(FunctionTest test, TajsTypeTester typeTester) {
        this.nextFunction = test;
        return false;
    }

    @Override
    public Collection<Test> getTestsToPerformAnyway(Solver.SolverInterface c) {
        if (!c.getWorklist().isEmpty() || this.nextFunction == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(this.nextFunction);
    }
}
