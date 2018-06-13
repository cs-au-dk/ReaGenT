package dk.webbies.tajscheck.parsespec.flow;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.DelayedType;
import dk.au.cs.casa.typescript.types.InterfaceType;
import dk.au.cs.casa.typescript.types.Type;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class TypeNameCreator {
    private final BiFunction<JsonObject, String, DelayedType> parseType;

    static Type lookUp(Map<String, Type> namedTypes, String nameContext, String name) {
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
        return parseType.apply(obj, typeContext);
    }

    TypeNameCreator(BiFunction<JsonObject, String, DelayedType> parseType) {
        this.parseType = parseType;
    }

    Map<String, DelayedType> createTypeNames(JsonArray body) {
        Map<String, DelayedType> result = new HashMap<>();
        for (JsonElement rawStatement : body) {
            JsonObject statement = rawStatement.getAsJsonObject();
            switch (statement.get("type").getAsString()) {
                case "DeclareModule":
                    result.putAll(parseModule(statement));
                    break;
                default:
                    throw new RuntimeException("Unknown type of statement: " + statement.get("type").getAsString());
            }
        }


        return result;
    }

    private Map<String, DelayedType> parseModule(JsonObject statement) {
        JsonObject id = statement.get("id").getAsJsonObject();
        assert id.get("type").getAsString().equals("Literal");

        String moduleName = statement.get("id").getAsJsonObject().get("raw").getAsString();

        assert statement.get("body").getAsJsonObject().get("type").getAsString().equals("BlockStatement");

        Map<String, DelayedType> result = new HashMap<>();

        List<JsonObject> moduleStatements = Lists.newArrayList(statement.get("body").getAsJsonObject().get("body").getAsJsonArray()).stream().map(JsonObject.class::cast).collect(Collectors.toList());

        for (JsonObject moduleStatementRaw : moduleStatements) {
            JsonObject moduleStatement = moduleStatementRaw.getAsJsonObject();
            parseModuleStatement(moduleName, result, moduleStatement);
        }

        if (moduleStatements.stream().anyMatch(stmt -> stmt.get("type").getAsString().equals("DeclareModuleExports"))) {
            //noinspection ConstantConditions
            JsonObject exports = moduleStatements.stream().filter(stmt -> stmt.get("type").getAsString().equals("DeclareModuleExports")).findFirst().get();
            DelayedType type = this.parseType(exports.get("typeAnnotation").getAsJsonObject(), moduleName);
            result.put(moduleName, type);
        }

        return result;
    }

    private void parseModuleStatement(String prefix, Map<String, DelayedType> result, JsonObject moduleStatement) {
        switch (moduleStatement.get("type").getAsString()) {
            case "DeclareExportDeclaration":
            case "DeclareModuleExports":
                // Does not produce a named type, and it has already been registered as the type for the module.
                break;
            case "DeclareTypeAlias":
                String name = moduleStatement.get("id").getAsJsonObject().get("name").getAsString();
                assert moduleStatement.get("typeParameters").isJsonNull();
                result.put(prefix + "." + name, parseType(moduleStatement.get("right").getAsJsonObject(), prefix));
                break;
            case "DeclareClass":
                result.put(
                        prefix + "." + moduleStatement.get("id").getAsJsonObject().get("name").getAsString(),
                        parseType(moduleStatement, prefix)
                );
                break;
            default:
                throw new RuntimeException(moduleStatement.get("type").getAsString());
        }
    }

}
