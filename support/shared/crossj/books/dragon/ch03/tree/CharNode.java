package crossj.books.dragon.ch03.tree;

import crossj.base.FrozenSet;
import crossj.base.List;
import crossj.base.Set;

/**
 * Node that corresponds to matching a single char
 */
public final class CharNode implements SyntaxTreeNode {
    private final int value;
    private int position = -1;

    private CharNode(int value) {
        this.value = value;
    }

    public static CharNode of(int value) {
        return new CharNode(value);
    }

    @Override
    public void numberCharNodes(List<CharNode> charNodes) {
        position = charNodes.size();
        charNodes.add(this);
    }

    @Override
    public boolean nullable() {
        return false;
    }

    @Override
    public FrozenSet<Integer> firstpos() {
        return FrozenSet.of(position);
    }

    @Override
    public FrozenSet<Integer> lastpos() {
        return firstpos();
    }

    public int getPosition() {
        return position;
    }

    public int getCharCode() {
        return value;
    }

    @Override
    public void precomputeFollowPos(List<Set<Integer>> followPosCache) {
    }
}
