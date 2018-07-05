package dk.webbies.tajscheck.parsespec.flow;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class FlowParser {
    private final SpecReader emptySpec;
    private final Map<String, Type> namedTypes = new HashMap<>();
    private final List<SpecReader.NamedType> ambientTypes = new ArrayList<>();
    private final Map<String, List<Pair<Pair<Integer, Integer>, Type>>> typeParameters = new HashMap<>();
    private final TypeNameCreator typeNameCreator;

    private FlowParser(ParseDeclaration.Environment environment, JsonArray body) {
        this.emptySpec = ParseDeclaration.getTypeSpecification(environment, Collections.emptyList());
        for (SpecReader.NamedType namedType : emptySpec.getNamedTypes()) {
            namedTypes.put(String.join(".", namedType.qName), namedType.type);
        }

        this.typeNameCreator = new TypeNameCreator(this, body);
        this.namedTypes.putAll(typeNameCreator.getTypeNames());
    }

    void registerTypeParameter(String name, Pair<Integer, Integer> range, Type type) {
        if (!typeParameters.containsKey(name)) {
            typeParameters.put(name, new ArrayList<>());
        }
        typeParameters.get(name).add(new Pair<>(range, type));
    }

    public static SpecReader parse(ParseDeclaration.Environment environment, List<String> declarationFiles) {
        final String astJSON = declarationFiles.isEmpty() ? "" : parseDeclaration(declarationFiles);
        JsonArray body = astJSON.isEmpty() ? new JsonArray() : new JsonParser().parse(astJSON).getAsJsonObject().get("body").getAsJsonArray();

        FlowParser flowParser = new FlowParser(environment, body);

        if (declarationFiles.isEmpty()) {
            InterfaceType global = SpecReader.makeEmptySyntheticInterfaceType();
            flowParser.emptySpec.getGlobal().getDeclaredProperties().forEach((name, type) -> {
                if (!global.getDeclaredProperties().containsKey(name)) {
                    global.getDeclaredProperties().put(name, type);
                }
            });
            return new SpecReader(global, flowParser.emptySpec.getNamedTypes(), new ArrayList<>(), new HashMap<>());
        }

        List<SpecReader.NamedType> namedTypes = new ArrayList<>(flowParser.emptySpec.getNamedTypes());
        Map<String, Type> globalProperties = new HashMap<>();

        for (JsonElement rawStatement : body) {
            JsonObject statement = rawStatement.getAsJsonObject();
            switch (statement.get("type").getAsString()) {
                case "DeclareModule":
                    flowParser.parseModule(flowParser.ambientTypes, globalProperties, statement);
                    break;
                case "ClassDeclaration":
                case "DeclareClass": {
                    Type classType = flowParser.parseClass(statement, "");
                    String name = statement.get("id").getAsJsonObject().get("name").getAsString();
                    globalProperties.put(name, classType);
                    break;
                }
                case "DeclareFunction":
                case "DeclareVariable": {
                    JsonObject varId = statement.get("id").getAsJsonObject();
                    String varName = varId.get("name").getAsString();
                    Type varType = flowParser.parseType(varId.get("typeAnnotation").getAsJsonObject(), "");
                    globalProperties.put(varName, varType);
                    break;
                }
                case "DeclareTypeAlias":
                case "TypeAlias":
                case "DeclareInterface":
                case "InterfaceDeclaration":
                    break; // Just declares a name, which has already been handled.
                default:
                    throw new RuntimeException("Unknown type of statement: " + statement.get("type").getAsString());
            }
        }

        InterfaceType global = SpecReader.makeEmptySyntheticInterfaceType();
        globalProperties.forEach((name, type) -> global.getDeclaredProperties().put(name, type instanceof DelayedType ? ((DelayedType) type).getType() : type));

        flowParser.emptySpec.getGlobal().getDeclaredProperties().forEach((name, type) -> {
            if (!global.getDeclaredProperties().containsKey(name)) {
                global.getDeclaredProperties().put(name, type);
            }
        });

        return new SpecReader(global, expandDelayed(namedTypes), expandDelayed(flowParser.ambientTypes), new HashMap<>());

    }

    private static String parseDeclaration(List<String> declarationFiles) {
        try {
            return Util.runNodeScript("resources/parse-flow.js " + String.join(" ", declarationFiles));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private static List<SpecReader.NamedType> expandDelayed(List<SpecReader.NamedType> namedTypes) {
        return namedTypes.stream().map(named -> {
            if (named.type instanceof DelayedType) {
                return new SpecReader.NamedType(((DelayedType) named.type).getType(), named.qName);
            } else {
                return named;
            }
        }).collect(Collectors.toList());
    }

    private void parseModule(List<SpecReader.NamedType> ambientTypes, Map<String, Type> globalProperties, JsonObject statement) {
        JsonObject id = statement.get("id").getAsJsonObject();

        String name = getName(id);

        assert statement.get("body").getAsJsonObject().get("type").getAsString().equals("BlockStatement");

        List<JsonObject> moduleStatements = Lists.newArrayList(statement.get("body").getAsJsonObject().get("body").getAsJsonArray()).stream().map(JsonObject.class::cast).collect(Collectors.toList());

        final Type type;
        if (moduleStatements.stream().anyMatch(stmt -> stmt.get("type").getAsString().equals("DeclareModuleExports"))) {
            //noinspection ConstantConditions
            JsonObject exports = moduleStatements.stream().filter(stmt -> stmt.get("type").getAsString().equals("DeclareModuleExports")).findFirst().get();
            type = this.parseType(exports.get("typeAnnotation").getAsJsonObject(), name);
        } else {
            Map<String, Type> declaredTypes = new HashMap<>();
            for (JsonObject moduleStatementRaw : moduleStatements) {
                parseModuleStatement(name, declaredTypes, moduleStatementRaw.getAsJsonObject());
            }

            InterfaceType interfaceType = SpecReader.makeEmptySyntheticInterfaceType();
            declaredTypes.forEach(interfaceType.getDeclaredProperties()::put);
            type = interfaceType;
        }

        if (name.startsWith("'") || name.startsWith("\"")) {
            name = name.substring(1, name.length() - 1);
//            assert !name.contains(".");
            ambientTypes.add(new SpecReader.NamedType(type, Collections.singletonList(name)));
        } else {
            globalProperties.put(name, type);
        }
    }

    static String getName(JsonObject id) {
        if (id.get("type").getAsString().equals("Literal")) {
            return id.get("raw").getAsString();
        } else if (id.get("type").getAsString().equals("QualifiedTypeIdentifier")) {
            return getName(id.get("qualification").getAsJsonObject()) + "." + getName(id.get("id").getAsJsonObject());
        } else {
            assert id.get("type").getAsString().equals("Identifier");
            return id.get("name").getAsString();
        }
    }
    static String getPropertyName(JsonObject id) {
        if (id.get("type").getAsString().equals("Literal")) {
            return id.get("value").getAsString();
        } else {
            assert id.get("type").getAsString().equals("Identifier");
            return id.get("name").getAsString();
        }
    }

    private void parseModuleStatement(String name, Map<String, Type> declaredTypes, JsonObject moduleStatement) {
        switch (moduleStatement.get("type").getAsString()) {
            case "DeclareClass":
                declaredTypes.put(
                        moduleStatement.get("id").getAsJsonObject().get("name").getAsString(),
                        this.parseType(moduleStatement, name, true)
                );
                break;
            case "DeclareExportDeclaration":  // For now, everything is exported.
                parseModuleStatement(name, declaredTypes, moduleStatement.get("declaration").getAsJsonObject());
                break;
            case "DeclareFunction":
            case "DeclareVariable": {
                JsonObject varId = moduleStatement.get("id").getAsJsonObject();
                String varName = varId.get("name").getAsString();
                Type varType = parseType(varId.get("typeAnnotation").getAsJsonObject(), name);
                declaredTypes.put(varName, varType);
                break;
            }
            case "InterfaceDeclaration":
            case "GenericTypeAnnotation":
            case "TypeAlias":
            case "DeclareTypeAlias":
            case "DeclareInterface":
                break; // Doesn't declare any actual value, just a named-type, which has already been handled.
            default:
                throw new RuntimeException(moduleStatement.get("type").getAsString());
        }
    }

    private final Map<Pair<JsonObject, Boolean>, DelayedType> parseTypeCache = new HashMap<>();
    DelayedType parseType(JsonObject typeJSON, String nameContext) {
        return parseType(typeJSON, nameContext, false);
    }

    DelayedType parseType(JsonObject typeJSON, String nameContext, boolean typeof) {
        Pair<JsonObject, Boolean> key = new Pair<>(typeJSON, typeof);
        if (parseTypeCache.containsKey(key)) {
            return parseTypeCache.get(key);
        }
        final DelayedType result = new DelayedType(() -> {
            switch (typeJSON.get("type").getAsString()) {
                case "TypeAnnotation":
                    assert !typeof;
                    return parseType(typeJSON.get("typeAnnotation").getAsJsonObject(), nameContext);
                case "FunctionTypeAnnotation":
                    return parseFunctionType(typeJSON, nameContext);
                case "StringTypeAnnotation":
                    return new SimpleType(SimpleTypeKind.String);
                case "NumberTypeAnnotation":
                    return new SimpleType(SimpleTypeKind.Number);
                case "VoidTypeAnnotation":
                    return new SimpleType(SimpleTypeKind.Void);
                case "MixedTypeAnnotation":
                case "AnyTypeAnnotation":
                case "ExistsTypeAnnotation": // Nothing better I can do, this is a declaration file.
                    return new SimpleType(SimpleTypeKind.Any);
                case "UnionTypeAnnotation":
                    return new UnionType(Lists.newArrayList(typeJSON.get("types").getAsJsonArray()).stream().map(JsonObject.class::cast).map(obj -> parseType(obj, nameContext)).collect(Collectors.toList()));
                case "ClassDeclaration":
                case "DeclareClass": {
                    if (typeof) {
                        return parseClass(typeJSON, nameContext);
                    } else {
                        Type classType = parseType(typeJSON, nameContext, true);
                        return new ClassInstanceType(classType);
                    }
                }
                case "GenericTypeAnnotation":{
                    List<Type> typeArguments = new ArrayList<>();
                    if (!typeJSON.get("typeParameters").isJsonNull()) {
                        JsonObject typeParameterJSON = typeJSON.get("typeParameters").getAsJsonObject();
                        assert typeJSON.get("typeParameters").getAsJsonObject().get("type").getAsString().equals("TypeParameterInstantiation");
                        Lists.newArrayList(typeParameterJSON.get("params").getAsJsonArray()).stream().map(json -> parseType(json.getAsJsonObject(), nameContext)).forEach(typeArguments::add);
                    }
                    String name = getName(typeJSON.get("id").getAsJsonObject());
                    if (utilityTypes.containsKey(name)) {
                        return utilityTypes.get(name).apply(this, typeArguments);
                    }

                    if (name.equals("this")) {
                        assert !typeof;
                        assert typeArguments.isEmpty();
                        Pair<Integer, Integer> range = parseRange(typeJSON);

                        List<Pair<JsonObject, String>> matchingClasses = typeNameCreator.getClassAndInterfaceDefinitions().stream().filter(classDef -> {
                            Pair<Integer, Integer> classDefRange = parseRange(classDef.getLeft());
                            return range.getLeft() >= classDefRange.getLeft() && range.getRight() <= classDefRange.getRight();
                        }).collect(Collectors.toList());

                        assert matchingClasses.size() == 1;

                        ThisType thisType = new ThisType();
                        Pair<JsonObject, String> matchingClassDef = matchingClasses.iterator().next();
                        thisType.setConstraint(parseType(matchingClassDef.getLeft(), matchingClassDef.getRight()));
                        return thisType;
                    }

                    final Type type;
                    if (typeof) {
                        if (this.emptySpec.getGlobal().getDeclaredProperties().containsKey(name)) {
                            type = this.emptySpec.getGlobal().getDeclaredProperties().get(name);
                        } else {
                            Type lookup = TypeNameCreator.lookUp(namedTypes, nameContext, name, typeParameters, typeJSON.get("range").getAsJsonArray());
                            Type instanceType = lookup instanceof DelayedType ? ((DelayedType) lookup).getType(): lookup;
                            if (instanceType instanceof ClassInstanceType) {
                                type = ((ClassInstanceType) instanceType).getClassType();
                            } else {
                                type = instanceType;
                            }
                        }
                    } else {
                        type = TypeNameCreator.lookUp(namedTypes, nameContext, name, typeParameters, typeJSON.get("range").getAsJsonArray());
                    }

                    if (type == null) {
                        throw new RuntimeException("Could not find: " + name + (typeof ? " (typeof)" : ""));
                    }

                    if (typeArguments.isEmpty()) {
                        return type;
                    }
                    ReferenceType refType = new ReferenceType();
                    refType.setTarget(type);
                    refType.setTypeArguments(typeArguments);
                    return refType;
                }
                case "TypeofTypeAnnotation":
                    return parseType(typeJSON.get("argument").getAsJsonObject(), nameContext, true);
                case "TypeParameter": {
                    Type bound = typeJSON.get("bound").isJsonNull() ? null : parseType(typeJSON.get("bound").getAsJsonObject(), nameContext);
//                    assert typeJSON.get("variance").isJsonNull(); // Doesn't matter for my purposes, so i just ignore variance
//                    assert typeJSON.get("default").isJsonNull(); // Default doesn't matter for my purposes.
                    TypeParameterType typeParameterType = new TypeParameterType();
                    typeParameterType.setConstraint(bound);
                    return typeParameterType;
                }
                case "NullLiteralTypeAnnotation":
                    return new SimpleType(SimpleTypeKind.Null);
                case "BooleanTypeAnnotation":
                    return new SimpleType(SimpleTypeKind.Boolean);
                case "EmptyTypeAnnotation":
                    return new SimpleType(SimpleTypeKind.Never);
                case "BooleanLiteralTypeAnnotation":
                    return new BooleanLiteral(typeJSON.get("value").getAsBoolean());
                case "NumberLiteralTypeAnnotation":
                    return new NumberLiteral(typeJSON.get("value").getAsNumber().doubleValue());
                case "StringLiteralTypeAnnotation":
                    return new StringLiteral(typeJSON.get("value").getAsString());
                case "NullableTypeAnnotation":
                    return new UnionType(Arrays.asList(new SimpleType(SimpleTypeKind.Null), new SimpleType(SimpleTypeKind.Undefined), parseType(typeJSON.get("typeAnnotation").getAsJsonObject(), nameContext)));
                case "ArrayTypeAnnotation": {
                    DelayedType indexType = parseType(typeJSON.get("elementType").getAsJsonObject(), nameContext);

                    Type array = namedTypes.get("Array");

                    ReferenceType resultType = new ReferenceType();
                    resultType.setTarget(array);
                    resultType.setTypeArguments(Collections.singletonList(indexType));

                    return resultType;
                }
                case "TupleTypeAnnotation": {
                    List<Type> types = Lists.newArrayList(typeJSON.get("types").getAsJsonArray()).stream().map(JsonElement::getAsJsonObject).map(json -> parseType(json, nameContext)).collect(Collectors.toList());
                    TupleType tupleType = new TupleType();
                    tupleType.setElementTypes(types);
                    return tupleType;
                }
                case "IntersectionTypeAnnotation": {
                    List<Type> types = Lists.newArrayList(typeJSON.get("types").getAsJsonArray()).stream().map(json -> parseType(json.getAsJsonObject(), nameContext)).collect(Collectors.toList());
                    IntersectionType intersectionType = new IntersectionType();
                    intersectionType.setElements(types);
                    return intersectionType;
                }
                case "ObjectTypeAnnotation":
                    InterfaceType interfaceType = SpecReader.makeEmptySyntheticInterfaceType();
                    assert typeJSON.get("exact") != null;
                    JsonArray indexers = typeJSON.get("indexers").getAsJsonArray();
                    for (JsonElement indexerJSONRaw : indexers) {
                        JsonObject indexerJSON = indexerJSONRaw.getAsJsonObject();
                        String keyType = indexerJSON.get("key").getAsJsonObject().get("type").getAsString();
                        assert keyType.equals("StringTypeAnnotation") || keyType.equals("NumberTypeAnnotation");

                        DelayedType indexerType = parseType(indexerJSON.get("value").getAsJsonObject(), nameContext);

                        if (keyType.equals("StringTypeAnnotation")) {
                            assert interfaceType.getDeclaredStringIndexType() == null;
                            interfaceType.setDeclaredStringIndexType(indexerType);
                        } else {
                            assert keyType.equals("NumberTypeAnnotation");
                            assert interfaceType.getDeclaredNumberIndexType() == null;
                            interfaceType.setDeclaredNumberIndexType(indexerType);
                        }
                    }

                    for (JsonElement callProperty : typeJSON.get("callProperties").getAsJsonArray()) {
                        assert callProperty.getAsJsonObject().get("type").getAsString().equals("ObjectTypeCallProperty");
                        assert !callProperty.getAsJsonObject().get("static").getAsBoolean();
                        InterfaceType callPropertyType = (InterfaceType) parseType(callProperty.getAsJsonObject().get("value").getAsJsonObject(), nameContext).getType();

                        assert !callPropertyType.getDeclaredCallSignatures().isEmpty();
                        interfaceType.getDeclaredCallSignatures().addAll(callPropertyType.getDeclaredCallSignatures());
                    }


                    assert typeJSON.get("internalSlots").getAsJsonArray().size() == 0;
                    Map<String, Type> properties = new HashMap<>();
                    for (JsonElement propertyRaw : typeJSON.get("properties").getAsJsonArray()) {
                        JsonObject propertyJSON = propertyRaw.getAsJsonObject();
                        assert propertyJSON.get("type").getAsString().equals("ObjectTypeProperty");
                        assert !propertyJSON.get("static").getAsBoolean();
                        assert propertyJSON.get("kind").getAsString().equals("init");
                        assert !propertyJSON.get("proto").getAsBoolean();
                        String name = getPropertyName(propertyJSON.get("key").getAsJsonObject());
                        Type type = parseType(propertyJSON.get("value").getAsJsonObject(), nameContext);
                        if (propertyJSON.get("optional").getAsBoolean()) {
                            type = new UnionType(Arrays.asList(type, new SimpleType(SimpleTypeKind.Undefined)));
                        }
                        properties.put(name, type);
                    }
                    interfaceType.getDeclaredProperties().putAll(properties);
                    return interfaceType;
                case "DeclareInterface":
                case "InterfaceDeclaration": {
                    String interfaceName = typeJSON.get("id").getAsJsonObject().get("name").getAsString(); // Good for debug.

                    List<Type> typeParameters = createTypeParameters(typeJSON, nameContext);

                    List<Type> baseTypes = parseBaseTypes(typeJSON, nameContext);

                    InterfaceType resultType = SpecReader.makeEmptySyntheticInterfaceType();
                    resultType.setTypeParameters(typeParameters);

                    resultType.getBaseTypes().addAll(baseTypes);

                    InterfaceType object = (InterfaceType) parseType(typeJSON.get("body").getAsJsonObject(), nameContext).getType();
                    resultType.getDeclaredProperties().putAll(object.getDeclaredProperties());
                    resultType.getDeclaredCallSignatures().addAll(object.getDeclaredCallSignatures());
                    resultType.getDeclaredConstructSignatures().addAll(object.getDeclaredConstructSignatures());
                    resultType.getReadonlyDeclarations().addAll(object.getReadonlyDeclarations());
                    resultType.setDeclaredStringIndexType(object.getDeclaredStringIndexType());
                    resultType.setDeclaredNumberIndexType(object.getDeclaredNumberIndexType());
                    return resultType;
                }
                default:
                    throw new RuntimeException("Unknown type: " + typeJSON.get("type").getAsString());
            }
        });
        parseTypeCache.put(key, result);
        return result;
    }

    private List<Type> parseBaseTypes(JsonObject typeJSON, String nameContext) {
        return parseBaseTypes(typeJSON, nameContext, "extends");
    }

    private List<Type> parseBaseTypes(JsonObject typeJSON, String nameContext, String propertyName) {
        final List<JsonElement> baseTypes;
        if (typeJSON.get(propertyName) != null) {
            baseTypes = Lists.newArrayList(typeJSON.get(propertyName).getAsJsonArray());
        } else {
            assert typeJSON.get("superClass") != null;
            if (typeJSON.get("superClass").isJsonNull()) {
                baseTypes = Collections.emptyList();
            } else {
                baseTypes = Collections.singletonList(typeJSON.get("superClass"));
            }
        }
        return baseTypes.stream().map(extend -> {
            assert extend.getAsJsonObject().get("type").getAsString().equals("InterfaceExtends") || extend.getAsJsonObject().get("type").getAsString().equals("Identifier");
            JsonObject id = extend.getAsJsonObject().get("type").getAsString().equals("Identifier") ? extend.getAsJsonObject() : extend.getAsJsonObject().get("id").getAsJsonObject();
            assert id.get("typeAnnotation").isJsonNull();
            assert !id.get("optional").getAsBoolean();
            assert id.get("type").getAsString().equals("Identifier");
            String name = id.get("name").getAsString();
            Type baseType = TypeNameCreator.lookUp(namedTypes, nameContext, name, this.typeParameters, typeJSON.get("range").getAsJsonArray());
            if (baseType instanceof DelayedType) {
                baseType = ((DelayedType) baseType).getType();
            }
            if (baseType instanceof ClassInstanceType) {
                baseType = ((ClassInstanceType) baseType).getClassType();
            }
            assert baseType != null;

            if (extend.getAsJsonObject().get("typeParameters") != null && !extend.getAsJsonObject().get("typeParameters").isJsonNull()) {
                JsonObject parameters = extend.getAsJsonObject().get("typeParameters").getAsJsonObject();
                assert parameters.get("type").getAsString().equals("TypeParameterInstantiation");
                List<Type> typeArguments = Lists.newArrayList(parameters.get("params").getAsJsonArray()).stream().map(param -> parseType(param.getAsJsonObject(), nameContext)).collect(Collectors.toList());

                assert !typeArguments.isEmpty();

                ReferenceType refType = new ReferenceType();
                refType.setTarget(baseType);
                refType.setTypeArguments(typeArguments);
                baseType = refType;
            }

            return baseType;
        }).collect(Collectors.toList());
    }

    List<Type> createTypeParameters(JsonObject typeJSON, String nameContext) {
        List<Type> typeParameters = new ArrayList<>();
        if (!typeJSON.get("typeParameters").isJsonNull()) {
            Pair<Integer, Integer> range = parseRange(typeJSON);

            JsonObject parametersJSON = typeJSON.get("typeParameters").getAsJsonObject();
            assert parametersJSON.get("type").getAsString().equals("TypeParameterDeclaration");
            for (JsonElement parameterJSONRaw : parametersJSON.get("params").getAsJsonArray()) {
                JsonObject parameterJSON = parameterJSONRaw.getAsJsonObject();
                String name = parameterJSON.get("name").getAsString();

                Type typeParameterType = parseType(parameterJSON, nameContext);
                registerTypeParameter(name, range, typeParameterType);
                typeParameters.add(typeParameterType);
            }
        }
        return typeParameters;
    }

    private Type parseClass(JsonObject classJSON, String nameContext) {
        ClassType classType = TypesUtil.emptyClassType();
        classType.getBaseTypes().addAll(parseBaseTypes(classJSON, nameContext));
        assert classJSON.get("implements").getAsJsonArray().size() == 0;
        classType.getBaseTypes().addAll(parseBaseTypes(classJSON, nameContext, "mixins"));
        createTypeParameters(classJSON, nameContext).forEach(classType.getTypeParameters()::add);
        assert classJSON.get("superTypeParameters") == null || classJSON.get("superTypeParameters").isJsonNull();
        assert classJSON.get("decorators") == null || classJSON.get("decorators").getAsJsonArray().size() == 0;

        JsonObject body = classJSON.get("body").getAsJsonObject();
        assert body.get("type").getAsString().equals("ObjectTypeAnnotation") || body.get("type").getAsString().equals("ClassBody");
        assert body.get("exact") == null || !body.get("exact").getAsBoolean();
        assert body.get("indexers") == null || body.get("indexers").getAsJsonArray().size() == 0;
        assert body.get("internalSlots") == null || body.get("internalSlots").getAsJsonArray().size() == 0;

        if (body.get("callProperties") != null) {
            for (JsonElement callProperty : body.get("callProperties").getAsJsonArray()) {
                boolean isStatic = callProperty.getAsJsonObject().get("static").getAsBoolean();
                List<Signature> signatures = ((InterfaceType) parseType(callProperty.getAsJsonObject().get("value").getAsJsonObject(), nameContext).getType()).getDeclaredCallSignatures();
                assert !signatures.isEmpty();

                if (isStatic) {
                    classType.getCallSignatures().addAll(signatures);
                } else {
                    throw new RuntimeException("I don't support this yet (requires extension in ClassType)");
                }
            }
        }

        final JsonArray properties;
        if (body.get("type").getAsString().equals("ObjectTypeAnnotation")) {
            properties = body.get("properties").getAsJsonArray();
        } else {
            properties = body.get("body").getAsJsonArray();
        }
        for (JsonElement rawProperty : properties) {
            JsonObject property = rawProperty.getAsJsonObject();
            switch (property.get("type").getAsString()) {
                case "ObjectTypeProperty":
                case "ClassProperty":
                case "MethodDefinition":
                    assert property.get("computed") == null || !property.get("computed").getAsBoolean();
                    Type propertyType;
                    if (property.get("value").isJsonNull()) {
                        propertyType = parseType(property.get("typeAnnotation").getAsJsonObject(), nameContext).getType();
                    } else {
                        propertyType = parseType(property.get("value").getAsJsonObject(), nameContext).getType();
                    }
                    if (property.get("optional") != null && property.get("optional").getAsBoolean()) {
                        propertyType = new UnionType(Arrays.asList(new SimpleType(SimpleTypeKind.Undefined), propertyType));
                    }
                    assert property.get("proto") == null || !property.get("proto").getAsBoolean();
//                    assert property.get("variance").isJsonNull() || !property.get("variance").getAsBoolean(); // Tbh. Variance doesn't matter for my purposes, so I just ignore it.
                    assert property.get("kind") == null || property.get("kind").getAsString().equals("init");
                    assert property.get("key").getAsJsonObject().get("type").getAsString().equals("Identifier");

                    boolean isStatic = property.get("static").getAsBoolean();
                    String name = property.get("key").getAsJsonObject().get("name").getAsString();

                    if (name.equals("constructor") && !isStatic) {
                        //noinspection ConstantConditions
                        List<Signature> signatures = ((InterfaceType)propertyType).getDeclaredCallSignatures();
                        assert !signatures.isEmpty();
                        signatures.stream().map(TypesUtil::cloneSignature).peek(sig -> sig.setResolvedReturnType(null)).forEach(classType.getConstructors()::add);
                        break;
                    }

                    if (isStatic) {
                        classType.getStaticProperties().put(name, propertyType);
                    } else {
                        classType.getInstanceProperties().put(name, propertyType);
                    }
                    break;
                default:
                    throw new RuntimeException(property.get("type").getAsString());
            }
        }


        return classType;
    }

    private Type parseFunctionType(JsonObject typeJSON, String nameContext) {
        Signature signature = TypesUtil.emptySignature();

        if (!typeJSON.get("typeParameters").isJsonNull()) {
            for (JsonElement typeParameterRaw : typeJSON.get("typeParameters").getAsJsonObject().get("params").getAsJsonArray()) {
                JsonObject typeParameter = typeParameterRaw.getAsJsonObject();
                Type typeParameterType = parseType(typeParameter, nameContext).getType();
                String name = typeParameter.get("name").getAsString();
                Pair<Integer, Integer> range = parseRange(typeJSON);
                registerTypeParameter(name, range, typeParameterType);
            }
        }

        signature.setResolvedReturnType(parseType(typeJSON.get("returnType").getAsJsonObject(), nameContext));

        ArrayList<JsonElement> rawParams = Lists.newArrayList(typeJSON.get("params").getAsJsonArray());

        AtomicInteger minArgs = new AtomicInteger();

        rawParams.stream().map(JsonObject.class::cast).map(param -> {
            assert param.get("type").getAsString().equals("FunctionTypeParam");

            if (!param.get("optional").getAsBoolean()) {
                minArgs.getAndIncrement();
            }
            Type type = parseType(param.get("typeAnnotation").getAsJsonObject(), nameContext);
            String name = param.get("name").isJsonNull() ? "arg" : param.get("name").getAsJsonObject().get("name").getAsString();
            return new Signature.Parameter(name, type);
        }).forEach(signature.getParameters()::add);

        if (!typeJSON.get("rest").isJsonNull()) {
            Type type = parseType(typeJSON.get("rest").getAsJsonObject().get("typeAnnotation").getAsJsonObject(), nameContext).getType();
            if (type instanceof SimpleType) {
                assert ((SimpleType) type).getKind() == SimpleTypeKind.Any;
                ReferenceType refType = new ReferenceType();
                refType.setTypeArguments(Collections.singletonList(new SimpleType(SimpleTypeKind.Any)));
                refType.setTarget(namedTypes.get("Array"));
                type = refType;
            }
            String name = typeJSON.get("rest").getAsJsonObject().get("name").getAsJsonObject().get("name").getAsString();
            signature.setHasRestParameter(true);
            signature.getParameters().add(new Signature.Parameter(name, type));
        }

        signature.setMinArgumentCount(minArgs.get());

        InterfaceType interfaceType = SpecReader.makeEmptySyntheticInterfaceType();
        interfaceType.getDeclaredCallSignatures().add(signature);
        return interfaceType;
    }

    private Pair<Integer, Integer> parseRange(JsonObject typeJSON) {
        return Lists.newArrayList(typeJSON.get("range").getAsJsonArray()).stream().map(JsonElement::getAsNumber).map(Number::intValue).collect(Pair.collector());
    }

    // https://flow.org/en/docs/types/utilities/
    private static Map<String, BiFunction<FlowParser, List<Type>, Type>> utilityTypes = new HashMap<>(){{
        put("$Exports", ((flowParser, types) -> { // <- Not in any documentation. Wtf flow?
            assert types.size() == 1;
            String moduleName = ((StringLiteral) ((DelayedType) types.get(0)).getType()).getText();
            List<SpecReader.NamedType> matchingModules = flowParser.ambientTypes.stream().filter(ambient -> ambient.qName.size() == 1 && ambient.qName.get(0).equals(moduleName)).collect(Collectors.toList());
            assert matchingModules.size() == 1;
            return matchingModules.get(0).type;
        }));
        put("$Keys", ((flowParser, types) -> { throw new RuntimeException();}));
        put("$Values", ((flowParser, types) -> { throw new RuntimeException();}));
        put("$ReadOnly", ((flowParser, types) -> { throw new RuntimeException();}));
        put("$Exact", ((flowParser, types) -> { throw new RuntimeException();}));
        put("$Diff", ((flowParser, types) -> { throw new RuntimeException();}));
        put("$Rest", ((flowParser, types) -> { throw new RuntimeException();}));
        put("$PropertyType", ((flowParser, types) -> { throw new RuntimeException();}));
        put("$ElementType", ((flowParser, types) -> { throw new RuntimeException();}));
        put("$ObjMap", ((flowParser, types) -> { throw new RuntimeException();}));
        put("$TupleMap", ((flowParser, types) -> { throw new RuntimeException();}));
        put("$Call", ((flowParser, types) -> { throw new RuntimeException();}));
        put("Class", ((flowParser, types) -> { // Only Utility type not to start with "$", yay for consistency.
            assert types.size() == 1;
            Type type = types.get(0);
            if (type instanceof DelayedType) {
                type = ((DelayedType) type).getType();
            }
            if (type instanceof ReferenceType) {
                type = ((ReferenceType) type).getTarget();
            }
            if (type instanceof DelayedType) {
                type = ((DelayedType) type).getType();
            }
            assert type instanceof ClassInstanceType;
            return ((ClassInstanceType) type).getClassType();
        }));
        put("$Supertype", ((flowParser, types) -> { throw new RuntimeException();}));
        put("$Subtype", ((flowParser, types) -> { throw new RuntimeException();}));
        put("$ReadOnlyArray", (((flowParser, types) -> { // Yet another undocumented feature. WHICH IS USED IN flow-typed!?!?
            assert types.size() == 1;

            Type array = flowParser.namedTypes.get("Array"); // For now just an ordinary array. I only read from it anyway.

            ReferenceType resultType = new ReferenceType();
            resultType.setTarget(array);
            resultType.setTypeArguments(Collections.singletonList(types.get(0)));

            return resultType;
        })));
        put("$Shape", (((flowParser, types) -> { // Yet another undocumented feature. Which is used in flow-typed, and their own documentation?!?!?!?
            assert types.size() == 1;

            Type type = types.iterator().next();
            if (type instanceof DelayedType) {
                type = ((DelayedType) type).getType();
            }
            if (type instanceof InterfaceType) {
                InterfaceType org = (InterfaceType) type;
                InterfaceType interfaceType = SpecReader.makeEmptySyntheticInterfaceType();
                interfaceType.setBaseTypes(org.getBaseTypes());
                interfaceType.setDeclaredCallSignatures(org.getDeclaredCallSignatures());
                interfaceType.setDeclaredConstructSignatures(org.getDeclaredConstructSignatures());
                interfaceType.setReadonlyDeclarations(org.getReadonlyDeclarations());
                interfaceType.setDeclaredNumberIndexType(org.getDeclaredNumberIndexType());
                interfaceType.setDeclaredProperties(org.getDeclaredProperties().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new UnionType(Arrays.asList(new SimpleType(SimpleTypeKind.Undefined), entry.getValue())))));
                interfaceType.setDeclaredStringIndexType(org.getDeclaredStringIndexType());
                interfaceType.setTypeParameters(org.getTypeParameters());
                return interfaceType;
            }
            if (type instanceof TypeParameterType) {
                return type; // good enough.
            }

            throw new RuntimeException(type.getClass().getSimpleName());
        })));
    }};
}
