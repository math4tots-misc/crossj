package crossj.books.dragon.ch03.nfa;

final class OrRegexNode implements RegexNode {
    public static final int BINDING_PRECEDENCE = 30;
    final RegexNode left;
    final RegexNode right;

    OrRegexNode(RegexNode left, RegexNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int getBindingPrecedence() {
        return BINDING_PRECEDENCE;
    }

    @Override
    public String toPattern() {
        return left.toPattern() + "|" + right.toPattern();
    }
}
