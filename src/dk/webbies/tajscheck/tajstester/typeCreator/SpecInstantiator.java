package dk.webbies.tajscheck.tajstester.typeCreator;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.AnonymousType;
import dk.au.cs.casa.typescript.types.BooleanLiteral;
import dk.au.cs.casa.typescript.types.ClassInstanceType;
import dk.au.cs.casa.typescript.types.ClassType;
import dk.au.cs.casa.typescript.types.GenericType;
import dk.au.cs.casa.typescript.types.IndexType;
import dk.au.cs.casa.typescript.types.IndexedAccessType;
import dk.au.cs.casa.typescript.types.InterfaceType;
import dk.au.cs.casa.typescript.types.IntersectionType;
import dk.au.cs.casa.typescript.types.NumberLiteral;
import dk.au.cs.casa.typescript.types.ReferenceType;
import dk.au.cs.casa.typescript.types.SimpleType;
import dk.au.cs.casa.typescript.types.SimpleTypeKind;
import dk.au.cs.casa.typescript.types.StringLiteral;
import dk.au.cs.casa.typescript.types.ThisType;
import dk.au.cs.casa.typescript.types.TupleType;
import dk.au.cs.casa.typescript.types.Type;
import dk.au.cs.casa.typescript.types.TypeParameterType;
import dk.au.cs.casa.typescript.types.TypeVisitor;
import dk.au.cs.casa.typescript.types.TypeVisitorWithArgument;
import dk.au.cs.casa.typescript.types.UnionType;
import dk.brics.tajs.analysis.HostAPIs;
import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.PKey;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.util.AnalysisException;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newMap;
import static dk.brics.tajs.util.Collections.newSet;
import static java.util.Collections.singletonList;

public class SpecInstantiator {

    private static final Logger log = Logger.getLogger(SpecInstantiator.class);

    private static final String globalObjectPath = "<the global object>";

    private final Type global;

    private final InstantiatorVisitor visitor;

    private final Set<TypeWithContext> processing;

    private final ObjectLabelKindDecider objectLabelKindDecider;

    private final CanonicalHostObjectLabelPaths canonicalHostObjectLabelPaths;

    private final Effects effects;
    private final BenchmarkInfo info;

    // misc. paths that we choose to ignore
    private Map<TypeWithContext, Value> valueCache;

    private Map<TypeWithContext, ObjectLabel> labelCache;

    private final Value ANY;

    private final Solver.SolverInterface c;

    private Value defaultAnyString;

    public SpecInstantiator(Solver.SolverInterface c, BenchmarkInfo info) {
        this.global = info.getSpec().getGlobal();
        this.visitor = new InstantiatorVisitor();
        this.objectLabelKindDecider = new ObjectLabelKindDecider();
        this.canonicalHostObjectLabelPaths = new CanonicalHostObjectLabelPaths(c.getState().getStore().keySet()); // TODO: See what is inside this thing.
        this.labelCache = newMap();
        this.valueCache = newMap();
        this.processing = newSet();
        this.effects = new Effects(c);
        this.info = info;
        this.c = c;

        initializeLabelsCacheWithCanonicals();

        this.ANY = createAny(info);
    }

    private Value createAny(BenchmarkInfo info) {
        // TODO: Rewrite this.
        ObjectLabel label1 = ObjectLabel.make(SpecObjects.getObjectAbstraction(Collections.singletonList("<any1>"), new TypeWithContext(new SimpleType(SimpleTypeKind.Any), TypeContext.create(info))), ObjectLabel.Kind.FUNCTION);
        ObjectLabel label2 = ObjectLabel.make(SpecObjects.getObjectAbstraction(Collections.singletonList("<any2>"), new TypeWithContext(new SimpleType(SimpleTypeKind.Any), TypeContext.create(info))), ObjectLabel.Kind.OBJECT);

        effects.newObject(label1);
        effects.multiplyObject(label1);
        effects.newObject(label2);
        effects.multiplyObject(label2);

        Value any = Value.makeObject(label1, label2).joinUndef().joinNull().joinAnyBool().joinAnyStr().joinAnyNum();

        effects.writeStringIndexer(label1, any);
        effects.writeStringIndexer(label2, any);
        return any;
    }

