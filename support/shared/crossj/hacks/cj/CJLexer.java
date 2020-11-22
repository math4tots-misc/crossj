package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Try;
import crossj.books.dragon.ch03.Lexer;
import crossj.books.dragon.ch03.RegexMatcher;

public final class CJLexer {
    private static final Lexer<CJToken> lexer = buildLexer();

    private static Lexer<CJToken> buildLexer() {
        var b = Lexer.<CJToken>builder();
        b.add("\\d+\\.\\d*|\\.\\d+", m -> tok(CJToken.DOUBLE, m));
        b.add("\\d+", m -> tok(CJToken.INT, m));
        for (int type : CJToken.KEYWORD_TYPES) {
            b.add(CJToken.keywordTypeToString(type), m -> tok(type, m));
        }
        b.add("[A-Z]\\w*", m -> tok(CJToken.TYPE_ID, m));
        b.add("\\w+", m -> tok(CJToken.ID, m));
        b.add("'\\\\.'", m -> tok(CJToken.CHAR, m));
        b.add("'[^'\\\\]'", m -> tok(CJToken.CHAR, m));
        b.add("\"(\\\\.|[^\"\\\\])*\"", m -> tok(CJToken.STRING, m));

        // single character symbol tokens
        b.add("\\(|\\)|\\{|\\}|\\[|\\]|\\+|\\*|/|-|%|~|\\.|^|&|\\||!|@|=|;|,|:|<|>|\\?", m -> chartok(m));

        // multi-character symbol tokens
        b.add("==", m -> symtok(CJToken.EQ, m));
        b.add("!=", m -> symtok(CJToken.NE, m));
        b.add("<=", m -> symtok(CJToken.LE, m));
        b.add(">=", m -> symtok(CJToken.GE, m));
        b.add("<<", m -> symtok(CJToken.LSHIFT, m));
        b.add(">>", m -> symtok(CJToken.RSHIFT, m));
        b.add("//", m -> symtok(CJToken.FLOORDIV, m));
        b.add("\\*\\*", m -> symtok(CJToken.POWER, m));

        // newline
        b.add("\n\\s*", m -> chartok(m));

        // comments
        b.add("#[^\n]+", m -> none());

        // whitespace
        b.add("[^\\S\n]+", m -> none());

        // NOTE: this must be updated whenever the grammar changes.
        b.setPrecomputedDFA(CJLexerPrecomputed.DFA);

        var lexer = b.build().get();
        // var dfa = lexer.getSerializedDFA();
        // IO.println("LENGTH = " + dfa.length);
        // IO.println(IntArray.fromJavaIntArray(dfa));
        // throw XError.withMessage("EXIT");
        return lexer;
    }

    public static Try<List<CJToken>> lex(String string) {
        var tryTokens = lexer.lexAll(string);
        if (tryTokens.isOk()) {
            var tokens = tryTokens.get();
            int line = 1;
            if (tokens.size() > 0) {
                line = tokens.last().line + 1;
            }
            tryTokens.get().add(CJToken.of(CJToken.EOF, "", line, 1));
        }
        return tryTokens;
    }

    private static Try<List<CJToken>> chartok(RegexMatcher m) {
        int type = m.getFirstCodePointOfMatch();
        return Try.ok(List.of(CJToken.of(type, "", m.getMatchLineNumber(), m.getMatchColumnNumber())));
    }

    private static Try<List<CJToken>> tok(int type, RegexMatcher m) {
        return Try.ok(List.of(CJToken.of(type, m.getMatchText(), m.getMatchLineNumber(), m.getMatchColumnNumber())));
    }

    private static Try<List<CJToken>> symtok(int type, RegexMatcher m) {
        return Try.ok(List.of(CJToken.of(type, "", m.getMatchLineNumber(), m.getMatchColumnNumber())));
    }

    private static Try<List<CJToken>> none() {
        return Try.ok(List.of());
    }
}
