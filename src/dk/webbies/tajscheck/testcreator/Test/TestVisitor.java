package dk.webbies.tajscheck.testcreator.Test;

/**
 * Created by erik1 on 02-11-2016.
 */
public interface TestVisitor<T> {

    T visit(MemberAccessTest test);

    T visit(LoadModuleTest test);

    T visit(MethodCallTest test);

    T visit(ConstructorCallTest test);

    T visit(FunctionCallTest test);
}
