package crossj.books.dragon.ch03.nfa;

import crossj.base.Compare;
import crossj.base.Func1;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Optional;
import crossj.base.Repr;
import crossj.base.Set;
import crossj.base.Str;

public final class NFA {

    public static NFA withRegexBuilder(Func1<RegexNode, RegexBuilder> f) {
        var builder = new RegexBuilder();
        var node = f.apply(builder);
        return NFABuilder.buildFromRegexNode(node);
    }

    private final int startState;
    private final int acceptState;
    private final List<Map<Optional<Integer>, Set<Integer>>> transitionMap;

    NFA(int startState, int acceptState, List<Map<Optional<Integer>, Set<Integer>>> transitionMap) {
        this.startState = startState;
        this.acceptState = acceptState;
        this.transitionMap = transitionMap;
    }

    public int getStartState() {
        return startState;
    }

    public int getAcceptState() {
        return acceptState;
    }

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

    public Set<Integer> transitionOf(Set<Integer> states, int letter) {
        var transition = Optional.of(letter);
        return epsilonClosureOf(Set.fromIterable(
                states.iter().flatMap(state -> transitionMap.get(state).getOrElse(transition, () -> Set.of()))));
    }

    public NFARun start() {
        return new NFARun(this);
    }

    public String inspect() {
        var sb = Str.builder();
        sb.s("start = ").i(startState).s(", accept = ").i(acceptState).s("\n");
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
