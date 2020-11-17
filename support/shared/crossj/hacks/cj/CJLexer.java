package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Str;
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
        b.add("\\(|\\)|\\{|\\}|\\[|\\]|\\+|\\*|-|%|\\.|^|&|\\||!|@|=|;|,|:|\\?", m -> chartok(m));

        // newline
        b.add("\n\\s*", m -> chartok(m));

        // comments
        b.add("#[^\n]+", m -> none());

        // whitespace
        b.add("[^\\S\n]+", m -> none());
        return b.build().get();
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
        var text = m.getMatchText();
        int line = m.getMatchLineNumber();
        int column = m.getMatchColumnNumber();
        int type = Str.codeAt(text, 0);
        return Try.ok(List.of(CJToken.of(type, text, line, column)));
    }

    private static Try<List<CJToken>> tok(int type, RegexMatcher m) {
        return Try.ok(List.of(CJToken.of(type, m.getMatchText(), m.getMatchLineNumber(), m.getMatchColumnNumber())));
    }

    private static Try<List<CJToken>> none() {
        return Try.ok(List.of());
    }
}
