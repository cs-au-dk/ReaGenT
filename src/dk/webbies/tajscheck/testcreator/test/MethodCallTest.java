package dk.webbies.tajscheck.testcreator.test;

import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.PrettyTypes;
import dk.webbies.tajscheck.TypeContext;

import java.util.Arrays;
import java.util.List;

/**
 * Created by erik1 on 02-11-2016.
 */
public class MethodCallTest extends Test {
    private Type object;
    private final String propertyName;
    private List<Type> parameters;
    private final boolean restArgs;

    public MethodCallTest(Type object, Type function, String propertyName, List<Type> parameters, Type returnType, String path, TypeContext typeContext, boolean restArgs) {
        super(Arrays.asList(object, function), parameters, returnType, path + PrettyTypes.parametersTypes(parameters), typeContext);
        this.object = object;
        this.propertyName = propertyName;
        this.parameters = parameters;
        this.restArgs = restArgs;
    }

    public Type getObject() {
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

    public boolean isRestArgs() {
        return restArgs;
    }
}
