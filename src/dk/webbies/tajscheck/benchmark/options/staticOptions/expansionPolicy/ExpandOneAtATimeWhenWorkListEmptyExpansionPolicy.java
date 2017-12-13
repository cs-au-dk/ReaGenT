package dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy;

import dk.brics.tajs.analysis.Solver;
import dk.webbies.tajscheck.testcreator.test.FunctionTest;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.*;

public class ExpandOneAtATimeWhenWorkListEmptyExpansionPolicy implements ExpansionPolicy {

    private Test nextFunction = null;

    Set<Test> included = new HashSet<>();

    @Override
    public void nextRound() {
        this.nextFunction = null;
    }

    @Override
    public boolean include(FunctionTest test) {
        if (included.contains(test)) {
            return true;
        }
        this.nextFunction = test;
        return false;
    }

    @Override
    public Collection<Test> getTestsToPerformAnyway(Solver.SolverInterface c) {
        if (!c.getWorklist().isEmpty() || this.nextFunction == null) {
            return Collections.emptyList();
        }
        this.included.add(this.nextFunction);
        return Collections.singletonList(this.nextFunction);
    }
}
