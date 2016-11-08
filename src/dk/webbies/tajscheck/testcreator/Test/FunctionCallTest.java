package dk.webbies.tajscheck.testcreator.Test;

import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.util.Util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by erik1 on 03-11-2016.
 */
public class FunctionCallTest extends Test {
    private final Type function;
    private List<Type> parameters;

    public FunctionCallTest(Type function, List<Type> parameters, Type returnType, String path) {
        super(Collections.singletonList(function), parameters, returnType, path + "()");
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
