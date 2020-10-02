package sanity.hacks.c;

import crossj.Assert;
import crossj.List;
import crossj.Test;
import crossj.hacks.c.CLexer;
import crossj.hacks.c.Source;

public final class CLexerTest {

    @Test
    public static void sample() {
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
    }

    private static List<String> lex(String data) {
        return CLexer.getDefault().lexAll(Source.of("<test>", data)).map(t -> t.getType() + "/" + t.getData());
    }
}
