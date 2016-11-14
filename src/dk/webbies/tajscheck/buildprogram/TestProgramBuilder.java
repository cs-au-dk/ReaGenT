package dk.webbies.tajscheck.buildprogram;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.ExecutionRecording;
import dk.webbies.tajscheck.ParameterMap;
import dk.webbies.tajscheck.TypeWithParameters;
import dk.webbies.tajscheck.TypesUtil;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.paser.AST.*;
import dk.webbies.tajscheck.paser.AstBuilder;
import dk.webbies.tajscheck.paser.JavaScriptParser;
import dk.webbies.tajscheck.testcreator.test.*;
import dk.webbies.tajscheck.testcreator.test.check.Check;
import dk.webbies.tajscheck.testcreator.test.check.CheckToExpression;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.paser.AstBuilder.*;

/**
 * Created by erik1 on 02-11-2016.
 */
public class TestProgramBuilder {
    public static final String ASSERTION_FAILURES = "assertionFailures";
    public static final String VARIABLE_NO_VALUE = "no_value";
    public static final String VALUE_VARIABLE_PREFIX = "value_";
    public static final String GET_TYPE_PREFIX = "getType_";
    public static final String CONSTRUCT_TYPE_PREFIX = "constructType_";
    public static final String TYPE_VALUE_PREFIX = "type_";
    public static final String RUNTIME_ERROR_NAME = "RuntimeError";

    private final Benchmark bench;
    private final List<Test> tests;
    private final Set<Type> nativeTypes;
    private final TypeParameterIndexer typeParameterIndexer;
    private Map<Type, String> typeNames;

    private final MultiMap<TypeWithParameters, Integer> valueLocations = new ArrayListMultiMap<>();
    private final Map<Test, Integer> testToValueMap = new IdentityHashMap<>();
    private int numberOfTypes = 0;
    private TypeCreator typeCreator;


    static final class TypeParameterIndexer {
        private final Map<TypeParameterType, Integer> map = new HashMap<>();
        String getMarkerField(TypeParameterType t) {
            if (map.containsKey(t)) {
                return "typeParameterMarker_" + map.get(t);
            } else {
                map.put(t, map.size());
                return getMarkerField(t);
            }
        }
    }

    public TestProgramBuilder(Benchmark bench, Set<Type> nativeTypes, Map<Type, String> typeNames, List<Test> tests) {
        this.bench = bench;
        this.tests = new ArrayList<>(tests);
        this.nativeTypes = nativeTypes;
        this.typeNames = typeNames;
        this.typeParameterIndexer = new TypeParameterIndexer();

        for (Test test : this.tests) {
            int index = numberOfTypes++;
            Type produces = test.getProduces();
            putProducedValueIndex(index, produces, test.getParameterMap());
            testToValueMap.put(test, index);
        }

        this.typeCreator = new TypeCreator(valueLocations, this.typeNames, nativeTypes, typeParameterIndexer);
    }

    private void putProducedValueIndex(int index, Type type, ParameterMap parameterMap) {
        valueLocations.put(new TypeWithParameters(type, parameterMap), index);
        if (type instanceof InterfaceType) {
            List<Type> baseTypes = ((InterfaceType) type).getBaseTypes();
            baseTypes.forEach(baseType -> putProducedValueIndex(index, baseType, parameterMap));
        }
        if (type instanceof ReferenceType) {
            putProducedValueIndex(index, ((ReferenceType) type).getTarget(), TypesUtil.generateParameterMap((ReferenceType) type, parameterMap));
        }
        if (type instanceof GenericType) {
            putProducedValueIndex(index, ((GenericType) type).toInterface(), parameterMap);
        }
    }

