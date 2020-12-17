package cjx.re

import cjx.re.DFA

private class DFARun {
    val _dfa: DFA
    var _state: Int

    def _new(dfa: DFA): Self {
        return Self.new(dfa, dfa.startState())
    }

    ## Returns true if we are currently in an accepting state
    def isMatching(self: Self): Bool {
        return self._dfa.getMatchIndex(self._state) >= 0
    }

    ## If isMatching is true, returns the index of the first matching RegexNode.
    ## Otherwise, returns -1
    def matchingAlternative(self: Self): Int {
        return self._dfa.getMatchIndex(self._state)
    }

    ## Returns true if it's no longer possible to enter the accepting state.
    def isDead(self: Self): Bool {
        return self._dfa.isDeadState(self._state)
    }

    def accept(self: Self, letter: Int) {
        self._state = self._dfa.transition(self._state, letter)
    }
}
