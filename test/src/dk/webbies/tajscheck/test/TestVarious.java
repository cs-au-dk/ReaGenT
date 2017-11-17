package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.testcreator.TestCreator;
import dk.webbies.tajscheck.util.Util;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by erik1 on 16-01-2017.
 */
public class TestVarious {
    @Test
    public void simplifyPath() throws Exception {
        assertThat(Util.simplifyPath("moment(obj, number)"), is(equalTo("moment()")));

        assertThat(Util.simplifyPath("moment(obj, number).stuff"), is(equalTo("moment().stuff")));

        assertThat(Util.simplifyPath("moment(obj, number).stuff(obj, number).new(foo).blaa"), is(equalTo("moment().stuff().new().blaa")));
    }
}
