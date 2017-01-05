package dk.webbies.tajscheck.testcreator.test;

import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.typeutil.PrettyTypes;
import dk.webbies.tajscheck.typeutil.TypeContext;

import java.util.Collections;
import java.util.List;

/**
 * Created by erik1 on 02-11-2016.
 */
public class ConstructorCallTest extends Test {
    private final Type function;
    private List<Type> parameters;
    private final boolean restArgs;

    public ConstructorCallTest(Type function, List<Type> parameters, Type returnType, String path, TypeContext typeContext, boolean restArgs) {
        super(Collections.singletonList(function), parameters, returnType, path + "new" + PrettyTypes.parametersTypes(parameters), typeContext);
        this.function = function;
        this.parameters = parameters;
        this.restArgs = restArgs;
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

    public boolean isRestArgs() {
        return restArgs;
    }
}
