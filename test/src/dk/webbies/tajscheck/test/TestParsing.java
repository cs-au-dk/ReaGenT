package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.parsespec.ParseDeclaration;
import dk.webbies.tajscheck.paser.AST.Statement;
import dk.webbies.tajscheck.paser.AstToStringVisitor;
import dk.webbies.tajscheck.paser.JavaScriptParser;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by erik1 on 03-01-2017.
 */
public class TestParsing {
    @Test
    public void testEscapedQuotes() throws Exception {
        testParse(
                "var test = \"\\\\[\" + whitespace + \"*(\" + identifier + \")(?:\" + whitespace +\n" +
                "                    // Operator (capture 2)\n" +
                "                    \"*([*^$|!~]?=)\" + whitespace +\n" +
                "                    // \"Attribute values must be CSS identifiers [capture 5] or strings [capture 3 or capture 4]\"\n" +
                "                    \"*(?:'((?:\\\\\\\\.|[^\\\\\\\\'])*)'|\\\"((?:\\\\\\\\.|[^\\\\\\\\\\\"])*)\\\"|(\" + identifier + \"))|)\" + whitespace +\n" +
                "                    \"*\\\\]\""
        );

    }

    @Test
    public void testNewOnExpression() throws Exception {
        testParse(
                "new ((function () {\n" +
                "\n" +
                "\n" +
                "})());"
        );
    }

    private void testParse(String content) {
        JavaScriptParser parser = new JavaScriptParser(ParseDeclaration.Environment.ES5DOM);

        Statement statement = parser.parse("name", content).toTSCreateAST().getBody().getStatements().iterator().next();

        String stringyfied = AstToStringVisitor.toString(statement);

        System.out.println(stringyfied);

        JavaScriptParser.ParseResult iteration2 = parser.parse("someName", stringyfied);

        String stringyfied2 = AstToStringVisitor.toString(iteration2.toTSCreateAST().getBody().getStatements().iterator().next());

        assertThat(stringyfied, is(equalTo(stringyfied2)));

        System.out.println(stringyfied);
    }
}
