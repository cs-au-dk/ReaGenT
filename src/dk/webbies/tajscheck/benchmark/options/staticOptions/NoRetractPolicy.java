package dk.webbies.tajscheck.benchmark.options.staticOptions;

import dk.brics.tajs.lattice.Context;
import dk.brics.tajs.solver.NodeAndContext;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Set;

public class NoRetractPolicy implements RetractionPolicy {
    @Override
    public void notifyTestTransfer(Test test, int totalTransfers) {

    }

    @Override
    public void notifySuspiciousLocation(Test test, Set<NodeAndContext<Context>> location) {

    }

    @Override
    public boolean isRetracted(Test test) {
        return false;
    }

    @Override
    public boolean isTimeout(Test test) {
        return false;
    }
}
