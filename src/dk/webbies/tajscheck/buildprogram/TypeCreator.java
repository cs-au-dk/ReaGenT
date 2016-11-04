package dk.webbies.tajscheck.buildprogram;

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

import static dk.webbies.tajscheck.buildprogram.TestProgramBuilder.GET_TYPE_PREFIX;
import static dk.webbies.tajscheck.buildprogram.TestProgramBuilder.TYPES_VARIABLE_PREFIX;
import static dk.webbies.tajscheck.buildprogram.TestProgramBuilder.VARIABLE_NO_VALUE;
import static dk.webbies.tajscheck.paser.AstBuilder.*;
import static dk.webbies.tajscheck.paser.AstBuilder.identifier;

/**
 * Created by erik1 on 03-11-2016.
 */
public class TypeCreator {
    private final Map<Type, Integer> typeIndexes;
    private final MultiMap<Type, Integer> valueLocations;
    private Map<Type, String> typeNames;
    private ArrayList<Statement> functions = new ArrayList<>();

    public TypeCreator(MultiMap<Type, Integer> valueLocations, Map<Type, String> typeNames) {
        this.valueLocations = valueLocations;
        this.typeNames = typeNames;
        this.typeIndexes = new HashMap<>();
    }

    public Statement createGetTypeStatement(Type type) {
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

        if (type instanceof UnionType) {
            UnionType union = (UnionType) type;

            List<Type> types = union.getElements();
            return returnOneOfTypes(types);
        }

        if (type instanceof ReferenceType && "Array".equals(typeNames.get(((ReferenceType) type).getTarget()))) {
            Type indexType = ((ReferenceType) type).getTypeArguments().iterator().next();
            Expression constructArrayElement = call(identifier(GET_TYPE_PREFIX + getTypeIndex(indexType)));
            return Return(array(constructArrayElement, constructArrayElement, constructArrayElement)); // TODO: An at runtime random number of elements, from 0 to 5.
        }

        if ("Object".equals(typeNames.get(type))) {
            return Return(object());
        }
        if ("Number".equals(typeNames.get(type))) {
            return createGetTypeStatement(new SimpleType(SimpleTypeKind.Number));
        }

        if (!(type instanceof InterfaceType || type instanceof GenericType)) {
            throw new RuntimeException("Unhandled type: " + type.getClass());
        }
        List<Integer> values = new ArrayList<>(valueLocations.get(type));

        // TODO: Always construct a type, but use it as fallback.

        if (values.size() == 1) {
            return Return(identifier(TYPES_VARIABLE_PREFIX + values.iterator().next()));
        }
        if (values.isEmpty()) {
            return constructType(type);
        }

        return returnOneOfIndexes(values, false);
    }

    private Statement returnOneOfTypes(List<Type> types) {
        List<Integer> elements = types.stream().distinct().map(this::getTypeIndex).collect(Collectors.toList());

        return returnOneOfIndexes(elements, true);
    }

    private Statement returnOneOfIndexes(List<Integer> elements, boolean fromTypes) {
        if (elements.size() == 1) {
            Integer index = elements.iterator().next();
            return Return(call(identifier(GET_TYPE_PREFIX + index)));
        }

        List<Pair<Integer, Integer>> elementsWithIndex = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            elementsWithIndex.add(new Pair<>(i, elements.get(i)));
        }
        List<Pair<Expression, Statement>> cases = elementsWithIndex.stream().map(pair -> {
            Expression getValueOrType;
            if (fromTypes) {
                getValueOrType = call(identifier(GET_TYPE_PREFIX + pair.getRight()));
            } else {
                getValueOrType = identifier(TYPES_VARIABLE_PREFIX + pair.getRight());
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

    private Statement constructType(Type type) {
        if (type instanceof InterfaceType) {
            InterfaceType inter = (InterfaceType) type;
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
                        variable(identifier("functionResult"), call(function(returnOneOfTypes(returnTypes))))
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
                                Return(call(identifier(GET_TYPE_PREFIX + getTypeIndex(property.getRight()))))
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

        throw new RuntimeException();
    }

    public int getTypeIndex(Type type) {
        if (typeIndexes.containsKey(type)) {
            Integer result = typeIndexes.get(type);
            return result;
        } else {
            int key = typeIndexes.size();
            typeIndexes.put(type, key);

            functions.add(expressionStatement(
                    function(
                            GET_TYPE_PREFIX + key,
                            block(this.createGetTypeStatement(type))
                    )
            ));

            return key;
        }
    }

    public BlockStatement getBlockStatementWithTypeFunctions() {
        return block(functions);
    }
}
