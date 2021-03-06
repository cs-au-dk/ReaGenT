package dk.webbies.tajscheck.paser.AST;

import com.google.javascript.jscomp.parsing.parser.util.SourceRange;
import dk.webbies.tajscheck.paser.ExpressionVisitor;

/**
 * Created by Erik Krogh Kristensen on 07-09-2015.
 */
public class DynamicAccessExpression extends Expression {
    private final Expression operand;
    private final Expression lookupKey;

    public DynamicAccessExpression(SourceRange loc, Expression operand, Expression lookupKey) {
        super(loc);
        this.operand = operand;
        this.lookupKey = lookupKey;
    }

    public Expression getOperand() {
        return operand;
    }

    public Expression getLookupKey() {
        return lookupKey;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
