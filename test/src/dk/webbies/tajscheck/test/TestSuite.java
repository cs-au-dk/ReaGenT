package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.test.dynamic.RunBenchmarks;
import dk.webbies.tajscheck.test.dynamic.UnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by erik1 on 24-11-2016.
 */
@RunWith(Suite.class)

@Suite.SuiteClasses({
        UnitTests.class,
        RunBenchmarks.class,
        TestParsing.class,
        TestVarious.class,
//        TAJSTests.class // Comment in if TAJS is relevant again.
})
public class TestSuite {
}
