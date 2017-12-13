package dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy;

import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.solver.GenericSolver;
import dk.webbies.tajscheck.testcreator.test.FunctionTest;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Collection;

public interface ExpansionPolicy {
    void nextRound();

    boolean include(FunctionTest test);

    Collection<Test> getTestsToPerformAnyway(Solver.SolverInterface c);
}
