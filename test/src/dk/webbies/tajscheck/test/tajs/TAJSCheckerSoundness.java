package dk.webbies.tajscheck.test.tajs;


import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.test.dynamic.UnitTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static dk.webbies.tajscheck.test.tajs.TAJSUnitTests.*;

@RunWith(Parameterized.class)
public class TAJSCheckerSoundness { // TODO: A lot of things fails here.
    @Parameterized.Parameter
    public Benchmark bench;

    @SuppressWarnings("ConstantConditions")
    @Parameterized.Parameters(name = "{0}")
    public static List<Benchmark> getBenchmarks() {
        List<Benchmark> result = new ArrayList<>();

        Arrays.stream(new File("test/unit/").listFiles())
                .filter(File::isDirectory)
                .map(File::getName)
//                    .filter(Util.not(blackList::contains))
                .map(UnitTests::benchFromFolder)
                .filter(bench ->
                    new File(bench.dTSFile).exists()
                )
                .forEach(result::add);

        Arrays.stream(new File("test/tajsUnit/").listFiles())
                .filter(File::isDirectory)
                .map(File::getName)
//                    .filter(Util.not(blackList::contains))
                .map(TAJSUnitTests::benchFromFolder)
                .filter(bench ->
                    new File(bench.dTSFile).exists()
                )
                .forEach(result::add);

        return result.stream()
                .filter(bench -> !bench.name.equals("unit-exponentialComplexity"))
                .filter(bench -> !blackList.contains(bench.name))
                .collect(Collectors.toList());
//        return result;
    }

    @Test
    public void testSoundness() throws Exception {
        TAJSUtil.TajsAnalysisResults result = TAJSUtil.runNoDriver(bench.withRunMethod(Benchmark.RUN_METHOD.BOOTSTRAP).withOptions(options -> options.setConstructAllTypes(true)), 60);
        System.out.println(result);
        expect(result).hasNoViolations();
    }

