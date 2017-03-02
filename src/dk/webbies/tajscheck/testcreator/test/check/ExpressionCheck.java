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
}
