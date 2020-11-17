package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Optional;
import crossj.base.Str;
import crossj.base.Try;

public final class CJParserState {
    private final String filename;
    private final List<CJToken> tokens;
    private int i = 0;

    private CJParserState(String filename, List<CJToken> tokens) {
        this.filename = filename;
        this.tokens = tokens;
    }

    public static CJParserState fromTokens(String filename, List<CJToken> tokens) {
        return new CJParserState(filename, tokens);
    }

    private CJToken peek() {
        return tokens.get(i);
    }

    private CJToken next() {
        return tokens.get(i++);
    }

    private boolean at(int tokenType) {
        return peek().type == tokenType;
    }

    private CJMark tokenToMark(CJToken token) {
        return CJMark.fromToken(filename, token);
    }

    private CJMark getMark() {
        return tokenToMark(peek());
    }

    private boolean consume(int tokenType) {
        if (at(tokenType)) {
            next();
            return true;
        } else {
            return false;
        }
    }

    private <R> Try<R> fail(String message) {
        var mark = getMark();
        return Try.<R>fail(message).withContext("on " + mark.line + ":" + mark.column + " in file " + filename);
    }

    private <R> Try<R> expectedKind(String kind) {
        return fail("Expected " + kind + " but got " + CJToken.typeToString(peek().type));
    }

    private <R> Try<R> expectedType(int type) {
        return expectedKind(CJToken.typeToString(type));
    }

    private String parseID() {
        Assert.that(at(CJToken.ID));
        return next().text;
    }

    private String parseTypeID() {
        Assert.that(at(CJToken.TYPE_ID));
        return next().text;
    }

    private boolean atDelimiter() {
        return at('\n') || at(';');
    }

    private boolean consumeDelimiters() {
        boolean found = false;
        while (atDelimiter()) {
            next();
            found = true;
        }
        return found;
    }

    Try<CJAstClassDefinition> parseAll() {
        var tryClassDef = parseClassFile();
        consumeDelimiters();
        if (tryClassDef.isFail()) {
            return tryClassDef.castFail();
        }
        if (!at(CJToken.EOF)) {
            return fail("Expected EOF but got " + CJToken.typeToString(peek().type));
        }
        return tryClassDef;
    }

    private Try<CJAstClassDefinition> parseClassFile() {
        if (!consume(CJToken.KW_PACKAGE)) {
            return fail("Expected 'package'");
        }
        var sb = Str.builder();
        while (true) {
            if (!at(CJToken.ID)) {
                return fail("Expected ID");
            }
            sb.s(parseID());
            if (consume('.')) {
                sb.c('.');
            } else {
                break;
            }
        }
        var pkg = sb.build();
        consumeDelimiters();

        var imports = List.<String>of();
        while (consume(CJToken.KW_IMPORT)) {
            sb = Str.builder();
            while (at(CJToken.ID)) {
                sb.s(parseID());
                if (consume('.')) {
                    sb.c('.');
                } else {
                    return fail("Expected '.'");
                }
            }
            if (!at(CJToken.TYPE_ID)) {
                return fail("Expected TYPE_ID");
            }
            sb.s(parseTypeID());
            imports.add(sb.build());
            consumeDelimiters();
        }

        return parseClassDefinition(pkg, imports);
    }

    private Try<CJAstClassDefinition> parseClassDefinition(String pkg, List<String> imports) {
        var mark = getMark();
        var modifiers = parseClassDefinitionModifiers();
        if (consume(CJToken.KW_TRAIT)) {
            modifiers |= CJAstClassDefinitionModifiers.TRAIT;
        } else if (!consume(CJToken.KW_CLASS)) {
            return fail("Expected 'class'");
        }

        if (!at(CJToken.TYPE_ID)) {
            return fail("Expected TYPE_ID");
        }
        var name = parseTypeID();

        var tryTypeParameters = parseTypeParameters();
        if (tryTypeParameters.isFail()) {
            return tryTypeParameters.castFail();
        }
        var typeParameters = tryTypeParameters.get();

        var traits = List.<CJAstTypeExpression>of();
        if (consume(':')) {
            while (true) {
                var tryTrait = parseTypeExpression();
                if (tryTrait.isFail()) {
                    return tryTrait.castFail();
                }
                traits.add(tryTrait.get());
                if (!consume(',')) {
                    break;
                }
            }
        }

        consumeDelimiters();
        if (!consume('{')) {
            return fail("Expected '{'");
        }
        consumeDelimiters();
        var members = List.<CJAstClassMemberDefinition>of();
        while (!consume('}')) {
            var tryMember = parseClassMember();
            if (tryMember.isFail()) {
                return tryMember.castFail();
            }
            members.add(tryMember.get());
            consumeDelimiters();
        }

        return Try.ok(new CJAstClassDefinition(mark, pkg, imports, modifiers, name, typeParameters, traits, members));
    }

    private int parseClassDefinitionModifiers() {
        int modifiers = 0;
        while (true) {
            boolean modified = false;
            switch (peek().type) {
                case CJToken.KW_NATIVE: {
                    next();
                    modifiers |= CJAstClassDefinitionModifiers.NATIVE;
                    modified = true;
                    break;
                }
            }
            if (!modified) {
                break;
            }
        }
        return modifiers;
    }

