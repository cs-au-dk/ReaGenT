package dk.webbies.tajscheck.testcreator;

import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.ParameterMap;
import dk.webbies.tajscheck.TypeWithParameters;
import dk.webbies.tajscheck.TypesUtil;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.buildprogram.TestProgramBuilder;
import dk.webbies.tajscheck.paser.AstBuilder;
import dk.webbies.tajscheck.testcreator.test.*;
import dk.webbies.tajscheck.testcreator.test.check.Check;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.testcreator.test.check.Check.*;

/**
 * Created by erik1 on 01-11-2016.
 */
public class TestCreator {

    public static List<Test> createTests(Set<Type> nativeTypes, Type typeToTest, Benchmark bench, TestProgramBuilder.TypeParameterIndexer typeParameterIndexer) {
        String module = bench.module;

        PriorityQueue<CreateTestQueueElement> queue = new PriorityQueue<>();

        Set<TypeWithParameters> negativeTypesSeen = new HashSet<>();

        CreateTestVisitor visitor = new CreateTestVisitor(nativeTypes, queue, negativeTypesSeen);

        List<Test> topLevelFunctionTests = addTopLevelFunctionTests(typeToTest, module, queue, new ParameterMap(), visitor, negativeTypesSeen, typeParameterIndexer, nativeTypes);

        queue.add(new CreateTestQueueElement(typeToTest, new Arg(module, new ParameterMap(), 0)));

        while (!queue.isEmpty()) {
            CreateTestQueueElement element = queue.poll();
            if (element.arg.withTopLevelFunctions) {
                addTopLevelFunctionTests(element.type, element.arg.path, queue, element.arg.parameterMap, visitor, negativeTypesSeen, typeParameterIndexer, nativeTypes);
            }
            element.type.accept(visitor, element.arg.noTopLevelFunctions());
        }


        Collection<Test> tests = visitor.getTests();

        if (bench.pathsToTest != null) {
            tests = tests.stream().filter(test ->
                bench.pathsToTest.stream().anyMatch(path -> path.startsWith(test.getPath()))
            ).collect(Collectors.toList());
        }
        return concatDuplicateTests(Util.concat(topLevelFunctionTests, tests));
    }

    private static List<Test> addTopLevelFunctionTests(Type type, String path, PriorityQueue<CreateTestQueueElement> queue, ParameterMap parameterMap, CreateTestVisitor visitor, Set<TypeWithParameters> negativeTypesSeen, TestProgramBuilder.TypeParameterIndexer typeParameterIndexer, Set<Type> nativeTypes) {
        if (nativeTypes.contains(type)) {
            return new ArrayList<>();
        }
        List<Test> topLevelFunctionTests = new ArrayList<>();

        assert type instanceof GenericType || type instanceof InterfaceType || type instanceof SimpleType || type instanceof ReferenceType || type instanceof UnionType || type instanceof TypeParameterType;

        if (type instanceof TypeParameterType) {
            List<Test> result = new ArrayList<>();
            TypeParameterType typeParameterType = (TypeParameterType) type;
            if (typeParameterType.getConstraint() != null) {
                result.addAll(addTopLevelFunctionTests(((TypeParameterType) type).getConstraint(), path, queue, parameterMap, visitor, negativeTypesSeen, typeParameterIndexer, nativeTypes));
            }
            List<Type> recursiveDefinition = TypesUtil.findRecursiveDefinition(typeParameterType, parameterMap, typeParameterIndexer);
            if (!recursiveDefinition.isEmpty()) {
                for (Type subType : recursiveDefinition) {
                    result.addAll(addTopLevelFunctionTests(subType, path, queue, parameterMap, visitor, negativeTypesSeen, typeParameterIndexer, nativeTypes));
                }
                return result;

            }
            if (parameterMap.containsKey(typeParameterType)) {
                result.addAll(addTopLevelFunctionTests(parameterMap.get(typeParameterType), path, queue, parameterMap, visitor, negativeTypesSeen, typeParameterIndexer, nativeTypes));
                return result;
            }
        }


        if (type instanceof ReferenceType) {
            ParameterMap newParameters = TypesUtil.generateParameterMap((ReferenceType) type);
            type = ((ReferenceType) type).getTarget();
            path = path + ".<>";
            parameterMap = parameterMap.append(newParameters);
        }

        if (type instanceof GenericType) {
            type = ((GenericType) type).toInterface();
        }

        if (type instanceof InterfaceType) {
            List<Signature> callSignatures = ((InterfaceType) type).getDeclaredCallSignatures();
            for (Signature callSignature : callSignatures) {
                List<Type> parameters = callSignature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                findPositiveTypesInParameters(visitor, new Arg(path, parameterMap, 0), parameters, negativeTypesSeen);
                topLevelFunctionTests.add(
                        new FunctionCallTest(type, parameters, callSignature.getResolvedReturnType(), path, new ParameterMap())
                );

                queue.add(new CreateTestQueueElement(callSignature.getResolvedReturnType(), new Arg(path + "()", parameterMap, 0)));
            }

            List<Signature> constructSignatures = ((InterfaceType) type).getDeclaredConstructSignatures();
            for (Signature constructSignature : constructSignatures) {
                List<Type> parameters = constructSignature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                findPositiveTypesInParameters(visitor, new Arg(path, parameterMap, 0), parameters, negativeTypesSeen);
                topLevelFunctionTests.add(
                        new ConstructorCallTest(type, parameters, constructSignature.getResolvedReturnType(), path, new ParameterMap())
                );

                queue.add(new CreateTestQueueElement(constructSignature.getResolvedReturnType(), new Arg("new " + path + "()", new ParameterMap(), 0)));
            }
        }
        return topLevelFunctionTests;
    }

