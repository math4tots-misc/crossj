package cjx.compiler

import cjx.compiler.CJLexer

class CJLexerTest {
    def testSample() {
        {
            val tokens = CJLexer.lex("123 45").get()
            Assert.equal(
                tokens,
                []
            )
        }
    }
}
