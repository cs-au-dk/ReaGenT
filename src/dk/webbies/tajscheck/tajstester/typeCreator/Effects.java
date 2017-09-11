package dk.webbies.tajscheck.tajstester.typeCreator;

import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.solver.GenericSolver;
import dk.brics.tajs.util.AnalysisException;
import dk.brics.tajs.util.Collections;

import java.util.Map;
import java.util.Set;

import static dk.brics.tajs.util.Collections.newMap;
import static dk.brics.tajs.util.Collections.newSet;

public class Effects {

    private final Stats stats;

    private final GenericSolver<State, Context, CallEdge, IAnalysisMonitoring, Analysis>.SolverInterface c;

    public Effects(Solver.SolverInterface c) {
        this.c = c;
        this.stats = new Stats();
    }

    public Stats getStats() {
        return stats;
    }

    public void newObject(ObjectLabel label) {
        stats.newObject(label);
        c.getState().newObject(label);
        ObjectLabel prototype;
        // TODO the kind of a label should not have semantic meaning: it is only intended to distinguish different allocations at the samme site...
        switch (label.getKind()) {
            case FUNCTION:
                prototype = InitialStateBuilder.FUNCTION_PROTOTYPE;
                break;
            case OBJECT:
                prototype = InitialStateBuilder.OBJECT_PROTOTYPE;
                break;
            case ARRAY:
                prototype = InitialStateBuilder.ARRAY_PROTOTYPE;
                break;
            case BOOLEAN:
                prototype = InitialStateBuilder.BOOLEAN_PROTOTYPE;
                break;
            case ERROR:
                prototype = InitialStateBuilder.ERROR_PROTOTYPE;
                break;
            case REGEXP:
                prototype = InitialStateBuilder.REGEXP_PROTOTYPE;
                break;
            case DATE:
                prototype = InitialStateBuilder.DATE_PROTOTYPE;
                break;
            default:
                throw new AnalysisException("No support for guessing prototype of " + label.getKind());
        }
        c.getState().writeInternalPrototype(label, Value.makeObject(prototype));
    }

    public void multiplyObject(ObjectLabel label) {
        c.getState().multiplyObject(label);
    }

    public void writeProperty(ObjectLabel label, String propertyName, Value value) {
        Obj object = c.getState().getObject(label, false);
        if (object.isAllNone()) {
            throw new AnalysisException("Trying to write properties of BottomObject?! (" + label + ")");
        }
        stats.newProperty(label, propertyName);
        c.getAnalysis().getPropVarOperations().writeProperty(label, propertyName, value);
    }

    public void writeNumberIndexer(ObjectLabel label, Value value) {
        Obj object = c.getState().getObject(label, true);
        if (object.isAllNone()) {
            throw new AnalysisException("Trying to write properties of BottomObject?! (" + label + ")");
        }
        stats.newProperty(label, "[numberIndexer]");
        object.setDefaultArrayProperty(value.join(Value.makeAbsent()));
    }

    public void writeStringIndexer(ObjectLabel label, Value value) {
        Obj object = c.getState().getObject(label, true);
        if (object.isAllNone()) {
            throw new AnalysisException("Trying to write properties of BottomObject?! (" + label + ")");
        }
        stats.newProperty(label, "[stringIndexer]");
        object.setDefaultNonArrayProperty(value.join(Value.makeAbsent()));
    }

    public static class Stats {

        private Set<ObjectLabel> newObjects = newSet();

        private Map<ObjectLabel, Set<String>> newProperties = newMap();

        public void newObject(ObjectLabel label) {
            newObjects.add(label);
        }

        public void newProperty(ObjectLabel label, String propertyName) {
            Collections.addToMapSet(newProperties, label, propertyName);
        }

        public Set<ObjectLabel> getNewObjects() {
            return newObjects;
        }

        public Map<ObjectLabel, Set<String>> getNewProperties() {
            return newProperties;
        }
    }
}
