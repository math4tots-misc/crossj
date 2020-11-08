package crossj.books.dragon.ch03.tree;

import crossj.base.FrozenSet;
import crossj.base.List;
import crossj.base.Set;

public final class StarNode implements SyntaxTreeNode {
    private final SyntaxTreeNode child;

    private StarNode(SyntaxTreeNode child) {
        this.child = child;
    }

    @Override
    public void numberCharNodes(List<CharNode> charNodes) {
        child.numberCharNodes(charNodes);
    }

    @Override
    public boolean nullable() {
        return true;
    }

    @Override
    public FrozenSet<Integer> firstpos() {
        return child.firstpos();
    }

    @Override
    public FrozenSet<Integer> lastpos() {
        return child.lastpos();
    }

    @Override
    public void precomputeFollowPos(List<Set<Integer>> followPosCache) {
        child.precomputeFollowPos(followPosCache);

        for (var i : lastpos()) {
            followPosCache.get(i).addAll(firstpos());
        }
    }
}
