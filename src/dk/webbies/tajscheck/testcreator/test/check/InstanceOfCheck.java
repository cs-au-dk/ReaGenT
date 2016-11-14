package dk.webbies.tajscheck.testcreator.test.check;

import dk.webbies.tajscheck.paser.AST.Expression;

/**
 * Created by erik1 on 14-11-2016.
 */
public class InstanceOfCheck implements Check {
    private Expression exp;

    public InstanceOfCheck(Expression exp) {
        this.exp = exp;
    }

    public Expression getExp() {
        return exp;
    }

    @Override
    public <T, A> T accept(CheckVisitorWithArgument<T, A> visitor, A a) {
        return visitor.visit(this, a);
    }

}
