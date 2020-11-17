package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.Repr;
import crossj.base.Str;
import crossj.base.Tuple;

/**
 * A cj token
 */
public final class CJToken {
    // various token types
    public static final int EOF = 1;
    public static final int DOUBLE = 2;
    public static final int INT = 3;
    public static final int ID = 4;
    public static final int CHAR = 5;
    public static final int STRING = 6;
    public static final int NEWLINE = 7;

    // token types in the range 32-127 are reserved for ASCII single character
    // token types.

    // keywords
    public static final int KW_DEF = 201;
    public static final int KW_CLASS = 202;
    public static final int KW_TRUE = 203;
    public static final int KW_FALSE = 204;
    public static final int KW_NULL = 205;
    public static final int KW_IF = 206;
    public static final int KW_ELSE = 207;
    public static final int KW_IMPORT = 208;
    public static final int KW_WHILE = 209;
    public static final int KW_BREAK = 210;
    public static final int KW_CONTINUE = 211;
    public static final int KW_VAR = 212;
    public static final int KW_VAL = 213;

    public static final Tuple<Integer> KEYWORD_TYPES = Tuple.of(KW_DEF, KW_CLASS, KW_TRUE, KW_FALSE, KW_NULL, KW_IF,
            KW_ELSE, KW_IMPORT, KW_WHILE, KW_BREAK, KW_CONTINUE, KW_VAR, KW_VAL);

    public final int type;
    public final String text;
    public final int line;
    public final int column;

    private CJToken(int type, String text, int line, int column) {
        this.type = type;
        this.text = text;
        this.line = line;
        this.column = column;
    }

    public static CJToken of(int type, String text, int line, int column) {
        return new CJToken(type, text, line, column);
    }

    public static CJToken ofInt(String text, int line, int column) {
        return of(INT, text, line, column);
    }

    public static CJToken ofDouble(String text, int line, int column) {
        return of(DOUBLE, text, line, column);
    }

    public static CJToken ofId(String text, int line, int column) {
        return of(ID, text, line, column);
    }

    @Override
    public String toString() {
        return "CJToken.of(" + typeToString(type) + ", " + Repr.reprstr(text) + ", " + line + ", " + column + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CJToken)) {
            return false;
        }
        var b = (CJToken) obj;
        return type == b.type && text.equals(b.text) && line == b.line && column == b.column;
    }

    public static String typeToString(int type) {
        switch (type) {
            case EOF:
                return "CJToken.EOF";
            case INT:
                return "CJToken.INT";
            case DOUBLE:
                return "CJToken.DOUBLE";
            case ID:
                return "CJToken.ID";
            case CHAR:
                return "CJToken.CHAR";
            case STRING:
                return "CJToken.STRING";
            case KW_DEF:
                return "CJToken.KW_DEF";
            case KW_CLASS:
                return "CJToken.KW_CLASS";
            case KW_TRUE:
                return "CJToken.KW_TRUE";
            case KW_FALSE:
                return "CJToken.KW_FALSE";
            case KW_NULL:
                return "CJToken.KW_NULL";
            case KW_IF:
                return "CJToken.KW_IF";
            case KW_IMPORT:
                return "CJToken.KW_IMPORT";
            case KW_WHILE:
                return "CJToken.KW_WHILE";
            case KW_BREAK:
                return "CJToken.KW_BREAK";
            case KW_CONTINUE:
                return "CJToken.KW_CONTINUE";
            case KW_VAR:
                return "CJToken.KW_VAR";
            case KW_VAL:
                return "CJToken.KW_VAL";
            case '\n':
                return "'\\n'";
            default:
                if (type >= 32 || type <= 127) {
                    return "'" + Str.fromCodePoint(type) + "'";
                } else {
                    return "Unknown(" + type + ")";
                }
        }
    }

    public static String keywordTypeToString(int type) {
        var name = typeToString(type);
        Assert.that(name.startsWith("CJToken.KW_"));
        return name.substring("CJToken.KW_".length(), name.length()).toLowerCase();
    }
}