    private int parseClassMemberModifiers() {
        int modifiers = 0;
        while (true) {
            boolean modified = false;
            switch (peek().type) {
                case CJToken.KW_STATIC: {
                    next();
                    modifiers |= CJAstClassMemberModifiers.STATIC;
                    modified = true;
                    break;
                }
                case CJToken.KW_NATIVE: {
                    next();
                    modifiers |= CJAstClassMemberModifiers.NATIVE;
                    modified = true;
                    break;
                }
                case CJToken.KW_PRIVATE: {
                    next();
                    modifiers |= CJAstClassMemberModifiers.PRIVATE;
                    modified = true;
                    break;
                }
            }
            if (!modified) {
                break;
            }
        }
        return modifiers;
    }

    private Try<CJAstClassMemberDefinition> parseClassMember() {
        int modifiers = parseClassMemberModifiers();
        switch (peek().type) {
            case CJToken.KW_VAR: {
                return parseFieldDefinition(modifiers).map(x -> x);
            }
            case CJToken.KW_DEF: {
                return parseMethodDefinition(modifiers).map(x -> x);
            }
            default: {
                return fail("Expected 'def' or 'var'");
            }
        }
    }

    private Try<CJAstFieldDefinition> parseFieldDefinition(int modifiers) {
        var mark = getMark();
        if (!consume(CJToken.KW_VAR)) {
            return fail("Expected 'var'");
        }
        if (!at(CJToken.ID)) {
            return expectedType(CJToken.ID);
        }
        var name = parseID();
        if (!consume(':')) {
            return expectedType(':');
        }
        var tryType = parseTypeExpression();
        if (tryType.isFail()) {
            return tryType.castFail();
        }
        var type = tryType.get();
        return Try.ok(new CJAstFieldDefinition(mark, modifiers, name, type));
    }

    private Try<CJAstMethodDefinition> parseMethodDefinition(int modifiers) {
        var mark = getMark();
        if (!consume(CJToken.KW_DEF)) {
            return fail("Expected 'def'");
        }
        if (!at(CJToken.ID)) {
            return fail("Expected ID");
        }
        var name = parseID();
        var tryTypeParameters = parseTypeParameters();
        if (tryTypeParameters.isFail()) {
            return tryTypeParameters.castFail();
        }
        var typeParameters = tryTypeParameters.get();
        var tryParameters = parseParameters();
        if (tryParameters.isFail()) {
            return tryParameters.castFail();
        }
        var parameters = tryParameters.get();
        if (!consume(':')) {
            return fail("Expected ':'");
        }
        var tryReturnType = parseTypeExpression();
        if (tryReturnType.isFail()) {
            return tryReturnType.castFail();
        }
        var returnType = tryReturnType.get();
        var body = Optional.<CJAstBlockStatement>empty();
        if (at('{')) {
            var tryBody = parseBlockStatement();
            if (tryBody.isFail()) {
                return tryBody.castFail();
            }
            body = Optional.of(tryBody.get());
        }
        return Try.ok(new CJAstMethodDefinition(mark, modifiers, name, typeParameters, parameters, returnType, body));
    }

    private Try<CJAstStatement> parseStatement() {
        var mark = getMark();
        switch (peek().type) {
            case '{':
                return parseBlockStatement().map(x -> x);
            case CJToken.KW_RETURN: {
                next();
                var tryExpr = parseExpression();
                if (tryExpr.isFail()) {
                    return tryExpr.castFail();
                }
                if (!atDelimiter()) {
                    return fail("Expected statement delimiter");
                }
                return Try.ok(new CJAstReturnStatement(mark, tryExpr.get()));
            }
            default: {
                var tryExpr = parseExpression();
                if (tryExpr.isFail()) {
                    return tryExpr.castFail();
                }
                if (!atDelimiter()) {
                    return fail("Expected statement delimiter");
                }
                return Try.ok(new CJAstExpressionStatement(mark, tryExpr.get()));
            }
        }
    }

    private Try<CJAstBlockStatement> parseBlockStatement() {
        var mark = getMark();
        if (!consume('{')) {
            return fail("Expected '{'");
        }
        consumeDelimiters();
        var list = List.<CJAstStatement>of();
        while (!consume('}')) {
            var tryStatement = parseStatement();
            if (tryStatement.isFail()) {
                return tryStatement.castFail();
            }
            list.add(tryStatement.get());
            consumeDelimiters();
        }
        return Try.ok(new CJAstBlockStatement(mark, list));
    }

    private Try<List<CJAstTypeParameter>> parseTypeParameters() {
        var list = List.<CJAstTypeParameter>of();
        if (consume('[')) {
            while (!consume(']')) {
                var tryTypeParameter = parseTypeParameter();
                if (tryTypeParameter.isFail()) {
                    return tryTypeParameter.castFail();
                }
                list.add(tryTypeParameter.get());
            }
        }
        return Try.ok(list);
    }

