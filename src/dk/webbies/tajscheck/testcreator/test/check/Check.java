package dk.webbies.tajscheck.testcreator.test.check;

import dk.webbies.tajscheck.paser.AST.Expression;

import java.util.Arrays;
import java.util.List;

/**
 * Created by erik1 on 14-11-2016.
 */
public interface Check {
    <T, A> T accept(CheckVisitorWithArgument<T, A> visitor, A a);

    static TypeOfCheck typeOf(String type) {
        return new TypeOfCheck(type);
    }

    static NotCheck not(Check check) {
        return new NotCheck(check);
    }

    static AndCheck and(Check... checks) {
        return and(Arrays.asList(checks));
    }

    static AndCheck and(List<Check> checks) {
        return new AndCheck(checks);
    }

    static OrCheck or(Check... checks) {
        return or(Arrays.asList(checks));
    }

    static OrCheck or(List<Check> checks) {
        return new OrCheck(checks);
    }

    static EqualityCheck equalTo(Expression exp) {
        return new EqualityCheck(exp);
    }

    static TrueCheck trueCheck() {
        return new TrueCheck();
    }

    default OrCheck or(Check other) {
        return Check.or(this, other);
    }

    default AndCheck and(Check other) {
        return Check.and(this, other);
    }

    static InstanceOfCheck instanceOf(Expression exp) {
        return new InstanceOfCheck(exp);
    }

    static FieldCheck field(String field, Check... subChecks) {
        return field(field, Arrays.asList(subChecks));
    }

    static FieldCheck field(String field, List<Check> subChecks) {
        return new FieldCheck(subChecks, field);
    }
}
