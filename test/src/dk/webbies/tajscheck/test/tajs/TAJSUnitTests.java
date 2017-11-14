package dk.webbies.tajscheck.test.tajs;

import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.benchmark.options.OptionsI;
import dk.webbies.tajscheck.benchmark.options.staticOptions.FixedExpansionOrder;
import dk.webbies.tajscheck.benchmark.options.staticOptions.LimitTransfersRetractionPolicy;
import dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
import dk.webbies.tajscheck.test.dynamic.UnitTests;
import dk.webbies.tajscheck.util.ArrayListMultiMap;
import dk.webbies.tajscheck.util.MultiMap;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.test.dynamic.UnitTests.ParseResultTester.ExpectType.STRING;
import static dk.webbies.tajscheck.util.Util.mkString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

public class TAJSUnitTests {
    private static TAJSUtil.TajsAnalysisResults run(String folderName) throws Exception {
        TAJSUtil.TajsAnalysisResults result = run(benchFromFolder(folderName, options()));
        System.out.println(result);
        return result;
    }

    private static TAJSUtil.TajsAnalysisResults run(String folderName, OptionsI.Builder options) throws Exception {
        return run(benchFromFolder(folderName, options));
    }

    private static TAJSUtil.TajsAnalysisResults run(Benchmark bench) throws Exception {
        return TAJSUtil.runNoDriver(bench, 60);
    }

    private TAJSUtil.TajsAnalysisResults soundness(String folder) throws Exception {
        return soundness(folder, opt -> opt);
    }

    private TAJSUtil.TajsAnalysisResults soundness(String folder, Function<CheckOptions.Builder, OptionsI.Builder> transformer) throws Exception {
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(benchFromFolder(folder).withRunMethod(Benchmark.RUN_METHOD.BOOTSTRAP).withOptions(transformer).withOptions(options -> options.setConstructAllTypes(true)), Integer.MAX_VALUE);
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

    private static CheckOptions.Builder options() {
        return CheckOptions.builder().setCheckDepthReport(0).setCheckDepthUseValue(0);
    }

    @SuppressWarnings("UnusedReturnValue")
    static class TAJSResultTester {
        private TAJSUtil.TajsAnalysisResults results;

        private TAJSResultTester(TAJSUtil.TajsAnalysisResults result) {
            this.results = result;
        }

        TAJSUnitTests.TAJSResultTester performed(String path) {
            MatcherAssert.assertThat("test for " + path + " hasn't been performed", results.testPerformed.stream().anyMatch(test -> test.getPath().equals(path)));
            return this;
        }

        TAJSUnitTests.TAJSResultTester performedAllTests() {
            MatcherAssert.assertThat("some tests were not performed: " + mkString(results.testNot, ", "), results.testNot.isEmpty());
            return this;
        }

        TAJSUnitTests.TAJSResultTester hasNoViolations() {
            MatcherAssert.assertThat("there are no violations", results.detectedViolations.size() == 0);
            return this;
        }

        TAJSUnitTests.TAJSResultTester hasNoWarnings() {
            MatcherAssert.assertThat("there are no warnings", results.detectedWarnings.size() == 0);
            return this;
        }

        TAJSUnitTests.TAJSResultTester hasViolations() {
            MatcherAssert.assertThat("there are violations", results.detectedViolations.size() != 0);
            return this;
        }

        TAJSResultTester forPath(String path) {
            MultiMap<String, TypeViolation> detectedViolations = results.detectedViolations.asMap().entrySet().stream().filter(entry -> entry.getKey().startsWith(path)).collect(ArrayListMultiMap.collector());
            List<dk.webbies.tajscheck.testcreator.test.Test> performedTest = results.testPerformed.stream().filter(test -> test.getPath().startsWith(path)).collect(Collectors.toList());
            List<dk.webbies.tajscheck.testcreator.test.Test> testsNot = results.testNot.stream().filter(test -> test.getPath().startsWith(path)).collect(Collectors.toList());
            MultiMap<String, TypeViolation> warnings = results.detectedWarnings.asMap().entrySet().stream().filter(entry -> entry.getKey().startsWith(path)).collect(ArrayListMultiMap.collector());

            return new TAJSResultTester(new TAJSUtil.TajsAnalysisResults(detectedViolations, warnings, performedTest, testsNot, results.certificates, results.testTranfers, results.timers, results.timedout, results.retractedTests));
        }

        TAJSUnitTests.TAJSResultTester hasWarnings() {
            MatcherAssert.assertThat("there are warnings", results.detectedWarnings.size() != 0);
            return this;
        }
    }

    public static TAJSUnitTests.TAJSResultTester expect(TAJSUtil.TajsAnalysisResults result) {
        return new TAJSResultTester(result);
    }

    @Test
    public void unionMightFail() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("unionMightFail");

        expect(result)
                .performed("module.foo()");

        expect(result)
                .forPath("module.foo()")
                .hasViolations();
    }

    @Test
    public void callbacks() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("callbacks");

        expect(result)
                .performed("module.foo(obj)");

