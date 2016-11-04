package dk.webbies.tajscheck.testcreator.Test;

import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.util.Util;

import java.util.Collections;
import java.util.List;

/**
 * Created by erik1 on 02-11-2016.
 */
public class ConstructorCallTest extends Test {
    private final Type function;
    private List<Type> parameters;

    public ConstructorCallTest(Type function, List<Type> parameters, Type returnType, String path) {
        super(Util.concat(Collections.singletonList(function), parameters), returnType, "new " + path + "()");
        this.function = function;
        this.parameters = parameters;
    }


    public List<Type> getParameters() {
        return parameters;
    }

    public Type getFunction() {
        return function;
    }

    @Override
    public <T> T accept(TestVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