    /**
     * Ensures that whenever a type T with a canonical representation is encountered, the canonical version is used.
     */
    private void initializeLabelsCacheWithCanonicals() {
        canonicalHostObjectLabelPaths.getPaths().forEach(p -> {
                    if (singletonList("print").equals(p) || singletonList("alert").equals(p) || singletonList("unescape").equals(p) || singletonList("escape").equals(p) || p.get(p.size() - 1).equals("toString")) {
                        return;
                    }

                    if (!p.isEmpty() && p.iterator().next().equals("Symbol")) {
                        return;
                    }
                    if (p.get(p.size() - 1).endsWith("instances")) {
                        return;
                    }
                    if (Arrays.asList("Object", "module").equals(p)) {
                        return;
                    }

                    TypeWithContext type = resolveType(p);
                    if (type == null) {
                        return;
                    }
                    Stack<String> stack = new Stack<>();
                    stack.addAll(p);
                    labelCache.put(type, canonicalHostObjectLabelPaths.get(stack));
                }
        );
    }

    private TypeWithContext resolveType(List<String> path) {
        TypeWithContext root = new TypeWithContext(global, TypeContext.create(info));
        if (singletonList(globalObjectPath).equals(path)) {
            return root;
        }
        if(path.size() > 0 && path.get(0).equals("Window")) {
            path = path.subList(1, path.size());
        }
        return resolveType(root, path);
    }

    private TypeWithContext resolveType(TypeWithContext root, List<String> path) {
        if (path.isEmpty()) {
            return root;
        }
        String step = path.get(0);
        if ("prototype".equals(step)) {
            return null;
        }
        if (root == null) {
            return null;
        }
        TypeWithContext newRoot = root.getType().accept(new TypeVisitor<TypeWithContext>() {
            @Override
            public TypeWithContext visit(AnonymousType anonymousType) {
                throw new RuntimeException();
            }

            @Override
            public TypeWithContext visit(ClassType classType) {
                return new TypeWithContext(classType.getStaticProperties().get(step), root.getTypeContext());
            }

            @Override
            public TypeWithContext visit(GenericType genericType) {
                return new TypeWithContext(genericType.getDeclaredProperties().get(step), root.getTypeContext());
            }

            @Override
            public TypeWithContext visit(InterfaceType interfaceType) {
                return new TypeWithContext(interfaceType.getDeclaredProperties().get(step), root.getTypeContext());
            }

            @Override
            public TypeWithContext visit(ReferenceType referenceType) {
                return resolveType(new TypeWithContext(referenceType.getTarget(), info.typesUtil.generateParameterMap(referenceType, root.getTypeContext())), path);
            }

            @Override
            public TypeWithContext visit(SimpleType simpleType) {
                throw new RuntimeException();
            }

            @Override
            public TypeWithContext visit(TupleType tupleType) {
                throw new RuntimeException();
            }

            @Override
            public TypeWithContext visit(UnionType unionType) {
                throw new RuntimeException();
            }

            @Override
            public TypeWithContext visit(TypeParameterType typeParameterType) {
                throw new RuntimeException();
            }

            @Override
            public TypeWithContext visit(StringLiteral t) {
                throw new RuntimeException();
            }

            @Override
            public TypeWithContext visit(BooleanLiteral t) {
                throw new RuntimeException();
            }

            @Override
            public TypeWithContext visit(NumberLiteral t) {
                throw new RuntimeException();
            }

            @Override
            public TypeWithContext visit(IntersectionType t) {
                throw new RuntimeException();
            }

            @Override
            public TypeWithContext visit(ClassInstanceType t) {
                throw new RuntimeException();
            }

            @Override
            public TypeWithContext visit(ThisType t) {
                throw new RuntimeException();
            }

            @Override
            public TypeWithContext visit(IndexType t) {
                throw new RuntimeException();
            }

            @Override
            public TypeWithContext visit(IndexedAccessType t) {
                throw new RuntimeException();
            }
        });
        if (newRoot == null) {
            throw new AnalysisException(String.format("Could not find type at step '%s' for root %s, with remaining path '%s'", step, root.toString(), path));
        }
        List<String> shorterPath = path.subList(1, path.size());
        return resolveType(newRoot, shorterPath);
    }