    // The ones commented out are the ones that are actually working, the rest still doesn't work.
    private static final List<String> blackList = Arrays.asList(
//            "unit-ambient",
//            "unit-ambient2",
//            "unit-ambient3",
            "unit-arrayType",
//            "unit-asyncBasicExample",
//            "unit-asyncGenerator",
//            "unit-basicExample1",
//            "unit-basicMemomizeExample",
//            "unit-booleanLiteral",
//            "unit-booleans",
//            "unit-browserCoverage",
//            "unit-browserCoverageTimeout",
//            "unit-browserMultipleProperties",
//            "unit-canFindErrorsEvenWhenTimeout",
//            "unit-canFindErrorsEvenWhenTimeoutChrome",
            "unit-canHaveError",
            "unit-canHaveErrorBrowser",
            "unit-canWriteComplex",
//            "unit-canWritePrimitives",
            "unit-classAndClassInstances",
            "unit-classesAndNamespaces",
            "unit-classProperties",
//            "unit-combinedShallowDeepChecking",
//            "unit-complexGenerics",
            "unit-complexGenerics2",
//            "unit-complexGenerics3",
//            "unit-complexOverloads",
            "unit-complexSanityCheck",
//            "unit-complexSanityCheck10",
            "unit-complexSanityCheck11",
//            "unit-complexSanityCheck12",
            "unit-complexSanityCheck13",
            "unit-complexSanityCheck14",
//            "unit-complexSanityCheck15",
            "unit-complexSanityCheck16",
            "unit-complexSanityCheck17",
            "unit-complexSanityCheck18",
            "unit-complexSanityCheck19",
            "unit-complexSanityCheck2",
            "unit-complexSanityCheck20",
//            "unit-complexSanityCheck21",
            "unit-complexSanityCheck22",
            "unit-complexSanityCheck23",
//            "unit-complexSanityCheck24",
            "unit-complexSanityCheck3",
//            "unit-complexSanityCheck4",
//            "unit-complexSanityCheck5",
            "unit-complexSanityCheck6",
            "unit-complexSanityCheck7",
            "unit-complexSanityCheck8",
            "unit-complexSanityCheck9",
            "unit-complexThisTypes",
            "unit-complexThisTypes2",
            "unit-complexThisTypes3",
//            "unit-complexUnion",
            "unit-constructClass",
            "unit-constructClassInstances",
//            "unit-constructOnlyPrimitives",
            "unit-correctArrayType",
            "unit-createNumberIndexer",
//            "unit-createStringIndexer",
//            "unit-deepUnion",
//            "unit-differentSizeOverloads",
//            "unit-doesNotHaveUnboundGenerics",
//            "unit-everythingIsRight",
            "unit-exponentialComplexity",
            "unit-extendingGenericClass",
            "unit-extendsArray",
            "unit-extendsArray2",
            "unit-extendsArray3",
            "unit-extendsArray4",
            "unit-extendsError",
//            "unit-extendsEvent",
//            "unit-extendsEvent2",
//            "unit-extendsEvent3",
//            "unit-findSimpleErrorChrome",
//            "unit-findSimpleErrorChromeWithErrors",
            "unit-firstMatchPolicy",
//            "unit-firstOrderFunctions",
//            "unit-foobar",
//            "unit-generators",
            "unit-genericClass",
            "unit-genericClass2",
            "unit-genericClass3",
            "unit-genericClass4",
            "unit-genericClassFeedback",
            "unit-genericClassFeedbackWithConstraint",
            "unit-genericExtendMethod",
            "unit-genericIndexedAccess",
//            "unit-genericInterfaceFeedback",
//            "unit-generics",
            "unit-genericsAreNotTooOptimized",
            "unit-genericsAreNotTooOptimized2",
            "unit-genericsAreOptimized",
            "unit-genericsAreOptimized2",
            "unit-genericsBustStack",
            "unit-genericsBustStack2",
            "unit-genericsBustStack3",
//            "unit-genericsBustStack4",
            "unit-genericsBustStackRuntime",
//            "unit-genericSignaturesError",
//            "unit-genericSignaturesSmokeTest",
            "unit-genericSignaturesSmokeTest2",
            "unit-genericsSplit",
//            "unit-genericsWithNoOptimization",
//            "unit-genericsWithNoOptimization2",
//            "unit-genRestArgs",
//            "unit-genRestArgsWithOverloads",
//            "unit-higherOrderFunctions",
//            "unit-indexedAccess",
            "unit-infiniteGenerics",
//            "unit-interfacesAndObjectsParsing",
            "unit-intersectionTypes",
            "unit-intersectionWithFunction",
//            "unit-keyOf",
            "unit-mappedTypes",
//            "unit-missingProperty",
            "unit-monitorUndeclaredAccess1",
            "unit-namespacesAndClassWithNestedClass",
//            "unit-never",
//            "unit-nodeCoverage",
//            "unit-nodeCoverageTimeout",
//            "unit-nodeList",
//            "unit-noIterations",
//            "unit-notDuplicatedAssertTypeFunctions",
            "unit-numberIndexer",
//            "unit-numberLiteral",
//            "unit-optionalDoesNotMeanUndefined",
            "unit-optionalDoesNotMeanUndefinedWithRestArg",
//            "unit-optionalParameters",
//            "unit-optionalParamsSmokeTest",
            "unit-optionalParamsSmokeTest2",
//            "unit-overloadsWithOptionalParameters",
            "unit-overrideNumberOfArguments",
//            "unit-propertyWithUnderscore",
            "unit-simpleClass",
//            "unit-simpleFunctionArg",
//            "unit-simpleOverloads",
            "unit-staticFields",
            "unit-staticFieldsInheritedInClass",
            "unit-stringIndexer",
            "unit-symbol",
            "unit-testClass",
            "unit-testRestArgs",
            "unit-thisTypes",
            "unit-thisTypes2",
            "unit-thisTypesAreOptimized",
            "unit-thisTypesAreOptimized2",
            "unit-thisTypesInInterfaces",
            "unit-thisTypesInInterfaces2",
            "unit-thisTypesInInterfaces3",
//            "unit-tuple",
//            "unit-tupleLength",
//            "unit-tupleSizes",
            "unit-typeInArray",
            "unit-typeofParsing",
//            "unit-unboundGenericsAreNotDuplicated",
//            "unit-undefinedOnObject",
//            "unit-undefinedReturnCanFail",
//            "unit-unknownFieldAccess",
            "unit-unnecessaryBigDriver",
            "unit-unsoundSiblings",
            "unit-valueCantBeTrueAndFalse",
            "unit-veryComplexThisType",
//            "unit-voidReturnCanBeAnything",
//            "unit-wrongSignaturePropagates",
//            "unit-wrongSimpleType",
//            "tajsunit-baitingTajsUnion",
//            "tajsunit-booleans",
//            "tajsunit-boolLit",
//            "tajsunit-callbacks",
            "tajsunit-testRestArgs",
            "tajsunit-classInstance",
            "tajsunit-constructor",
            "tajsunit-constructors2",
//            "tajsunit-everythingIsRight",
//            "tajsunit-function",
//            "tajsunit-functionAndObject",
//            "tajsunit-getter",
//            "tajsunit-getterInfiniteloop",
//            "tajsunit-multipleFunctions",
//            "tajsunit-nestedFunctions",
//            "tajsunit-numberLit",
//            "tajsunit-numbers",
//            "tajsunit-objectWithNumberProps",
//            "tajsunit-primitiveOrObject",
//            "tajsunit-primitivesFail",
//            "tajsunit-prototypeChains",
//            "tajsunit-recursiveObject",
//            "tajsunit-samePathFunctions",
            "tajsunit-sideEffects"
//            "tajsunit-simpleUnion",
//            "tajsunit-splitSignatures",
//            "tajsunit-spuriousOverload",
//            "tajsunit-spuriousUnion",
//            "tajsunit-stringLit",
//            "tajsunit-strings",
//            "tajsunit-unionMightFail"
    );
}


