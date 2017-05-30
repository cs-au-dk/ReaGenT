package dk.webbies.tajscheck.buildprogram;

import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.paser.AST.Expression;
import dk.webbies.tajscheck.paser.AST.ObjectLiteral;
import dk.webbies.tajscheck.paser.AST.Operator;
import dk.webbies.tajscheck.paser.AST.Statement;
import dk.webbies.tajscheck.paser.AstBuilder;
import dk.webbies.tajscheck.typeutil.RecursiveTypeVisitor;

import java.util.*;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.paser.AstBuilder.*;

public class ProxyBuilder {

    private final static String proxyHandlerFunction = "proxyHandler";
    private final static String checkPropertyFunction = "checkProperty";
    private final static String knownObjectPropertiesObject = "knownObjectProperties";
    private final static String unknownFieldAccessFunction = "unknownFieldAccess";
    private final static String accessCountersObject = "accessCountersObject";

    private final InterfaceType global;

    private final List<Statement> program;

    private final Map<Type, Collection<String>> properties;

    private final Map<Type, String> typeNames;

    private final Map<Type, String> assignedNames = new HashMap<>();

    private final Statement knownProperyObjectCreation;

    ProxyBuilder(List<Statement> program, BenchmarkInfo info) {

        this.global = (InterfaceType) info.getSpec().getGlobal();
        this.typeNames = info.typeNames;
        this.program = program;

        // we extract all properties from all known types
        DeclaredPropertyExtractor dep = new DeclaredPropertyExtractor();
        global.accept(dep);
        this.properties = dep.getProperties();


        // knownObjectProperties = {}
        this.knownProperyObjectCreation = variable(knownObjectPropertiesObject, object());
        program.add(knownProperyObjectCreation);

        // accessCounters = {good: 0, bad: 0}
        program.add(variable(accessCountersObject, object(new ObjectLiteral.Property("good", number(0)), new ObjectLiteral.Property("bad", number(0)))));

        // function that check whether a property is known for an object
        program.add(
                (statement(function(checkPropertyFunction,
                        stmtFromString(String.join("\n",
                                "var possible = false;",
                                "for(var obj in publicApiObjects) {",
                                "    switch(publicApiObjects[obj]) {",
                                "        case 'Any':",
                                "            possible = true;",
                                "            break;",
                                "        case 'String':",
                                "            if(String.prototype.hasOwnProperty(property)) possible = true;",
                                "            break;",
                                "        case 'Number':",
                                "            if(Number.prototype.hasOwnProperty(property)) possible = true;",
                                "            break;",
                                "        case 'Boolean':",
                                "            if(Boolean.prototype.hasOwnProperty(property)) possible = true;",
                                "            break;",
                                "        case 'Symbol':",
                                "            if(Symbol.prototype.hasOwnProperty(property)) possible = true;",
                                "            break;",
                                "        case 'Enum':",
                                "        case 'Void':",
                                "        case 'Undefined':",
                                "        case 'Null':",
                                "        case 'Never':",
                                "            break;",
                                "        case 'Object':",
                                "            if(Object.prototype.hasOwnProperty(property)) possible = true;",
                                "            break;",
                                "        default:",
                                "            if(" + knownObjectPropertiesObject + "[publicApiObjects[obj]].indexOf(property) >= 0) {",
                                "                possible = true;",
                                "            }",
                                "    }",
                                "}",
                                "return possible;"
                        )),
                        "property", "publicApiObjects")
                )
                ));

        program.add(
                (statement(function(unknownFieldAccessFunction,
                        stmtFromString(String.join("\n",
                                "if(!known) { " + accessCountersObject + ".bad++; error(message() + ' good: ' + " + accessCountersObject + ".good + ' bad: ' + " + accessCountersObject + ".bad); } else {" + accessCountersObject + ".good++; }"
                        )),
                        "message", "known")
                )
                ));

        // function to create proxy for objects
        program.add(
                (statement(function(proxyHandlerFunction,
                        block(
                                ifThenElse(
                                        binary(
                                                binary(unary(Operator.TYPEOF, identifier("target")), Operator.EQUAL_EQUAL_EQUAL, string("object")),
                                                Operator.OR,
                                                binary(unary(Operator.TYPEOF, identifier("target")), Operator.EQUAL_EQUAL_EQUAL, string("function"))
                                        ),
                                        Return(newCall(
                                                identifier("Proxy"),
                                                identifier("target"),
                                                object(
                                                        //PgetPrototypeOf(),
                                                        //PsetPrototypeOf(),
                                                        //PisExtensible(),
                                                        //PpreventExtensions(),
                                                        //PgetOwnPropertyDescriptor(),
                                                        PdefineProperty(),
                                                        Phas(),
                                                        Pget(),
                                                        Pset(),
                                                        PdeleteProperty()
                                                        //PownKeys(),
                                                        //Papply(),
                                                        //Pconstruct()
                                                ))),
                                        // else
                                        Return(identifier("target"))
                                )),
                        "target", "publicApiObjects")
                )
                ));
    }

