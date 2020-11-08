package crossj.books.dragon.ch03.tree;

import crossj.base.FrozenSet;
import crossj.base.List;
import crossj.base.Set;

public final class EpsilonNode implements SyntaxTreeNode {
    private static final EpsilonNode INSTANCE = new EpsilonNode();

    private EpsilonNode() {}

    public static EpsilonNode getInstance() {
        return INSTANCE;
    }

    @Override
    public void numberCharNodes(List<CharNode> charNodes) {
    }

    @Override
    public boolean nullable() {
        return true;
    }

    @Override
    public FrozenSet<Integer> firstpos() {
        return FrozenSet.of();
    }

    @Override
    public FrozenSet<Integer> lastpos() {
        return firstpos();
    }

    @Override
    public void precomputeFollowPos(List<Set<Integer>> followPosCache) {
    }
}
