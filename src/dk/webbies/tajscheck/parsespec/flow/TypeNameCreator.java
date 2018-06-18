package dk.webbies.tajscheck.parsespec.flow;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.au.cs.casa.typescript.types.DelayedType;
import dk.au.cs.casa.typescript.types.ReferenceType;
import dk.au.cs.casa.typescript.types.Type;
import dk.au.cs.casa.typescript.types.TypeParameterType;
import dk.webbies.tajscheck.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class TypeNameCreator {
    private FlowParser flowParser;

    static Type lookUp(Map<String, Type> namedTypes, String nameContext, String name, Map<String, List<Pair<Pair<Integer, Integer>, Type>>> typeParameters, JsonArray rawRange) {
        if (typeParameters.containsKey(name)) {
            Pair<Integer, Integer> range = Lists.newArrayList(rawRange).stream().map(JsonElement::getAsNumber).map(Number::intValue).collect(Pair.collector());
            List<Pair<Pair<Integer, Integer>, Type>> candidates = typeParameters.get(name).stream().filter(candidate -> candidate.getLeft().getLeft() <= range.getLeft() && candidate.getLeft().getRight() >= range.getRight()).collect(Collectors.toList());
            assert candidates.size() < 2;
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

    private DelayedType parseType(JsonObject obj, String typeContext) {
        return flowParser.parseType(obj, typeContext);
    }

    TypeNameCreator(FlowParser flowParser) {
        this.flowParser = flowParser;
    }

    Map<String, Type> createTypeNames(JsonArray body) {
        Map<String, Type> result = new HashMap<>();
        for (JsonElement rawStatement : body) {
            parseModuleStatement("", result, rawStatement.getAsJsonObject());
        }


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
            case "DeclareExportDeclaration":{ // For now, everthing is assumed to be exported. Which is not an issue for my purpose.
                JsonObject declaration = moduleStatement.get("declaration").getAsJsonObject();
                parseModuleStatement(nameContext, result, declaration);
                break;
            }
            case "DeclareVariable": {
                JsonObject id = moduleStatement.get("id").getAsJsonObject();
                String name = id.get("name").getAsString();
                DelayedType type = parseType(id.get("typeAnnotation").getAsJsonObject(), nameContext);
                result.put(newNameContext(nameContext, name), type);
                break;
            }
            case "InterfaceDeclaration": {
                JsonObject id = moduleStatement.get("id").getAsJsonObject();
                String name = id.get("name").getAsString();
                DelayedType type = parseType(moduleStatement, nameContext);
                result.put(newNameContext(nameContext, name), type);
                break;
            }
            case "DeclareTypeAlias":
            case "TypeAlias":
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
            case "DeclareClass":
            case "ClassDeclaration":
                result.put(
                        newNameContext(nameContext, moduleStatement.get("id").getAsJsonObject().get("name").getAsString()),
                        parseType(moduleStatement, nameContext)
                );
                break;
            case "DeclareModule":
                result.putAll(parseModule(moduleStatement, nameContext));
                break;
            default:
                throw new RuntimeException(moduleStatement.get("type").getAsString());
        }
    }

}
