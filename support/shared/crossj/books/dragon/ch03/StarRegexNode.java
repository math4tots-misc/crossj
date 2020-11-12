package crossj.books.dragon.ch03;

final class StarRegexNode implements RegexNode {
    public static final int BINDING_PRECEDENCE = 50;

    final RegexNode child;

    StarRegexNode(RegexNode child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return child + "*";
    }

    @Override
    public int getBindingPrecedence() {
        return BINDING_PRECEDENCE;
    }

    @Override
    public String toPattern() {
        return RegexNodeHelper.wrap(child, BINDING_PRECEDENCE) + "*";
    }
}
