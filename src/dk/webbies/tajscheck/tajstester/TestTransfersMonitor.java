package dk.webbies.tajscheck.tajstester;

import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.monitoring.DefaultAnalysisMonitoring;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Map;

import static dk.brics.tajs.util.Collections.newMap;

public class TestTransfersMonitor extends DefaultAnalysisMonitoring {
    private boolean DEBUG = true;

    private Map<Test, Integer> testsTransfer = newMap();

    private TajsTypeTester tester;

    public TestTransfersMonitor(TajsTypeTester tester) {
        this.tester = tester;
    }

    public Map<Test, Integer> getTestTransfers() {
        return testsTransfer;
    }

    @Override
    public void visitBlockTransferPre(BasicBlock b, State s) {
        if(tester.getSensitivity() != null && tester.getSensitivity().isTestContext(s.getContext())) {
            Test t = tester.getSensitivity().getTest(s.getContext());
            int curTransfer = testsTransfer.getOrDefault(t, 0);
            testsTransfer.put(t, curTransfer + 1);
        }
    }
}
