package crossj.books.dragon.ch03.tree;

import crossj.base.FrozenSet;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Set;

/**
 * The syntax tree of a parsed regular expression
 */
public final class SyntaxTree {
    private final SyntaxTreeNode root;
    private final List<CharNode> charNodes = List.of();
    private final List<FrozenSet<Integer>> followPosCache;

    private SyntaxTree(SyntaxTreeNode root) {
        this.root = root;
        root.numberCharNodes(charNodes);
        {
            var cache = List.ofSize(charNodes.size(), () -> Set.<Integer>of());
            root.precomputeFollowPos(cache);
            followPosCache = cache.map(set -> FrozenSet.fromIterable(set));
        }
    }

    FrozenSet<Integer> followpos(int p) {
        return followPosCache.get(p);
    }

    void computeDFA() {
        var startState = root.firstpos();
        var dStates = Set.of(startState);

        // dTrans maps (dfa-state) -> (input-symbol) -> (new-dfa-state)
        var dTrans = Map.<FrozenSet<Integer>, Map<Integer, FrozenSet<Integer>>>of();
        var unmarked = List.fromIterable(dStates);
        while (unmarked.size() > 0) {
            var state = unmarked.pop();
            dTrans.put(state, Map.of());
            var localTransitions = dTrans.get(state);

            var charCodesToPositions = Map.<Integer, Set<Integer>>of();
            for (var pos : state) {
                var charCode = getCharCodeForPos(pos);
                var positions = charCodesToPositions.getOrNull(charCode);
                var follow = followpos(pos);
                if (positions == null) {
                    charCodesToPositions.put(charCode, Set.fromIterable(follow));
                } else {
                    positions.addAll(follow);
                }
            }

            for (var charCode : charCodesToPositions.keys()) {
                var newState = FrozenSet.fromIterable(charCodesToPositions.get(charCode));
                if (!dStates.contains(newState)) {
                    dStates.add(newState);
                    unmarked.add(newState);
                }
                localTransitions.put(charCode, newState);
            }
        }
    }

    int getCharCodeForPos(int pos) {
        return charNodes.get(pos).getCharCode();
    }
}
