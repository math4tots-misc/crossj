package crossj.books.dragon.ch03;

import crossj.base.Func1;
import crossj.base.List;
import crossj.base.Try;

public final class LexerBuilder<Token> {
    List<String> patterns = List.of();
    List<Func1<Try<List<Token>>, RegexMatcher>> callbacks = List.of();

    LexerBuilder() {}

    public LexerBuilder<Token> add(String pattern, Func1<Try<List<Token>>, RegexMatcher> f) {
        patterns.add(pattern);
        callbacks.add(f);
        return this;
    }

    public Try<Lexer<Token>> build() {
        var tryRegex = Regex.fromPatternList(patterns);
        if (tryRegex.isFail()) {
            return tryRegex.castFail();
        }
        return Try.ok(new Lexer<Token>(tryRegex.get(), callbacks));
    }
}
