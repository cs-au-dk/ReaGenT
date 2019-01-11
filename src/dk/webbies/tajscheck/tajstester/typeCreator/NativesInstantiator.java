package dk.webbies.tajscheck.tajstester.typeCreator;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
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
import dk.brics.tajs.analysis.dom.event.Event;
import dk.brics.tajs.analysis.dom.html.HTMLCollection;
import dk.brics.tajs.analysis.dom.html.HTMLImageElement;
import dk.brics.tajs.analysis.dom.html.HTMLTextAreaElement;
import dk.brics.tajs.analysis.dom.html5.CanvasRenderingContext2D;
import dk.brics.tajs.analysis.dom.html5.HTMLCanvasElement;
import dk.brics.tajs.analysis.js.UserFunctionCalls;
import dk.brics.tajs.analysis.nativeobjects.ECMAScriptObjects;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.lattice.*;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.tajstester.TajsTestVisitor;
import dk.webbies.tajscheck.tajstester.TajsTypeTester;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;

import java.util.*;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newMap;
import static dk.brics.tajs.util.Collections.singleton;

public class NativesInstantiator {
    private final BenchmarkInfo info;
    private final Context initialContext;
    private final BasicBlock initialBlock;
    private SpecInstantiator specInstantiator;
    private TajsTypeTester tajsTypeTester;

    public NativesInstantiator(BenchmarkInfo info, SpecInstantiator specInstantiator, TajsTypeTester tajsTypeTester, Solver.SolverInterface c) {
        this.info = info;
        this.specInstantiator = specInstantiator;
        this.tajsTypeTester = tajsTypeTester;

        // by doing this, i add the call to the constructor here, and i then access the same call later. Meaning there will be a value ready
        this.initialContext = c.getState().getContext();
        this.initialBlock = c.getState().getBasicBlock();

        evalConstructorMap.forEach((name, args) -> {
            evalConstructor(c, name, args.toArray(new Value[0]));
        });
    }

    private static final Set<String> constructStructually = new HashSet<>(Arrays.asList(
            "ArrayLike"
    ));

    public boolean shouldConstructAsNative(Type type) {
        if (type instanceof ReferenceType) {
            type = ((ReferenceType) type).getTarget();
        }
        String name = info.typeNames.get(type);
        return
                !constructStructually.contains(name) &&
                info.nativeTypes.contains(type) &&
                !nativesToConstructStructurally.contains(name) &&
                !(type instanceof TypeParameterType) &&
                !(type instanceof ReferenceType && info.typeNames.get(((ReferenceType) type).getTarget()).equals("Array")) &&
                !name.contains("[") &&
                !name.startsWith("window.");

    }

    private static final Set<String> nativesToConstructStructurally = new HashSet<>(Arrays.asList(
            "RTCConfiguration",
            "RTCIceServer",
            "RTCIceTransportPolicy",
            "RTCBundlePolicy"
    ));

    Value instantiateNative(Type type, SpecInstantiator.MiscInfo info, Solver.SolverInterface c) {
        String name;
        if (type instanceof ReferenceType) {
            name = this.info.typeNames.get(((ReferenceType) type).getTarget());
        } else {
            name = this.info.typeNames.get(type);
        }

        return constructFromName(type, info, c, name);
    }

