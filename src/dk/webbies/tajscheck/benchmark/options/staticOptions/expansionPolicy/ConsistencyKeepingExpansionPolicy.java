package dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy;

import dk.brics.tajs.analysis.Solver;
import dk.webbies.tajscheck.tajstester.TajsTypeTester;
import dk.webbies.tajscheck.testcreator.test.FunctionTest;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConsistencyKeepingExpansionPolicy implements ExpansionPolicy {
    private final ExpansionPolicy delegate;
    private final Set<Test> alreadyExpandedTo = new HashSet<>();

    public ConsistencyKeepingExpansionPolicy(ExpansionPolicy delegate) {
        this.delegate = delegate;
    }

    @Override
    public void nextRound() {
        delegate.nextRound();
    }

    @Override
    public boolean expandTo(FunctionTest test, TajsTypeTester typeTester) {
        //noinspection SimplifiableIfStatement
        if (alreadyExpandedTo.contains(test)) {
            return true;
        }
        return delegate.expandTo(test, typeTester);
    }

    @Override
    public Collection<Test> getTestsToPerformAnyway(Solver.SolverInterface c) {
        Collection<Test> result = delegate.getTestsToPerformAnyway(c);
        if (result != null) {
            alreadyExpandedTo.addAll(result);
        }
        return result;
    }
}