    public Statement buildTestProgram(ExecutionRecording recording) throws IOException {
        List<Statement> program = new ArrayList<>();

        // var initialRandomness = Math.random()
        if (recording == null) {
            program.add(variable("initialRandomness", methodCall(identifier("Math"), "random")));
        } else {
            program.add(variable("initialRandomness", string(recording.seed)));
        }

        program.add(parseProgram("prelude.js"));

        // Adding all the var variable_X = null;
        for (int i = 0; i < this.numberOfTypes; i++) {
            program.add(
                    variable(VALUE_VARIABLE_PREFIX + i, identifier(VARIABLE_NO_VALUE))
            );
        }

        // Adding all the getType_X functions.

        program.add(typeCreator.getBlockStatementWithTypeFunctions());

        Expression iterationsToRun;
        if (recording == null) {
            iterationsToRun = number(1000);
        } else {
            iterationsToRun = member(identifier("recording"), "length");
            program.add(variable("recording", array()));
            Arrays.stream(recording.testSequence)
                    .mapToObj(i -> methodCall(identifier("recording"), "push", number(i)))
                    .map(AstBuilder::expressionStatement)
                    .forEach(program::add);
        }

        // Non-deterministically running all the test-cases.
        Expression getNumberToRun = null;
        if (recording == null) {
            getNumberToRun = binary(binary(call(identifier("random")), Operator.MULT, number(tests.size())), Operator.BITWISE_OR, number(0));
        } else {
            getNumberToRun = arrayAccess(identifier("recording"), identifier("i"));
        }
        program.add(forLoop(
                variable("i", number(0)),
                binary(identifier("i"), Operator.LESS_THAN, iterationsToRun),
                unary(Operator.POST_PLUS_PLUS, identifier("i")),
                block(
                        variable("testNumberToRun", getNumberToRun),
                        expressionStatement(methodCall(identifier("testOrderRecording"), "push", identifier("testNumberToRun"))),
                        tryCatch(
                                AstBuilder.switchCase(
                                        identifier("testNumberToRun"),
                                        buildTestCases()),
                                catchBlock(
                                        identifier("e"),
                                        block(
//                                                expressionStatement(call(identifier("print"), identifier("e"))),
                                                ifThen(
                                                        binary(identifier("e"), Operator.INSTANCEOF, identifier(RUNTIME_ERROR_NAME)),
                                                        throwStatement(identifier("e"))
                                                )
                                        )))
                )));

        program.add(parseProgram("dumb.js"));

        return expressionStatement(call(function(block(program))));
    }

    private BlockStatement parseProgram(String fileName) throws IOException {
        return new JavaScriptParser(ParseDeclaration.Environment.ES5Core).parse(fileName, Resources.toString(this.getClass().getResource(fileName), Charsets.UTF_8)).toTSCreateAST().getBody();
    }


    private List<Pair<Expression, Statement>> buildTestCases() {
        List<Pair<Expression, Statement>> result = new ArrayList<>();

        List<Test> tests = new ArrayList<>(this.tests);

        for (int i = 0; i < tests.size(); i++) {
            System.out.println(i + " / " + tests.size());
            Test test = tests.get(i);
            result.add(new Pair<>(
                    number(i),
                    block(
                            expressionStatement(call(function(block(buildTestCase(test))))),
                            breakStatement()
                    )
            ));
        }

        return result;
    }

    private List<Statement> buildTestCase(Test test) {
        List<Statement> testCode = test.accept(new TestBuilderVisitor());
        /*
         * Check dependencies
         * Run test, put result in "result"
         * Check that "result" is of the right type
         * Store result for use by other tests
         */
        return Util.concat(
                getDependencies(test),
                testCode,
                Arrays.asList(
                        new CheckType(nativeTypes, typeNames, typeParameterIndexer, test.getParameterMap()).checkResultingType(test.getProduces(), identifier("result"), test.getPath()),
                        expressionStatement(binary(identifier(VALUE_VARIABLE_PREFIX + testToValueMap.get(test)), Operator.EQUAL, identifier("result")))
                ));
    }