    private Value constructFromName(Type type, SpecInstantiator.MiscInfo info, Solver.SolverInterface c, String name) {
        if (evalConstructorMap.containsKey(name)) {
            Value result = evalConstructor(c, name, evalConstructorMap.get(name).toArray(new Value[0]));
            if (result.isNone()) {
                // so i at least return the right object-label. The properties will get added from the worklist.
                return c.withState(c.getAnalysisLatticeElement().getState(initialBlock, initialContext),() -> {
                    return evalConstructor(c, name, evalConstructorMap.get(name).toArray(new Value[0]));
                });
            } else {
                return result;
            }
        }
        switch (name) {
            case "Object":
                return specInstantiator.convertType(type, info, () -> new TypeWithContext(SpecReader.makeEmptySyntheticInterfaceType(), TypeContext.create(this.info)));
            case "Date":
                return specInstantiator.withNewObject(info, objlabel -> {
                    c.getState().writeInternalValue(objlabel, Value.makeAnyNumUInt());
                });
            case "Function":
                return specInstantiator.convertType(type, info, () -> {
                    InterfaceType inter = SpecReader.makeEmptySyntheticInterfaceType();
                    Signature signature = TypesUtil.emptySignature();

                    // a lot of this is copied from where the any-type is created.
                    GenericType arrayBase = (GenericType) ((ReferenceType) ((InterfaceType) this.info.getSpec().getGlobal().getDeclaredProperties().get("Array")).getDeclaredProperties().get("prototype")).getTarget();
                    ReferenceType anyArray = new ReferenceType();
                    anyArray.setTarget(arrayBase);
                    anyArray.setTypeArguments(Collections.singletonList(new SimpleType(SimpleTypeKind.Any)));
                    Signature.Parameter anyParameter = new Signature.Parameter();
                    anyParameter.setName("any");
                    anyParameter.setType(anyArray);
                    signature.setMinArgumentCount(0);
                    signature.setParameters(Collections.singletonList(anyParameter));
                    signature.setResolvedReturnType(new SimpleType(SimpleTypeKind.Any));
                    signature.setHasRestParameter(true);

                    inter.setDeclaredCallSignatures(Collections.singletonList(signature));
                    inter.setDeclaredConstructSignatures(Collections.singletonList(signature));

                    return new TypeWithContext(inter, TypeContext.create(this.info));
                });
            case "RegExp":
                return specInstantiator.withNewObject(info, label -> {});
            case "Element":
            case "HTMLElement":
                return DOMFunctions.makeAnyHTMLElement();
            case "Error": {
                ObjectLabel ex = info.labelToUse;
                c.getState().newObject(ex);
                c.getState().writeInternalPrototype(ex, Value.makeObject(InitialStateBuilder.ERROR_PROTOTYPE));
                c.getAnalysis().getPropVarOperations().writeProperty(ex, "message", Value.makeAnyStr());
                return Value.makeObject(ex);
            }
            case "StringConstructor":
            case "DateConstructor":
            case "ArrayConstructor":
                return specInstantiator.withNewObject(info, (label) -> {});
            case "String":
                return specInstantiator.convertType(type, info, () -> new TypeWithContext(new SimpleType(SimpleTypeKind.String), TypeContext.create(this.info)));
            case "Window":
                return Value.makeObject(Collections.singleton(DOMWindow.WINDOW_CONSTRUCTOR));
            case "EventTarget":
                return constructFromPrototype(info, DOMObjects.EVENT_TARGET_PROTOTYPE, c);
            case "Event":
                return Value.makeObject(Event.INSTANCES);
            case "WebGLRenderingContext":
                return constructFromPrototype(info, DOMObjects.WEBGLRENDERINGCONTEXT_PROTOTYPE, c);
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
            case "CanvasGradient":
                return Value.makeObject(CanvasRenderingContext2D.GRADIENT);
            case "CanvasPattern":
                return Value.makeObject(CanvasRenderingContext2D.PATTERN);
            case "Node":
                return Value.makeObject(DOMNode.INSTANCES);
            case "Array":
                return constructArray(info, ((ReferenceType) type).getTypeArguments().iterator().next(), c);
            default:
                throw new RuntimeException("Yet unknown how to create native object: " + name);
        }
    }

    private Value constructArray(SpecInstantiator.MiscInfo info, Type indexType, Solver.SolverInterface c) {
        return specInstantiator.withNewObject(info, label -> {
            Value indexValue = specInstantiator.instantiate(indexType, info, "[numberindexer]");
            c.getAnalysis().getPropVarOperations().writeProperty(Collections.singleton(label), Value.makeAnyStrUInt(), indexValue);
            c.getAnalysis().getPropVarOperations().writeProperty(label, "length", Value.makeAnyNumUInt());
        });
    }

    private final Map<String, List<Value>> evalConstructorMap = new HashMap<>(); {
        evalConstructorMap.put("Uint8Array", Collections.singletonList(Value.makeAnyNum()));
        evalConstructorMap.put("Int8Array", Collections.singletonList(Value.makeAnyNum()));
        evalConstructorMap.put("Uint8ClampedArray", Collections.singletonList(Value.makeAnyNum()));
        evalConstructorMap.put("Int16Array", Collections.singletonList(Value.makeAnyNum()));
        evalConstructorMap.put("Uint16Array", Collections.singletonList(Value.makeAnyNum()));
        evalConstructorMap.put("Int32Array", Collections.singletonList(Value.makeAnyNum()));
        evalConstructorMap.put("Uint32Array", Collections.singletonList(Value.makeAnyNum()));
        evalConstructorMap.put("Float32Array", Collections.singletonList(Value.makeAnyNum()));
        evalConstructorMap.put("Float64Array", Collections.singletonList(Value.makeAnyNum()));
    }