    public void showStats() {
        Effects.Stats stats = effects.getStats();

        StringBuilder sb = new StringBuilder();
        int allocationCount = stats.getNewObjects().size();
        long newFunctionCount = stats.getNewObjects().stream().filter(l -> l.getKind() == ObjectLabel.Kind.FUNCTION).count();
        long newObjectCount = stats.getNewObjects().stream().filter(l -> l.getKind() == ObjectLabel.Kind.OBJECT).count();

        sb.append(String.format(getClass().getSimpleName() + " statistics:%n"));
        sb.append(String.format(" Allocations: %d%n", allocationCount));
        sb.append(String.format("  Functions: %d%n", newFunctionCount));
        sb.append(String.format("  Objects: %d%n", newObjectCount));
        sb.append(String.format("  Other: %d%n", allocationCount - newFunctionCount - newObjectCount));

        System.out.println(sb.toString());
    }

    private ObjectLabel getObjectLabel(Type type, MiscInfo info) {
        TypeWithContext key = new TypeWithContext(type, info.context);
        if (!labelCache.containsKey(key)) {
            // (this call should not lead to recursion)
            final ObjectLabel label;
            if (canonicalHostObjectLabelPaths.has(info.path)) {
                label = canonicalHostObjectLabelPaths.get(info.path);
            } else {
                label = makeObjectLabel(type, info);
            }
            labelCache.put(key, label);
        }
        return labelCache.get(key);
    }

    private ObjectLabel.Kind getObjectLabelKind(Type type) {
        return type.accept(objectLabelKindDecider);
    }

    private Value instantiate(Type type, MiscInfo info, String step) {
        info = info.withContext(info.context.optimizeTypeParameters(type));
        if (step != null) {
            info = info.apendPath(step);
        }

        if (type instanceof UnionType) {
            MiscInfo finalInfo = info;
            return Value.join(Util.withIndex(((UnionType) type).getElements()).map(subType -> instantiate(subType.getLeft(), finalInfo, "[union" + subType.getRight() + "]")).collect(Collectors.toList()));
        }

        TypeWithContext key = new TypeWithContext(type, info.context);
        if (!valueCache.containsKey(key)) {
            Value value;
            ObjectLabel label = getObjectLabel(type, info);
            if (processing.contains(key) && !(type instanceof ThisType || type instanceof TypeParameterType)) { // if thisType or ParameterType, it is actually the type that is "pointed" to that counts.
                // trying to instantiate a (recursive) type that is already being instantiated
                assert labelCache.containsKey(new TypeWithContext(type, info.context));
                if (label == null) {
                    throw new NullPointerException();
                }
                value = Value.makeObject(label);
            } else {
                processing.add(key);
                log.debug("Visiting: " + info.path.toString());
                value = type.accept(visitor, info.withlabel(label));
                processing.remove(key);
            }

            if (value.isMaybeObject() && !SpecInstantiator.this.info.options.staticOptions.createSingletonObjects) {
                value = value.restrictToNotObject().join(Value.makeObject(value.getObjectLabels().stream().map(effects::summeraize).collect(Collectors.toSet())));
            }

            valueCache.put(key, value);
        }
        return valueCache.get(key);
    }

    public Value createValue(TypeWithContext type, String path) {
        MiscInfo misc = new MiscInfo(path, type.getTypeContext(), null);
        return instantiate(type.getType(), misc, null);
    }

