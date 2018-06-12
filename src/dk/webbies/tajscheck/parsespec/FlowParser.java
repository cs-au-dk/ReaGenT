package dk.webbies.tajscheck.parsespec;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FlowParser {
    private static final SpecReader emptySpec = ParseDeclaration.getTypeSpecification(ParseDeclaration.Environment.ES5Core, Collections.emptyList());
    private static final Map<String, Type> nativeNamedTypes = new HashMap<>(); {
        for (SpecReader.NamedType namedType : emptySpec.getNamedTypes()) {
            nativeNamedTypes.put(String.join(".", namedType.qName), namedType.type);
        }
    }

    public static SpecReader parse(List<String> declarationFiles) {
        if (declarationFiles.isEmpty()) {
            return new SpecReader(SpecReader.makeEmptySyntheticInterfaceType(), new ArrayList<>(), new ArrayList<>(), new HashMap<>());
        }
        final String astJSON;
        try {
            astJSON = Util.runNodeScript("resources/parse-flow.js " + String.join(" ", declarationFiles));
        } catch (IOException e) {
            throw new RuntimeException();
        }

        List<SpecReader.NamedType> namedTypes = new ArrayList<>();
        List<SpecReader.NamedType> ambientTypes = new ArrayList<>();
        Map<String, Type> globalProperties = new HashMap<>();

        FlowParser flowParser = new FlowParser();


        for (JsonElement rawStatement : new JsonParser().parse(astJSON).getAsJsonObject().get("body").getAsJsonArray()) {
            JsonObject statement = rawStatement.getAsJsonObject();
            switch (statement.get("type").getAsString()) {
                case "DeclareModule":{
                    JsonObject id = statement.get("id").getAsJsonObject();
                    assert id.get("type").getAsString().equals("Literal");

                    String name = statement.get("id").getAsJsonObject().get("raw").getAsString();

                    assert statement.get("body").getAsJsonObject().get("type").getAsString().equals("BlockStatement");

                    List<JsonObject> moduleStatements = Lists.newArrayList(statement.get("body").getAsJsonObject().get("body").getAsJsonArray()).stream().map(JsonObject.class::cast).collect(Collectors.toList());

                    final Type type;
                    if (moduleStatements.stream().anyMatch(stmt -> stmt.get("type").getAsString().equals("DeclareModuleExports"))) {
                        //noinspection ConstantConditions
                        JsonObject exports = moduleStatements.stream().filter(stmt -> stmt.get("type").getAsString().equals("DeclareModuleExports")).findFirst().get();
                        type = flowParser.parseType(exports.get("typeAnnotation").getAsJsonObject());
                    } else {
                        throw new RuntimeException();
                    }

                    if (name.startsWith("'")) {
                        name = name.substring(1, name.length() - 1);
                        assert !name.contains(".");
                        ambientTypes.add(new SpecReader.NamedType(type, Collections.singletonList(name)));
                        break;
                    } else {
                        throw new RuntimeException();
                    }

                }
                default:
                    throw new RuntimeException("Unknown type of statement: " + statement.get("type").getAsString());
            }
        }

        InterfaceType global = SpecReader.makeEmptySyntheticInterfaceType();
        globalProperties.forEach(global.getDeclaredProperties()::put);
        return new SpecReader(global, namedTypes, ambientTypes, new HashMap<>());

    }

    private Type parseType(JsonObject typeJSON) {
        switch (typeJSON.get("type").getAsString()) {
            case "TypeAnnotation": {
                return parseType(typeJSON.get("typeAnnotation").getAsJsonObject());
            }
            case "FunctionTypeAnnotation": {
                return parseFunctionType(typeJSON);
            }
            case "StringTypeAnnotation":
                return new SimpleType(SimpleTypeKind.String);
            case "NumberTypeAnnotation":
                return new SimpleType(SimpleTypeKind.Number);
            case "UnionTypeAnnotation":
                return new UnionType(Lists.newArrayList(typeJSON.get("types").getAsJsonArray()).stream().map(JsonObject.class::cast).map(this::parseType).collect(Collectors.toList()));
            default:
                throw new RuntimeException("Unknown type: " + typeJSON.get("type").getAsString());
        }
    }

    private Type parseFunctionType(JsonObject typeJSON) {
        Signature signature = TypesUtil.emptySignature();

        signature.setResolvedReturnType(parseType(typeJSON.get("returnType").getAsJsonObject()));

        ArrayList<JsonElement> rawParams = Lists.newArrayList(typeJSON.get("params").getAsJsonArray());

        AtomicInteger minArgs = new AtomicInteger();

        rawParams.stream().map(JsonObject.class::cast).map(param -> {
            assert param.get("type").getAsString().equals("FunctionTypeParam");

            if (!param.get("optional").getAsBoolean()) {
                minArgs.getAndIncrement();
            }
            Type type = parseType(param.get("typeAnnotation").getAsJsonObject());
            String name = param.get("name").getAsJsonObject().get("name").getAsString();
            return new Signature.Parameter(name, type);
        }).forEach(signature.getParameters()::add);

        signature.setMinArgumentCount(minArgs.get());

        InterfaceType interfaceType = SpecReader.makeEmptySyntheticInterfaceType();
        interfaceType.getDeclaredCallSignatures().add(signature);
        return interfaceType;
    }
}
