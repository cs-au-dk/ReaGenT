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
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class FlowParser {
    // TODO: See if declaring a variable to be instanceof a class works like it should.
    private final SpecReader emptySpec;
    private final Map<String, Type> namedTypes = new HashMap<>();

    private FlowParser(ParseDeclaration.Environment environment) {
        this.emptySpec = ParseDeclaration.getTypeSpecification(environment, Collections.emptyList());
        for (SpecReader.NamedType namedType : emptySpec.getNamedTypes()) {
            namedTypes.put(String.join(".", namedType.qName), namedType.type);
        }
    }

    public static SpecReader parse(ParseDeclaration.Environment environment, List<String> declarationFiles) {
        FlowParser flowParser = new FlowParser(environment);

        if (declarationFiles.isEmpty()) {
            return new SpecReader(SpecReader.makeEmptySyntheticInterfaceType(), flowParser.emptySpec.getNamedTypes(), new ArrayList<>(), new HashMap<>());
        }
        final String astJSON = parseDeclaration(declarationFiles);

        List<SpecReader.NamedType> namedTypes = new ArrayList<>(flowParser.emptySpec.getNamedTypes());
        List<SpecReader.NamedType> ambientTypes = new ArrayList<>();
        Map<String, Type> globalProperties = new HashMap<>();

        JsonArray body = new JsonParser().parse(astJSON).getAsJsonObject().get("body").getAsJsonArray();

        flowParser.namedTypes.putAll(new TypeNameCreator(flowParser::parseType).createTypeNames(body));

        for (JsonElement rawStatement : body) {
            JsonObject statement = rawStatement.getAsJsonObject();
            switch (statement.get("type").getAsString()) {
                case "DeclareModule":
                    flowParser.parseModule(ambientTypes, globalProperties, statement);
                    break;
                default:
                    throw new RuntimeException("Unknown type of statement: " + statement.get("type").getAsString());
            }
        }

        InterfaceType global = SpecReader.makeEmptySyntheticInterfaceType();
        globalProperties.forEach((name, type) -> global.getDeclaredProperties().put(name, type instanceof DelayedType ? ((DelayedType) type).getType() : type));
        return new SpecReader(global, expandDelayed(namedTypes), expandDelayed(ambientTypes), new HashMap<>());

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
        assert id.get("type").getAsString().equals("Literal");

        String name = statement.get("id").getAsJsonObject().get("raw").getAsString();

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
                JsonObject moduleStatement = moduleStatementRaw.getAsJsonObject();
                switch (moduleStatement.get("type").getAsString()) {
                    case "DeclareExportDeclaration":
                        JsonObject declaration = moduleStatement.get("declaration").getAsJsonObject();
                        switch (declaration.get("type").getAsString()) {
                            case "DeclareClass":
                                declaredTypes.put(
                                        declaration.get("id").getAsJsonObject().get("name").getAsString(),
                                        this.parseType(declaration, name)
                                );
                                break;
                            case "DeclareVariable": {
                                JsonObject varId = declaration.get("id").getAsJsonObject();
                                String varName = varId.get("name").getAsString();
                                Type varType = parseType(varId.get("typeAnnotation").getAsJsonObject(), name);
                                declaredTypes.put(varName, varType);
                                break;
                            }
                            case "InterfaceDeclaration":
                                break; // Doesn't declare any actual value, just a named-type, which has already been handled.
                            default:
                                throw new RuntimeException(declaration.get("type").getAsString());
                        }
                        break;
                    default:
                        throw new RuntimeException(moduleStatement.get("type").getAsString());
                }
            }

            InterfaceType interfaceType = SpecReader.makeEmptySyntheticInterfaceType();
            declaredTypes.forEach(interfaceType.getDeclaredProperties()::put);
            type = interfaceType;
        }

        if (name.startsWith("'")) {
            name = name.substring(1, name.length() - 1);
            assert !name.contains(".");
            ambientTypes.add(new SpecReader.NamedType(type, Collections.singletonList(name)));
        } else {
            globalProperties.put(name, type);
        }
    }

    private final Map<JsonObject, DelayedType> parseTypeCache = new HashMap<>();
    private DelayedType parseType(JsonObject typeJSON, String nameContext) {
        if (parseTypeCache.containsKey(typeJSON)) {
            return parseTypeCache.get(typeJSON);
        }
        DelayedType result = new DelayedType(() -> {
            switch (typeJSON.get("type").getAsString()) {
                case "TypeAnnotation":
                    return parseType(typeJSON.get("typeAnnotation").getAsJsonObject(), nameContext);
                case "FunctionTypeAnnotation":
                    return parseFunctionType(typeJSON, nameContext);
                case "StringTypeAnnotation":
                    return new SimpleType(SimpleTypeKind.String);
                case "NumberTypeAnnotation":
                    return new SimpleType(SimpleTypeKind.Number);
                case "VoidTypeAnnotation":
                    return new SimpleType(SimpleTypeKind.Void);
                case "UnionTypeAnnotation":
                    return new UnionType(Lists.newArrayList(typeJSON.get("types").getAsJsonArray()).stream().map(JsonObject.class::cast).map(obj -> parseType(obj, nameContext)).collect(Collectors.toList()));
                case "DeclareClass":
                    return parseClass(typeJSON, nameContext);
                case "GenericTypeAnnotation":{
                    assert typeJSON.get("typeParameters").isJsonNull();
                    String name = typeJSON.get("id").getAsJsonObject().get("name").getAsString();
                    Type type = TypeNameCreator.lookUp(namedTypes, nameContext, name);
                    assert type != null;
                    return type;
                }
                case "BooleanTypeAnnotation":
                    return new SimpleType(SimpleTypeKind.Boolean);
                case "BooleanLiteralTypeAnnotation":
                    return new BooleanLiteral(typeJSON.get("value").getAsBoolean());
                case "NullableTypeAnnotation":
                    return new UnionType(Arrays.asList(new SimpleType(SimpleTypeKind.Null), new SimpleType(SimpleTypeKind.Undefined), parseType(typeJSON.get("typeAnnotation").getAsJsonObject(), nameContext)));
                case "ObjectTypeAnnotation":
                    assert !typeJSON.get("exact").getAsBoolean();
                    assert typeJSON.get("indexers").getAsJsonArray().size() == 0;
                    assert typeJSON.get("callProperties").getAsJsonArray().size() == 0;
                    assert typeJSON.get("internalSlots").getAsJsonArray().size() == 0;
                    Map<String, Type> properties = new HashMap<>();
                    for (JsonElement propertyRaw : typeJSON.get("properties").getAsJsonArray()) {
                        JsonObject propertyJSON = propertyRaw.getAsJsonObject();
                        assert propertyJSON.get("type").getAsString().equals("ObjectTypeProperty");
                        String name = propertyJSON.get("key").getAsJsonObject().get("name").getAsString();
                        DelayedType type = parseType(propertyJSON.get("value").getAsJsonObject(), nameContext);
                        properties.put(name, type);
                    }
                    InterfaceType interfaceType = SpecReader.makeEmptySyntheticInterfaceType();
                    interfaceType.getDeclaredProperties().putAll(properties);
                    return interfaceType;
                default:
                    throw new RuntimeException("Unknown type: " + typeJSON.get("type").getAsString());
            }
        });
        parseTypeCache.put(typeJSON, result);
        return result;
    }

    private Type parseClass(JsonObject classJSON, String nameContext) {
        ClassType classType = TypesUtil.emptyClassType();
        assert classJSON.get("extends").getAsJsonArray().size() == 0;
        assert classJSON.get("implements").getAsJsonArray().size() == 0;
        assert classJSON.get("mixins").getAsJsonArray().size() == 0;
        assert classJSON.get("typeParameters").isJsonNull();

        JsonObject body = classJSON.get("body").getAsJsonObject();
        assert body.get("type").getAsString().equals("ObjectTypeAnnotation");
        assert !body.get("exact").getAsBoolean();
        assert body.get("indexers").getAsJsonArray().size() == 0;
        assert body.get("callProperties").getAsJsonArray().size() == 0;
        assert body.get("internalSlots").getAsJsonArray().size() == 0;

        for (JsonElement rawProperty : body.get("properties").getAsJsonArray()) {
            JsonObject property = rawProperty.getAsJsonObject();
            switch (property.get("type").getAsString()) {
                case "ObjectTypeProperty":
                    Type propertyType = parseType(property.get("value").getAsJsonObject(), nameContext);
                    assert !property.get("optional").getAsBoolean();
                    assert !property.get("static").getAsBoolean();
                    assert !property.get("proto").getAsBoolean();
                    assert property.get("variance").isJsonNull() || !property.get("variance").getAsBoolean();
                    assert property.get("kind").getAsString().equals("init");
                    assert property.get("key").getAsJsonObject().get("type").getAsString().equals("Identifier");

                    String name = property.get("key").getAsJsonObject().get("name").getAsString();
                    classType.getInstanceProperties().put(name, propertyType);
                    break;
                default:
                    throw new RuntimeException(property.get("type").getAsString());
            }
        }


        return classType;
    }

    private Type parseFunctionType(JsonObject typeJSON, String nameContext) {
        Signature signature = TypesUtil.emptySignature();

        signature.setResolvedReturnType(parseType(typeJSON.get("returnType").getAsJsonObject(), nameContext));

        ArrayList<JsonElement> rawParams = Lists.newArrayList(typeJSON.get("params").getAsJsonArray());

        AtomicInteger minArgs = new AtomicInteger();

        rawParams.stream().map(JsonObject.class::cast).map(param -> {
            assert param.get("type").getAsString().equals("FunctionTypeParam");

            if (!param.get("optional").getAsBoolean()) {
                minArgs.getAndIncrement();
            }
            Type type = parseType(param.get("typeAnnotation").getAsJsonObject(), nameContext);
            String name = param.get("name").getAsJsonObject().get("name").getAsString();
            return new Signature.Parameter(name, type);
        }).forEach(signature.getParameters()::add);

        signature.setMinArgumentCount(minArgs.get());

        InterfaceType interfaceType = SpecReader.makeEmptySyntheticInterfaceType();
        interfaceType.getDeclaredCallSignatures().add(signature);
        return interfaceType;
    }
}
