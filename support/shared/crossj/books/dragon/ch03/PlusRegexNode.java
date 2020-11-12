package crossj.books.dragon.ch03;

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
}
