package cjx.re

import cjx.re.Lexer
import cjx.re.Regex
import cjx.re.RegexMatcher

class LexerBuilder[Token] {
    val _patterns: MutableList[String]
    val _callbacks: MutableList[Fn[Try[List[Token]], RegexMatcher]]

    def _new(): Self {
        return Self.new(@[], @[])
    }

    def add(self: Self, pattern: String, f: Fn[Try[List[Token]], RegexMatcher]): Self {
        self._patterns.add(pattern)
        self._callbacks.add(f)
        return self
    }

    def build(self: Self): Try[Lexer[Token]] {
        val regex = Regex.fromPatterns(self._patterns.toList())?
        return Try.Ok(Lexer[Token].new(regex, self._callbacks.toList()))
    }
}
