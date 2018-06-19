package dk.webbies.tajscheck.test.tajs;

import dk.au.cs.casa.typescript.SpecReader;
import dk.au.cs.casa.typescript.types.InterfaceType;
import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.benchmark.options.OptionsI;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.tajstester.TAJSUtil;
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
    static Benchmark benchFromFolder(String folderName) {
        return benchFromFolder(folderName, options());
    }

    static Benchmark benchFromFolder(String folderName, OptionsI.Builder options) {
        return benchFromFolder(folderName, options, Benchmark.RUN_METHOD.NODE);
    }

    static Benchmark benchFromFolder(String folderName, OptionsI.Builder options, Benchmark.RUN_METHOD run_method) {
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


}
