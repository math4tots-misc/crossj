package crossj.books.dragon.ch03;

import crossj.base.Assert;
import crossj.base.FrozenSet;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Set;
import crossj.base.Str;

final class DFA {
    private final int[] transitionMap;
    private final int[] acceptMap;

    private DFA(int[] transitionMap, int[] acceptMap) {
        Assert.equals(transitionMap.length, acceptMap.length * Alphabet.COUNT);
        this.transitionMap = transitionMap;
        this.acceptMap = acceptMap;
    }

    public int getNumberOfStates() {
        return acceptMap.length;
    }

    /**
     * Returns the start state of this DFA
     */
    public int getStartState() {
        return 0;
    }

    /**
     * Returns the index of the regex alternative that matches with the given state.
     * If the given state does not match any alternatives, returns -1.
     */
    public int getMatchIndex(int state) {
        return state < 0 ? -1 : acceptMap[state];
    }

    /**
     * Returns true if the given state is dead and it is no longer possible to reach
     * an accepting state.
     */
    public boolean isDeadState(int state) {
        return state < 0;
    }

    /**
     * Given the current state, and an input letter, returns the new resulting
     * state.
     */
    public int transition(int state, int letter) {
        if (state < 0) {
            return -1;
        }
        if (letter < 0 || letter >= Alphabet.COUNT) {
            letter = Alphabet.CATCH_ALL;
        }
        return transitionMap[state * Alphabet.COUNT + letter];
    }

    public DFARun start() {
        return new DFARun(this);
    }

    /**
     * Tries to find the longest matching substring that starts from the beginning
     * of the given string.
     *
     * Returns the length of the match if found, or returns -1 if there is no match.
     */
    public int match(String text) {
        int lastMatch = -1;
        int pos = 0;
        var run = start();
        if (run.isMatching()) {
            lastMatch = pos;
        }
        while (!run.isDead() && pos < text.length()) {
            var letter = text.charAt(pos);
            run.accept(letter);
            pos++;
            if (run.isMatching()) {
                lastMatch = pos;
            }
        }
        return lastMatch;
    }

    public String inspect() {
        var sb = Str.builder();
        sb.s("start = ").i(getStartState()).s("\n");

        for (int state = 0; state < acceptMap.length; state++) {
            sb.s("  ").i(state);
            if (acceptMap[state] >= 0) {
                sb.s(" (").i(acceptMap[state]).s(")");
            }
            sb.s("\n");
            for (int letter = 0; letter < Alphabet.COUNT; letter++) {
                int newState = transitionMap[state * Alphabet.COUNT + letter];
                if (newState >= 0) {
                    sb.s("    ").s(Str.fromCodePoint(letter)).s(" -> ").i(newState).s("\n");
                }
            }
        }

        return sb.build();
    }

    public static DFA fromRegexNodes(RegexNode... nodes) {
        return fromNFA(NFA.fromRegexNodeList(List.fromJavaArray(nodes)));
    }

    public static DFA fromNFA(NFA nfa) {
        var currentStates = nfa.epsilonClosureOf(Set.of(nfa.getStartState()));
        var startState = FrozenSet.fromIterable(currentStates);
        var transitionMap = Map.<FrozenSet<Integer>, Map<Integer, FrozenSet<Integer>>>of();
        var seen = Set.of(startState);
        var todo = List.of(startState);
        var allStates = List.<FrozenSet<Integer>>of();
        var acceptMap = Map.<FrozenSet<Integer>, Integer>of();

        while (todo.size() > 0) {
            var state = todo.pop();
            allStates.add(state);
            transitionMap.put(state, Map.of());
            var localMap = transitionMap.get(state);

            // Check if this is an accepting state, and if so, which alternative it matches.
            int acceptingAlternative = -1;
            for (int alt = 0; alt < nfa.getNumberOfAlternatives(); alt++) {
                if (state.contains(alt)) {
                    acceptingAlternative = alt;
                    break;
                }
            }
            acceptMap.put(state, acceptingAlternative);

            var letters = nfa.lettersFromStates(state);
            for (var letter : letters) {
                var newState = FrozenSet.fromIterable(nfa.transitionOf(state, letter));
                localMap.put(letter, newState);
                if (!seen.contains(newState)) {
                    seen.add(newState);
                    todo.add(newState);
                }
            }
        }

        var frozenSetToIndex = Map.<FrozenSet<Integer>, Integer>of();
        for (int i = 0; i < allStates.size(); i++) {
            frozenSetToIndex.put(allStates.get(i), i);
        }

        // Convert the information in transitionMap and acceptMap to use
        // integers to represent the states rather than FrozenSets
        var newTransitionMap = new int[allStates.size() * Alphabet.COUNT];
        var newAcceptMap = new int[allStates.size()];
        for (int i = 0; i < newAcceptMap.length; i++) {
            newAcceptMap[i] = acceptMap.getOrElse(allStates.get(i), () -> -1);
        }
        for (int i = 0; i < newTransitionMap.length; i++) {
            newTransitionMap[i] = -1;
        }
        for (int state = 0; state < newAcceptMap.length; state++) {
            var localMap = transitionMap.get(allStates.get(state));
            for (var letter : localMap.keys()) {
                var destinationState = localMap.get(letter);
                var destinationStateIndex = frozenSetToIndex.get(destinationState);
                newTransitionMap[state * Alphabet.COUNT + letter] = destinationStateIndex;
            }
        }

        return new DFA(newTransitionMap, newAcceptMap);
    }
}
