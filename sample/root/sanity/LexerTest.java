package sanity;

import com.github.math4tots.crossj.parser.Lexer;
import com.github.math4tots.crossj.parser.Source;
import com.github.math4tots.crossj.parser.Token;

import crossj.*;

public final class LexerTest {

    // private static List<Token> lex(String string) {
    //     return Lexer.lex(new Source("<string>", string));
    // }

    @Test
    public static void mixed() {
        // List<Token> tokens = lex("'a' \"def\" 123 2.2 a.f()");
        // Assert.equals(
        //     tokens.map((Token t) -> Pair.of(t.getType(), t.getValue())),
        //     List.of(
        //         Pair.of("char", "a"),
        //         Pair.of("string", "def"),
        //         Pair.of("int", 123),
        //         Pair.of("double", 2.2),
        //         Pair.of("name", "a"),
        //         Pair.of(".", null),
        //         Pair.of("name", "f"),
        //         Pair.of("(", null),
        //         Pair.of(")", null),
        //         Pair.of("EOF", null)
        //     )
        // );
    }
}
