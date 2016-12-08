package dk.webbies.tajscheck.buildprogram;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.au.cs.casa.typescript.types.BooleanLiteral;
import dk.au.cs.casa.typescript.types.NumberLiteral;
import dk.au.cs.casa.typescript.types.StringLiteral;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.TypeContext;
import dk.webbies.tajscheck.TypeWithParameters;
import dk.webbies.tajscheck.TypesUtil;
import dk.webbies.tajscheck.paser.AST.*;
import dk.webbies.tajscheck.paser.AstBuilder;
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
    int createProducedValueVariable(Type type, TypeContext typeContext) {
        int index = valueCounter++;
        valueVariableDeclarationList.add(variable(VALUE_VARIABLE_PREFIX + index, identifier(VARIABLE_NO_VALUE)));

        putProducedValueIndex(index, type, typeContext);
        return index;
    }

    public Collection<Statement> getValueVariableDeclarationList() {
        return valueVariableDeclarationList;
    }

    private void putProducedValueIndex(int index, Type type, TypeContext typeContext) {
        valueLocations.put(new TypeWithParameters(type, typeContext), index);
        if (type instanceof InterfaceType) {
            List<Type> baseTypes = ((InterfaceType) type).getBaseTypes();
            baseTypes.forEach(baseType -> putProducedValueIndex(index, baseType, typeContext));
        } else if (type instanceof ReferenceType) {
            putProducedValueIndex(index, ((ReferenceType) type).getTarget(), TypesUtil.generateParameterMap((ReferenceType) type, typeContext));
        } else if (type instanceof GenericType) {
            putProducedValueIndex(index, ((GenericType) type).toInterface(), typeContext);
        } else if (type instanceof ClassType) {
            List<Type> baseTypes = ((ClassType) type).getBaseTypes();
            valueLocations.put(new TypeWithParameters(type, typeContext.withClass((ClassType) type)), index);
            baseTypes.forEach(baseType -> putProducedValueIndex(index, baseType, typeContext.withClass((ClassType) type)));
        } else if (type instanceof ClassInstanceType) {
            putProducedValueIndex(index, ((ClassType) ((ClassInstanceType) type).getClassType()).getInstanceType(), typeContext);
        } else if (type instanceof ThisType) {
            putProducedValueIndex(index, typeContext.getClassType().getInstanceType(), typeContext);
        } else if (type instanceof TypeParameterType || type instanceof SimpleType || type instanceof NumberLiteral || type instanceof StringLiteral || type instanceof BooleanLiteral || type instanceof UnionType || type instanceof TupleType || type instanceof NeverType || type instanceof SymbolType) {
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
            return TypesUtil.classToInterface(t).accept(this, typeContext);
        }

        @Override
        public Statement visit(GenericType type, TypeContext typeContext) {
            assert type.getTypeParameters().equals(type.getTypeArguments());
            if (nativeTypes.contains(type)) {
                return constructTypeFromName(typeNames.get(type), typeContext);
            }

            return type.toInterface().accept(this, typeContext);
        }

        @Override
        public Statement visit(InterfaceType type, TypeContext typeContext) {
            if (nativeTypes.contains(type) && !TypesUtil.isEmptyInterface(type)) {
                return constructTypeFromName(typeNames.get(type), typeContext);
            }

            Pair<InterfaceType, TypeContext> pair = constructSyntheticInterfaceWithBaseTypes(type);
            InterfaceType inter = pair.getLeft();
            typeContext = typeContext.append(pair.getRight());
            assert inter.getBaseTypes().isEmpty();

            int numberOfSignatures = type.getDeclaredCallSignatures().size() + inter.getDeclaredConstructSignatures().size();

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

            InterfaceType target;
            if (type.getTarget() instanceof GenericType) {
                target = ((GenericType) type.getTarget()).toInterface();
            } else {
                assert type.getTarget() instanceof ClassInstanceType;
                target = ((ClassType) ((ClassInstanceType) type.getTarget()).getClassType()).getInstanceType();
            }
            return constructNewInstanceOfType(target, TypesUtil.generateParameterMap(type, typeContext));
        }

        @Override
        public Statement visit(SimpleType simple, TypeContext typeContext) {
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
            throw new RuntimeException();
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

                return constructNewInstanceOfType(typeContext.get(type), typeContext);
            }
            String markerField = typeParameterIndexer.getMarkerField(type);
            return block(
                    variable("result", call(function(constructNewInstanceOfType(type.getConstraint(), typeContext)))),
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
            throw new RuntimeException();
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
            return typeContext.getClassType().getInstanceType().accept(this, typeContext);
        }
    }

    private FunctionExpression createFunction(InterfaceType inter, TypeContext typeContext) {
        List<Signature> signatures = Util.concat(inter.getDeclaredCallSignatures(), inter.getDeclaredConstructSignatures());

        assert signatures.size() > 0;

        int maxArgs = signatures.stream().map(Signature::getParameters).map(List::size).reduce(0, Math::max);

        List<String> args = new ArrayList<>();
        for (int i = 0; i < maxArgs; i++) {
            args.add("arg" + i);
        }

        CheckType typeChecker = new CheckType(nativeTypes, typeNames, typeParameterIndexer, typeContext);

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
                    saveArgsAndReturnValue(signature, typeContext)
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
                                    throwStatement(newCall(identifier(RUNTIME_ERROR_NAME), binary(string("Could not find correct overload for function: " + interName + " results: "), Operator.PLUS, methodCall(identifier("foundSignatures"), "toString"))))
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

    private BlockStatement saveArgsAndReturnValue(Signature signature, TypeContext typeContext) {
        List<Statement> saveArgumentValues = Util.withIndex(
                signature.getParameters().stream().map(par -> createProducedValueVariable(par.getType(), typeContext)),
                (valueIndex, argIndex) -> {
                    return statement(binary(identifier(VALUE_VARIABLE_PREFIX + valueIndex), Operator.EQUAL, identifier("arg" + argIndex)));
                }
        ).collect(Collectors.toList());

        return block(
                block(saveArgumentValues),
                Return(constructType(signature.getResolvedReturnType(), typeContext))
        );
    }


    private Statement constructTypeFromName(String name, TypeContext typeContext) {
        if (name == null) {
            throw new NullPointerException();
        }
        switch (name) {
            case "Object":
                return constructNewInstanceOfType(SpecReader.makeEmptySyntheticInterfaceType(), typeContext);
            case "Number":
                return constructNewInstanceOfType(new SimpleType(SimpleTypeKind.Number), typeContext);
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
                Expression constructString = call(function(constructNewInstanceOfType(new SimpleType(SimpleTypeKind.String), new TypeContext())));
                return Return(newCall(identifier("RegExp"), constructString));
            case "String":
                return constructNewInstanceOfType(new SimpleType(SimpleTypeKind.String), typeContext);
            case "HTMLCanvasElement":
                return AstBuilder.fromString("return document.createElement('canvas')");
            case "HTMLVideoElement":
                return AstBuilder.fromString("return document.createElement('video')");
            case "HTMLImageElement":
                return AstBuilder.fromString("return document.createElement('img')");
            case "Uint32Array":
                return AstBuilder.fromString("return new Uint32Array()");
            case "Float32Array":
                return AstBuilder.fromString("return new Float32Array()");
            case "Uint16Array":
                return AstBuilder.fromString("return new Uint16Array()");
            case "WebGLRenderingContext":
                return AstBuilder.fromString("return document.createElement(\"canvas\").getContext(\"webgl\")");
            case "WebGLTexture":
                return AstBuilder.fromString("return document.createElement(\"canvas\").getContext(\"webgl\").createTexture()");
            case "WebGLFramebuffer":
                return AstBuilder.fromString("return document.createElement(\"canvas\").getContext(\"webgl\").createFramebuffer()");
            case "WebGLRenderbuffer":
                return AstBuilder.fromString("return document.createElement(\"canvas\").getContext(\"webgl\").createRenderbuffer()");
            case "CanvasRenderingContext2D":
                return AstBuilder.fromString("return document.createElement(\"canvas\").getContext(\"2d\")");
            case "MouseEvent":
                return AstBuilder.fromString("return new MouseEvent(null)");
            case "Event":
                return AstBuilder.fromString("return new Event(null)");
            case "WebGLProgram":
                return AstBuilder.fromString("return document.createElement(\"canvas\").getContext(\"webgl\").createProgram()");
            case "WebGLBuffer":
                return AstBuilder.fromString("return document.createElement(\"canvas\").getContext(\"webgl\").createBuffer()");
            case "ArrayBuffer":
                return AstBuilder.fromString("return new ArrayBuffer()");
            case "ImageData":
                return AstBuilder.fromString("return new ImageData(10, 10)");
            case "TouchEvent":
                return AstBuilder.fromString("return new TouchEvent(null)");
            case "WebGLContextEvent":
                return AstBuilder.fromString("return new WebGLContextEvent(null)");
            case "PointerEvent":
                return AstBuilder.fromString("return new PointerEvent(\"pointermove\")");
            case "CanvasGradient":
                return AstBuilder.fromString("return document.createElement(\"canvas\").getContext(\"2d\").createLinearGradient()");
            case "HTMLElement":
                return AstBuilder.fromString("return document.createElement('div')");
            default:
                throw new RuntimeException("Unknown: " + name);
        }
    }


    private Statement constructNewInstanceOfType(Type type, TypeContext typeContext) {
        return type.accept(new ConstructNewInstanceVisitor(), typeContext);
    }

    private Pair<InterfaceType, TypeContext> constructSyntheticInterfaceWithBaseTypes(InterfaceType inter) {
        if (inter.getBaseTypes().isEmpty()) {
            return new Pair<>(inter, new TypeContext());
        }
//        assert inter.getTypeParameters().isEmpty(); // This should only happen when constructed from a generic/reference type, and in that case we have handled the TypeParameters.
        Map<TypeParameterType, Type> newParameters = new TypeContext().getMap();
        InterfaceType result = SpecReader.makeEmptySyntheticInterfaceType();

        result.getDeclaredCallSignatures().addAll(inter.getDeclaredCallSignatures());
        result.getDeclaredConstructSignatures().addAll(inter.getDeclaredConstructSignatures());
        result.setDeclaredNumberIndexType(inter.getDeclaredNumberIndexType());
        result.setDeclaredStringIndexType(inter.getDeclaredStringIndexType());

        typeNames.put(result, typeNames.get(inter));
        inter.getBaseTypes().forEach(subType -> {
            if (subType instanceof ReferenceType) {
                newParameters.putAll(TypesUtil.generateParameterMap((ReferenceType) subType).getMap());
                subType = ((ReferenceType) subType).getTarget();
            }
            if (subType instanceof GenericType) {
                subType = ((GenericType) subType).toInterface();
            }
            Pair<InterfaceType, TypeContext> pair = constructSyntheticInterfaceWithBaseTypes((InterfaceType) subType);
            newParameters.putAll(pair.getRight().getMap());
            InterfaceType type = pair.getLeft();
            result.getDeclaredCallSignatures().addAll((type.getDeclaredCallSignatures()));
            result.getDeclaredConstructSignatures().addAll(type.getDeclaredConstructSignatures());
            if (result.getDeclaredNumberIndexType() == null) {
                result.setDeclaredNumberIndexType(type.getDeclaredNumberIndexType());
            }
            if (result.getDeclaredStringIndexType() == null) {
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
        return new Pair<>(result, new TypeContext().append(newParameters));
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
        TypeWithParameters typeWithParameters = typeIndexes.inverse().get(index);
        return constructType(typeWithParameters.getType(), typeWithParameters.getTypeContext());
    }

    private int getTypeIndex(Type type, TypeContext typeContext) {
//        TypeContext typeContext = TypesUtil.filterParameterMap(unFilteredParameterMap, type);

        TypeWithParameters key = new TypeWithParameters(type, typeContext);
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
            addGetTypeFunction(type.getType(), type.getTypeContext());
        }
    }

    private int addGetTypeFunction(Type type, TypeContext typeContext) {
        int value = typeIndexes.get(new TypeWithParameters(type, typeContext));

        Collection<Integer> values = valueLocations.keySet().stream()
                .filter(candidate -> type.equals(candidate.getType()) && typeContext.equals(candidate.getTypeContext()))
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
