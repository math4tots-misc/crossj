package crossj.books.dragon.ch03.tree;

import crossj.base.FrozenSet;
import crossj.base.List;
import crossj.base.Set;

public interface SyntaxTreeNode {
    /**
     * Used to collect and give a unique identifier to each non-epsilon leaf node
     */
    void numberCharNodes(List<CharNode> charNodes);

    /**
     * Returns true iff the regex implied by this subtree node matches an empty
     * string
     */
    boolean nullable();

    /**
     * The set of positions in this node that could potentially match the first
     * character of a (non-empty) matching string.
     */
    FrozenSet<Integer> firstpos();

    /**
     * The set of positions in this node that could potentially match the last
     * character of a (non-empty) matching string
     */
    FrozenSet<Integer> lastpos();

    /**
     * Precompute followPos(p)
     *
     * followPos(p) for a position p, is the set of all positions that can
     * potentially follow p in a matching string.
     *
     * See p. 176 for a more precise definition
     */
    void precomputeFollowPos(List<Set<Integer>> followPosCache);
}