    public ValueTransformer transformer() {
        return new ProxyValueTransformer();
    }

    private static final String NOT_AVAILABLE_ON = ", not available on";


    public static List<String> extractFields(List<String> errors) {
        return errors.stream().map(error -> {
            if (error.startsWith(ACCESSING_UNKNOWN_PROPERTY)) {
                return error.substring(ACCESSING_UNKNOWN_PROPERTY.length(), error.indexOf(NOT_AVAILABLE_ON));
            } else if (error.startsWith(SETTING_UNKNOWN_PROPERTY)) {
                return error.substring(SETTING_UNKNOWN_PROPERTY.length(), error.indexOf(NOT_AVAILABLE_ON));
            } else if (error.startsWith(CHECKING_PRESENCE_OF_UNKNOWN_PROPERTY)) {
                return error.substring(CHECKING_PRESENCE_OF_UNKNOWN_PROPERTY.length(), error.indexOf(NOT_AVAILABLE_ON));
            } else if (error.startsWith(DELETING_UNKOWN_PROPERTY)) {
                return error.substring(DELETING_UNKOWN_PROPERTY.length(), error.indexOf(NOT_AVAILABLE_ON));
            }
            throw new RuntimeException();
        }).collect(Collectors.toList());
    }


    private class ProxyValueTransformer implements ValueTransformer {

        public Expression transform(Expression target, Type proxiedType) {
            return call(
                    identifier(proxyHandlerFunction),
                    target,
                    array(extractFlattendedTypes(proxiedType).stream()
                            .map(x -> string(findNiceTypeName(x)))
                            .collect(Collectors.toList()))
            );
        }
    }

    /*
     *   Builds a trap for Object.getPrototypeOf.
     */
    protected ObjectLiteral.Property PgetPrototypeOf() {
        return new ObjectLiteral.Property("getPrototypeOf", function(block(), "target"));
    }

    /*
     *   Builds a trap for Object.setPrototypeOf.
     */
    protected ObjectLiteral.Property PsetPrototypeOf() {
        return new ObjectLiteral.Property("setPrototypeOf", function(block(), "target", "prototype"));
    }

    /*
     *   Builds a trap for Object.isExtensible.
     */
    protected ObjectLiteral.Property PisExtensible() {
        return new ObjectLiteral.Property("isExtensible", function(block(), "target"));
    }

    /*
     *   Builds a trap for Object.preventExtensions.
     */
    protected ObjectLiteral.Property PpreventExtensions() {
        return new ObjectLiteral.Property("preventExtensions", function(block(), "target"));
    }

    /*
     *   Builds a trap for Object.getOwnPropertyDescriptor.
     */
    protected ObjectLiteral.Property PgetOwnPropertyDescriptor() {
        return new ObjectLiteral.Property("getOwnPropertyDescriptor", function(block(), "target", "property"));
    }

    public static final String monitorUnknownPropAccessErrorPrefix = "monitorUnknown: ";

    public static List<String> filterErrors(List<String> errors) {
        List<String> result = new ArrayList<>();
        for (String error : errors) {
            if (error.startsWith(monitorUnknownPropAccessErrorPrefix)) {
                result.add(error.substring(monitorUnknownPropAccessErrorPrefix.length(), error.length()));
            }
        }
        return result;
    }

    public static int getGoodCount(String error) {
        String substring = error.substring(error.indexOf("good: ") + 6, error.length()).trim();
        return Integer.parseInt(substring.substring(0, substring.indexOf(" ")));
    }

    public static int getBadCount(String error) {
        return Integer.parseInt(error.substring(error.indexOf("bad: ") + 5, error.length()).trim());
    }

    private static final String CHECKING_PRESENCE_OF_UNKNOWN_PROPERTY = "Checking presence of unknown property: ";

    /*
     *   Builds a trap for Object.defineProperty.
     */
    protected ObjectLiteral.Property PdefineProperty() {
        return new ObjectLiteral.Property("defineProperty", function(stmtFromString(
                unknownFieldAccessFunction + "(function(){return '" + monitorUnknownPropAccessErrorPrefix + CHECKING_PRESENCE_OF_UNKNOWN_PROPERTY + "' + property + ', not available on ' + publicApiObjects.join(',')}, checkProperty(property, publicApiObjects)); target.defineProperty(property, descriptor); return true;"
        ), "target", "property", "descriptor"));
    }

