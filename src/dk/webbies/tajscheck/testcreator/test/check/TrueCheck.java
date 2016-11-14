package dk.webbies.tajscheck.testcreator.test.check;

/**
 * Created by erik1 on 14-11-2016.
 */
public class TrueCheck implements Check {
    @Override
    public <T, A> T accept(CheckVisitorWithArgument<T, A> visitor, A a) {
        return visitor.visit(this, a);
    }
}

