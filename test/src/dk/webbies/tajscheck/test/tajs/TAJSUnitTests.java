package dk.webbies.tajscheck.test.tajs;

import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.benchmark.options.OptionsI;
import dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy.*;
import dk.webbies.tajscheck.benchmark.options.staticOptions.LimitTransfersRetractionPolicy;
import dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions;
import dk.webbies.tajscheck.benchmark.options.staticOptions.filter.CopyObjectInstantiation;
import dk.webbies.tajscheck.benchmark.options.staticOptions.preferlibvalues.PreferLibValuesPolicy;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
import dk.webbies.tajscheck.test.dynamic.UnitTests;
import dk.webbies.tajscheck.test.tajs.analyze.AnalyzeBenchmarks;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import dk.webbies.tajscheck.util.Util;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions.ArgumentValuesStrategy.*;
import static dk.webbies.tajscheck.tajstester.TAJSUtil.*;
import static dk.webbies.tajscheck.test.dynamic.UnitTests.ParseResultTester.ExpectType.STRING;
import static dk.webbies.tajscheck.util.Util.mkString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TAJSUnitTests {
    private static TajsAnalysisResults run(String folderName) throws Exception {
        return run(benchFromFolder(folderName, options()));
    }

    private static TajsAnalysisResults run(String folderName, OptionsI.Builder options) throws Exception {
        return run(benchFromFolder(folderName, options));
    }

    private static TajsAnalysisResults run(String folderName, OptionsI.Builder options, int timeout) throws Exception {
        return run(benchFromFolder(folderName, options), timeout);
    }

    public static TajsAnalysisResults run(Benchmark bench) throws Exception {
        return run(bench, Integer.MAX_VALUE);
    }

    static TajsAnalysisResults run(Benchmark bench, int timeout) throws Exception {
        TajsAnalysisResults result = runNoDriver(bench, timeout);
        System.out.println(result);
        return result;
    }

    private TajsAnalysisResults soundness(String folder) throws Exception {
        return soundness(folder, opt -> opt);
    }

    private TajsAnalysisResults soundness(String folder, Function<CheckOptions.Builder, OptionsI.Builder> transformer) throws Exception {
        TajsAnalysisResults result = runNoDriver(benchFromFolder(folder).withRunMethod(Benchmark.RUN_METHOD.BOOTSTRAP).withOptions(transformer).withOptions(options -> options.setConstructAllTypes(true)), Integer.MAX_VALUE);
        System.out.println(result);
        expect(result)
                .hasNoViolations();
        return result;
    }

    static Benchmark benchFromFolder(String folderName) {
        return benchFromFolder(folderName, options());
    }

    static Benchmark benchFromFolder(String folderName, OptionsI.Builder options) {
        return benchFromFolder(folderName, options, Benchmark.RUN_METHOD.NODE);
    }

    static Benchmark benchFromFolder(String folderName, OptionsI.Builder options, Benchmark.RUN_METHOD run_method) {
        return new Benchmark("tajsunit-" + folderName, ParseDeclaration.Environment.ES5Core, "test/tajsUnit/" + folderName + "/implementation.js", "test/tajsUnit/" + folderName + "/declaration.d.ts", run_method, options.build());
    }

    static Benchmark benchFromFolderTSFile(String folderName, CheckOptions.Builder options, Benchmark.RUN_METHOD run_method) throws IOException {
        String tsFile = "test/tajsUnit/" + folderName + "/implementation.ts";
        String name = "tajsunit-" + folderName;
        return Benchmark.fromTSFile(tsFile, name, ParseDeclaration.Environment.ES5Core, run_method, options.build());
    }

    public static CheckOptions.Builder options() {
        return CheckOptions.builder().setCheckDepthReport(0).setCheckDepthUseValue(0);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class TAJSResultTester {
        private TajsAnalysisResults results;

        private TAJSResultTester(TajsAnalysisResults result) {
            this.results = result;
        }

        public TAJSUnitTests.TAJSResultTester performed(String path) {
            MatcherAssert.assertThat("test for " + path + " hasn't been performed", results.testPerformed.stream().anyMatch(test -> test.getPath().equals(path)));
            return this;
        }

        public TAJSUnitTests.TAJSResultTester performedAllTests() {
            MatcherAssert.assertThat("some tests were not performed: " + mkString(results.testNot, ", "), results.testNot.isEmpty());
            return this;
        }

        public TAJSUnitTests.TAJSResultTester notPerformedAllTests() {
            MatcherAssert.assertThat("there weere no tests that were not performed", !results.testNot.isEmpty());
            return this;
        }

        public TAJSUnitTests.TAJSResultTester hasNoViolations() {
            MatcherAssert.assertThat("there are no violations", results.detectedViolations.size() == 0);
            return this;
        }

        public TAJSUnitTests.TAJSResultTester hasNoWarnings() {
            MatcherAssert.assertThat("there are no warnings", results.detectedWarnings.size() == 0);
            return this;
        }

        public TAJSUnitTests.TAJSResultTester hasViolations() {
            MatcherAssert.assertThat("there are violations", results.detectedViolations.size() != 0);
            return this;
        }

        public TAJSResultTester forPath(String path) {
            MultiMap<String, TypeViolation> detectedViolations = results.detectedViolations.asMap().entrySet().stream().filter(entry -> entry.getKey().startsWith(path)).collect(ArrayListMultiMap.collector());
            List<dk.webbies.tajscheck.testcreator.test.Test> performedTest = results.testPerformed.stream().filter(test -> test.getPath().startsWith(path)).collect(Collectors.toList());
            List<dk.webbies.tajscheck.testcreator.test.Test> testsNot = results.testNot.stream().filter(test -> test.getPath().startsWith(path)).collect(Collectors.toList());
            MultiMap<String, TypeViolation> warnings = results.detectedWarnings.asMap().entrySet().stream().filter(entry -> entry.getKey().startsWith(path)).collect(ArrayListMultiMap.collector());

            return new TAJSResultTester(new TajsAnalysisResults(
                    detectedViolations,
                    warnings,
                    performedTest,
                    testsNot,
                    results.certificates,
                    results.testTranfers,
                    results.timers,
                    results.timedout,
                    results.retractedTests,
                    results.timeoutTests,
                    results.typeCheckedTests,
                    results.detectedViolationsBeforeScan,
                    results.possiblyProblematicReads,
                    results.statementCoverage,
                    results.branchCoverage,
                    results.functionCoverage,
                    results.hasClassesInDec));
        }

        TAJSUnitTests.TAJSResultTester hasWarnings() {
            MatcherAssert.assertThat("there are warnings", results.detectedWarnings.size() != 0);
            return this;
        }
    }

    public static TAJSUnitTests.TAJSResultTester expect(TajsAnalysisResults result) {
        return new TAJSResultTester(result);
    }

    @Test
    public void unionMightFail() throws Exception {
        TajsAnalysisResults result = run("unionMightFail");

        expect(result)
                .performed("module.foo()");

        expect(result)
                .forPath("module.foo()")
                .hasViolations();
    }

    @Test
    public void callbacks() throws Exception {
        TajsAnalysisResults result = run("callbacks");

        expect(result)
                .performed("module.foo(obj)");

        expect(result)
                .forPath("module.foo(obj)")
                .hasViolations();
    }

    @Test
    @Ignore // TODO: Calling "pv.readPropertyValue" with a getter property, seems to go wrong (it seems to mix in the base object).
    public void getter() throws Exception {
        TajsAnalysisResults result = run("getter");

        expect(result)
                .hasNoViolations();
    }

    @Test(timeout = 20 * 1000)
    @Ignore // No idea, started to fail after we fixed the activation-contexts (to make sure that we could write strongly to variables).
    public void getterInfiniteLoop() throws Exception {
        run("getterInfiniteLoop"); // smoke test, is good if it ever terminates.
    }

    @Test
    public void classInstance() throws Exception {
        expect(run("classInstance"))
                .performedAllTests()
                .hasNoViolations();
    }


    @Test
    public void simpleUnion() throws Exception {
        TajsAnalysisResults result = run("simpleUnion");

        expect(result)
                .performed("module.foo()");
    }

    @Test
    public void primitiveOrObject() throws Exception {
        TajsAnalysisResults result = run("primitiveOrObject");

        expect(result)
                .performed("module.foo()");
    }

    @Test
    public void objectWithNumberProps() throws Exception {
        TajsAnalysisResults result = run("objectWithNumberProps");

        expect(result)
                .performed("module.foo()");

        expect(result)
                .performed("module.bar()");
    }

    @Test
    public void everythingIsRight() throws Exception {
        TajsAnalysisResults result = run("everythingIsRight");

        expect(result).hasNoViolations();

        expect(result)
                .performed("module.foo.bar");

        expect(result)
                .performed("module.foo");

        expect(result)
                .performed("module.foo.foo");

        expect(result)
                .performed("module");

    }

    @Test
    public void recursiveObject() throws Exception {
        TajsAnalysisResults result = run("recursiveObject");

        expect(result)
                .performed("module.foo.rec");
    }

    @Test
    public void functionAndObject() throws Exception {
        TajsAnalysisResults result = run("functionAndObject");

        expect(result)
                .performed("module.foo.foo");
    }

    /**
     * This test exposes how a sound static analysis can produce an unsound result.
     * Because what the analysis does, is say, if this code is reachable, then the following is true.
     * But if that code isn't reachable,
     */
    @Test(expected = AssertionError.class)
    public void baitingTajsUnion() throws Exception {
        TajsAnalysisResults result = run("baitingTajsUnion");

        expect(result)
                .performed("module.foo().[union0].bar.baz");

        expect(result)
                .performed("module.foo().[union1].bar.baz");
    }

    @Test
    public void spuriousUnion() throws Exception {
        TajsAnalysisResults result = run("spuriousUnion");

        expect(result)
                .performedAllTests()
                .forPath("module.foo()")
                .hasWarnings();
    }

    @Test
    public void spuriousOverload() throws Exception {
        // I wanted to make a more complicated test, but since TAJS cannot see that (typeof [bool/number] !== "string"), it has to be quite simple.
        TajsAnalysisResults result = run("spuriousOverload");

        expect(result)
                .performed("module.foo(obj)")
                .forPath("Foo");

    }

    @Test
    public void splitSignatures() throws Exception {
        TajsAnalysisResults result = run("splitSignatures");

        expect(result)
                .performedAllTests()
                .forPath("Foo");
    }

    @Test
    public void multipleFunctions() throws Exception {
        TajsAnalysisResults result = run("multipleFunctions");

        expect(result)
                .hasNoViolations();
    }

    @Test
    public void numbers() throws Exception {
        TajsAnalysisResults result = run("numbers");

        expect(result)
                .performed("module.id(number)")
                .hasNoViolations();
    }

    @Test
    public void strings() throws Exception {
        TajsAnalysisResults result = run("strings");

        expect(result)
                .performed("module.id(string)")
                .hasNoViolations();
    }

    @Test
    public void booleans() throws Exception {
        TajsAnalysisResults result = run("booleans");

        expect(result)
                .performed("module.id(boolean)")
                .hasNoViolations();
    }

    @Test
    public void numberLit() throws Exception {
        TajsAnalysisResults result = run("numberLit");

        expect(result)
                .performed("module.id(3)")
                .hasNoViolations();
    }

    @Test
    public void stringLit() throws Exception {
        TajsAnalysisResults result = run("stringLit");

        expect(result)
                .performed("module.id(\"str\")")
                .hasNoViolations();
    }

    @Test
    public void boolLit() throws Exception {
        TajsAnalysisResults result = run("boolLit");

        expect(result)
                .performed("module.id(true)")
                .hasNoViolations();
    }

    @Test
    public void primitivesFail() throws Exception {
        TajsAnalysisResults result = run("primitivesFail");

        expect(result)
                .performed("module.id(number)")
                .hasViolations();
    }

    @Test
    public void samePathFunctions() throws Exception {
        expect(run(benchFromFolder("samePathFunctions", options()).withOptions(options -> options.setSplitUnions(false))))
                .hasNoViolations();
    }

    @Test
    public void function() throws Exception {
        expect(run("function"))
                .performedAllTests()
                .forPath("foo()")
                .hasViolations();
    }

    @Test
    public void constructor() throws Exception {
        expect(run("constructor"))
                .performedAllTests()
                .forPath("construct.new()")
                .hasViolations();
    }

    @Test
    public void nestedFunctions() throws Exception {
        TajsAnalysisResults result = run("nestedFunctions");
        expect(result)
                .performedAllTests();

        expect(result)
                .forPath("module.foo().foo")
                .hasViolations();

        expect(result)
                .forPath("module.foo().bar")
                .hasNoViolations();
    }

    @Test
    public void functionCall1() throws Exception {
        run(benchFromFolder("functionCall1", options(), Benchmark.RUN_METHOD.BROWSER)); // Smoke test.
    }

    @Test
    public void numberIndexer() throws Exception {
        TajsAnalysisResults result = run("numberIndexer");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void numberIndexerFails() throws Exception {
        TajsAnalysisResults result = run("numberIndexerFails");

        expect(result)
                .forPath("foo().[numberIndexer]")
                .hasViolations();
    }

    @Test
    public void testRestArgs() throws Exception {
        TajsAnalysisResults result = run("testRestArgs");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void overloadedCallbacks() throws Exception {
        TajsAnalysisResults result = run("overloadedCallbacks");

        expect(result)
                .performedAllTests()
                .forPath("module.foo(obj)")
                .hasViolations();

        expect(result)
                .forPath("Foo")
                .hasNoViolations();
    }

    @Test
    public void overloadedCallbacks2() throws Exception {
        TajsAnalysisResults result = run("overloadedCallbacks2");

        expect(result)
                .performedAllTests()
                .forPath("Foo")
                .hasViolations();

    }
    @Test
    public void overloadedCallbacks3() throws Exception {
        TajsAnalysisResults result = run("overloadedCallbacks3");

        expect(result)
                .performedAllTests()
                .hasNoViolations();

    }

    @Test
    public void constructors2() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("constructors2", options(), Benchmark.RUN_METHOD.BROWSER));

        expect(result)
                .performedAllTests()
                .hasNoViolations();


    }

    @Test
    public void stringIndexer() throws Exception {
        TajsAnalysisResults result = run("stringIndexer");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void primitives() throws Exception {
        TajsAnalysisResults result = run("primitives", options().staticOptions);
        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void createRecursiveObject() throws Exception {
        TajsAnalysisResults result = run("createRecursiveObject", options().staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void extendsInterface() throws Exception {
        TajsAnalysisResults result = run("extendsInterface", options().staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void readUndefProp() throws Exception {
        TajsAnalysisResults result = run("readUndefProp");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void weakWrites() throws Exception {
        TajsAnalysisResults result = run("weakWrites");

        expect(result)
                .performedAllTests()
                .forPath("module.foo(obj)")
                .hasViolations();
    }

    @Test
    public void correctUnion() throws Exception {
        TajsAnalysisResults result = run("correctUnion");

        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoViolations();
    }

    @Test
    public void createConstructor() throws Exception {
        TajsAnalysisResults result = run("createConstructor", options().staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void canHaveDifferentGenerics() throws Exception {
        TajsAnalysisResults result = run("canHaveDifferentGenerics", options().staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void numberIndexer2() throws Exception {
        TajsAnalysisResults result = run("numberIndexer2");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void indirectRecursiveObjects() throws Exception {
        TajsAnalysisResults result = run("indirectRecursiveObjects", options().staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void classInheritsConstructors() throws Exception {
        TajsAnalysisResults result = run("classInheritsConstructors", options().setConstructAllTypes(true).staticOptions.setUseValuesWithMismatches(false).setPropagateStateFromFailingTest(false));

        assertThat(result.detectedViolations.asMap().keySet(), is(hasSize(3)));

        expect(result)
                .performedAllTests()
                .forPath("module.Baz.[arg0]")
                .hasViolations();

        expect(result)
                .performedAllTests()
                .forPath("module.Bar")
                .hasViolations();
    }

    @Test
    public void classInheritance() throws Exception {
        TajsAnalysisResults withConstructAll = run("classInheritance", options().setConstructAllTypes(true).staticOptions.setArgumentValuesStrategy(ONLY_CONSTRUCTED).setCreateSingletonObjects(true));

        expect(withConstructAll)
                .performedAllTests()
                .hasNoViolations();

        TajsAnalysisResults dontConstructAll = run("classInheritance", options().setConstructAllTypes(false).staticOptions.setArgumentValuesStrategy(ONLY_CONSTRUCTED).setCreateSingletonObjects(true));

        expect(dontConstructAll)
                .hasNoViolations()
                .notPerformedAllTests();
    }

    @Test
    public void createRestArgs() throws Exception {
        TajsAnalysisResults result = run("createRestArgs");

        assertThat(result.detectedViolations.asMap().keySet(), hasSize(1));

        expect(result)
                .performedAllTests()
                .forPath("Bar.[arg4]")
                .hasViolations();

    }

    @Test
    public void any() throws Exception {
        TajsAnalysisResults result = run("any");

        expect(result)
                .performedAllTests();

        expect(result)
                .forPath("module.aliasing(any)")
                .hasViolations();

        expect(result)
                .forPath("module.coerceToBool(any)")
                .hasViolations();

        expect(result)
                .forPath("module.throws(any)")
                .hasViolations();

        expect(result)
                .forPath("module.nestedProps(any)")
                .hasNoViolations();
    }

    @Test
    public void createRecursiveObject2() throws Exception {
        TajsAnalysisResults result = run("createRecursiveObject2");

        expect(result)
                .performedAllTests()
                .hasViolations();
    }

    @Test
    @Ignore // Not used anymore.
    public void fromTSFile() throws Exception {
        TajsAnalysisResults result = run(benchFromFolderTSFile("fromTSFile", options(), Benchmark.RUN_METHOD.BROWSER));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    @Ignore // TODO: Fails because the keys of constructed objects aren't iterated when doing a for-in loop.
    public void copiesFunctions() throws Exception {
        TajsAnalysisResults result = run(benchFromFolderTSFile("copiesFunctions", options(), Benchmark.RUN_METHOD.BROWSER));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    @Ignore // TODO: Fails exactly because making an object a summery adds a possible "undefined" on all properties.
    public void weakReadsGiveUndefined() throws Exception {
        TajsAnalysisResults result = run("weakReadsGiveUndefined");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void readProperty() throws Exception {
        TajsAnalysisResults result = run("readProperty", options().staticOptions.setCreateSingletonObjects(true));
        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void createUnionsOfDateAndFunction() throws Exception {
        TajsAnalysisResults result = run("createUnionsOfDateAndFunction", options().setSplitUnions(false).staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }
    @Test
    public void checkRecursiveObject() throws Exception {
        TajsAnalysisResults result = run("checkRecursiveObject");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void soundness1() throws Exception {
        soundness("soundness1", options -> options.staticOptions.setCreateSingletonObjects(true));
    }

    @Test
    public void soundness2() throws Exception {
        soundness("soundness2", options -> options.staticOptions.setCreateSingletonObjects(true));
    }

    @Test
    public void smokeTest1() throws Exception {
        run(benchFromFolder("smokeTest1", options(), Benchmark.RUN_METHOD.BROWSER));
    }

    @Test
    public void instanceOfSmokeTest() throws Exception {
        TajsAnalysisResults result = run("instanceOfSmokeTest", options().setSplitUnions(false).staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoWarnings()
                .hasNoViolations();
    }

    @Test(timeout = 20000)
    public void createWeakObjects() throws Exception {
        run("createWeakObjects");
    }

    @Test
    @Ignore // TODO: Fails because we never generate an object that has the same identity as an object created internally by the library.
    public void objectIdentity() throws Exception {
        TajsAnalysisResults result = run("objectIdentity");

        expect(result)
                .performedAllTests()
                .forPath("module.test(obj)")
                .hasViolations();
    }

    @Test
    public void motivatingPath() throws Exception {
        Benchmark bench = benchFromFolder("motivatingPath", options(), Benchmark.RUN_METHOD.BROWSER);

        TajsAnalysisResults result = run(bench);
        expect(result)
                .performedAllTests()
                .forPath("window.Path.map.().exit.[arg0]")
                .hasViolations();

        // the dynamic testing can also find the error.
        OutputParser.RunResult dynamicResult = UnitTests.run(bench, "foo");
        UnitTests.expect(dynamicResult)
                .forPath("window.Path.map.().exit.[arg0]")
                .expected("1 parameters")
                .got(STRING, "0 parameters");
    }

    @Test
    public void leafletMotivatingBuggy() throws Exception {
        TajsAnalysisResults buggy = run(benchFromFolder("leafletMotivating/buggy", options()
                        .setConstructAllTypes(true)
                        .staticOptions
                            .setUseInspector(false)
                            .setCreateSingletonObjects(true)
                            .setArgumentValuesStrategy(FEEDBACK_IF_POSSIBLE)
                            .setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments())
                , Benchmark.RUN_METHOD.BROWSER));


        System.out.println(buggy);

        expect(buggy)
                .hasViolations();
    }

    @Test
    public void leafletMotivatingFixed() throws Exception {
        TajsAnalysisResults fixed = run(benchFromFolder("leafletMotivating/fixed",
                options()
                        .setConstructAllTypes(true)
                        .setSplitUnions(true)
                        .staticOptions
                            .setUseInspector(false)
                            .setCreateSingletonObjects(true)
                            .setArgumentValuesStrategy(FEEDBACK_IF_POSSIBLE)
                            .setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments()
                        )
                , Benchmark.RUN_METHOD.BROWSER));

        System.out.println(fixed);

        expect(fixed)
                .hasNoWarnings()
                .hasNoViolations();
    }

    @Test
    public void feedbackValuesReadUndefined() throws Exception {
        TajsAnalysisResults fixed = run("feedbackValuesReadUndefined", options().setConstructAllTypes(false).staticOptions.setUseInspector(false));

        System.out.println(fixed);

        expect(fixed)
                .hasNoWarnings()
                .hasNoViolations()
                .performedAllTests();
    }

    @Test
    public void retract() throws Exception {
        StaticOptions.Builder options = options().staticOptions.setRetractionPolicy(new LimitTransfersRetractionPolicy(100, 0)).setExpansionPolicy(new FixedExpansionOrder("module.toRetract(string)", "module.returnsBool()"));

        TajsAnalysisResults result = run("retract", options);

        System.out.println(result);

        expect(result)
                .hasNoViolations();
    }

    @Test
    public void readableObjectErrors() throws Exception {
        TajsAnalysisResults result = run("readableObjectErrors");

        System.out.println(result);

        assertThat(result.detectedViolations.keySet(), hasSize(1));
    }

    @Test // Tests that the order in which tests are executed does not matter.
    public void discardSpuriousValues() throws Exception {
        List<String> executionOrder = Arrays.asList("module.foo()", "module.foo().baz()", "module.bar()");

        TajsAnalysisResults resultNoOrder = run("discardSpuriousValues", options());

        TajsAnalysisResults resultFixedExpansion = run("discardSpuriousValues", options().staticOptions.setExpansionPolicy(new FixedExpansionOrder(executionOrder)));

        System.out.println("With default expansion-policy");
        System.out.println(resultNoOrder);

        System.out.println("With fixed expansion");
        System.out.println(resultFixedExpansion);

        assertThat(resultNoOrder.detectedViolations.keySet(), is(equalTo(resultFixedExpansion.detectedViolations.keySet())));
    }

    @Test
    public void discardSpuriousValuesHigherOrder() throws Exception {
        List<String> executionOrder = Arrays.asList("module.gen(obj)", "module.gen.[arg0].[arg0].foo()", "module.gen.[arg0].[arg0].foo().baz()", "module.gen.[arg0].[arg0].bar()");

        TajsAnalysisResults resultNoOrder = run("discardSpuriousValuesHigherOrder", options());

        TajsAnalysisResults resultFixedExpansion = run("discardSpuriousValuesHigherOrder", options().staticOptions.setExpansionPolicy(new FixedExpansionOrder(executionOrder)));

        System.out.println("With default expansion-policy");
        System.out.println(resultNoOrder);

        System.out.println("With fixed expansion");
        System.out.println(resultFixedExpansion);

        assertThat(resultNoOrder.detectedViolations.keySet(), is(equalTo(resultFixedExpansion.detectedViolations.keySet())));

        expect(resultFixedExpansion)
                .hasViolations();
        expect(resultNoOrder)
                .hasViolations();
    }

    @Test
    public void asyncError() throws Exception {
        TajsAnalysisResults result = run("asyncError");

        System.out.println(result);
        expect(result)
                .performedAllTests()
                .forPath("window.module.foo.[arg0]")
                .hasViolations();

    }

    @Test
    public void noStateFromFailingMethods() throws Exception {
        TajsAnalysisResults resultNoPropagate = run("noStateFromFailingMethods", options().staticOptions.setPropagateStateFromFailingTest(false).setUseValuesWithMismatches(false));

        assertThat(resultNoPropagate.detectedViolations.keySet(), hasSize(1));

        TajsAnalysisResults resultDoPropagate = run("noStateFromFailingMethods", options().staticOptions.setPropagateStateFromFailingTest(true).setUseValuesWithMismatches(false));

        assertThat(resultDoPropagate.detectedViolations.keySet(), hasSize(2));
    }

    @Test
    public void cannotHaveNonMonotomeState() throws Exception {
        // If we retracted state, this would run in an infinite loop.
        TajsAnalysisResults result = run("cannotHaveNonMonotomeState", options().staticOptions.setUseValuesWithMismatches(false).setPropagateStateFromFailingTest(false));

        expect(result)
                .notPerformedAllTests();
    }

    @Test
    public void restArgsSoundness() throws Exception {
        TajsAnalysisResults result = soundness("restArgsSoundness", options -> options.staticOptions.setCreateSingletonObjects(true));
        System.out.println(result);
        expect(result)
                .performedAllTests();
    }

    @Test
    public void higherOrderFunctionCompleteness() throws Exception {
        TajsAnalysisResults result = soundness("higherOrderFunctionCompleteness", options -> options.staticOptions.setCreateSingletonObjects(true));
        System.out.println(result);
        expect(result)
                .performedAllTests();
    }

    @Test
    public void multiSignatureSoundness() throws Exception { // The actual issue is that the two arrays are allocated in the same object-label.
        TajsAnalysisResults result = run("multiSignatureSoundness");
        System.out.println(result);
        expect(result)
                .performedAllTests()
                .hasNoViolations();

    }

    @Test
    public void createStringIndexer() throws Exception {
        TajsAnalysisResults result = run("createStringIndexer", options().staticOptions.setCreateSingletonObjects(true));

        System.out.println(result);

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void createStringIndexer2() throws Exception {
        TajsAnalysisResults result = run("createStringIndexer2");
        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void createStringIndexer3() throws Exception {
        TajsAnalysisResults result = run("createStringIndexer3");
        expect(result)
                .performedAllTests()
                .hasNoWarnings()
                .hasNoViolations();
    }

    @Test
    public void createStringIndexer4() throws Exception {
        TajsAnalysisResults result = run("createStringIndexer4");
        expect(result)
                .performedAllTests()
                .hasNoWarnings()
                .hasNoViolations();
    }

    @Test
    public void functionProperties() throws Exception {
        TajsAnalysisResults result = run("functionProperties", options());

        expect(result)
                .performedAllTests()
                .hasNoWarnings()
                .hasNoViolations();
    }

    @Test
    @Ignore // we don't yet model a functions .prototype (and the .constructor on that .prototype).
    public void functionProperties2() throws Exception {
        TajsAnalysisResults result = run("functionProperties2", options());

        expect(result)
                .performedAllTests()
                .hasNoWarnings()
                .hasNoViolations();
    }

    @Test
    public void compareWithTheAny() throws Exception {
        TajsAnalysisResults result = run("compareWithTheAny");

        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoWarnings();
    }

    @Test
    public void polutionFromNeighbour() throws Exception {
        TajsAnalysisResults result = run("polutionFromNeighbour", options());

        String path = "L.testMethod()";
        assertFalse(
                result.detectedViolations.containsKey(path) &&
                result.detectedViolations.get(path).iterator().next().toString().trim().equals("Expected string but found 2.0 in test " + path)
        );
    }

    @Test
    public void timeout() throws Exception {
        TajsAnalysisResults result = run("timeout", options().staticOptions
                .setExpansionPolicy(new FixedExpansionOrder("module.foo()", "module.polute()"))
                .setRetractionPolicy(new LimitTransfersRetractionPolicy(100, 2))
        );


        assertThat(result.timeoutTests, hasSize(1));
        assertThat(result.timeoutTests.iterator().next().getPath(), is(equalTo("module.foo()")));

        expect(result)
                .forPath("module.foo().foo")
                .hasViolations();

    }

    @Test
    public void higherOrderIncludesCallsite() throws Exception {
        TajsAnalysisResults result = run("higherOrderIncludesCallsite");

        System.out.println(result);

        assertThat(result.detectedViolations.asMap().entrySet(), hasSize(2));
    }

    @Test
    public void dontConstructClassInstances() throws Exception {
        TajsAnalysisResults result = run("dontConstructClassInstances", options()
                .setSplitUnions(false)
                .setConstructAllTypes(false)
                .setConstructClassInstances(false)
                .setConstructClassTypes(false)
                .staticOptions.setArgumentValuesStrategy(MIX_FEEDBACK_AND_CONSTRUCTED)
        );

        expect(result)
                .performedAllTests()
                .hasNoWarnings()
                .hasNoViolations();

        TajsAnalysisResults withSplitUnions = run("dontConstructClassInstances", options()
                .setSplitUnions(true)
                .setConstructAllTypes(false)
                .setConstructClassInstances(false)
                .setConstructClassTypes(false)
                .staticOptions.setArgumentValuesStrategy(MIX_FEEDBACK_AND_CONSTRUCTED)
//                .setUseInspector(true)
        );

        expect(withSplitUnions)
                .performedAllTests()
                .hasNoWarnings()
                .hasNoViolations();
    }

    @Test
    public void mixConstructedAndFeedbackValues() throws Exception {
        TajsAnalysisResults result = run("mixConstructedAndFeedbackValues", options().setConstructAllTypes(true).staticOptions.setArgumentValuesStrategy(MIX_FEEDBACK_AND_CONSTRUCTED));

        expect(result)
                .performedAllTests()
                .hasNoWarnings()
                .hasNoViolations();
    }

    @Test
    public void instanceofTest() throws Exception {
        TajsAnalysisResults result = run("instanceof");

        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoWarnings();
    }

    @Test
    public void properWidthSubtyping() throws Exception {
        TajsAnalysisResults result = run("properWidthSubtyping", options().staticOptions.setProperWidthSubtyping(true));

        expect(result)
                .performedAllTests()
                .hasNoWarnings()
                .hasNoViolations();
    }

    @Test
    public void stringIndexCheck() throws Exception {
        TajsAnalysisResults result = run("stringIndexCheck");
        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoWarnings();
    }

    @Test
    public void stringIndexCheck2() throws Exception {
        TajsAnalysisResults result = run("stringIndexCheck2");

        System.out.println(result);

        expect(result)
                .forPath("module.noError()")
                .hasNoViolations()
                .hasNoWarnings();

        expect(result)
                .forPath("module.hasError()")
                .hasViolations();
    }

    @Test
    public void lateExpansion() throws Exception {
        TajsAnalysisResults result = run("lateExpansion", options().staticOptions.setExpansionPolicy(new DelayAllTestsExpansionPolicy()));

        expect(result)
                .performedAllTests()
                .hasNoViolations();

        TajsAnalysisResults evenLater = run("lateExpansion", options().staticOptions.setExpansionPolicy(new ExpandOneAtATimeWhenWorkListEmpty()));

        expect(evenLater)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void lateExpansion2() throws Exception {
        TajsAnalysisResults result = run("lateExpansion2", options().staticOptions.setExpansionPolicy(new DelayAllTestsExpansionPolicy()));

        expect(result)
                .forPath("module.bar().bar")
                .hasViolations();

        TajsAnalysisResults evenLater = run("lateExpansion2", options().staticOptions.setExpansionPolicy(new ExpandOneAtATimeWhenWorkListEmpty()));

        expect(evenLater)
                .forPath("module.bar().bar")
                .hasViolations();
    }

    @Test
    public void emptyObjectHasToString() throws Exception {
        TajsAnalysisResults result = run("emptyObjectHasToString");

        expect(result)
                .performedAllTests()
                .hasNoWarnings()
                .hasNoViolations();
    }

    @Test
    public void useFeedbackIfAvailable() throws Exception {
        TajsAnalysisResults withManualExpansionOrder = run("useFeedbackIfAvailable", options().staticOptions
                .setArgumentValuesStrategy(FEEDBACK_IF_POSSIBLE)
                .setExpansionPolicy(new FixedExpansionOrder("module.produceFoo()", "module.produceBar()", "module.useFoo(obj)", "module.useBar(obj)"))
        );

        assertThat(withManualExpansionOrder.detectedViolations.asMap().entrySet(), hasSize(1));

        expect(withManualExpansionOrder)
                .forPath("module.useFoo(obj)")
                .hasNoViolations()
                .hasNoWarnings();

        TajsAnalysisResults automaticExpansionOrder = run("useFeedbackIfAvailable", options().staticOptions
                .setArgumentValuesStrategy(FEEDBACK_IF_POSSIBLE)
                .setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments())
        );

        assertThat(automaticExpansionOrder.detectedViolations.asMap().entrySet(), hasSize(1));

        expect(automaticExpansionOrder)
                .forPath("module.useFoo(obj)")
                .hasNoViolations()
                .hasNoWarnings();
    }

    @Test
    public void noFeedbackCausesException() throws Exception {
        TajsAnalysisResults result = run("noFeedbackCausesException", options()
                .staticOptions
                    .setUseInspector(false)
                    .setArgumentValuesStrategy(FEEDBACK_IF_POSSIBLE)
                    .setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments())
        );

        expect(result)
                .hasNoViolations();
    }

    @Test
    public void callbacksAreConstructedEarly() throws Exception {
        TajsAnalysisResults result = run("callbacksAreConstructedEarly", options().staticOptions
                .setArgumentValuesStrategy(FEEDBACK_IF_POSSIBLE)
                .setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments())
        );

        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoWarnings();
    }

    @Test
    public void harmfulSideEffectsAreCatched() throws Exception {
        TajsAnalysisResults result = run("harmfulSideEffectsAreCatched", options().staticOptions
                .setCheckAllPropertiesAfterFunctionCall(true)
                .setPropagateStateFromFailingTest(false)
                .setUseValuesWithMismatches(false)
        );

        expect(result)
                .performedAllTests() // <- that is the real test, that all tests are still performed.
                .hasViolations();
    }

    @Test
    public void writeToPrimitives() throws Exception {
        TajsAnalysisResults result = run("writeToPrimitives", options()
                .setWritePrimitives(true)
                .staticOptions
                    .setArgumentValuesStrategy(FEEDBACK_IF_POSSIBLE)
                    .setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments())
        );

        expect(result)
                .performedAllTests()
                .forPath("module.testFoo(obj)")
                .hasViolations();


    }

    @Test
    public void canConstructUint8Array() throws Exception {
        TajsAnalysisResults result = run("canConstructUint8Array");

        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoWarnings();
    }

    @Test
    public void instanceofChecks() throws Exception {
        TajsAnalysisResults result = run("instanceofChecks");

        assertThat(result.detectedViolations.asMap().entrySet(), hasSize(1));

        expect(result)
                .performedAllTests()
                .forPath("module.baz()")
                .hasViolations();
    }

    @Test
    public void readPropertyFromProto() throws Exception {
        TajsAnalysisResults result = run("readPropertyFromProto");

        expect(result)
                .performedAllTests()
                .hasNoWarnings()
                .hasNoViolations();
    }

    @Test
    public void directExport() throws Exception {
        TajsAnalysisResults result = run("directExport", options().staticOptions.setUseInspector(false));

        expect(result)
                .performedAllTests()
                .hasNoWarnings()
                .hasNoViolations();
    }

    @Test
    public void exceptionalFlow() throws Exception {
        TajsAnalysisResults result = run("exceptionalFlow");

        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoWarnings();
    }

    @Test
    public void singletonInstanceof() throws Exception {
        TajsAnalysisResults singletons = run(benchFromFolder("singletonInstanceof",
                options()
                        .staticOptions
                            .setUseInspector(false)
                            .setCreateSingletonObjects(true)
        ));

        expect(singletons)
                .hasNoViolations();

        TajsAnalysisResults summaries = run(benchFromFolder("singletonInstanceof",
                options()
                        .staticOptions
                            .setUseInspector(false)
                            .setCreateSingletonObjects(false)
        ));

        expect(summaries)
                .hasNoViolations();
    }

    @Test
    public void definiteExceptions() throws Exception {
        /*
            function foo00(): string; // <- returns bool | string (maybe error).
            function foo0(): string; // <- returns bool (definite error).
            function foo1(): string | number; // <- returns bool (definite error).
            function foo2(): string | boolean; // <- returns bool | number (maybe error).
            function foo3(): {foo: boolean} // returns {foo: string} (definite error)
            function foo4(): {foo: boolean} // returns {foo: string | boolean} (maybe error)
            function foo5(): {foo: boolean} // returns {foo: string} | {foo: boolean} (maybe error)
         */
        TajsAnalysisResults result = run("definiteExceptions", options().staticOptions.setUseValuesWithMismatches(false).setPropagateStateFromFailingTest(false));

        System.out.println(result);

        expect(result)
                .hasViolations();

        //noinspection ResultOfMethodCallIgnored
        result.detectedViolations.asMap().values().forEach(violations -> violations.stream().reduce((a, b) -> {assert a.definite == b.definite; return a;}));

        assertThat(result.detectedViolations.get("module.foo00()").iterator().next().definite, is(false));
        assertThat(result.detectedViolations.get("module.foo0()").iterator().next().definite, is(true));
        assertThat(result.detectedViolations.get("module.foo1()").iterator().next().definite, is(true));
        assertThat(result.detectedViolations.get("module.foo2()").iterator().next().definite, is(false));
        assertThat(result.detectedViolations.get("module.foo3().foo").iterator().next().definite, is(true));
        assertThat(result.detectedViolations.get("module.foo4().foo").iterator().next().definite, is(false));
        assertThat(result.detectedViolations.get("module.foo5().foo").iterator().next().definite, is(false));
    }

    @Test
    public void constructBasicIntersection() throws Exception {
        TajsAnalysisResults result = run("constructBasicIntersection");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void recursiveFeedback() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("recursiveFeedback", options().staticOptions
                .setArgumentValuesStrategy(FEEDBACK_IF_POSSIBLE)
                .setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments())
            , Benchmark.RUN_METHOD.BROWSER));

        assertThat(result.exceptionsEncountered.asMap().entrySet(), is(empty()));
    }

    @Test
    public void constructNodeList() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("constructNodeList", options(), Benchmark.RUN_METHOD.BROWSER));

        expect(result)
                .hasNoViolations()
                .performedAllTests()
                .hasNoWarnings();
    }

    @Test
    @Ignore // TODO: Something with Object.assign.
    public void emptyValueException() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("emptyValueException", options(), Benchmark.RUN_METHOD.BROWSER));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void constructArrayElements() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder(
                "constructArrayElements",
                    options()
                        .setConstructAllTypes(true)
                        .staticOptions
                            .setArgumentValuesStrategy(FEEDBACK_IF_POSSIBLE)
                            .setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments()
                        )
                , Benchmark.RUN_METHOD.BROWSER));

        expect(result)
                .hasNoViolations()
                .hasNoWarnings()
                .performedAllTests();
    }

    @Test // smoke test, it just shouldn't crash.
    public void undefinedRegisterException() throws Exception {
        run("undefinedRegisterException");
    }

    @Test
    @Ignore  // https://github.com/cs-au-dk/TAJS-private/issues/523
    public void forInOnPrototypeProperties() throws Exception {
        String out = Util.runNodeScript("test/tajsUnit/forInOnPrototypeProperties/testImpl.js");

        // Precondition: When concretely executed, it works fine.
        assertThat(out.trim(), is("[Function: wrap]"));

        // When TAJS executes it, it does not.
        TajsAnalysisResults result = run("forInOnPrototypeProperties");
        System.out.println(result);

        // If there is an exception, is must not be definite.
        if (result.detectedViolations.containsKey("\"axios\".request")) {
            assertThat(result.detectedViolations.get("\"axios\".request").size(), is(1));
            assertThat(result.detectedViolations.get("\"axios\".request").iterator().next().definite, is(false));
        }
    }

    @Test
    public void maybeViolationsOnLiterals() throws Exception {
        TajsAnalysisResults result = run("maybeViolationsOnLiterals");

        assertThat(result.detectedViolations.asMap().entrySet(), hasSize(3));

        result.detectedViolations.asMap().entrySet().stream().map(Map.Entry::getValue).flatMap(Collection::stream).forEach(violation -> {
            System.out.println(violation.path);
            assertThat(violation.definite, is(false));
        });
    }

    @Test(timeout = 20000)
    public void creatingObjectResultsInInfiniteLoop() throws Exception {
        TajsAnalysisResults result = run("creatingObjectResultsInInfiniteLoop");
    }

    @Test
    public void useNativeTwice() throws Exception {
        TajsAnalysisResults result = run("useNativeTwice");

        expect(result)
                .performedAllTests();

        assertThat(result.detectedViolations.asMap().entrySet(), hasSize(1));

        expect(result)
                .forPath("module.foo(obj)")
                .hasNoViolations()
                .hasNoWarnings();

        expect(result)
                .forPath("module.bar(obj)")
                .hasViolations();
    }

    @Test
    @Ignore // goes into infinite loop. Because somehow the bar property keeps getting new data-flow (likely because i forget to propagate something, I just cant find it).
    public void useNativeTwiceUint8Array() throws Exception {
        TajsAnalysisResults result = run("useNativeTwiceUint8Array");

        expect(result)
                .performedAllTests();

        assertThat(result.detectedViolations.asMap().entrySet(), hasSize(1));

        expect(result)
                .forPath("module.foo(obj)")
                .hasNoViolations()
                .hasNoWarnings();

        expect(result)
                .forPath("module.bar(obj)")
                .hasViolations();
    }

    @Test
    public void createNatives() throws Exception {
        TajsAnalysisResults result = run("createNatives", options().staticOptions.setUseInspector(false));

        System.out.println(result);

        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoWarnings();
    }

    @Test
    public void introMotivating() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("introMotivating", options(), Benchmark.RUN_METHOD.BROWSER));

        System.out.println(result);

        assertThat(result.detectedViolations.asMap().entrySet(), hasSize(1));

        assertThat(result.detectedViolations.asMap().keySet().iterator().next(), startsWith("window.introJs.().onhintclose.[arg0].[arg0]"));

        Collection<TypeViolation> violations = result.detectedViolations.asMap().values().iterator().next();

        assert violations.stream().map(Object::toString).anyMatch(violation -> violation.startsWith("Definite: Expected number but found Str in test window.introJs.().onhintclose.[arg0].[arg0]"));

    }

    @Test
    public void constructAnyUnion() throws Exception {
        run("constructAnyUnion");
    }

    @Test
    public void arrayTakesToLong() throws Exception {
        // the implementation in this is a straight copy from accounting.js.
        TajsAnalysisResults result = run(benchFromFolder("arrayTakesToLong", options().staticOptions.setRetractionPolicy(new LimitTransfersRetractionPolicy(1000, 0)), Benchmark.RUN_METHOD.BROWSER));

        System.out.println(result);

        expect(result)
                .hasNoWarnings()
                .hasNoViolations();

        assertThat(result.timeoutTests, is(empty()));
    }

    @Test
    public void arraySplit() throws Exception {
        TajsAnalysisResults result = run("arraySplit");

        assertThat(result.exceptionsEncountered.asMap().entrySet(), is(empty()));

        expect(result)
                .performedAllTests()
                .hasNoWarnings()
                .hasNoViolations();
    }

    @Test
    public void nativeFunction() throws Exception {
        TajsAnalysisResults result = run("nativeFunction");

        expect(result)
                .hasNoViolations()
                .hasNoWarnings()
                .performedAllTests();
    }

    @Test//(timeout = 20000)
    public void loopsIfCannotConstructType() throws Exception {
        TajsAnalysisResults mix = run(benchFromFolder("loopsIfCannotConstructType", options().setConstructAllTypes(false).staticOptions.setArgumentValuesStrategy(MIX_FEEDBACK_AND_CONSTRUCTED), Benchmark.RUN_METHOD.BROWSER));

        expect(mix)
                .hasNoViolations()
                .hasNoWarnings();

        TajsAnalysisResults feedbackIfPossible = run(benchFromFolder("loopsIfCannotConstructType", options().setConstructAllTypes(false).staticOptions.setArgumentValuesStrategy(FEEDBACK_IF_POSSIBLE), Benchmark.RUN_METHOD.BROWSER));

        expect(feedbackIfPossible)
                .hasNoViolations()
                .hasNoWarnings();

        TajsAnalysisResults onlyConstructed = run(benchFromFolder("loopsIfCannotConstructType", options().setConstructAllTypes(false).staticOptions.setArgumentValuesStrategy(ONLY_CONSTRUCTED), Benchmark.RUN_METHOD.BROWSER));

        expect(onlyConstructed)
                .hasNoViolations()
                .hasNoWarnings();

    }

    @Test
    public void mixFeedback() throws Exception {
        TajsAnalysisResults result = run("mixFeedback", options().setConstructAllTypes(true).staticOptions.setCreateSingletonObjects(false).setArgumentValuesStrategy(MIX_FEEDBACK_AND_CONSTRUCTED));

        expect(result)
                .performedAllTests()
                .hasViolations();
    }

    @Test
    @Ignore // this shows a precision-issue, where we are unable to keep different objects in the heap separate. It seems to be related to allocation-site-abstraction, since the latest (which is singleton due to recency) does not experience any error.
    public void precisionIssue1() throws Exception {
        TajsAnalysisResults result = run("precisionIssue1");

        expect(result)
                .hasNoViolations()
                .hasNoWarnings()
                .performedAllTests();
    }

    @Test
    @Ignore // This might not be an issue at all. It is caused the "any" value being the same across function-calls, and a side-effects therefore leaks. A side-effects which is technically possible of the same object is used, however it is highly unlikely.
    public void precisionIssue2() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("precisionIssue2", options(), Benchmark.RUN_METHOD.BROWSER));

        expect(result)
                .hasNoViolations()
                .hasNoWarnings()
                .performedAllTests();
    }

    @Test
    @Ignore // it is really the same as precisionIssue1.
    public void precisionIssue3() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("precisionIssue3", options().staticOptions.setArgumentValuesStrategy(MIX_FEEDBACK_AND_CONSTRUCTED), Benchmark.RUN_METHOD.BROWSER));

        expect(result)
                .hasNoViolations()
                .hasNoWarnings()
                .performedAllTests();
    }

    @Test
    @Ignore // methods are only weakly written to the global object.
    public void precisionIssue4() throws Exception {
        TajsAnalysisResults result = run("precisionIssue4");

        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoWarnings();
    }

    @Test // was fixed.
    public void precisionIssue5() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("precisionIssue5", options().staticOptions.setCreateSingletonObjects(true), Benchmark.RUN_METHOD.BROWSER));

        expect(result)
                .hasNoViolations();
    }


    @Test
    public void noValueFound() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("noValueFound", AnalyzeBenchmarks.options().apply(CheckOptions.builder()), Benchmark.RUN_METHOD.BROWSER));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void multipleConstructors() throws Exception {
        run("multipleConstructors");
    }

    @Test
    public void dontReportFakeField() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("dontReportFakeField", options(), Benchmark.RUN_METHOD.BROWSER));
        System.out.println(result);

        int fakeFieldViolations = (int) result.detectedViolations.keySet().stream().filter(key -> key.contains("fakeFieldPath")).count();

        assertThat(fakeFieldViolations, is(0));
    }

    @Test
    public void isArray() throws Exception {
        TajsAnalysisResults result = run("isArray");

        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoWarnings();
    }

    @Test
    public void wrongIn() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("wrongIn", AnalyzeBenchmarks.options().apply(CheckOptions.builder()), Benchmark.RUN_METHOD.NODE));

        expect(result)
                .hasViolations();
    }


    @Test
    @Ignore // The "is undefined" violation is not definite, there is an object. But because that object fails its type-check, it is marked as a definite instead of a maybe.
    // Can properly get around in the places where i split the value by checking if the different splits have violations in different locations. In which case the top-most location is a maybe.
    public void definiteIssue() throws Exception {
        TajsAnalysisResults result = run("definiteIssue");

        assert result.detectedViolations.asMap().values().stream().flatMap(Collection::stream).anyMatch(violation -> !violation.definite);
    }

    @Test
    public void suppressionsAllowFeedback() throws Exception {
        TajsAnalysisResults result = run("suppressionAllowsFeedback");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    @Ignore
    public void weirdSideEffect() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("weirdSideEffect", AnalyzeBenchmarks.options().apply(CheckOptions.builder()), Benchmark.RUN_METHOD.BROWSER));

        System.out.println(result);

        expect(result)
                .hasNoViolations();
    }


    @Test
    public void precisionIssue6() throws Exception {
        TajsAnalysisResults result = run("precisionIssue6");

        System.out.println(result);

        expect(result)
                .hasNoViolations();
    }

    @Test
    public void optionalArguments() throws Exception {
        TajsAnalysisResults result = run("optionalArguments");

        expect(result)
                .performedAllTests()
                .hasNoViolations();

    }

    @Test
    public void extendsArray() throws Exception {
        TajsAnalysisResults result = run("extendsArray");

        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoWarnings();
    }

    @Test
    public void multipleStringLiterals() throws Exception {
        TajsAnalysisResults result = run("multipleStringLiterals");

        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoWarnings();
    }

    @Test
    @Ignore // TODO: I can't get this one to make sense.
    public void noneObject() throws Exception {
        TajsAnalysisResults result = run("noneObject", options()
                .setSplitUnions(false)
                .staticOptions
                .setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.FEEDBACK_IF_POSSIBLE)
        );

        List<TypeViolation> violations = result.detectedViolations.asMap().values().stream().flatMap(Collection::stream).collect(Collectors.toList());

        assert violations.stream().map(Object::toString).noneMatch(str -> str.contains("{<none>}"));
    }

    @Test
    public void genericConstraints() throws Exception {
        TajsAnalysisResults result = run("genericConstraints", options().staticOptions.setUseValuesWithMismatches(true).setPropagateStateFromFailingTest(true));

        expect(result)
                .performedAllTests();
    }

    @Test
    public void ignoreMaybeUndef() throws Exception {
        TajsAnalysisResults result = run("ignoreMaybeUndef");

        expect(result)
                .performedAllTests()
                .hasViolations();

        result = run("ignoreMaybeUndef", options().staticOptions.setIgnoreMaybeUndefined(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    @Ignore // TODO: mez.
    public void propReadFromTwoObjectMaybeUndef() throws Exception {
        TajsAnalysisResults result = run("propReadFromTwoObjectMaybeUndef", options().setSplitUnions(false)); // for benchmarks, unions are not being split, because it for some benchmarks results in an explosion of signatures.

        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoWarnings();
    }

    @Test
    @Ignore // TODO: Very likely related to the above. But I have no idea what is going on.
    public void weirdObjectWrite() throws Exception {
        TajsAnalysisResults result = run("weirdObjectWrite");

        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoWarnings();
    }

    @Test
    public void higherOrderFunctionSideEffects1() throws Exception {
        TajsAnalysisResults result = run("higherOrderFunctionSideEffects");

        expect(result)
                .hasViolations();
   }

    @Test
    @Ignore
    public void anyUndeclaredEnumerable() throws Exception {
        TajsAnalysisResults result = run("anyUndeclaredEnumerable", options().staticOptions.setProperWidthSubtyping(true));

        expect(result)
                .hasNoViolations();
    }

    @Test
    public void blockStateFromCallback() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("blockStateFromCallback", options().staticOptions.setCheckAllPropertiesAfterFunctionCall(true).setPropagateStateFromFailingTest(false)));

        expect(result)
                .hasViolations();


        assertThat(result.toString(), containsString("callback"));
    }

    @Test
    public void multiObjectError() throws Exception {
        TajsAnalysisResults result = run("multiObjectError");

        assertThat(result.detectedViolations.size(), is(2));
    }

    @Test
    public void callbacksMgcFalsePositive() throws Exception {
        TajsAnalysisResults mgc = run("callbacksMgcFalsePositive", options().staticOptions.setCallbacksAreMGC(true));

        TajsAnalysisResults notMgc = run("callbacksMgcFalsePositive", options().staticOptions.setCallbacksAreMGC(false));

        expect(mgc)
                .forPath("redux.createStore().<>.getState()")
                .hasViolations();

        expect(notMgc)
                .forPath("redux.createStore().<>.getState()")
                .hasNoViolations();
    }

    @Test
    public void notWriteReadonly() throws Exception {
        TajsAnalysisResults result = run("notWriteToReadOnly", options().setWritePrimitives(true));

        expect(result)
                .forPath("module.readsFoo")
                .hasNoViolations();

        expect(result)
                .forPath("module.readsBar")
                .hasViolations();
    }


    @Test
    public void warningAboutReadOfStdLibValue() throws Exception {
        TajsAnalysisResults results = run("warningsReadFromStdlib");
        assertThat(results.possiblyProblematicReads.stream().map(r -> r.getSourceLocation().toString()).distinct().collect(Collectors.toList()), hasSize(3));
    }

    @Test
    public void warningsReadFromStdlibPrimitive() throws Exception {
        TajsAnalysisResults results = run("warningsReadFromStdlibPrimitive");
        assertThat(results.possiblyProblematicReads.stream().map(r -> r.getSourceLocation().toString()).distinct().collect(Collectors.toList()), hasSize(3));
    }

    @Test
    @Ignore
    public void semverParse() throws Exception {
        TajsAnalysisResults result = run("semverParse", options()
                .setSplitUnions(false)
                .setConstructClassInstances(false)
                .setConstructClassTypes(false)
                .staticOptions
                .setUseInspector(false)
        );
        expect(result)
                .hasNoViolations();
    }

    @Test
    public void ignoreTypes() throws Exception {
        expect(run("ignoreTypes", options()))
                .hasNoViolations();

        expect(run("ignoreTypes", options().staticOptions.setIgnoreTypeDecs(true)))
                .hasViolations();
    }


    @Test
    public void newTypeCreation() throws Exception {
        TajsAnalysisResults result = run("newTypeCreation", options().staticOptions
                .setUseValuesWithMismatches(true)
                .setPropagateStateFromFailingTest(true)
                .setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.FEEDBACK_IF_POSSIBLE)
                .setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments())

                .setInstantiationFilter(new CopyObjectInstantiation())
        );

        assert result.exceptionsEncountered.isEmpty();

        assertThat(result.detectedViolations.size(), is(1));

        expect(result)
                .forPath("module.getFoo().dirty")
                .hasViolations();
    }

    @Test
    public void typeFilterMultipleObjects() throws Exception {
        TajsAnalysisResults result = run("typeFilterMultipleObjects", options().staticOptions
                .setUseValuesWithMismatches(true)
                .setPropagateStateFromFailingTest(true)
                .setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.FEEDBACK_IF_POSSIBLE)
                .setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments())


