package cjx.re

import cjx.re.RegexNode
import cjx.re.DFAIR
import cjx.re.NFA
import cjx.re.DFARun
import cjx.re.Alphabet

private class DFA {
    val _startState: Int
    val _transitionMap: List[Int]
    val _acceptMap: List[Int]

    def _new(startState: Int, transitionMap: List[Int], acceptMap: List[Int]): Self {
        Assert.equal(transitionMap.size(), acceptMap.size() * Alphabet.count)
        return Self.new(startState, transitionMap, acceptMap)
    }

    ## number of states in this DFA
    def numberOfStates(self: Self): Int {
        return self._acceptMap.size()
    }

    ## The start state of this DFA
    def startState(self: Self): Int {
        return self._startState
    }

    ## Returns the index of the regex alternative that matches with the given state.
    ## If the given state does not match any alternatives, returns -1.
    def getMatchIndex(self: Self, state: Int): Int {
        return if (state < 0) -1 else self._acceptMap.get(state)
    }

    ## Returns true if the given state is dead and it is no longer possible to reach
    ## an accepting state.
    def isDeadState(self: Self, state: Int): Bool {
        return state < 0
    }

    ## Given the current state, and an input letter, returns the new resulting
    ## state.
    def transition(self: Self, state: Int, var letter: Int): Int {
        if state < 0 {
            return -1
        }
        if letter < 0 or letter >= Alphabet.count {
            letter = Alphabet.catchall
        }
        return self._transitionMap.get(state * Alphabet.count + letter)
    }

    def start(self: Self): DFARun {
        return DFARun._new(self)
    }

    ## Tries to find the longest matching substring that starts from the beginning
    ## of the given string.
    ##
    ## Returns the length of the match if found, or returns -1 if there is no match.
    def match(self: Self, text: String): Int {
        var lastMatch = -1
        var pos = 0
        val run = self.start()
        if run.isMatching() {
            lastMatch = pos
        }
        while not run.isDead() and pos < text.size() {
            val letter = text.codeAt(pos)
            run.accept(letter)
            pos += 1
            if run.isMatching() {
                lastMatch = pos
            }
        }
        return lastMatch
    }

    def inspect(self: Self): String {
        val sb = String.builder()
        sb.add("=== DFA TRANSITIONS === (start = " + self.startState() + ")\n")
        for state = 0; state < self._acceptMap.size(); state += 1 {
            sb.add("  " + state)
            if state == self._startState {
                sb.add(" [START]")
            }
            if self._acceptMap.get(state) >= 0 {
                sb.add(" (accept " + self._acceptMap.get(state) + ")")
            }
            sb.add("\n")
            for letter = 0; letter < Alphabet.count; letter += 1 {
                val newState = self._transitionMap.get(state * Alphabet.count + letter)
                if newState >= 0 {
                    var end = letter
                    while (
                            end < Alphabet.count and
                            self._transitionMap.get(state * Alphabet.count + end) == newState) {
                        end += 1
                    }
                    val last = end - 1
                    sb.add("    ")
                    if letter < last {
                        # consecutive runs of characters transition to the same state
                        sb.add(letter.toChar().repr() + "-" + last.toChar().repr())
                        letter = last
                    } else {
                        sb.add(letter.toChar().repr())
                    }
                    sb.add(" -> " + newState + "\n")
                }
            }
        }
        return sb.build()
    }

    def fromRegexNodes(nodes: List[RegexNode]): Self {
        return Self.fromNFA(NFA.fromRegexNodes(nodes))
    }

    def fromNFA(nfa: NFA): Self {
        val currentStates = nfa.epsilonClosureOf(Set.of([nfa.startState()]))
        val startState = Set.fromIterable(currentStates)
        val transitionMap: MutableMap[Set[Int], MutableMap[Int, Set[Int]]] = @[:]
        val seen = MutableSet.of([startState])
        val todo = @[startState]
        val allStates: MutableList[Set[Int]] = @[]
        val acceptMap: MutableMap[Set[Int], Int] = @[:]

        while todo.toBool() {
            val state = todo.pop()
            allStates.add(state)
            transitionMap.put(state, @[:])
            val localMap = transitionMap.get(state)

            # Check if this is an accepting state, and if so, which alternative it matches.
            var acceptingAlternative = -1
            var repeat = true
            for alt = 0; repeat and alt < nfa.numberOfAlternatives(); alt += 1 {
                if alt in state {
                    acceptingAlternative = alt
                    repeat = false
                }
            }
            if acceptingAlternative != -1 {
                acceptMap.put(state, acceptingAlternative)
            }

            val letters = nfa.lettersFromStates(state)
            for letter in letters {
                val newState = Set.fromIterable(nfa.transitionOf(state, letter))
                localMap.put(letter, newState)
                if newState not in seen {
                    seen.add(newState)
                    todo.add(newState)
                }
            }
        }

        val dfair = DFAIR._fromFrozenSetDescription(
            allStates, startState, transitionMap, acceptMap)
        return dfair.withMinimumNumberOfStates()
    }
}
