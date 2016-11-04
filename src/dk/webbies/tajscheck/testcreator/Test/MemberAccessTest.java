package dk.webbies.tajscheck.testcreator.Test;

import dk.au.cs.casa.typescript.types.Type;

/**
 * Created by erik1 on 02-11-2016.
 */
public class MemberAccessTest extends Test {
    private Type baseType;
    private final String key;

    public MemberAccessTest(Type dependsOn, Type produces, String key, String path) {
        super(dependsOn, produces, path + "." + key);
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
    public <T> T accept(TestVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
