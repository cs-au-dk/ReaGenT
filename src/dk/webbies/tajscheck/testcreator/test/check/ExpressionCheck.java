package dk.webbies.tajscheck.testcreator.test.check;

import dk.webbies.tajscheck.paser.AST.Expression;

import java.util.function.Function;

/**
 * Created by erik1 on 04-01-2017.
 */
public class ExpressionCheck implements Check {
    private Function<Expression, Expression> generator;

    public ExpressionCheck(Function<Expression, Expression> generator) {
        this.generator = generator;
    }

    public Function<Expression, Expression> getGenerator() {
        return generator;
    }

    @Override
    public <T, A> T accept(CheckVisitorWithArgument<T, A> visitor, A a) {
        return visitor.visit(this, a);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpressionCheck that = (ExpressionCheck) o;

        return generator != null ? generator.equals(that.generator) : that.generator == null;
    }

    @Override
    public int hashCode() {
        return generator != null ? generator.hashCode() : 0;
    }
}
