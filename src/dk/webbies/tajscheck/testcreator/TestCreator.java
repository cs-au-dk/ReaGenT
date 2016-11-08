package dk.webbies.tajscheck.testcreator;

import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.testcreator.Test.*;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 01-11-2016.
 */
public class TestCreator {
    public static Collection<Test> createTests(Set<Type> nativeTypes, Type typeToTest, String module) {
        CreateTestVisitor visitor = new CreateTestVisitor(nativeTypes);

        List<Test> topLevelFunctionTests = new ArrayList<>();

        if (typeToTest instanceof InterfaceType || typeToTest instanceof GenericType) {
            List<Signature> callSignatures = typeToTest instanceof InterfaceType ? ((InterfaceType) typeToTest).getDeclaredCallSignatures() : ((GenericType) typeToTest).getDeclaredCallSignatures();
            for (Signature callSignature : callSignatures) {
                List<Type> parameters = callSignature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                topLevelFunctionTests.add(
                        new FunctionCallTest(typeToTest, parameters, callSignature.getResolvedReturnType(), module)
                );

                callSignature.getResolvedReturnType().accept(visitor, new Arg(module + "()"));
            }

            List<Signature> constructSignatures = typeToTest instanceof InterfaceType ? ((InterfaceType) typeToTest).getDeclaredConstructSignatures() : ((GenericType) typeToTest).getDeclaredConstructSignatures();
            for (Signature constructSignature : constructSignatures) {
                List<Type> parameters = constructSignature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                topLevelFunctionTests.add(
                        new ConstructorCallTest(typeToTest, parameters, constructSignature.getResolvedReturnType(), module)
                );

                constructSignature.getResolvedReturnType().accept(visitor, new Arg("new " + module + "()"));
            }
        }

        typeToTest.accept(visitor, new Arg(module));

        return Util.concat(topLevelFunctionTests, visitor.getTests());
    }

    private static final class Arg {
        private final String path;

        private Arg(String path) {
            this.path = path;
        }

        private Arg append(String path) {
            return new Arg(this.path + "." + path);
        }
    }

    private static final class CreateTestVisitor implements TypeVisitorWithArgument<Void, Arg> {
        private final Set<Type> seen = new HashSet<>();
        private final Set<Type> nativeTypes;
        private final List<Test> tests = new ArrayList<>();

        private CreateTestVisitor(Set<Type> nativeTypes) {
            this.nativeTypes = nativeTypes;
        }

