package dk.webbies.tajscheck.testcreator;

import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.typeutil.TypeContext;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.typeutil.TypesUtil;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.buildprogram.TestProgramBuilder.TypeParameterIndexer;
import dk.webbies.tajscheck.testcreator.test.*;
import dk.webbies.tajscheck.testcreator.test.check.Check;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 01-11-2016.
 */
public class TestCreator {

    private final Set<Type> nativeTypes;
    private Map<Type, String> typeNames;
    private final Type typeToTest;
    private final Benchmark bench;
    private final TypeParameterIndexer typeParameterIndexer;
    private Set<Type> hasThisTypes;
    private MultiMap<Type, TypeParameterType> reachableTypeParameters;

    public TestCreator(Set<Type> nativeTypes, Map<Type, String> typeNames, Type typeToTest, Benchmark bench, TypeParameterIndexer typeParameterIndexer, Set<Type> hasThisTypes, MultiMap<Type, TypeParameterType> reachableTypeParameters) {
        this.nativeTypes = nativeTypes;
        this.typeNames = typeNames;
        this.typeToTest = typeToTest;
        this.bench = bench;
        this.typeParameterIndexer = typeParameterIndexer;
        this.hasThisTypes = hasThisTypes;
        this.reachableTypeParameters = reachableTypeParameters;
    }

    public List<Test> createTests() {
        return createTests(true);
    }

    public List<Test> createTests(boolean concatDuplicates) {
        String module = bench.module;

        PriorityQueue<CreateTestQueueElement> queue = new PriorityQueue<>();

        Set<TypeWithContext> negativeTypesSeen = new HashSet<>();

        CreateTestVisitor visitor = new CreateTestVisitor(queue, negativeTypesSeen);

        List<Test> topLevelFunctionTests = new ArrayList<>();
        topLevelFunctionTests.addAll(addTopLevelFunctionTests(typeToTest, module, new TypeContext(bench), visitor, negativeTypesSeen, typeParameterIndexer, nativeTypes, 0));

        queue.add(new CreateTestQueueElement(typeToTest, new Arg(module, new TypeContext(bench), 0)));

        Set<TypeWithContext> seenTopLevel = new HashSet<>();

        while (!queue.isEmpty()) {
            CreateTestQueueElement element = queue.poll();
            Arg arg = element.arg;

            if (!bench.options.disableSizeOptimization) {
                arg = arg.replaceTypeContext(arg.typeContext.cleanTypeParameters(element.type, reachableTypeParameters));
            }


            if (arg.withTopLevelFunctions) {
                TypeWithContext withParameters = new TypeWithContext(element.type, arg.getTypeContext());
                if (!seenTopLevel.contains(withParameters)) {
                    topLevelFunctionTests.addAll(addTopLevelFunctionTests(element.type, arg.path, arg.typeContext, visitor, negativeTypesSeen, typeParameterIndexer, nativeTypes, arg.depth));
                }
                seenTopLevel.add(withParameters);

            }
            element.type.accept(visitor, arg.noTopLevelFunctions());
        }


        List<Test> tests = Util.concat(visitor.getTests(), topLevelFunctionTests);

        if (bench.pathsToTest != null) {
            tests = tests.stream().filter(test ->
                bench.pathsToTest.stream().anyMatch(path -> path.startsWith(test.getPath()))
            ).collect(Collectors.toList());
        }
        if (concatDuplicates) {
            return concatDuplicateTests(tests);
        } else {
            return tests;
        }
    }

