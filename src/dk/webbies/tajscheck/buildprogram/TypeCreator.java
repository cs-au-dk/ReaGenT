package dk.webbies.tajscheck.buildprogram;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.au.cs.casa.typescript.types.BooleanLiteral;
import dk.au.cs.casa.typescript.types.NumberLiteral;
import dk.au.cs.casa.typescript.types.StringLiteral;
import dk.webbies.tajscheck.typeutil.PrettyTypes;
import dk.webbies.tajscheck.typeutil.TypeContext;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.benchmarks.CheckOptions;
import dk.webbies.tajscheck.paser.AST.*;
import dk.webbies.tajscheck.paser.AstBuilder;
import dk.webbies.tajscheck.testcreator.test.Test;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.buildprogram.TestProgramBuilder.*;
import static dk.webbies.tajscheck.paser.AstBuilder.*;

/**
 * Created by erik1 on 03-11-2016.
 */
public class TypeCreator {
    private final BiMap<TypeWithContext, Integer> typeIndexes;
    private final MultiMap<TypeWithContext, Integer> valueLocations;
    private final Map<Test, List<Integer>> testValueLocations = new IdentityHashMap<>();
    private final CheckOptions options;
    private Benchmark benchmark;
    private Map<Type, String> typeNames;
    private Set<Type> nativeTypes;
    private TypeParameterIndexer typeParameterIndexer;
    private ArrayList<Statement> functions = new ArrayList<>();
    private final MultiMap<Type, TypeParameterType> reachableTypeParameters;
    private final Set<Type> hasThisTypes;

    private static final String GET_TYPE_PREFIX = "getType_";
    private static final String CONSTRUCT_TYPE_PREFIX = "constructType_";
    private List<Statement> valueVariableDeclarationList = new ArrayList<>();

    TypeCreator(Map<Type, String> typeNames, Set<Type> nativeTypes, TypeParameterIndexer typeParameterIndexer, List<Test> tests, Benchmark benchmark, MultiMap<Type, TypeParameterType> reachableTypeParameters, Set<Type> hasThisTypes) {
        this.options = benchmark.options;
        this.benchmark = benchmark;
        this.reachableTypeParameters = reachableTypeParameters;
        this.hasThisTypes = hasThisTypes;
        this.valueLocations = new ArrayListMultiMap<>();
        this.typeNames = typeNames;
        this.nativeTypes = nativeTypes;
        this.typeParameterIndexer = typeParameterIndexer;
        this.typeIndexes = HashBiMap.create();

        for (Test test : tests) {
            List<Integer> testValueLocations = new ArrayList<>();
            this.testValueLocations.put(test, testValueLocations);
            for (Type type : test.getProduces()) {
                testValueLocations.add(createProducedValueVariable(type, test.getTypeContext()));
            }
        }

        for (Test test : tests) {
            // Forcing all these to be created ahead of time
            for (Type dependsOn : test.getDependsOn()) {
                constructType(dependsOn, test.getTypeContext());
            }
            for (Type type : test.getTypeToTest()) {
                getType(type, test.getTypeContext());
            }
        }
        finish();

    }

    public List<Integer> getTestProducesIndexes(Test test) {
        return testValueLocations.get(test);
    }


    public Collection<Integer> getValueIndex(Type type, TypeContext context) {
        return valueLocations.get(new TypeWithContext(type, context));
    }

    private int valueCounter = 0;

    private int createProducedValueVariable(Type type, TypeContext typeContext) {
        int index = valueCounter++;
        valueVariableDeclarationList.add(variable(VALUE_VARIABLE_PREFIX + index, identifier(VARIABLE_NO_VALUE)));

        putProducedValueIndex(index, type, typeContext);
        return index;
    }

    public Collection<Statement> getValueVariableDeclarationList() {
        return valueVariableDeclarationList;
    }

    private void putProducedValueIndex(int index, Type type, TypeContext typeContext) {
        putProducedValueIndex(index, type, typeContext, false);
    }

