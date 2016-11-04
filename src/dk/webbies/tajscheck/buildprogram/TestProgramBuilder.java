package dk.webbies.tajscheck.buildprogram;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.paser.AST.*;
import dk.webbies.tajscheck.paser.AstBuilder;
import dk.webbies.tajscheck.paser.JavaScriptParser;
import dk.webbies.tajscheck.testcreator.Test.*;
import dk.webbies.tajscheck.util.ArrayListMultimap;
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
    public static final String TYPES_VARIABLE_PREFIX = "value_";
    public static final String GET_TYPE_PREFIX = "getType_";

    private final Benchmark bench;
    private final List<Test> tests;
    private final Set<Type> nativeTypes;
    private Map<Type, String> typeNames;

    private final MultiMap<Type, Integer> valueLocations = new ArrayListMultimap<>();
    private final Map<Test, Integer> testToValueMap = new HashMap<>();
    private int numberOfTypes = 0;
    private TypeCreator typeCreator;

    public TestProgramBuilder(Benchmark bench, Collection<Test> tests, Set<Type> nativeTypes, Map<Type, String> typeNames) {
        this.bench = bench;
        this.tests = new ArrayList<>(tests);
        this.nativeTypes = nativeTypes;
        this.typeNames = typeNames;

        for (Test test : this.tests) {
            int index = numberOfTypes++;
            Type produces = test.getProduces();
            putProducedValueIndex(index, produces);
            testToValueMap.put(test, index);
        }

        this.typeCreator = new TypeCreator(valueLocations, this.typeNames);
    }

    private void putProducedValueIndex(int index, Type type) {
        valueLocations.put(type, index);
        if (type instanceof InterfaceType || type instanceof GenericType) {
            List<Type> baseTypes = type instanceof InterfaceType ? ((InterfaceType) type).getBaseTypes() : ((GenericType) type).getBaseTypes();
            baseTypes.forEach(baseType -> putProducedValueIndex(index, baseType));
        }
    }

    public Statement buildTestProgram() throws IOException {
        List<Statement> program = new ArrayList<>();

        program.add(parseProgram("prelude.js"));

        // Adding all the var variable_X = null;
        for (int i = 0; i < this.numberOfTypes; i++) {
            program.add(
                    variable(identifier(TYPES_VARIABLE_PREFIX + i), identifier(VARIABLE_NO_VALUE))
            );
        }

        // Adding all the getType_X functions.

        program.add(typeCreator.getBlockStatementWithTypeFunctions());

        // Non-deterministically running all the test-cases.
        program.add(forLoop(
                variable(identifier("i"), number(0)),
                binary(identifier("i"), Operator.LESS_THAN, number(1000)),
                binary(identifier("i"), Operator.EQUAL, binary(identifier("i"), Operator.PLUS, number(1))),
                block(
                        variable(identifier("testNumberToRun"), binary(binary(methodCall(identifier("Math"), "random"), Operator.MULT, number(tests.size())), Operator.BITWISE_OR, number(0))),
                        tryCatch(
                                AstBuilder.switchCase(
                                        identifier("testNumberToRun"),
                                        buildTestCases()),
                                catchBlock(
                                        identifier("e"),
                                        block(/* Empty */)))
                )));

        program.add(parseProgram("dumb.js"));

        return expressionStatement(call(function(block(program))));
    }

    private BlockStatement parseProgram(String fileName) throws IOException {
        return new JavaScriptParser(ParseDeclaration.Environment.ES5Core).parse(fileName, Resources.toString(this.getClass().getResource(fileName), Charsets.UTF_8)).toTSCreateAST().getBody();
    }


    private List<Pair<Expression, Statement>> buildTestCases() {
        List<Pair<Expression, Statement>> result = new ArrayList<>();

        List<Test> tests = new ArrayList<>(this.tests); // This works really well to ensure that something happens.
//        Collections.reverse(tests);

        for (int i = 0; i < tests.size(); i++) {
            Test test = tests.get(i);
            result.add(new Pair<>(
                    number(i),
                    block(
                            Util.concat(
                                    buildTestCase(test),
                                    Collections.singletonList(breakStatement())
                            )
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
                Collections.singletonList(checkDependencies(test)),
                testCode,
                Arrays.asList(
                        new CheckType(nativeTypes, typeNames).checkResultingType(test.getProduces(), identifier("result"), test.getPath()),
                        expressionStatement(binary(identifier(TYPES_VARIABLE_PREFIX + testToValueMap.get(test)), Operator.EQUAL, identifier("result")))
                ));
    }

    private IfStatement checkDependencies(Test test) {
        List<Type> dependsOn = new ArrayList<>(test.getDependsOn());

        return ifThen(buildDependsOnCheck(dependsOn), continueStatement());
    }

    private Expression buildDependsOnCheck(List<Type> dependsOn) {
        if (dependsOn.isEmpty()) {
            return bool(false);
        }
        Type typeToCheck = dependsOn.iterator().next();

        CallExpression resultTypeCall = getTypeCall(typeToCheck);
        BinaryExpression checkNextType = binary(resultTypeCall, Operator.EQUAL_EQUAL_EQUAL, identifier(VARIABLE_NO_VALUE));

        if (dependsOn.size() == 1) {
            return checkNextType;
        } else {
            return binary(checkNextType, Operator.OR, buildDependsOnCheck(dependsOn.subList(1, dependsOn.size())));
        }
    }

    private CallExpression getTypeCall(Type typeToCheck) {
        return call(identifier(GET_TYPE_PREFIX + typeCreator.getTypeIndex(typeToCheck)));
    }

    /**
     * For each of these, produce code that expects all the getType calls to succeed.
     * And the result of the test should be put into a variable "result".
     */
    private class TestBuilderVisitor implements TestVisitor<List<Statement>> {
        @Override
        public List<Statement> visit(MemberAccessTest test) {
            return Arrays.asList(
                    variable(identifier("base"), getTypeCall(test.getBaseType())),
                    variable(identifier("result"), member(identifier("base"), test.getProperty()))
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

            result.add(variable(identifier("base"), getTypeCall(test.getObject())));

            List<Expression> parameters = test.getParameters().stream().map(TestProgramBuilder.this::getTypeCall).collect(Collectors.toList());
            MethodCallExpression methodCall = methodCall(identifier("base"), test.getPropertyName(), parameters);

            result.add(variable(identifier("result"), methodCall));

            return result;
        }

        @Override
        public List<Statement> visit(ConstructorCallTest test) {
            List<Statement> result = new ArrayList<>();

            result.add(variable(identifier("base"), getTypeCall(test.getFunction())));

            List<Expression> parameters = test.getParameters().stream().map(TestProgramBuilder.this::getTypeCall).collect(Collectors.toList());
            Expression newCall = AstBuilder.newCall(identifier("base"), parameters);
            result.add(variable(identifier("result"), newCall));

            return result;
        }

        @Override
        public List<Statement> visit(FunctionCallTest test) {
            List<Statement> result = new ArrayList<>();

            result.add(variable(identifier("base"), getTypeCall(test.getFunction())));

            List<Expression> parameters = test.getParameters().stream().map(TestProgramBuilder.this::getTypeCall).collect(Collectors.toList());
            Expression newCall = AstBuilder.call(identifier("base"), parameters);
            result.add(variable(identifier("result"), newCall));

            return result;
        }
    }
}