    private ObjectLabel makeObjectLabel(Type t, MiscInfo miscInfo) {
        TypeWithContext key = new TypeWithContext(t, miscInfo.context);
        if (labelCache.containsKey(key)) {
            return labelCache.get(key);
        }
        ObjectLabel.Kind kind = getObjectLabelKind(t);
        ObjectLabel label = null;
        if (kind != null) {
            label = ObjectLabel.make(SpecObjects.getObjectAbstraction(miscInfo.path, key), kind);
        }
        labelCache.put(key, label);
        return label;
    }

    private Value withNewObject(ObjectLabel label, Consumer<ObjectLabel> initializer) {
        if (label == null) {
            throw new NullPointerException();
        }
        if (label.getHostObject().getAPI() != HostAPIs.SPEC) {
            initializer.accept(label);
            return Value.makeObject(label);
        }
        // keep the object a singleton during instantiation, we create a summary later.

        effects.newObject(label);
        if (info.options.staticOptions.createSingletonObjects) {// if we keep it as singleton, create summary now.
            effects.summeraize(label);
        }
        initializer.accept(label); // TODO: This might be too strong, it should be summarized at some point.

        effects.writeStringIndexer(label, Value.makeUndef()); // TODO: Try to instead have an special (function) object-label, that summerizes "any". When called it returns itself (and throws exceptions). // TODO:
        return Value.makeObject(label);
    }

    public Value getTheAny() {
        return ANY;
    }

    private class InstantiatorVisitor implements TypeVisitorWithArgument<Value, MiscInfo> {

        @Override
        public Value visit(AnonymousType t, MiscInfo info) {
            throw new RuntimeException();
        }

        @Override
        public Value visit(ClassType t, MiscInfo info) {
            System.err.println("Inaccurate modelling of classes");
            Pair<InterfaceType, Map<TypeParameterType, Type>> pair = SpecInstantiator.this.info.typesUtil.classToInterface(t);
            return pair.getLeft().accept(this, info.withContext(info.context.append(pair.getRight())));
        }

        @Override
        public Value visit(GenericType t, MiscInfo info) {
            return t.toInterface().accept(this, info);
        }

        @Override
        public Value visit(InterfaceType t, MiscInfo info) {
            if (SpecInstantiator.this.info.freeGenericsFinder.hasThisTypes(t)) {
                info = info.withContext(info.context.withThisType(t));
            }
            Pair<InterfaceType, Map<TypeParameterType, Type>> withBaseTypes = SpecInstantiator.this.info.typesUtil.constructSyntheticInterfaceWithBaseTypes(t);

            return writePropertiesToInterface(withBaseTypes.getLeft(), info.withContext(info.context.append(withBaseTypes.getRight())));
        }

        private Value writePropertiesToInterface(InterfaceType type, MiscInfo info) {
            return withNewObject(info.labelToUse, label -> {
                Map<String, Type> declaredProperties = type.getDeclaredProperties();

                if (type.getDeclaredNumberIndexType() != null) {
                    effects.writeNumberIndexer(label, type.getDeclaredNumberIndexType().accept(this, info.apendPath("[numberIndexer]")));
                }
                writeProperties(label, declaredProperties, info);

                if (type.getDeclaredStringIndexType() != null) {
                    effects.writeStringIndexer(label, type.getDeclaredStringIndexType().accept(this, info.apendPath("[stringIndexer]")));
                }
            });
        }

        private void writeProperties(ObjectLabel label, Map<String, Type> declaredProperties, MiscInfo info) {
            declaredProperties.forEach((propertyName, propertyType) -> {
                writeProperty(label, propertyName, propertyType, info);
            });

            // XXX circumventing the lack of knowledge of prototype chain of Functions (constructors)
            if (label.getKind() == ObjectLabel.Kind.FUNCTION) {
                effects.writeProperty(label, "prototype", Value.makeObject(label));
            }
        }

        private void writeProperty(ObjectLabel label, String propertyName, Type propertyType, MiscInfo info) {
            List<String> fullPath = newList(info.path);

            fullPath.add(propertyName);
            Value value = SpecInstantiator.this.instantiate(propertyType, info, propertyName);

            effects.writeProperty(label, propertyName, value);
        }