        @Override
        public Void visit(AnonymousType t, Arg arg) {
            if (seen.contains(t) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(t);

            throw new RuntimeException();
        }

        @Override
        public Void visit(ClassType t, Arg arg) {
            if (seen.contains(t) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(t);

            throw new RuntimeException();
        }

        @Override
        public Void visit(GenericType t, Arg arg) {
            if (seen.contains(t) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(t);

            assert t.getTypeArguments().isEmpty();

            InterfaceType result = new InterfaceType();
            result.setTypeParameters(t.getTypeParameters());
            result.setBaseTypes(t.getBaseTypes());
            result.setDeclaredProperties(t.getDeclaredProperties());
            result.setDeclaredCallSignatures(t.getDeclaredCallSignatures());
            result.setDeclaredConstructSignatures(t.getDeclaredConstructSignatures());
            result.setDeclaredStringIndexType(t.getDeclaredStringIndexType());
            result.setDeclaredNumberIndexType(t.getDeclaredNumberIndexType());

            result.accept(this, arg);
            return null;
        }



        @Override
        public Void visit(InterfaceType t, Arg arg) {
            if (seen.contains(t) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(t);

            t.getBaseTypes().forEach(base -> base.accept(this, arg));

            // Handled later
            assert t.getTypeParameters().isEmpty();

            assert t.getDeclaredStringIndexType() == null;
            assert t.getDeclaredNumberIndexType() == null;

            for (Map.Entry<String, Type> entry : t.getDeclaredProperties().entrySet()) {
                String key = entry.getKey();
                Type type = entry.getValue();

                tests.add(new MemberAccessTest(t, type, key, arg.path));

                // TODO: Functions with lower-minArgs.
                if (type instanceof InterfaceType || type instanceof GenericType) {
                    List<Signature> callSignatures = type instanceof InterfaceType ? ((InterfaceType) type).getDeclaredCallSignatures() : ((GenericType) type).getDeclaredCallSignatures();
                    for (Signature signature : callSignatures) {
                        List<Type> parameters = signature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                        tests.add(new MethodCallTest(t, type, key, parameters, signature.getResolvedReturnType(), arg.append(key).path));

                        signature.getResolvedReturnType().accept(this, arg.append(key + "()"));
                    }

                    List<Signature> constructSignatures = type instanceof InterfaceType ? ((InterfaceType) type).getDeclaredConstructSignatures() : ((GenericType) type).getDeclaredConstructSignatures();
                    for (Signature signature : constructSignatures) {
                        List<Type> parameters = signature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                        tests.add(new ConstructorCallTest(type, parameters, signature.getResolvedReturnType(), arg.append(key).path));

                        signature.getResolvedReturnType().accept(this, arg.append(key + "()"));
                    }
                }

                type.accept(this, arg.append(key));
            }


            return null;
        }

        @Override
        public Void visit(ReferenceType t, Arg arg) {
            if (seen.contains(t) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(t);

            throw new RuntimeException();
        }

        @Override
        public Void visit(SimpleType t, Arg arg) {
            if (seen.contains(t) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(t);

            throw new RuntimeException();
        }

        @Override
        public Void visit(TupleType t, Arg arg) {
            if (seen.contains(t) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(t);

            throw new RuntimeException();
        }

        @Override
        public Void visit(UnionType union, Arg arg) {
            if (seen.contains(union) || nativeTypes.contains(union)) {
                return null;
            }
            seen.add(union);

            // Filtering out all primitive types, there is not test generated on those anyway.
            List<Type> elements = union.getElements().stream().filter(type -> {
                if (type instanceof SimpleType) {
                    switch (((SimpleType) type).getKind()) {
                        case String:
                        case Boolean:
                        case Number:
                            return false;
                        case Undefined:
                            return true;
                        default:
                            throw new RuntimeException("Kind: " + ((SimpleType) type).getKind());
                    }
                } else if (type instanceof BooleanLiteral || type instanceof NumberLiteral || type instanceof StringLiteral) {
                    return false;
                } else if (type instanceof InterfaceType || type instanceof GenericType ||type instanceof ReferenceType){
                    return true;
                } else {
                    throw new RuntimeException(type.getClass().getName());
                }
            }).collect(Collectors.toList());

            if (elements.size() == 0) {
                return null;
            }

            // undefined | Type
            if (elements.size() == 2 && elements.get(0) instanceof SimpleType && ((SimpleType) elements.get(0)).getKind() == SimpleTypeKind.Undefined) {
                Type type = elements.get(1);
                assert type instanceof InterfaceType;
                tests.add(new IsDefinedTest(union, type, arg.path));

                type.accept(this, arg);
                return null;
            } else {
                throw new RuntimeException();
            }

        }

        @Override
        public Void visit(UnresolvedType t, Arg arg) {
            if (seen.contains(t) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(t);

            throw new RuntimeException();
        }

        @Override
        public Void visit(TypeParameterType t, Arg arg) {
            if (seen.contains(t) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(t);

            throw new RuntimeException();
        }

        @Override
        public Void visit(SymbolType t, Arg arg) {
            if (seen.contains(t) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(t);

            throw new RuntimeException();
        }

        @Override
        public Void visit(StringLiteral t, Arg arg) {
            return null;
        }

        @Override
        public Void visit(BooleanLiteral t, Arg arg) {
            return null;
        }

        @Override
        public Void visit(NumberLiteral t, Arg arg) {
            return null;
        }

        public Collection<Test> getTests() {
            return tests;
        }
    }
}
