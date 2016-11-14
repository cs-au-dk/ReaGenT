package dk.webbies.tajscheck.testcreator.test.check;

import java.util.List;

/**
 * Created by erik1 on 14-11-2016.
 */
public class OrCheck implements Check {
    private final List<Check> checks;

    OrCheck(List<Check> checks) {
        this.checks = checks;
    }

    public List<Check> getChecks() {
        return checks;
    }

    @Override
    public <T, A> T accept(CheckVisitorWithArgument<T, A> visitor, A a) {
        return visitor.visit(this, a);
    }
}
