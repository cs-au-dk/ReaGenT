package dk.webbies.tajscheck.parsespec;

import com.google.gson.*;
import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// TODO: prelude.js from flow. lib/dom.js lib/node.js
public class FlowParser {
    private Map<Integer, JsonObject> resolved_types = new HashMap<>();

    public FlowParser(JsonArray exportedTypes) {
        for (JsonElement exportedType : exportedTypes) {
            if (exportedType.isJsonObject() && exportedType.getAsJsonObject().get("raw_type") != null) {
                findResolvedTypes(new JsonParser().parse(exportedType.getAsJsonObject().get("raw_type").getAsString()));

            }
        }
    }

    private void findResolvedTypes(JsonElement json) {
        if (json.isJsonObject() && json.getAsJsonObject().get("cache_id") != null) {
            JsonObject obj = json.getAsJsonObject();
            int id = Integer.parseInt(obj.get("cache_id").getAsString());
            assert !resolved_types.containsKey(id);
            resolved_types.put(id, obj);
        }
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                findResolvedTypes(entry.getValue());
            }
        }
        if (json.isJsonArray()) {
            for (JsonElement jsonElement : json.getAsJsonArray()) {
                findResolvedTypes(jsonElement);
            }
        }
    }

    public static SpecReader parse(Collection<String> declarationFiles) {
        if (declarationFiles.isEmpty()) {
            return new SpecReader(SpecReader.makeEmptySyntheticInterfaceType(), Collections.emptyList(), Collections.emptyList(), new HashMap<>());
        }
        assert declarationFiles.size() == 1;
        final String flowTypeJSON;
        try {
            String flowBinaryPath = Util.unixify(Paths.get("./lib/flow/flow"));
            flowTypeJSON = Util.runScript("bash -c \"" + flowBinaryPath + " dump-types --raw " + declarationFiles.iterator().next() + "\"", 10 * 60 * 1000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<SpecReader.NamedType> namedTypes = new ArrayList<>();
        List<SpecReader.NamedType> ambientTypes = new ArrayList<>();
        Map<String, Type> globalProperties = new HashMap<>();

        JsonArray exportedTypes = new JsonParser().parse(flowTypeJSON).getAsJsonArray();
        FlowParser flowParser = new FlowParser(exportedTypes);

        for (JsonElement typeDescriptionRaw : exportedTypes) {
            JsonObject typeDescription = typeDescriptionRaw.getAsJsonObject();
            String typeString = typeDescription.get("type").getAsString();
            if (typeString.startsWith("[")) {
                continue;
            }
            switch (typeString) {
                case "Module":
                    String name = getNameFromModule(typeDescription);
                    String rawTypeJSON = typeDescription.get("raw_type").getAsString();
                    Type type = flowParser.parseType(new JsonParser().parse(rawTypeJSON).getAsJsonObject());
                    if (name.startsWith("`")) {
                        name = name.substring(1, name.length() - 1);
                        ambientTypes.add(new SpecReader.NamedType(type, Collections.singletonList(name)));
                    } else {
                        globalProperties.put(name, type);
                    }
                    break;
                case "This":
                    break; // Do nothing, no need.
                default:
                    throw new RuntimeException(typeString);
            }
        }


        InterfaceType global = SpecReader.makeEmptySyntheticInterfaceType();
        globalProperties.forEach(global.getDeclaredProperties()::put);
        return new SpecReader(global, namedTypes, ambientTypes, new HashMap<>());
    }

    private Type parseType(JsonObject typeDescription) {
        typeDescription = resolve(typeDescription);
        String kind = typeDescription.get("kind").getAsString();
        switch (kind) {
            case "ModuleT":
//                assert typeDescription.get("namedExports").getAsJsonArray().size() == 0; // I don't see any need for me to use the namedExports.
                return parseType(typeDescription.get("cjsExport").getAsJsonObject());
            case "FunT":
                return parseFunctionType(typeDescription.get("funType").getAsJsonObject());
            case "StrT":
                return new SimpleType(SimpleTypeKind.String);
            case "NumT":
                return new SimpleType(SimpleTypeKind.Number);
            case "MixedT":
                return new SimpleType(SimpleTypeKind.Any);
            case "VoidT":
                return new SimpleType(SimpleTypeKind.Void);
            case "MaybeT":
                Type type = parseType(typeDescription.get("type").getAsJsonObject());
                return new UnionType(Arrays.asList(new SimpleType(SimpleTypeKind.Null), new SimpleType(SimpleTypeKind.Undefined), type));
            case "OptionalT":
                throw new RuntimeException("Raw optional!");
            case "UnionT":
                return new UnionType(StreamSupport.stream(typeDescription.get("types").getAsJsonArray().spliterator(), false).map(JsonElement::getAsJsonObject).map(this::parseType).collect(Collectors.toList()));
            case "AnnotT":
                assert !typeDescription.get("useDesc").getAsBoolean();
                return parseType(typeDescription.get("assume").getAsJsonObject());
            case "InstanceT":
                ClassInstanceType instanceType = new ClassInstanceType();
                instanceType.setClassType(parseClassType(typeDescription.get("instance").getAsJsonObject()));
                return instanceType;
            case "OpenT": // type annotation kind of thing.
                int id = typeDescription.get("id").getAsNumber().intValue();
                if (openTCache.containsKey(id)) {
                    return openTCache.get(id);
                }
                JsonObject node = typeDescription.get("node").getAsJsonObject();
                assert node.get("kind").getAsString().equals("Root");
                JsonObject root = node.get("root").getAsJsonObject();
                assert root.get("rank").getAsNumber().intValue() == 0;
                JsonObject constraints = root.get("constraints").getAsJsonObject();
                assert constraints.get("kind").getAsString().equals("Resolved");
                JsonObject constraintType = constraints.get("type").getAsJsonObject();
                Type resultType = parseType(constraintType);
                openTCache.put(id, resultType);
                return resultType;
            case "Array":
                throw new RuntimeException(); // TODO: Need native Array type, and where do I get that? (From TypeScript?).
            case "ObjT":
                if (typeDescription.get("type") != null) {
                    typeDescription = typeDescription.get("type").getAsJsonObject();
                }
                JsonObject flags = typeDescription.get("flags").getAsJsonObject();
                assert !flags.get("frozen").getAsBoolean();
//                assert !flags.get("sealed").getAsBoolean(); // Can be both, and that is OK.
//                assert flags.get("exact").getAsBoolean(); // Can be both, and not sure what it means

                Map<String, Type> propTypes = new HashMap<>();
                for (JsonElement propTypeRaw : typeDescription.get("propTypes").getAsJsonArray()) {
                    JsonObject propType = propTypeRaw.getAsJsonObject();
                    String name = propType.get("name").getAsString();
                    JsonObject prop = propType.get("prop").getAsJsonObject();
                    assert prop.get("polarity").getAsString().equals("Neutral");
                    propTypes.put(name, parseType(prop.get("field").getAsJsonObject()));
                }

                InterfaceType interfaceType = SpecReader.makeEmptySyntheticInterfaceType();
                propTypes.forEach(interfaceType.getDeclaredProperties()::put);
                return interfaceType;
            default:
                throw new RuntimeException(kind);
        }
    }

    private JsonObject resolve(JsonObject object) {
        if (object.get("kind").getAsString().equals("unresolved")) {
            int typeId = Integer.parseInt(object.get("id").getAsString());
            return resolved_types.get(typeId);
        } else {
            return object;
        }
    }

    private final Map<Integer, Type> openTCache = new HashMap<>();
    private final Map<Integer, ClassType> classes = new HashMap<>();

    private Type parseClassType(JsonObject classJSON) {
        int classId = classJSON.get("classId").getAsNumber().intValue();

        if (classes.containsKey(classId)) {
            return classes.get(classId);
        }
        ClassType classType = TypesUtil.emptyClassType();
        classes.put(classId, classType);

        assert classJSON.get("typeArgs").getAsJsonArray().size() == 0;
        assert classJSON.get("argPolarities").getAsJsonArray().size() == 0;
        assert classJSON.get("fieldTypes").getAsJsonArray().size() == 0;
        assert !classJSON.get("mixins").getAsBoolean();
        assert !classJSON.get("structural").getAsBoolean();

        for (JsonElement methodTypeRaw : classJSON.get("methodTypes").getAsJsonArray()) {
            JsonObject methodTypeJSON = methodTypeRaw.getAsJsonObject();
            String name = methodTypeJSON.get("name").getAsString();
            Type propType = parseType(methodTypeJSON.get("prop").getAsJsonObject().get("method").getAsJsonObject());
            if (name.equals("constructor")) {
                classType.getSignatures().addAll(((InterfaceType) propType).getDeclaredCallSignatures());
            } else {
                classType.getInstanceProperties().put(name, propType);
            }
        }

        return classType;
    }

    private Type parseFunctionType(JsonObject description) {
        List<Type> paramTypes = StreamSupport.stream(description.get("paramTypes").getAsJsonArray().spliterator(), false).map(elem -> {
            elem = resolve(elem.getAsJsonObject());
            if (elem.getAsJsonObject().get("kind").getAsString().equals("OptionalT")) {
                return parseType(elem.getAsJsonObject().get("type").getAsJsonObject());
            } else {
                return parseType(elem.getAsJsonObject());
            }
        }).collect(Collectors.toList());
        List<String> paramNames = StreamSupport.stream(description.get("paramNames").getAsJsonArray().spliterator(), false).map(JsonElement::getAsString).collect(Collectors.toList());

        assert description.get("restParam").isJsonNull();

        Signature signature = TypesUtil.emptySignature();
        signature.setResolvedReturnType(parseType(description.get("returnType").getAsJsonObject()));

        assert paramTypes.size() == paramNames.size();
        for (int i = 0; i < paramNames.size(); i++) {
            Type type = paramTypes.get(i);
            String name = paramNames.get(i);
            signature.getParameters().add(new Signature.Parameter(name, type));
        }

        int minArgs = 0;
        for (JsonElement type : description.get("paramTypes").getAsJsonArray()) {
            if (!type.getAsJsonObject().get("kind").getAsString().equals("OptionalT")) {
                minArgs++;
            } else {
                break;
            }
        }

        signature.setMinArgumentCount(minArgs);

        InterfaceType interfaceType = SpecReader.makeEmptySyntheticInterfaceType();

        interfaceType.getDeclaredCallSignatures().add(signature);

        return interfaceType;
    }

    private static String getNameFromModule(JsonObject typeDescription) {
        String moduleDescriptor = typeDescription.get("reasons").getAsJsonArray().get(0).getAsJsonObject().get("desc").getAsString();
        moduleDescriptor = Util.removePrefix(moduleDescriptor, "module ");
        return moduleDescriptor;
    }
}
