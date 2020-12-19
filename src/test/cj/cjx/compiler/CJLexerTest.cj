package cjx.compiler

import cjx.compiler.CJToken
import cjx.compiler.CJLexer

class CJLexerTest {
    def testSample() {
        {
            val tokens = CJLexer.lex("123 45").get()
            Assert.equal(
                tokens,
                [
                    CJToken.of(CJToken.tInt, "123", 1, 1),
                    CJToken.of(CJToken.tInt, "45", 1, 5),
                    CJToken.of(CJToken.tEof, "", 2, 1),
                ]
            )
        }
        {
            val tokens = CJLexer.lex("abc\n%%").get()
            Assert.equal(
                tokens,
                [
                    CJToken.of(CJToken.tId, "abc", 1, 1),
                    CJToken.of('\n'.toInt(), "", 1, 4),
                    CJToken.of('%'.toInt(), "", 2, 1),
                    CJToken.of('%'.toInt(), "", 2, 2),
                    CJToken.of(CJToken.tEof, "", 3, 1),
                ]
            )
        }
    }
}
