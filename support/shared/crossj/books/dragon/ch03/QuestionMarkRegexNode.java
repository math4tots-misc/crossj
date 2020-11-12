package crossj.books.dragon.ch03;

import crossj.base.Optional;

final class QuestionMarkRegexNode implements RegexNode {
    public static final int BINDING_PRECEDENCE = StarRegexNode.BINDING_PRECEDENCE;

    private final RegexNode inner;

    QuestionMarkRegexNode(RegexNode inner) {
        this.inner = inner;
    }

    @Override
    public int getBindingPrecedence() {
        return BINDING_PRECEDENCE;
    }

    @Override
    public String toPattern() {
        return RegexNodeHelper.wrap(inner, BINDING_PRECEDENCE) + "?";
    }

    @Override
    public void buildBlock(NFABuilder builder, int startState, int acceptState) {
        inner.buildBlock(builder, startState, acceptState);
        builder.connect(startState, Optional.empty(), acceptState);
    }
}
