package crossj.books.dragon.ch03;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Pair;
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

    @Test
    public static void sample2() {
        var lexer = Lexer.<String>builder()
            .add("(1|2|3|4|5|6|7|8|9|0)+", m -> Try.ok(List.of("digits:" + m.getMatchText())))
            .add(" +", m -> Try.ok(List.of()))
            .add("a|x+", m -> Try.ok(List.of(m.getMatchText())))
            .build()
            .get();
        var tokens = lexer.lexAll("843  43 x").get();
        Assert.equals(tokens, List.of("digits:843", "digits:43", "x"));
    }

    @Test
    public static void emptyMatch() {
        var lexer = Lexer.<String>builder()
            .add("(1|2|3|4|5|6|7|8|9|0)+", m -> Try.ok(List.of("digits:" + m.getMatchText())))
            .add(" +", m -> Try.ok(List.of()))
            .add("a|", m -> Try.ok(List.of(m.getMatchText())))
            .build()
            .get();
        Assert.that(lexer.lexAll("843  43").isFail());
        Assert.equals(lexer.lexAll("843  43").getErrorMessage(), "Zero length match (pattern 2)");
    }

    @Test
    public static void sample3() {
        var lexer = Lexer.<String>builder()
            .add("\\d+", m -> Try.ok(List.of("digits:" + m.getMatchText())))
            .add(" +", m -> Try.ok(List.of()))
            .add("a|x+", m -> Try.ok(List.of(m.getMatchText())))
            .build()
            .get();
        var stream = lexer.lex("843  43 x");
        var tokens = List.<String>of();
        while (stream.hasNext()) {
            tokens.add(stream.next().get());
        }
        Assert.equals(tokens, List.of("digits:843", "digits:43", "x"));
    }

    @Test
    public static void lineAndColumnNumbers() {
        var lexer = Lexer.<Pair<Integer, Integer>>builder()
            .add("\\d+", m -> Try.ok(List.of(locFromMatcher(m))))
            .add("\\w+", m -> Try.ok(List.of(locFromMatcher(m))))
            .add("\\w+-\\w+", m -> Try.ok(List.of(locFromMatcher(m))))
            .add("\\s+", m -> Try.ok(List.of()))
            .build().get();

        var tokens = lexer.lexAll("hello world\nnext line\nthird-line").get();

        Assert.equals(tokens, List.of(Pair.of(1, 1), Pair.of(1, 7), Pair.of(2, 1), Pair.of(2, 6), Pair.of(3, 1)));
    }

    private static Pair<Integer, Integer> locFromMatcher(RegexMatcher matcher) {
        return Pair.of(matcher.getMatchLineNumber(), matcher.getMatchColumnNumber());
    }
}