    /*
     *   Builds a trap for the in operator.
     */
    protected ObjectLiteral.Property Phas() {
        return new ObjectLiteral.Property("has", function(
                stmtFromString(
                        unknownFieldAccessFunction + "(function(){return '" + monitorUnknownPropAccessErrorPrefix + CHECKING_PRESENCE_OF_UNKNOWN_PROPERTY + "' + property + ', not available on ' + publicApiObjects.join(',')}, checkProperty(property, publicApiObjects)); return target.hasOwnProperty(property);"
                ),
                "target", "property"));
    }

    private static final String ACCESSING_UNKNOWN_PROPERTY = "Accessing unknown property: ";

    /*
     *   Builds a trap for getting property values.
     */
    protected ObjectLiteral.Property Pget() {
        return new ObjectLiteral.Property("get", function(
                stmtFromString(
                        unknownFieldAccessFunction + "(function(){return '" + monitorUnknownPropAccessErrorPrefix + ACCESSING_UNKNOWN_PROPERTY + "' + property + ', not available on ' + publicApiObjects.join(',')}, checkProperty(property, publicApiObjects)); return target[property];"
                ),
                "target", "property", "receiver"));
    }

    private static final String SETTING_UNKNOWN_PROPERTY = "Setting unknown property: ";

    /*
     *   Builds a trap for setting property values.
     */
    protected ObjectLiteral.Property Pset() {
        return new ObjectLiteral.Property("set", function(
                stmtFromString(
                        unknownFieldAccessFunction + "(function(){return '" + monitorUnknownPropAccessErrorPrefix + SETTING_UNKNOWN_PROPERTY + "' + property + ', not available on ' + publicApiObjects.join(',')}, checkProperty(property, publicApiObjects)); target[property] = value; return true;"
                ), "target", "property", "value", "receiver"));
    }

    private static final String DELETING_UNKOWN_PROPERTY = "Deleting unknown property: ";

    /*
     *   Builds a trap for the delete operator.
     */
    protected ObjectLiteral.Property PdeleteProperty() {
        return new ObjectLiteral.Property("deleteProperty", function(
                stmtFromString(
                        unknownFieldAccessFunction + "(function(){return '" + monitorUnknownPropAccessErrorPrefix + DELETING_UNKOWN_PROPERTY + "' + property + ', not available on ' + publicApiObjects.join(',')}, checkProperty(property, publicApiObjects)); delete target[property]; return true;"
                ), "target", "property"));
    }

    /*
     *   Builds a trap for Object.getOwnPropertyNames and Object.getOwnPropertySymbols.
     */
    protected ObjectLiteral.Property PownKeys() {
        return new ObjectLiteral.Property("ownKeys", function(
                block(), "target")); //FIXME: Probably we should print a warning if there are types that are not in the public interface
    }

    /*
     *   Builds a trap for a function call.
     */
    protected ObjectLiteral.Property Papply() {
        return new ObjectLiteral.Property("apply", function(block(), "target", "thisArgs", "argumentsList"));
    }

    /*
     *   Builds a trap for the new operator.
     */
    protected ObjectLiteral.Property Pconstruct() {
        return new ObjectLiteral.Property("construct", function(block(), "target", "argumentsList", "newTarget"));
    }

    private List<Type> extractFlattendedTypes(Type t) {
        FlattenedTypesExtractor ipe = new FlattenedTypesExtractor();
        t.accept(ipe);
        //if(ipe.types.isEmpty()) System.out.println("For " + t + " we have: " + ipe.types.stream().collect(Collectors.toList()));
        return ipe.types.stream().collect(Collectors.toList());
    }

    private String findNiceTypeName(Type t) {
        if (t instanceof SimpleType) return ((SimpleType) t).getKind().name();
        if (t.equals(global.getDeclaredProperties().get("Object"))) return "Object";

        if (!assignedNames.containsKey(t)) {
            String typeName = (typeNames.containsKey(t) ? typeNames.get(t) : "T") + "_" + assignedNames.size();
            assignedNames.put(t, typeName); //FIXME use something mnemonic
            List<Expression> objectProperties = properties.containsKey(t) ? properties.get(t).stream().map(AstBuilder::string).collect(Collectors.toList()) : new ArrayList<>();

            // lazily update the program with the properties
            program.add(program.indexOf(knownProperyObjectCreation) + 1,
                    statement(binary(
                            arrayAccess(identifier(knownObjectPropertiesObject), string(typeName)),
                            Operator.EQUAL,
                            array(objectProperties))
                    ));
        }
        return assignedNames.get(t);
    }

