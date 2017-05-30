package dk.webbies.tajscheck.buildprogram;

import dk.au.cs.casa.typescript.types.*;
import dk.brics.tajs.analysis.PropVarOperations;
import dk.brics.tajs.analysis.Unsoundness;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.options.Options;
import dk.brics.tajs.solver.GenericSolver;
import dk.brics.tajs.util.Pair;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.paser.AST.Expression;
import dk.webbies.tajscheck.paser.AST.Operator;
import dk.webbies.tajscheck.paser.AST.Statement;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.paser.AstBuilder.*;

/**
 * Created by erik1 on 02-05-2017.
 */
public class TAJSTypeChecker {
    private final BenchmarkInfo info;
    private final Map<Integer, Function<Pair<Value, State>, Bool>> callbacks = new HashMap<>();
    private final PropVarOperations pv;
    private TAJSTypeChecker(BenchmarkInfo info) {
        this.info = info;
        pv = new PropVarOperations(new Unsoundness(Options.get().getUnsoundness(), (n, s, m) -> {}));
    }

    public static TAJSTypeChecker get(BenchmarkInfo info) {
        return info.getAttribute(TAJSTypeChecker.class, "instance", () -> new TAJSTypeChecker(info));
    }

    Expression callback(Function<Pair<Value, State>, Bool> func, Expression exp) {
        int index = callbacks.size();
        callbacks.put(index, func);
        return call(identifier("TAJS_custom"), number(index), exp);
    }

    // Creates a statement that returns false if the type-check fails. (And we log that it failed!)
    Statement assertResultingType(TypeWithContext type, Expression exp, String path, String testType) {
        Function<Pair<Value, State>, Bool> func = (pair) -> checkType(type, pair.getFirst(), pair.getSecond());

        String expected = TypeChecker.createIntersection(type.getType().accept(new TypeChecker.CreateTypeCheckVisitor(info), new TypeChecker.Arg(type.getTypeContext(), 0))).getExpected();

        // assert(cond, path, expected, actual, iteration, descrip);
        return block(
                ifThen(
                        unary(Operator.NOT, call(identifier("assert"), callback(func, exp), string(path), string(expected), exp, identifier("i"), string(testType))),
                        Return(bool(false))
                )
        );
    }




    // Creates an expression that returns whether the type-check passed or not.
    Expression checkResultingType(TypeWithContext type, Expression exp, String path) {
        return callback((pair) -> checkType(type, pair.getFirst(), pair.getSecond()), exp);
    }

    private Map<Pair<TypeWithContext, Pair<Value, State>>, Bool> typeCheckingCache = new HashMap<>();

    private Bool checkType(TypeWithContext type, Value value, State state) {
        Pair<TypeWithContext, Pair<Value, State>> key = Pair.make(type, Pair.make(value, state));
        if (typeCheckingCache.containsKey(key)) {
            return typeCheckingCache.get(key); // TODO: Test the number of cache-hits.
        }
        typeCheckingCache.put(key, Value.makeBool(true)); // We assume that this is true, while we go through children (induction hypothesis).

        Bool result;
        if (!value.isMaybePresent()) {
            result = FALSE();
        } else {
            result = type.getType().accept(new TypeCheckerVisitor(), new Arg(type.getTypeContext(), state, value));
        }

        typeCheckingCache.put(key, result);
        return result;
    }

    public BiFunction<GenericSolver.SolverInterface, List<Value>, Value> getCustomFunction() {
        return (s, args) -> {
            assert args.size() == 2;
            //noinspection ConstantConditions
            int index = args.get(0).getNum().intValue();
            Value value = args.get(1);
            pv.setSolverInterface(s);
            return Value.makeBool(callbacks.get(index).apply(Pair.make(value, (State)s.getState())));
        };
    }
    
    private static final class Arg {
        private final TypeContext context;
        private final State state;
        private final Value value;

        private Arg(TypeContext context, State state, Value value) {
            this.context = context;
            this.state = state;
            this.value = value;
        }

        Arg withValue(Value value) {
            return new Arg(this.context, this.state, UnknownValueResolver.getRealValue(value, this.state));
        }

        Arg withContext(TypeContext typeContext) {
            return new Arg(typeContext, this.state, this.value);
        }
    }

