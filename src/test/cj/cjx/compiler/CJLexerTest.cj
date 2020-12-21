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
        {
            val tokens = CJLexer.lex("def 'x' '\\n' \"Hello world!\"").get()
            Assert.equal(
                tokens,
                [
                    CJToken.of(CJToken.kwDef, "def", 1, 1),
                    CJToken.of(CJToken.tChar, "'x'", 1, 5),
                    CJToken.of(CJToken.tChar, "'\\n'", 1, 9),
                    CJToken.of(CJToken.tString, "\"Hello world!\"", 1, 14),
                    CJToken.of(CJToken.tEof, "", 2, 1),
                ]
            )
        }
        {
            val tokens = CJLexer.lex("TypeName variableName").get()
            Assert.equal(
                tokens,
                [
                    CJToken.of(CJToken.tTypeId, "TypeName", 1, 1),
                    CJToken.of(CJToken.tId, "variableName", 1, 10),
                    CJToken.of(CJToken.tEof, "", 2, 1),
                ]
            )
        }
        {
            # multi-character tokens
            val tokens = CJLexer.lex("== ** <= >>").get()
            Assert.equal(
                tokens,
                [
                    CJToken.of(CJToken.tEq, "", 1, 1),
                    CJToken.of(CJToken.tPower, "", 1, 4),
                    CJToken.of(CJToken.tLe, "", 1, 7),
                    CJToken.of(CJToken.tRshift, "", 1, 10),
                    CJToken.of(CJToken.tEof, "", 2, 1),
                ]
            )
        }
        {
            # comments
            val tokens = CJLexer.lex("asdf\n#hello!\n  # world!\n").get()
            Assert.equal(
                tokens,
                [
                    CJToken.of(CJToken.tId, "asdf", 1, 1),
                    CJToken.of('\n'.toInt(), "", 1, 5),
                    CJToken.of('\n'.toInt(), "", 3, 11),
                    CJToken.of(CJToken.tEof, "", 4, 1),
                ]
            )
        }
    }

    def testDeadStates() {
        {
            # If the dfa has non-negative dead states, it's possible for the
            # lexer to require backtracking a large number of input characters
            # when lexing a document.
            # We check to ensure that that's not the case here.
            val dfa = CJLexer.dfa()
            Assert.withMessage(
                dfa.findDeadState().isEmpty(),
                "CJLexer.dfa() should have no non-negative dead states")
            val run = dfa.start()
            run.accept('a'.toInt())
            Assert.withMessage(not run.isDead(), "dead after a")
            run.accept('b'.toInt())
            Assert.withMessage(not run.isDead(), "dead after b")
            run.accept('%'.toInt())
            Assert.withMessage(run.isDead(), "dead after %")
        }
    }
}
