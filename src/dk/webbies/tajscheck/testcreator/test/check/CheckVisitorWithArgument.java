package dk.webbies.tajscheck.testcreator.test.check;

/**
 * Created by erik1 on 14-11-2016.
 */
public interface CheckVisitorWithArgument<T, A> {
    T visit(OrCheck filter, A a);

    T visit(TypeOfCheck filter, A a);

    T visit(NotCheck filter, A a);

    T visit(AndCheck filter, A a);

    T visit(EqualityCheck filter, A a);

    T visit(TrueCheck trueFilter, A a);

    T visit(InstanceOfCheck filter, A a);

    T visit(FieldCheck filter, A a);
}