//                .setUseInspector(true)

                .setInstantiationFilter(new CopyObjectInstantiation())
        );

        assert result.exceptionsEncountered.isEmpty();

        assertThat(result.detectedViolations.keySet(), hasSize(1));

        expect(result)
                .forPath("module.getFoo().dirty")
                .hasViolations();
    }

    @Test
    public void widthSubtypingMotivating() throws Exception {
        TajsAnalysisResults result = run("widthSubtypingMotivating", options().staticOptions.setProperWidthSubtyping(true));

        expect(result)
                .forPath("module.v4(obj,obj).[numberIndexer]")
                .hasViolations();
    }

    @Test
    public void newObjectCreationEquality() throws Exception {
        TajsAnalysisResults result = run("newObjectCreationEquality", options().staticOptions
                .setUseValuesWithMismatches(true)
                .setPropagateStateFromFailingTest(true)
                .setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.FEEDBACK_IF_POSSIBLE)
                .setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments())


//                .setUseInspector(true)

                .setInstantiationFilter(new CopyObjectInstantiation())
        );


                assertThat(result.detectedViolations.keySet(), hasSize(1));

        expect(result)
                .forPath("module.equalFoo(obj)")
                .hasViolations();

        assert result.detectedViolations.asMap().values().iterator().next().iterator().next().toString().contains("Bool");
    }

    @Test
    public void newObjectCreationBadObjectsGoAway() throws Exception {
        TajsAnalysisResults result = run("newObjectCreationBadObjectsGoAway", options().staticOptions
                .setUseValuesWithMismatches(true)
                .setPropagateStateFromFailingTest(true)
                .setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.FEEDBACK_IF_POSSIBLE)
                .setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments())


