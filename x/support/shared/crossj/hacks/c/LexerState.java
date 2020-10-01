package crossj.hacks.c;

import crossj.IntArray;
import crossj.Str;

public final class LexerState {
    private final Source source;
    private final IntArray codePoints;
    private int i = 0;
    private int lineno = 1;
    private int colno = 1;

    private LexerState(Source source) {
        this.source = source;
        codePoints = Str.toUTF32(source.getData());
    }

    public static LexerState withSource(Source source) {
        return new LexerState(source);
    }

    public Token gettok() {
        var s = codePoints;
        while (true) {
            var i = this.i;
            while (CChar.isBlank(s.get(i))) {
                i++;
            }
        }
    }

    public Source getSource() {
        return source;
    }

    public int getLineno() {
        return lineno;
    }

    public int getColno() {
        return colno;
    }

    public IntArray getCodePoints() {
        return codePoints;
    }
}
