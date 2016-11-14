package dk.webbies.tajscheck.testcreator.test.check;

/**
 * Created by erik1 on 14-11-2016.
 */
public class NotCheck implements Check {
    private final Check check;

    NotCheck(Check check) {
        this.check = check;
    }

    public Check getCheck() {
        return check;
    }

    @Override
    public <T, A> T accept(CheckVisitorWithArgument<T, A> visitor, A a) {
        return visitor.visit(this, a);
    }
}
