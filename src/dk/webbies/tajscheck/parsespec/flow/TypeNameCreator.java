package dk.webbies.tajscheck.parsespec.flow;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class TypeNameCreator {
    private final Map<String, Type> typeNames;
    private final FlowParser flowParser;
    private final List<Pair<JsonObject, String>> classDefinitions = new ArrayList<>();

    static Type lookUp(Map<String, Type> namedTypes, String nameContext, String name, Map<String, List<Pair<Pair<Integer, Integer>, Type>>> typeParameters, JsonArray rawRange) {
        if (name.contains(".")) {
            Type baseType = lookUp(namedTypes, nameContext, name.substring(0, name.indexOf(".")), typeParameters, rawRange);
            return lookUpInType(baseType, name.substring(name.indexOf(".") + 1, name.length()));
        }
        if (typeParameters.containsKey(name)) {
            Pair<Integer, Integer> range = Lists.newArrayList(rawRange).stream().map(JsonElement::getAsNumber).map(Number::intValue).collect(Pair.collector());
            List<Pair<Pair<Integer, Integer>, Type>> candidates = typeParameters.get(name).stream().filter(candidate -> candidate.getLeft().getLeft() <= range.getLeft() && candidate.getLeft().getRight() >= range.getRight()).collect(Collectors.toList());

            if (candidates.size() < 2) {
                //noinspection AssertWithSideEffects
                assert candidates.stream().map(Pair::getRight).map(t -> {
                    if (t instanceof DelayedType) {
                        return ((DelayedType) t).getType();
                    } else {
                        return t;
                    }
                }).distinct().count() == 1;
            }
            if (!candidates.isEmpty()) {
                return candidates.iterator().next().getRight();
            }
        }


        if (namedTypes.containsKey(name)) {
            return namedTypes.get(name);
        }
        if (nameContext.equals("")) {
            return namedTypes.get(name);
        }
        while (true) {
            String key = nameContext + "." + name;
            if (namedTypes.containsKey(key)) {
                return namedTypes.get(key);
            }
            if (nameContext.contains(".")) {
                nameContext = nameContext.substring(0, nameContext.lastIndexOf("."));
            } else {
                break;
            }
        }
        return null;
    }

    private static Type lookUpInType(Type type, String prop) {
        assert !prop.contains(".");
        if (type instanceof DelayedType) {
            return lookUpInType(((DelayedType) type).getType(), prop);
        } else if (type instanceof InterfaceType) {
            return ((InterfaceType) type).getDeclaredProperties().get(prop);
        } else if (type instanceof GenericType) {
            return lookUpInType(((GenericType) type).toInterface(), prop);
        } else if (type instanceof ClassInstanceType) {
            return lookUpInType(((ClassInstanceType) type).getClassType(), prop);
        } else if (type instanceof ClassType) {
            Type result = ((ClassType) type).getStaticProperties().get(prop);
            assert result != null;
            return result;
        }

        throw new RuntimeException(type.getClass().getSimpleName());
    }

    private DelayedType parseType(JsonObject obj, String typeContext) {
        return flowParser.parseType(obj, typeContext);
    }

    TypeNameCreator(FlowParser flowParser, JsonArray body) {
        this.flowParser = flowParser;
        this.typeNames = createTypeNames(body);
    }

    public Map<String, Type> getTypeNames() {
        return typeNames;
    }

    public List<Pair<JsonObject, String>> getClassDefinitions() {
        return classDefinitions;
    }

    private Map<String, Type> createTypeNames(JsonArray body) {
        Map<String, Type> result = new HashMap<>();
        body.forEach(rawStatement -> parseModuleStatement("", result, rawStatement.getAsJsonObject()));
        return result;
    }

    private Map<String, Type> parseModule(JsonObject statement, String nameContext) {
        JsonObject id = statement.get("id").getAsJsonObject();

        nameContext = newNameContext(nameContext, FlowParser.getName(id));


        assert statement.get("body").getAsJsonObject().get("type").getAsString().equals("BlockStatement");

        Map<String, Type> result = new HashMap<>();

        List<JsonObject> moduleStatements = Lists.newArrayList(statement.get("body").getAsJsonObject().get("body").getAsJsonArray()).stream().map(JsonObject.class::cast).collect(Collectors.toList());

        for (JsonObject moduleStatementRaw : moduleStatements) {
            JsonObject moduleStatement = moduleStatementRaw.getAsJsonObject();
            parseModuleStatement(nameContext, result, moduleStatement);
        }

        if (moduleStatements.stream().anyMatch(stmt -> stmt.get("type").getAsString().equals("DeclareModuleExports"))) {
            //noinspection ConstantConditions
            JsonObject exports = moduleStatements.stream().filter(stmt -> stmt.get("type").getAsString().equals("DeclareModuleExports")).findFirst().get();
            DelayedType type = this.parseType(exports.get("typeAnnotation").getAsJsonObject(), nameContext);
            result.put(nameContext, type);
        }

        return result;
    }

    private String newNameContext(String previous, String name) {
        if (previous.isEmpty()) {
            return name;
        }
        return previous + "." + name;
    }

    private void parseModuleStatement(String nameContext, Map<String, Type> result, JsonObject moduleStatement) {
        switch (moduleStatement.get("type").getAsString()) {
            case "DeclareModuleExports":
                // Does not produce a named type, and it has already been registered as the type for the module.
                break;
            case "DeclareExportDeclaration":{ // For now, everything is assumed to be exported. Which is not an issue for my purpose.
                JsonObject declaration = moduleStatement.get("declaration").getAsJsonObject();
                parseModuleStatement(nameContext, result, declaration);
                break;
            }
            case "DeclareFunction":
            case "DeclareVariable": {
                JsonObject id = moduleStatement.get("id").getAsJsonObject();
                String name = id.get("name").getAsString();
                DelayedType type = parseType(id.get("typeAnnotation").getAsJsonObject(), nameContext);
                result.put(newNameContext(nameContext, name), type);
                break;
            }
            case "DeclareInterface":
            case "GenericTypeAnnotation":
            case "InterfaceDeclaration": {
                JsonObject id = moduleStatement.get("id").getAsJsonObject();
                String name = id.get("name").getAsString();
                DelayedType type = parseType(moduleStatement, nameContext);
                result.put(newNameContext(nameContext, name), type);
                break;
            }
            case "DeclareOpaqueType": {
                String name = moduleStatement.get("id").getAsJsonObject().get("name").getAsString();
                List<Type> typeParameters = flowParser.createTypeParameters(moduleStatement, nameContext);

                assert moduleStatement.get("impltype").isJsonNull();
                assert moduleStatement.get("supertype").isJsonNull();
                Type type = new SimpleType(SimpleTypeKind.Any); // Don't really know how to handle this.
                if (!typeParameters.isEmpty()) {
                    ReferenceType refType = new ReferenceType();
                    refType.setTypeArguments(typeParameters);
                    refType.setTarget(type);
                    type = refType;
                }

                result.put(newNameContext(nameContext, name), type);
                break;
            }
            case "DeclareTypeAlias":
            case "TypeAlias":{
                String name = moduleStatement.get("id").getAsJsonObject().get("name").getAsString();
                List<Type> typeParameters = flowParser.createTypeParameters(moduleStatement, nameContext);

                Type type = parseType(moduleStatement.get("right").getAsJsonObject(), nameContext);
                if (!typeParameters.isEmpty()) {
                    ReferenceType refType = new ReferenceType();
                    refType.setTypeArguments(typeParameters);
                    refType.setTarget(type);
                    type = refType;
                }

                result.put(newNameContext(nameContext, name), type);
                break;
            }
            case "DeclareClass":
            case "ClassDeclaration": {
                classDefinitions.add(new Pair<>(moduleStatement, nameContext));
                result.put(
                        newNameContext(nameContext, moduleStatement.get("id").getAsJsonObject().get("name").getAsString()),
                        parseType(moduleStatement, nameContext)
                );
                break;
            }
            case "DeclareModule":
                result.putAll(parseModule(moduleStatement, nameContext));
                break;
            case "EmptyStatement":
                break; // Literally noting.
            default:
                throw new RuntimeException(moduleStatement.get("type").getAsString());
        }
    }

}
