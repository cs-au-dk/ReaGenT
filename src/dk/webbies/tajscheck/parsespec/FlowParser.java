package dk.webbies.tajscheck.parsespec;

import com.google.gson.*;
import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FlowParser {
    public static SpecReader parse(Collection<String> declarationFiles) {
        if (declarationFiles.isEmpty()) {
            return new SpecReader(SpecReader.makeEmptySyntheticInterfaceType(), Collections.emptyList(), Collections.emptyList(), new HashMap<>());
        }
        assert declarationFiles.size() == 1;
        final String flowTypeJSON;
        try {
            flowTypeJSON = Util.runScript("bash -c \"flow dump-types --raw " + declarationFiles.iterator().next() + "\"", 60 * 1000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<SpecReader.NamedType> namedTypes = new ArrayList<>();
        List<SpecReader.NamedType> ambientTypes = new ArrayList<>();
        Map<String, Type> globalProperties = new HashMap<>();

        FlowParser flowParser = new FlowParser();

        for (JsonElement typeDescriptionRaw : new JsonParser().parse(flowTypeJSON).getAsJsonArray()) {
            JsonObject typeDescription = typeDescriptionRaw.getAsJsonObject();
            String typeString = typeDescription.get("type").getAsString();
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
                case "[type: string]":
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
            default:
                throw new RuntimeException(kind);
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
