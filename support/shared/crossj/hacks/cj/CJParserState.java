package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Optional;
import crossj.base.Pair;
import crossj.base.Str;
import crossj.base.Try;
import crossj.base.XError;

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

    private boolean atOffset(int tokenType, int offset) {
        return i + offset < tokens.size() && tokens.get(i + offset).type == tokenType;
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

    private Optional<String> consumeDelimitersAndComments() {
        var comments = Optional.<String>empty();
        while (true) {
            if (consumeDelimiters()) {
                continue;
            }
            if (at(CJToken.COMMENT)) {
                comments = Optional.of(next().text);
                continue;
            }
            break;
        }
        return comments;
    }

    Try<CJAstItemDefinition> parseAll() {
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

    private Try<CJAstItemDefinition> parseClassFile() {
        consumeDelimitersAndComments();
        if (!consume(CJToken.KW_PACKAGE)) {
            return fail("Expected 'package'");
        }
        var sb = Str.builder();
        while (true) {
            if (!at(CJToken.ID)) {
                return expectedType(CJToken.ID);
            }
            sb.s(parseID());
            if (consume('.')) {
                sb.c('.');
            } else {
                break;
            }
        }
        var pkg = sb.build();
        var lastComments = consumeDelimitersAndComments();

        var imports = List.<CJAstImport>of();
        while (at(CJToken.KW_IMPORT)) {
            var mark = getMark();
            next();
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
            imports.add(new CJAstImport(mark, sb.build()));
            lastComments = consumeDelimitersAndComments();
        }

        return parseItemDefinition(pkg, imports, lastComments);
    }

    private Try<CJAstItemDefinition> parseItemDefinition(String pkg, List<CJAstImport> imports,
            Optional<String> itemComments) {
        var mark = getMark();
        var modifiers = parseClassDefinitionModifiers();
        if (consume(CJToken.KW_TRAIT)) {
            modifiers |= CJAstItemModifiers.TRAIT;
        } else if (consume(CJToken.KW_UNION)) {
            modifiers |= CJAstItemModifiers.UNION;
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

        var conditionalTraits = List.<Pair<CJAstTraitExpression, List<CJAstTypeCondition>>>of();
        consumeDelimiters();
        if (consume(':')) {
            while (true) {
                consumeDelimiters();
                var tryTrait = parseTraitExpression();
                if (tryTrait.isFail()) {
                    return tryTrait.castFail();
                }
                if (at(CJToken.KW_IF)) {
                    var tryConditions = parseTypeConditions();
                    if (tryConditions.isFail()) {
                        return tryConditions.castFail();
                    }
                    conditionalTraits.add(Pair.of(tryTrait.get(), tryConditions.get()));
                } else {
                    conditionalTraits.add(Pair.of(tryTrait.get(), List.of()));
                }
                consumeDelimiters();
                if (!consume(',')) {
                    break;
                }
                consumeDelimiters();
            }
        }

        consumeDelimiters();
        if (!consume('{')) {
            return expectedType('{');
        }
        var lastComment = consumeDelimitersAndComments();
        var members = List.<CJAstItemMemberDefinition>of();
        while (!consume('}')) {
            if (at(CJToken.KW_IF)) {
                var tryTypeConditions = parseTypeConditions();
                if (tryTypeConditions.isFail()) {
                    return tryTypeConditions.castFail();
                }
                var typeConditions = tryTypeConditions.get();
                consumeDelimitersAndComments();
                if (!consume('{')) {
                    return expectedType('{');
                }
                lastComment = consumeDelimitersAndComments();
                while (!consume('}')) {
                    int memberModifiers = parseClassMemberModifiers();
                    var tryMethod = parseMethodDefinition(typeConditions, lastComment, memberModifiers);
                    if (tryMethod.isFail()) {
                        return tryMethod.castFail();
                    }
                    members.add(tryMethod.get());
                    lastComment = consumeDelimitersAndComments();
                }
            } else {
                var tryMember = parseClassMember(lastComment);
                if (tryMember.isFail()) {
                    return tryMember.castFail();
                }
                members.add(tryMember.get());
            }
            lastComment = consumeDelimitersAndComments();
        }

        return Try.ok(new CJAstItemDefinition(mark, pkg, imports, itemComments, modifiers, name, typeParameters,
                conditionalTraits, members));
    }

    private Try<List<CJAstTypeCondition>> parseTypeConditions() {
        if (!consume(CJToken.KW_IF)) {
            return expectedType(CJToken.KW_IF);
        }
        var conditions = List.<CJAstTypeCondition>of();
        var tryCondition = parseTypeCondition();
        if (tryCondition.isFail()) {
            return tryCondition.castFail();
        }
        conditions.add(tryCondition.get());
        while (consume(CJToken.KW_AND)) {
            tryCondition = parseTypeCondition();
            if (tryCondition.isFail()) {
                return tryCondition.castFail();
            }
            conditions.add(tryCondition.get());
        }
        return Try.ok(conditions);
    }

    private Try<CJAstTypeCondition> parseTypeCondition() {
        var mark = getMark();
        var tryType = parseTypeExpression();
        if (tryType.isFail()) {
            return tryType.castFail();
        }
        var type = tryType.get();
        if (!consume(':')) {
            return expectedType(':');
        }
        var tryTraits = parseTraitExpressionSeq();
        if (tryTraits.isFail()) {
            return tryTraits.castFail();
        }
        var traits = tryTraits.get();
        return Try.ok(new CJAstTypeCondition(mark, type, traits));
    }

    private Try<List<CJAstTraitExpression>> parseTraitExpressionSeq() {
        var traits = List.<CJAstTraitExpression>of();
        var tryTrait = parseTraitExpression();
        if (tryTrait.isFail()) {
            return tryTrait.castFail();
        }
        traits.add(tryTrait.get());
        while (consume('&')) {
            tryTrait = parseTraitExpression();
            if (tryTrait.isFail()) {
                return tryTrait.castFail();
            }
            traits.add(tryTrait.get());
        }
        return Try.ok(traits);
    }

    private int parseClassDefinitionModifiers() {
        int modifiers = 0;
        while (true) {
            boolean modified = false;
            switch (peek().type) {
                case CJToken.KW_NATIVE:
                    next();
                    modifiers |= CJAstItemModifiers.NATIVE;
                    modified = true;
                    break;
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
                    modifiers |= CJAstItemMemberModifiers.STATIC;
                    modified = true;
                    break;
                }
                case CJToken.KW_NATIVE: {
                    next();
                    modifiers |= CJAstItemMemberModifiers.NATIVE;
                    modified = true;
                    break;
                }
                case CJToken.KW_PRIVATE: {
                    next();
                    modifiers |= CJAstItemMemberModifiers.PRIVATE;
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

    private Try<CJAstItemMemberDefinition> parseClassMember(Optional<String> comment) {
        int modifiers = parseClassMemberModifiers();
        switch (peek().type) {
            case CJToken.KW_VAR: {
                return parseFieldDefinition(comment, modifiers).map(x -> x);
            }
            case CJToken.KW_DEF: {
                return parseMethodDefinition(List.of(), comment, modifiers).map(x -> x);
            }
            case CJToken.KW_CASE: {
                return parseUnionCaseDefinition(comment, modifiers).map(x -> x);
            }
            default: {
                return expectedKind("Expected 'def', 'var' or 'case'");
            }
        }
    }

    private Try<CJAstFieldDefinition> parseFieldDefinition(Optional<String> comment, int modifiers) {
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
        return Try.ok(new CJAstFieldDefinition(mark, comment, modifiers, name, type));
    }

    private Try<CJAstMethodDefinition> parseMethodDefinition(List<CJAstTypeCondition> typeConditions,
            Optional<String> comment, int modifiers) {
        var mark = getMark();
        if (!consume(CJToken.KW_DEF)) {
            return expectedType(CJToken.KW_DEF);
        }
        if (!at(CJToken.ID)) {
            return expectedType(CJToken.ID);
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
            return expectedType(':');
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
        return Try.ok(new CJAstMethodDefinition(mark, typeConditions, comment, modifiers, name, typeParameters,
                parameters, returnType, body));
    }

    private Try<CJAstUnionCaseDefinition> parseUnionCaseDefinition(Optional<String> comment, int modifiers) {
        var mark = getMark();
        if (!consume(CJToken.KW_CASE)) {
            return expectedType(CJToken.KW_CASE);
        }
        if (!at(CJToken.TYPE_ID)) {
            return expectedType(CJToken.TYPE_ID);
        }
        var name = parseTypeID();
        var valueTypes = List.<CJAstTypeExpression>of();
        if (consume('(')) {
            while (!consume(')')) {
                var tryType = parseTypeExpression();
                if (tryType.isFail()) {
                    return tryType.castFail();
                }
                valueTypes.add(tryType.get());
                if (!consume(',') && !at(')')) {
                    return expectedType(')');
                }
            }
        }
        return Try.ok(new CJAstUnionCaseDefinition(mark, comment, modifiers, name, valueTypes));
    }

    private Try<CJAstStatement> parseStatement() {
        var mark = getMark();
        switch (peek().type) {
            case '{':
                return parseBlockStatement().map(x -> x);
            case CJToken.KW_IF:
                return parseIfStatement().map(x -> x);
            case CJToken.KW_WHILE: {
                next();
                var tryCond = parseExpression();
                if (tryCond.isFail()) {
                    return tryCond.castFail();
                }
                var cond = tryCond.get();
                var tryBody = parseBlockStatement();
                if (tryBody.isFail()) {
                    return tryBody.castFail();
                }
                var body = tryBody.get();
                return Try.ok(new CJAstWhileStatement(mark, cond, body));
            }
            case CJToken.KW_VAR: {
                next();
                if (!at(CJToken.ID)) {
                    return expectedType(CJToken.ID);
                }
                var name = parseID();
                var type = Optional.<CJAstTypeExpression>empty();
                if (consume(':')) {
                    var tryType = parseTypeExpression();
                    if (tryType.isFail()) {
                        return tryType.castFail();
                    }
                    type = Optional.of(tryType.get());
                }
                if (!consume('=')) {
                    return expectedType('=');
                }
                var tryExpr = parseExpression();
                if (tryExpr.isFail()) {
                    return tryExpr.castFail();
                }
                var expr = tryExpr.get();
                if (!atDelimiter()) {
                    return expectedKind("statement delimiter");
                }
                return Try.ok(new CJAstVariableDeclarationStatement(mark, name, type, expr));
            }
            case CJToken.KW_RETURN: {
                next();
                var tryExpr = parseExpression();
                if (tryExpr.isFail()) {
                    return tryExpr.castFail();
                }
                if (!atDelimiter()) {
                    return expectedKind("statement delimiter");
                }
                return Try.ok(new CJAstReturnStatement(mark, tryExpr.get()));
            }
            case CJToken.KW_SWITCH: {
                next();
                if (!consume(CJToken.KW_UNION)) {
                    return expectedType(CJToken.KW_UNION);
                }
                var tryTarget = parseExpression();
                if (tryTarget.isFail()) {
                    return tryTarget.castFail();
                }
                var target = tryTarget.get();
                consumeDelimitersAndComments();
                if (!consume('{')) {
                    return expectedType('{');
                }
                var unionCases = List.<CJAstSwitchUnionCase>of();
                var defaultBody = Optional.<CJAstBlockStatement>empty();
                consumeDelimitersAndComments();
                while (at(CJToken.KW_CASE)) {
                    var caseMark = getMark();
                    next();
                    if (!at(CJToken.TYPE_ID)) {
                        return expectedType(CJToken.TYPE_ID);
                    }
                    var name = parseTypeID();
                    var valueNames = List.<String>of();
                    if (consume('(')) {
                        while (!consume(')')) {
                            if (!at(CJToken.ID)) {
                                return expectedType(CJToken.ID);
                            }
                            valueNames.add(parseID());
                            if (!consume(',') && !at(')')) {
                                return expectedType(')');
                            }
                        }
                    }
                    var tryBody = parseBlockStatement();
                    if (tryBody.isFail()) {
                        return tryBody.castFail();
                    }
                    var body  = tryBody.get();
                    unionCases.add(new CJAstSwitchUnionCase(caseMark, name, valueNames, body));
                    consumeDelimitersAndComments();
                }
                if (consume(CJToken.KW_DEFAULT)) {
                    var tryBody = parseBlockStatement();
                    if (tryBody.isFail()) {
                        return tryBody.castFail();
                    }
                    var body  = tryBody.get();
                    defaultBody = Optional.of(body);
                }
                if (!consume('}')) {
                    return expectedType('}');
                }
                return Try.ok(new CJAstSwitchUnionStatement(mark, target, unionCases, defaultBody));
            }
            default: {
                if (at(CJToken.ID) && atOffset('=', 1)) {
                    var name = parseID();
                    next();
                    var tryExpr = parseExpression();
                    if (tryExpr.isFail()) {
                        return tryExpr.castFail();
                    }
                    var expr = tryExpr.get();
                    if (!atDelimiter()) {
                        return expectedKind("statement delimiter");
                    }
                    return Try.ok(new CJAstAssignmentStatement(mark, name, expr));
                } else {
                    var tryExpr = parseExpression();
                    if (tryExpr.isFail()) {
                        return tryExpr.castFail();
                    }
                    if (!atDelimiter()) {
                        return expectedKind("statement delimiter");
                    }
                    return Try.ok(new CJAstExpressionStatement(mark, tryExpr.get()));
                }
            }
        }
    }

    private Try<CJAstIfStatement> parseIfStatement() {
        var mark = getMark();
        if (!consume(CJToken.KW_IF)) {
            return expectedType(CJToken.KW_IF);
        }
        var tryCondition = parseExpression();
        if (tryCondition.isFail()) {
            return tryCondition.castFail();
        }
        var condition = tryCondition.get();
        var tryBody = parseBlockStatement();
        if (tryBody.isFail()) {
            return tryBody.castFail();
        }
        var body = tryBody.get();
        if (consume(CJToken.KW_ELSE)) {
            switch (peek().type) {
                case '{':
                case CJToken.KW_IF: {
                    var tryOther = parseStatement();
                    if (tryOther.isFail()) {
                        return tryOther.castFail();
                    }
                    return Try.ok(new CJAstIfStatement(mark, condition, body, Optional.of(tryOther.get())));
                }
                default:
                    return expectedKind("'if' or '{'");
            }
        } else {
            return Try.ok(new CJAstIfStatement(mark, condition, body, Optional.empty()));
        }
    }

    private Try<CJAstBlockStatement> parseBlockStatement() {
        var mark = getMark();
        if (!consume('{')) {
            return expectedType('{');
        }
        consumeDelimitersAndComments();
        var list = List.<CJAstStatement>of();
        while (!consume('}')) {
            var tryStatement = parseStatement();
            if (tryStatement.isFail()) {
                return tryStatement.castFail();
            }
            list.add(tryStatement.get());
            consumeDelimitersAndComments();
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
                if (!consume(',') && !at(']')) {
                    return expectedType(']');
                }
            }
        }
        return Try.ok(list);
    }

    private Try<CJAstTypeParameter> parseTypeParameter() {
        var mark = getMark();
        if (!at(CJToken.TYPE_ID)) {
            return expectedType(CJToken.TYPE_ID);
        }
        var name = parseTypeID();
        var bounds = List.<CJAstTraitExpression>of();
        if (consume(':')) {
            var tryTraits = parseTraitExpressionSeq();
            if (tryTraits.isFail()) {
                return tryTraits.castFail();
            }
            bounds = tryTraits.get();
        }
        return Try.ok(new CJAstTypeParameter(mark, name, bounds));
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
                return expectedType(')');
            }
        }
        return Try.ok(list);
    }

    private Try<CJAstParameter> parseParameter() {
        var mark = getMark();
        if (!at(CJToken.ID)) {
            return expectedType(CJToken.ID);
        }
        var name = parseID();
        if (!consume(':')) {
            return expectedType(':');
        }
        var tryTrait = parseTypeExpression();
        if (tryTrait.isFail()) {
            return tryTrait.castFail();
        }
        return Try.ok(new CJAstParameter(mark, name, tryTrait.get()));
    }

    private Try<CJAstTypeExpression> parseTypeExpression() {
        if (at(CJToken.TYPE_ID)) {
            var mark = getMark();
            var name = parseTypeID();
            var tryArgs = parseTypeArguments();
            if (tryArgs.isFail()) {
                return tryArgs.castFail();
            }
            return Try.ok(new CJAstTypeExpression(mark, name, tryArgs.get()));
        } else {
            return expectedKind("type expression");
        }
    }

    private Try<CJAstTraitExpression> parseTraitExpression() {
        if (!at(CJToken.TYPE_ID)) {
            return expectedKind("trait expression");
        }
        var mark = getMark();
        var name = parseTypeID();
        var tryArgs = parseTypeArguments();
        if (tryArgs.isFail()) {
            return tryArgs.castFail();
        }
        return Try.ok(new CJAstTraitExpression(mark, name, tryArgs.get()));
    }

    private Try<CJAstExpression> parseExpression() {
        return parseExpressionWithPrecedence(0);
    }

    // binds just a little bit tighter than comparisons
    private static final int LOGICAL_NOT_BINDING_POWER = getTokenPrecedence(CJToken.EQ) + 5;

    private static int getTokenPrecedence(int tokenType) {
        // mostly follows Python
        switch (tokenType) {
            // '?' is reserved for short-circuit returns as in Rust.
            // case '?':
            // return 30;
            case CJToken.KW_OR:
                return 40;
            case CJToken.KW_AND:
                return 50;
            case '<':
            case '>':
            case CJToken.EQ:
            case CJToken.NE:
            case CJToken.GE:
            case CJToken.LE:
            case CJToken.KW_IS:
            case CJToken.KW_IN:
            case CJToken.KW_NOT:
                return 60;
            case '|':
                return 70;
            case '&':
                return 80;
            case CJToken.LSHIFT:
            case CJToken.RSHIFT:
                return 90;
            case '+':
            case '-':
                return 100;
            case '*':
            case '/':
            case '%':
            case CJToken.FLOORDIV:
                return 110;
            case CJToken.POWER:
                return 120;
            case '.':
                return 130;
            default:
                return -1;
        }
    }

    private Try<CJAstExpression> parseExpressionWithPrecedence(int precedence) {
        var tryExpr = parseAtom();
        int tokenPrecedence = getTokenPrecedence(peek().type);
        while (tryExpr.isOk() && tokenPrecedence >= precedence) {
            var mark = getMark();
            switch (peek().type) {
                case '+':
                case '-':
                case '*':
                case '/':
                case '%':
                case '<':
                case '>':
                case '|':
                case '&':
                case CJToken.POWER:
                case CJToken.FLOORDIV:
                case CJToken.EQ:
                case CJToken.NE:
                case CJToken.LE:
                case CJToken.GE:
                case CJToken.KW_IN: {
                    String methodName = null;
                    boolean logicalNot = false;
                    boolean rightAssociative = false;
                    switch (next().type) {
                        case '+':
                            methodName = "__add";
                            break;
                        case '-':
                            methodName = "__sub";
                            break;
                        case '*':
                            methodName = "__mul";
                            break;
                        case '/':
                            methodName = "__div";
                            break;
                        case '%':
                            methodName = "__mod";
                            break;
                        case '<':
                            methodName = "__lt";
                            break;
                        case '>':
                            methodName = "__gt";
                            break;
                        case CJToken.POWER:
                            methodName = "__pow";
                            rightAssociative = true;
                            break;
                        case CJToken.FLOORDIV:
                            methodName = "__floordiv";
                            break;
                        case CJToken.EQ:
                            methodName = "__eq";
                            break;
                        case CJToken.NE:
                            methodName = "__eq";
                            logicalNot = true;
                            break;
                        case CJToken.LE:
                            methodName = "__le";
                            break;
                        case CJToken.GE:
                            methodName = "__ge";
                            break;
                        case CJToken.KW_IN:
                            methodName = "__contains";
                            break;
                        case CJToken.KW_NOT:
                            if (!consume(CJToken.KW_IN)) {
                                return expectedType(CJToken.KW_IN);
                            }
                            methodName = "__contains";
                            logicalNot = true;
                            break;
                    }
                    Assert.that(methodName != null);
                    var lhs = tryExpr.get();
                    var tryRhs = parseExpressionWithPrecedence(
                            rightAssociative ? tokenPrecedence : tokenPrecedence + 1);
                    if (tryRhs.isFail()) {
                        return tryRhs.castFail();
                    }
                    var rhs = tryRhs.get();
                    tryExpr = Try.ok(new CJAstInstanceMethodCallExpression(mark, methodName, List.of(lhs, rhs)));
                    if (logicalNot) {
                        tryExpr = Try.ok(new CJAstLogicalNotExpression(mark, tryExpr.get()));
                    }
                    break;
                }
                case '.': {
                    next();
                    if (!at(CJToken.ID)) {
                        return expectedType(CJToken.ID);
                    }
                    var name = parseID();
                    var tryArgs = parseArguments();
                    if (tryArgs.isFail()) {
                        return tryArgs.castFail();
                    }
                    var otherArgs = tryArgs.get();
                    var arg = tryExpr.get();
                    var args = List.of(List.of(arg), otherArgs).flatMap(x -> x);
                    tryExpr = Try.ok(new CJAstInstanceMethodCallExpression(mark, name, args));
                    break;
                }
                default:
                    throw XError.withMessage("TODO: Expression operator " + CJToken.typeToString(peek().type));
            }
            tokenPrecedence = getTokenPrecedence(peek().type);
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
            case CJToken.KW_TRUE: { // true literal
                next();
                return Try.ok(new CJAstLiteralExpression(mark, CJAstLiteralExpression.BOOL, "true"));
            }
            case CJToken.KW_FALSE: { // false literal
                next();
                return Try.ok(new CJAstLiteralExpression(mark, CJAstLiteralExpression.BOOL, "false"));
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
            case '@': { // mutable collection literals
                next();
                if (consume('[')) {
                    if (at(CJToken.TYPE_ID)) {
                        int savedI = i;
                        var tryType = parseTypeExpression();
                        if (tryType.isOk() && consume(']')) {
                            // empty mutable list
                            return Try.ok(new CJAstEmptyMutableListExpression(mark, tryType.get()));
                        } else {
                            // non-empty mutable list
                            i = savedI;
                            return fail("Non-empty mutable lists are not yet supported");
                        }
                    }
                } else {
                    return expectedType('[');
                }
            }
            case CJToken.KW_DEF: {
                next();
                var tryParameters = parseLambdaParameters();
                if (tryParameters.isFail()) {
                    return tryParameters.castFail();
                }
                var parameters = tryParameters.get();
                CJAstStatement body;
                if (consume('=')) {
                    var tryRetExpr = parseExpression();
                    if (tryRetExpr.isFail()) {
                        return tryRetExpr;
                    }
                    var retExpr = tryRetExpr.get();
                    body = new CJAstReturnStatement(mark, retExpr);
                } else {
                    var tryBody = parseBlockStatement();
                    if (tryBody.isFail()) {
                        return tryBody.castFail();
                    }
                    body = tryBody.get();
                }
                return Try.ok(new CJAstLambdaExpression(mark, parameters, body));
            }
            case CJToken.KW_NOT: { // logical not expressions
                next();
                var tryInner = parseExpressionWithPrecedence(LOGICAL_NOT_BINDING_POWER);
                if (tryInner.isFail()) {
                    return tryInner.castFail();
                }
                var inner = tryInner.get();
                return Try.ok(new CJAstLogicalNotExpression(mark, inner));
            }
            case CJToken.TYPE_ID: { // explicit-type method call
                var tryType = parseTypeExpression();
                if (tryType.isFail()) {
                    return tryType.castFail();
                }
                var type = tryType.get();
                if (!consume('.')) {
                    return fail("Expected '.'");
                }
                if (consume(CJToken.KW_NEW)) { // new expressions
                    var tryArgs = parseArguments();
                    if (tryArgs.isFail()) {
                        return tryArgs.castFail();
                    }
                    return Try.ok(new CJAstNewExpression(mark, tryType.get(), tryArgs.get()));
                } else if (at(CJToken.TYPE_ID)) { // union new expressions
                    var name = parseTypeID();
                    var tryArgs = parseArguments();
                    if (tryArgs.isFail()) {
                        return tryArgs.castFail();
                    }
                    var args = tryArgs.get();
                    return Try.ok(new CJAstNewUnionExpression(mark, type, name, args));
                } else { // method call expressions
                    if (!at(CJToken.ID)) {
                        return expectedType(CJToken.ID);
                    }
                    var methodName = parseID();
                    var mustInfer = !at('[');
                    var tryTypeArgs = parseTypeArguments();
                    if (tryTypeArgs.isFail()) {
                        return tryTypeArgs.castFail();
                    }
                    var typeArgs = tryTypeArgs.get();
                    var tryArgs = parseArguments();
                    if (tryArgs.isFail()) {
                        return tryArgs.castFail();
                    }
                    var args = tryArgs.get();
                    if (mustInfer) {
                        Assert.equals(typeArgs.size(), 0);
                        return Try.ok(new CJAstInferredGenericsMethodCallExpression(mark, type, methodName, args));
                    } else {
                        return Try.ok(new CJAstMethodCallExpression(mark, type, methodName, typeArgs, args));
                    }
                }
            }
            case '(': { // parenthetical expression
                next();
                var tryExpr = parseExpression();
                if (tryExpr.isFail()) {
                    return tryExpr.castFail();
                }
                if (!consume(')')) {
                    return expectedType(')');
                }
                return tryExpr;
            }
        }

        return expectedKind("expression");
    }

    private Try<List<String>> parseLambdaParameters() {
        if (!consume('(')) {
            return expectedType('(');
        }
        var ret = List.<String>of();
        while (!consume(')')) {
            if (!at(CJToken.ID)) {
                return expectedType(CJToken.ID);
            }
            ret.add(parseID());
            if (!consume(',') && !at(')')) {
                return expectedType(')');
            }
        }
        return Try.ok(ret);
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
            return expectedType('(');
        }
        var list = List.<CJAstExpression>of();
        while (!consume(')')) {
            var tryExpr = parseExpression();
            if (tryExpr.isFail()) {
                return tryExpr.castFail();
            }
            list.add(tryExpr.get());
            if (!consume(',') && !at(')')) {
                return expectedType(')');
            }
        }
        return Try.ok(list);
    }
}
