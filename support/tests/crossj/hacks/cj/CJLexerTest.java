package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Test;

public final class CJLexerTest {
    @Test
    public static void sample() {
        {
            var tokens = CJLexer.lex("123 45").get();
            Assert.equals(tokens,
                    List.of(CJToken.ofInt("123", 1, 1), CJToken.ofInt("45", 1, 5), CJToken.of(CJToken.EOF, "", 2, 1)));
        }
        {
            var tokens = CJLexer.lex("abc\n%%").get();
            Assert.equals(tokens, List.of(CJToken.of(CJToken.ID, "abc", 1, 1), CJToken.of('\n', "", 1, 4),
                    CJToken.of('%', "", 2, 1), CJToken.of('%', "", 2, 2), CJToken.of(CJToken.EOF, "", 3, 1)));
        }
        {
            var tokens = CJLexer.lex("def 'x' '\\n' \"Hello world!\"").get();
            Assert.equals(tokens,
                    List.of(CJToken.of(CJToken.KW_DEF, "def", 1, 1), CJToken.of(CJToken.CHAR, "\'x\'", 1, 5),
                            CJToken.of(CJToken.CHAR, "\'\\n\'", 1, 9),
                            CJToken.of(CJToken.STRING, "\"Hello world!\"", 1, 14), CJToken.of(CJToken.EOF, "", 2, 1)));
        }
        {
            var tokens = CJLexer.lex("TypeName variableName").get();
            Assert.equals(tokens, List.of(CJToken.of(CJToken.TYPE_ID, "TypeName", 1, 1),
                    CJToken.of(CJToken.ID, "variableName", 1, 10), CJToken.of(CJToken.EOF, "", 2, 1)));
        }
        {
            // multi-character tokens
            var tokens = CJLexer.lex("== ** <= >>").get();
            Assert.equals(tokens,
                    List.of(CJToken.of(CJToken.EQ, "", 1, 1), CJToken.of(CJToken.POWER, "", 1, 4),
                            CJToken.of(CJToken.LE, "", 1, 7), CJToken.of(CJToken.RSHIFT, "", 1, 10),
                            CJToken.of(CJToken.EOF, "", 2, 1)));
        }
    }
}