    private List<Test> addTopLevelFunctionTests(Type type, String path, TypeContext typeContext, CreateTestVisitor visitor, Set<TypeWithContext> negativeTypesSeen, TypeParameterIndexer typeParameterIndexer, Set<Type> nativeTypes, int depth) {
        if (nativeTypes.contains(type)) {
            return new ArrayList<>();
        }

        if (type instanceof SimpleType || type instanceof StringLiteral || type instanceof NumberLiteral || type instanceof BooleanLiteral || type instanceof AnonymousType || type instanceof ClassType /* The class in classType are handled in the visitor */ || type instanceof ClassInstanceType || type instanceof TupleType || type instanceof ThisType || type instanceof SymbolType) {
            return Collections.emptyList();
        }

        if (type instanceof IndexedAccessType) {
            return Collections.emptyList();
        }

        if (type instanceof IntersectionType) {
            List<Test> result = new ArrayList<>();
            for (Type subType : ((IntersectionType) type).getElements()) {
                result.addAll(addTopLevelFunctionTests(subType, path, typeContext, visitor, negativeTypesSeen, typeParameterIndexer, nativeTypes, depth));
            }

            return result;
        }

        if (type instanceof UnionType) {
            List<Test> result = new ArrayList<>();
            List<Type> element = ((UnionType) type).getElements();
            for (int i = 0; i < element.size(); i++) {
                Type subType = element.get(i);
                result.addAll(addTopLevelFunctionTests(subType, path + ".[union" + i + "]", typeContext, visitor, negativeTypesSeen, typeParameterIndexer, nativeTypes, depth));
            }
            return result;
        }

        if (type instanceof TypeParameterType) {
            List<Test> result = new ArrayList<>();
            TypeParameterType typeParameterType = (TypeParameterType) type;
            if (typeParameterType.getConstraint() != null) {
                result.addAll(addTopLevelFunctionTests(((TypeParameterType) type).getConstraint(), path, typeContext, visitor, negativeTypesSeen, typeParameterIndexer, nativeTypes, depth));
            }
            List<Type> recursiveDefinition = TypesUtil.findRecursiveDefinition(typeParameterType, typeContext, typeParameterIndexer);
            if (!recursiveDefinition.isEmpty()) {
                for (Type subType : recursiveDefinition) {
                    result.addAll(addTopLevelFunctionTests(subType, path, typeContext, visitor, negativeTypesSeen, typeParameterIndexer, nativeTypes, depth));
                }
                return result;

            }
            if (typeContext.containsKey(typeParameterType)) {
                TypeWithContext lookup = typeContext.get(typeParameterType);
                result.addAll(addTopLevelFunctionTests(lookup.getType(), path, lookup.getTypeContext(), visitor, negativeTypesSeen, typeParameterIndexer, nativeTypes, depth));
            }
            return result;
        }


        if (type instanceof ReferenceType) {
            TypeContext newParameters = new TypesUtil(bench).generateParameterMap((ReferenceType) type);
            type = ((ReferenceType) type).getTarget();
            path = path + ".<>";
            typeContext = typeContext.append(newParameters);
            return addTopLevelFunctionTests(type, path, typeContext, visitor, negativeTypesSeen, typeParameterIndexer, nativeTypes, depth);
        }

        if (type instanceof GenericType) {
            type = ((GenericType) type).toInterface();
            return addTopLevelFunctionTests(type, path, typeContext, visitor, negativeTypesSeen, typeParameterIndexer, nativeTypes, depth);
        }

        if (type instanceof InterfaceType) {
            List<Test> result = new ArrayList<>();
            List<Signature> callSignatures = ((InterfaceType) type).getDeclaredCallSignatures();
            for (Signature callSignature : callSignatures) {
                List<Type> parameters = callSignature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                findPositiveTypesInParameters(visitor, new Arg(path, typeContext, depth), parameters, negativeTypesSeen, nativeTypes);
                result.add(
                        new FunctionCallTest(type, parameters, callSignature.getResolvedReturnType(), path, typeContext, callSignature.isHasRestParameter())
                );

                visitor.recurse(callSignature.getResolvedReturnType(), new Arg(path + "()", typeContext, depth + 1).withTopLevelFunctions());
            }

            List<Signature> constructSignatures = ((InterfaceType) type).getDeclaredConstructSignatures();
            for (Signature constructSignature : constructSignatures) {
                List<Type> parameters = constructSignature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                findPositiveTypesInParameters(visitor, new Arg(path, typeContext, depth), parameters, negativeTypesSeen, nativeTypes);
                result.add(
                        new ConstructorCallTest(type, parameters, constructSignature.getResolvedReturnType(), path, typeContext, constructSignature.isHasRestParameter())
                );

                visitor.recurse(constructSignature.getResolvedReturnType(), new Arg(path + ".new()", typeContext, depth + 1).withTopLevelFunctions());
            }
            return result;
        }
        throw new RuntimeException(type.getClass().getName());
    }

