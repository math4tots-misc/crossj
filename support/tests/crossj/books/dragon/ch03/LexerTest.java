package crossj.books.dragon.ch03;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Test;
import crossj.base.Try;

public final class LexerTest {

    @Test
    public static void sample() {
        var lexer = Lexer.<String>builder()
            .add("(1|2|3|4|5|6|7|8|9|0)+", m -> Try.ok(List.of("digits:" + m.getMatchText())))
            .add("(a|b|c|d|e|f|g)+", m -> Try.ok(List.of("letters:" + m.getMatchText())))
            .build()
            .get();

        var tokens = lexer.lexAll("24baef44").get();

        Assert.equals(tokens, List.of("digits:24", "letters:baef", "digits:44"));
    }
}