    private Value FALSE() {
        return Value.makeBool(false);
    }

    private Value MAYBE() {
        return Value.makeAnyBool();
    }

    private Value TRUE() {
        return Value.makeBool(true);
    }

    private final class TypeCheckerVisitor implements TypeVisitorWithArgument<Bool, Arg> {

        @Override
        public Bool visit(AnonymousType t, Arg arg) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(ClassType t, Arg arg) {
            return recurse(TypesUtil.classToInterface(t, info.freeGenericsFinder), arg);
        }

        @Override
        public Bool visit(GenericType t, Arg arg) {
            return recurse(t.toInterface(), arg);
        }

        @Override
        public Bool visit(InterfaceType t, Arg arg) {
            if (info.freeGenericsFinder.hasThisTypes(t)) {
                arg = arg.withContext(arg.context.withThisType(t));
            }

            Value value = arg.value;

            List<Bool> results = new ArrayList<>();
            boolean failed = false;
            boolean succeeded = false;

            if (value.isMaybeOtherThanObject()) {
                failed = true;
            }
            if (value.isMaybeObject()) {
                succeeded = true;
                assert !value.getObjectLabels().isEmpty();
            }

            for (Type base : t.getBaseTypes()) {
                results.add(recurse(base, arg));
            }

            if (t.getDeclaredStringIndexType() != null) {
                results.add(recurse(t.getDeclaredStringIndexType(), arg.withValue(pv.readPropertyValue(value.getObjectLabels(), Value.makeAnyStr()))));
            }
            if (t.getDeclaredNumberIndexType() != null) {
                results.add(recurse(t.getDeclaredNumberIndexType(), arg.withValue(pv.readPropertyValue(value.getObjectLabels(), Value.makeAnyStrUInt()))));
            }

            if (!t.getDeclaredCallSignatures().isEmpty() || !t.getDeclaredConstructSignatures().isEmpty()) {
                boolean functionFailed = false;
                boolean functionSucceeded = false;
                if (value.isMaybeObject()) {
                    for (ObjectLabel label : value.getObjectLabels()) {
                        if (label.getKind() != ObjectLabel.Kind.FUNCTION) {
                            functionFailed = true;
                        } else {
                            functionSucceeded = true;
                        }
                    }
                    results.add(boolFromFail(functionFailed, functionSucceeded));
                } else {
                    results.add(FALSE());
                }
            }

            for (Map.Entry<String, Type> entry : t.getDeclaredProperties().entrySet()) {
                Value propValue = pv.readPropertyValue(value.getObjectLabels(), entry.getKey());
                results.add(recurse(entry.getValue(), arg.withValue(propValue)));
            }


            return worstCase(Util.concat(results, Collections.singletonList(boolFromFail(failed, succeeded))));
        }

        private Bool recurse(Type type, Arg arg) {
            return checkType(new TypeWithContext(type, arg.context), arg.value, arg.state);
        }

        private Bool boolFromFail(boolean failed, boolean succeeded) {
            if (failed && succeeded) {
                return Value.makeAnyBool();
            }
            if (succeeded) {
                return TRUE();
            }
            if (failed) {
                return FALSE();
            }
            throw new RuntimeException();
        }

        @Override
        public Bool visit(ReferenceType t, Arg arg) {
            return recurse(t.getTarget(), arg.withContext(new TypesUtil(info).generateParameterMap(t, arg.context)));
        }

