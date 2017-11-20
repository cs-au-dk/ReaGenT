package dk.webbies.tajscheck.tajstester.typeCreator;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.analysis.dom.DOMFunctions;
import dk.brics.tajs.analysis.dom.DOMObjects;
import dk.brics.tajs.analysis.dom.DOMWindow;
import dk.brics.tajs.analysis.nativeobjects.ECMAScriptObjects;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.unevalizer.SimpleUnevalizerAPI;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.typeutil.TypesUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NativesInstantiator {
    private final BenchmarkInfo info;
    private SpecInstantiator specInstantiator;

    public NativesInstantiator(BenchmarkInfo info, SpecInstantiator specInstantiator) {
        this.info = info;
        this.specInstantiator = specInstantiator;
    }

    public boolean shouldConstructAsNative(Type type) {
        return
                info.nativeTypes.contains(type) &&
                !nativesToConstructStructurally.contains(info.typeNames.get(type)) &&
                !(type instanceof TypeParameterType) &&
                !(type instanceof ReferenceType && info.typeNames.get(((ReferenceType) type).getTarget()).equals("Array")) &&
                !info.typeNames.get(type).contains("[") &&
                !info.typeNames.get(type).startsWith("window.");

    }


    private static final Set<String> nativesToConstructStructurally = new HashSet<>(Arrays.asList(
            "RTCConfiguration",
            "RTCIceServer",
            "RTCIceTransportPolicy",
            "RTCBundlePolicy"
    ));

    Value instantiateNative(Type type, SpecInstantiator.MiscInfo info, String step, Solver.SolverInterface c) {
        String name = this.info.typeNames.get(type);
        switch (name) {
            case "Element":
            case "HTMLElement": {
                return DOMFunctions.makeAnyHTMLElement();
            }
            case "Date": {
                ObjectLabel objlabel = ObjectLabel.make(c.getNode(), ObjectLabel.Kind.DATE);
                c.getState().newObject(objlabel);
                c.getState().writeInternalValue(objlabel, Value.makeAnyNumUInt());
                c.getState().writeInternalPrototype(objlabel, Value.makeObject(InitialStateBuilder.DATE_PROTOTYPE));
                return Value.makeObject(objlabel);
            }
            case "Function": {
                return SimpleUnevalizerAPI.evaluateFunctionCall(c.getNode(), Collections.emptyList(), "", c);
            }
            case "RegExp": {
                ObjectLabel label = ObjectLabel.make(c.getNode(), ObjectLabel.Kind.REGEXP, null);
                c.getState().newObject(label);
                c.getState().writeInternalPrototype(label, Value.makeObject(InitialStateBuilder.REGEXP_PROTOTYPE));
                return Value.makeObject(label);
            }
            case "Error": {
                ObjectLabel ex = ObjectLabel.make(c.getNode(), ObjectLabel.Kind.ERROR);
                c.getState().newObject(ex);
                c.getState().writeInternalPrototype(ex, Value.makeObject(InitialStateBuilder.ERROR_PROTOTYPE));
                c.getAnalysis().getPropVarOperations().writeProperty(ex, "message", Value.makeAnyStr());
                return Value.makeObject(ex);
            }
            case "StringConstructor":
                return Value.makeObject(ObjectLabel.make(ECMAScriptObjects.STRING, ObjectLabel.Kind.FUNCTION));
            case "DateConstructor":
                return Value.makeObject(ObjectLabel.make(ECMAScriptObjects.DATE, ObjectLabel.Kind.FUNCTION));
            case "ArrayConstructor":
                return Value.makeObject(ObjectLabel.make(ECMAScriptObjects.ARRAY, ObjectLabel.Kind.FUNCTION));
            case "String":
                return specInstantiator.instantiate(new SimpleType(SimpleTypeKind.String), info, step);
            case "Window":
                return Value.makeObject(Collections.singleton(DOMWindow.WINDOW_CONSTRUCTOR));
            case "EventTarget":
                return constructFromPrototype(info, DOMObjects.EVENT_TARGET_PROTOTYPE, c);
            case "WebGLRenderingContext":
                return constructFromPrototype(info, DOMObjects.WEBGLRENDERINGCONTEXT_PROTOTYPE, c);
            case "Object":
                return specInstantiator.instantiate(SpecReader.makeEmptySyntheticInterfaceType(), info, step);
            default:
                throw new RuntimeException("Yet unknown how to create native object: " + name);
        }
    }


    private Value constructFromPrototype(SpecInstantiator.MiscInfo info, HostObject prototype, Solver.SolverInterface c) {
        ObjectLabel prototypeLabel = ObjectLabel.make(prototype, ObjectLabel.Kind.OBJECT);
        c.getState().newObject(prototypeLabel);
        c.getState().writeInternalPrototype(info.labelToUse, Value.makeObject(prototypeLabel));
        return Value.makeObject(info.labelToUse);
    }

}
