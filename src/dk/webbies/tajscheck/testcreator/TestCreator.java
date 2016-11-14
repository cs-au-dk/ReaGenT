package dk.webbies.tajscheck.testcreator;

import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.TypeWithParameters;
import dk.webbies.tajscheck.TypesUtil;
import dk.webbies.tajscheck.testcreator.Test.*;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 01-11-2016.
 */
public class TestCreator {
    public static List<Test> createTests(Set<Type> nativeTypes, Type typeToTest, String module) {
        CreateTestVisitor visitor = new CreateTestVisitor(nativeTypes);

        List<Test> topLevelFunctionTests = new ArrayList<>();

        if (typeToTest instanceof InterfaceType || typeToTest instanceof GenericType) {
            assert typeToTest instanceof InterfaceType;
//            List<Signature> callSignatures = typeToTest instanceof InterfaceType ? ((InterfaceType) typeToTest).getDeclaredCallSignatures() : ((GenericType) typeToTest).getDeclaredCallSignatures();
            List<Signature> callSignatures = ((InterfaceType) typeToTest).getDeclaredCallSignatures();
            for (Signature callSignature : callSignatures) {
                List<Type> parameters = callSignature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                topLevelFunctionTests.add(
                        new FunctionCallTest(typeToTest, parameters, callSignature.getResolvedReturnType(), module, new HashMap<>())
                );

                callSignature.getResolvedReturnType().accept(visitor, new Arg(module + "()", new HashMap<>()));
            }

//            List<Signature> constructSignatures = typeToTest instanceof InterfaceType ? ((InterfaceType) typeToTest).getDeclaredConstructSignatures() : ((GenericType) typeToTest).getDeclaredConstructSignatures();
            List<Signature> constructSignatures = ((InterfaceType) typeToTest).getDeclaredConstructSignatures();
            for (Signature constructSignature : constructSignatures) {
                List<Type> parameters = constructSignature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                topLevelFunctionTests.add(
                        new ConstructorCallTest(typeToTest, parameters, constructSignature.getResolvedReturnType(), module, new HashMap<>())
                );

                constructSignature.getResolvedReturnType().accept(visitor, new Arg("new " + module + "()", new HashMap<>()));
            }
        }

        typeToTest.accept(visitor, new Arg(module, new HashMap<>()));

        return concatDuplicateTests(Util.concat(topLevelFunctionTests, visitor.getTests()));
    }

    private static final class TestNoPathContainer {
        private final Test test;

        public TestNoPathContainer(Test test) {
            this.test = test;
        }

        public Test getTest() {
            return test;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || !(o == null || getClass() != o.getClass()) && test.equalsNoPath(((TestNoPathContainer) o).test);
        }

        @Override
        public int hashCode() {
            return test.hashCodeNoPath();
        }
    }

    private static List<Test> concatDuplicateTests(List<Test> list) {
        MultiMap<TestNoPathContainer, TestNoPathContainer> multimap = new ArrayListMultiMap<>();
        list.forEach(test -> {
            TestNoPathContainer key = new TestNoPathContainer(test);
            multimap.put(key, key);
        });
        return multimap.toMap().entrySet().stream().map(entry -> {
            if (entry.getValue().size() == 1) {
                return entry.getValue().iterator().next().getTest();
            } else {
                List<Test> tests = entry.getValue().stream().map(TestNoPathContainer::getTest).collect(Collectors.toList());
                StringBuilder newPath = new StringBuilder("(");
                for (int i = 0; i < tests.size(); i++) {
                    newPath.append(tests.get(i).getPath());
                    if (i != tests.size() - 1) {
                        newPath.append(", ");
                    }
                }
                newPath.append(")");
                Test sample = tests.iterator().next();
                sample.setPath(newPath.toString());
                return sample;
            }
        }).collect(Collectors.toList());
    }

    private static final class Arg {
        private final String path;
        private final Map<TypeParameterType, Type> parameterMap;

        private Arg(String path, Map<TypeParameterType, Type> parameterMap) {
            this.path = path;
            this.parameterMap = Collections.unmodifiableMap(parameterMap);
        }

        private Arg append(String path) {
            return new Arg(this.path + "." + path, parameterMap);
        }

        public Map<TypeParameterType,Type> getParameterMap() {
            return parameterMap;
        }

        private Arg withParameters(Map<TypeParameterType, Type> newParameters) {
            HashMap<TypeParameterType, Type> map = new HashMap<>();
            map.putAll(this.parameterMap);
            map.putAll(newParameters);
            return new Arg(this.path, map);
        }
    }

    // TODO: In the Arg, remove any non-relevant Type parameters (the ones that are not reachable).
    // TODO: Use this to: 1) Insert primitives for generics in some tests and 2) remove duplicated tests.
    private static final class CreateTestVisitor implements TypeVisitorWithArgument<Void, Arg> {
        private final Set<TypeWithParameters> seen = new HashSet<>();
        private final Set<Type> nativeTypes;
        private final List<Test> tests = new ArrayList<>();

        private CreateTestVisitor(Set<Type> nativeTypes) {
            this.nativeTypes = nativeTypes;
        }