    private static void findPositiveTypesInParameters(CreateTestVisitor visitor, Arg arg, List<Type> parameters, Set<TypeWithParameters> negativeTypesSeen) {
        for (int i = 0; i < parameters.size(); i++) {
            Type parameter = parameters.get(i);
            findPositiveTypes(visitor, parameter, arg.append("[arg" + i + "]"), negativeTypesSeen);
        }
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
        private final ParameterMap parameterMap;
        private final int depth;
        private final boolean withTopLevelFunctions;

        private Arg(String path, ParameterMap parameterMap, int depth) {
            this(path, parameterMap, depth, false);
        }

        private Arg(String path, ParameterMap parameterMap, int depth, boolean withTopLevelFunctions) {
            this.path = path;
            this.parameterMap = parameterMap;
            this.depth = depth;
            this.withTopLevelFunctions = withTopLevelFunctions;
        }

        private Arg append(String path) {
            return new Arg(this.path + "." + path, parameterMap, depth + 1);
        }

        public ParameterMap getParameterMap() {
            return parameterMap;
        }

        private Arg withParameters(ParameterMap newParameters) {
            return new Arg(this.path, this.parameterMap.append(newParameters), depth);
        }

        public Arg withTopLevelFunctions() {
            return new Arg(this.path, this.parameterMap, this.depth, true);
        }

        public Arg noTopLevelFunctions() {
            return new Arg(this.path, this.parameterMap, this.depth, false);
        }
    }

    private static final class CreateTestQueueElement implements Comparable<CreateTestQueueElement> {
        private final Type type;
        private final Arg arg;

        private CreateTestQueueElement(Type t, Arg arg) {
            this.type = t;
            this.arg = arg;
        }

        @Override
        public int compareTo(CreateTestQueueElement o) {
            return Integer.compare(o.arg.depth, o.arg.depth);
        }
    }



    private static final class CreateTestVisitor implements TypeVisitorWithArgument<Void, Arg> {
        private final Set<TypeWithParameters> seen = new HashSet<>();
        private final Set<Type> nativeTypes;
        private final List<Test> tests = new ArrayList<>();
        private final PriorityQueue<CreateTestQueueElement> queue;
        private Set<TypeWithParameters> negativeTypesSeen;

        private CreateTestVisitor(Set<Type> nativeTypes, PriorityQueue<CreateTestQueueElement> queue, Set<TypeWithParameters> negativeTypesSeen) {
            this.nativeTypes = nativeTypes;
            this.queue = queue;
            this.negativeTypesSeen = negativeTypesSeen;
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

            recurse(result, arg);
            return null;
        }