    private class FlattenedTypesExtractor implements TypeVisitor<Void> {
        private final Set<Type> types = new HashSet<>();


        @Override
        public Void visit(ClassType t) {
            types.add(global.getDeclaredProperties().get("Object"));
            types.add(t);
            t.getBaseTypes().forEach(base -> base.accept(this));
            return null;
        }

        @Override
        public Void visit(InterfaceType t) {
            types.add(global.getDeclaredProperties().get("Object"));
            types.add(t);
            t.getBaseTypes().forEach(base -> base.accept(this));
            return null;
        }

        @Override
        public Void visit(AnonymousType t) {
            return null;
        }

        @Override
        public Void visit(GenericType t) {
            types.add(global.getDeclaredProperties().get("Object"));
            types.add(t);
            t.getBaseTypes().forEach(base -> base.accept(this));
            return null;
        }

        @Override
        public Void visit(ReferenceType t) {
            types.add(global.getDeclaredProperties().get("Object"));
            types.add(t);
            t.getTarget().accept(this);
            return null;
        }

        @Override
        public Void visit(SimpleType t) {
            InterfaceType inter = (InterfaceType) global.getDeclaredProperties().get(t.getKind().name());
            if (inter != null) {
                return inter.getDeclaredProperties().get("prototype").accept(this);
            } else {
                return null;
            }
        }

        @Override
        public Void visit(TupleType t) {
            types.add(global.getDeclaredProperties().get("Array"));
            return null;
        }

        @Override
        public Void visit(UnionType t) {
            t.getElements().forEach(els -> els.accept(this));
            return null;
        }

        @Override
        public Void visit(TypeParameterType t) {
            return null;
        }

        @Override
        public Void visit(StringLiteral t) {
            types.add(new SimpleType(SimpleTypeKind.String));
            return null;
        }

        @Override
        public Void visit(BooleanLiteral t) {
            types.add(new SimpleType(SimpleTypeKind.Boolean));
            return null;
        }

        @Override
        public Void visit(NumberLiteral t) {
            types.add(new SimpleType(SimpleTypeKind.Number));
            return null;
        }

        @Override
        public Void visit(IntersectionType t) {
            // behaving as for union
            t.getElements().forEach(els -> els.accept(this));
            return null;
        }

        @Override
        public Void visit(ClassInstanceType t) {
            return ((ClassType) t.getClassType()).getInstanceType().accept(this);
        }

        @Override
        public Void visit(ThisType t) {
            return null;
        }

        @Override
        public Void visit(IndexType t) {
            throw new RuntimeException();
        }

        @Override
        public Void visit(IndexedAccessType t) {
            throw new RuntimeException();
        }
    }


    private static class DeclaredPropertyExtractor extends RecursiveTypeVisitor<Void> {
        private final Map<Type, Collection<String>> properties = new HashMap<>();
        private final Set<Type> visited = new HashSet<>();

        Map<Type, Collection<String>> getProperties() {
            return properties;
        }

        @Override
        public Void visit(ClassType t) {
            if (visited.contains(t)) return null;
            else visited.add(t);
            super.visit(t);
            ArrayList<String> props = new ArrayList<>(t.getStaticProperties().keySet());
            properties.put(t, props);
            t.getBaseTypes().forEach(base -> base.accept(this));
            t.getBaseTypes().stream().map(properties::get).filter(Objects::nonNull).forEach(props::addAll);
            return null;
        }

        @Override
        public Void visit(InterfaceType t) {
            if (visited.contains(t)) return null;
            else visited.add(t);
            super.visit(t);
            ArrayList<String> props = new ArrayList<>(t.getDeclaredProperties().keySet());
            properties.put(t, props);
            t.getBaseTypes().forEach(base -> base.accept(this));
            t.getBaseTypes().stream().map(properties::get).filter(Objects::nonNull).forEach(props::addAll);
            return null;
        }

        @Override
        public Void visit(ReferenceType t) {
            if (visited.contains(t)) return null;
            else visited.add(t);
            super.visit(t);
            t.getTarget().accept(this);
            if (properties.containsKey(t.getTarget())) {
                properties.put(t, properties.get(t.getTarget()));
            }
            return null;
        }

        @Override
        public Void visit(GenericType t) {
            if (visited.contains(t)) return null;
            else visited.add(t);
            super.visit(t);
            ArrayList<String> props = new ArrayList<>(t.getDeclaredProperties().keySet());
            properties.put(t, props);
            t.getTarget().accept(this);
            t.getBaseTypes().forEach(base -> base.accept(this));
            t.getBaseTypes().stream().map(properties::get).filter(Objects::nonNull).forEach(props::addAll);
            return null;
        }
    }

}
