package dk.webbies.tajscheck.buildprogram;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.au.cs.casa.typescript.types.BooleanLiteral;
import dk.au.cs.casa.typescript.types.NumberLiteral;
import dk.au.cs.casa.typescript.types.StringLiteral;
import dk.webbies.tajscheck.paser.AST.*;
import dk.webbies.tajscheck.paser.ASTUtil;
import dk.webbies.tajscheck.paser.AstBuilder;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.paser.AstBuilder.*;

/**
 * Created by erik1 on 03-11-2016.
 */
public class CheckType {
    private final Set<Type> nativeTypes;
    private Map<Type, String> typeNames;
    private TestProgramBuilder.TypeParameterIndexer typeParameterIndexer;
    private Map<TypeParameterType, Type> parameterMap;

    public CheckType(Set<Type> nativeTypes, Map<Type, String> typeNames, TestProgramBuilder.TypeParameterIndexer typeParameterIndexer, Map<TypeParameterType, Type> parameterMap) {
        this.nativeTypes = nativeTypes;
        this.typeNames = typeNames;
        this.typeParameterIndexer = typeParameterIndexer;
        this.parameterMap = parameterMap;
    }

    public Statement checkResultingType(Type type, Expression exp, String path) {
        List<TypeCheck> typeChecks = generateAssertExpressions(type, exp);


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

        public Expression getExpression() {
            return expression;
        }

        public String getExpected() {
            return expected;
        }
    }

    private List<TypeCheck> generateAssertExpressions(Type type, Expression exp) {
        if ("Object".equals(typeNames.get(type))) {
            type = SpecReader.makeEmptySyntheticInterfaceType();
        }
        if ("Number".equals(typeNames.get(type))) {
            type = new SimpleType(SimpleTypeKind.Number);
        }
        if ("String".equals(typeNames.get(type))) {
            type = new SimpleType(SimpleTypeKind.String);
        }

        if (type instanceof UnionType) {
            List<List<TypeCheck>> unionElements = ((UnionType) type).getElements().stream().map(subType -> this.generateAssertExpressions(subType, exp)).collect(Collectors.toList());

            return Collections.singletonList(createUnionCheck(unionElements));
        } else if (type instanceof IntersectionType) {
            return ((IntersectionType) type)
                    .getElements()
                    .stream()
                    .map(subType -> this.generateAssertExpressions(subType, exp))
                    .reduce(new ArrayList<>(), Util::reduceList);
        }

        if (type instanceof BooleanLiteral) {
            boolean value = ((BooleanLiteral) type).getValue();
            return Collections.singletonList(
                    new TypeCheck(binary(exp, Operator.EQUAL_EQUAL_EQUAL, bool(value)), Boolean.toString(value))
            );
        } else if (type instanceof NumberLiteral) {
            double value = ((NumberLiteral) type).getValue();
            return Collections.singletonList(
                    new TypeCheck(binary(exp, Operator.EQUAL_EQUAL_EQUAL, number(value)), Util.toPrettyNumber(value))
            );
        } else if (type instanceof StringLiteral) {
            String value = ((StringLiteral) type).getText();
            return Collections.singletonList(
                    new TypeCheck(binary(exp, Operator.EQUAL_EQUAL_EQUAL, string(value)), "\"" + value + "\"")
            );
        }

        if (type instanceof SimpleType) {
            if (((SimpleType) type).getKind() == SimpleTypeKind.Any) {
                return Collections.singletonList(
                        new TypeCheck(bool(true), "[any]")
                );
            }
            String typeof = getTypeOf((SimpleType) type);

            return getTypeOfCheck(exp, typeof);
        } else if (type instanceof InterfaceType && isEmptyInterface((InterfaceType) type)) {
            return Collections.singletonList(
                    new TypeCheck(bool(true), "[any]")
            );
        } else if (type instanceof ReferenceType && "Array".equals(typeNames.get(((ReferenceType) type).getTarget()))) {
            // TODO: Check the index type:
            BinaryExpression check = binary(string("[object Array]"), Operator.EQUAL_EQUAL_EQUAL, methodCall(member(member(identifier("Object"), "prototype"), "toString"), "call", exp));

            return Arrays.asList(expectNotNull(exp), new TypeCheck(check, "Array"));
        } else if ("Date".equals(typeNames.get(type))) {
            BinaryExpression check = binary(member(exp, "__proto__"), Operator.EQUAL_EQUAL_EQUAL, member(identifier("Date"), "prototype"));

            return Arrays.asList(expectNotNull(exp), new TypeCheck(check, "Date"));
        } else if ("Function".equals(typeNames.get(type))) {
            return getTypeOfCheck(exp, "function");
        } else if (nativeTypes.contains(type)) {
            throw new RuntimeException();
        } else if (type instanceof InterfaceType || type instanceof GenericType || type instanceof ReferenceType) {

            boolean hasFunctions;
            if (type instanceof ReferenceType) {
                type = ((ReferenceType) type).getTarget();
                assert type instanceof GenericType;
            }

            if (type instanceof InterfaceType) {
                hasFunctions = !((InterfaceType) type).getDeclaredCallSignatures().isEmpty();
                hasFunctions |= !((InterfaceType) type).getDeclaredConstructSignatures().isEmpty();
            } else {
                hasFunctions = !((GenericType) type).getDeclaredCallSignatures().isEmpty();
                hasFunctions |= !((GenericType) type).getDeclaredConstructSignatures().isEmpty();
            }

            if (hasFunctions) {
                return getTypeOfCheck(exp, "function");
            } else {
                return Collections.singletonList(
                        createUnionCheck(getTypeOfCheck(exp, "function"), getTypeOfCheck(exp, "object"))
                );
            }
        } else if (type instanceof TypeParameterType) {
            TypeParameterType parameter = (TypeParameterType) type;
            assert parameter.getTarget() == null;

            if (parameterMap.containsKey(type)) {
                return generateAssertExpressions(parameterMap.get(type), exp);
            }

            List<TypeCheck> checks = new ArrayList<>(generateAssertExpressions(parameter.getConstraint(), exp));

            String markerField = typeParameterIndexer.getMarkerField(parameter);
            checks.add(new TypeCheck(
                    binary(member(exp, markerField), Operator.EQUAL_EQUAL_EQUAL, bool(true)),
                    "a marker i placed (." + markerField + ") to be present, because this is a generic type, it wasn't! "
            ));

            return checks;
        } else {
            throw new RuntimeException("Unhandled type: " + type.getClass());
        }
    }

