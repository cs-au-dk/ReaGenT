package dk.webbies.tajscheck.buildprogram;

import dk.au.cs.casa.typescript.types.*;
import dk.au.cs.casa.typescript.types.BooleanLiteral;
import dk.au.cs.casa.typescript.types.NumberLiteral;
import dk.au.cs.casa.typescript.types.StringLiteral;
import dk.webbies.tajscheck.ParameterMap;
import dk.webbies.tajscheck.TypesUtil;
import dk.webbies.tajscheck.paser.AST.*;
import dk.webbies.tajscheck.testcreator.test.check.Check;
import dk.webbies.tajscheck.testcreator.test.check.CheckToExpression;
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
    private ParameterMap parameterMap;

    public CheckType(Set<Type> nativeTypes, Map<Type, String> typeNames, TestProgramBuilder.TypeParameterIndexer typeParameterIndexer, ParameterMap parameterMap) {
        this.nativeTypes = nativeTypes;
        this.typeNames = typeNames;
        this.typeParameterIndexer = typeParameterIndexer;
        this.parameterMap = parameterMap;
    }

    public Statement checkResultingType(Type type, Expression exp, String path) {
        List<TypeCheck> typeChecks = type.accept(new CreateTypeCheckVisitor(nativeTypes, typeParameterIndexer, typeNames), parameterMap);

        return block(
                typeChecks.stream().map(check -> {
                    // assert(cond, path, expected, actual)
                    Expression checkExpression = check.getCheck().accept(new CheckToExpression(), exp);
                    CallExpression assertCall = call(identifier("assert"), checkExpression, string(path), string(check.getExpected()), exp);
                    return ifThen(unary(Operator.NOT, assertCall), Return());
                }).collect(Collectors.toList())
        );
    }

    private static final class TypeCheck {
        private final Check check;
        private final String expected;

        private TypeCheck(Check check, String expected) {
            this.check = check;
            this.expected = expected;
        }

        public String getExpected() {
            return expected;
        }

        public Check getCheck() {
            return check;
        }
    }

    /*private List<TypeCheck> generateAssertExpressionsOld(Type type, Expression exp) {
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
            List<List<TypeCheck>> unionElements = ((UnionType) type).getElements().stream().map(subType -> this.generateAssertExpressionsOld(subType, exp)).collect(Collectors.toList());

            return Collections.singletonList(createUnionCheck(unionElements));
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

        if (nativeTypes.contains(type)) {
            throw new RuntimeException();
        } else if (type instanceof TypeParameterType) {
            TypeParameterType parameter = (TypeParameterType) type;
            assert parameter.getTarget() == null;

            if (parameterMap.containsKey(type)) {
                return generateAssertExpressionsOld(parameterMap.get(type), exp);
            }

            List<TypeCheck> checks = new ArrayList<>(generateAssertExpressionsOld(parameter.getConstraint(), exp));

            String markerField = typeParameterIndexer.getMarkerField(parameter);
            checks.add(new TypeCheck(
                    binary(member(exp, markerField), Operator.EQUAL_EQUAL_EQUAL, bool(true)),
                    "a marker i placed (." + markerField + ") to be present, because this is a generic type, it wasn't! "
            ));

            return checks;
        } else {
            throw new RuntimeException("Unhandled type: " + type.getClass());
        }
    }*/


    private static final class CreateTypeCheckVisitor implements TypeVisitorWithArgument<List<TypeCheck>, ParameterMap> {
        private final Set<Type> nativeTypes;
        private final TestProgramBuilder.TypeParameterIndexer typeParameterIndexer;
        private final Map<Type, String> typeNames;

        private CreateTypeCheckVisitor(Set<Type> nativeTypes, TestProgramBuilder.TypeParameterIndexer typeParameterIndexer, Map<Type, String> typeNames) {
            this.nativeTypes = nativeTypes;
            this.typeParameterIndexer = typeParameterIndexer;
            this.typeNames = typeNames;
        }

        @Override
        public List<TypeCheck> visit(AnonymousType t, ParameterMap parameterMap) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(ClassType t, ParameterMap parameterMap) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(GenericType t, ParameterMap parameterMap) {
            if (nativeTypes.contains(t)) {
                if ("RegExp".equals(typeNames.get(t))) {
                    return Arrays.asList(
                            expectNotNull(),
                            new TypeCheck(
                                    Check.instanceOf(identifier("RegExp")),
                                    "RegExp"
                            )
                    );
                }
                throw new RuntimeException();
            }
            assert t.getTypeParameters().equals(t.getTypeArguments());
            return t.toInterface().accept(this, parameterMap);
        }

        @Override
        public List<TypeCheck> visit(InterfaceType t, ParameterMap parameterMap) {
            if (TypesUtil.isEmptyInterface(t)) {
                return Collections.singletonList(new TypeCheck(Check.trueCheck(), "[any]"));
            }
            if ("Date".equals(typeNames.get(t))) {
                return Arrays.asList(expectNotNull(), new TypeCheck(Check.instanceOf(identifier("Date")), "Date"));
            }
            if ("Function".equals(typeNames.get(t))) {
                return Collections.singletonList(new TypeCheck(Check.typeOf("function"), "function"));
            }

            if (nativeTypes.contains(t)) {
                throw new RuntimeException();
            }

            if (!t.getDeclaredCallSignatures().isEmpty() || !t.getDeclaredConstructSignatures().isEmpty()) {
                return Collections.singletonList(
                        new TypeCheck(
                                Check.typeOf("function"),
                                "function"
                        )
                );
            } else {
                return Collections.singletonList(
                        new TypeCheck(
                                Check.or(
                                        Check.typeOf("function"),
                                        Check.typeOf("object")
                                ),
                                "function or object"
                        )
                );
            }
        }

        @Override
        public List<TypeCheck> visit(ReferenceType t, ParameterMap parameterMap) {
            if ("Array".equals(typeNames.get(t.getTarget()))) {
                // TODO: Check the index type:
                return Arrays.asList(expectNotNull(), new TypeCheck(Check.instanceOf(identifier("Array")), "Array"));
            }

            if (nativeTypes.contains(t)) {
                throw new RuntimeException();
            }
            return t.getTarget().accept(this, parameterMap.append(TypesUtil.generateParameterMap(t)));
        }

        @Override
        public List<TypeCheck> visit(SimpleType t, ParameterMap parameterMap) {
            if (t.getKind() == SimpleTypeKind.Any) {
                return Collections.singletonList(
                        new TypeCheck(Check.trueCheck(), "[any]")
                );
            }
            String typeOf = getTypeOf(t);
            return Collections.singletonList(new TypeCheck(Check.typeOf(typeOf), typeOf));
        }

        @Override
        public List<TypeCheck> visit(TupleType t, ParameterMap parameterMap) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(UnionType t, ParameterMap parameterMap) {
            return Collections.singletonList(
                    createUnionCheck(t.getElements().stream().map(subType -> subType.accept(this, parameterMap)).collect(Collectors.toList()))
            );
        }

        @Override
        public List<TypeCheck> visit(UnresolvedType t, ParameterMap parameterMap) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(TypeParameterType parameter, ParameterMap parameterMap) {
            assert parameter.getTarget() == null;

            if (parameterMap.containsKey(parameter)) {
                if (!TypesUtil.findRecursiveDefinition(parameter, parameterMap, typeParameterIndexer).isEmpty()) {
                    List<Type> constraints = TypesUtil.findRecursiveDefinition(parameter, parameterMap, typeParameterIndexer);
                    IntersectionType constraintsIntersection = new IntersectionType();
                    constraintsIntersection.setElements(constraints);
                    return constraintsIntersection.accept(this, parameterMap);
                }
                if (Thread.currentThread().getStackTrace().length > 1000) {
                    System.err.println("");
                }
                return parameterMap.get(parameter).accept(this, parameterMap);
            }

            List<TypeCheck> checks = new ArrayList<>(parameter.getConstraint().accept(this, parameterMap));

            String markerField = typeParameterIndexer.getMarkerField(parameter);

            checks.add(expectNotNull());
            checks.add(new TypeCheck(
                    Check.field(markerField, Check.equalTo(bool(true))),
                    "a marker i placed (." + markerField + ") to be present, because this is a generic type, it wasn't! "
            ));

            return checks;
        }

        @Override
        public List<TypeCheck> visit(SymbolType t, ParameterMap parameterMap) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(StringLiteral t, ParameterMap parameterMap) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(BooleanLiteral t, ParameterMap parameterMap) {
            return Collections.singletonList(
                    new TypeCheck(Check.equalTo(bool(t.getValue())), Boolean.toString(t.getValue()))
            );
        }

        @Override
        public List<TypeCheck> visit(NumberLiteral t, ParameterMap parameterMap) {
            throw new RuntimeException();
        }

        @Override
        public List<TypeCheck> visit(IntersectionType t, ParameterMap parameterMap) {
            return t.getElements()
                    .stream()
                    .map(subType -> subType.accept(this, parameterMap))
                    .reduce(new ArrayList<>(), Util::reduceList);
        }
    }


    @SafeVarargs
    private static final TypeCheck createUnionCheck(List<TypeCheck>... checks) {
        List<List<TypeCheck>> checksList = Arrays.asList(checks);
        return createUnionCheck(checksList);
    }

    private static TypeCheck createUnionCheck(List<List<TypeCheck>> checksLists) {
        assert !checksLists.isEmpty();
        if (checksLists.size() == 1) {
            return createIntersection(checksLists.iterator().next());
        }

        List<TypeCheck> checks = checksLists.stream().map(CheckType::createIntersection).collect(Collectors.toList());

        StringBuilder expected = new StringBuilder("(");
        for (int i = 0; i < checks.size(); i++) {
            expected.append(checks.get(i).expected);
            if (i != checks.size() - 1) {
                expected.append(" or ");
            }
        }
        expected.append(")");

        Check check = Check.or(checks.stream().map(TypeCheck::getCheck).collect(Collectors.toList()));

        return new TypeCheck(check, expected.toString());
    }

    private static TypeCheck createIntersection(List<TypeCheck> checks) {
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

        Check check = Check.and(checks.stream().map(TypeCheck::getCheck).collect(Collectors.toList()));

        return new TypeCheck(check, expected.toString());
    }

    private static String getTypeOf(SimpleType type) {
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

    private static TypeCheck expectNotNull() {
        return new TypeCheck(
                Check.and(
                    Check.not(Check.typeOf("undefined")),
                    Check.not(Check.equalTo(nullLiteral()))
                ),
                "a non null value"
        );
    }
}
