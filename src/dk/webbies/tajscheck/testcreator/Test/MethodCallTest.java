package dk.webbies.tajscheck.testcreator.Test;

import dk.au.cs.casa.typescript.types.InterfaceType;
import dk.au.cs.casa.typescript.types.Type;
import dk.au.cs.casa.typescript.types.TypeParameterType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by erik1 on 02-11-2016.
 */
public class MethodCallTest extends Test {
    private InterfaceType object;
    private final String propertyName;
    private List<Type> parameters;

    public MethodCallTest(InterfaceType object, Type function, String propertyName, List<Type> parameters, Type returnType, String path, Map<TypeParameterType, Type> parameterMap) {
        super(Arrays.asList(object, function), parameters, returnType, path + "()", parameterMap);
        this.object = object;
        this.propertyName = propertyName;
        this.parameters = parameters;
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
    public boolean equalsNoPath(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodCallTest test = (MethodCallTest) o;
        if (!test.propertyName.equals(this.propertyName)) return false;
        return super.equalsNoPathBase(test);
    }

    @Override
    public int hashCodeNoPath() {
        return super.hashCodeNoPathBase() + this.propertyName.hashCode();
    }

    @Override
    public <T> T accept(TestVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
