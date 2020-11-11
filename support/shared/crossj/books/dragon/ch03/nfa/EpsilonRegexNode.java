package crossj.books.dragon.ch03.nfa;

final class EpsilonRegexNode implements RegexNode {
    public static final int BINDING_PRECEDENCE = LetterRegexNode.BINIDNG_PRECEDENCE;

    @Override
    public int getBindingPrecedence() {
        return BINDING_PRECEDENCE;
    }

    @Override
    public String toPattern() {
        return "";
    }
}
