package dk.webbies.tajscheck.testcreator.test.check;

import dk.webbies.tajscheck.TypeWithContext;

public class NumberIndexCheck implements Check, CanHaveSubTypeCheck {
    private final Check subCheck;
    private final TypeWithContext subType;

    public NumberIndexCheck(Check subCheck, TypeWithContext subType) {
        this.subCheck = subCheck;
        this.subType = subType;
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

        NumberIndexCheck that = (NumberIndexCheck) o;

        return subCheck != null ? subCheck.equals(that.subCheck) : that.subCheck == null;
    }

    @Override
    public int hashCode() {
        return subCheck != null ? subCheck.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "numberindex(" + subCheck +")";
    }

    @Override
    public TypeWithContext getSubType() {
        return subType;
    }
}
