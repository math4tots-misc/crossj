package crossj.books.dragon.ch03.nfa;

import crossj.base.Str;

final class LetterRegexNode implements RegexNode {
    public static final int BINIDNG_PRECEDENCE = 90;

    final int letter;

    LetterRegexNode(int letter) {
        this.letter = letter;
    }

    @Override
    public int getBindingPrecedence() {
        return BINIDNG_PRECEDENCE;
    }

    @Override
    public String toPattern() {
        switch (letter) {
            case (int) '\\': return "\\\\";
            case (int) '(': return "\\(";
            case (int) ')': return "\\)";
            default: return Str.fromCodePoint(letter);
        }
    }
}
