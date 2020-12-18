package cjx.re

import cjx.re.Lexer
import cjx.re.RegexMatcher

class LexerTest {

    def testSample() {
        val lexer = (Lexer[String].builder()
            .add("(1|2|3|4|5|6|7|8|9|0)+", m -> Try.Ok(["digits:" + m.matchText()]))
            .add("(a|b|c|d|e|f|g)+", m -> Try.Ok(["letters:" + m.matchText()]))
            .build()
            .get())

        val tokens = lexer.lexAll("24baef44").get()

        Assert.equal(tokens, ["digits:24", "letters:baef", "digits:44"])
    }

    def testSample2() {
        val lexer = (Lexer[String].builder()
            .add("(1|2|3|4|5|6|7|8|9|0)+", m -> Try.Ok(["digits:" + m.matchText()]))
            .add(" +", m -> Try.Ok(List[String].empty()))
            .add("a|x+", m -> Try.Ok([m.matchText()]))
            .build()
            .get())
        val tokens = lexer.lexAll("843  43 x").get()
        Assert.equal(tokens, ["digits:843", "digits:43", "x"])
    }

    def testEmptyMatch() {
        val lexer = (Lexer[String].builder()
            .add("(1|2|3|4|5|6|7|8|9|0)+", m -> Try.Ok(["digits:" + m.matchText()]))
            .add(" +", m -> Try.Ok(List[String].empty()))
            .add("a|", m -> Try.Ok([m.matchText()]))
            .build()
            .get())
        Assert.that(lexer.lexAll("843  43").isFail())
        Assert.equal(lexer.lexAll("843  43").getErrorMessage(), "Zero length match (pattern 2)")
    }

    def testSample3() {
        val lexer = (Lexer[String].builder()
            .add("\\d+", m -> Try.Ok(["digits:" + m.matchText()]))
            .add(" +", m -> Try.Ok(List[String].empty()))
            .add("a|x+", m -> Try.Ok([m.matchText()]))
            .build()
            .get())
        val tokens = lexer.lexAll("843  43 x").get()
        Assert.equal(tokens, ["digits:843", "digits:43", "x"])
    }

    def testLineAndColumnNumbers() {
        val lexer = (Lexer[Tuple[Int, Int]].builder()
            .add("\\d+", m -> Try.Ok([Self._locFromMatcher(m)]))
            .add("\\w+", m -> Try.Ok([Self._locFromMatcher(m)]))
            .add("\\w+-\\w+", m -> Try.Ok([Self._locFromMatcher(m)]))
            .add("\\s+", m -> Try.Ok(List[Tuple[Int, Int]].empty()))
            .build().get())

        val tokens = lexer.lexAll("hello world\nnext line\nthird-line").get()

        Assert.equal(tokens, [(1, 1), (1, 7), (2, 1), (2, 6), (3, 1)])
    }

    def _locFromMatcher(matcher: RegexMatcher): Tuple[Int, Int] {
        return (matcher.matchLineNumber(), matcher.matchColumnNumber())
    }
}
