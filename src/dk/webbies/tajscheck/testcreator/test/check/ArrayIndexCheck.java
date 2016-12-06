package dk.webbies.tajscheck.testcreator.test.check;

/**
 * Created by erik1 on 06-12-2016.
 */
public class ArrayIndexCheck implements Check {
    private Check subCheck;

    public ArrayIndexCheck(Check subCheck) {
        this.subCheck = subCheck;
    }

    public Check getSubCheck() {
        return subCheck;
    }

    @Override
    public <T, A> T accept(CheckVisitorWithArgument<T, A> visitor, A a) {
        return visitor.visit(this, a);
    }
}
