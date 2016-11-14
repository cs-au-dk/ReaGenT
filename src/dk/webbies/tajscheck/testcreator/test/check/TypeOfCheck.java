package dk.webbies.tajscheck.testcreator.test.check;

/**
 * Created by erik1 on 14-11-2016.
 */
public class TypeOfCheck implements Check {
    private final String type;

    TypeOfCheck(String type) {
        this.type = type;
    }

    public String getTypeString() {
        return type;
    }

    @Override
    public <T, A> T accept(CheckVisitorWithArgument<T, A> visitor, A a) {
        return visitor.visit(this, a);
    }
}
