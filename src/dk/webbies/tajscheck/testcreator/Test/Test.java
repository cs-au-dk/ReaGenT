package dk.webbies.tajscheck.testcreator.Test;

import dk.au.cs.casa.typescript.types.Type;
import dk.au.cs.casa.typescript.types.TypeParameterType;
import dk.webbies.tajscheck.TypesUtil;
import dk.webbies.tajscheck.util.Util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by erik1 on 01-11-2016.
 */
public abstract class Test {
    private final Collection<Type> typeToTest;
    private final Collection<Type> dependsOn;
    private final Type produces;
    private String path;
    private final Map<TypeParameterType, Type> parameterMap;

    protected Test(Collection<Type> typeToTest, Collection<Type> dependsOn, Type produces, String path, Map<TypeParameterType, Type> parameterMap) {
        this.typeToTest = typeToTest;
        this.dependsOn = dependsOn;
        this.produces = produces;
        this.path = path;
        // TODO: Should be able to revert the below, check for getType_18.
//        this.parameterMap = TypesUtil.filterParameterMap(parameterMap, Util.concat(typeToTest, dependsOn, Collections.singletonList(produces)));
        this.parameterMap = TypesUtil.filterParameterMap(parameterMap, Collections.singletonList(produces));
    }

    public Collection<Type> getDependsOn() {
        return dependsOn;
    }

    public Collection<Type> getTypeToTest() {
        return typeToTest;
    }

    public Type getProduces() {
        return produces;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public abstract <T> T accept(TestVisitor<T> visitor);

    @Override
    public boolean equals(Object o) {
        throw new RuntimeException(this.getClass().toString());
    }

    @Override
    public int hashCode() {
        throw new RuntimeException(this.getClass().toString());
    }

    public boolean equalsNoPathBase(Test test) {
        if (!typeToTest.equals(test.typeToTest)) return false;
        if (!dependsOn.equals(test.dependsOn)) return false;
        if (!produces.equals(test.produces)) return false;
        return parameterMap.equals(test.parameterMap);
    }

    public abstract boolean equalsNoPath(Object o);

    public int hashCodeNoPathBase() {
        int result = typeToTest.hashCode();
        result = 31 * result + dependsOn.hashCode();
        result = 31 * result + produces.hashCode();
        result = 31 * result + parameterMap.hashCode();
        return result;
    }

    public abstract int hashCodeNoPath();

    public Map<TypeParameterType, Type> getParameterMap() {
        return parameterMap;
    }
}
