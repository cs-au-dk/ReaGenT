package dk.webbies.tajscheck.paser;

import dk.webbies.tajscheck.paser.AST.*;
import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.util.List;

/**
 * Created by erik1 on 01-11-2016.
 */
public class AstToStringVisitor implements ExpressionVisitor<Void>, StatementVisitor<Void> {
    private int ident = 0;
    private StringBuilder builder = new StringBuilder();

    @Override
    public Void visit(BinaryExpression binOp) {
        writeParenthesizedExpression(binOp.getLhs());
        write(" ");
        switch (binOp.getOperator()) {
            case LESS_THAN:
                write("<");
                break;
            case EQUAL:
                write("=");
                break;
            case PLUS:
                write("+");
                break;
            case MULT:
                write("*");
                break;
            case BITWISE_OR:
                write("|");
                break;
            case EQUAL_EQUAL_EQUAL:
                write("===");
                break;
            case OR:
                write("||");
                break;
            case AND:
                write("&&");
                break;
            case NOT_EQUAL_EQUAL:
                write("!==");
                break;
            case PLUS_EQUAL:
                write("+=");
                break;
            default:
                throw new RuntimeException("Yet unhandled operator: " + binOp.getOperator());
        }

        write(" ");

        writeParenthesizedExpression(binOp.getRhs());

        return null;
    }

    @Override
    public Void visit(BooleanLiteral bool) {
        write(Boolean.toString(bool.getBooleanValue()));
        return null;
    }

    @Override
    public Void visit(CallExpression call) {
        if (call.getFunction() instanceof Identifier || call.getFunction() instanceof MemberExpression || call.getFunction() instanceof DynamicAccessExpression) {
            call.getFunction().accept(this);
        } else if (call.getFunction() instanceof FunctionExpression) {
            write("(");
            call.getFunction().accept(this);
            write(")");
        } else {
            throw new RuntimeException();
        }
        writeArgs(call.getArgs());

        return null;
    }

    private void writeArgs(List<Expression> args) {
        write("(");
        for (int i = 0; i < args.size(); i++) {
            Expression arg = args.get(i);
            if (i != 0) {
                write(", ");
            }
            arg.accept(this);
        }
        write(")");
    }

    @Override
    public Void visit(CommaExpression commaExpression) {
        throw new RuntimeException();
    }

    @Override
    public Void visit(ConditionalExpression conditionalExpression) {
        throw new RuntimeException();
    }

    @Override
    public Void visit(FunctionExpression func) {
        write("function ");
        if (func.getName() != null) {
            write(func.getName().getName());
            write(" ");
        }
        writeArgs(Util.cast(Expression.class, func.getArguments()));
        if (!func.getBody().getStatements().isEmpty()) {
            write(" {\n");
            ident++;
            writeAsBlock(func.getBody());
            ident--;
            ident();
            write("}");
        } else {
            write(" {}");
        }
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        write(identifier.getName());
        return null;
    }

    @Override
    public Void visit(MemberExpression memberExpression) {
        memberExpression.getExpression().accept(this);
        write(".");
        write(memberExpression.getProperty());
        return null;
    }

    @Override
    public Void visit(DynamicAccessExpression exp) {
        exp.getOperand().accept(this);
        write("[");
        exp.getLookupKey().accept(this);
        write("]");
        return null;
    }

    @Override
    public Void visit(MethodCallExpression methodCallExpression) {
        new CallExpression(null, methodCallExpression.getMemberExpression(), methodCallExpression.getArgs()).accept(this);
        return null;
    }

    @Override
    public Void visit(NewExpression call) {
        write("new ");
        call.getOperand().accept(this);
        writeArgs(call.getArgs());

        return null;
    }

    @Override
    public Void visit(NullLiteral nullLiteral) {
        write("null");
        return null;
    }

    @Override
    public Void visit(NumberLiteral numberLiteral) {
        double d = numberLiteral.getNumber();
        write(Util.toPrettyNumber(d));
        return null;
    }

    @Override
    public Void visit(ObjectLiteral object) {
        if (object.getProperties().isEmpty()) {
            write("{}");
        } else {
            write("{\n");
            ident++;
            for (int i = 0; i < object.getProperties().size(); i++) {
                ObjectLiteral.Property property = object.getProperties().get(i);
                ident();
                AstBuilder.string(property.name).accept(this);
                write(": ");
                property.expression.accept(this);
                if (i != object.getProperties().size() - 1) {
                    write(", ");
                }
                write("\n");
            }

            ident--;
            ident();
            write("}");
        }
        return null;
    }

