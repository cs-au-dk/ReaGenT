package dk.webbies.tajscheck.testcreator.test.check;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayIndexCheck that = (ArrayIndexCheck) o;

        return subCheck != null ? subCheck.equals(that.subCheck) : that.subCheck == null;
    }

    @Override
    public int hashCode() {
        return subCheck != null ? subCheck.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "arrayIndex(" + subCheck +")";
    }
}
