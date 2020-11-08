package crossj.books.dragon.ch03.tree;

import crossj.base.FrozenSet;
import crossj.base.List;
import crossj.base.Set;

public final class OrNode implements SyntaxTreeNode {
    private final SyntaxTreeNode left, right;
    private final FrozenSet<Integer> firstposSet, lastposSet;

    public OrNode(SyntaxTreeNode left, SyntaxTreeNode right) {
        this.left = left;
        this.right = right;
        this.firstposSet = FrozenSet.join(left.firstpos(), right.firstpos());
        this.lastposSet = FrozenSet.join(left.lastpos(), right.lastpos());
    }

    @Override
    public void numberCharNodes(List<CharNode> charNodes) {
        left.numberCharNodes(charNodes);
        right.numberCharNodes(charNodes);
    }

    @Override
    public boolean nullable() {
        return left.nullable() || right.nullable();
    }

    @Override
    public FrozenSet<Integer> firstpos() {
        return firstposSet;
    }

    @Override
    public FrozenSet<Integer> lastpos() {
        return lastposSet;
    }

    @Override
    public void precomputeFollowPos(List<Set<Integer>> followPosCache) {
        left.precomputeFollowPos(followPosCache);
        right.precomputeFollowPos(followPosCache);
    }
}
