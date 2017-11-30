package dk.webbies.tajscheck.benchmark.options.staticOptions;

import dk.brics.tajs.lattice.Context;
import dk.brics.tajs.solver.NodeAndContext;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Set;

public interface RetractionPolicy {
    void notifyTestTransfer(Test test, int totalTransfers);

    void notifySuspiciousLocation(Test test, Set<NodeAndContext<Context>> location);

    boolean isRetracted(Test test);

    boolean isTimeout(Test test);
}
