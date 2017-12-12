package dk.webbies.tajscheck.tajstester;

import dk.brics.tajs.analysis.Transfer;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.jsnodes.EventDispatcherNode;
import dk.brics.tajs.lattice.Context;
import dk.brics.tajs.lattice.State;
import dk.webbies.tajscheck.testcreator.test.Test;

public class TesterTransfer extends Transfer {
    @Override
    public Context transfer(BasicBlock src, BasicBlock dst, State state) {
        AbstractNode n = src.getLastNode();

        if (n instanceof EventDispatcherNode && ((EventDispatcherNode) n).getType() == EventDispatcherNode.Type.TYPE_TESTS) {// we want flow for the "all-tests" context
            // But we also want to kill any test-context when moving outside of the testing block
            if(TesterContextSensitivity.isTestContext(state.getContext())) return null;
            return state.getContext();
        }

        return super.transfer(src, dst, state);
    }
}
