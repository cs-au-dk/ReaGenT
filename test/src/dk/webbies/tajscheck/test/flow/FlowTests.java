package dk.webbies.tajscheck.test.flow;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.InterfaceType;
import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.DynamicMain;
import dk.webbies.tajscheck.Main;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.benchmark.options.OptionsI;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
import dk.webbies.tajscheck.test.tajs.TAJSUnitTests;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

import static dk.webbies.tajscheck.test.tajs.TAJSUnitTests.*;
import static dk.webbies.tajscheck.test.tajs.TAJSUnitTests.options;
import static org.hamcrest.MatcherAssert.assertThat;
import dk.webbies.tajscheck.OutputParser;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.benchmark.options.OptionsI;
import dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy.DelayAllTestsExpansionPolicy;
import dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy.ExpandOneAtATimeWhenWorkListEmpty;
import dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy.FixedExpansionOrder;
import dk.webbies.tajscheck.benchmark.options.staticOptions.LimitTransfersRetractionPolicy;
import dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions;
import dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy.LateExpansionToFunctionsWithConstructedArguments;
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
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions.ArgumentValuesStrategy.FEEDBACK_IF_POSSIBLE;
import static dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions.ArgumentValuesStrategy.MIX_FEEDBACK_AND_CONSTRUCTED;
import static dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions.ArgumentValuesStrategy.ONLY_CONSTRUCTED;
import static dk.webbies.tajscheck.tajstester.TAJSUtil.*;
import static dk.webbies.tajscheck.test.dynamic.UnitTests.ParseResultTester.ExpectType.STRING;
import static dk.webbies.tajscheck.util.Util.mkString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FlowTests {
    public static Benchmark benchFromFolder(String folderName) {
        return benchFromFolder(folderName, options());
    }

    static Benchmark benchFromFolder(String folderName, OptionsI.Builder options) {
        return benchFromFolder(folderName, options, Benchmark.RUN_METHOD.NODE);
    }

    public static Benchmark benchFromFolder(String folderName, OptionsI.Builder options, Benchmark.RUN_METHOD run_method) {
        return new Benchmark("flow-" + folderName, ParseDeclaration.Environment.ES6DOM, "test/flowtests/" + folderName + "/implementation.js", "test/flowtests/" + folderName + "/declaration.js", run_method, options.build());
    }

    @Test
    public void createInfo() {
        BenchmarkInfo.create(benchFromFolder("left-pad"));
    }

    @Test
    public void parseClass() {
        BenchmarkInfo.create(benchFromFolder("class"));
    }
    @Test
    public void parseRestArgs() throws Exception {
        DynamicMain.generateFullDriver(benchFromFolder("parseRestArgs"));
    }

    @Test
    public void parseUnified() {
        BenchmarkInfo.create(benchFromFolder("unified"));
    }

    @Test
    public void parseUnderscoreDotString() {
        BenchmarkInfo.create(benchFromFolder("underscore.string"));
    }

    @Test
    public void parseVscode() {
        BenchmarkInfo.create(benchFromFolder("vscode"));
    }

    @Test
    public void parseVerbalExpressions() {
        BenchmarkInfo.create(benchFromFolder("verbal-expressions"));
    }

    @Test
    public void parseYargs() {
        BenchmarkInfo.create(benchFromFolder("yargs"));
    }


    @Test
    public void parseYamljs() {
        BenchmarkInfo.create(benchFromFolder("yamljs"));
    }

    @Test
    public void parseXterm() {
        BenchmarkInfo.create(benchFromFolder("xterm"));
    }

    @Test
    public void parsedomApi() {
        BenchmarkInfo.create(benchFromFolder("domapi"));
    }

    @Test
    public void parseValidator() {
        BenchmarkInfo.create(benchFromFolder("validator"));
    }

    @Test
    public void parseValidate_js() {
        BenchmarkInfo.create(benchFromFolder("validate_js"));
    }

    @Test
    public void parseUuid() {
        BenchmarkInfo.create(benchFromFolder("uuid"));
    }

    @Test
    public void parseUrlParse() {
        BenchmarkInfo.create(benchFromFolder("url-parse"));
    }

    @Test
    public void parseUrlJoin() {
        BenchmarkInfo.create(benchFromFolder("url-join"));
    }

    @Test
    public void parseUniversalRouter() {
        BenchmarkInfo.create(benchFromFolder("universal-router"));
    }

    @Test
    public void objectWithCallsignature() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("objectWithCallsignature"));

        assertThat(result.detectedViolations.keySet(), hasSize(1));

        expect(result)
                .forPath("foo.baz(boolean)")
                .hasViolations();
    }

    @Test
    public void genericClass() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("genericClass"));

        assertThat(result.detectedViolations.keySet(), hasSize(2));

        expect(result)
                .forPath("foo.MyKlass.new(typeParameter).isFalse")
                .hasViolations();

        expect(result)
                .forPath("foo.bar().value")
                .hasViolations();
    }

    @Test
    public void restArgs() throws Exception {
        expect(run(benchFromFolder("restArgs")))
                .hasNoViolations();
    }

    @Test
    public void natives() throws Exception {
        expect(run(benchFromFolder("natives")))
                .hasNoViolations();
    }

    @Test
    public void verifyLeftPad() throws Exception {
        expect(run(benchFromFolder("left-pad")))
                .hasNoViolations();
    }

    @Test
    public void verifyXmlEscape() throws Exception {
        expect(run(benchFromFolder("xml-escape")))
                .hasNoViolations();
    }

    @Test
    public void classInstance() throws Exception {
        expect(run(benchFromFolder("classInstance")))
                .hasNoViolations();
    }

    @Test
    public void typeOfClassInstance() throws Exception {
        expect(run(benchFromFolder("typeofclassInstance")))
                .hasNoViolations();
    }

    @Test
    public void extendsInterface() throws Exception {
        expect(run(benchFromFolder("extendsInterface")))
                .hasNoViolations();
    }

    @Test
    public void genericInterface() throws Exception {
        expect(run(benchFromFolder("genericInterface")))
                .hasNoViolations();
    }


    @Test
    public void genericWithTypeAliasInterface() throws Exception {
        TAJSUtil.TajsAnalysisResults result = run(benchFromFolder("genericWithTypeAliasInterface"));

        assertThat(result.detectedViolations.size(), is(2));

        expect(result)
                .forPath("\"yada\".num.value")
                .hasViolations();

        expect(result)
                .forPath("\"yada\".num2.value")
                .hasViolations();
    }

    @Test
    public void classExtends() throws Exception {
        expect(run(benchFromFolder("classExtends")))
                .performedAllTests()
                .hasNoViolations();
    }

    @Test
    public void classExtendsWithError() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("classExtendsWithError"));

        assertThat(result.detectedViolations.size(), is(1));

        expect(result)
                .forPath("foo.bar.base")
                .hasViolations();
    }

    @Test
    public void array() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("array"));

        assertThat(result.detectedViolations.keySet(), hasSize(1));

        expect(result)
                .forPath("foo.bar.[numberIndexer]")
                .hasViolations();
    }

    @Test
    public void indexers() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("indexers"));

        assertThat(result.detectedViolations.keySet(), hasSize(1));

        expect(result)
                .forPath("foo.bar.[stringIndexer]")
                .hasViolations();
    }

    @Test
    public void genericMethod() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("genericMethod"));

        assertThat(result.detectedViolations.keySet(), hasSize(1));

        expect(result)
                .forPath("foo.foo.gen(typeParameter).notThere")
                .hasViolations();
    }

    @Test
    public void genericMethodWithBound() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("genericMethodWithBound"));

        assertThat(result.detectedViolations.keySet(), hasSize(1));

        expect(result)
                .forPath("foo.foo.gen(typeParameter).value")
                .hasViolations();
    }

    @Test
    public void tuple() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("tuple"));

        assertThat(result.detectedViolations.keySet(), hasSize(1));

        expect(result)
                .forPath("foo.foo.2")
                .hasViolations();
    }

    @Test
    public void optionalField() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("optionalField"));

        expect(result)
                .hasNoViolations();
    }

    @Test
    @Ignore // There is a big issue with getters in TAJS, things gets mixed up the wrong way.
    public void newCallPrimitive() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("newCallPrimitive"));

        System.out.println(result);
        expect(result)
                .hasNoViolations();
    }

    @Test
    public void typeofProperty() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("typeofProperty"));

        assertThat(result.detectedViolations.keySet(), hasSize(1));

        expect(result)
                .forPath("foo.foo()")
                .hasViolations();
    }

    @Test
    @Ignore // Seems the flow-parser forgets about the typeof annotation.
    public void moduleExportsClass() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("moduleExportsClass"));

        assertThat(result.detectedViolations.keySet(), hasSize(2));

        expect(result)
                .forPath("bizniz.Bizniz.bar()")
                .hasViolations();

        expect(result)
                .forPath("bizniz.Bizniz.new().foo()")
                .hasViolations();
    }

    @Test
    public void genericInBaseInterface() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("genericInBaseInterface"));

        expect(result)
                .hasNoViolations();
    }

    @Test
    public void genericInBaseInterface2() throws Exception {
        // Using TSTest, because massive implementation copied from library.
        OutputParser.RunResult result = UnitTests.run(benchFromFolder("genericInBaseInterface2"));

        UnitTests.expect(result)
                .toPass();
    }

    @Test
    public void superMethods() throws Exception {
        OutputParser.RunResult result = UnitTests.run(benchFromFolder("superMethods", options().setMaxIterationsToRun(1000)));

        UnitTests.expect(result)
                .toPass();

        TajsAnalysisResults tajsResult = run(benchFromFolder("superMethods"));

        expect(tajsResult)
                .hasNoViolations();
    }

    @Test
    public void genericInBaseClass() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("genericInBaseClass"));

        expect(result)
                .hasNoViolations();
    }

    @Test
    public void parseAllBenchmarks() {
        List<Benchmark> benchmarks = FlowBenchmarks.getBenchmarks();
        for (int i = 0; i < benchmarks.size(); i++) {
            System.out.println(i + "/" + benchmarks.size());
            BenchmarkInfo.create(benchmarks.get(i));
        }
    }

    @Test
    public void genericsAndFunctions() throws IOException {
        for (int i = 0; i < 5; i++) {
            String driver = DynamicMain.generateFullDriver(benchFromFolder("genericsAndFunctions")).getRight();
            assertThat(driver, not(containsString("_isUnboundGeneric")));
        }
    }

    @Test
    public void documentClass() throws Exception {
        TajsAnalysisResults result = run(benchFromFolder("documentClass"));
        expect(result)
                .hasNoViolations();
    }

    // TODO: Exact objects.
    /* TODO: Things to do before the camera ready version:
        - Reduce the number of @ignored test-cases in TAJSUnitTests.
        - Reduce the number of @ignored test-cases in FlowTests.
     */
    // TODO: Rest-parameters in tuple-types.
    // TODO: Use TupleType.minLength when instantiating a tuple-type. 
}
