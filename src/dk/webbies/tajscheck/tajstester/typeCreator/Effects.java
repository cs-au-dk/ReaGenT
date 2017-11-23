package dk.webbies.tajscheck.tajstester.typeCreator;

import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.Obj;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.PKey;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.util.AnalysisException;
import dk.brics.tajs.util.Collections;

public class Effects {

    private final Solver.SolverInterface c;

    public Effects(Solver.SolverInterface c) {
        this.c = c;
    }

    public void newObject(ObjectLabel label) {
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

    public void multiplyObject(ObjectLabel label) {
        c.getState().multiplyObject(label);
    }

    public void writeProperty(ObjectLabel label, String propertyName, Value value) {
        c.getAnalysis().getPropVarOperations().writeProperty(label, propertyName, value);
    }

    public void writeNumberIndexer(ObjectLabel label, Value value) {
        Obj object = c.getState().getObject(label, true);
        object.setDefaultArrayProperty(value.join(Value.makeAbsent()));
    }

    public void writeStringIndexer(ObjectLabel label, Value value) {
        Obj object = c.getState().getObject(label, true);
        object.setDefaultNonArrayProperty(value.join(Value.makeAbsent()));
    }

    public ObjectLabel summarize(ObjectLabel objectLabel) {
        c.getState().multiplyObject(objectLabel);
        return objectLabel.makeSingleton().makeSummary();
    }
}
