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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeOfCheck that = (TypeOfCheck) o;

        return type != null ? type.equals(that.type) : that.type == null;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}