        expect(result)
                .forPath("module.foo(obj)")
                .hasViolations();
    }

    @Test
    @Ignore // TODO: Calling "pv.readPropertyValue" with a getter property, seems to go wrong (it seems to mix in the base object).
    public void getter() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("getter");

        expect(result)
                .hasNoViolations();
    }

    @Test()
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
        TAJSUtil.TajsAnalysisResults result = run("simpleUnion");

        expect(result)
                .performed("module.foo()");
    }

    @Test
    public void primitiveOrObject() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("primitiveOrObject");

        expect(result)
                .performed("module.foo()");
    }

    @Test
    public void objectWithNumberProps() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("objectWithNumberProps");

        expect(result)
                .performed("module.foo()");

        expect(result)
                .performed("module.bar()");
    }

    @Test
    public void everythingIsRight() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("everythingIsRight");

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
        TAJSUtil.TajsAnalysisResults result = run("recursiveObject");

        expect(result)
                .performed("module.foo.rec");
    }

    @Test
    public void functionAndObject() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("functionAndObject");

        expect(result)
                .performed("module.foo.foo");
    }

    /**
     * This test exposes how a sound static analysis can produce an unsound result.
     * Because what the analysis does, is say, if this code is reachable, then the following is true.
     * But if that code isn't reachable,
     */
    @Test
    @Ignore // TODO: maybe for later.
    public void baitingTajsUnion() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("baitingTajsUnion");

        expect(result)
                .performed("module.foo().[union0].bar.baz");

        expect(result)
                .performed("module.foo().[union1].bar.baz");
    }

    @Test
    public void spuriousUnion() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("spuriousUnion");

        expect(result)
                .performedAllTests()
                .forPath("module.foo()")
                .hasWarnings();
    }

    @Test
    public void spuriousOverload() throws Exception {
        // I wanted to make a more complicated test, but since TAJS cannot see that (typeof [bool/number] !== "string"), it has to be quite simple.
        TAJSUtil.TajsAnalysisResults result = run("spuriousOverload");

        expect(result)
                .performed("module.foo(obj)")
                .forPath("Foo")
                .hasWarnings();

    }

    @Test
    public void splitSignatures() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("splitSignatures");

        expect(result)
                .performedAllTests()
                .forPath("Foo")
                .hasWarnings();
    }

    @Test
    public void multipleFunctions() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("multipleFunctions");

        expect(result)
                .hasNoViolations();
    }

    @Test
    public void numbers() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("numbers");

        expect(result)
                .performed("module.id(number)")
                .hasNoViolations();
    }

    @Test
    public void strings() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("strings");

        expect(result)
                .performed("module.id(string)")
                .hasNoViolations();
    }

    @Test
    public void booleans() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("booleans");

        expect(result)
                .performed("module.id(boolean)")
                .hasNoViolations();
    }

    @Test
    public void numberLit() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("numberLit");

        expect(result)
                .performed("module.id(3)")
                .hasNoViolations();
    }

    @Test
    public void stringLit() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("stringLit");

        expect(result)
                .performed("module.id(\"str\")")
                .hasNoViolations();
    }

    @Test
    public void boolLit() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("boolLit");

        expect(result)
                .performed("module.id(true)")
                .hasNoViolations();
    }

    @Test
    public void primitivesFail() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("primitivesFail");

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
        TAJSUtil.TajsAnalysisResults result = run("nestedFunctions");
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
        TAJSUtil.TajsAnalysisResults result = run("numberIndexer");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void numberIndexerFails() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("numberIndexerFails");

        expect(result)
                .forPath("foo().[numberIndexer]")
                .hasViolations();
    }

    @Test
    public void testRestArgs() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("testRestArgs");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void overloadedCallbacks() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("overloadedCallbacks");

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
        TAJSUtil.TajsAnalysisResults result = run("overloadedCallbacks2");

        expect(result)
                .performedAllTests()
                .forPath("Foo")
                .hasViolations();

    }
    @Test
    public void overloadedCallbacks3() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("overloadedCallbacks3");

        expect(result)
                .performedAllTests()
                .hasNoViolations();

    }

    @Test
    public void constructors2() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run(benchFromFolder("constructors2", options(), Benchmark.RUN_METHOD.BROWSER));

        expect(result)
                .performedAllTests()
                .hasNoViolations();


    }

    @Test
    public void stringIndexer() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("stringIndexer");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void primitives() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("primitives", options().staticOptions);
        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void createRecursiveObject() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("createRecursiveObject", options().staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void extendsInterface() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("extendsInterface", options().staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void readUndefProp() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("readUndefProp");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void weakWrites() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("weakWrites");

        expect(result)
                .performedAllTests()
                .forPath("module.foo(obj)")
                .hasViolations();
    }

    @Test
    public void correctUnion() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("correctUnion");

        expect(result)
                .performedAllTests()
                .hasNoViolations()
                .hasNoViolations();
    }

    @Test
    public void createConstructor() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("createConstructor", options().staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void canHaveDifferentGenerics() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("canHaveDifferentGenerics", options().staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void numberIndexer2() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("numberIndexer2");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void indirectRecursiveObjects() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("indirectRecursiveObjects", options().staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void classInheritsConstructors() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("classInheritsConstructors", options().setConstructAllTypes(true));

        assertThat(result.detectedViolations.asMap().keySet(), is(hasSize(2)));

        expect(result)
                .performedAllTests()
                .forPath("module.Baz.[arg0]")
                .hasViolations();

        expect(result)
                .performedAllTests()
                .forPath("window, module, Bar")
                .hasViolations();
    }

    @Test
    public void classInheritance() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("classInheritance", options().staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void createRestArgs() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("createRestArgs");

        assertThat(result.detectedViolations.asMap().keySet(), hasSize(1));

        expect(result)
                .performedAllTests()
                .forPath("Bar.[arg4]")
                .hasViolations();

    }

    @Test
    @Ignore // TODO: Look at any later.
    public void any() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("any");

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
    @Ignore // when this works, any-type should come for free.
    public void createRecursiveObject2() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("createRecursiveObject2");

        expect(result)
                .performedAllTests()
                .hasViolations();
    }

    @Test
    public void fromTSFile() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run(benchFromFolderTSFile("fromTSFile", options(), Benchmark.RUN_METHOD.BROWSER));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    @Ignore // TODO: Fails because the keys of constructed objects aren't iterated when doing a for-in loop.
    public void copiesFunctions() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run(benchFromFolderTSFile("copiesFunctions", options(), Benchmark.RUN_METHOD.BROWSER));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    @Ignore // TODO: Fails exactly because making an object a summery adds a possible "undefined" on all properties.
    public void weakReadsGiveUndefined() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("weakReadsGiveUndefined");

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void readProperty() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("readProperty", options().staticOptions.setCreateSingletonObjects(true));
        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void createUnionsOfDateAndFunction() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("createUnionsOfDateAndFunction", options().setSplitUnions(false).staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void deepChecking() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("deepChecking");

        assertTrue(result.testNot.stream().map(dk.webbies.tajscheck.testcreator.test.Test::getPath).anyMatch("foo().foo"::equals));
    }

    @Test
    public void checkRecursiveObject() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("checkRecursiveObject");

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
    @Ignore // TODO: Fails because the instanceof checks always spuriously throws at runtime.
    public void instanceOfSmokeTest() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("instanceOfSmokeTest", options().setSplitUnions(false).staticOptions.setCreateSingletonObjects(true));

        expect(result)
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void createWeakObjects() throws Exception {
        run("createWeakObjects");
    }

    @Test
    @Ignore // TODO: Fails because we never generate an object that has the same identity as an object created internally by the library.
    public void objectIdentity() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("objectIdentity");

        expect(result)
                .performedAllTests()
                .forPath("module.test(obj)")
                .hasViolations();
    }

    @Test
    public void motivatingPath() throws Exception {
        Benchmark bench = benchFromFolder("motivatingPath", options(), Benchmark.RUN_METHOD.BROWSER);

        TAJSUtil.TajsAnalysisResults result = run(bench);
        expect(result)
                .performedAllTests()
                .forPath("window, Path, map, (), exit, [arg0]")
                .hasViolations();

        // the dynamic testing can also find the error.
        OutputParser.RunResult dynamicResult = UnitTests.run(bench, "foo");
        UnitTests.expect(dynamicResult)
                .forPath("window.Path.map.().exit.[arg0]")
                .expected("1 parameters")
                .got(STRING, "0 parameters");
    }

    @Test
    public void retract() throws Exception {
        StaticOptions.Builder options = options().staticOptions.setRetractionPolicy(new LimitTransfersRetractionPolicy(100, 0));

        TAJSUtil.TajsAnalysisResults result = run("retract", options);

        System.out.println(result);

        expect(result)
                .hasNoViolations();
    }

    @Test
    public void readableObjectErrors() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run("readableObjectErrors");

        System.out.println(result);

        assertThat(result.detectedViolations.keySet(), hasSize(1));
    }

    @Test // Tests that the order in which tests are executed does not matter.
    public void discardSpuriousValues() throws Exception {
        List<String> executionOrder = Arrays.asList("module.foo()", "module.foo().baz()", "module.bar()");

        TAJSUtil.TajsAnalysisResults resultNoOrder = run("discardSpuriousValues", options());

        TAJSUtil.TajsAnalysisResults resultFixedExpansion = run("discardSpuriousValues", options().staticOptions.setExpansionPolicy(new FixedExpansionOrder(executionOrder)));

        System.out.println("With default expansion-policy");
        System.out.println(resultNoOrder);

        System.out.println("With fixed expansion");
        System.out.println(resultFixedExpansion);

        assertThat(resultNoOrder.detectedViolations.keySet(), is(equalTo(resultFixedExpansion.detectedViolations.keySet())));
    }

    // TODO: Test string-indexers somehow.
    // TODO: Handle construction of native objects.
}