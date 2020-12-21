package cjx.re

import cjx.re.DFA
import cjx.re.LexerBuilder
import cjx.re.RegexMatcher
import cjx.re.Regex

class Lexer[Token] {

    def builder(): LexerBuilder[Token] {
        return LexerBuilder[Token]._new()
    }

    val _regex: Regex
    val _callbacks: List[Fn[Try[List[Token]], RegexMatcher]]

    def lexAll(self: Self, string: String): Try[List[Token]] {
        val tokens = List[Token].builder()
        val matcher = self._regex.matcher(string)
        while matcher.match() {
            val matchIndex = matcher.matchIndex()
            if matcher.atZeroLengthMatch() {
                return Try.fail("Zero length match (pattern " + matchIndex + ")")
            }
            tokens.addAll(self._callbacks.get(matchIndex).call(matcher)?)
        }
        if matcher.walker().peek().isPresent() {
            return Try.fail(
                "Unrecognized token on " +
                matcher.currentLineNumber() + ":" +
                matcher.currentColumnNumber())
        }
        return Try.Ok(tokens.build())
    }

    def dfa(self: Self): DFA {
        return self._regex._dfa
    }
}
