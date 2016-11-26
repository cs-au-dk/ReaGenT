package dk.webbies.tajscheck.buildprogram;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import dk.au.cs.casa.typescript.types.*;
import dk.webbies.tajscheck.*;
import dk.webbies.tajscheck.benchmarks.Benchmark;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.paser.AST.*;
import dk.webbies.tajscheck.paser.AstBuilder;
import dk.webbies.tajscheck.paser.JavaScriptParser;
import dk.webbies.tajscheck.testcreator.test.*;
import dk.webbies.tajscheck.testcreator.test.check.Check;
import dk.webbies.tajscheck.testcreator.test.check.CheckToExpression;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dk.webbies.tajscheck.paser.AstBuilder.*;

/**
 * Created by erik1 on 02-11-2016.
 */
public class TestProgramBuilder {
    public static final String ASSERTION_FAILURES = "assertionFailures";
    public static final String VARIABLE_NO_VALUE = "no_value";
    public static final String VALUE_VARIABLE_PREFIX = "value_";
    public static final String RUNTIME_ERROR_NAME = "RuntimeError";

    private final Benchmark bench;
    private final List<Test> tests;
    private final Set<Type> nativeTypes;
    private final TypeParameterIndexer typeParameterIndexer;
    private Map<Type, String> typeNames;
    private Type moduleType;

    private TypeCreator typeCreator;


    public static final class TypeParameterIndexer {
        private final Map<TypeParameterType, Integer> map = new HashMap<>();

        public String getMarkerField(TypeParameterType t) {
            if (map.containsKey(t)) {
                return "typeParameterMarker_" + map.get(t);
            } else {
                map.put(t, map.size());
                return getMarkerField(t);
            }
        }
    }

    public TestProgramBuilder(Benchmark bench, Set<Type> nativeTypes, Map<Type, String> typeNames, List<Test> tests, Type moduleType, TypeParameterIndexer typeParameterIndexer) {
        this.bench = bench;
        this.tests = new ArrayList<>(tests);
        this.nativeTypes = nativeTypes;
        this.typeNames = typeNames;
        this.moduleType = moduleType;
        this.typeParameterIndexer = typeParameterIndexer;

        this.typeCreator = new TypeCreator(this.typeNames, nativeTypes, typeParameterIndexer);
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

        program.add(block(typeCreator.getValueVariableDeclarationList()));

        // Adding all the getType_X functions.

        program.add(typeCreator.getBlockStatementWithTypeFunctions());

        Expression iterationsToRun;
        if (recording == null || recording.testSequence == null) {
            iterationsToRun = number(1000);
        } else {
            iterationsToRun = member(identifier("recording"), "length");
            program.add(variable("recording", array()));
            Arrays.stream(recording.testSequence)
                    .mapToObj(i -> methodCall(identifier("recording"), "push", number(i)))
                    .map(AstBuilder::statement)
                    .forEach(program::add);
        }

        // Non-deterministically running all the test-cases.
        Expression getNumberToRun;
        if (recording == null || recording.testSequence == null) {
            getNumberToRun = binary(binary(call(identifier("random")), Operator.MULT, number(tests.size())), Operator.BITWISE_OR, number(0));
        } else {
            getNumberToRun = arrayAccess(identifier("recording"), identifier("i"));
        }

        if (Main.CHECK_HEAP) {
            program.add(createCheckHeapFunction());
        }

        program.add(forLoop(
                variable("i", number(0)),
                binary(identifier("i"), Operator.LESS_THAN, iterationsToRun),
                unary(Operator.POST_PLUS_PLUS, identifier("i")),
                block(
                        Main.CHECK_HEAP ? statement(call(identifier("checkHeap"))) : comment("checkHeap()"),
                        variable("testNumberToRun", getNumberToRun),
                        statement(methodCall(identifier("testOrderRecording"), "push", identifier("testNumberToRun"))),
                        tryCatch(
                                AstBuilder.switchCase(
                                        identifier("testNumberToRun"),
                                        buildTestCases()),
                                catchBlock(
                                        identifier("e"),
                                        block(
//                                                statement(call(identifier("print"), identifier("e"))),
                                                ifThen(
                                                        binary(identifier("e"), Operator.INSTANCEOF, identifier(RUNTIME_ERROR_NAME)),
                                                        block(
                                                                statement(call(identifier("print"), member(identifier("e"), "message"))),
                                                                throwStatement(identifier("e"))
                                                        )
                                                )
                                        )))
                )));

        program.add(parseProgram("dumb.js"));

        typeCreator.finish();

        if (bench.load_method == Benchmark.LOAD_METHOD.BROWSER) {
            BlockStatement dependency = new JavaScriptParser(ParseDeclaration.Environment.ES5Core).parse(bench.jsFile, Util.readFile(bench.jsFile)).toTSCreateAST().getBody();
            return block(
                    dependency,
                    statement(call(function(block(program))))
            );
        } else {
            assert bench.load_method == Benchmark.LOAD_METHOD.NODE;
            return statement(call(function(block(program))));
        }


    }