        @Override
        public Void visit(AnonymousType t, Arg arg) {
            TypeWithParameters withParameters = new TypeWithParameters(t, arg.getParameterMap());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            throw new RuntimeException();
        }

        @Override
        public Void visit(ClassType t, Arg arg) {
            TypeWithParameters withParameters = new TypeWithParameters(t, arg.getParameterMap());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            throw new RuntimeException();
        }

        @Override
        public Void visit(GenericType t, Arg arg) {
            TypeWithParameters withParameters = new TypeWithParameters(t, arg.getParameterMap());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            InterfaceType result = t.toInterface();

            result.accept(this, arg);
            return null;
        }


        @Override
        public Void visit(InterfaceType t, Arg arg) {
            TypeWithParameters withParameters = new TypeWithParameters(t, arg.getParameterMap());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            t.getBaseTypes().forEach(base -> base.accept(this, arg));

            assert t.getDeclaredStringIndexType() == null;
            assert t.getDeclaredNumberIndexType() == null;

            for (Map.Entry<String, Type> entry : t.getDeclaredProperties().entrySet()) {
                String key = entry.getKey();
                Type type = entry.getValue();

                tests.add(new MemberAccessTest(t, type, key, arg.path, arg.getParameterMap()));

                // TODO: Functions with lower-minArgs.
                // TODO: Consider doing something with positive and negative types, as in function parameters and function return. Such that arguments that the library passes to callbacks are catched, and tested.
                if (type instanceof InterfaceType || type instanceof GenericType) {
                    List<Signature> callSignatures = type instanceof InterfaceType ? ((InterfaceType) type).getDeclaredCallSignatures() : ((GenericType) type).getDeclaredCallSignatures();
                    for (Signature signature : callSignatures) {
                        List<Type> parameters = signature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                        tests.add(new MethodCallTest(t, type, key, parameters, signature.getResolvedReturnType(), arg.append(key).path, arg.getParameterMap()));

                        signature.getResolvedReturnType().accept(this, arg.append(key + "()"));
                    }

                    List<Signature> constructSignatures = type instanceof InterfaceType ? ((InterfaceType) type).getDeclaredConstructSignatures() : ((GenericType) type).getDeclaredConstructSignatures();
                    for (Signature signature : constructSignatures) {
                        List<Type> parameters = signature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                        tests.add(new ConstructorCallTest(type, parameters, signature.getResolvedReturnType(), arg.append(key).path, arg.getParameterMap()));

                        signature.getResolvedReturnType().accept(this, arg.append(key + "()"));
                    }
                }

                type.accept(this, arg.append(key));
            }


            return null;
        }

        @Override
        public Void visit(ReferenceType t, Arg arg) {
            TypeWithParameters withParameters = new TypeWithParameters(t, arg.getParameterMap());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            Map<TypeParameterType, Type> newParameters = TypesUtil.generateParameterMap(t);

            t.getTarget().accept(this, arg.append("<>").withParameters(newParameters));

            return null;
        }

        @Override
        public Void visit(SimpleType t, Arg arg) {
            TypeWithParameters withParameters = new TypeWithParameters(t, arg.getParameterMap());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            throw new RuntimeException();
        }

        @Override
        public Void visit(TupleType t, Arg arg) {
            TypeWithParameters withParameters = new TypeWithParameters(t, arg.getParameterMap());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            throw new RuntimeException();
        }

        @Override
        public Void visit(UnionType union, Arg arg) {
            TypeWithParameters withParameters = new TypeWithParameters(union, arg.getParameterMap());
            if (seen.contains(withParameters) || nativeTypes.contains(union)) {
                return null;
            }
            seen.add(withParameters);

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
                assert type instanceof InterfaceType || type instanceof GenericType;
                tests.add(new IsDefinedTest(union, type, arg.path, arg.getParameterMap()));

                type.accept(this, arg);
                return null;
            } else {
                throw new RuntimeException();
            }

        }

        @Override
        public Void visit(UnresolvedType t, Arg arg) {
            TypeWithParameters withParameters = new TypeWithParameters(t, arg.getParameterMap());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            throw new RuntimeException();
        }

        @Override
        public Void visit(TypeParameterType t, Arg arg) {
            TypeWithParameters withParameters = new TypeWithParameters(t, arg.getParameterMap());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            if (arg.getParameterMap().containsKey(t)) {
                arg.getParameterMap().get(t).accept(this, arg);
            }

            assert t.getTarget() == null;

            if (t.getConstraint() != null) {
                tests.add(new IdTest(t, t.getConstraint(), arg.path, arg.getParameterMap()));
            }

            return null;
        }

        @Override
        public Void visit(SymbolType t, Arg arg) {
            TypeWithParameters withParameters = new TypeWithParameters(t, arg.getParameterMap());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

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

        @Override
        public Void visit(IntersectionType t, Arg arg) {
            TypeWithParameters withParameters = new TypeWithParameters(t, arg.getParameterMap());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            for (Type subType : t.getElements()) {
                tests.add(new IdTest(t, subType, arg.path, arg.getParameterMap()));
                subType.accept(this, arg);
            }

            return null;
        }

        public Collection<Test> getTests() {
            return tests;
        }
    }
}
