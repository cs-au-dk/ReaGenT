package dk.webbies.tajscheck.testcreator.test.check;

import java.util.List;

/**
 * Created by erik1 on 14-11-2016.
 */
public class FieldCheck implements Check {
    private final List<Check> checks;
    private final String field;

    public FieldCheck(List<Check> checks, String field) {
        this.checks = checks;
        this.field = field;
    }

    public List<Check> getChecks() {
        return checks;
    }

    public String getField() {
        return field;
    }

    @Override
    public <T, A> T accept(CheckVisitorWithArgument<T, A> visitor, A a) {
        return visitor.visit(this, a);
    }
}
