package dk.webbies.tajscheck.testcreator.test;

import dk.au.cs.casa.typescript.types.Signature;
import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.typeutil.typeContext.TypeContext;

import java.util.Collections;
import java.util.List;

/**
 * Created by erik1 on 03-11-2016.
 */
public class FunctionCallTest extends FunctionTest {
    private final Type function;
    private final boolean restArgs;

    public FunctionCallTest(Type function, List<Type> parameters, Type returnType, String path, TypeContext typeContext, boolean restArgs, List<Signature> precedingSignatures) {
        super(Collections.singletonList(function), parameters, returnType, path, typeContext, precedingSignatures);
        this.function = function;
        this.restArgs = restArgs;
    }

    public Type getFunction() {
        return function;
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
    public String getTestType() {
        return "function call";
    }

    @Override
    public <T> T accept(TestVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public boolean isRestArgs() {
        return restArgs;
    }
}
