package dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy;

import dk.brics.tajs.analysis.Solver;
import dk.webbies.tajscheck.tajstester.TajsTypeTester;
import dk.webbies.tajscheck.testcreator.test.FunctionTest;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Collection;

public interface ExpansionPolicy {
    void nextRound();

    boolean expandTo(FunctionTest test, TajsTypeTester typeTester);

    Collection<Test> getTestsToPerformAnyway(Solver.SolverInterface c);
}
