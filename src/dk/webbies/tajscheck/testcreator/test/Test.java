package dk.webbies.tajscheck.testcreator.test;

import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.ParameterMap;
import dk.webbies.tajscheck.TypesUtil;
import dk.webbies.tajscheck.util.Util;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by erik1 on 01-11-2016.
 */
public abstract class Test {
    private final Collection<Type> typeToTest;
    private final Collection<Type> dependsOn;
    private final Collection<Type> produces;
    private String path;
    private final ParameterMap parameterMap;

    protected Test(Collection<Type> typeToTest, Collection<Type> dependsOn, Type produces, String path, ParameterMap parameterMap) {
        this(typeToTest, dependsOn, Collections.singletonList(produces), path, parameterMap);
    }

    protected Test(Collection<Type> typeToTest, Collection<Type> dependsOn, Collection<Type> produces, String path, ParameterMap parameterMap) {
        this.typeToTest = typeToTest;
        this.dependsOn = dependsOn;
        this.produces = produces;
        this.path = path;
        this.parameterMap = parameterMap;
    }

    public Collection<Type> getDependsOn() {
        return dependsOn;
    }

    public Collection<Type> getTypeToTest() {
        return typeToTest;
    }

    public Collection<Type> getProduces() {
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

    public ParameterMap getParameterMap() {
        return parameterMap;
    }
}
