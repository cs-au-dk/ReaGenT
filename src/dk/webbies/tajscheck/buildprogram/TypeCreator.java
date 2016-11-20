package dk.webbies.tajscheck.buildprogram;

import com.google.common.reflect.TypeParameter;
import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.au.cs.casa.typescript.types.BooleanLiteral;
import dk.au.cs.casa.typescript.types.NumberLiteral;
import dk.au.cs.casa.typescript.types.StringLiteral;
import dk.webbies.tajscheck.ParameterMap;
import dk.webbies.tajscheck.TypeWithParameters;
import dk.webbies.tajscheck.TypesUtil;
import dk.webbies.tajscheck.paser.AST.*;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.buildprogram.TestProgramBuilder.*;
import static dk.webbies.tajscheck.paser.AstBuilder.*;
import static dk.webbies.tajscheck.paser.AstBuilder.identifier;

/**
 * Created by erik1 on 03-11-2016.
 */
public class TypeCreator {
    private final Map<TypeWithParameters, Integer> typeIndexes;
    private final MultiMap<TypeWithParameters, Integer> valueLocations;
    private Map<Type, String> typeNames;
    private Set<Type> nativeTypes;
    private TypeParameterIndexer typeParameterIndexer;
    private ArrayList<Statement> functions = new ArrayList<>();

    public TypeCreator(MultiMap<TypeWithParameters, Integer> valueLocations, Map<Type, String> typeNames, Set<Type> nativeTypes, TypeParameterIndexer typeParameterIndexer) {
        this.valueLocations = valueLocations;
        this.typeNames = typeNames;
        this.nativeTypes = nativeTypes;
        this.typeParameterIndexer = typeParameterIndexer;
        this.typeIndexes = new HashMap<>();
    }

    public Statement getExistingInstanceOfType(Type type, ParameterMap parameterMap) {
        // This is filtering the keys, where the type is the same, and where the map contains at least the same key/value pairs.
        Collection<Integer> values = valueLocations.keySet().stream()
                .filter(candidate -> TypeCreator.canTypeBeUsed(candidate, new TypeWithParameters(type, parameterMap))).map(valueLocations::get)
                .reduce(new ArrayList<>(), Util::reduceCollection);

        if (values.size() == 1) {
            return Return(identifier(VALUE_VARIABLE_PREFIX + values.iterator().next()));
        }
        if (values.isEmpty()) {
            return Return(identifier(VARIABLE_NO_VALUE));
        }

        return returnOneOfIndexes(values, false, false);
    }

    /*
            Collection<Integer> values = valueLocations.keySet().stream()
                .check(key ->
                        key.getTypeString() == type
                ).check(key -> {
                    return parameterMap.entrySet().stream().allMatch(entry -> {
                        return parameterMap.containsKey(entry.getKey()) && parameterMap.get(entry.getKey()) == entry.getValue();
                    });
                }).map(valueLocations::get)
     */
    private static boolean canTypeBeUsed(TypeWithParameters candidate, TypeWithParameters type) {
        if (!candidate.getType().equals(type.getType())) {
            return false;
        }
        return type.getParameterMap().isSubSetOf(candidate.getParameterMap());
    }

    private Statement returnOneOfTypes(List<Type> types, boolean construct, ParameterMap parameterMap) {
        List<Integer> elements = types.stream().distinct().map((type) -> getTypeIndex(type, parameterMap)).collect(Collectors.toList());

        return returnOneOfIndexes(elements, true, construct);
    }

