package cjx.re

import cjx.re.Regex
import cj.StringWalker
import cjx.re.DFA

class RegexMatcher {
    var _dfa: DFA
    val _walker: StringWalker
    var _matchIndex: Int
    var _matchStartPosition: Int
    var _matchLineNumber: Int
    var _matchColumnNumber: Int
    var _currentLineNumber: Int
    var _currentColumnNumber: Int

    def _new(dfa: DFA, string: String): Self {
        return Self.new(
            dfa,
            StringWalker.of(string),
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
        )
    }

    ## Replaces the regex used to match the current string
    def useRegex(self: Self, regex: Regex) {
        self._dfa = regex.dfa()
    }

    ## Gets the underlying StringWalker instance used to walk the string for matching.
    ##
    ## This can be used to detect the current position in the string and whether the
    ## matcher has reached the end of the string
    def walker(self: Self): StringWalker {
        return self._walker
    }

    ## Tries to find the longets regex match starting from the current position.
    ##
    ## If the match is successful, true is returned, and the position is incremented
    ## to just after the match.
    def match(self: Self): Bool {
        val walker = self._walker
        val dfa = self._dfa
        var state = dfa.startState()
        val startPosition = self._walker.tell()
        val startLineNumber = self._currentLineNumber
        val startColumnNumber = self._currentColumnNumber
        var lastMatchIndex = dfa.getMatchIndex(state)
        var lastMatchPosition = if (lastMatchIndex >= 0) startPosition else -1
        var lastMatchEndLineNumber = -1
        var lastMatchEndColumnNumber = -1
        var runningLineNumber = self._currentLineNumber
        var runningColumnNumber = self._currentColumnNumber
        while walker.peek().isPresent() and not dfa.isDeadState(state) {
            val codePoint = walker.next().get()
            if codePoint == '\n' {
                runningLineNumber += 1
                runningColumnNumber = 1
            } else {
                runningColumnNumber += 1
            }
            state = dfa.transition(state, codePoint.toInt())
            val matchIndex = dfa.getMatchIndex(state)
            if matchIndex >= 0 {
                lastMatchPosition = walker.tell()
                lastMatchIndex = matchIndex
                lastMatchEndLineNumber = runningLineNumber
                lastMatchEndColumnNumber = runningColumnNumber
            }
        }
        if lastMatchPosition == -1 {
            # no match found
            walker.seek(startPosition)
            self._matchStartPosition = -1
            self._matchIndex = -1
            self._matchLineNumber = -1
            self._matchColumnNumber = -1
            self._currentLineNumber = startLineNumber
            self._currentColumnNumber = startColumnNumber
            return false
        } else {
            # match found
            walker.seek(lastMatchPosition)
            self._matchStartPosition = startPosition
            self._matchIndex = lastMatchIndex
            self._matchLineNumber = startLineNumber
            self._matchColumnNumber = startColumnNumber
            self._currentLineNumber = lastMatchEndLineNumber
            self._currentColumnNumber = lastMatchEndColumnNumber
            return true
        }
    }

    ## Like match(), but requires that the entire string matches to the end.
    def matchAll(self: Self): Bool {
        val walker = self._walker
        val startPosition = walker.tell()
        if self.match() {
            if walker.peek().isPresent() {
                walker.seek(startPosition)
                self._matchStartPosition = -1
                self._matchIndex = -1
                return false
            } else {
                return true
            }
        } else {
            return false
        }
    }

    ## After a successful match(), calling this method returns the index of the
    ## matching pattern of the associated Regex.
    def matchIndex(self: Self): Int {
        return self._matchIndex
    }

    def matchLength(self: Self): Int {
        return self._walker.tell() - self._matchStartPosition
    }

    def atZeroLengthMatch(self: Self): Bool {
        return self.matchLength() == 0
    }

    ## After a successful match(), calling this method returns the matching section
    ## of the text.
    def matchText(self: Self): String {
        if self._matchIndex == -1 {
            IO.panic("MatchText() after a failed match")
        }
        return self._walker.cutFrom(self._matchStartPosition)
    }

    def firstCodePointOfMatch(self: Self): Char {
        val walker = self._walker
        if self._matchIndex == -1 {
            IO.panic("firstCodePointOfMatch() after a failed match")
        }
        if self._matchStartPosition >= walker.tell() {
            IO.panic("firstCodePointOfMatch() on zero length match")
        }
        val savedPosition = walker.tell()
        walker.seek(self._matchStartPosition)
        val codePoint = walker.peek().get()
        walker.seek(savedPosition)
        return codePoint
    }

    ## Returns the line number of the start of the last match
    ##
    ## NOTE: return value of this method is invalidated if the underlying
    ## StringWalker is moved externally
    def matchLineNumber(self: Self): Int {
        return self._matchLineNumber
    }

    ## Returns the column number of the start of the last match
    ##
    ## NOTE: return value of this method is invalidated if the underlying
    ## StringWalker is moved externally.
    def matchColumnNumber(self: Self): Int {
        return self._matchColumnNumber
    }

    def currentLineNumber(self: Self): Int {
        return self._currentLineNumber
    }

    def currentColumnNumber(self: Self): Int {
        return self._currentColumnNumber
    }
}
