package dk.webbies.tajscheck.benchmark.options.staticOptions;

import dk.webbies.tajscheck.testcreator.test.FunctionTest;

import java.util.Arrays;
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
    public boolean include(FunctionTest test) {
        assert executionOrder.contains(test.getPath());
        List<String> subList = executionOrder.subList(0, Math.min(index, executionOrder.size()));
        return subList.contains(test.getPath());
    }
}
