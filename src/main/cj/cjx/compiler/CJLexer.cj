package cjx.compiler

import cjx.re.RegexMatcher
import cjx.compiler.CJToken
import cjx.re.Lexer

class CJLexer {
    private static val lexer: Lexer[CJToken] = Self.buildLexer()

    private def buildLexer(): Lexer[CJToken] {
        val b = Lexer[CJToken].builder()
        b.add("\\d+\\.\\d*|\\.\\d+", m -> Self.tok(CJToken.tDouble, m))
        b.add("0x[0-9A-Fa-f]+", m -> Self.tok(CJToken.tInt, m)) # hex literals
        b.add("\\d+", m -> Self.tok(CJToken.tInt, m))
        for type in CJToken.keywordTypes {
            b.add(CJToken.keywordTypeToString(type), m -> Self.tok(type, m))
        }
        b.add("[A-Z]\\w*", m -> Self.tok(CJToken.tTypeId, m))
        b.add("[a-z_]\\w*", m -> Self.tok(CJToken.tId, m))
        b.add("'\\\\.'", m -> Self.tok(CJToken.tChar, m))
        b.add("'[^'\\\\]'", m -> Self.tok(CJToken.tChar, m))
        b.add("\"(\\\\.|[^\"\\\\])*\"", m -> Self.tok(CJToken.tString, m))

        # single character symbol tokens
        b.add(
            "\\(|\\)|\\{|\\}|\\[|\\]|\\+|\\*|/|-|%|~|\\.|^|&|\\||!|@|=|;|,|:|<|>|\\?",
            m -> Self.chartok(m))

        # multi-character symbol tokens
        b.add("==", m -> Self.symtok(CJToken.tEq, m))
        b.add("!=", m -> Self.symtok(CJToken.tNe, m))
        b.add("<=", m -> Self.symtok(CJToken.tLe, m))
        b.add(">=", m -> Self.symtok(CJToken.tGe, m))
        b.add("<<", m -> Self.symtok(CJToken.tLshift, m))
        b.add(">>", m -> Self.symtok(CJToken.tRshift, m))
        b.add(">>>", m -> Self.symtok(CJToken.tRshiftu, m))
        b.add("//", m -> Self.symtok(CJToken.tTruncdiv, m))
        b.add("->", m -> Self.symtok(CJToken.tRightArrow, m))
        b.add("\\*\\*", m -> Self.symtok(CJToken.tPower, m))
        b.add("\\+=", m -> Self.symtok(CJToken.tPlusEq, m))
        b.add("-=", m -> Self.symtok(CJToken.tMinusEq, m))
        b.add("\\*=", m -> Self.symtok(CJToken.tStarEq, m))
        b.add("%=", m -> Self.symtok(CJToken.tRemEq, m))

        # newline
        b.add("\n\\s*", m -> Self.chartok(m))

        # comments
        # b.add("##[^\n]*(\n\\s*##[^\n]*)*", m -> Self.tok(CJToken.tComment, m))
        b.add("#[^\n]*(\n\\s*#[^\n]*)*", m -> Self.none())

        # whitespace
        b.add("[^\\S\n]+", m -> Self.none())

        IO.println("patterns = " + b._patterns)

        val lexer = b.build().get()
        return lexer
    }

    def lex(string: String): Try[List[CJToken]] {
        val rawTokens = Self.lexer.lexAll(string)?

        # Filter out newline tokens inside pairs of '()' or '[]' tokens
        # but not '{}'.
        val newTokens = List[CJToken].builder()
        val stack = @[Char]
        var lastLine = 1
        for token in rawTokens {
            val type = token.type.toChar()
            switch type {
                case '('
                case '['
                case '{' {
                    stack.add(type)
                }
                case ')'
                case ']'
                case '}' {
                    if stack.size() > 0 {
                        stack.pop()
                    }
                }
            }
            if type != '\n' or stack.size() == 0 or stack.last() == '{' {
                newTokens.add(token)
            }
            lastLine = token.line + 1
        }
        newTokens.add(CJToken.of(CJToken.tEof, "", lastLine, 1))

        return Try.Ok(newTokens.build())
    }

    private def chartok(m: RegexMatcher): Try[List[CJToken]] {
        val type = m.firstCodePointOfMatch().toInt()
        return Try.Ok([
            CJToken.of(type, "", m.matchLineNumber(), m.matchColumnNumber())
        ])
    }

    private def tok(type: Int, m: RegexMatcher): Try[List[CJToken]] {
        return Try.Ok([
            CJToken.of(
                type,
                m.matchText(),
                m.matchLineNumber(),
                m.matchColumnNumber())])
    }

    private def symtok(type: Int, m: RegexMatcher): Try[List[CJToken]] {
        return Try.Ok([CJToken.of(
            type,
            "",
            m.matchLineNumber(),
            m.matchColumnNumber(),)])
    }

    private def none(): Try[List[CJToken]] {
        return Try.Ok(List[CJToken].empty())
    }
}