        @Override
        public Void visit(InterfaceType t, Arg arg) {
            TypeWithParameters withParameters = new TypeWithParameters(t, arg.getParameterMap());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            t.getBaseTypes().forEach(base -> recurse(base, arg));

            assert t.getDeclaredStringIndexType() == null;
            assert t.getDeclaredNumberIndexType() == null;

            for (Map.Entry<String, Type> entry : t.getDeclaredProperties().entrySet()) {
                String key = entry.getKey();
                Type type = entry.getValue();

                tests.add(new MemberAccessTest(t, type, key, arg.path, arg.getParameterMap()));

                // TODO: Functions with lower-minArgs.
                if (type instanceof InterfaceType || type instanceof GenericType) {
                    List<Signature> callSignatures = type instanceof InterfaceType ? ((InterfaceType) type).getDeclaredCallSignatures() : ((GenericType) type).getDeclaredCallSignatures();
                    for (Signature signature : callSignatures) {
                        List<Type> parameters = signature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                        findPositiveTypesInParameters(this, arg.append(key), parameters, this.negativeTypesSeen);
                        tests.add(new MethodCallTest(t, type, key, parameters, signature.getResolvedReturnType(), arg.append(key).path, arg.getParameterMap()));

                        recurse(signature.getResolvedReturnType(), arg.append(key + "()"));
                    }

                    List<Signature> constructSignatures = type instanceof InterfaceType ? ((InterfaceType) type).getDeclaredConstructSignatures() : ((GenericType) type).getDeclaredConstructSignatures();
                    for (Signature signature : constructSignatures) {
                        List<Type> parameters = signature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                        findPositiveTypesInParameters(this, arg.append(key), parameters, this.negativeTypesSeen);
                        tests.add(new ConstructorCallTest(type, parameters, signature.getResolvedReturnType(), arg.append(key).path, arg.getParameterMap()));

                        recurse(signature.getResolvedReturnType(), arg.append(key + "[new]()"));
                    }
                }

                recurse(type, arg.append(key));
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

            ParameterMap newParameters = TypesUtil.generateParameterMap(t);

            recurse(t.getTarget(), arg.append("<>").withParameters(newParameters));

            return null;
        }

        private void recurse(Type type, Arg arg) {
            queue.add(new CreateTestQueueElement(type, arg));
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
                        case Null:
                            return true;
                        default:
                            throw new RuntimeException("Kind: " + ((SimpleType) type).getKind());
                    }
                } else if (type instanceof BooleanLiteral || type instanceof NumberLiteral || type instanceof StringLiteral) {
                    return false;
                } else if (type instanceof InterfaceType || type instanceof GenericType ||type instanceof ReferenceType || type instanceof TypeParameterType){
                    return true;
                } else {
                    throw new RuntimeException(type.getClass().getName());
                }
            }).collect(Collectors.toList());

            if (elements.size() == 0) {
                return null;
            }

            // undefined | Type
            if (elements.size() == 2 && elements.get(0) instanceof SimpleType && (((SimpleType) elements.get(0)).getKind() == SimpleTypeKind.Undefined || ((SimpleType) elements.get(0)).getKind() == SimpleTypeKind.Null)) {
                Type type = elements.get(1);
                assert type instanceof InterfaceType || type instanceof GenericType || type instanceof ReferenceType || type instanceof TypeParameterType;
                Check check = not(or(
                        typeOf("undefined"),
                        equalTo(AstBuilder.nullLiteral())
                ));
                tests.add(new FilterTest(union, type, arg.path, arg.getParameterMap(), check));

                recurse(type, arg);
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
                recurse(arg.getParameterMap().get(t), arg);
            }

            assert t.getTarget() == null;

            if (t.getConstraint() != null) {
                tests.add(new FilterTest(t, t.getConstraint(), arg.path, arg.getParameterMap(), Check.trueCheck()));
                recurse(t.getConstraint(), arg.append("[constraint]"));
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
                tests.add(new FilterTest(t, subType, arg.path, arg.getParameterMap(), Check.trueCheck()));
                recurse(subType, arg);
            }

            return null;
        }

        public Collection<Test> getTests() {
            return tests;
        }
    }

    private static void findPositiveTypes(CreateTestVisitor visitor, Type type, Arg arg, Set<TypeWithParameters> negativeTypesSeen) {
        type.accept(new FindPositiveTypesVisitor(visitor, negativeTypesSeen), arg);
    }

    private static class FindPositiveTypesVisitor implements TypeVisitorWithArgument<Void, Arg> {
        private CreateTestVisitor visitor;
        private Set<TypeWithParameters> negativeTypesSeen;

        public FindPositiveTypesVisitor(CreateTestVisitor createTestVisitor, Set<TypeWithParameters> negativeTypesSeen) {
            this.visitor = createTestVisitor;
            this.negativeTypesSeen = negativeTypesSeen;
        }

        @Override
        public Void visit(AnonymousType t, Arg arg) {
            return null;
        }

        @Override
        public Void visit(ClassType t, Arg arg) {
            throw new RuntimeException();
        }

        @Override
        public Void visit(GenericType t, Arg arg) {
            t.toInterface().accept(this, arg);
            return null;
        }

        @Override
        public Void visit(InterfaceType t, Arg arg) {
            if (negativeTypesSeen.contains(new TypeWithParameters(t, arg.getParameterMap()))) {
                return null;
            }
            negativeTypesSeen.add(new TypeWithParameters(t, arg.getParameterMap()));

            for (Signature signature : Util.concat(t.getDeclaredCallSignatures(), t.getDeclaredConstructSignatures())) {
                for (int i = 0; i < signature.getParameters().size(); i++) {
                    Signature.Parameter parameter = signature.getParameters().get(i);
                    visitor.recurse(parameter.getType(), arg.append("[arg" + i + "]").withTopLevelFunctions());
                    signature.getResolvedReturnType().accept(this, arg.append("()"));
                }
            }

            for (Type baseType : t.getBaseTypes()) {
                baseType.accept(this, arg);
            }

            if (t.getDeclaredStringIndexType() != null) {
                t.getDeclaredStringIndexType().accept(this, arg.append("[stringIndexer]"));
            }
            if (t.getDeclaredNumberIndexType() != null) {
                t.getDeclaredNumberIndexType().accept(this, arg.append("[numberIndexer"));
            }
            for (Map.Entry<String, Type> entry : t.getDeclaredProperties().entrySet()) {
                entry.getValue().accept(this, arg.append(entry.getKey()));
            }

            return null;
        }

        @Override
        public Void visit(ReferenceType t, Arg arg) {
            ParameterMap newParameters = TypesUtil.generateParameterMap(t);

            t.getTarget().accept(this, arg.append("<>").withParameters(newParameters));

            return null;
        }

        @Override
        public Void visit(SimpleType t, Arg arg) {
            return null;
        }

        @Override
        public Void visit(TupleType t, Arg arg) {
            throw new RuntimeException();
        }

        @Override
        public Void visit(UnionType union, Arg arg) {
            for (int i = 0; i < union.getElements().size(); i++) {
                Type type = union.getElements().get(i);
                type.accept(this, arg.append("[union" + i + "]"));
            }
            return null;
        }

        @Override
        public Void visit(UnresolvedType t, Arg arg) {
            throw new RuntimeException();
        }

        @Override
        public Void visit(TypeParameterType t, Arg arg) {
            if (negativeTypesSeen.contains(new TypeWithParameters(t, arg.getParameterMap()))) {
                return null;
            }
            negativeTypesSeen.add(new TypeWithParameters(t, arg.getParameterMap()));

            if (arg.getParameterMap().containsKey(t)) {
                arg.getParameterMap().get(t).accept(this, arg);
            }

            assert t.getTarget() == null;

            if (t.getConstraint() != null) {
                t.getConstraint().accept(this, arg.append("[constraint]"));
            }
            return null;
        }

        @Override
        public Void visit(SymbolType t, Arg arg) {
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
        public Void visit(IntersectionType intersection, Arg arg) {
            for (int i = 0; i < intersection.getElements().size(); i++) {
                Type type = intersection.getElements().get(i);
                type.accept(this, arg.append("[intersection" + i + "]"));
            }
            return null;
        }
    }
}
