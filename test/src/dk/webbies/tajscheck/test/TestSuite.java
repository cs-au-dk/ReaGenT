package dk.webbies.tajscheck.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by erik1 on 24-11-2016.
 */
@RunWith(Suite.class)

@Suite.SuiteClasses({
        TestUnderscore.class,
        UnitTests.class,
        WriteAllDrivers.class
})
public class TestSuite {
}
