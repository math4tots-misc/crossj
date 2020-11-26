package crossj.hacks.cj;

import crossj.base.Try;

public final class CJParser {
    public static Try<CJAstItemDefinition> parseString(String filename, String string) {
        var tryTokens = CJLexer.lex(string);
        if (tryTokens.isFail()) {
            return tryTokens.withContext("while lexing " + filename).castFail();
        }
        return CJParserState.fromTokens(filename, tryTokens.get()).parseAll();
    }
}
