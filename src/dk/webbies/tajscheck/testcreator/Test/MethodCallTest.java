package dk.webbies.tajscheck.testcreator.Test;

import dk.au.cs.casa.typescript.types.InterfaceType;
import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.util.Util;

import java.util.Arrays;
import java.util.List;

/**
 * Created by erik1 on 02-11-2016.
 */
public class MethodCallTest extends Test {
    private InterfaceType object;
    private final Type function;
    private final String propertyName;
    private List<Type> parameters;
    private final Type returnType;
    private final String path;

    public MethodCallTest(InterfaceType object, Type function, String propertyName, List<Type> parameters, Type returnType, String path) {
        super(Arrays.asList(object, function), parameters, returnType, path + "()");
        this.object = object;
        this.function = function;
        this.propertyName = propertyName;
        this.parameters = parameters;
        this.returnType = returnType;
        this.path = path;
    }

    public InterfaceType getObject() {
        return object;
    }

    public List<Type> getParameters() {
        return parameters;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public <T> T accept(TestVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