        @Override
        public Value visit(ReferenceType t, MiscInfo info) {
            info = info.withContext(SpecInstantiator.this.info.typesUtil.generateParameterMap(t, info.context));
            return t.getTarget().accept(this, info);
        }

        @Override
        public Value visit(SimpleType t, MiscInfo info) {
            switch (t.getKind()) {
                case Any:
                    return ANY;
                case String:
                    return makeString();
                case Enum:
                case Number:
                    return Value.makeAnyNum();
                case Boolean:
                    return Value.makeAnyBool();
                case Void:
                case Undefined:
                    return Value.makeUndef();
                case Null:
                    return Value.makeNull();
                case Never:
                    return Value.makeNone();
                case Symbol:
                    System.err.println("Symbols should be inprecise, they are not."); // TODO:
                    SpecObjects hostObject = SpecObjects.getObjectAbstraction(info.path, new TypeWithContext(t, info.context));
                    return Value.makeObject(ObjectLabel.make(hostObject, ObjectLabel.Kind.SYMBOL));
                default:
                    throw new RuntimeException("Unhandled TypeKind: " + t);
            }
        }

        private Value makeString() {
            if (info.options.betterAnyString) {
                if(defaultAnyString == null) {
                    State initial = c.getAnalysisLatticeElement().getState(c.getAnalysis().getInitialStateBuilder().initialBlockAndContext);
                    List<ObjectLabel> lbs = newList();
                    lbs.add(InitialStateBuilder.OBJECT_PROTOTYPE);
                    lbs.add(InitialStateBuilder.FUNCTION_PROTOTYPE);
                    lbs.add(InitialStateBuilder.ARRAY_PROTOTYPE);
                    lbs.add(InitialStateBuilder.STRING_PROTOTYPE);
                    lbs.add(InitialStateBuilder.BOOLEAN_PROTOTYPE);
                    lbs.add(InitialStateBuilder.NUMBER_PROTOTYPE);
                    lbs.add(InitialStateBuilder.DATE_PROTOTYPE);
                    lbs.add(InitialStateBuilder.PROXY_PROTOTYPE);
                    lbs.add(InitialStateBuilder.REGEXP_PROTOTYPE);
                    //lbs.add(InitialStateBuilder.GLOBAL);
                    lbs.add(InitialStateBuilder.DATE_PROTOTYPE);

                    Set<String> forbidden = initial.getProperties(lbs, false, false, false, false)
                            .getMaybe()
                            .stream()
                            .map(k -> k.asStringKey().asString())
                            .collect(Collectors.toSet());
                    forbidden.add("prototype");
                    forbidden.add(PKey.__PROTO__.asString());

                    defaultAnyString = Value.makeAnyStrNotUInt().removeStrings(forbidden);
                }
                return defaultAnyString;
            }
            return Value.makeAnyStr();
        }

        @Override
        public Value visit(TupleType t, MiscInfo info) {
            return withNewObject(info.labelToUse, label -> {
                for (int i = 0; i < t.getElementTypes().size(); i++) {
                    Type componentType = t.getElementTypes().get(i);
                    String indexName = i + "";
                    writeProperty(label, indexName, componentType, info);
                }
                writeProperty(label, "length", new NumberLiteral(t.getElementTypes().size()), info);
            });
        }

        @Override
        public Value visit(UnionType t, MiscInfo info) {
            Value unionValue = Value.makeNone();
            List<Type> unionTypes = t.getElements();
            for (final Type componentType : unionTypes) {
                Value componentValue = SpecInstantiator.this.instantiate(componentType, info, "<union-member>");
                unionValue = unionValue.join(componentValue);
            }
            return unionValue;
        }

