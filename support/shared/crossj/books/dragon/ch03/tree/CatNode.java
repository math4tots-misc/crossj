package crossj.books.dragon.ch03.tree;

import crossj.base.FrozenSet;
import crossj.base.List;
import crossj.base.Set;

public final class CatNode implements SyntaxTreeNode {
    private final SyntaxTreeNode left, right;
    private final FrozenSet<Integer> firstposSet;
    private final FrozenSet<Integer> lastposSet;

    public CatNode(SyntaxTreeNode left, SyntaxTreeNode right) {
        this.left = left;
        this.right = right;
        this.firstposSet = left.nullable() ? FrozenSet.join(left.firstpos(), right.firstpos()) : left.firstpos();
        this.lastposSet = right.nullable() ? FrozenSet.join(left.lastpos(), right.lastpos()) : right.lastpos();
    }

    @Override
    public void numberCharNodes(List<CharNode> charNodes) {
        left.numberCharNodes(charNodes);
        right.numberCharNodes(charNodes);
    }

    @Override
    public boolean nullable() {
        return left.nullable() && right.nullable();
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

        for (var i : left.lastpos()) {
            followPosCache.get(i).addAll(right.firstpos());
        }
    }
}
