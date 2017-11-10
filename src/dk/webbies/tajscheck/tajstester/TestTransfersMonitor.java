package dk.webbies.tajscheck.tajstester;

import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.monitoring.DefaultAnalysisMonitoring;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Map;
import java.util.function.BiConsumer;

import static dk.brics.tajs.util.Collections.newMap;

public class TestTransfersMonitor extends DefaultAnalysisMonitoring {
    private boolean DEBUG = true;

    private Map<Test, Integer> testsTransfer = newMap();

    private TajsTypeTester tester;
    private BiConsumer<Test, Integer> notify;

    public TestTransfersMonitor(TajsTypeTester tester, BiConsumer<Test, Integer> notify) {
        this.tester = tester;
        this.notify = notify;
    }

    public Map<Test, Integer> getTestTransfers() {
        return testsTransfer;
    }

    @Override
    public void visitBlockTransferPre(BasicBlock b, State s) {
        if (s.getSolverInterface().isScanning()) {
            return;
        }
        if(tester.getSensitivity() != null && tester.getSensitivity().isTestContext(s.getContext())) {
            Test t = tester.getSensitivity().getTest(s.getContext());
            int newTransfer = testsTransfer.getOrDefault(t, 0) + 1;
            testsTransfer.put(t, newTransfer);
            notify.accept(t, newTransfer);
        }
    }
}
