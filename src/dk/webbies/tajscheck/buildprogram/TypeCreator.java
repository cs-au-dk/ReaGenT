package dk.webbies.tajscheck.buildprogram;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.au.cs.casa.typescript.types.BooleanLiteral;
import dk.au.cs.casa.typescript.types.NumberLiteral;
import dk.au.cs.casa.typescript.types.StringLiteral;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.ParameterMap;
import dk.webbies.tajscheck.TypeWithParameters;
import dk.webbies.tajscheck.TypesUtil;
import dk.webbies.tajscheck.paser.AST.*;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.buildprogram.TestProgramBuilder.*;
import static dk.webbies.tajscheck.paser.AstBuilder.*;

/**
 * Created by erik1 on 03-11-2016.
 */
public class TypeCreator {
    private final BiMap<TypeWithParameters, Integer> typeIndexes;
    private final MultiMap<TypeWithParameters, Integer> valueLocations;
    private Map<Type, String> typeNames;
    private Set<Type> nativeTypes;
    private TypeParameterIndexer typeParameterIndexer;
    private ArrayList<Statement> functions = new ArrayList<>();

    private static final String GET_TYPE_PREFIX = "getType_";
    private static final String CONSTRUCT_TYPE_PREFIX = "constructType_";
    private List<Statement> valueVariableDeclarationList = new ArrayList<>();

    TypeCreator(Map<Type, String> typeNames, Set<Type> nativeTypes, TypeParameterIndexer typeParameterIndexer) {
        this.valueLocations = new ArrayListMultiMap<>();
        this.typeNames = typeNames;
        this.nativeTypes = nativeTypes;
        this.typeParameterIndexer = typeParameterIndexer;
        this.typeIndexes = HashBiMap.create();
    }

    private int valueCounter = 0;
    int createProducedValueVariable(Type type, ParameterMap parameterMap) {
        int index = valueCounter++;
        valueVariableDeclarationList.add(variable(VALUE_VARIABLE_PREFIX + index, identifier(VARIABLE_NO_VALUE)));

        putProducedValueIndex(index, type, parameterMap);
        return index;
    }

    public Collection<Statement> getValueVariableDeclarationList() {
        return valueVariableDeclarationList;
    }

    private void putProducedValueIndex(int index, Type type, ParameterMap parameterMap) {
        valueLocations.put(new TypeWithParameters(type, parameterMap), index);
        if (type instanceof InterfaceType) {
            List<Type> baseTypes = ((InterfaceType) type).getBaseTypes();
            baseTypes.forEach(baseType -> putProducedValueIndex(index, baseType, parameterMap));
        }
        if (type instanceof ReferenceType) {
            putProducedValueIndex(index, ((ReferenceType) type).getTarget(), TypesUtil.generateParameterMap((ReferenceType) type, parameterMap));
        }
        if (type instanceof GenericType) {
            putProducedValueIndex(index, ((GenericType) type).toInterface(), parameterMap);
        }
    }


