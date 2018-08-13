package dk.webbies.tajscheck.tajstester.monitors;

import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.Function;
import dk.brics.tajs.flowgraph.SourceLocation;
import dk.brics.tajs.flowgraph.jsnodes.*;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.monitoring.AnalysisPhase;
import dk.brics.tajs.monitoring.DefaultAnalysisMonitoring;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.solver.GenericSolver;
import dk.brics.tajs.util.Collectors;
import dk.webbies.tajscheck.tajstester.TesterContextSensitivity;
import dk.webbies.tajscheck.tajstester.typeCreator.SpecObjects;
import dk.webbies.tajscheck.util.Util;

import java.util.*;

public class ReadFromStdlibMonitor extends DefaultAnalysisMonitoring {
    private boolean libTestingInProgress = false;
    private final Set<AbstractNode> nodesReadDuringInit = new HashSet<>();
    private final Set<ReadPropertyNode> possiblyProblematicReads = new HashSet<>();

    @Override
    public void visitPropertyRead(AbstractNode n, Set<ObjectLabel> objs, PKeys propertyname, State state, boolean check_unknown) {
        if (n.getSourceLocation().toString().startsWith("HOST")) {
            return;
        }
        if (!libTestingInProgress) {
            nodesReadDuringInit.add(n);
        }
    }

    @Override
    public void visitReadProperty(ReadPropertyNode n, Set<ObjectLabel> objlabels, PKeys propertyname, boolean maybe, State state, Value v, ObjectLabel global_obj) {
        if (!libTestingInProgress) {
            nodesReadDuringInit.add(n);
            return;
        }
        if (nodesReadDuringInit.contains(n)) {
            return;
        }
//        if (!hasHostObject(v)) {
//            return;
//        }
        if (n.getSourceLocation().toString().startsWith("HOST")) {
            return;
        }
        if (objlabels.stream().noneMatch(Util.not(ReadFromStdlibMonitor::isClientDefined))) {
            return;
        }
        possiblyProblematicReads.add(n);
    }

    private static boolean isClientDefined(ObjectLabel label) {
        switch (label.getKind()) {
            case STRING:
            case BOOLEAN:
            case NUMBER:
                return false;
        }
        if (label.getHostObject() != null && label.getHostObject() instanceof SpecObjects.TypedObject) {
            return true;
        }
        if (label.isHostObject()) {
            return false;
        }

        return true;
    }

    private static boolean hasHostObject(Value v) {
        if (!v.isMaybeObject()) {
            return false;
        }
        return v.getObjectLabels().stream().anyMatch(ObjectLabel::isHostObject);
    }

    public void setLibTestingHasStarted() {
        this.libTestingInProgress = true;
    }

    public Set<ReadPropertyNode> getPossiblyProblematicReads() {
        return possiblyProblematicReads;
    }
}
