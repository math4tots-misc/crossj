package crossj.hacks.c;

import crossj.List;
import crossj.Tuple;
import crossj.XIterable;

public final class CLexer {
    public static final Tuple<String> OPERATORS = Tuple.of("...", ">>=", "<<=", "+=", "-=", "*=", "/=", "%=", "&=",
            "^=", "|=", ">>", "<<", "++", "--", "->", "&&", "||", "<=", ">=", "==", "!=", ";", ",", ":", "=", "(", ")",
            ".", "&", "!", "~", "-", "+", "*", "/", "%", "<", ">", "^", "|", "?", "{", "}", "[", "]");
    public static final Tuple<String> KEYWORDS = Tuple.of("auto", "double", "int", "struct", "break", "else", "long",
            "switch", "case", "enum", "register", "typedef", "char", "extern", "return", "union", "const", "float",
            "short", "unsigned", "continue", "for", "signed", "void", "default", "goto", "sizeof", "volatile", "do",
            "if", "static", "while");

    private static final CLexer DEFAULT = builder().build();

    private final ASCIITrie operators;
    private final ASCIITrie keywords;
    private final boolean cStyleComments;
    private final boolean hashComments;

    CLexer(XIterable<String> operators, XIterable<String> keywords, boolean cStyleComments, boolean hashComments) {
        this.operators = ASCIITrie.fromIterable(operators);
        this.keywords = ASCIITrie.fromIterable(keywords);
        this.cStyleComments = cStyleComments;
        this.hashComments = hashComments;
    }

    public static CLexer getDefault() {
        return DEFAULT;
    }

    public static CLexerBuilder builder() {
        return new CLexerBuilder(KEYWORDS, OPERATORS, true, false);
    }

    static CLexer fromBuilder(CLexerBuilder builder) {
        return new CLexer(builder.operators, builder.keywords, builder.cStyleComments, builder.hashComments);
    }

    public CLexerState startLex(Source source) {
        return new CLexerState(this, source);
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

    public boolean useCStyleComments() {
        return cStyleComments;
    }

    public boolean useHashComments() {
        return hashComments;
    }
}
