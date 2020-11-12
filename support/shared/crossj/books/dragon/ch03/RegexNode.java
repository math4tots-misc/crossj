package crossj.books.dragon.ch03;

import crossj.base.Try;

/**
 * Describes the components of a regular expression.
 *
 * NOTE: In [dragon.ch03] regular expressions, all non-ASCII values are mapped
 * to the value 127.
 */
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

    /**
     * Returns a new regex pattern that matches this RegexNode zero or more times
     */
    default RegexNode star() {
        return new StarRegexNode(this);
    }

    default RegexNode and(RegexNode other) {
        if (this instanceof EpsilonRegexNode) {
            return other;
        } else if (other instanceof EpsilonRegexNode) {
            return this;
        } else {
            return new CatRegexNode(this, other);
        }
    }

    default RegexNode or(RegexNode other) {
        return new OrRegexNode(this, other);
    }

    public static RegexNode epsilon() {
        return new EpsilonRegexNode();
    }

    /**
     * Returns a RegexNode that matches exactly one letter
     *
     * NOTE: this letter must be an ASCII character.
     */
    public static RegexNode ofChar(char letter) {
        return ofCodePoint(letter);
    }

    static RegexNode ofCodePoint(int codePoint) {
        return new LetterRegexNode(codePoint);
    }

    public static Try<RegexNode> fromPattern(String pattern) {
        return RegexNodeParser.parse(pattern);
    }
}