        @Override
        public Bool visit(SimpleType t, Arg arg) {
            boolean failed = false;
            boolean succeeded = false;
            Value value = arg.value;

            switch (t.getKind()) {
                case Boolean:
                    if (value.isMaybeAnyBool() || value.isMaybeTrue() || value.isMaybeFalse()) {
                        succeeded = true;
                    }
                    if (value.isMaybeOtherThanBool()) {
                        failed = true;
                    }
                    break;
                case String:
                    if (!value.isNotStr()) {
                        succeeded = true;
                    }
                    if (value.isMaybeOtherThanStr()) {
                        failed = true;
                    }
                    break;
                case Number:
                    if (!value.isNotNum()) {
                        succeeded = true;
                    }
                    if (value.isMaybeOtherThanNum()) {
                        failed = true;
                    }
                    break;
                case Any:
                    return TRUE();
                case Undefined:
                    if (value.isMaybeUndef()) {
                        succeeded = true;
                    }
                    if (value.isMaybeOtherThanUndef()) {
                        failed = true;
                    }
                    break;
                case Never:
                    if (value.isMaybeAbsent()) {
                        succeeded = true;
                    }
                    if (value.isMaybePresent()) {
                        failed = true;
                    }
                    break;
                case Null:
                    if (value.isMaybeNull()) {
                        succeeded = true;
                    }
                    if (value.isMaybeOtherThanNull()) {
                        failed = true;
                    }
                    break;
                case Object:
                    if (value.isMaybeObject()) {
                        succeeded = true;
                    }
                    if (value.isMaybeOtherThanObject()) {
                        failed = true;
                    }
                    break;
                case Void: // Void has been pre-transformed to only be direct returns of functions. And in that position it is equivalent to Any.
                    return TRUE();
                case Symbol:
                    if (value.isMaybeOtherThanObject()) {
                        failed = true;
                    }
                    for (ObjectLabel label : value.getObjectLabels()) {
                        if (label.getKind() == ObjectLabel.Kind.SYMBOL) {
                            succeeded = true;
                        } else {
                            failed = true;
                        }
                    }
                    break;
                default:
                    throw new RuntimeException(t.getKind().toString());
            }

            return boolFromFail(failed, succeeded);
        }

        @Override
        public Bool visit(TupleType t, Arg arg) {
            Value value = arg.value;
            if (!value.isMaybeObject()) {
                return FALSE();
            }

            List<Bool> results = new ArrayList<>();

            for (ObjectLabel label : value.getObjectLabels()) {
                if (label.getKind() == ObjectLabel.Kind.ARRAY) {
                    results.add(TRUE());
                } else {
                    results.add(FALSE());
                }
            }

            // I could check on the length of the array. I don't, I just check on each of the properties.

            for (int index = 0; index < t.getElementTypes().size(); index++) {
                Type elementType = t.getElementTypes().get(index);
                results.add(recurse(elementType, arg.withValue(pv.readPropertyValue(value.getObjectLabels(), Integer.toString(index)))));
            }

            return worstCase(results);
        }

        @Override
        public Bool visit(UnionType t, Arg arg) {
            Collection<Value> split = split(arg.value);

            Bool result = join(split.stream().map(value -> bestCase(t.getElements().stream().map(type -> recurse(type, arg.withValue(value))).collect(Collectors.toList()))).collect(Collectors.toList()));
            return result;
        }

        @SuppressWarnings("Duplicates")
        private Bool join(List<Bool> cases) {
            boolean isTrue = false;
            boolean isMaybe = false;
            boolean isFalse = false;
            for (Bool bool : cases) {
                if (bool.isMaybeFalseButNotTrue()) {
                    isFalse = true;
                }
                if (bool.isMaybeTrueButNotFalse()) {
                    isTrue = true;
                }
                if (bool.isMaybeAnyBool()) {
                    isMaybe = true;
                }
            }
            if (isMaybe || (isTrue && isFalse)) {
                return MAYBE();
            }
            if (isTrue) {
                return TRUE();
            }
            return FALSE();
        }

        @SuppressWarnings("Duplicates")
        private Bool worstCase(List<Bool> cases) {
            boolean isTrue = false;
            boolean isMaybe = false;
            boolean isFalse = false;
            for (Bool bool : cases) {
                if (bool.isMaybeFalseButNotTrue()) {
                    isFalse = true;
                }
                if (bool.isMaybeTrueButNotFalse()) {
                    isTrue = true;
                }
                if (bool.isMaybeAnyBool()) {
                    isMaybe = true;
                }
            }
            if (isFalse) {
                return FALSE();
            }
            if (isMaybe) {
                return MAYBE();
            }
            return TRUE();
        }

        @SuppressWarnings("Duplicates")
        private Bool bestCase(List<Bool> cases) {
            boolean isTrue = false;
            boolean isMaybe = false;
            boolean isFalse = false;
            for (Bool bool : cases) {
                if (bool.isMaybeFalse()) {
                    isFalse = true;
                }
                if (bool.isMaybeTrue()) {
                    isTrue = true;
                }
                if (bool.isMaybeAnyBool()) {
                    isMaybe = true;
                }
            }
            if (isTrue) {
                return TRUE();
            }
            if (isMaybe) {
                return MAYBE();
            }
            return FALSE();
        }

