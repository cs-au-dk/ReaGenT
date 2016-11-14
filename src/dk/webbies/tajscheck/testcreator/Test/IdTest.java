package dk.webbies.tajscheck.testcreator.Test;

import dk.au.cs.casa.typescript.types.Type;
import dk.au.cs.casa.typescript.types.TypeParameterType;

import java.util.Collections;
import java.util.Map;

/**
 * The simplest test there can be, it just loads the type to test, and spits it out the other end.
 */
public class IdTest extends Test {
    private Type type;

    // TODO: This and the "IsDefinedTest" into a single FilterTest, which then has a list of filters.
    public IdTest(Type type, Type produces, String path, Map<TypeParameterType, Type> parameterMap) {
        super(Collections.singletonList(type), Collections.EMPTY_LIST, produces, path, parameterMap);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public <T> T accept(TestVisitor<T> visitor) {
        return visitor.visit(this);
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
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

}
