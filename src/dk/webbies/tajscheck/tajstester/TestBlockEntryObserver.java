package dk.webbies.tajscheck.tajstester;

import dk.brics.tajs.solver.GenericSolver;

public interface TestBlockEntryObserver {
    void onTestBlockEntry(GenericSolver.SolverInterface c);
}