    @SafeVarargs
    private final TypeCheck createUnionCheck(List<TypeCheck>... checks) {
        List<List<TypeCheck>> checksList = Arrays.asList(checks);
        return createUnionCheck(checksList);
    }

    private TypeCheck createUnionCheck(List<List<TypeCheck>> checksLists) {
        assert !checksLists.isEmpty();
        if (checksLists.size() == 1) {
            return createIntersection(checksLists.iterator().next());
        }

        List<TypeCheck> checks = checksLists.stream().map(this::createIntersection).collect(Collectors.toList());

        StringBuilder expected = new StringBuilder("(");
        for (int i = 0; i < checks.size(); i++) {
            expected.append(checks.get(i).expected);
            if (i != checks.size() - 1) {
                expected.append(" or ");
            }
        }
        expected.append(")");

        Expression check = ASTUtil.or(checks.stream().map(TypeCheck::getExpression).collect(Collectors.toList()));

        return new TypeCheck(check, expected.toString());
    }

    private TypeCheck createIntersection(List<TypeCheck> checks) {
        assert !checks.isEmpty();
        if (checks.size() == 1) {
            return checks.iterator().next();
        }
        StringBuilder expected = new StringBuilder("(");
        for (int i = 0; i < checks.size(); i++) {
            expected.append(checks.get(i).expected);
            if (i != checks.size() - 1) {
                expected.append(" and ");
            }
        }
        expected.append(")");

        Expression check = ASTUtil.and(checks.stream().map(TypeCheck::getExpression).collect(Collectors.toList()));

        return new TypeCheck(check, expected.toString());
    }

    private List<TypeCheck> getTypeOfCheck(Expression exp, String typeof) {
        Expression check = binary(unary(Operator.TYPEOF, exp), Operator.EQUAL_EQUAL_EQUAL, string(typeof));
        return Collections.singletonList(new TypeCheck(check, typeof));
    }

    private String getTypeOf(SimpleType type) {
        switch (type.getKind()) {
            case String:
                return "string";
            case Number:
                return "number";
            case Boolean:
                return "boolean";
            case Undefined:
            case Void:
                return "undefined";
            default:
                throw new RuntimeException(type.getKind().toString());
        }
    }

    private TypeCheck expectNotNull(Expression exp) {
        return new TypeCheck(
                binary(
                        binary(exp, Operator.NOT_EQUAL_EQUAL, nullLiteral()),
                        Operator.AND,
                        binary(unary(Operator.TYPEOF, exp), Operator.NOT_EQUAL_EQUAL, string("undefined"))
                ),
                "a non null value"
        );
    }

    private boolean isEmptyInterface(InterfaceType type) {
        return type.getDeclaredProperties().isEmpty() &&
                type.getBaseTypes().isEmpty() &&
                type.getTypeParameters().isEmpty() &&
                type.getDeclaredCallSignatures().isEmpty() &&
                type.getDeclaredConstructSignatures().isEmpty() &&
                type.getDeclaredStringIndexType() == null &&
                type.getDeclaredNumberIndexType() == null;
    }
}