//                .setUseInspector(true)

                .setInstantiationFilter(new CopyObjectInstantiation())
        );


                assertThat(result.detectedViolations.keySet(), hasSize(1));

        expect(result)
                .forPath("module.createFoo().foo")
                .hasViolations();

        expect(result)
                .forPath("module.useFoo")
                .hasNoViolations();

        assert result.detectedViolations.asMap().values().iterator().next().iterator().next().toString().contains("Bool");
    }


    @Test
    public void prototypesAlsoGetsFiltered() throws Exception {
        TajsAnalysisResults result = run("prototypesAlsoGetsFiltered", options().staticOptions
                .setUseValuesWithMismatches(true)
                .setPropagateStateFromFailingTest(true)
                .setArgumentValuesStrategy(StaticOptions.ArgumentValuesStrategy.FEEDBACK_IF_POSSIBLE)
                .setExpansionPolicy(new LateExpansionToFunctionsWithConstructedArguments())


//                .setUseInspector(true)

                .setInstantiationFilter(new CopyObjectInstantiation())
        );


                assertThat(result.detectedViolations.keySet(), hasSize(1));

        expect(result)
                .forPath("module.createFoo().foo")
                .hasViolations();

        expect(result)
                .forPath("module.useFoo")
                .hasNoViolations();

        assert result.detectedViolations.asMap().values().iterator().next().iterator().next().toString().contains("Bool");
    }

    @Test
    public void widthSubtypingDoesntLeak() throws Exception {
        TajsAnalysisResults result = run("widthSubtypingDoesntLeak", options().staticOptions.setProperWidthSubtyping(true).setWidthSubtpyingIncludesAllObjects(true));

        expect(result)
                .hasNoViolations();

    }

    @Test
    public void newDecidingLibraryConstructedAlgo() throws Exception {
        TajsAnalysisResults result = run("newDecidingLibraryConstructedAlgo", options().staticOptions.setArgumentValuesStrategy(new PreferLibValuesPolicy()::getArgumentStrategy));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void preferLibValuesInWaves() throws Exception {
        TajsAnalysisResults result = run("preferLibValuesInWaves", options().staticOptions.setArgumentValuesStrategy(new PreferLibValuesPolicy()::getArgumentStrategy));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test(timeout = 60 * 1000)
    public void anotherInfiniteLoop() throws Exception {
        run("anotherInfiniteLoop", options().setWriteAll(true));
    }

    @Test
    public void testClassWarning() throws Exception {
        assertThat(run("classInstance", options().setOnlyInitialize(true)).hasClassesInDec, is(true));
        assertThat(run("anotherInfiniteLoop", options().setOnlyInitialize(true)).hasClassesInDec, is(false));
    }

    @Test
    public void subTypingAliasingLibraryConstructed() throws Exception {
        TajsAnalysisResults result = run("subTypingAliasingLibraryConstructed", options().staticOptions.setArgumentValuesStrategy(FORCE_FEEDBACK));

        assertThat(result.detectedViolations.keySet(), hasSize(1));

        expect(result)
                .forPath("module.foo(obj)")
                .hasViolations();
    }

    @Test
    public void subTypingAliasingClientConstructed() throws Exception {
        TajsAnalysisResults result = run("subTypingAliasingClientConstructed");

        assertThat(result.detectedViolations.keySet(), hasSize(1));

        expect(result)
                .forPath("module.foo(obj)")
                .hasViolations();

        assertThat(result.detectedViolations.asMap().values().iterator().next().iterator().next().message, containsString("Bool"));
    }

    // TODO: Should receiver of methodCall get filtered?
}