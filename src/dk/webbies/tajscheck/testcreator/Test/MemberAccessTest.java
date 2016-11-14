package dk.webbies.tajscheck.testcreator.Test;

import dk.au.cs.casa.typescript.types.Type;
import dk.au.cs.casa.typescript.types.TypeParameterType;

import java.util.Collections;
import java.util.Map;

/**
 * Created by erik1 on 02-11-2016.
 */
public class MemberAccessTest extends Test {
    private Type baseType;
    private final String key;

    public MemberAccessTest(Type dependsOn, Type produces, String key, String path, Map<TypeParameterType, Type> parameterMap) {
        super(Collections.singletonList(dependsOn), Collections.EMPTY_LIST, produces, path + "." + key, parameterMap);
        this.baseType = dependsOn;
        this.key = key;
    }

    public String getProperty() {
        return key;
    }

    public Type getBaseType() {
        return baseType;
    }

    @Override
    public boolean equalsNoPath(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberAccessTest test = (MemberAccessTest) o;
        if (!test.key.equals(this.key)) return false;
        return super.equalsNoPathBase(test);
    }

    @Override
    public int hashCodeNoPath() {
        return super.hashCodeNoPathBase() + this.key.hashCode();
    }

    @Override
    public <T> T accept(TestVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
