package dk.webbies.tajscheck.test.tajs;

import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.benchmark.BenchmarkInfo;
import dk.webbies.tajscheck.benchmark.options.OptionsI;
import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import org.junit.Test;

import static dk.webbies.tajscheck.test.tajs.TAJSUnitTests.*;
import static dk.webbies.tajscheck.test.tajs.TAJSUnitTests.options;

public class FlowTests {
    static Benchmark benchFromFolder(String folderName) {
        return benchFromFolder(folderName, options());
    }

    static Benchmark benchFromFolder(String folderName, OptionsI.Builder options) {
        return benchFromFolder(folderName, options, Benchmark.RUN_METHOD.NODE);
    }

    static Benchmark benchFromFolder(String folderName, OptionsI.Builder options, Benchmark.RUN_METHOD run_method) {
        return new Benchmark("flow-" + folderName, ParseDeclaration.Environment.ES5Core, "test/flowtyped/" + folderName + "/implementation.js", "test/flowtyped/" + folderName + "/declaration.js", run_method, options.build());
    }

    @Test
    public void createInfo() {
        Benchmark benchmark = benchFromFolder("left-pad");
        BenchmarkInfo.create(benchmark);
    }

    @Test
    public void parseClass() {
        Benchmark benchmark = benchFromFolder("class");
        BenchmarkInfo.create(benchmark);
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


}
