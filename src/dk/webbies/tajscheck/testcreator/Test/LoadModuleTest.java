package dk.webbies.tajscheck.testcreator.Test;

import dk.au.cs.casa.typescript.types.Type;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by erik1 on 02-11-2016.
 */
public class LoadModuleTest extends Test {
    private final String module;

    public LoadModuleTest(String module, Type typeToTest) {
        super(Collections.EMPTY_LIST, Collections.EMPTY_LIST, typeToTest, "require(\"" + module + "\")");
        this.module = module;
    }

    public String getModule() {
        return module;
    }

    @Override
    public <T> T accept(TestVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
