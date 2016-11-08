package dk.webbies.tajscheck.buildprogram;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.au.cs.casa.typescript.types.BooleanLiteral;
import dk.au.cs.casa.typescript.types.NumberLiteral;
import dk.au.cs.casa.typescript.types.StringLiteral;
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
    private final Map<Type, Integer> typeIndexes;
    private final MultiMap<Type, Integer> valueLocations;
    private Map<Type, String> typeNames;
    private Set<Type> nativeTypes;
    private ArrayList<Statement> functions = new ArrayList<>();

    public TypeCreator(MultiMap<Type, Integer> valueLocations, Map<Type, String> typeNames, Set<Type> nativeTypes) {
        this.valueLocations = valueLocations;
        this.typeNames = typeNames;
        this.nativeTypes = nativeTypes;
        this.typeIndexes = new HashMap<>();
    }

    public Statement getExistingInstanceOfType(Type type) {
        List<Integer> values = new ArrayList<>(valueLocations.get(type));

        if (values.size() == 1) {
            return Return(identifier(VALUE_VARIABLE_PREFIX + values.iterator().next()));
        }
        if (values.isEmpty()) {
            return Return(identifier(VARIABLE_NO_VALUE));
        }

        return returnOneOfIndexes(values, false, false);
    }

    private Statement returnOneOfTypes(List<Type> types, boolean construct) {
        List<Integer> elements = types.stream().distinct().map(this::getTypeIndex).collect(Collectors.toList());

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
                    variable(identifier("result"), getValueOrType),
                    ifThen(binary(identifier("result"), Operator.NOT_EQUAL_EQUAL, identifier(VARIABLE_NO_VALUE)), Return(identifier("result")))
            ));
        }).collect(Collectors.toList());

        return block(
                switchCase(
                        binary(binary(methodCall(identifier("Math"), "random"), Operator.MULT, number(elements.size())), Operator.BITWISE_OR, number(0)),
                        cases
                ),
                // If the switch fails to return, check if anything can be returned.
                block(cases.stream().map(Pair::getRight).collect(Collectors.toList())),
                // If nothing has been returned, return the NO_VALUE object.
                Return(identifier(TestProgramBuilder.VARIABLE_NO_VALUE))
        );
    }

    private Statement constructNewInstanceOfType(Type type) {
        if (type instanceof SimpleType) {
            SimpleType simple = (SimpleType) type;
            if (simple.getKind() == SimpleTypeKind.String) {
                // Math.random().toString(36).substring(Math.random() * 20);
                MethodCallExpression produceRandomString = methodCall(methodCall(methodCall(identifier("Math"), "random"), "toString", number(26)), "substring",
                        binary(
                                methodCall(identifier("Math"), "random"),
                                Operator.MULT,
                                number(20)
                        ));
                return Return(produceRandomString);
            } else if (simple.getKind() == SimpleTypeKind.Number) {
                return Return(methodCall(identifier("Math"), "random"));
            } else if (simple.getKind() == SimpleTypeKind.Boolean) {
                // Math.random() > 0.5
                return Return(
                        binary(
                                methodCall(identifier("Math"), "random"),
                                Operator.LESS_THAN,
                                number(0.5)
                        )
                );
            } else if (simple.getKind() == SimpleTypeKind.Any) {
                return Return(
                        object(new ObjectLiteral.Property("__isAnyMarker", object()))
                );
            } else if (simple.getKind() == SimpleTypeKind.Undefined) {
                return Return(
                        unary(Operator.VOID, number(0))
                );
            }
            throw new RuntimeException("Cannot yet produce a simple: " + simple.getKind());
        }

        if (type instanceof StringLiteral) {
            return Return(string(((StringLiteral) type).getText()));
        }
        if (type instanceof BooleanLiteral) {
            return Return(bool(((BooleanLiteral) type).getValue()));
        }
        if (type instanceof NumberLiteral) {
            return Return(number(((NumberLiteral) type).getValue()));
        }

        if (typeNames.containsKey(type) && nativeTypes.contains(type)) {
            String name = typeNames.get(type);
            switch (name) {
                case "Object":
                    return Return(object());
                case "Number":
                    return constructNewInstanceOfType(new SimpleType(SimpleTypeKind.Number));
                case "Date":
                    return Return(newCall(identifier("Date")));
                default:
                    throw new RuntimeException("Unknown: " + name);
            }
        }

        if (type instanceof InterfaceType) {
            InterfaceType inter = constructSyntheticInterfaceWithBaseTypes((InterfaceType) type);
            assert inter.getBaseTypes().isEmpty();
            assert inter.getTypeParameters().isEmpty();

            assert inter.getDeclaredStringIndexType() == null;
            assert inter.getDeclaredNumberIndexType() == null;

            List<Type> returnTypes = Util.concat(inter.getDeclaredCallSignatures(), inter.getDeclaredConstructSignatures()).stream().map(Signature::getResolvedReturnType).collect(Collectors.toList());

            List<Statement> program = new ArrayList<>();
            if (returnTypes.isEmpty()) {
                program.add(variable(identifier("result"), object()));
            } else {
                program.add(
                        variable(identifier("functionResult"), call(function(returnOneOfTypes(returnTypes, true))))
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
                        variable(identifier("objectResult_" + i), call(function(
                                Return(call(identifier(CONSTRUCT_TYPE_PREFIX + getTypeIndex(property.getRight()))))
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

        if (type instanceof ReferenceType && "Array".equals(typeNames.get(((ReferenceType) type).getTarget()))) {
            // TODO: This is basically copy-paste, do something about that.
            Type indexType = ((ReferenceType) type).getTypeArguments().iterator().next();
            Expression constructArrayElement = call(identifier(CONSTRUCT_TYPE_PREFIX + getTypeIndex(indexType)));
            return Return(array(constructArrayElement, constructArrayElement, constructArrayElement)); // TODO: An at runtime random number of elements, from 0 to 5.
        }

        if (type instanceof UnionType) {
            return returnOneOfTypes(((UnionType) type).getElements(), true);
        }

        throw new RuntimeException(type.getClass().toString());
    }

    private InterfaceType constructSyntheticInterfaceWithBaseTypes(InterfaceType inter) {
        if (inter.getBaseTypes().isEmpty()) {
            return inter;
        }
        assert inter.getTypeParameters().isEmpty();
        InterfaceType result = SpecReader.makeEmptySyntheticInterfaceType();
        inter.getBaseTypes().forEach(subType -> {
            InterfaceType type = constructSyntheticInterfaceWithBaseTypes((InterfaceType) subType);
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
            assert type.getTypeParameters().isEmpty();
        });
        return result;
    }

    public int getTypeIndex(Type type) {
        if (typeIndexes.containsKey(type)) {
            return typeIndexes.get(type);
        } else {
            int key = typeIndexes.size();
            typeIndexes.put(type, key);

            ExpressionStatement primaryFunction = expressionStatement(
                    function(
                            GET_TYPE_PREFIX + key,
                            block(this.getExistingInstanceOfType(type))
                    )
            );

            ExpressionStatement constructNewInstanceFunction = expressionStatement(
                    function(
                            CONSTRUCT_TYPE_PREFIX + key,
                            block(
                                    variable(identifier("result"), call(identifier(GET_TYPE_PREFIX + key))),
                                    ifThenElse(
                                            binary(identifier("result"), Operator.NOT_EQUAL_EQUAL, identifier(VARIABLE_NO_VALUE)),
                                            Return(identifier("result")),
                                            this.constructNewInstanceOfType(type)
                                    )
                            )
                    )
            );

            functions.add(primaryFunction);
            functions.add(constructNewInstanceFunction);

            return key;
        }
    }

    public BlockStatement getBlockStatementWithTypeFunctions() {
        return block(functions);
    }
}
