package crossj.books.dragon.ch03;

import crossj.base.Func1;
import crossj.base.IO;
import crossj.base.List;
import crossj.base.Try;

public final class Lexer<Token> {

    public static <Token> LexerBuilder<Token> builder() {
        return new LexerBuilder<>();
    }

    private final Regex regex;
    private final List<Func1<Try<List<Token>>, RegexMatcher>> callbacks;

    Lexer(Regex regex, List<Func1<Try<List<Token>>, RegexMatcher>> callbacks) {
        this.regex = regex;
        this.callbacks = callbacks;
    }

    Regex getRegex() {
        return regex;
    }

    List<Func1<Try<List<Token>>, RegexMatcher>> getCallbacks() {
        return callbacks;
    }

    public Try<List<Token>> lexAll(String string) {
        var tokens = List.<Token>of();
        var matcher = regex.matcher(string);
        while (matcher.match()) {
            var matchIndex = matcher.getMatchIndex();
            var tryTokenList = callbacks.get(matchIndex).apply(matcher);
            if (tryTokenList.isFail()) {
                return tryTokenList;
            }
            IO.println("tryTokenList.get() = " + tryTokenList.get());
            tokens.addAll(tryTokenList.get());
        }
        if (matcher.getStrIter().hasCodePoint()) {
            return Try.fail("Unrecognized token while lexing at position " + matcher.getStrIter().getPosition());
        }
        return Try.ok(tokens);
    }
}
