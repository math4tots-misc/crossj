package cjx.re

import cjx.re.Alphabet
import cjx.re.NFA

## A running instance of an NFA
## For emulating an NFA without conversion to a DFA
class NFARun {
    val _nfa: NFA
    val _currentStates: Set[Int]

    def from(nfa: NFA): Self {
        val currentStates = nfa.epsilonClosureOf([nfa.startState()])
        return Self.new(nfa, currentStates)
    }

    ## Returns true if we are currently in an accepting state
    def isMatching(self: Self): Bool {
        return self._nfa.acceptState() in self._currentStates
    }

    ## If isMatching is true, returns the index of the first matching RegexNode.
    ## Otherwise, return -1
    def matchingAlternative(self: Self): Int {
        val n = self._nfa.numberOfAlternatives()
        for i = 0; i < n; i += 1 {
            if i in self._currentStates {
                return i
            }
        }
        # no match was found
        return -1
    }

    ## Returns true if our set of states is currently empty, and it's
    ## impossible to enter the accepting state.
    def isDead(self: Self): Bool {
        return self._currentStates.size() == 0
    }

    ## Updates the current state based on the next input letter.
    def accept(self: Self, var letter: Int) {
        letter = Alphabet.normalize(letter)
        self._currentStates = self._nfa.transitionOf(self._currentStates, letter)
    }
}
