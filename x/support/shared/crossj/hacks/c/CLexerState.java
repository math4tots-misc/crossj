package crossj.hacks.c;

import crossj.BigInt;
import crossj.IntArray;
import crossj.Num;
import crossj.Str;
import crossj.XError;

public final class CLexerState {
    private final static int BACK_SLASH = (int) '\\';
    private final static int SLASH = (int) '/';
    private final static int STAR = (int) '*';
    private final static int DOT = (int) '.';
    private final static int LOWER_F = (int) 'f';
    private final static int UPPER_F = (int) 'F';
    private final static int LOWER_L = (int) 'l';
    private final static int UPPER_L = (int) 'L';
    private final static int LOWER_E = (int) 'e';
    private final static int UPPER_E = (int) 'E';
    private final static int LOWER_X = (int) 'x';
    private final static int UPPER_X = (int) 'X';
    private final static int ZERO = (int) '0';
    private final static int PLUS = (int) '+';
    private final static int MINUS = (int) '-';
    private final static int DOUBLE_QUOTE = (int) '"';
    private final static int SINGLE_QUOTE = (int) '\'';
    private final static String EOF = "EOF";
    private final CLexer lexer;
    private final Source source;
    private final IntArray codePoints;
    private int i = 0;
    private int lineno = 1;
    private int colno = 1;
    private int lastNewline = -1;
    private boolean done = false;
    private Token peek = null;

    CLexerState(CLexer lexer, Source source) {
        this.lexer = lexer;
        this.source = source;
        codePoints = Str.toUTF32(source.getData());
    }

    public Token peek() {
        if (peek == null && !done) {
            peek = pump();
        }
        return peek;
    }

    public Token next() {
        var token = peek();
        if (token == null) {
            throw XError.withMessage("No more tokens");
        }
        peek = null;
        return token;
    }

    private Token pump() {
        var s = codePoints;
        var len = s.size();
        var i = this.i;
        var cStyleComments = lexer.useCStyleComments();
        var hashComments = lexer.useHashComments();
        // skip spaces and comments
        while (true) {
            while (i < len && CChar.isBlank(s.get(i))) {
                i++;
            }
            if (i < len && CChar.isNewline(s.get(i))) {
                lastNewline = i;
                lineno++;
                i++;
                continue;
            }
            if (cStyleComments && i + 1 < len && s.get(i) == SLASH) {
                if (s.get(i + 1) == SLASH) {
                    // line comment
                    i += 2;
                    while (i < len && !CChar.isNewline(s.get(i))) {
                        i++;
                    }
                    continue;
                } else if (s.get(i + 1) == STAR) {
                    // block comment
                    i += 2;
                    while (i + 1 < len && !(s.get(i) == STAR && s.get(i + 1) == SLASH)) {
                        i++;
                    }
                    i += 2;
                    continue;
                }
            }
            if (hashComments && i < len && s.get(i) == (int) '#') {
                // line comment
                while (i < len && !CChar.isNewline(s.get(i))) {
                    i++;
                }
            }
            break;
        }
        var mark = Mark.of(source, lineno, i - lastNewline);
        if (i >= s.size()) {
            this.i = i;
            done = true;
            return Token.of(mark, EOF, null);
        }
        var ch = s.get(i);
        var start = i;
        i++;
        // integer and floating constants
        if (CChar.isDigit(ch) || ch == DOT && i < len && CChar.isDigit(s.get(i))) {
            if (ch == ZERO && i < len) {
                var c = s.get(i);
                i++;
                BigInt value = null;
                if (c == LOWER_X || c == UPPER_X) {
                    // hex literal
                    while (i < len && CChar.isHexDigit(s.get(i))) {
                        i++;
                    }
                    value = BigInt.fromHexString(Str.fromSliceOfCodePoints(s, start + 2, i));
                } else if (CChar.isDigit(c)) {
                    // oct literal
                    while (i < len && CChar.isDigit(s.get(i))) {
                        i++;
                    }
                    value = BigInt.fromOctString(Str.fromSliceOfCodePoints(s, start + 1, i));
                }
                if (value != null) {
                    this.i = i;
                    return Token.of(mark, "INT", value);
                }
            }
            while (i < len && CChar.isDigit(s.get(i))) {
                i++;
            }
            if (i < len && (s.get(i) == DOT || s.get(i) == LOWER_E || s.get(i) == UPPER_E)) {
                // floating constant
                if (s.get(i) == DOT) {
                    i++;
                }
                while (i < len && CChar.isDigit(s.get(i))) {
                    i++;
                }
                // exponent part
                if (i < len && (s.get(i) == LOWER_E || s.get(i) == UPPER_E)) {
                    i++;
                    if (i < len && (s.get(i) == PLUS || s.get(i) == MINUS)) {
                        i++;
                    }
                    while (i < len && CChar.isDigit(s.get(i))) {
                        i++;
                    }
                }
                var type = "DOUBLE";
                var data = Num.parseDouble(Str.fromSliceOfCodePoints(s, start, i));
                if (i < len) {
                    var c = s.get(i);
                    if (c == UPPER_F || c == LOWER_F) {
                        i++;
                        type = "FLOAT";
                    } else if (c == UPPER_L || c == LOWER_L) {
                        i++;
                    }
                }
                this.i = i;
                return Token.of(mark, type, data);
            } else {
                // integer
                var data = Num.parseInt(Str.fromSliceOfCodePoints(s, start, i));
                this.i = i;
                return Token.of(mark, "INT", data);
            }
        }
        // operators and delimiters
        {
            var op = lexer.getOperators().findLongestMatchUsingCharsOrNull(s, start, len);
            if (op != null) {
                // this is ok because all operators are ASCII
                this.i = start + op.length();
                return Token.of(mark, op, null);
            }
        }
        // string and char literals
        if (ch == DOUBLE_QUOTE || ch == SINGLE_QUOTE) {
            var quote = ch;
            while (i < len && s.get(i) != quote) {
                if (s.get(i) == BACK_SLASH) {
                    i += 2;
                } else {
                    i++;
                }
            }
            if (i >= len) {
                throw XError.withMessage(mark.format() + "Unterminated string literal");
            }
            var data = Str.unescape(Str.fromSliceOfCodePoints(s, start + 1, i));
            this.i = i + 1;
            return Token.of(mark, quote == DOUBLE_QUOTE ? "STRING" : "CHAR", data);
        }
        // identifier and keywords
        if (CChar.isLetterOrUnderscore(ch)) {
            while (i < len && CChar.isLetterOrDigitOrUnderscore(s.get(i))) {
                i++;
            }
            var keyword = lexer.getKeywords().findUsingCharsOrNull(s, start, i);
            this.i = i;
            if (keyword != null) {
                return Token.of(mark, keyword, null);
            } else {
                return Token.of(mark, "ID", Str.fromSliceOfCodePoints(s, start, i));
            }
        }
        throw XError.withMessage(mark.format() + "Unrecognized token: " + Str.fromCodePoint(ch));
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