        @Override
        public Value visit(TypeParameterType t, MiscInfo info) {
            if (info.context.containsKey(t)) {
                TypeWithContext lookup = info.context.get(t);
                return instantiate(lookup.getType(), info.withContext(lookup.getTypeContext()), null);
            } else {
                if (t.getConstraint() == null && !TypesUtil.isEmptyInterface(t.getConstraint())) {
                    System.err.println("Just returning a dummy object for unbound type parameters."); // TODO:
                    return instantiate(unboundTypeParameter, info, null);
                } else {
                    System.err.println("Not returning anything that actually extends a typeParameter"); // TODO:
                    IntersectionType intersection = new IntersectionType();
                    intersection.setElements(Arrays.asList(unboundTypeParameter, t.getConstraint()));
                    return instantiate(intersection, info, null);
                }
            }
        }

        private final InterfaceType unboundTypeParameter = SpecReader.makeEmptySyntheticInterfaceType();
        {
            unboundTypeParameter.getDeclaredProperties().put("_isUnboundGeneric", new BooleanLiteral(true));
        }

        @Override
        public Value visit(StringLiteral t, MiscInfo miscInfo) {
            return Value.makeSpecialStrings(Collections.singletonList(t.getText()));
        }

        @Override
        public Value visit(BooleanLiteral t, MiscInfo miscInfo) {
            return Value.makeBool(t.getValue());
        }

        @Override
        public Value visit(NumberLiteral t, MiscInfo miscInfo) {
            return Value.makeNum(t.getValue());
        }

        @Override
        public Value visit(IntersectionType t, MiscInfo miscInfo) {
            throw new RuntimeException("Cannot construct IntersectionType");
        }

        @Override
        public Value visit(ClassInstanceType t, MiscInfo miscInfo) {
            if (SpecInstantiator.this.info.freeGenericsFinder.hasThisTypes(t)) {
                miscInfo = miscInfo.withContext(miscInfo.context.withThisType(t));
            }
            System.err.println("Inaccurately modelling class instances");
            return info.typesUtil.createClassInstanceType(((ClassType) t.getClassType())).accept(this, miscInfo); // TODO:
        }

        @Override
        public Value visit(ThisType t, MiscInfo miscInfo) {
            if (miscInfo.context.getThisType() == null) {
                throw new NullPointerException();
            }
            return instantiate(miscInfo.context.getThisType(), miscInfo, null);
        }

        @Override
        public Value visit(IndexType t, MiscInfo miscInfo) {
            throw new RuntimeException("Not implemented...");
        }

        @Override
        public Value visit(IndexedAccessType t, MiscInfo miscInfo) {
            throw new RuntimeException("Not implemented...");
        }

    }

    private class MiscInfo {

        public final List<String> path;
        public final TypeContext context;
        public final ObjectLabel labelToUse;

        MiscInfo(String initialPath, TypeContext context, ObjectLabel labelToUse) {
            this(Arrays.asList(initialPath.split("\\.")), context, labelToUse);
        }

        MiscInfo(List<String> path, TypeContext context, ObjectLabel labelToUse) {
            this.context = context;
            this.labelToUse = labelToUse;
            this.path = path;
        }

        public MiscInfo withContext(TypeContext typeContext) {
            return new MiscInfo(path, typeContext, labelToUse);
        }

        public MiscInfo apendPath(String path) {
            return new MiscInfo(Util.concat(this.path, Collections.singletonList(path)), context, labelToUse);
        }

        public MiscInfo withlabel(ObjectLabel label) {
            return new MiscInfo(path, context, label);
        }
    }

    private class ObjectLabelKindDecider implements TypeVisitor<ObjectLabel.Kind> {

        @Override
        public ObjectLabel.Kind visit(AnonymousType t) {
            throw new RuntimeException("Not implemented...");
        }

        @Override
        public ObjectLabel.Kind visit(ClassType t) {
            return ObjectLabel.Kind.FUNCTION;
        }

        @Override
        public ObjectLabel.Kind visit(GenericType t) {
            return t.toInterface().accept(this);
        }

