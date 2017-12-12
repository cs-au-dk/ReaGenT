package dk.webbies.tajscheck.tajstester.typeCreator;

import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.analysis.PropVarOperations;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.Obj;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.util.AnalysisException;

import java.util.HashSet;
import java.util.Set;

class Effects {

    private final Solver.SolverInterface c;
    private PropVarOperations pv;

    Effects(Solver.SolverInterface c) {
        this.c = c;
        this.pv = c.getAnalysis().getPropVarOperations();
    }


    private final Set<ObjectLabel> initialized = new HashSet<>();
    void newObject(ObjectLabel label, SpecInstantiator.MiscInfo info) {
        if (initialized.contains(label)) {
            return;
        }
        initialized.add(label);

        c.getState().newObject(label);
        ObjectLabel prototype;
        switch (label.getKind()) {
            case FUNCTION:
                prototype = InitialStateBuilder.FUNCTION_PROTOTYPE;
                writeProperty(label, "length", Value.makeAnyNumUInt());
                writeProperty(label, "name", Value.makeAnyStr());
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
            case STRING:
                prototype = InitialStateBuilder.STRING_PROTOTYPE;
                break;
            case SYMBOL:
                prototype = InitialStateBuilder.SYMBOL_PROTOTYPE;
                break;
            default:
                throw new AnalysisException("No support for guessing prototype of " + label.getKind());
        }
        c.getState().writeInternalPrototype(label, Value.makeObject(prototype));
    }

    void writeProperty(ObjectLabel label, String propertyName, Value value) {
        pv.writeProperty(label, propertyName, value);
    }

    void writeNumberIndexer(ObjectLabel label, Value value) {
        Obj object = c.getState().getObject(label, true);
        object.setDefaultArrayProperty(value.join(Value.makeAbsent()));
    }

    void writeStringIndexer(ObjectLabel label, Value value) {
        Obj object = c.getState().getObject(label, true);
        object.setDefaultNonArrayProperty(value.join(Value.makeAbsent()));
    }

    private final Set<ObjectLabel> summarized = new HashSet<>();
    ObjectLabel summarize(ObjectLabel label) {
        if (!summarized.contains(label)) {
            c.getState().multiplyObject(label);
            summarized.add(label);
        }
        return label.makeSingleton().makeSummary();
    }
}
