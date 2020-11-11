package crossj.books.dragon.ch03;

import crossj.base.Compare;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Optional;
import crossj.base.Repr;
import crossj.base.Set;
import crossj.base.Str;
import crossj.base.XIterable;

public final class NFA {
    public static NFA fromRegexNodeList(List<RegexNode> nodes) {
        return NFABuilder.buildFromRegexNodeList(nodes);
    }

    public static NFA fromRegexNodes(RegexNode... nodes) {
        return fromRegexNodeList(List.fromJavaArray(nodes));
    }

    private final List<Map<Optional<Integer>, Set<Integer>>> transitionMap;
    private final int acceptState;

    NFA(List<Map<Optional<Integer>, Set<Integer>>> transitionMap, int acceptState) {
        this.transitionMap = transitionMap;
        this.acceptState = acceptState;
    }

    public int getStartState() {
        return acceptState + 1;
    }

    public int getAcceptState() {
        return acceptState;
    }

    public int getNumberOfAlternatives() {
        return acceptState;
    }

    /**
     * Given a set of states, returns the epsilon-closure of those states.
     */
    public Set<Integer> epsilonClosureOf(Set<Integer> states) {
        var todo = List.fromIterable(states);
        var closure = Set.fromIterable(states);
        while (todo.size() > 0) {
            var state = todo.pop();
            for (var neighborState : transitionMap.get(state).getOrElse(Optional.empty(), () -> Set.of())) {
                if (!closure.contains(neighborState)) {
                    closure.add(neighborState);
                    todo.add(neighborState);
                }
            }
        }
        return closure;
    }

    /**
     * Given a set of states and an input letter, returns a new set of states after
     * the given letter is accepted.
     *
     * The returned set accounts for an epsilon closure after the transition.
     */
    public Set<Integer> transitionOf(XIterable<Integer> states, int letter) {
        if (letter < 0 || letter >= Alphabet.COUNT) {
            letter = Alphabet.CATCH_ALL;
        }
        var transition = Optional.of(letter);
        return epsilonClosureOf(Set.fromIterable(
                states.iter().flatMap(state -> transitionMap.get(state).getOrElse(transition, () -> Set.of()))));
    }

    /**
     * Given a set of states, returns a set of letters that appears as an edge
     * coming out from at least one of those states.
     */
    Set<Integer> lettersFromStates(XIterable<Integer> states) {
        var letters = Set.<Integer>of();
        for (var state : states) {
            for (var transition : transitionMap.get(state).keys()) {
                if (transition.isPresent()) {
                    letters.add(transition.get());
                }
            }
        }
        return letters;
    }

    public NFARun start() {
        return new NFARun(this);
    }

    public String inspect() {
        var sb = Str.builder();
        sb.s("start = ").i(getStartState()).s(", accept = ").i(acceptState).s("\n");
        for (int state = 0; state < transitionMap.size(); state++) {
            sb.i(state).s("\n");
            var localMap = transitionMap.get(state);
            var keys = List.sortedBy(localMap.keys(), (a, b) -> Compare.optionals(a, b));
            for (var key : keys) {
                sb.s("  ").obj(key.map(i -> Repr.of(Str.fromCodePoint(i))).getOrElse("epsilon")).s(" -> ")
                        .obj(List.sorted(localMap.get(key))).s("\n");
            }
        }
        return sb.build();
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
        var run = new NFARun(this);
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
}
