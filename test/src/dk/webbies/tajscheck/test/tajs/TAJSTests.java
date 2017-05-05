package dk.webbies.tajscheck.test.tajs;

import dk.webbies.tajscheck.test.tajs.analyze.AnalyzeBenchmarks;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by erik1 on 15-12-2016.
 */
@RunWith(Suite.class)

@Suite.SuiteClasses({
        TAJSUnitTests.class,
        RunAllDynamicUnitTests.class,
        AnalyzeBenchmarks.class
})
public class TAJSTests {
}