    private Statement returnOneOfIndexes(Collection<Integer> elementsCollection, boolean fromTypes, boolean constructNew) {
        List<Integer> elements = new ArrayList<>(elementsCollection);
        if (elements.size() == 1) {
            Integer index = elements.iterator().next();
            if (fromTypes) {
                if (constructNew) {
                    return Return(call(identifier(CONSTRUCT_TYPE_PREFIX + index)));
                } else {
                    return Return(call(identifier(GET_TYPE_PREFIX + index)));
                }
            } else {
                return Return(identifier(VALUE_VARIABLE_PREFIX + index));
            }
        }

        List<Pair<Integer, Integer>> elementsWithIndex = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            elementsWithIndex.add(new Pair<>(i, elements.get(i)));
        }
        List<Pair<Expression, Statement>> cases = elementsWithIndex.stream().map(pair -> {
            Expression getValueOrType;
            if (fromTypes) {
                if (constructNew) {
                    getValueOrType = call(identifier(CONSTRUCT_TYPE_PREFIX + pair.getRight()));
                } else {
                    getValueOrType = call(identifier(GET_TYPE_PREFIX + pair.getRight()));
                }
            } else {
                getValueOrType = identifier(VALUE_VARIABLE_PREFIX + pair.getRight());
            }
            return new Pair<Expression, Statement>(number(pair.getLeft()), block(
                    variable("result", getValueOrType),
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
                // TODO: This is wrong, it should actually return the correct type corrosponding to the arguments given.
                program.add(
                        variable("functionResult", call(function(returnOneOfTypes(returnTypes, true, parameterMap))))
                );
                program.add(
                        ifThenElse(
                                binary(identifier("functionResult"), Operator.EQUAL_EQUAL_EQUAL, identifier(VARIABLE_NO_VALUE)),
                                Return(identifier(VARIABLE_NO_VALUE)),
                                variable(
                                        identifier("result"),
                                        function(
                                                Return(identifier("functionResult"))
                                        )
                                )
                        )
                );
            }

            List<Pair<String, Type>> properties = inter.getDeclaredProperties().entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue())).collect(Collectors.toList());

            for (int i = 0; i < properties.size(); i++) {
                Pair<String, Type> property = properties.get(i);
                program.add(
                        variable("objectResult_" + i, call(function(
                                Return(call(identifier(CONSTRUCT_TYPE_PREFIX + getTypeIndex(property.getRight(), parameterMap))))
                        )))
                );
                program.add(ifThenElse(
                        binary(identifier("objectResult_" + i), Operator.EQUAL_EQUAL_EQUAL, identifier(VARIABLE_NO_VALUE)),
                        Return(identifier(VARIABLE_NO_VALUE)),
                        expressionStatement(binary(member(identifier("result"), property.getLeft()), Operator.EQUAL, identifier("objectResult_" + i)))
                ));
            }

            program.add(Return(identifier("result")));

            return block(program);
        }

        @Override
        public Statement visit(ReferenceType type, ParameterMap parameterMap) {
            if ("Array".equals(typeNames.get(type.getTarget()))) {
                Type indexType = type.getTypeArguments().iterator().next();
                Expression constructArrayElement = call(identifier(CONSTRUCT_TYPE_PREFIX + getTypeIndex(indexType, parameterMap)));
                return Return(array(constructArrayElement, constructArrayElement, constructArrayElement)); // TODO: An at runtime random number of elements, from 0 to 5.
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
            return returnOneOfTypes(t.getElements(), true, parameterMap);
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
                    expressionStatement(
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
            return throwStatement(newCall(identifier("RuntimeError"), string("Not implemented yet, intersectionTypes"))); // TODO:
        }
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

    public int getTypeIndex(Type type, ParameterMap parameterMap) {
        parameterMap = TypesUtil.filterParameterMap(parameterMap, type);

        TypeWithParameters key = new TypeWithParameters(type, parameterMap);
        if (typeIndexes.containsKey(key)) {
            return typeIndexes.get(key);
        } else {
            int value = typeIndexes.size();
            typeIndexes.put(key, value);

            ExpressionStatement primaryFunction = expressionStatement(
                    function(
                            GET_TYPE_PREFIX + value,
                            block(this.getExistingInstanceOfType(type, parameterMap))
                    )
            );

            ExpressionStatement constructNewInstanceFunction = expressionStatement(
                    function(
                            CONSTRUCT_TYPE_PREFIX + value,
                            block(
                                    variable("result", call(identifier(GET_TYPE_PREFIX + value))),
                                    ifThenElse(
                                            binary(
                                                    binary(identifier("result"), Operator.NOT_EQUAL_EQUAL, identifier(VARIABLE_NO_VALUE)),
                                                    Operator.AND,
                                                    binary(call(identifier("random")), Operator.GREATER_THAN, number(0.8))
                                            ),
                                            Return(identifier("result")),
                                            this.constructNewInstanceOfType(type, parameterMap)
                                    )
                            )
                    )
            );

            functions.add(primaryFunction);
            functions.add(constructNewInstanceFunction);

            return value;
        }
    }

    public BlockStatement getBlockStatementWithTypeFunctions() {
        return block(functions);
    }
}