    private void findPositiveTypesInParameters(CreateTestVisitor visitor, Arg arg, List<Type> parameters, Set<TypeWithContext> negativeTypesSeen, Set<Type> nativeTypes) {
        for (int i = 0; i < parameters.size(); i++) {
            Type parameter = parameters.get(i);
            findPositiveTypes(visitor, parameter, arg.append("[arg" + i + "]"), nativeTypes);
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
        return multimap.asMap().entrySet().stream().map(entry -> {
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
        private final TypeContext typeContext;
        private final int depth;
        private final boolean withTopLevelFunctions;

        private Arg(String path, TypeContext typeContext, int depth) {
            this(path, typeContext, depth, false);
        }

        private Arg(String path, TypeContext typeContext, int depth, boolean withTopLevelFunctions) {
            this.path = path;
            this.typeContext = typeContext;
            this.depth = depth;
            this.withTopLevelFunctions = withTopLevelFunctions;
        }

        private Arg append(String path) {
            return new Arg(this.path + "." + path, typeContext, depth + 1);
        }

        public TypeContext getTypeContext() {
            return typeContext;
        }

        private Arg withParameters(TypeContext newParameters) {
            return new Arg(this.path, this.typeContext.append(newParameters), depth);
        }

        public Arg withTopLevelFunctions() {
            return new Arg(this.path, this.typeContext, this.depth, true);
        }

        public Arg noTopLevelFunctions() {
            return new Arg(this.path, this.typeContext, this.depth, false);
        }

        public Arg addDepth() {
            return new Arg(this.path, this.typeContext, this.depth + 1, this.withTopLevelFunctions);
        }

        public Arg withClassType(Type classType) {
            return new Arg(this.path, this.typeContext.withClass(classType), this.depth, this.withTopLevelFunctions);
        }

        public Arg replaceTypeContext(TypeContext newContext) {
            return new Arg(this.path, newContext, this.depth, this.withTopLevelFunctions);
        }
    }

    private static final class CreateTestQueueElement implements Comparable<CreateTestQueueElement> {
        private final Type type;
        private final Arg arg;

        private CreateTestQueueElement(Type t, Arg arg) {
            if (t == null || arg == null) {
                throw new NullPointerException();
            }
            this.type = t;
            this.arg = arg;
        }

        @Override
        public int compareTo(CreateTestQueueElement o) {
            return Integer.compare(this.arg.depth, o.arg.depth);
        }
    }



    private final class CreateTestVisitor implements TypeVisitorWithArgument<Void, Arg> {
        private final Set<TypeWithContext> seen = new HashSet<>();
        private final List<Test> tests = new ArrayList<>();
        private final PriorityQueue<CreateTestQueueElement> queue;
        private Set<TypeWithContext> negativeTypesSeen;

        private CreateTestVisitor(PriorityQueue<CreateTestQueueElement> queue, Set<TypeWithContext> negativeTypesSeen) {
            this.queue = queue;
            this.negativeTypesSeen = negativeTypesSeen;
        }

        @Override
        public Void visit(AnonymousType t, Arg arg) {
            TypeWithContext withParameters = new TypeWithContext(t, arg.getTypeContext());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            return null;
        }

        @Override
        public Void visit(ClassType t, Arg arg) {
            TypeWithContext withParameters = new TypeWithContext(t, arg.getTypeContext());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            if (hasThisTypes.contains(t)) {
                arg = arg.withClassType(t.getInstanceType());
            }

            assert t.getTarget().equals(t) || (t.getTarget() instanceof ClassInstanceType && ((ClassInstanceType) t.getTarget()).getClassType().equals(t));

            for (Type baseType : t.getBaseTypes()) {
                recurse(baseType, arg);
            }

            assert !t.getSignatures().isEmpty();
            for (Signature signature : t.getSignatures()) {
                tests.add(new ConstructorCallTest(t, signature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList()), t.getInstanceType(), arg.path, arg.typeContext, signature.isHasRestParameter()));
            }

            recurse(t.getInstanceType(), arg.append("new()"));

            visitProperties(t, arg, t.getStaticProperties());

            return null;
        }

        @Override
        public Void visit(GenericType t, Arg arg) {
            TypeWithContext withParameters = new TypeWithContext(t, arg.getTypeContext());
            if (typeNames.get(t).equals("Array")) {
                assert t.getTypeParameters().size() == 1;
                TypeWithContext lookup = arg.typeContext.get((TypeParameterType) t.getTypeParameters().iterator().next());
                arg = arg.withParameters(arg.getTypeContext());
                Type arrayType = lookup.getType();
                tests.add(new NumberIndexTest(t, arrayType, arg.path, arg.typeContext));
                recurse(arrayType, arg.append("[numberIndexer]"));

                return null;
            }
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            if (hasThisTypes.contains(t)) {
                arg = arg.withClassType(t);
            }

            for (Type base : t.getBaseTypes()) {
                recurse(base, arg);
            }

            if (t.getDeclaredStringIndexType() != null) {
                tests.add(new StringIndexTest(t, t.getDeclaredStringIndexType(), arg.path, arg.typeContext));
                recurse(t.getDeclaredStringIndexType(), arg.append("[stringIndexer]"));
            }
            if (t.getDeclaredNumberIndexType() != null) {
                tests.add(new NumberIndexTest(t, t.getDeclaredNumberIndexType(), arg.path, arg.typeContext));
                recurse(t.getDeclaredNumberIndexType(), arg.append("[numberIndexer]"));
            }

            Map<String, Type> properties = t.getDeclaredProperties();
            visitProperties(t, arg, properties);

            return null;
        }


        @Override
        public Void visit(InterfaceType t, Arg arg) {
            TypeWithContext withParameters = new TypeWithContext(t, arg.getTypeContext());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            if (hasThisTypes.contains(t)) {
                arg = arg.withClassType(t);
            }

            for (Type base : t.getBaseTypes()) {
                recurse(base, arg);
            }

            if (t.getDeclaredStringIndexType() != null) {
                tests.add(new StringIndexTest(t, t.getDeclaredStringIndexType(), arg.path, arg.typeContext));
                recurse(t.getDeclaredStringIndexType(), arg.append("[stringIndexer]"));
            }
            if (t.getDeclaredNumberIndexType() != null) {
                tests.add(new NumberIndexTest(t, t.getDeclaredNumberIndexType(), arg.path, arg.typeContext));
                recurse(t.getDeclaredNumberIndexType(), arg.append("[numberIndexer]"));
            }

            Map<String, Type> properties = t.getDeclaredProperties();
            visitProperties(t, arg, properties);


            return null;
        }

        private void visitProperties(Type t, Arg arg, Map<String, Type> properties) {
            for (Map.Entry<String, Type> entry : properties.entrySet()) {
                String key = entry.getKey();
                Type type = entry.getValue();

                tests.add(new MemberAccessTest(t, type, key, arg.path, arg.getTypeContext()));

                addMethodCallTest(t, arg, key, type);

                recurse(type, arg.append(key));
            }
        }

        private void addMethodCallTest(Type baseType, Arg arg, String key, Type propertyType) {
            if (propertyType instanceof InterfaceType) {
                List<Signature> callSignatures = ((InterfaceType) propertyType).getDeclaredCallSignatures();
                for (Signature signature : callSignatures) {
                    List<Type> parameters = signature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                    findPositiveTypesInParameters(this, arg.append(key), parameters, this.negativeTypesSeen, TestCreator.this.nativeTypes);
                    tests.add(new MethodCallTest(baseType, propertyType, key, parameters, signature.getResolvedReturnType(), arg.append(key).path, arg.getTypeContext(), signature.isHasRestParameter()));

                    recurse(signature.getResolvedReturnType(), arg.append(key + "()").addDepth().withTopLevelFunctions());
                }

                List<Signature> constructSignatures = ((InterfaceType) propertyType).getDeclaredConstructSignatures();
                for (Signature signature : constructSignatures) {
                    List<Type> parameters = signature.getParameters().stream().map(Signature.Parameter::getType).collect(Collectors.toList());
                    findPositiveTypesInParameters(this, arg.append(key), parameters, this.negativeTypesSeen, TestCreator.this.nativeTypes);
                    tests.add(new ConstructorCallTest(propertyType, parameters, signature.getResolvedReturnType(), arg.append(key).path, arg.getTypeContext(), signature.isHasRestParameter()));

                    recurse(signature.getResolvedReturnType(), arg.append(key + "new()").addDepth().withTopLevelFunctions());
                }
                return;
            }

            if (propertyType instanceof GenericType) {
                addMethodCallTest(baseType, arg, key, ((GenericType) propertyType).toInterface());
                return;
            }
            if (propertyType instanceof ClassType) {
                return;
            }
            if (propertyType  instanceof TypeParameterType) {
                TypeParameterType typeParameterType = (TypeParameterType) propertyType ;
                if (typeParameterType.getConstraint() != null) {
                    addMethodCallTest(baseType, arg, key, ((TypeParameterType) propertyType ).getConstraint());
                }
                List<Type> recursiveDefinition = TypesUtil.findRecursiveDefinition(typeParameterType, arg.typeContext, typeParameterIndexer);
                if (!recursiveDefinition.isEmpty()) {
                    for (Type subType : recursiveDefinition) {
                        addMethodCallTest(baseType, arg, key, subType);
                    }
                    return;
                }
                if (arg.typeContext.containsKey(typeParameterType)) {
                    TypeWithContext lookup = arg.typeContext.get(typeParameterType);
                    addMethodCallTest(baseType, arg.withParameters(lookup.getTypeContext()), key, lookup.getType());
                }
                return;
            }
            if (propertyType instanceof SimpleType || propertyType instanceof StringLiteral || propertyType instanceof BooleanLiteral || propertyType instanceof NumberLiteral || propertyType instanceof ThisType || propertyType instanceof TupleType) {
                return;
            }

            if (propertyType instanceof ReferenceType) {
                TypeContext newParameters = new TypesUtil(bench).generateParameterMap((ReferenceType) propertyType);
                Type subType = ((ReferenceType) propertyType).getTarget();
                Arg newArg = arg.append("<>").withParameters(newParameters);
                addMethodCallTest(baseType, newArg, key, subType);
                return;
            }

            if (propertyType instanceof ClassInstanceType) {
                return;
            }

            if (propertyType instanceof UnionType) {
                List<Type> elements = ((UnionType) propertyType).getElements();
                for (int i = 0; i < elements.size(); i++) {
                    Type type = elements.get(i);
                    addMethodCallTest(baseType, arg.append("[union" + i + "]"), key, type);
                }
                return;
            }

            if (propertyType instanceof IntersectionType) {
                List<Type> elements = ((IntersectionType) propertyType).getElements();
                for (int i = 0; i < elements.size(); i++) {
                    Type type = elements.get(i);
                    addMethodCallTest(baseType, arg.append("[intersection" + i + "]"), key, type);
                }
                return;
            }


            throw new RuntimeException(propertyType.getClass().getName());
        }

        @Override
        public Void visit(ReferenceType t, Arg arg) {
            TypeWithContext withParameters = new TypeWithContext(t, arg.getTypeContext());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            TypeContext newParameters = new TypesUtil(bench).generateParameterMap(t);

            recurse(t.getTarget(), arg.append("<>").withParameters(newParameters));

            return null;
        }

        private void recurse(Type type, Arg arg) {
            queue.add(new CreateTestQueueElement(type, arg));
        }

        @Override
        public Void visit(SimpleType t, Arg arg) {
            return null;
        }

        @Override
        public Void visit(TupleType tuple, Arg arg) {
            TypeWithContext withParameters = new TypeWithContext(tuple, arg.getTypeContext());
            if (seen.contains(withParameters)/* || nativeTypes.contains(tuple)*/) { // TupleTypes for some weird reason ends up as the result of en Array's map function.
                return null;
            }
            seen.add(withParameters);

            for (int i = 0; i < tuple.getElementTypes().size(); i++) {
                Type type = tuple.getElementTypes().get(i);
                tests.add(new MemberAccessTest(tuple, type, Integer.toString(i), arg.path, arg.typeContext));
                recurse(type, arg.append(Integer.toString(i)));
            }

            return null;
        }

        @Override
        public Void visit(UnionType union, Arg arg) {
            TypeWithContext withParameters = new TypeWithContext(union, arg.getTypeContext());
            if (seen.contains(withParameters)) { /* || nativeTypes.contains(union)) { sometimes union-types ends up in the native-types thing, i just test all of em. */
                return null;
            }
            seen.add(withParameters);

            List<Type> elements = union.getElements();

            if (elements.size() == 0) {
                return null;
            }

            tests.add(new UnionTypeTest(union, union.getElements(), arg.path, arg.typeContext));

            for (int i = 0; i < union.getElements().size(); i++) {
                Type type = union.getElements().get(i);
                recurse(type, arg.append("[union" + i + "]"));
            }

            return null;
        }

        @Override
        public Void visit(UnresolvedType t, Arg arg) {
            TypeWithContext withParameters = new TypeWithContext(t, arg.getTypeContext());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            throw new RuntimeException();
        }

        @Override
        public Void visit(TypeParameterType t, Arg arg) {
            TypeWithContext withParameters = new TypeWithContext(t, arg.getTypeContext());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);

            if (arg.getTypeContext().containsKey(t)) {
                TypeWithContext lookup = arg.getTypeContext().get(t);
                arg = arg.withParameters(lookup.getTypeContext());
                recurse(lookup.getType(), arg);
            }

            if (t.getConstraint() != null) {
                tests.add(new FilterTest(t, t.getConstraint(), arg.path, arg.getTypeContext(), Check.alwaysTrue()));
                recurse(t.getConstraint(), arg.append("[constraint]"));
            }

            return null;
        }

        @Override
        public Void visit(SymbolType t, Arg arg) {
            return null;
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
            TypeWithContext withParameters = new TypeWithContext(t, arg.getTypeContext());
            if (seen.contains(withParameters) || nativeTypes.contains(t)) {
                return null;
            }
            seen.add(withParameters);


            for (int i = 0; i < t.getElements().size(); i++) {
                Type subType = t.getElements().get(i);
                tests.add(new FilterTest(t, subType, arg.path, arg.getTypeContext(), Check.alwaysTrue()));
                recurse(subType, arg.append("[intersection" + i + "]"));
            }

            return null;
        }

        @Override
        public Void visit(ClassInstanceType t, Arg arg) {
            InterfaceType instanceType = ((ClassType) t.getClassType()).getInstanceType();
            // tests.add(new FilterTest(t, instanceType, arg.path, arg.typeContext, Check.alwaysTrue())); // Not needed, the TypeCreator will make sure the actual InstanceType is found.

            recurse(instanceType, arg);
            return null;
        }

        @Override
        public Void visit(NeverType t, Arg arg) {
            return null;
        }

        @Override
        public Void visit(ThisType t, Arg arg) {
            return arg.typeContext.getThisType().accept(this, arg);
        }

        @Override
        public Void visit(IndexType t, Arg arg) {
            throw new RuntimeException();
        }

        @Override
        public Void visit(IndexedAccessType t, Arg arg) {
            throw new RuntimeException();
        }

        public Collection<Test> getTests() {
            return tests;
        }
    }

    private void findPositiveTypes(CreateTestVisitor visitor, Type type, Arg arg, Set<Type> nativeTypes) {
        type.accept(new FindPositiveTypesVisitor(visitor, visitor.negativeTypesSeen, nativeTypes, bench, hasThisTypes), arg);
    }

    private static class FindPositiveTypesVisitor implements TypeVisitorWithArgument<Void, Arg> {
        private CreateTestVisitor visitor;
        private Set<TypeWithContext> negativeTypesSeen;
        private Set<Type> nativeTypes;
        private final Benchmark bench;
        private Set<Type> hasThisTypes;

        public FindPositiveTypesVisitor(CreateTestVisitor createTestVisitor, Set<TypeWithContext> negativeTypesSeen, Set<Type> nativeTypes, Benchmark bench, Set<Type> hasThisTypes) {
            this.visitor = createTestVisitor;
            this.negativeTypesSeen = negativeTypesSeen;
            this.nativeTypes = nativeTypes;
            this.bench = bench;
            this.hasThisTypes = hasThisTypes;
        }

        @Override
        public Void visit(AnonymousType t, Arg arg) {
            return null;
        }

        @Override
        public Void visit(ClassType t, Arg arg) {
            if (negativeTypesSeen.contains(new TypeWithContext(t, arg.getTypeContext())) || nativeTypes.contains(t)) {
                return null;
            }
            negativeTypesSeen.add(new TypeWithContext(t, arg.getTypeContext()));

            t.getInstanceType().accept(this, arg.append("new()"));

            assert !t.getSignatures().isEmpty();

            for (Signature signature : t.getSignatures()) {
                for (int i = 0; i < signature.getParameters().size(); i++) {
                    Signature.Parameter parameter = signature.getParameters().get(i);
                    visitor.recurse(parameter.getType(), arg.append("[arg" + i + "]").withTopLevelFunctions());
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

            for (Map.Entry<String, Type> entry : t.getStaticProperties().entrySet()) {
                entry.getValue().accept(this, arg.append(entry.getKey()));
            }

            return null;
        }

        @Override
        public Void visit(GenericType t, Arg arg) {
            if (negativeTypesSeen.contains(new TypeWithContext(t, arg.getTypeContext())) || nativeTypes.contains(t)) {
                return null;
            }
            negativeTypesSeen.add(new TypeWithContext(t, arg.getTypeContext()));

            if (hasThisTypes.contains(t)) {
                arg = arg.withClassType(t);
            }

            assert t.getTypeParameters().equals(t.getTypeArguments()); // If this fails, look at the other visitor.
            t.toInterface().accept(this, arg);
            return null;
        }

        @Override
        public Void visit(InterfaceType t, Arg arg) {
            if (negativeTypesSeen.contains(new TypeWithContext(t, arg.getTypeContext())) || nativeTypes.contains(t)) {
                return null;
            }
            negativeTypesSeen.add(new TypeWithContext(t, arg.getTypeContext()));

            if (hasThisTypes.contains(t)) {
                arg = arg.withClassType(t);
            }

            for (Signature signature : Util.concat(t.getDeclaredCallSignatures(), t.getDeclaredConstructSignatures())) {
                for (int i = 0; i < signature.getParameters().size(); i++) {
                    Signature.Parameter parameter = signature.getParameters().get(i);
                    visitor.recurse(parameter.getType(), arg.append("[arg" + i + "]").withTopLevelFunctions());
                }
                signature.getResolvedReturnType().accept(this, arg.append("()"));
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
            if (negativeTypesSeen.contains(new TypeWithContext(t, arg.getTypeContext())) || nativeTypes.contains(t)) {
                return null;
            }
            negativeTypesSeen.add(new TypeWithContext(t, arg.getTypeContext()));

            TypeContext newParameters = new TypesUtil(bench).generateParameterMap(t);

            t.getTarget().accept(this, arg.append("<>").withParameters(newParameters));

            return null;
        }

        @Override
        public Void visit(SimpleType t, Arg arg) {
            return null;
        }

        @Override
        public Void visit(TupleType t, Arg arg) {
            if (negativeTypesSeen.contains(new TypeWithContext(t, arg.getTypeContext())) /* || nativeTypes.contains(tuple)*/) { // TupleTypes for some weird reason ends up as the result of en Array's map function.
                return null;
            }
            negativeTypesSeen.add(new TypeWithContext(t, arg.getTypeContext()));

            for (int i = 0; i < t.getElementTypes().size(); i++) {
                Type type = t.getElementTypes().get(i);
                type.accept(this, arg.append(Integer.toString(i)));
            }

            return null;
        }

        @Override
        public Void visit(UnionType union, Arg arg) {
            if (negativeTypesSeen.contains(new TypeWithContext(union, arg.getTypeContext())) || nativeTypes.contains(union)) {
                return null;
            }
            negativeTypesSeen.add(new TypeWithContext(union, arg.getTypeContext()));

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
            if (negativeTypesSeen.contains(new TypeWithContext(t, arg.getTypeContext())) || nativeTypes.contains(t)) {
                return null;
            }
            negativeTypesSeen.add(new TypeWithContext(t, arg.getTypeContext()));

            if (arg.getTypeContext().containsKey(t)) {
                TypeWithContext lookup = arg.getTypeContext().get(t);
                lookup.getType().accept(this, arg.withParameters(lookup.getTypeContext()));
            }

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
            if (negativeTypesSeen.contains(new TypeWithContext(intersection, arg.getTypeContext())) || nativeTypes.contains(intersection)) {
                return null;
            }
            negativeTypesSeen.add(new TypeWithContext(intersection, arg.getTypeContext()));

            for (int i = 0; i < intersection.getElements().size(); i++) {
                Type type = intersection.getElements().get(i);
                type.accept(this, arg.append("[intersection" + i + "]"));
            }
            return null;
        }

        @Override
        public Void visit(ClassInstanceType t, Arg arg) {
            return ((ClassType) t.getClassType()).getInstanceType().accept(this, arg);
        }

        @Override
        public Void visit(NeverType t, Arg arg) {
            return null;
        }

        @Override
        public Void visit(ThisType t, Arg arg) {
            return arg.getTypeContext().getThisType().accept(this, arg);
        }

        @Override
        public Void visit(IndexType t, Arg arg) {
            return t.getType().accept(this, arg.append("[index]"));
        }

        @Override
        public Void visit(IndexedAccessType t, Arg arg) {
            t.getObjectType().accept(this, arg.append("[objectType]"));
            t.getIndexType().accept(this, arg.append("[indexType]"));
            return null;
        }
    }
}
