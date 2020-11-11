package crossj.books.dragon.ch03.nfa;

public interface RegexNode {
    /**
     * Returns an integer indicating how tightly the node's operation binds.
     * For determining where to put parentheses in toString()
     */
    int getBindingPrecedence();

    /**
     * Returns a regex pattern corresponding to this RegexNode
     */
    String toPattern();

    default RegexNode star() {
        return new StarRegexNode(this);
    }
}