        @Override
        public Bool visit(TypeParameterType t, Arg arg) {
            if (arg.context.containsKey(t)) {
                TypeWithContext lookup = arg.context.get(t);
                return recurse(lookup.getType(), arg.withContext(lookup.getTypeContext()));
            }
            String markerField = info.typeParameterIndexer.getMarkerField(t);
            return recurse(new BooleanLiteral(true), arg.withValue(pv.readPropertyValue(arg.value.getAllObjectLabels(), markerField)));
        }

        @Override
        public Bool visit(StringLiteral t, Arg arg) {
            boolean failed = false;
            boolean succeeded = false;

            Value value = arg.value;

            if (arg.value.isMaybeOtherThanStr()) {
                failed = true;
            }

            if (arg.value.isMaybeStr(t.getText())) {
                succeeded = true;
            } else if (arg.value.getStr() != null) {
                failed = true;
            } else if (!value.isNotStr()) {
                failed = true;
                succeeded = true;
            }

            value.isMaybeOtherThanStr();
            return boolFromFail(failed, succeeded);
        }

        @Override
        public Bool visit(BooleanLiteral t, Arg arg) {
            boolean failed = false;
            boolean succeeded = false;
            Value value = arg.value;
            if (value.isMaybeOtherThanBool()) {
                failed = true;
            }

            if (t.getValue()) {
                if (value.isMaybeFalse()) {
                    failed = true;
                }
                if (value.isMaybeTrue()) {
                    succeeded = true;
                }
            } else {
                if (value.isMaybeFalse()) {
                    succeeded = true;
                }
                if (value.isMaybeTrue()) {
                    failed = true;
                }
            }

            return boolFromFail(failed, succeeded);
        }

        @Override
        public Bool visit(NumberLiteral t, Arg arg) {
            boolean failed = false;
            boolean succeeded = false;
            Value value = arg.value;
            if (value.isMaybeOtherThanNum()) {
                failed = true;
            }
            if (value.isMaybeNum(t.getValue())) {
                succeeded = true;
            } else if (value.getNum() != null) {
                failed = true;
            } else if (!value.isNotNum()) {
                failed = true;
                succeeded = true;
            }
            return boolFromFail(failed, succeeded);
        }

        @Override
        public Bool visit(IntersectionType t, Arg arg) {
            return worstCase(t.getElements().stream().map(type -> recurse(type, arg)).collect(Collectors.toList()));
        }

        @Override
        public Bool visit(ClassInstanceType t, Arg arg) {
            return recurse(((ClassType)t.getClassType()).getInstanceType(), arg);
        }

        @Override
        public Bool visit(ThisType t, Arg arg) {
            if (arg.context.getThisType() == null) {
                throw new RuntimeException();
            }
            return recurse(arg.context.getThisType(), arg);
        }

        @Override
        public Bool visit(IndexType t, Arg arg) {
            throw new RuntimeException();
        }

        @Override
        public Bool visit(IndexedAccessType t, Arg arg) {
            throw new RuntimeException();
        }
    }

    private Collection<Value> split(Value value) {
        ArrayList<Value> result = new ArrayList<>();
        if (!value.isNotNum()) { // if number
            result.add(value.restrictToNum());
            value = value.restrictToNotNum();
        }
        if (!value.isNotStr()) { // if string
            result.add(value.restrictToStr());
            value = value.restrictToNotStr();
        }
        if (!value.isNotBool()) { // if bool
            result.add(value.restrictToBool());
            value = value.restrictToNotBool();
        }
        if (value.isMaybeUndef()) { // if undefined
            result.add(value.restrictToUndef());
            value = value.restrictToNotUndef();
        }
        if (value.isMaybeNull()) { // if null
            result.add(value.restrictToNull());
            value = value.restrictToNotNull();
        }
        for (ObjectLabel label : value.getObjectLabels()) {
            result.add(Value.makeObject(label));
        }
        value = value.restrictToNotObject();
        if (value.isMaybePresent()) {
            value.isMaybePresent();
        }

        assert !value.isMaybePresent(); // Making sure we got everything.

        return result;
    }
}