    private static final Map<String, Value> cachedConstructed = new HashMap<>();

    private Value evalConstructor(Solver.SolverInterface c, String name, Value... args) {
        if (cachedConstructed.containsKey(name) && c.getState().getStore().containsKey(cachedConstructed.get(name).getObjectLabels().iterator().next())) {
            return cachedConstructed.get(name);
        }
        Context newc = makeConstructionContext(c.getState().getContext(), name);

        BasicBlock block = c.getState().getBasicBlock();
        c.propagateToBasicBlock(c.getAnalysisLatticeElement().getState(block, c.getState().getContext()).clone(), block, newc);

        State testState = c.getAnalysisLatticeElement().getState(block, newc);

        Value result = c.withState(testState, () -> {
            Value function = UnknownValueResolver.getProperty(InitialStateBuilder.GLOBAL, PKey.make(Value.makeStr(name)), c.getState(), false);

            assert function.getObjectLabels().size() == 1;

            FunctionCalls.CallInfo callinfo = TajsTestVisitor.createCallInfo(Value.makeObject(InitialStateBuilder.GLOBAL), true, Arrays.asList(args), false, null, c.getNode());

            BasicBlock implicitAfterCall = UserFunctionCalls.implicitUserFunctionCall(function.getObjectLabels().iterator().next(), callinfo, c);

            return UserFunctionCalls.implicitUserFunctionReturn(newList(), true, implicitAfterCall, c);
        });

        c.propagateToBasicBlock(testState.clone(), block, c.getState().getContext());
        if (!result.isNone()) {
            cachedConstructed.put(name, result);
        }

        return result;
    }

    public Context makeConstructionContext(Context from, String name) {
        Map<LocalContext.Qualifier, Value> localContext = newMap();
        if (from.getLocalContext() != null) {
            localContext.putAll(from.getLocalContext().getQualifiers());
        }
        localContext.put(ConstructionQualifier.instance, Value.makeStr(name));

        return Context.make(from.getThisVal(), from.getFunArgs(), from.getSpecialRegisters(), LocalContext.make(localContext), from.getLocalContextAtEntry());
    }

    public static class ConstructionQualifier implements LocalContext.Qualifier {

        private static ConstructionQualifier instance = new ConstructionQualifier();

        private ConstructionQualifier() {
        }

        @Override
        public String toString() {
            return "construction-local-context";
        }
    }


    private Map<HostObject, ObjectLabel> prototypeCache = new HashMap<>();
    private Value constructFromPrototype(SpecInstantiator.MiscInfo info, HostObject prototype, Solver.SolverInterface c) {
        return specInstantiator.withNewObject(info, label -> {
            ObjectLabel prototypeLabel = prototypeCache.getOrDefault(prototype, ObjectLabel.make(prototype, ObjectLabel.Kind.OBJECT));
            if (!c.getState().getStore().containsKey(prototypeLabel)) {
                c.getState().newObject(prototypeLabel);
            }
            c.getState().writeInternalPrototype(info.labelToUse, Value.makeObject(prototypeLabel));
        });
    }

    ObjectLabel createObjectLabel(Type type, HostObject hostObject) {
        String name;
        if ((type instanceof ReferenceType)) {
            name = this.info.typeNames.get(((ReferenceType) type).getTarget());
        } else {
            name = this.info.typeNames.get(type);
        }
        switch (name) {
            case "Object":
            case "EventTarget":
            case "WebGLRenderingContext":
                return ObjectLabel.make(hostObject, ObjectLabel.Kind.OBJECT);
            case "Date":
                return ObjectLabel.make(hostObject, ObjectLabel.Kind.DATE);
            case "RegExp":
                return ObjectLabel.make(hostObject, ObjectLabel.Kind.REGEXP);
            case "Error":
                return ObjectLabel.make(hostObject, ObjectLabel.Kind.ERROR);
            case "Array":
                return ObjectLabel.make(hostObject, ObjectLabel.Kind.ARRAY);
            case "ArrayConstructor":
            case "StringConstructor":
            case "DateConstructor":
                return ObjectLabel.make(hostObject, ObjectLabel.Kind.FUNCTION);
        }
        return null;
    }
}
