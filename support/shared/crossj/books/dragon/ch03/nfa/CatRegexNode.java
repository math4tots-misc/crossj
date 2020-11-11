package crossj.books.dragon.ch03.nfa;

final class CatRegexNode implements RegexNode {
    public static final int BINDING_PRECEDENCE = 40;

    final RegexNode left;
    final RegexNode right;

    CatRegexNode(RegexNode left, RegexNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int getBindingPrecedence() {
        return BINDING_PRECEDENCE;
    }

    @Override
    public String toPattern() {
        return RegexBuilder.wrap(left, BINDING_PRECEDENCE) + RegexBuilder.wrap(right, BINDING_PRECEDENCE);
    }
}
