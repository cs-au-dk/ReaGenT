package dk.webbies.tajscheck.testcreator.Test;

import dk.au.cs.casa.typescript.types.Type;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by erik1 on 01-11-2016.
 */
public abstract class Test {
    private final Collection<Type> typeToTest;
    private final Collection<Type> dependsOn;
    private final Type produces;
    private final String path;

    protected Test(Collection<Type> typeToTest, Collection<Type> dependsOn, Type produces, String path) {
        this.typeToTest = typeToTest;
        this.dependsOn = dependsOn;
        this.produces = produces;
        this.path = path;
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

    public abstract <T> T accept(TestVisitor<T> visitor);
}
