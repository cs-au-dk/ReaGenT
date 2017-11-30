package dk.webbies.tajscheck.benchmark.options.staticOptions;

import dk.brics.tajs.lattice.Context;
import dk.brics.tajs.solver.NodeAndContext;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.HashSet;
import java.util.Set;

public class LimitTransfersRetractionPolicy implements RetractionPolicy {
    private final int maxTransfers;
    private int maxSuspiciousLocations;
    private final Set<Test> retracted = new HashSet<>();
    private final Set<Test> timeouts = new HashSet<>();

    public LimitTransfersRetractionPolicy(int maxTransfers, int maxSuspiciousLocations) {
        this.maxTransfers = maxTransfers;
        this.maxSuspiciousLocations = maxSuspiciousLocations;
    }

    @Override
    public void notifyTestTransfer(Test test, int totalTransfers) {
        if (totalTransfers > maxTransfers) {
            timeouts.add(test);
        }
    }

    @Override
    public void notifySuspiciousLocation(Test test, Set<NodeAndContext<Context>> location) {
        if (location.size() > maxSuspiciousLocations) {
            retracted.add(test);
        }
    }

    @Override
    public boolean isRetracted(Test test) {
        return retracted.contains(test);
    }

    @Override
    public boolean isTimeout(Test test) {
        return timeouts.contains(test);
    }
}
