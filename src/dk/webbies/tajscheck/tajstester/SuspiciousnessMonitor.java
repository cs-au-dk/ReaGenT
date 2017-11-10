package dk.webbies.tajscheck.tajstester;

import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.jsnodes.Node;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.monitoring.DefaultAnalysisMonitoring;
import dk.brics.tajs.solver.NodeAndContext;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static dk.brics.tajs.util.Collections.newMap;
import static dk.brics.tajs.util.Collections.newSet;

public class SuspiciousnessMonitor extends DefaultAnalysisMonitoring {

    private Map<Test, Set<NodeAndContext<Context>>> suspiciousLocations = newMap();

    private TajsTypeTester tester;
    private BiConsumer<Test, Set<NodeAndContext<Context>>> notify;

    private NodeAndContext<Context> currentBc;

    public SuspiciousnessMonitor(TajsTypeTester tester, BiConsumer<Test, Set<NodeAndContext<Context>>> notify) {
        this.tester = tester;
        this.notify = notify;
    }

    public Map<Test, Set<NodeAndContext<Context>>> getSuspiciousLocations() {
        return suspiciousLocations;
    }


    @Override
    public void visitNodeTransferPre(AbstractNode n, State s) {
        this.currentBc = new NodeAndContext<Context>(n, s.getContext());
    }


    @Override
    public void visitCall(AbstractNode n, Value funval) {
        if(currentBc != null) {
            updateSuspicousnessInfo(currentBc, funval);
        }
    }

    @Override
    public void visitPropertyRead(AbstractNode n, Set<ObjectLabel> objs, PKeys propertystr, State state, boolean check_unknown) {
        if(currentBc != null && objs != null) {
            updateSuspicousnessInfo(currentBc, Value.makeObject(objs));
        }
    }

    @Override
    public void visitPropertyWrite(Node n, Set<ObjectLabel> objs, PKeys propertystr) {
        if(currentBc != null && objs != null) {
            updateSuspicousnessInfo(currentBc, Value.makeObject(objs));
        }
    }

    private void updateSuspicousnessInfo(NodeAndContext<Context> nc, Value v) {
        if(hasSuspiciousPrecisionLoss(v) && tester.getSensitivity() != null && tester.getSensitivity().isTestContext(nc.getContext())) {
            Test t = tester.getSensitivity().getTest(nc.getContext());
            suspiciousLocations.putIfAbsent(t, newSet());
            Set<NodeAndContext<Context>> snc = suspiciousLocations.get(t);
            snc.add(nc);
            notify.accept(t, snc);
        }
    }


    private boolean hasSuspiciousPrecisionLoss(Value v) {
        int native_functions = 0;
        int user_functions = 0;
        for (ObjectLabel objlabel : v.getObjectLabels()) {
            if (objlabel.getKind() == ObjectLabel.Kind.FUNCTION) {
                if (objlabel.isHostObject()) {
                    native_functions++;
                } else {
                    user_functions++;
                }
                if (native_functions >= 2 && user_functions >= 1) {
                    return true;
                }
            }
        }
        return false;
    }

}