    private void putProducedValueIndex(int index, Type type, TypeContext typeContext, boolean touchedThisTypes) {
        valueLocations.put(new TypeWithContext(type, typeContext), index);

        if (!touchedThisTypes) {
            if (typeContext.getThisType() != null) {
                putProducedValueIndex(index, type, typeContext.withClass(null), true);
            }
            if (typeContext.getThisType() == null) {
                if (hasThisTypes.contains(type)) {
                    putProducedValueIndex(index, type, typeContext.withClass(type), true);
                }
            }
        }

        TypeContext newContext = typeContext.cleanTypeParameters(type, reachableTypeParameters);
        if (!newContext.equals(typeContext)) {
            putProducedValueIndex(index, type, newContext);
        }

        if (type instanceof InterfaceType) {
            List<Type> baseTypes = ((InterfaceType) type).getBaseTypes();
            baseTypes.forEach(baseType -> putProducedValueIndex(index, baseType, typeContext));
        } else if (type instanceof IntersectionType) {
            List<Type> baseTypes = ((IntersectionType) type).getElements();
            baseTypes.forEach(baseType -> putProducedValueIndex(index, baseType, typeContext));
        } else if (type instanceof ReferenceType) {
            putProducedValueIndex(index, ((ReferenceType) type).getTarget(), new TypesUtil(benchmark).generateParameterMap((ReferenceType) type, typeContext));
        } else if (type instanceof GenericType) {
            putProducedValueIndex(index, ((GenericType) type).toInterface(), typeContext);
        } else if (type instanceof ClassType) {
            List<Type> baseTypes = ((ClassType) type).getBaseTypes();
            valueLocations.put(new TypeWithContext(type, typeContext.withClass(((ClassType) type).getInstanceType())), index);
            baseTypes.forEach(baseType -> putProducedValueIndex(index, baseType, typeContext.withClass(((ClassType) type).getInstanceType())));
        } else if (type instanceof ClassInstanceType) {
            putProducedValueIndex(index, ((ClassType) ((ClassInstanceType) type).getClassType()).getInstanceType(), typeContext);
        } else if (type instanceof ThisType) {
            Type thisType = typeContext.getThisType();
            putProducedValueIndex(index, thisType != null ? thisType : ((ThisType) type).getConstraint(), typeContext);
        } else if (type instanceof TypeParameterType) {
            if (typeContext.get((TypeParameterType) type) != null) {
                TypeWithContext lookup = typeContext.get((TypeParameterType) type);
                List<Type> recursiveDefinition = TypesUtil.findRecursiveDefinition((TypeParameterType) type, typeContext, typeParameterIndexer);
                if (recursiveDefinition.isEmpty()) {
                    putProducedValueIndex(index, lookup.getType(), lookup.getTypeContext());
                } else {
                    for (Type constraint : recursiveDefinition) {
                        putProducedValueIndex(index, constraint, typeContext);
                    }
                }
            } else {
                // Do nothing
            }
        } else if (type instanceof SimpleType || type instanceof NumberLiteral || type instanceof StringLiteral || type instanceof BooleanLiteral || type instanceof UnionType || type instanceof TupleType || type instanceof NeverType || type instanceof SymbolType) {
            // Do nothing.
        } else {
            throw new RuntimeException(type.getClass().getName());
        }

    }


    private Statement constructUnion(List<Type> types, TypeContext typeContext) {
        List<Integer> elements = types.stream().distinct().map((type) -> getTypeIndex(type, typeContext)).collect(Collectors.toList());

        if (elements.size() == 1) {
            return Return(constructType(elements.iterator().next()));
        }

        List<Pair<Expression, Statement>> cases = Util.withIndex(elements).map(pair -> {
            return new Pair<Expression, Statement>(number(pair.getRight()), Return(constructType(pair.getLeft())));
        }).collect(Collectors.toList());

        return block(
                switchCase(
                        binary(binary(call(identifier("random")), Operator.MULT, number(elements.size())), Operator.BITWISE_OR, number(0)),
                        cases,
                        block(
                                comment("Unreachable"),
                                Return(constructType(elements.iterator().next()))
                        )
                )
        );
    }

    private Statement returnOneOfExistingValues(Collection<Integer> elementsCollection) {
        List<Integer> elements = new ArrayList<>(elementsCollection);
        if (elements.size() == 1) {
            Integer index = elements.iterator().next();
            return Return(identifier(VALUE_VARIABLE_PREFIX + index));
        }

        List<Pair<Expression, Statement>> cases = Util.withIndex(elements).map(pair -> {
            Expression getValue = identifier(VALUE_VARIABLE_PREFIX + pair.getLeft());
            return new Pair<Expression, Statement>(number(pair.getRight()), block(
                    variable("result", getValue),
                    ifThen(binary(identifier("result"), Operator.NOT_EQUAL_EQUAL, identifier(VARIABLE_NO_VALUE)), Return(identifier("result")))
            ));
        }).collect(Collectors.toList());

        return block(
                switchCase(
                        binary(binary(call(identifier("random")), Operator.MULT, number(elements.size())), Operator.BITWISE_OR, number(0)),
                        cases
                ),
                // If the switch fails to return, check if anything can be returned.
                block(cases.stream().map(Pair::getRight).collect(Collectors.toList())),
                // If nothing has been returned, return the NO_VALUE object.
                Return(identifier(TestProgramBuilder.VARIABLE_NO_VALUE))
        );
    }

    private final class ConstructNewInstanceVisitor implements TypeVisitorWithArgument<Statement, TypeContext> {
        @Override
        public Statement visit(AnonymousType t, TypeContext typeContext) {
            throw new RuntimeException();
        }

        @Override
        public Statement visit(ClassType t, TypeContext typeContext) {
            if (hasThisTypes.contains(t)) {
                typeContext = typeContext.withClass(t.getInstanceType());
            }

            assert t.getSignatures().size() > 0;

            List<Statement> program = new ArrayList<>();

            List<Signature> signatures = t.getSignatures().stream().map(sig -> TypesUtil.createConstructorSignature(t, sig)).collect(Collectors.toList());
            program.add(variable("result", createFunction(signatures, typeContext, typeNames.get(t))));

            Pair<InterfaceType, TypeContext> pair = new TypesUtil(benchmark).constructSyntheticInterfaceWithBaseTypes(TypesUtil.classToInterface(t, hasThisTypes), typeNames);
            InterfaceType inter = pair.getLeft();
            typeContext = typeContext.append(pair.getRight());

            if (inter.getDeclaredNumberIndexType() != null) {
                program.addAll(addNumberIndexerType(inter.getDeclaredNumberIndexType(), typeContext, identifier("result"), typeNames.get(t).hashCode()));
            }

            if (inter.getDeclaredStringIndexType() != null) {
                program.addAll(addStringIndexerType(inter.getDeclaredStringIndexType(), typeContext, identifier("result"), inter.getDeclaredProperties().keySet(), typeNames.get(t).hashCode()));
            }

            List<Pair<String, Type>> properties = inter.getDeclaredProperties().entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue())).collect(Collectors.toList());