    @Override
    public Void visit(StringLiteral stringLiteral) {
        write("\"");
        write(stringLiteral.getString().replace("\"", "\\\""));
        write("\"");
        return null;
    }

    @Override
    public Void visit(ThisExpression thisExpression) {
        throw new RuntimeException();
    }

    @Override
    public Void visit(UnaryExpression unary) {
        switch (unary.getOperator()) {
            case POST_PLUS_PLUS:
                writeParenthesizedExpression(unary.getExpression());
                write("++");
                return null;

        }


        switch (unary.getOperator()) {
            case NOT:
                write("!");
                break;
            case TYPEOF:
                write("typeof ");
                break;
            case VOID:
                write("void ");
                break;
            default:
                throw new RuntimeException("Yet unknown operator: " + unary.getOperator());
        }
        writeParenthesizedExpression(unary.getExpression());
        return null;
    }

    private void writeParenthesizedExpression(Expression exp) {
        if (exp instanceof BinaryExpression || exp instanceof UnaryExpression) {
            write("(");
            exp.accept(this);
            write(")");
        } else {
            exp.accept(this);
        }
    }

    @Override
    public Void visit(UndefinedLiteral undefinedLiteral) {
        throw new RuntimeException();
    }

    @Override
    public Void visit(GetterExpression getter) {
        throw new RuntimeException();
    }

    @Override
    public Void visit(SetterExpression setter) {
        throw new RuntimeException();
    }

