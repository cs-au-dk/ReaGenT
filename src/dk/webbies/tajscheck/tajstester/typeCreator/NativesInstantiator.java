package dk.webbies.tajscheck.tajstester.typeCreator;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.analysis.dom.DOMFunctions;
import dk.brics.tajs.analysis.dom.DOMObjects;
import dk.brics.tajs.analysis.dom.DOMWindow;
import dk.brics.tajs.analysis.dom.ajax.XmlHttpRequest;
import dk.brics.tajs.analysis.dom.core.DOMDocument;
import dk.brics.tajs.analysis.dom.core.DOMNode;
import dk.brics.tajs.analysis.dom.core.DOMNodeList;
import dk.brics.tajs.analysis.dom.html.HTMLCollection;
import dk.brics.tajs.analysis.dom.html.HTMLImageElement;
import dk.brics.tajs.analysis.dom.html.HTMLTextAreaElement;
import dk.brics.tajs.analysis.dom.html5.CanvasRenderingContext2D;
import dk.brics.tajs.analysis.dom.html5.HTMLCanvasElement;
import dk.brics.tajs.analysis.js.UserFunctionCalls;
import dk.brics.tajs.analysis.nativeobjects.ECMAScriptObjects;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.solver.GenericSolver;
import dk.brics.tajs.unevalizer.SimpleUnevalizerAPI;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.TajsTestVisitor;
import dk.webbies.tajscheck.util.Pair;

import java.util.*;

import static dk.brics.tajs.util.Collections.newList;

public class NativesInstantiator {
    private final BenchmarkInfo info;
    private SpecInstantiator specInstantiator;

    public NativesInstantiator(BenchmarkInfo info, SpecInstantiator specInstantiator) {
        this.info = info;
        this.specInstantiator = specInstantiator;
    }

    public boolean shouldConstructAsNative(Type type) {
        if (type instanceof ReferenceType) {
            type = ((ReferenceType) type).getTarget();
        }
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

    private final Map<Pair<Type, List<String>>, Value> nativesValuesCache = new HashMap<>();
    Value instantiateNative(Type type, SpecInstantiator.MiscInfo info, String step, Solver.SolverInterface c) {
        Pair<Type, List<String>> key = new Pair<>(type, info.path);
        if (nativesValuesCache.containsKey(key)) {
            return nativesValuesCache.get(key);
        }
        Value result = constructNoCache(type, info, step, c);
        if (!result.isNone()) {
            nativesValuesCache.put(key, result);
        }
        return result;
    }

    private Value constructNoCache(Type type, SpecInstantiator.MiscInfo info, String step, Solver.SolverInterface c) {
        String name;
        if (type instanceof ReferenceType) {
            name = this.info.typeNames.get(((ReferenceType) type).getTarget());
        } else {
            name = this.info.typeNames.get(type);
        }
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
            case "XMLHttpRequest":
                return Value.makeObject(XmlHttpRequest.INSTANCES);
            case "HTMLTextAreaElement":
                return Value.makeObject(HTMLTextAreaElement.INSTANCES);
            case "HTMLImageElement":
                return Value.makeObject(HTMLImageElement.INSTANCES);
            case "HTMLCanvasElement":
                return Value.makeObject(HTMLCanvasElement.INSTANCES);
            case "CanvasRenderingContext2D":
                return Value.makeObject(CanvasRenderingContext2D.CONTEXT2D);
            case "HTMLCollection":
                return Value.makeObject(HTMLCollection.INSTANCES);
            case "NodeList":
            case "NodeListOf":
                return Value.makeObject(DOMNodeList.INSTANCES);
            case "Document":
                return Value.makeObject(DOMDocument.INSTANCES);
            case "Uint8Array":
            case "Int8Array":
            case "Uint8ClampedArray":
            case "Int16Array":
            case "Uint16Array":
            case "Int32Array":
            case "Uint32Array":
            case "Float32Array":
            case "Float64Array":
                return evalConstructor(c, name, Value.makeAnyNumUInt());
            case "Node":
                return Value.makeObject(DOMNode.INSTANCES);
            case "Array":
                //noinspection ConstantConditions
                return constructArray(info, ((ReferenceType) type).getTypeArguments().iterator().next(), c);
            default:
                throw new RuntimeException("Yet unknown how to create native object: " + name);
        }
    }

    private Value constructArray(SpecInstantiator.MiscInfo info, Type indexType, Solver.SolverInterface c) {
        ObjectLabel array = info.labelToUse;
        Value indexValue = specInstantiator.instantiate(indexType, info, "[numberindexer]");
        c.getAnalysis().getPropVarOperations().writeProperty(Collections.singleton(array), Value.makeAnyStrUInt(), indexValue);
        c.getAnalysis().getPropVarOperations().writeProperty(array, "length", Value.makeAnyNumUInt());
        return Value.makeObject(array);
    }

    private Value evalConstructor(Solver.SolverInterface c, String name, Value... args) {
        Value function = UnknownValueResolver.getProperty(InitialStateBuilder.GLOBAL, PKey.make(Value.makeStr(name)), c.getState(), false);

        assert function.getObjectLabels().size() == 1;

        FunctionCalls.CallInfo callinfo = TajsTestVisitor.createCallInfo(Value.makeObject(InitialStateBuilder.GLOBAL), true, Arrays.asList(args), false, null, c.getNode());

        BasicBlock implicitAfterCall = UserFunctionCalls.implicitUserFunctionCall(function.getObjectLabels().iterator().next(), callinfo, c);

        return UserFunctionCalls.implicitUserFunctionReturn(newList(), true, implicitAfterCall, c);
    }


    private Value constructFromPrototype(SpecInstantiator.MiscInfo info, HostObject prototype, Solver.SolverInterface c) {
        ObjectLabel prototypeLabel = ObjectLabel.make(prototype, ObjectLabel.Kind.OBJECT);
        c.getState().newObject(prototypeLabel);
        c.getState().writeInternalPrototype(info.labelToUse, Value.makeObject(prototypeLabel));
        return Value.makeObject(info.labelToUse);
    }

}