            for (Pair<String, Type> property : properties) {
                program.add(statement(binary(member(identifier("result"), property.getLeft()), Operator.EQUAL, constructType(property.getRight(), typeContext))));
            }

            program.add(Return(identifier("result")));

            return block(program);
        }

        @Override
        public Statement visit(GenericType type, TypeContext typeContext) {
            assert type.getTypeParameters().equals(type.getTypeArguments());
            if (nativeTypes.contains(type)) {
                try {
                    return constructTypeFromName(typeNames.get(type), typeContext);
                } catch (ProduceManuallyException e) {
                    // continue
                }
            }

            return type.toInterface().accept(this, typeContext);
        }

        @Override
        public Statement visit(InterfaceType type, TypeContext typeContext) {
            if (nativeTypes.contains(type) && !TypesUtil.isEmptyInterface(type) && !typeNames.get(type).startsWith("window.")) {
                try {
                    return constructTypeFromName(typeNames.get(type), typeContext);
                } catch (ProduceManuallyException e) {
                    // continue
                }
            }

            if (hasThisTypes.contains(type)) {
                typeContext = typeContext.withClass(type);
            }

            Pair<InterfaceType, TypeContext> pair = new TypesUtil(benchmark).constructSyntheticInterfaceWithBaseTypes(type, typeNames);
            InterfaceType inter = pair.getLeft();
            typeContext = typeContext.append(pair.getRight());
            assert inter.getBaseTypes().isEmpty();

            int numberOfSignatures = inter.getDeclaredCallSignatures().size() + inter.getDeclaredConstructSignatures().size();

            List<Statement> program = new ArrayList<>();
            if (numberOfSignatures == 0) {
                program.add(variable("result", object()));
            } else {
                program.add(variable("result", createFunction(inter, typeContext)));
            }

            if (inter.getDeclaredNumberIndexType() != null) {
                program.addAll(addNumberIndexerType(inter.getDeclaredNumberIndexType(), typeContext, identifier("result"), typeNames.get(type).hashCode()));
            }

            if (inter.getDeclaredStringIndexType() != null) {
                program.addAll(addStringIndexerType(inter.getDeclaredStringIndexType(), typeContext, identifier("result"), inter.getDeclaredProperties().keySet(), typeNames.get(type).hashCode()));
            }

            List<Pair<String, Type>> properties = inter.getDeclaredProperties().entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue())).collect(Collectors.toList());

            for (Pair<String, Type> property : properties) {
                program.add(statement(binary(member(identifier("result"), property.getLeft()), Operator.EQUAL, constructType(property.getRight(), typeContext))));
            }

            program.add(Return(identifier("result")));

            return block(program);
        }

        private Collection<Statement> addStringIndexerType(Type type, TypeContext context, Expression exp, Set<String> existingKeys, int seed) {
            char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            Random random = new Random(seed); // I need this to be completely deterministic.

            int keys = random.nextInt(10) + 1;

            List<Statement> result = new ArrayList<>();


            for (int i = 0; i < keys; i++) {
                String key;
                //noinspection StatementWithEmptyBody
                while (existingKeys.contains(key = RandomStringUtils.random(random.nextInt(10) + 1, chars))) {
                    // do nothing.
                }

                result.add(statement(binary(member(exp, key), Operator.EQUAL, constructType(type, context))));
            }

            return result;
        }

        private Collection<Statement> addNumberIndexerType(Type type, TypeContext context, Expression exp, int seed) {
            Random random = new Random(seed);

            List<Statement> result = new ArrayList<>();

            int keys = random.nextInt(10) + 1;
            for (int i = 0; i < keys; i++) {
                result.add(statement(binary(member(exp, Integer.toString(random.nextInt(100))), Operator.EQUAL, constructType(type, context))));
            }

            return result;
        }

        @Override
        public Statement visit(ReferenceType type, TypeContext typeContext) {
            if ("Array".equals(typeNames.get(type.getTarget()))) {
                Type indexType = type.getTypeArguments().iterator().next();
                Expression constructElement = constructType(indexType, typeContext);


                // An expression that returns an array with the correct type, with either 0, 1, 3, 4 or 5 elements in the array.
                return Return(conditional(
                        binary(call(identifier("random")), Operator.GREATER_THAN, number(0.8)),
                        array(),
                        conditional(
                                binary(call(identifier("random")), Operator.GREATER_THAN, number(0.75)),
                                array(constructElement),
                                conditional(
                                        binary(call(identifier("random")), Operator.GREATER_THAN, number(0.67)),
                                        array(constructElement, constructElement, constructElement),
                                        conditional(
                                                binary(call(identifier("random")), Operator.GREATER_THAN, number(0.5)),
                                                array(constructElement, constructElement, constructElement, constructElement),
                                                array(constructElement, constructElement, constructElement, constructElement, constructElement)
                                        )
                                )
                        )
                ));
            }

            Type target;
            if (type.getTarget() instanceof GenericType) {
                target = ((GenericType) type.getTarget()).toInterface();
            } else if (type.getTarget() instanceof TupleType) {
                target = type.getTarget();
            } else {
                assert type.getTarget() instanceof ClassInstanceType;
                target = ((ClassType) ((ClassInstanceType) type.getTarget()).getClassType()).getInstanceType();
            }
            return constructNewInstanceOfType(target, new TypesUtil(benchmark).generateParameterMap(type, typeContext));
        }

        @Override
        public Statement visit(SimpleType simple, TypeContext typeContext) {
            if (benchmark.useTAJS) {
                switch (simple.getKind()) {
                    case String:
                        return Return(call(identifier("TAJS_make"), string("AnyStr")));
                    case Any:
                        return Return(
                                object(new ObjectLiteral.Property("__isAnyMarker", object()))
                        );
                    case Boolean:
                        return Return(call(identifier("TAJS_make"), string("AnyBool")));
                    case Null:
                        return Return(nullLiteral());
                    case Number:
                        return Return(call(identifier("TAJS_make"), string("AnyNum")));
                    case Undefined:
                    case Void:
                        return Return(
                                unary(Operator.VOID, number(0))
                        );
                    default:
                        throw new RuntimeException("Cannot yet produce a simple: " + simple.getKind());
                }
            }

            switch (simple.getKind()) {
                case String:
                    // Math.random().toString(36).substring(Math.random() * 20);
                    MethodCallExpression produceRandomString = methodCall(methodCall(call(identifier("random")), "toString", number(26)), "substring",
                            binary(
                                    call(identifier("random")),
                                    Operator.MULT,
                                    number(20)
                            ));
                    return Return(produceRandomString);
                case Number:
                    return Return(call(identifier("random")));
                case Boolean:
                    // Math.random() > 0.5
                    return Return(
                            binary(
                                    call(identifier("random")),
                                    Operator.LESS_THAN,
                                    number(0.5)
                            )
                    );
                case Any:
                    return Return(
                            object(new ObjectLiteral.Property("__isAnyMarker", object()))
                    );
                case Undefined:
                case Void:
                    return Return(
                            unary(Operator.VOID, number(0))
                    );
                case Null:
                    return Return(nullLiteral());
                default:
                    throw new RuntimeException("Cannot yet produce a simple: " + simple.getKind());
            }
        }

        @Override
        public Statement visit(TupleType t, TypeContext typeContext) {
            return Return(array(t.getElementTypes().stream().map(element -> constructType(element, typeContext)).collect(Collectors.toList())));
        }

        @Override
        public Statement visit(UnionType t, TypeContext typeContext) {
            return constructUnion(t.getElements(), typeContext);
        }

        @Override
        public Statement visit(UnresolvedType t, TypeContext typeContext) {
            throw new RuntimeException();
        }

        @Override
        public Statement visit(TypeParameterType type, TypeContext typeContext) {
            if (typeContext.containsKey(type)) {
                if (!TypesUtil.findRecursiveDefinition(type, typeContext, typeParameterIndexer).isEmpty()) {
                    IntersectionType intersection = new IntersectionType();
                    intersection.setElements(TypesUtil.findRecursiveDefinition(type, typeContext, typeParameterIndexer));

                    return constructNewInstanceOfType(intersection, typeContext);
                }

                TypeWithContext lookup = typeContext.get(type);
                return constructNewInstanceOfType(lookup.getType(), lookup.getTypeContext());
            }
            String markerField = typeParameterIndexer.getMarkerField(type);
            return block(
                    variable("result", constructType(type.getConstraint(), typeContext)),
                    ifThen(
                            binary(unary(Operator.TYPEOF, identifier("result")), Operator.NOT_EQUAL_EQUAL, string("object")),
                            throwStatement(newCall(identifier(RUNTIME_ERROR_NAME)))
                    ),
                    statement(
                            binary(member(identifier("result"), markerField), Operator.EQUAL, bool(true))
                    ),
                    Return(identifier("result"))
            );
        }

        @Override
        public Statement visit(SymbolType t, TypeContext typeContext) {
            Expression constructString = constructType(new SimpleType(SimpleTypeKind.String), new TypeContext(benchmark));
            return Return(call(identifier("Symbol"), constructString));
        }

        @Override
        public Statement visit(StringLiteral str, TypeContext typeContext) {
            return Return(string(str.getText()));
        }

        @Override
        public Statement visit(BooleanLiteral t, TypeContext typeContext) {
            return Return(bool(t.getValue()));
        }

        @Override
        public Statement visit(NumberLiteral t, TypeContext typeContext) {
            return Return(number(t.getValue()));
        }

        @Override
        public Statement visit(IntersectionType t, TypeContext typeContext) {
            return throwStatement(newCall(identifier(RUNTIME_ERROR_NAME), string("Not implemented yet, intersectionTypes")));
        }

        @Override
        public Statement visit(ClassInstanceType t, TypeContext typeContext) {
            return ((ClassType) t.getClassType()).getInstanceType().accept(this, typeContext);
        }

        @Override
        public Statement visit(NeverType t, TypeContext typeContext) {
            return throwStatement(newCall(identifier("Error"), string("This is a correct result of a never-type")));
        }

        @Override
        public Statement visit(ThisType t, TypeContext typeContext) {
            return typeContext.getThisType().accept(this, typeContext);
        }

        @Override
        public Statement visit(IndexType t, TypeContext typeContext) {
            throw new RuntimeException();
        }

        @Override
        public Statement visit(IndexedAccessType t, TypeContext typeContext) {
            throw new RuntimeException();
        }
    }

    private FunctionExpression createFunction(InterfaceType inter, TypeContext typeContext) {
        List<Signature> signatures = TypesUtil.removeDuplicateSignatures(Util.concat(inter.getDeclaredCallSignatures(), inter.getDeclaredConstructSignatures()));

        String interName = typeNames.get(inter);
        assert interName != null;

        return createFunction(signatures, typeContext, interName);
    }

    private FunctionExpression createFunction(List<Signature> signatures, TypeContext typeContext, String path) {
        assert signatures.size() > 0;

        int maxArgs = signatures.stream().map(Signature::getParameters).map(List::size).reduce(0, Math::max);

        List<String> args = new ArrayList<>();
        for (int i = 0; i < maxArgs; i++) {
            args.add("arg" + i);
        }

        TypeChecker typeChecker = new TypeChecker(nativeTypes, typeNames, typeParameterIndexer, typeContext);


        assert !signatures.isEmpty();

        if (signatures.size() == 1) {
            Signature signature = signatures.iterator().next();

            List<Signature.Parameter> parameters = signature.getParameters();

            List<Statement> typeChecks = new ArrayList<>();

            if (signature.isHasRestParameter()) {
                ReferenceType restTypeArr = (ReferenceType) parameters.get(parameters.size() - 1).getType();

                assert "Array".equals(typeNames.get(restTypeArr.getTarget()));
                assert restTypeArr.getTypeArguments().size() == 1;

                Type restType = restTypeArr.getTypeArguments().iterator().next();

                typeChecks.add(statement(call(
                        identifier("assert"),
                        call(identifier("checkRestArgs"), identifier("args"), number(parameters.size() - 1),
                                function(block(
                                        Return(typeChecker.checkResultingType(restType, identifier("exp"), path + ".[restArgs]", options.checkDepth))
                                ), "exp")
                        ),
                        string(path + ".[restArgs]"),
                        string("valid rest-args"),
                        AstBuilder.expFromString("Array.prototype.slice.call(args)"),
                        identifier("i")
                )));

                parameters = parameters.subList(0, parameters.size() - 1);
            }

            Util.zip(args.stream(), parameters.stream(), (argName, par) ->
                    typeChecker.assertResultingType(par.getType(), identifier(argName), path + ".[" + argName + "]", options.checkDepth)
            ).forEach(typeChecks::add);

            typeChecks.add(checkNumberOfArgs(signature));

            BlockStatement functionBody = block(
                    variable(identifier("args"), identifier("arguments")),
                    // Currently not using the information whether or not the signature was correct. The assertion-errors has already been reported anyway.
                    variable(identifier("signatureCorrect"), call(function(
                            block(
                                    block(typeChecks),
                                    Return(bool(true))
                            )
                    ))),
                    saveArgsAndReturnValue(signature, typeContext)
            );


            return function(
                    block(functionBody),
                    args
            );
        } else {
            Statement functionBody = block(
                    variable(identifier("args"), identifier("arguments")),
                    variable("foundSignatures", array()),
                    // Checking each signature, to see if correct.
                    block(Util.withIndex(signatures).map(signaturePair -> {
                        int signatureIndex = signaturePair.getRight();
                        Signature signature = signaturePair.getLeft();
                        List<Signature.Parameter> parameters = signature.getParameters();
                        Statement checkRestArgs = block();

                        if (signature.isHasRestParameter()) {
                            ReferenceType restTypeArr = (ReferenceType) parameters.get(parameters.size() - 1).getType();

                            assert "Array".equals(typeNames.get(restTypeArr.getTarget()));
                            assert restTypeArr.getTypeArguments().size() == 1;

                            Type restType = restTypeArr.getTypeArguments().iterator().next();

                            checkRestArgs = ifThen(unary(Operator.NOT,
                                    call(identifier("checkRestArgs"), identifier("args"), number(parameters.size() - 1),
                                            function(block(
                                                    Return(typeChecker.checkResultingType(restType, identifier("exp"), path + ".[restArgs]", options.checkDepth))
                                            ), "exp")
                                    )),
                                    Return(bool(false))
                            );

                            parameters = parameters.subList(0, parameters.size() - 1);
                        }

                        return block(
                                variable("signatureCorrect" + signatureIndex, call(function(block(
                                        checkNumberOfArgs(signature),
                                        checkRestArgs,
                                        block(Util.withIndex(parameters).map(parameterPair -> {
                                            Integer argIndex = parameterPair.getRight();
                                            Signature.Parameter arg = parameterPair.getLeft();

                                            return block(
                                                    variable(identifier("arg" + argIndex + "Correct"), typeChecker.checkResultingType(arg.getType(), identifier("arg" + argIndex), path + ".[arg" + argIndex + "]", options.checkDepth)),
                                                    ifThen(
                                                            unary(Operator.NOT, identifier("arg" + argIndex + "Correct")),
                                                            Return(bool(false))
                                                    )
                                            );
                                        }).collect(Collectors.toList())),
                                        Return(bool(true))
                                )))),
                                benchmark.useTAJS ? statement(
                                        call(
                                                identifier("assert"),
                                                identifier("signatureCorrect" + signatureIndex),
                                                string(path),
                                                string("overload " + PrettyTypes.parameters(signature.getParameters()) + " to be called"), string("it was not called"),
                                                identifier("i")
                                        )
                                ): block(),
                                ifThen(
                                        identifier("signatureCorrect" + signatureIndex),
                                        statement(methodCall(identifier("foundSignatures"), "push", number(signatureIndex)))
                                )
                        );
                    }).collect(Collectors.toList())),
                    ifThen(
                            binary(
                                    member(identifier("foundSignatures"), "length"),
                                    Operator.EQUAL_EQUAL_EQUAL,
                                    number(0)
                            ),
                            block(
                                    comment("Call assert, no valid overload found, the application was called in a wrong way."),
                                    statement(call(
                                            identifier("assert"),
                                            binary(
                                                    member(identifier("foundSignatures"), "length"),
                                                    Operator.NOT_EQUAL_EQUAL,
                                                    number(0)
                                            ),
                                            string(path),
                                            string("A valid overload"),
                                            AstBuilder.expFromString("Array.prototype.slice.call(args)"),
                                            identifier("i")
                                    )),
                                    Return()
                            )
                    ),
                    ifThen(
                            binary(
                                    member(identifier("foundSignatures"), "length"),
                                    Operator.GREATER_THAN_EQUAL,
                                    number(2)
                            ),
                            block(
                                    comment("Call error, the application was imprecise, and couldn't identity the correct overload"),
                                    throwStatement(newCall(identifier(RUNTIME_ERROR_NAME), binary(string("Could not find correct overload for function: " + path + " results: "), Operator.PLUS, methodCall(identifier("foundSignatures"), "toString"))))
                            )
                    ),
                    comment("Save the arguments, and returns the value, of the correct overload. "),
                    switchCase(
                            arrayAccess(identifier("foundSignatures"), number(0)),
                            Util.withIndex(signatures).map(pair -> {
                                Integer signatureIndex = pair.getRight();
                                Signature signature = pair.getLeft();
                                return new Pair<Expression, Statement>(
                                        number(signatureIndex),
                                        saveArgsAndReturnValue(signature, typeContext)
                                );
                            }).collect(Collectors.toList())
                    )
            );

            return function(
                    block(functionBody),
                    args
            );
        }
    }

    private Statement checkNumberOfArgs(Signature signature) {
        // minArgsCondition
        BinaryExpression condition = binary(
                member(identifier("args"), "length"),
                Operator.GREATER_THAN_EQUAL,
                number(signature.getMinArgumentCount()));
        if (!signature.isHasRestParameter()) {
            // and maxArgsCondition
            condition = binary(
                    condition,
                    Operator.AND,
                    binary(
                            member(identifier("args"), "length"),
                            Operator.LESS_THAN_EQUAL,
                            number(signature.getParameters().size())
                    )
            );
        }
        return block(
                ifThen(
                        unary(Operator.NOT, condition
                        ),
                        Return(bool(false))
                )
        );
    }

    private BlockStatement saveArgsAndReturnValue(Signature signature, TypeContext typeContext) {
        List<Signature.Parameter> parameters = signature.getParameters();

        List<Statement> saveArgumentValues = new ArrayList<>();

        if (signature.isHasRestParameter()) {
            Type restArgTypeArray = parameters.get(parameters.size() - 1).getType();
            assert restArgTypeArray instanceof ReferenceType;
            assert "Array".equals(typeNames.get(((ReferenceType) restArgTypeArray).getTarget()));
            assert ((ReferenceType) restArgTypeArray).getTypeArguments().size() == 1;

            Type restArgType = ((ReferenceType) restArgTypeArray).getTypeArguments().iterator().next();

            parameters = parameters.subList(0, parameters.size() - 1);

            int valueIndex = createProducedValueVariable(restArgType, typeContext);

            int saveFromIndex = parameters.size(); // inclusive

            Expression indexToSave = AstBuilder.expFromString(saveFromIndex + " + (Math.random() * (arguments.length - " + saveFromIndex + ") | 0)");

            saveArgumentValues.add(
                    ifThen(
                            binary(AstBuilder.expFromString("arguments.length"), Operator.GREATER_THAN, number(saveFromIndex)),
                            statement(binary(identifier(VALUE_VARIABLE_PREFIX + valueIndex), Operator.EQUAL, arrayAccess(identifier("arguments"), indexToSave)))
                    )
            );
        }

        Util.withIndex(
                parameters.stream().map(par -> createProducedValueVariable(par.getType(), typeContext)),
                (valueIndex, argIndex) -> {
                    return block(
                            statement(binary(identifier(VALUE_VARIABLE_PREFIX + valueIndex), Operator.EQUAL, identifier("arg" + argIndex))),
                            statement(call(identifier("registerValue"), number(valueIndex)))
                    );
                }
        ).forEach(saveArgumentValues::add);

        return block(
                block(saveArgumentValues),
                Return(constructType(signature.getResolvedReturnType(), typeContext))
        );
    }


    private Statement constructTypeFromName(String name, TypeContext typeContext) throws ProduceManuallyException {
        if (name == null) {
            throw new NullPointerException();
        }
        if (name.startsWith("global.")) {
            name = name.substring("global.".length(), name.length());
        }

        switch (name) {
            case "Object":
                return constructNewInstanceOfType(SpecReader.makeEmptySyntheticInterfaceType(), typeContext);
            case "Number":
                return constructNewInstanceOfType(new SimpleType(SimpleTypeKind.Number), typeContext);
            case "Boolean":
                return constructNewInstanceOfType(new SimpleType(SimpleTypeKind.Boolean), typeContext);
            case "Date":
                return Return(newCall(identifier("Date")));
            case "Function":
                InterfaceType interfaceWithSimpleFunction = SpecReader.makeEmptySyntheticInterfaceType();
                Signature callSignature = new Signature();
                callSignature.setParameters(new ArrayList<>());
                callSignature.setMinArgumentCount(0);
                callSignature.setResolvedReturnType(new SimpleType(SimpleTypeKind.Any));
                interfaceWithSimpleFunction.getDeclaredCallSignatures().add(callSignature);
                typeNames.put(interfaceWithSimpleFunction, "Function");
                return constructNewInstanceOfType(interfaceWithSimpleFunction, typeContext);
            case "Error":
                return Return(newCall(identifier("Error")));
            case "RegExp":
                Expression constructString = call(function(constructNewInstanceOfType(new SimpleType(SimpleTypeKind.String), new TypeContext(benchmark))));
                return Return(newCall(identifier("RegExp"), constructString));
            case "String":
                return constructNewInstanceOfType(new SimpleType(SimpleTypeKind.String), typeContext);
            case "HTMLCanvasElement":
                return AstBuilder.stmtFromString("return document.createElement('canvas')");
            case "HTMLVideoElement":
                return AstBuilder.stmtFromString("return document.createElement('video')");
            case "HTMLImageElement":
                return AstBuilder.stmtFromString("return document.createElement('img')");
            case "Uint32Array":
                return AstBuilder.stmtFromString("return new Uint32Array()");
            case "Float32Array":
                return AstBuilder.stmtFromString("return new Float32Array()");
            case "Uint16Array":
                return AstBuilder.stmtFromString("return new Uint16Array()");
            case "WebGLRenderingContext":
                return AstBuilder.stmtFromString("return document.createElement(\"canvas\").getContext(\"webgl\")");
            case "WebGLTexture":
                return AstBuilder.stmtFromString("return document.createElement(\"canvas\").getContext(\"webgl\").createTexture()");
            case "WebGLFramebuffer":
                return AstBuilder.stmtFromString("return document.createElement(\"canvas\").getContext(\"webgl\").createFramebuffer()");
            case "WebGLRenderbuffer":
                return AstBuilder.stmtFromString("return document.createElement(\"canvas\").getContext(\"webgl\").createRenderbuffer()");
            case "CanvasRenderingContext2D":
                return AstBuilder.stmtFromString("return document.createElement(\"canvas\").getContext(\"2d\")");
            case "MouseEvent":
                return AstBuilder.stmtFromString("return new MouseEvent(null)");
            case "Event":
                return AstBuilder.stmtFromString("return new Event(null)");
            case "WebGLProgram":
                return AstBuilder.stmtFromString("return document.createElement(\"canvas\").getContext(\"webgl\").createProgram()");
            case "WebGLBuffer":
                return AstBuilder.stmtFromString("return document.createElement(\"canvas\").getContext(\"webgl\").createBuffer()");
            case "ArrayBuffer":
                return AstBuilder.stmtFromString("return new ArrayBuffer()");
            case "ImageData":
                return AstBuilder.stmtFromString("return new ImageData(10, 10)");
            case "TouchEvent":
                return AstBuilder.stmtFromString("return new TouchEvent(null)");
            case "WebGLContextEvent":
                return AstBuilder.stmtFromString("return new WebGLContextEvent(null)");
            case "PointerEvent":
                return AstBuilder.stmtFromString("return new PointerEvent(\"pointermove\")");
            case "CanvasGradient":
                return AstBuilder.stmtFromString("return document.createElement(\"canvas\").getContext(\"2d\").createLinearGradient()");
            case "HTMLElement":
                return AstBuilder.stmtFromString("return document.createElement('div')");
            case "CanvasPattern":
                return AstBuilder.stmtFromString("return document.createElement(\"canvas\").getContext(\"2d\").createPattern()");
            case "XMLHttpRequest":
                return AstBuilder.stmtFromString("return new XMLHttpRequest()");
            case "EventTarget":
                return AstBuilder.stmtFromString("return document"); // Not good, but good enough.
            case "Element":
            case "Node":
                return AstBuilder.stmtFromString("return document.createElement(\"div\")");
            case "ErrorEvent":
                return AstBuilder.stmtFromString("return new ErrorEvent(\"foo\")");
            case "XMLHttpRequestUpload":
                return AstBuilder.stmtFromString("return new XMLHttpRequest().upload");
            case "Text":
                return AstBuilder.stmtFromString("return document.createTextNode(\"foo\")");
            case "DocumentFragment":
                return AstBuilder.stmtFromString("return new DocumentFragment()");
            case "XMLDocument":
                return AstBuilder.stmtFromString("return XMLDocument.load()");
            case "Document":
                return AstBuilder.stmtFromString("return document");
            case "Window":
                return AstBuilder.stmtFromString("return window");
            case "DragEvent":
                return AstBuilder.stmtFromString("return new DragEvent(12)");
            case "Navigator":
                return AstBuilder.stmtFromString("return window.navigator");
            case "MSCredentials":
                return AstBuilder.stmtFromString("return new MSCredentials()");
            case "Storage":
                return AstBuilder.stmtFromString("return window.localStorage");
            case "ApplicationCache":
                return AstBuilder.stmtFromString("return window.applicationCache");
            case "BarProp":
                return AstBuilder.stmtFromString("return window.locationbar");
            case "IDBFactory":
                return AstBuilder.stmtFromString("return window.indexedDB");
            case "Location":
                return AstBuilder.stmtFromString("return window.location");
            case "MediaQueryList":
                return AstBuilder.stmtFromString("return window.matchMedia(123)");
            case "URL":
                return AstBuilder.stmtFromString("return new URL(\"http://google.com\")");
            case "Screen":
                return AstBuilder.stmtFromString("return window.screen");
            case "Blob":
                return AstBuilder.stmtFromString("return new Blob()");
            case "History":
                return AstBuilder.stmtFromString("return window.history");
            case "Crypto":
                return AstBuilder.stmtFromString("return window.crypto");
            case "Console":
                return AstBuilder.stmtFromString("return console");
            case "StyleMedia":
                return AstBuilder.stmtFromString("return window.styleMedia");
            case "Selection":
                return AstBuilder.stmtFromString("return window.getSelection()");
            case "Performance":
                return AstBuilder.stmtFromString("return window.performance");
            case "SVGElement":
                return AstBuilder.stmtFromString("return document.createElementNS(\"http://www.w3.org/2000/svg\", \"g\")");
            case "CSSRuleList":
            case "CSSStyleDeclaration":
                // Hacky, i know.
                return AstBuilder.stmtFromString("return (function () {var tmp = {}; tmp.__proto__  = " + name + "; return tmp})();");
            case "EventListener":
            case "EventListenerObject":
            case "WebKitPoint":
            case "ErrorEventHandler":
                throw new ProduceManuallyException();
            default:
                throw new RuntimeException("Unknown: " + name);
        }

    }

    private final class ProduceManuallyException extends Exception {
    }



    private Statement constructNewInstanceOfType(Type type, TypeContext typeContext) {
        return type.accept(new ConstructNewInstanceVisitor(), typeContext);
    }

    public CallExpression getType(Type type, TypeContext typeContext) {
        int index = getTypeIndex(type, typeContext);

        return getType(index);
    }

    public CallExpression getType(int index) {
        return call(identifier(GET_TYPE_PREFIX + index));
    }

    public CallExpression constructType(Type type, TypeContext typeContext) {
        int index = getTypeIndex(type, typeContext);

        addConstructInstanceFunction(index);

        return call(identifier(CONSTRUCT_TYPE_PREFIX + index));
    }

    public CallExpression constructType(int index) {
        TypeWithContext typeWithParameters = typeIndexes.inverse().get(index);
        return constructType(typeWithParameters.getType(), typeWithParameters.getTypeContext());
    }

    private int getTypeIndex(Type type, TypeContext typeContext) {
        TypeWithContext key = new TypeWithContext(type, typeContext);
        if (typeIndexes.containsKey(key)) {
            return typeIndexes.get(key);
        } else {
            int value = typeIndexes.size();
            typeIndexes.put(key, value);

            if (finished) {
                throw new RuntimeException("Already finished");
            }

            getTypeFunctionQueue.add(key);

            return value;
        }
    }

    private final List<TypeWithContext> getTypeFunctionQueue = new ArrayList<>();
    private boolean finished = false;

    private void finish() {
        finished = true;
        for (TypeWithContext key : getTypeFunctionQueue) {
            int value = typeIndexes.get(key);

            Collection<Integer> values = valueLocations.get(key);

            Statement returnTypeStatement;

            if (values.size() == 1) {
                returnTypeStatement = Return(identifier(VALUE_VARIABLE_PREFIX + values.iterator().next()));
            } else if (values.isEmpty()) {
                returnTypeStatement = Return(identifier(VARIABLE_NO_VALUE));
            } else {
                returnTypeStatement = returnOneOfExistingValues(values);
            }

            ExpressionStatement getTypeFunction = statement(
                    function(
                            GET_TYPE_PREFIX + value,
                            block(returnTypeStatement)
                    )
            );
            functions.add(getTypeFunction);
        }

    }

    private final Set<Integer> hasCreateTypeFunction = new HashSet<>();

    private void addConstructInstanceFunction(int index) {
        if (hasCreateTypeFunction.contains(index)) {
            return;
        }
        hasCreateTypeFunction.add(index);

        if (finished) {
            throw new RuntimeException("Already finished");
        }

        TypeWithContext typeWithParameters = typeIndexes.inverse().get(index);
        Type type = typeWithParameters.getType();
        TypeContext typeContext = typeWithParameters.getTypeContext();

        ExpressionStatement constructNewInstanceFunction = statement(
                function(
                        CONSTRUCT_TYPE_PREFIX + index,
                        block(
                                variable("existingValue", getType(index)),
                                ifThenElse(
                                        binary(
                                                binary(identifier("existingValue"), Operator.NOT_EQUAL_EQUAL, identifier(VARIABLE_NO_VALUE)),
                                                Operator.AND,
                                                binary(call(identifier("random")), Operator.GREATER_THAN, number(0.5))
                                        ),
                                        Return(identifier("existingValue")),
                                        this.constructNewInstanceOfType(type, typeContext)
                                )
                        )
                )
        );

        functions.add(constructNewInstanceFunction);
    }

    public BlockStatement getBlockStatementWithTypeFunctions() {
        return block(functions);
    }
}