    private Collection<Statement> getDependencies(Test test) {
        List<Statement> result = new ArrayList<>();

        ParameterMap parameterMap = test.getParameterMap();

        test.getTypeToTest().stream()
                .map((type) -> variable(TYPE_VALUE_PREFIX + typeCreator.getTypeIndex(type, parameterMap), null))
                .forEach(result::add);

        test.getTypeToTest().stream().map((type) -> {
            int index = typeCreator.getTypeIndex(type, parameterMap);
            return ifThen(
                    binary(
                            binary(
                                    identifier(TYPE_VALUE_PREFIX + index),
                                    Operator.EQUAL,
                                    call(identifier(GET_TYPE_PREFIX + index))
                            ),
                            Operator.EQUAL_EQUAL_EQUAL,
                            identifier(VARIABLE_NO_VALUE)
                    ),
                    Return()
            );
        }).forEach(result::add);

        test.getDependsOn().stream().map((type) -> {
            int index = typeCreator.getTypeIndex(type, parameterMap);
            return variable(
                    identifier(TYPE_VALUE_PREFIX + index),
                    call(identifier(CONSTRUCT_TYPE_PREFIX + index)
            ));
        }).forEach(result::add);


        return result;
    }

    /**
     * For each of these, produce code that expects all the getTypeString calls to succeed.
     * And the result of the test should be put into a variable "result".
     */
    private class TestBuilderVisitor implements TestVisitor<List<Statement>> {
        Expression getTypeExpression(Type type, ParameterMap parameterMap) {
            return identifier(TYPE_VALUE_PREFIX + typeCreator.getTypeIndex(type, parameterMap));
        }

        @Override
        public List<Statement> visit(MemberAccessTest test) {
            return Arrays.asList(
                    variable("base", getTypeExpression(test.getBaseType(), test.getParameterMap())),
                    variable("result", member(identifier("base"), test.getProperty()))
            );
        }

        @Override
        public List<Statement> visit(LoadModuleTest test) {
            return Collections.singletonList(
                    variable(
                            identifier("result"),
                            call(identifier("require"), string(test.getModule()))
                    )
            );
        }

        @Override
        public List<Statement> visit(MethodCallTest test) {
            List<Statement> result = new ArrayList<>();

            result.add(variable("base", getTypeExpression(test.getObject(), test.getParameterMap())));

            List<Expression> parameters = test.getParameters().stream().map((type) -> getTypeExpression(type, test.getParameterMap())).collect(Collectors.toList());
            MethodCallExpression methodCall = methodCall(identifier("base"), test.getPropertyName(), parameters);

            result.add(variable("result", methodCall));

            return result;
        }

        @Override
        public List<Statement> visit(ConstructorCallTest test) {
            List<Statement> result = new ArrayList<>();

            result.add(variable("base", getTypeExpression(test.getFunction(), test.getParameterMap())));

            List<Expression> parameters = test.getParameters().stream().map((type) -> getTypeExpression(type, test.getParameterMap())).collect(Collectors.toList());
            Expression newCall = AstBuilder.newCall(identifier("base"), parameters);
            result.add(variable("result", newCall));

            return result;
        }

        @Override
        public List<Statement> visit(FunctionCallTest test) {
            List<Statement> result = new ArrayList<>();

            result.add(variable("base", getTypeExpression(test.getFunction(), test.getParameterMap())));

            List<Expression> parameters = test.getParameters().stream().map((type) -> getTypeExpression(type, test.getParameterMap())).collect(Collectors.toList());
            Expression newCall = AstBuilder.call(identifier("base"), parameters);
            result.add(variable("result", newCall));

            return result;
        }

        @Override
        public List<Statement> visit(FilterTest test) {
            return Arrays.asList(
                    variable("result", getTypeExpression(test.getType(), test.getParameterMap())),
                    ifThen(
                            CheckToExpression.generate(Check.not(test.getCheck()), identifier("result")),
                            Return()
                    )
            );
        }

    }
}
