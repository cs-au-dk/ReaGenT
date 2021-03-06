package dk.webbies.tajscheck.benchmark.options.staticOptions.filter;

import dk.au.cs.casa.typescript.types.*;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.analysis.HostAPIs;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.solver.GenericSolver;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.buildprogram.TypeChecker;
import dk.webbies.tajscheck.buildprogram.typechecks.TypeCheck;
import dk.webbies.tajscheck.tajstester.TajsTypeChecker;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
import dk.webbies.tajscheck.tajstester.typeCreator.SpecInstantiator;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newMap;

public class CopyObjectInstantiation implements SpecInstantiator.InstantiationFilter, TypeVisitorWithArgument<Value, CopyObjectInstantiation.Arg> {

    // splitting the value, but keeping the objects in one lump.
    public static List<Value> split(Value v) {
        if (v.isNone()) {
            return Collections.emptyList();
        }
        if (v.restrictToNotObject().isNone()) {
            return Collections.singletonList(v);
        }
        Set<ObjectLabel> labels = v.getObjectLabels();
        v = v.restrictToNotObject();
        List<Value> list = TajsTypeChecker.split(v);
        return Util.concat(list, Collections.singletonList(Value.makeObject(labels)));
    }

    @Override
    public Value filter(TypeWithContext type, Value value, Solver.SolverInterface c, BenchmarkInfo info) {
        if (value == null) {
            return null;
        }
        value = UnknownValueResolver.getRealValue(value, c.getState());

        Set<ObjectLabel> labels = value.getObjectLabels();
        Set<ObjectLabel> filteredAwayObjectLabels = new HashSet<>();
        value = value
                .restrictToNotObject()
                .join(
                        Value.makeObject(labels.stream().filter(Util.not(
                                label -> {
                                    if (
                                            label.toString().equals("@Object.prototype[native]") ||
                                            label.toString().equals("@Function.prototype[native]") ||
                                            label.getHostObject() instanceof CopiedObjectLabel && ((CopiedObjectLabel) label.getHostObject()).type.equals(type)
                                    ) {
                                        filteredAwayObjectLabels.add(label);
                                        return true;
                                    }  else {
                                        return false;
                                    }
                                }
                        )).collect(Collectors.toSet()))
                );

        List<Value> results = split(value).stream().map(v -> {
            try {
                return type.getType().accept(this, new Arg(type.getTypeContext(), v, c, info));
            } catch (NoSuchTypePossible e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        Value resultValue = Value.join(results).join(Value.makeObject(filteredAwayObjectLabels));
        if (resultValue.isNone()) {
            throw new NoSuchTypePossible();
        }
        return resultValue;
    }

    public static final class NoSuchTypePossible extends RuntimeException {
        public NoSuchTypePossible() {}
    }


    public Value filter(Type type, Arg arg) {
        return filter(new TypeWithContext(type, arg.context), arg.value, arg.c, arg.info);
    }

    private List<TypeViolation> typeCheck(Type t, Arg arg) {
        TajsTypeChecker typeChecker = new TajsTypeChecker(null, arg.c, arg.info, null);
        List<TypeCheck> typeChecks = TypeChecker.getTypeChecks(t, arg.context, arg.info, 0);
        return typeChecker.getTypeViolations(new TypeWithContext(t, arg.context), arg.value, typeChecks, "");
    }

    @Override
    public Value visit(AnonymousType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(ClassType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(GenericType t, Arg arg) {
        return visit(t.toInterface(), arg);
    }

    @Override
    public Value visit(InterfaceType t, Arg arg) {
        Value value = arg.value;
        TypeContext context = arg.context;
        Solver.SolverInterface c = arg.c;

        if (!value.isMaybeObject()) {
            throw new NoSuchTypePossible();
        }

        Set<ObjectLabel> objectLabels = value.getObjectLabels().stream().filter(label -> label.getKind() != ObjectLabel.Kind.SYMBOL).collect(Collectors.toSet()); // the symbol is mostly to remove my "any" type.

        assert value.restrictToNotSymbol().restrictToNotObject().isNone();
        assert t.getBaseTypes().isEmpty(); // TypesUtil.constructSyntheticInterfaceWithBaseTypes

        if (!t.getDeclaredCallSignatures().isEmpty() || !t.getDeclaredConstructSignatures().isEmpty()) {
            assert t.getDeclaredNumberIndexType() == null;
            assert t.getDeclaredStringIndexType() == null;
            assert t.getDeclaredProperties().isEmpty();
            return arg.value;
        }

        assert t.getDeclaredNumberIndexType() == null;
        assert t.getDeclaredStringIndexType() == null;



        if (objectLabels.stream().allMatch(orgLabel -> orgLabel.getHostObject() != null && orgLabel.getHostObject() instanceof CopiedObjectLabel)) {
            return value;
        }

        Set<ObjectLabel> orgLabels = objectLabels.stream().filter(Util.not(orgLabel -> orgLabel.getHostObject() != null && orgLabel.getHostObject() instanceof CopiedObjectLabel)).collect(Collectors.toSet());

        if (!orgLabels.stream().allMatch(label -> label.getKind() == orgLabels.iterator().next().getKind())) {
            Map<ObjectLabel.Kind, ObjectLabel> labelMap = new HashMap<>();
            orgLabels.forEach(label -> labelMap.put(label.getKind(), label));
            return Value.join(labelMap.values().stream().map(labels -> {
                return visit(t, new Arg(arg.context, Value.makeObject(labels), arg.c, arg.info));
            }).collect(Collectors.toSet()));
        }


        ObjectLabel label = ObjectLabel.make(new CopiedObjectLabel(orgLabels, new TypeWithContext(t, arg.context)), orgLabels.iterator().next().getKind());
        if (!c.getState().getStore().containsKey(label)) {
            c.getState().newObject(label); // this takes care of summarizing old objects (of which there are always none...)
        }

        if (c.getState().getObject(label, false).getInternalPrototype() == null) {
            c.getState().getObject(label, true).setInternalPrototype(Value.makeNull());
        }


        addToExistingLabels(orgLabels, label, c.getState());

        Obj object = c.getState().getObject(label, true);
        {
            Value originalPrototype = readSomething(orgLabels, c.getState(), (objectLabel, obj) -> obj.getInternalPrototype());
            Value internalPrototype = originalPrototype;
            if (originalPrototype.getObjectLabels() != null && originalPrototype.getObjectLabels().stream().anyMatch(lbl -> lbl.getKind() != ObjectLabel.Kind.SYMBOL)) {
                object.setInternalPrototype(Value.makeNull());
                internalPrototype = filter(new TypeWithContext(t, context), originalPrototype, c, arg.info);
                if (internalPrototype == null) {
                    throw new NoSuchTypePossible();
                }
            }
            Value previous = UnknownValueResolver.getInternalPrototype(label, c.getState(), false);
            if (hasSomethingNew(internalPrototype, previous, c.getState())) {
                object.setInternalPrototype(internalPrototype);
            }
        }


        {
            Value nonArrayProperty = readSomething(orgLabels, c.getState(), (objectLabel, obj) -> obj.getDefaultOtherProperty());
            Value previous = UnknownValueResolver.getDefaultOtherProperty(label, c.getState());
            if (hasSomethingNew(nonArrayProperty, previous, c.getState())) {
                object.setDefaultOtherProperty(nonArrayProperty);
            }
        }

        assert !readSomething(orgLabels, c.getState(), (objectLabel, obj) -> obj.getDefaultNumericProperty()).isMaybePresent();
        assert !readSomething(orgLabels, c.getState(), (objectLabel, obj) -> obj.getInternalValue()).isMaybePresent();
        // TODO: scope, scope_unknown

        Set<PKey> properties = readProperties(orgLabels, c.getState());

        Map<String, Type> propDecs = t.getDeclaredProperties();
        for (PKey propKey : properties) {
            Value propValue = readSomething(orgLabels, c.getState(), (subLabel, obj) -> UnknownValueResolver.getProperty(subLabel, propKey, c.getState(), false));
            if (propValue.isMaybeAbsent() && propValue.restrictToNotAbsent().isNone()) {
                object.setProperty(propKey, propValue);
                continue;
            }
            assert propKey instanceof PKey.StringPKey;
            String propName = ((PKey.StringPKey) propKey).getStr();
            Value existingPropValue = object.getProperty(propKey);
            Value newPropValue;
            if (propDecs.containsKey(propName)) {
                newPropValue = filter(new TypeWithContext(propDecs.get(propName), arg.context), propValue, c, arg.info);
            } else {
                newPropValue = propValue;
            }
            if (propName.equals("__proto__")) {
                if (newPropValue.getObjectLabels() != null && newPropValue.getObjectLabels().stream().anyMatch(lbl -> lbl.getKind() != ObjectLabel.Kind.SYMBOL)) {
                    newPropValue = filter(new TypeWithContext(t, context), newPropValue, c, arg.info);
                    if (newPropValue == null) {
                        throw new NoSuchTypePossible();
                    }
                }
            }
            existingPropValue = existingPropValue == null ? null : UnknownValueResolver.getRealValue(existingPropValue, c.getState());
            if (hasSomethingNew(newPropValue, existingPropValue, c.getState())) {
                object.setProperty(propKey, newPropValue.setAttributes(propValue));
            }
        }

        return Value.makeObject(label);
    }

    private Set<PKey> readProperties(Set<ObjectLabel> labels, State state) {
        return labels.stream().map(label -> UnknownValueResolver.getProperties(label, state).keySet()).reduce(new HashSet<>(), Util::reduceSet);
    }

    private Value readSomething(Set<ObjectLabel> labels, State state, BiFunction<ObjectLabel, Obj, Value> foo) {
        return Value.join(labels.stream().map(label -> foo.apply(label, state.getObject(label, false))).collect(Collectors.toList()));
    }

    public static boolean hasSomethingNew(Value newValue, Value existing, State state) {
        if (newValue == null || existing == null) {
            return newValue != null || existing != null;
        }
        newValue = allLabelsToSingleton(UnknownValueResolver.getRealValue(newValue, state)).restrictToNonAttributes();
        existing = allLabelsToSingleton(UnknownValueResolver.getRealValue(existing, state)).restrictToNonAttributes();
        return !existing.join(newValue).equals(existing);
    }

    public static Value allLabelsToSingleton(Value value) {
        value = Value.makeNone().join(value);
        Set<ObjectLabel> labels = value.getObjectLabels();
        value = value.restrictToNotObject();
        value = value.join(Value.makeObject(labels.stream().map(label -> {
            if (label.isSingleton()) {
                return label;
            } else {
                return label.makeSingleton();
            }
        }).collect(Collectors.toSet())));
        return value;
    }

    @Override
    public Value visit(ReferenceType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(SimpleType t, Arg arg) {
        List<TypeViolation> typeViolations = typeCheck(t, arg);

        if (typeViolations.isEmpty()) {
            return arg.value;
        } else {
            throw new NoSuchTypePossible();
        }
    }

    @Override
    public Value visit(TupleType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(UnionType t, Arg arg) {
        List<Value> possibleValues = t.getElements().stream().map(element -> {
            try {
                return filter(element, arg);
            } catch (NoSuchTypePossible e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (possibleValues.isEmpty()) {
            throw new NoSuchTypePossible();
        } else {
            return Value.join(possibleValues);
        }
    }

    @Override
    public Value visit(IntersectionType t, Arg arg) {
        Value value = arg.value;
        for (Type type : t.getElements()) {
            value = filter(new TypeWithContext(type, arg.context), value, arg.c, arg.info);
        }
        return value;
    }

    @Override
    public Value visit(TypeParameterType t, Arg arg) {
        if (arg.context.get(t) == null) {
            return arg.value;
        }
        throw new RuntimeException();
    }

    @Override
    public Value visit(StringLiteral t, Arg arg) {
        if (arg.value.isMaybeStr(t.getText())) {
            return Value.makeStr(t.getText());
        } else {
            throw new NoSuchTypePossible();
        }
    }

    @Override
    public Value visit(BooleanLiteral t, Arg arg) {
        if (t.getValue()) {
            if (arg.value.isMaybeTrue()) {
                return Value.makeBool(true);
            } else {
                throw new NoSuchTypePossible();
            }
        } else {
            if (arg.value.isMaybeFalse()) {
                return Value.makeBool(false);
            } else {
                throw new NoSuchTypePossible();
            }
        }
    }

    @Override
    public Value visit(NumberLiteral t, Arg arg) {
        if (arg.value.isMaybeNum(t.getValue())) {
            return Value.makeNum(t.getValue());
        } else {
            throw new NoSuchTypePossible();
        }
    }

    @Override
    public Value visit(ClassInstanceType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(ThisType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(IndexType t, Arg arg) {
        throw new RuntimeException();
    }

    @Override
    public Value visit(IndexedAccessType t, Arg arg) {
        throw new RuntimeException();
    }


    public static final class Arg {
        private final TypeContext context;
        private final Value value;
        private final Solver.SolverInterface c;
        private final BenchmarkInfo info;

        public Arg(TypeContext context, Value value, GenericSolver<State, Context, CallEdge, IAnalysisMonitoring, Analysis>.SolverInterface c, BenchmarkInfo info) {
            this.context = context;
            this.value = value;
            this.c = c;
            this.info = info;
        }
    }

    public static class CopiedObjectLabel implements HostObject {
        @Override
        public HostAPI getAPI() {
            return HostAPIs.SPEC;
        }

        private final Set<ObjectLabel> orgLabels;
        private final TypeWithContext type;

        public CopiedObjectLabel(Set<ObjectLabel> orgLabels, TypeWithContext type) {
            this.orgLabels = orgLabels;
            this.type = type;
        }

        public Set<ObjectLabel> getOrgLabels() {
            return orgLabels;
        }

        public TypeWithContext getType() {
            return type;
        }

        @Override
        public String toString() {
            return "CopiedObjectLabel;";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CopiedObjectLabel that = (CopiedObjectLabel) o;
            return Objects.equals(orgLabels, that.orgLabels) &&
                    Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(orgLabels, type);
        }
    }

    private void addToExistingLabels(Set<ObjectLabel> orgLabels, ObjectLabel newLabel, State state) {
        if (orgLabels == null) {
            return;
        }
        for (ObjectLabel objlabel2 : state.getStore().keySet()) { // redirect.
            try {
                if (orgLabels.stream().anyMatch(orgLabel -> state.getObject(objlabel2, false).containsObjectLabel(orgLabel))) {
                    Obj obj = state.getObject(objlabel2, true);
                    addToExistingLabels(objlabel2, obj, orgLabels, newLabel, state);
                }
            } catch (NullPointerException e) {
                continue; // TODO: Ugly sauce.
            }
        }
    }


    private void addToExistingLabels(ObjectLabel objectLabel, Obj obj, Set<ObjectLabel> oldlabels, ObjectLabel newlabel, State state) {
        Map<PKey, Value> newproperties = newMap();
        for (Map.Entry<PKey, Value> me : obj.getProperties().entrySet())
            newproperties.put(me.getKey(), addToExistingLabels(me.getValue(), oldlabels, newlabel));
        obj.setProperties(newproperties);

        obj.setScopeChain(addToExistingScope(UnknownValueResolver.getScopeChain(objectLabel, state), oldlabels, newlabel));

        obj.setDefaultNumericProperty(addToExistingLabels(obj.getDefaultNumericProperty(), oldlabels, newlabel));
        obj.setDefaultOtherProperty(addToExistingLabels(obj.getDefaultOtherProperty(), oldlabels, newlabel));
        obj.setInternalPrototype(addToExistingLabels(obj.getInternalPrototype(), oldlabels, newlabel));
        obj.setInternalValue(addToExistingLabels(obj.getInternalValue(), oldlabels, newlabel));
    }

    private ScopeChain addToExistingScope(ScopeChain scopeChain, Set<ObjectLabel> oldlabels, ObjectLabel newlabel) {
        if (scopeChain == null) {
            return null;
        }
        return ScopeChain.make(addToExistingLabels(scopeChain.getObject(), oldlabels, newlabel), addToExistingScope(scopeChain.next(), oldlabels, newlabel));
    }

    private Set<ObjectLabel> addToExistingLabels(Set<ObjectLabel> objects, Set<ObjectLabel> oldlabels, ObjectLabel newlabel) {
        if (objects.stream().anyMatch(oldlabels::contains)) {
            return Util.concatSet(objects, Collections.singleton(newlabel));
        } else {
            return objects;
        }
    }

    private Value addToExistingLabels(Value value, Set<ObjectLabel> oldLabels, ObjectLabel newlabel) {
        if (value.getObjectLabels().stream().anyMatch(oldLabels::contains)) {
            return value.join(Value.makeObject(newlabel));
        } else {
            return value;
        }
    }
}
