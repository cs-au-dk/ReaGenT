package dk.webbies.tajscheck.buildprogram;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.au.cs.casa.typescript.types.BooleanLiteral;
import dk.au.cs.casa.typescript.types.NumberLiteral;
import dk.au.cs.casa.typescript.types.StringLiteral;
import dk.webbies.tajscheck.paser.AST.*;
import dk.webbies.tajscheck.paser.AstBuilder;
import dk.webbies.tajscheck.util.Util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.buildprogram.TestProgramBuilder.VARIABLE_NO_VALUE;
import static dk.webbies.tajscheck.paser.AstBuilder.*;

/**
 * Created by erik1 on 03-11-2016.
 */
public class CheckType {
    private final Set<Type> nativeTypes;
    private Map<Type, String> typeNames;

    public CheckType(Set<Type> nativeTypes, Map<Type, String> typeNames) {
        this.nativeTypes = nativeTypes;
        this.typeNames = typeNames;
    }

    public Statement checkResultingType(Type type, Expression exp, String path) {
        List<TypeCheck> typeChecks = generateAssertExpressions(type, exp, path);


        return block(
                typeChecks.stream().map(check -> {
                    // assert(cond, path, expected, actual)
                    CallExpression assertCall = call(identifier("assert"), check.expression, string(path), string(check.expected), exp);
                    return ifThen(unary(Operator.NOT, AstBuilder.and(assertCall)), Return());
                }).collect(Collectors.toList())
        );
    }

    private static final class TypeCheck {
        private final Expression expression;
        private final String expected;

        private TypeCheck(Expression expression, String expected) {
            this.expression = expression;
            this.expected = expected;
        }
    }

    private List<TypeCheck> generateAssertExpressions(Type type, Expression exp, String path) {
        if ("Object".equals(typeNames.get(type))) {
            type = SpecReader.makeEmptySyntheticInterfaceType();
        }
        if ("Number".equals(typeNames.get(type))) {
            type = new SimpleType(SimpleTypeKind.Number);
        }
        if ("String".equals(typeNames.get(type))) {
            type = new SimpleType(SimpleTypeKind.String);
        }

        if (type instanceof ReferenceType && "Array".equals(typeNames.get(((ReferenceType) type).getTarget()))) {
            // TODO: Check the index type:
            BinaryExpression check = binary(string("[object Array]"), Operator.EQUAL_EQUAL_EQUAL, methodCall(member(member(identifier("Object"), "prototype"), "toString"), "call", exp));

            return Collections.singletonList(new TypeCheck(check, "Array"));
        } else if ("Date".equals(typeNames.get(type))) {
            BinaryExpression check = binary(member(exp, "__proto__"), Operator.EQUAL_EQUAL_EQUAL, member(identifier("Date"), "prototype"));

            return Collections.singletonList(new TypeCheck(check, "Array"));
        } else if (nativeTypes.contains(type) && !(type instanceof SimpleType) && !(type instanceof UnionType)) {
            throw new RuntimeException();
        } else if (type instanceof SimpleType || type instanceof InterfaceType || type instanceof GenericType || type instanceof UnionType) {
            Expression check = generateCheckResultingType(type, exp);

            return Collections.singletonList(new TypeCheck(check, getShortDescription(type)));
        } else if (type instanceof StringLiteral) {
            BinaryExpression check = binary(string(((StringLiteral) type).getText()), Operator.EQUAL_EQUAL_EQUAL, exp);

            return Collections.singletonList(new TypeCheck(check, "string constant\"" + ((StringLiteral) type).getText() + "\""));
        } else if (type instanceof NumberLiteral) {
            BinaryExpression check = binary(number(((NumberLiteral) type).getValue()), Operator.EQUAL_EQUAL_EQUAL, exp);

            return Collections.singletonList(new TypeCheck(check, "number " + ((NumberLiteral) type).getValue()));
        } else if (type instanceof BooleanLiteral) {
            BinaryExpression check = binary(bool(((BooleanLiteral) type).getValue()), Operator.EQUAL_EQUAL_EQUAL, exp);

            return Collections.singletonList(new TypeCheck(check, "boolean constant: " + ((BooleanLiteral) type).getValue()));
        } else {
            throw new RuntimeException("Unhandled type: " + type.getClass());
        }
    }

