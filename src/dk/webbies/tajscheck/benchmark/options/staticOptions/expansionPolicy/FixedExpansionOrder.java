package dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy;

import dk.brics.tajs.analysis.Solver;
import dk.webbies.tajscheck.tajstester.TajsTypeTester;
import dk.webbies.tajscheck.testcreator.test.FunctionTest;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FixedExpansionOrder implements ExpansionPolicy {
    private final List<String> executionOrder;
    private int index = 0;

    public FixedExpansionOrder(String... executionOrder) {
        this(Arrays.asList(executionOrder));
    }

    public FixedExpansionOrder(List<String> executionOrder) {
        this.executionOrder = executionOrder;
    }

    @Override
    public void nextRound() {
        index++;
    }

    @Override
    public boolean expandTo(FunctionTest test, TajsTypeTester typeTester) {
        assert executionOrder.contains(test.getPath());
        List<String> subList = executionOrder.subList(0, Math.min(index, executionOrder.size()));
        return subList.contains(test.getPath());
    }

    @Override
    public Collection<Test> getTestsToPerformAnyway(Solver.SolverInterface c) {
        return Collections.emptyList();
    }
}
