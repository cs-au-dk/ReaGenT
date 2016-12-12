package dk.webbies.tajscheck.testcreator.test;

import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.PrettyTypes;
import dk.webbies.tajscheck.TypeContext;

import java.util.Collections;
import java.util.List;

/**
 * Created by erik1 on 03-11-2016.
 */
public class FunctionCallTest extends Test {
    private final Type function;
    private List<Type> parameters;

    public FunctionCallTest(Type function, List<Type> parameters, Type returnType, String path, TypeContext typeContext) {
        super(Collections.singletonList(function), parameters, returnType, path + PrettyTypes.parameters(parameters), typeContext);
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
    public boolean equalsNoPath(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return super.equalsNoPathBase((Test) o);
    }

    @Override
    public int hashCodeNoPath() {
        return super.hashCodeNoPathBase();
    }

    @Override
    public <T> T accept(TestVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
