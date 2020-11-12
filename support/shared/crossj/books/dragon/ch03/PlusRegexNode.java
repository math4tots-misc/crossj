package crossj.books.dragon.ch03;

import crossj.base.Optional;

final class PlusRegexNode implements RegexNode {
    public static final int BINDING_PRECEDENCE = StarRegexNode.BINDING_PRECEDENCE;

    final RegexNode child;

    PlusRegexNode(RegexNode child) {
        this.child = child;
    }

    @Override
    public int getBindingPrecedence() {
        return BINDING_PRECEDENCE;
    }

    @Override
    public String toPattern() {
        return RegexNodeHelper.wrap(child, BINDING_PRECEDENCE) + "+";
    }

    @Override
    public void buildBlock(NFABuilder builder, int startState, int acceptState) {
        builder.buildBlock(child, startState, acceptState);
        builder.connect(acceptState, Optional.empty(), startState);
    }
}
