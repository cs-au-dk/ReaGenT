package dk.webbies.tajscheck.benchmark.options.staticOptions;

import dk.brics.tajs.lattice.Context;
import dk.brics.tajs.solver.NodeAndContext;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.HashSet;
import java.util.Set;

public class LimitTransfersRetractionPolicy implements RetractionPolicy {
    private final int maxTransfers;
    private final Set<Test> retracted = new HashSet<>();

    public LimitTransfersRetractionPolicy(int maxTransfers) {
        this.maxTransfers = maxTransfers;
    }

    @Override
    public void notifyTestTransfer(Test test, int totalTransfers) {
        if (totalTransfers > maxTransfers) {
            retracted.add(test);
        }
    }

    @Override
    public void notifySuspiciousLocation(Test test, Set<NodeAndContext<Context>> location) {

    }

    @Override
    public boolean isRetracted(Test test) {
        return retracted.contains(test);
    }
}
