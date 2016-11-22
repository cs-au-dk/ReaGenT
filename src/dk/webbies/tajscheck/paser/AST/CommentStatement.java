package dk.webbies.tajscheck.paser.AST;

import com.google.javascript.jscomp.parsing.parser.util.SourceRange;
import dk.webbies.tajscheck.paser.StatementVisitor;

/**
 * Created by erik1 on 21-11-2016.
 */
public class CommentStatement extends Statement {
    private String comment;

    public CommentStatement(String comment) {
        super(null);
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public <T> T accept(StatementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
