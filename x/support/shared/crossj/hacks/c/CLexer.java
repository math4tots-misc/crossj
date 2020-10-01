package crossj.hacks.c;

import crossj.List;
import crossj.Tuple;
import crossj.XIterable;

public final class CLexer {
    public static final Tuple<String> OPERATORS = Tuple.of("&&", "||", "+=", "-=", "*=", "/=", "%=", "!=", "==", "+",
    "-", "*", "/", "%", ",", ":", ";", "{", "}", "[", "]", "(", ")", "...");
    public static final Tuple<String> KEYWORDS = Tuple.of();

    private static final CLexer DEFAULT = new CLexer(OPERATORS, KEYWORDS);

    private final ASCIITrie operators;
    private final ASCIITrie keywords;

    private CLexer(XIterable<String> operators, XIterable<String> keywords) {
        this.operators = ASCIITrie.fromIterable(operators);
        this.keywords = ASCIITrie.fromIterable(keywords);
    }

    public static CLexer getDefault() {
        return DEFAULT;
    }

    public LexerState startLex(Source source) {
        return new LexerState(this, source);
    }

    public List<Token> lexAll(Source source) {
        var state = startLex(source);
        var tokens = List.<Token>of();
        while (state.peek() != null) {
            tokens.add(state.next());
        }
        return tokens;
    }

    public ASCIITrie getOperators() {
        return operators;
    }

    public ASCIITrie getKeywords() {
        return keywords;
    }
}