    private ExpressionStatement createCheckHeapFunction() {
        return statement(function("checkHeap", block(
                // If module not loaded, return.
                variable("module", null),
                ifThen(
                        binary(
                                binary(
                                        identifier("module"),
                                        Operator.EQUAL,
                                        typeCreator.getType(moduleType, new ParameterMap())
                                ),
                                Operator.EQUAL_EQUAL_EQUAL,
                                identifier(VARIABLE_NO_VALUE)
                        ),
                        Return()
                ),
                new CheckType(nativeTypes, typeNames, typeParameterIndexer, new ParameterMap()).assertResultingType(moduleType, identifier("module"), "require(" + bench.module + ")", Integer.MAX_VALUE)

        )));
    }

    private BlockStatement parseProgram(String fileName) throws IOException {
        return new JavaScriptParser(ParseDeclaration.Environment.ES5Core).parse(fileName, Resources.toString(this.getClass().getResource(fileName), Charsets.UTF_8)).toTSCreateAST().getBody();
    }


    private List<Pair<Expression, Statement>> buildTestCases() {
        List<Pair<Expression, Statement>> result = new ArrayList<>();

        List<Test> tests = new ArrayList<>(this.tests);

        for (int i = 0; i < tests.size(); i++) {
            Test test = tests.get(i);
            result.add(new Pair<>(
                    number(i),
                    block(
                            statement(call(function(block(buildTestCase(test))))),
                            breakStatement()
                    )
            ));
        }

        return result;
    }

    private List<Statement> buildTestCase(Test test) {
        List<Statement> testCode = test.accept(new TestBuilderVisitor());

        List<Type> produces = new ArrayList<>(test.getProduces());
        Statement saveResultStatement;
        CheckType checkType = new CheckType(nativeTypes, typeNames, typeParameterIndexer, test.getParameterMap());
        if (produces.size() == 1) {
            Type product = produces.iterator().next();
            int index = typeCreator.createProducedValueVariable(product, test.getParameterMap());
            saveResultStatement = block(
                    checkType.assertResultingType(product, identifier("result"), test.getPath(), Main.CHECK_DEPTH),
                    statement(binary(identifier(VALUE_VARIABLE_PREFIX + index), Operator.EQUAL, identifier("result")))
            );
        } else {
            List<Integer> valueIndexes = produces.stream().map(type -> typeCreator.createProducedValueVariable(type, test.getParameterMap())).collect(Collectors.toList());

            saveResultStatement = block(
                    variable("passedResults", array()),
                    block(
                            Util.zip(produces, IntStream.range(0, produces.size()).boxed()).map(pair -> {
                                Type type = pair.getLeft();
                                Integer valueIndex = pair.getRight();
                                return block(
                                        variable("passed" + valueIndex, checkType.checkResultingType(type, identifier("result"), test.getPath(), Main.CHECK_DEPTH_FOR_UNIONS)),
                                        ifThen(
                                                identifier("passed" + valueIndex),
                                                statement(methodCall(identifier("passedResults"), "push", number(valueIndex)))
                                        )
                                );
                            }).collect(Collectors.toList())
                    ),
                    // If no type passed, then we have an assertionError
                    ifThen(
                            binary(
                                    member(identifier("passedResults"), "length"),
                                    Operator.EQUAL_EQUAL_EQUAL,
                                    number(0)
                            ),
                            block(
                                    statement(
                                            call(identifier("assert"), bool(false), string(test.getPath()), string(checkType.getTypeDescription(createUnionType(produces), Main.CHECK_DEPTH_FOR_UNIONS)), identifier("result"))
                                    ),
                                    Return()
                            )
                    ),
                    // If we have more than 1 type that passed, then it is our fault, we cannot distinguish between the union types.
                    ifThen(
                            binary(
                                    member(identifier("passedResults"), "length"),
                                    Operator.GREATER_THAN_EQUAL,
                                    number(2)
                            ),
                            block(
                                    statement(call(identifier("error"), binary(string("Could not distinguish which union on path: " + test.getPath() + " types: "), Operator.PLUS, methodCall(identifier("passedResults"), "toString")))),
                                    Return()
                            )
                    ),
                    // Otherwise, assign to the single found union-type, the result.
                    switchCase(
                            arrayAccess(identifier("passedResults"), number(0)),
                            IntStream.range(0, produces.size()).mapToObj(index ->
                                    new Pair<Expression, Statement>(
                                            number(index),
                                            block(
                                                    statement(binary(identifier(VALUE_VARIABLE_PREFIX + valueIndexes.get(index)), Operator.EQUAL, identifier("result"))),
                                                    breakStatement()
                                            )
                                    )
                            ).collect(Collectors.toList())
                    )
            );
        }

        /*
         * Check dependencies
         * Run test, put result in "result"
         * Check that "result" is of the right type
         * Store result for use by other tests
         */

        return Util.concat(
                checkDependencies(test),
                testCode,
                Collections.singletonList(saveResultStatement)
        );
    }

