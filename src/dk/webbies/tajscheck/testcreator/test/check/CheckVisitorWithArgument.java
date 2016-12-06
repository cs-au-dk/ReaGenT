package dk.webbies.tajscheck.testcreator.test.check;

/**
 * Created by erik1 on 14-11-2016.
 */
public interface CheckVisitorWithArgument<T, A> {
    T visit(OrCheck check, A a);

    T visit(TypeOfCheck check, A a);

    T visit(NotCheck check, A a);

    T visit(AndCheck check, A a);

    T visit(EqualityCheck check, A a);

    T visit(TrueCheck trueFilter, A a);

    T visit(InstanceOfCheck check, A a);

    T visit(FieldCheck check, A a);

    T visit(NumberIndexCheck check, A a);
}
