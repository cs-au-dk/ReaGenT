package dk.webbies.tajscheck.testcreator.test;

import dk.au.cs.casa.typescript.types.Type;
import dk.au.cs.casa.typescript.types.UnionType;
import dk.webbies.tajscheck.ParameterMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by erik1 on 25-11-2016.
 */
public class UnionTypeTest extends Test {
    private UnionType unionType;

    public UnionTypeTest(UnionType unionType, List<Type> elements, String path, ParameterMap parameterMap) {
        super(Collections.singletonList(unionType), new ArrayList<>(), elements, path, parameterMap);
        this.unionType = unionType;
    }

    public UnionType getGetUnionType() {
        return unionType;
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


}
