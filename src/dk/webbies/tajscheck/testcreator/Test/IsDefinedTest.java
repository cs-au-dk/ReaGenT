package dk.webbies.tajscheck.testcreator.Test;

import dk.au.cs.casa.typescript.types.Type;

import java.util.Collections;

/**
 * Created by erik1 on 07-11-2016.
 */
public class IsDefinedTest extends Test {
    private Type type;

    public IsDefinedTest(Type type, Type produces, String path) {
        super(Collections.singletonList(type), Collections.EMPTY_LIST, produces, path);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public <T> T accept(TestVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