    @Override
    public Void visit(ArrayLiteral arrayLiteral) {
        write("[");
        List<Expression> elements = arrayLiteral.getExpressions();
        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).accept(this);
            if (i != elements.size() - 1) {
                write(", ");
            }
        }
        write("]");
        return null;
    }

    @Override
    public Void visit(RegExpExpression regExp) {
        throw new RuntimeException();
    }

    @Override
    public Void visit(BlockStatement block) {
        throw new RuntimeException();
    }

    @Override
    public Void visit(BreakStatement breakStatement) {
        ident();
        write("break;\n");
        return null;
    }

    @Override
    public Void visit(ContinueStatement continueStatement) {
        ident();
        write("continue;\n");
        return null;
    }

    @Override
    public Void visit(ExpressionStatement expressionStatement) {
        ident();
        expressionStatement.getExpression().accept(this);
        if (expressionStatement.getExpression() instanceof FunctionExpression) {
            write("\n");
        } else {
            write(";\n");
        }
        return null;
    }

    @Override
    public Void visit(ForStatement forStatement) {
        ident();

        write("for (");

        if (forStatement.getInitialize() instanceof BlockStatement && ((BlockStatement) forStatement.getInitialize()).getStatements().size() == 1) {
            forStatement = new ForStatement(
                    forStatement.location,
                    ((BlockStatement) forStatement.getInitialize()).getStatements().iterator().next(),
                    forStatement.getCondition(),
                    forStatement.getIncrement(),
                    forStatement.getBody()
            );
        }

        assert forStatement.getInitialize() instanceof VariableNode;
        write("var ");
        ((VariableNode) forStatement.getInitialize()).getlValue().accept(this);
        write(" = ");
        ((VariableNode) forStatement.getInitialize()).getInit().accept(this);

        write("; ");

        forStatement.getCondition().accept(this);

        write("; ");

        forStatement.getIncrement().accept(this);

        write(") {\n");
        ident++;

        writeAsBlock(forStatement.getBody());

        ident--;
        ident();
        write("} \n");

        return null;
    }

    @Override
    public Void visit(IfStatement ifStatement) {
        ident();
        write("if (");
        ifStatement.getCondition().accept(this);
        write(") {\n");
        ident++;
        Statement ifBranch = ifStatement.getIfBranch();
        writeAsBlock(ifBranch);
        ident--;
        ident();
        write("}");
        Statement elseBranch = ifStatement.getElseBranch();
        if (elseBranch != null) {
            write(" else { \n");
            ident++;
            writeAsBlock(elseBranch);
            ident--;
            ident();
            write("}");
        }
        write("\n");
        return null;
    }

    private void writeAsBlock(Statement statement) {
        if (!(statement instanceof BlockStatement)) {
            statement.accept(this);
        } else {
            ((BlockStatement) statement).getStatements().forEach(this::writeAsBlock);
        }
    }

    @Override
    public Void visit(Return aReturn) {
        ident();
        write("return ");
        aReturn.getExpression().accept(this);
        write("\n");
        return null;
    }

    @Override
    public Void visit(SwitchStatement switchStatement) {
        ident();
        write("switch (");
        switchStatement.getExpression().accept(this);
        write(") {\n");
        ident++;
        for (Pair<Expression, Statement> pair : switchStatement.getCases()) {
            ident();
            write("case ");
            pair.getLeft().accept(this);
            write(": {\n");
            ident++;
            writeAsBlock(pair.getRight());
            ident--;
            ident();
            write("} \n");
        }

        if (switchStatement.getDefaultCase() != null) {
            ident();
            write("default: {\n");
            ident++;
            writeAsBlock(switchStatement.getDefaultCase());
            ident--;
            ident();
            write("} \n");
        }

        ident--;
        ident();
        write("} \n");
        return null;
    }

    @Override
    public Void visit(ThrowStatement throwStatement) {
        throw new RuntimeException();
    }

    @Override
    public Void visit(VariableNode variableNode) {
        ident();
        write("var ");
        variableNode.getlValue().accept(this);
        write(" = ");
        variableNode.getInit().accept(this);
        write(";\n");
        return null;
    }

    @Override
    public Void visit(WhileStatement whileStatement) {
        throw new RuntimeException();
    }

    @Override
    public Void visit(ForInStatement forinStatement) {
        if (forinStatement.getInitializer() instanceof BlockStatement && ((BlockStatement) forinStatement.getInitializer()).getStatements().size() == 1) {
            forinStatement = new ForInStatement(
                    forinStatement.location,
                    ((BlockStatement) forinStatement.getInitializer()).getStatements().iterator().next(),
                    forinStatement.getCollection(),
                    forinStatement.getBody()
            );
        }

        // for (var key in console) {
        //    console[key] = function () {};
        // }
        ident();

        write("for (");

        assert forinStatement.getInitializer() instanceof VariableNode;
        write("var ");
        ((VariableNode) forinStatement.getInitializer()).getlValue().accept(this);

        write(" in ");

        forinStatement.getCollection().accept(this);

        write(") {\n");
        ident++;

        writeAsBlock(forinStatement.getBody());

        ident--;
        ident();
        write("} \n");

        return null;
    }

    @Override
    public Void visit(TryStatement tryStatement) {
        ident();

        write("try {\n");

        ident++;
        writeAsBlock(tryStatement.getTryBlock());
        ident--;

        ident();
        write("} catch(" + tryStatement.getCatchBlock().getException().getName() + ") { \n");

        ident++;
        writeAsBlock(tryStatement.getCatchBlock().getBody());
        ident--;
        ident();
        write("} ");
        if (tryStatement.getFinallyBlock() != null) {
            write("finally {");
            ident++;
            writeAsBlock(tryStatement.getFinallyBlock());
            ident--;
            ident();
            write("}" );
        }
        write("\n");

        return null;
    }

    @Override
    public Void visit(CatchStatement catchStatement) {
        throw new RuntimeException();
    }

    @Override
    public Void visit(LabeledStatement labeledStatement) {
        throw new RuntimeException();
    }

    public static String toString(Expression exp) {
        AstToStringVisitor visitor = new AstToStringVisitor();
        exp.accept(visitor);
        return visitor.builder.toString();
    }

    public static String toString(Statement stmt) {
        AstToStringVisitor visitor = new AstToStringVisitor();
        stmt.accept(visitor);
        return visitor.builder.toString();
    }

    private void write(String s) {
        this.builder.append(s);
    }

    private void writeLn(String s) {
        ident();
        write(s);
        write("\n");
    }

    private void ident() {
        for (int i = 0; i < this.ident; i++) {
            builder.append("    ");
        }
    }

    public static String toString(List<Statement> program) {
        AstToStringVisitor visitor = new AstToStringVisitor();
        program.forEach(visitor::accept);
        return visitor.builder.toString();
    }

    private void accept(Statement statement) {
        statement.accept(this);
    }
}