        @Override
        public ObjectLabel.Kind visit(InterfaceType t) {
            if (info.typeNames.get(t) != null) {
                switch (info.typeNames.get(t)) {
                    case "Array": return ObjectLabel.Kind.ARRAY;
                    case "Function": return ObjectLabel.Kind.FUNCTION;
                    case "RegExp": return ObjectLabel.Kind.REGEXP;
                    case "Date": return ObjectLabel.Kind.DATE;
                    case "String": return ObjectLabel.Kind.STRING;
                    case "Number": return ObjectLabel.Kind.NUMBER;
                    case "Boolean": return ObjectLabel.Kind.BOOLEAN;
                    case "Error": return ObjectLabel.Kind.ERROR;
                    case "Math": return ObjectLabel.Kind.MATH;
                }
            }
            ObjectLabel.Kind kind;
            if (t.getDeclaredCallSignatures().isEmpty() && t.getDeclaredConstructSignatures().isEmpty()) {
                kind = ObjectLabel.Kind.OBJECT;
            } else {
                kind = ObjectLabel.Kind.FUNCTION;
            }
            if (kind == ObjectLabel.Kind.OBJECT) {
                for (ObjectLabel.Kind label : t.getBaseTypes().stream().map(subType -> subType.accept(this)).collect(Collectors.toList())) {
                    if (label != ObjectLabel.Kind.OBJECT) {
                        kind = label;
                    }
                }
            }
            return kind;
        }

        @Override
        public ObjectLabel.Kind visit(ReferenceType t) {
            return t.getTarget().accept(this);
        }

        @Override
        public ObjectLabel.Kind visit(SimpleType t) {
            switch (t.getKind()) {
                case Undefined:
                case Any:
                case Void:
                case Boolean:
                case String:
                case Never:
                case Number:
                case Null:
                case Enum:
                    return null;
                case Symbol:
                    return ObjectLabel.Kind.SYMBOL;
                case Object:
                    return ObjectLabel.Kind.OBJECT;
                default:
                    throw new RuntimeException("Not implemented: " + t.getKind());
            }
        }

        @Override
        public ObjectLabel.Kind visit(TupleType t) {
            return ObjectLabel.Kind.ARRAY;
        }

        @Override
        public ObjectLabel.Kind visit(UnionType t) {
            return t.getElements().stream().map(sub -> sub.accept(this)).reduce(null, (a, b) -> {
                if (a == null || b == null) {
                    return a != null ? a : b;
                }
                if (a == ObjectLabel.Kind.OBJECT || b == ObjectLabel.Kind.OBJECT) {
                    return a != ObjectLabel.Kind.OBJECT ? a : b;
                }
                if (a == b) {
                    return a;
                }
                throw new RuntimeException("Dont know what to do about " + a + " and " + b);
            });
        }

        @Override
        public ObjectLabel.Kind visit(TypeParameterType t) {
            return null; // If the parameter points to some object, i get asked about that specific one.
        }

        @Override
        public ObjectLabel.Kind visit(StringLiteral t) {
            return null;
        }

        @Override
        public ObjectLabel.Kind visit(BooleanLiteral t) {
            return null;
        }

        @Override
        public ObjectLabel.Kind visit(NumberLiteral t) {
            return null;
        }

        @Override
        public ObjectLabel.Kind visit(IntersectionType t) {
            // if you think about it, it acts the same way as a union-type.
            UnionType union = new UnionType();
            union.setElements(t.getElements());
            return union.accept(this);
        }

        @Override
        public ObjectLabel.Kind visit(ClassInstanceType t) {
            return info.typesUtil.createClassInstanceType(((ClassType) t.getClassType())).accept(this);
        }

        @Override
        public ObjectLabel.Kind visit(ThisType t) {
            return null;  // If the thisType points to some object, i get asked about that specific one.
        }

        @Override
        public ObjectLabel.Kind visit(IndexType t) {
            throw new RuntimeException("Not implemented...");
        }

        @Override
        public ObjectLabel.Kind visit(IndexedAccessType t) {
            throw new RuntimeException("Not implemented...");
        }


    }
}
