package dk.webbies.tajscheck.paser.AST;

import com.google.javascript.jscomp.parsing.parser.util.SourceRange;
import dk.webbies.tajscheck.paser.ExpressionVisitor;

/**
 * Created by Erik Krogh Kristensen on 01-09-2015.
 */
public class Identifier extends Expression {
    private  String name;
    public Identifier declaration = null;
    public boolean isGlobal = false;

    public Identifier(SourceRange location, String name) {
        super(location);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Identifier getDeclaration() {
        return declaration;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
    public void setName(String newName) {
        this.name = newName;
    }

}
