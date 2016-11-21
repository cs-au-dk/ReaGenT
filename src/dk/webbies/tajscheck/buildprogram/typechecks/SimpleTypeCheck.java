package dk.webbies.tajscheck.buildprogram.typechecks;

import dk.webbies.tajscheck.testcreator.test.check.Check;

/**
 * Created by erik1 on 21-11-2016.
 */
public class SimpleTypeCheck implements TypeCheck {
    private final Check check;
    private final String expected;

    public SimpleTypeCheck(Check check, String expected) {
        this.check = check;
        this.expected = expected;
    }

    public String getExpected() {
        return expected;
    }

    public Check getCheck() {
        return check;
    }
}
