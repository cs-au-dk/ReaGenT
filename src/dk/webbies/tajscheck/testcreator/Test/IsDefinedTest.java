package dk.webbies.tajscheck.testcreator.Test;

import dk.au.cs.casa.typescript.types.Type;
import dk.au.cs.casa.typescript.types.TypeParameterType;

import java.util.Collections;
import java.util.Map;

/**
 * Created by erik1 on 07-11-2016.
 */
public class IsDefinedTest extends Test {
    private Type type;

    public IsDefinedTest(Type type, Type produces, String path, Map<TypeParameterType, Type> parameterMap) {
        super(Collections.singletonList(type), Collections.EMPTY_LIST, produces, path, parameterMap);
        this.type = type;
    }

    public Type getType() {
        return type;
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