    private Expression generateCheckResultingType(Type type, Expression exp) {
        if (type instanceof UnionType) {
            List<Type> elements = ((UnionType) type).getElements();

            List<Expression> checkExpressions = elements.stream().map(element -> generateCheckResultingType(element, exp)).collect(Collectors.toList());

            return AstBuilder.or(checkExpressions);
        } else if (type instanceof BooleanLiteral) {
            return binary(exp, Operator.EQUAL_EQUAL_EQUAL, bool(((BooleanLiteral) type).getValue()));
        } else if (type instanceof NumberLiteral) {
            return binary(exp, Operator.EQUAL_EQUAL_EQUAL, number(((NumberLiteral) type).getValue()));
        } else if (type instanceof StringLiteral) {
            return binary(exp, Operator.EQUAL_EQUAL_EQUAL, string(((StringLiteral) type).getText()));
        } else {
            if (type instanceof SimpleType && ((SimpleType) type).getKind() == SimpleTypeKind.Any) {
                return bool(true);
            }

            String typeDescription = getTypeOfDescription(type);

            // assert(typeof exp === type, "Expected " + path + " to be a " + type + "was a " + typeof type

            // typeof exp === type
            return binary(unary(Operator.TYPEOF, exp), Operator.EQUAL_EQUAL_EQUAL, string(typeDescription));
        }
    }

    private String getShortDescription(Type type) {
        if (type instanceof SimpleType && ((SimpleType) type).getKind() == SimpleTypeKind.Any) {
            return "any";
        }
        if (type instanceof SimpleType || type instanceof GenericType || type instanceof InterfaceType) {
            return getTypeOfDescription(type);
        } else if (type instanceof UnionType) {
            String result = "(";
            List<Type> elements = ((UnionType) type).getElements();
            for (int i = 0; i < elements.size(); i++) {
                Type element = elements.get(i);
                result += getShortDescription(element);
                if (i != elements.size() - 1) {
                    result += " or ";
                }
            }
            return result + ")";
        } else if (type instanceof BooleanLiteral) {
            return Boolean.toString(((BooleanLiteral) type).getValue());
        } else if (type instanceof NumberLiteral) {
            return Util.toPrettyNumber(((NumberLiteral) type).getValue());
        } else if (type instanceof StringLiteral) {
            return "\"" + ((StringLiteral) type).getText() + "\"";
        }
        throw new RuntimeException("Unhandled type: " + type.getClass());
    }

    private String getTypeOfDescription(Type type) {

        if (type instanceof SimpleType) {
            switch (((SimpleType) type).getKind()) {
                case String:
                    return "string";
                case Boolean:
                    return "boolean";
                case Number:
                    return "number";
                case Undefined:
                    return "undefined";
                default:
                    throw new RuntimeException("Unhandled simple kind: " + ((SimpleType) type).getKind());
            }
        } else if (type instanceof InterfaceType) {
            InterfaceType inter = (InterfaceType) type;
            if (!inter.getDeclaredCallSignatures().isEmpty() || !inter.getDeclaredConstructSignatures().isEmpty()) {
                return "function";
            } else {
                return "object";
            }
        } else if (type instanceof GenericType) {
            GenericType inter = (GenericType) type;
            if (!inter.getDeclaredCallSignatures().isEmpty() || !inter.getDeclaredConstructSignatures().isEmpty()) {
                return "function";
            } else {
                return "object";
            }
        }

        throw new RuntimeException("Unhandled type: " + type.getClass());
    }
}