    private Try<CJAstTypeParameter> parseTypeParameter() {
        var mark = getMark();
        if (!at(CJToken.TYPE_ID)) {
            return fail("Expected TYPE_ID");
        }
        var name = parseTypeID();
        var bound = Optional.<CJAstTypeExpression>empty();
        if (consume(':')) {
            var tryBoundType = parseTypeExpression();
            if (tryBoundType.isFail()) {
                return tryBoundType.castFail();
            }
            bound = Optional.of(tryBoundType.get());
        }
        return Try.ok(new CJAstTypeParameter(mark, name, bound));
    }

    private Try<List<CJAstParameter>> parseParameters() {
        if (!consume('(')) {
            return fail("Expected '('");
        }
        var list = List.<CJAstParameter>of();
        while (!consume(')')) {
            var tryParameter = parseParameter();
            if (tryParameter.isFail()) {
                return tryParameter.castFail();
            }
            list.add(tryParameter.get());
            if (!consume(',') && !at(')')) {
                return fail("Expected ')");
            }
        }
        return Try.ok(list);
    }

    private Try<CJAstParameter> parseParameter() {
        var mark = getMark();
        if (!at(CJToken.ID)) {
            return fail("Expected ID");
        }
        var name = parseID();
        if (!consume(':')) {
            return expectedType(':');
        }
        var tryType = parseTypeExpression();
        if (tryType.isFail()) {
            return tryType.castFail();
        }
        return Try.ok(new CJAstParameter(mark, name, tryType.get()));
    }

    private Try<CJAstTypeExpression> parseTypeExpression() {
        if (!at(CJToken.TYPE_ID)) {
            return expectedKind("type expression");
        }
        var mark = getMark();
        var name = parseTypeID();
        var tryArgs = parseTypeArguments();
        if (tryArgs.isFail()) {
            return tryArgs.castFail();
        }
        return Try.ok(new CJAstTypeExpression(mark, name, tryArgs.get()));
    }

    private Try<CJAstExpression> parseExpression() {
        var tryExpr = parseAtom();
        if (tryExpr.isFail()) {
            return tryExpr.castFail();
        }
        return tryExpr;
    }

    private Try<CJAstExpression> parseAtom() {
        var mark = getMark();

        switch (peek().type) {
            case CJToken.ID: { // local variable name
                var name = parseID();
                return Try.ok(new CJAstNameExpression(mark, name));
            }
            case CJToken.CHAR: { // char literal
                var rawText = next().text;
                return Try.ok(new CJAstLiteralExpression(mark, CJAstLiteralExpression.CHAR, rawText));
            }
            case CJToken.STRING: { // string literal
                var rawText = next().text;
                return Try.ok(new CJAstLiteralExpression(mark, CJAstLiteralExpression.STRING, rawText));
            }
            case CJToken.INT: { // int literal
                var rawText = next().text;
                return Try.ok(new CJAstLiteralExpression(mark, CJAstLiteralExpression.INT, rawText));
            }
            case CJToken.DOUBLE: { // double literal
                var rawText = next().text;
                return Try.ok(new CJAstLiteralExpression(mark, CJAstLiteralExpression.DOUBLE, rawText));
            }
            case CJToken.TYPE_ID: { // explicit-type method call
                var tryType = parseTypeExpression();
                if (tryType.isFail()) {
                    return tryType.castFail();
                }
                if (!consume('.')) {
                    return fail("Expected '.'");
                }
                if (!at(CJToken.ID)) {
                    return fail("Expected ID");
                }
                var methodName = parseID();
                var tryTypeArgs = parseTypeArguments();
                if (tryTypeArgs.isFail()) {
                    return tryTypeArgs.castFail();
                }
                var tryArgs = parseArguments();
                if (tryArgs.isFail()) {
                    return tryArgs.castFail();
                }
                return Try.ok(new CJAstMethodCallExpression(mark, tryType.get(), methodName, tryTypeArgs.get(),
                        tryArgs.get()));
            }
            case '(': { // parenthetical expression
                var tryExpr = parseExpression();
                if (!consume(')')) {
                    return fail("Expected ')'");
                }
                return tryExpr;
            }
        }

        return fail("Expected expression");
    }

    private Try<List<CJAstTypeExpression>> parseTypeArguments() {
        var args = List.<CJAstTypeExpression>of();
        if (consume('[')) {
            while (!consume(']')) {
                var tryArg = parseTypeExpression();
                if (tryArg.isFail()) {
                    return tryArg.castFail();
                }
                args.add(tryArg.get());
                if (!consume(',') && !at(']')) {
                    return fail("Expected ']'");
                }
            }
        }
        return Try.ok(args);
    }

    private Try<List<CJAstExpression>> parseArguments() {
        if (!consume('(')) {
            return fail("Expected '(");
        }
        var list = List.<CJAstExpression>of();
        while (!consume(')')) {
            var tryExpr = parseExpression();
            if (tryExpr.isFail()) {
                return tryExpr.castFail();
            }
            list.add(tryExpr.get());
            if (!consume(',') && !at(')')) {
                return fail("Expected ')'");
            }
        }
        return Try.ok(list);
    }
}
