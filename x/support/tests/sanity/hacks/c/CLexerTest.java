package sanity.hacks.c;

import crossj.Assert;
import crossj.List;
import crossj.Test;
import crossj.hacks.c.CLexer;
import crossj.hacks.c.Source;

public final class CLexerTest {

    @Test
    public static void includesCertainKeywordsAndOperators() {
        var lexer = CLexer.getDefault();
        Assert.equals(lexer.getOperators().findUsingStringOrNull("+"), "+");
        Assert.equals(lexer.getKeywords().findUsingStringOrNull("break"), "break");
    }

    @Test
    public static void misc() {
        {
            var tokens = lex("");
            Assert.equals(tokens, List.of("EOF/null"));
        }
        {
            var tokens = lex("some names");
            Assert.equals(tokens, List.of("ID/some", "ID/names", "EOF/null"));
        }
        {
            // operators and keyword
            var tokens = lex("+ break");
            Assert.equals(tokens, List.of("+/null", "break/null", "EOF/null"));
        }
        {
            // hex integers
            var tokens = lex("123 0x4FE");
            Assert.equals(tokens, List.of("INT/123", "INT/" + (4 * 16 * 16 + 15 * 16 + 14), "EOF/null"));
        }
        {
            // oct integers
            var tokens = lex("0333");
            Assert.equals(tokens, List.of("INT/" + (3 * 8 * 8 + 3 * 8 + 3), "EOF/null"));
        }
        {
            var tokens = lex("asdf 'c' \"some string\" 5 12.2 2e-3");
            Assert.equals(tokens, List.of("ID/asdf", "CHAR/c", "STRING/some string", "INT/5", "DOUBLE/12.2",
                    "DOUBLE/0.002", "EOF/null"));
        }
        {
            // ;
            var tokens = lex(";\n// foo");
            Assert.equals(tokens, List.of(";/null", "EOF/null"));
        }
        {
            var tokens = lex("import crossj.foo.bar;\npublic class Foo {}");
            Assert.equals(tokens, List.of("ID/import", "ID/crossj", "./null", "ID/foo", "./null", "ID/bar", ";/null",
                    "ID/public", "ID/class", "ID/Foo", "{/null", "}/null", "EOF/null"));
        }
    }

    @Test
    public static void comments() {
        {
            var tokens = lex("2.4e-1 // these are some comments\n" + "next_line");
            Assert.equals(tokens, List.of("DOUBLE/0.24", "ID/next_line", "EOF/null"));
        }
        {
            var tokens = lex("2.4e-1 /* these are some comments */ 'x'\n" + "next_line");
            Assert.equals(tokens, List.of("DOUBLE/0.24", "CHAR/x", "ID/next_line", "EOF/null"));
        }
    }

    private static List<String> lex(String data) {
        return CLexer.getDefault().lexAll(Source.of("<test>", data)).map(t -> t.getType() + "/" + t.getData());
    }
}