    private Statement constructUnion(List<Type> types, ParameterMap parameterMap) {
        List<Integer> elements = types.stream().distinct().map((type) -> getTypeIndex(type, parameterMap)).collect(Collectors.toList());

        if (elements.size() == 1) {
            return Return(createType(elements.iterator().next()));
        }

        List<Pair<Expression, Statement>> cases = Util.withIndex(elements).map(pair -> {
            return new Pair<Expression, Statement>(number(pair.getRight()), Return(createType(pair.getLeft())));
        }).collect(Collectors.toList());

        return block(
                switchCase(
                        binary(binary(call(identifier("random")), Operator.MULT, number(elements.size())), Operator.BITWISE_OR, number(0)),
                        cases,
                        block(
                                comment("Unreachable"),
                                Return(createType(elements.iterator().next()))
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

    private final class ConstructNewInstanceVisitor implements TypeVisitorWithArgument<Statement, ParameterMap> {
        @Override
        public Statement visit(AnonymousType t, ParameterMap parameterMap) {
            throw new RuntimeException();
        }

        @Override
        public Statement visit(ClassType t, ParameterMap parameterMap) {
            throw new RuntimeException();
        }

        @Override
        public Statement visit(GenericType type, ParameterMap parameterMap) {
            assert type.getTypeParameters().equals(type.getTypeArguments());
            if (nativeTypes.contains(type)) {
                return constructTypeFromName(typeNames.get(type), parameterMap);
            }

            return type.toInterface().accept(this, parameterMap);
        }

        @Override
        public Statement visit(InterfaceType type, ParameterMap parameterMap) {
            if (nativeTypes.contains(type) && !TypesUtil.isEmptyInterface(type)) {
                return constructTypeFromName(typeNames.get(type), parameterMap);
            }


            Pair<InterfaceType, ParameterMap> pair = constructSyntheticInterfaceWithBaseTypes(type);
            InterfaceType inter = pair.getLeft();
            parameterMap = parameterMap.append(pair.getRight());
            assert inter.getBaseTypes().isEmpty();

            assert inter.getDeclaredStringIndexType() == null;
            assert inter.getDeclaredNumberIndexType() == null;

            List<Type> returnTypes = Util.concat(inter.getDeclaredCallSignatures(), inter.getDeclaredConstructSignatures()).stream().map(Signature::getResolvedReturnType).collect(Collectors.toList());

            List<Statement> program = new ArrayList<>();
            if (returnTypes.isEmpty()) {
                program.add(variable("result", object()));
            } else {
                program.add(variable("result", createFunction(inter, parameterMap)));
            }

            List<Pair<String, Type>> properties = inter.getDeclaredProperties().entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue())).collect(Collectors.toList());

            for (Pair<String, Type> property : properties) {
                program.add(statement(binary(member(identifier("result"), property.getLeft()), Operator.EQUAL, createType(property.getRight(), parameterMap))));
            }

            program.add(Return(identifier("result")));

            return block(program);
        }

        @Override
        public Statement visit(ReferenceType type, ParameterMap parameterMap) {
            if ("Array".equals(typeNames.get(type.getTarget()))) {
                Type indexType = type.getTypeArguments().iterator().next();
                Expression constructElement = createType(indexType, parameterMap);


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

            GenericType target = (GenericType) type.getTarget();

            return constructNewInstanceOfType(target.toInterface(), TypesUtil.generateParameterMap(type, parameterMap));
        }

        @Override
        public Statement visit(SimpleType simple, ParameterMap parameterMap) {
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
        public Statement visit(TupleType t, ParameterMap parameterMap) {
            throw new RuntimeException();
        }

        @Override
        public Statement visit(UnionType t, ParameterMap parameterMap) {
            return constructUnion(t.getElements(), parameterMap);
        }

        @Override
        public Statement visit(UnresolvedType t, ParameterMap parameterMap) {
            throw new RuntimeException();
        }

        @Override
        public Statement visit(TypeParameterType type, ParameterMap parameterMap) {
            if (parameterMap.containsKey(type)) {
                if (!TypesUtil.findRecursiveDefinition(type, parameterMap, typeParameterIndexer).isEmpty()) {
                    IntersectionType intersection = new IntersectionType();
                    intersection.setElements(TypesUtil.findRecursiveDefinition(type, parameterMap, typeParameterIndexer));

                    return constructNewInstanceOfType(intersection, parameterMap);
                }

                return constructNewInstanceOfType(parameterMap.get(type), parameterMap);
            }
            String markerField = typeParameterIndexer.getMarkerField(type);
            return block(
                    variable("result", call(function(constructNewInstanceOfType(type.getConstraint(), parameterMap)))),
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
        public Statement visit(SymbolType t, ParameterMap parameterMap) {
            throw new RuntimeException();
        }

        @Override
        public Statement visit(StringLiteral str, ParameterMap parameterMap) {
            return Return(string(str.getText()));
        }

        @Override
        public Statement visit(BooleanLiteral t, ParameterMap parameterMap) {
            return Return(bool(t.getValue()));
        }

        @Override
        public Statement visit(NumberLiteral t, ParameterMap parameterMap) {
            return Return(number(t.getValue()));
        }

        @Override
        public Statement visit(IntersectionType t, ParameterMap parameterMap) {
            return throwStatement(newCall(identifier("RuntimeError"), string("Not implemented yet, intersectionTypes")));
        }
    }

    private FunctionExpression createFunction(InterfaceType inter, ParameterMap parameterMap) {
        List<Signature> signatures = Util.concat(inter.getDeclaredCallSignatures(), inter.getDeclaredConstructSignatures());

        assert signatures.size() > 0;

        int maxArgs = signatures.stream().map(Signature::getParameters).map(List::size).reduce(0, Math::max);

        List<String> args = new ArrayList<>();
        for (int i = 0; i < maxArgs; i++) {
            args.add("arg" + i);
        }

        CheckType typeChecker = new CheckType(nativeTypes, typeNames, typeParameterIndexer, parameterMap);

        String interName = typeNames.get(inter);
        assert interName != null;

        assert !signatures.isEmpty();

        if (signatures.size() == 1) {
            Signature signature = signatures.iterator().next();

            // Currently changing nothing if it ended up not type-checking.
            List<Statement> typeChecks = Util.zip(args.stream(), signature.getParameters().stream(), (argName, par) ->
                typeChecker.assertResultingType(par.getType(), identifier(argName), interName + ".[" + argName + "]", Main.CHECK_DEPTH)
            ).collect(Collectors.toList());

            BlockStatement functionBody = block(
                    // Currently not using the information whether or not the signature was correct. The assertion-errors has already been reported anyway.
                    variable(identifier("signatureCorrect"), call(function(
                            block(
                                    Util.concat(
                                            typeChecks,
                                            Collections.singletonList(Return(bool(true)))
                                    )
                            )
                    ))),
                    saveArgsAndReturnValue(signature, parameterMap)
            );


            return function(
                    block(functionBody),
                    args
            );
        } else {
            Statement functionBody = block(
                    variable("foundSignatures", array()),
                    // Checking each signature, to see if correct.
                    block(Util.withIndex(signatures).map(signaturePair -> {
                        int signatureIndex = signaturePair.getRight();
                        Signature signature = signaturePair.getLeft();
                        return block(
                                variable("signatureCorrect" + signatureIndex, call(function(block(
                                        block(Util.withIndex(signature.getParameters()).map(parameterPair -> {
                                            Integer argIndex = parameterPair.getRight();
                                            Signature.Parameter arg = parameterPair.getLeft();

                                            return block(
                                                    variable(identifier("arg" + argIndex + "Correct"), typeChecker.checkResultingType(arg.getType(), identifier("arg" + argIndex), interName + ".[arg" + argIndex + "]", Main.CHECK_DEPTH_FOR_UNIONS)),
                                                    ifThen(
                                                            unary(Operator.NOT, identifier("arg" + argIndex + "Correct")),
                                                            Return(bool(false))
                                                    )
                                            );
                                        }).collect(Collectors.toList())),
                                        Return(bool(true))
                                )))),
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
                                    statement(call(identifier("assert"), bool(false), string(interName), string("A valid overload"), string("No valid overloads found!"))),
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
                                    statement(call(identifier("error"), binary(string("Could not find correct overload for function: " + interName + " results: "), Operator.PLUS, methodCall(identifier("foundSignatures"), "toString")))),
                                    Return()
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
                                        saveArgsAndReturnValue(signature, parameterMap)
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

    private BlockStatement saveArgsAndReturnValue(Signature signature, ParameterMap parameterMap) {
        List<Statement> saveArgumentValues = Util.withIndex(
                signature.getParameters().stream().map(par -> createProducedValueVariable(par.getType(), parameterMap)),
                (valueIndex, argIndex) -> {
                    return statement(binary(identifier(VALUE_VARIABLE_PREFIX + valueIndex), Operator.EQUAL, identifier("arg" + argIndex)));
                }
        ).collect(Collectors.toList());

        return block(
                block(saveArgumentValues),
                Return(createType(signature.getResolvedReturnType(), parameterMap))
        );
    }


    private Statement constructTypeFromName(String name, ParameterMap parameterMap) {
        if (name == null) {
            throw new NullPointerException();
        }
        switch (name) {
            case "Object":
                return constructNewInstanceOfType(SpecReader.makeEmptySyntheticInterfaceType(), parameterMap);
            case "Number":
                return constructNewInstanceOfType(new SimpleType(SimpleTypeKind.Number), parameterMap);
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
                return constructNewInstanceOfType(interfaceWithSimpleFunction, parameterMap);
            case "Error":
                return Return(newCall(identifier("Error")));
            case "RegExp":
                Expression constructString = call(function(constructNewInstanceOfType(new SimpleType(SimpleTypeKind.String), new ParameterMap())));
                return Return(newCall(identifier("RegExp"), constructString));
            case "String":
                return constructNewInstanceOfType(new SimpleType(SimpleTypeKind.String), parameterMap);
            default:
                throw new RuntimeException("Unknown: " + name);
        }
    }


    private Statement constructNewInstanceOfType(Type type, ParameterMap parameterMap) {
        return type.accept(new ConstructNewInstanceVisitor(), parameterMap);
    }

    private Pair<InterfaceType, ParameterMap> constructSyntheticInterfaceWithBaseTypes(InterfaceType inter) {
        if (inter.getBaseTypes().isEmpty()) {
            return new Pair<>(inter, new ParameterMap());
        }
//        assert inter.getTypeParameters().isEmpty(); // This should only happen when constructed from a generic/reference type, and in that case we have handled the TypeParameters.
        Map<TypeParameterType, Type> newParameters = new ParameterMap().getMap();
        InterfaceType result = SpecReader.makeEmptySyntheticInterfaceType();
        inter.getBaseTypes().forEach(subType -> {
            if (subType instanceof ReferenceType) {
                newParameters.putAll(TypesUtil.generateParameterMap((ReferenceType) subType).getMap());
                subType = ((ReferenceType) subType).getTarget();
            }
            if (subType instanceof GenericType) {
                subType = ((GenericType) subType).toInterface();
            }
            Pair<InterfaceType, ParameterMap> pair = constructSyntheticInterfaceWithBaseTypes((InterfaceType) subType);
            newParameters.putAll(pair.getRight().getMap());
            InterfaceType type = pair.getLeft();
            result.getDeclaredCallSignatures().addAll((type.getDeclaredCallSignatures()));
            result.getDeclaredConstructSignatures().addAll(type.getDeclaredConstructSignatures());
            if (inter.getDeclaredNumberIndexType() != null) {
                result.setDeclaredNumberIndexType(inter.getDeclaredNumberIndexType());
            } else {
                result.setDeclaredNumberIndexType(type.getDeclaredNumberIndexType());
            }
            if (inter.getDeclaredStringIndexType() != null) {
                result.setDeclaredStringIndexType(inter.getDeclaredStringIndexType());
            } else {
                result.setDeclaredStringIndexType(type.getDeclaredStringIndexType());
            }
            result.getDeclaredProperties().putAll(inter.getDeclaredProperties());
            for (Map.Entry<String, Type> entry : type.getDeclaredProperties().entrySet()) {
                if (result.getDeclaredProperties().containsKey(entry.getKey())) {
                    continue;
                }
                result.getDeclaredProperties().put(entry.getKey(), entry.getValue());
            }
        });
        return new Pair<>(result, new ParameterMap().append(newParameters));
    }

    public CallExpression getType(Type type, ParameterMap parameterMap) {
        int index = getTypeIndex(type, parameterMap);

        return getType(index);
    }

    public CallExpression getType(int index) {
        return call(identifier(GET_TYPE_PREFIX + index));
    }

    public CallExpression createType(Type type, ParameterMap parameterMap) {
        int index = getTypeIndex(type, parameterMap);

        addConstructInstanceFunction(index);

        return call(identifier(CONSTRUCT_TYPE_PREFIX + index));
    }

    public CallExpression createType(int index) {
        TypeWithParameters typeWithParameters = typeIndexes.inverse().get(index);
        return createType(typeWithParameters.getType(), typeWithParameters.getParameterMap());
    }

    private int getTypeIndex(Type type, ParameterMap parameterMap) {
//        ParameterMap parameterMap = TypesUtil.filterParameterMap(unFilteredParameterMap, type);

        TypeWithParameters key = new TypeWithParameters(type, parameterMap);
        if (typeIndexes.containsKey(key)) {
            return typeIndexes.get(key);
        } else {
            int value = typeIndexes.size();
            typeIndexes.put(key, value);

//            getType()

            getTypeFunctionQueue.add(key);

            return value;
        }
    }

    private List<TypeWithParameters> getTypeFunctionQueue = new ArrayList<>();

    public void finish() {
        for (TypeWithParameters type : getTypeFunctionQueue) {
            addGetTypeFunction(type.getType(), type.getParameterMap());
        }
    }

    private int addGetTypeFunction(Type type, ParameterMap parameterMap) {
        int value = typeIndexes.get(new TypeWithParameters(type, parameterMap));

        Collection<Integer> values = valueLocations.keySet().stream()
                .filter(candidate -> type.equals(candidate.getType()) && parameterMap.equals(candidate.getParameterMap()))
                .map(valueLocations::get)
                .reduce(new ArrayList<>(), Util::reduceCollection);

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
        return value;
    }

    private final Set<Integer> hasCreateTypeFunction = new HashSet<>();

    private void addConstructInstanceFunction(int index) {
        if (hasCreateTypeFunction.contains(index)) {
            return;
        }
        hasCreateTypeFunction.add(index);

        TypeWithParameters typeWithParameters = typeIndexes.inverse().get(index);
        Type type = typeWithParameters.getType();
        ParameterMap parameterMap = typeWithParameters.getParameterMap();

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
                                        this.constructNewInstanceOfType(type, parameterMap)
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