    private UnionType createUnionType(List<Type> types) {
        UnionType union = new UnionType();
        union.setElements(types);
        return union;
    }

    private Collection<Statement> checkDependencies(Test test) {
        List<Statement> result = new ArrayList<>();

        ParameterMap parameterMap = test.getParameterMap();

        test.getTypeToTest().stream().map((type) ->
                ifThen(
                        binary(
                                typeCreator.getType(type, parameterMap),
                                Operator.EQUAL_EQUAL_EQUAL,
                                identifier(VARIABLE_NO_VALUE)
                        ),
                        Return()
                )).forEach(result::add);

        return result;
    }

    /**
     * For each of these, produce code that expects all the getTypeString calls to succeed.
     * And the result of the test should be put into a variable "result".
     */
    private class TestBuilderVisitor implements TestVisitor<List<Statement>> {
        Expression getTypeExpression(Type type, ParameterMap parameterMap) {
            return call(function(block(
                    variable("base", typeCreator.getType(type, parameterMap)),
                    ifThenElse(
                            binary(identifier("base"), Operator.NOT_EQUAL_EQUAL, identifier(VARIABLE_NO_VALUE)),
                            Return(identifier("base")),
                            throwStatement(newCall(identifier(RUNTIME_ERROR_NAME)))
                    )
            )));
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
            switch (bench.load_method) {
                case NODE:
                    return Collections.singletonList(
                            variable(
                                    identifier("result"),
                                    call(identifier("loadLibrary"), string(test.getModule()))
                            )
                    );
                case BROWSER:
                    return Collections.singletonList(
                            variable("result", identifier(bench.module))
                    );
                default:
                    throw new RuntimeException();
            }
        }

        @Override
        public List<Statement> visit(MethodCallTest test) {
            List<Statement> result = new ArrayList<>();

            result.add(variable("base", getTypeExpression(test.getObject(), test.getParameterMap())));

            List<Expression> parameters = test.getParameters().stream().map((type) -> typeCreator.createType(type, test.getParameterMap())).collect(Collectors.toList());
            MethodCallExpression methodCall = methodCall(identifier("base"), test.getPropertyName(), parameters);

            result.add(variable("result", methodCall));

            return result;
        }

        @Override
        public List<Statement> visit(ConstructorCallTest test) {
            List<Statement> result = new ArrayList<>();

            result.add(variable("base", getTypeExpression(test.getFunction(), test.getParameterMap())));

            List<Expression> parameters = test.getParameters().stream().map((type) -> typeCreator.createType(type, test.getParameterMap())).collect(Collectors.toList());
            Expression newCall = AstBuilder.newCall(identifier("base"), parameters);
            result.add(variable("result", newCall));

            return result;
        }

        @Override
        public List<Statement> visit(FunctionCallTest test) {
            List<Statement> result = new ArrayList<>();

            result.add(variable("base", getTypeExpression(test.getFunction(), test.getParameterMap())));

            // TODO: Save the dependencies in variables, dependency_1...

            List<Expression> parameters = test.getParameters().stream().map((type) -> typeCreator.createType(type, test.getParameterMap())).collect(Collectors.toList());
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

        @Override
        public List<Statement> visit(UnionTypeTest test) {
            // Looks trivial, but that is because everything complicated is handled by the method calling this visitor.
            return Collections.singletonList(
                    variable("result", getTypeExpression(test.getGetUnionType(), test.getParameterMap()))
            );
        }

    }
}
