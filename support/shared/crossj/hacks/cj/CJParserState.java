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
        return failWithMark(message, mark);
    }

    private <R> Try<R> expectedKind(String kind) {
        return fail("Expected " + kind + " but got " + CJToken.typeToString(peek().type));
    }

    private <R> Try<R> failWithMark(String message, CJMark mark) {
        return Try.<R>fail(message).withContext("on " + mark.line + ":" + mark.column + " in file " + filename);
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

    private boolean atDelimiterOrComment() {
        return atDelimiter() || at(CJToken.COMMENT);
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
                case CJToken.KW_PRIVATE:
                    next();
                    modifiers |= CJAstItemModifiers.PRIVATE;
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
                case CJToken.KW_ASYNC: {
                    next();
                    modifiers |= CJAstItemMemberModifiers.ASYNC;
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
            case CJToken.KW_VAR:
            case CJToken.KW_VAL: {
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
        var mutable = at(CJToken.KW_VAR);
        if (!consume(CJToken.KW_VAL) && !consume(CJToken.KW_VAR)) {
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
        CJAstExpression expression = null;
        if (consume('=')) {
            var tryExpression = parseExpression();
            if (tryExpression.isFail()) {
                return tryExpression.castFail();
            }
            expression = tryExpression.get();
        }
        var static_ = (modifiers & CJAstItemMemberModifiers.STATIC) != 0;
        if (static_ != (expression != null)) {
            return fail("A field should have an initializer iff it is static");
        }
        return Try.ok(new CJAstFieldDefinition(mark, comment, modifiers, mutable, name, type, expression));
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
        CJAstTypeExpression returnType;
        if (consume(':')) {
            var tryReturnType = parseTypeExpression();
            if (tryReturnType.isFail()) {
                return tryReturnType.castFail();
            }
            returnType = tryReturnType.get();
        } else {
            // If a return type is not specified, assume Unit
            returnType = new CJAstTypeExpression(mark, "Unit", List.of());
        }
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

    private Try<CJAstAssignmentTarget> parseAssignmentTarget() {
        var mark = getMark();
        switch (peek().type) {
            case CJToken.ID: {
                var name = parseID();
                return Try.ok(new CJAstNameTarget(mark, name));
            }
            case '(': {
                next();
                var subtargets = List.<CJAstAssignmentTarget>of();
                while (!consume(')')) {
                    var trySubtarget = parseAssignmentTarget();
                    if (trySubtarget.isFail()) {
                        return trySubtarget;
                    }
                    subtargets.add(trySubtarget.get());
                    if (!consume(',') && !at(')')) {
                        return expectedType(')');
                    }
                }
                return Try.ok(new CJAstTupleTarget(mark, subtargets));
            }
            default:
                return expectedKind("assignment target");
        }
    }

    private Try<CJAstStatement> parseStatement() {
        var mark = getMark();
        switch (peek().type) {
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
            case CJToken.KW_FOR: {
                next();
                if (at(';') || at(CJToken.ID) && atOffset('=', 1)) {
                    // classic for loop
                    var name = Optional.<String>empty();
                    var startExpr = Optional.<CJAstExpression>empty();
                    if (at(CJToken.ID)) {
                        name = Optional.of(parseID());
                        if (!consume('=')) {
                            return expectedType('=');
                        }
                        var tryExpr = parseExpression();
                        if (tryExpr.isFail()) {
                            return tryExpr.castFail();
                        }
                        startExpr = Optional.of(tryExpr.get());
                    }
                    if (!consume(';')) {
                        return expectedType(';');
                    }
                    CJAstExpression condExpr;
                    if (!at(';')) {
                        var tryExpr = parseExpression();
                        if (tryExpr.isFail()) {
                            return tryExpr.castFail();
                        }
                        condExpr = tryExpr.get();
                    } else {
                        condExpr = new CJAstLiteralExpression(mark, CJAstLiteralExpression.BOOL, "true");
                    }
                    if (!consume(';')) {
                        return expectedType(';');
                    }
                    var incrStmt = Optional.<CJAstStatement>empty();
                    if (!at('{')) {
                        var tryStmt = parseStatement();
                        if (tryStmt.isFail()) {
                            return tryStmt;
                        }
                        var stmt = tryStmt.get();
                        if (!(stmt instanceof CJAstAssignmentStatement
                                || stmt instanceof CJAstAugmentedAssignmentStatement
                                || stmt instanceof CJAstExpressionStatement)) {
                            return failWithMark(
                                    "Only assignment, augmented assignment or expression statements are allowed here",
                                    stmt.getMark());
                        }
                        incrStmt = Optional.of(stmt);
                    }
                    var tryBody = parseBlockStatement();
                    if (tryBody.isFail()) {
                        return tryBody.castFail();
                    }
                    var body = tryBody.get();
                    var stmts = List.<CJAstStatement>of();
                    if (name.isPresent()) {
                        stmts.add(new CJAstVariableDeclarationStatement(mark, true,
                                new CJAstNameTarget(mark, name.get()), Optional.empty(), startExpr.get()));
                    }
                    if (incrStmt.isPresent()) {
                        body.getStatements().add(incrStmt.get());
                    }
                    stmts.add(new CJAstWhileStatement(mark, condExpr, body));
                    return Try.ok(new CJAstBlockStatement(mark, stmts));
                } else {
                    var tryTarget = parseAssignmentTarget();
                    if (tryTarget.isFail()) {
                        return tryTarget.castFail();
                    }
                    var target = tryTarget.get();
                    if (!consume(CJToken.KW_IN)) {
                        return expectedType(CJToken.KW_IN);
                    }
                    var tryContainerExpr = parseExpression();
                    if (tryContainerExpr.isFail()) {
                        return tryContainerExpr.castFail();
                    }
                    var containerExpr = tryContainerExpr.get();
                    var tryBody = parseBlockStatement();
                    if (tryBody.isFail()) {
                        return tryBody.castFail();
                    }
                    var body = tryBody.get();
                    return Try.ok(new CJAstForStatement(mark, target, containerExpr, body));
                }
            }
            case CJToken.KW_VAR:
            case CJToken.KW_VAL: {
                var mutable = at(CJToken.KW_VAR);
                next();
                var tryTarget = parseAssignmentTarget();
                if (tryTarget.isFail()) {
                    return tryTarget.castFail();
                }
                var target = tryTarget.get();
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
                if (!atDelimiterOrComment()) {
                    return expectedKind("statement delimiter");
                }
                return Try.ok(new CJAstVariableDeclarationStatement(mark, mutable, target, type, expr));
            }
            case CJToken.KW_RETURN: {
                next();
                var tryExpr = parseExpression();
                if (tryExpr.isFail()) {
                    return tryExpr.castFail();
                }
                if (!atDelimiterOrComment()) {
                    return expectedKind("statement delimiter");
                }
                return Try.ok(new CJAstReturnStatement(mark, tryExpr.get()));
            }
            case CJToken.KW_SWITCH: {
                next();
                var tryTarget = parseExpression();
                if (tryTarget.isFail()) {
                    return tryTarget.castFail();
                }
                var target = tryTarget.get();
                consumeDelimitersAndComments();
                if (!consume('{')) {
                    return expectedType('{');
                }
                var unionCases = List.<CJAstRawSwitchCase>of();
                var defaultBody = Optional.<CJAstBlockStatement>empty();
                consumeDelimitersAndComments();
                while (at(CJToken.KW_CASE)) {
                    var caseMark = getMark();
                    var valueExprs = List.<CJAstExpression>of();
                    while (consume(CJToken.KW_CASE)) {
                        var tryExpr = parseExpression();
                        if (tryExpr.isFail()) {
                            return tryExpr.castFail();
                        }
                        valueExprs.add(tryExpr.get());
                        consumeDelimitersAndComments();
                    }
                    var tryBody = parseBlockStatement();
                    if (tryBody.isFail()) {
                        return tryBody.castFail();
                    }
                    var body = tryBody.get();
                    consumeDelimitersAndComments();
                    unionCases.add(new CJAstRawSwitchCase(caseMark, valueExprs, body));
                    consumeDelimitersAndComments();
                }
                if (consume(CJToken.KW_DEFAULT)) {
                    var tryBody = parseBlockStatement();
                    if (tryBody.isFail()) {
                        return tryBody.castFail();
                    }
                    var body = tryBody.get();
                    defaultBody = Optional.of(body);
                    consumeDelimitersAndComments();
                }
                if (!consume('}')) {
                    return expectedType('}');
                }
                return Try.ok(new CJAstRawSwitchStatement(mark, target, unionCases, defaultBody));
            }
            case CJToken.KW_UNION: {
                next();
                var tryTarget = parseExpression();
                if (tryTarget.isFail()) {
                    return tryTarget.castFail();
                }
                var target = tryTarget.get();
                consumeDelimitersAndComments();
                if (!consume('{')) {
                    return expectedType('{');
                }
                var unionCases = List.<CJAstUnionSwitchCase>of();
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
                    var body = tryBody.get();
                    unionCases.add(new CJAstUnionSwitchCase(caseMark, name, valueNames, body));
                    consumeDelimitersAndComments();
                }
                if (consume(CJToken.KW_DEFAULT)) {
                    var tryBody = parseBlockStatement();
                    if (tryBody.isFail()) {
                        return tryBody.castFail();
                    }
                    var body = tryBody.get();
                    defaultBody = Optional.of(body);
                    consumeDelimitersAndComments();
                }
                if (!consume('}')) {
                    return expectedType('}');
                }
                return Try.ok(new CJAstUnionSwitchStatement(mark, target, unionCases, defaultBody));
            }
            default: {
                var tryExpr = parseExpression();
                if (tryExpr.isFail()) {
                    return tryExpr.castFail();
                }

                switch (peek().type) {
                    case '=': {
                        next();
                        var tryTarget = expressionToExtendedAssignmentTarget(tryExpr.get());
                        if (tryTarget.isFail()) {
                            return tryTarget.castFail();
                        }
                        var target = tryTarget.get();
                        var tryValExpr = parseExpression();
                        if (tryValExpr.isFail()) {
                            return tryValExpr.castFail();
                        }
                        var valExpr = tryValExpr.get();
                        return Try.ok(new CJAstAssignmentStatement(mark, target, valExpr));
                    }
                    case CJToken.PLUS_EQ:
                    case CJToken.MINUS_EQ:
                    case CJToken.STAR_EQ: {
                        String type;
                        int toktype = next().type;
                        switch (toktype) {
                            case CJToken.PLUS_EQ:
                                type = "+=";
                                break;
                            case CJToken.MINUS_EQ:
                                type = "-=";
                                break;
                            case CJToken.STAR_EQ:
                                type = "*=";
                                break;
                            default:
                                throw XError
                                        .withMessage("Unrecognized aug assign token: " + CJToken.typeToString(toktype));
                        }
                        var owner = Optional.<CJAstExpression>empty();
                        var typeOwner = Optional.<CJAstTypeExpression>empty();
                        String name;
                        var target = tryExpr.get();
                        if (target instanceof CJAstNameExpression) {
                            name = ((CJAstNameExpression) target).getName();
                        } else if (target instanceof CJAstFieldAccessExpression) {
                            owner = Optional.of(((CJAstFieldAccessExpression) target).getOwner());
                            name = ((CJAstFieldAccessExpression) target).getName();
                        } else if (target instanceof CJAstStaticFieldAccessExpression) {
                            typeOwner = Optional.of(((CJAstStaticFieldAccessExpression) target).getOwner());
                            name = ((CJAstStaticFieldAccessExpression) target).getName();
                        } else {
                            return failWithMark(
                                    "Only variable or field or static fields can be used with augmented assignment",
                                    mark);
                        }
                        var tryValExpr = parseExpression();
                        if (tryValExpr.isFail()) {
                            return tryValExpr.castFail();
                        }
                        var valExpr = tryValExpr.get();
                        return Try
                                .ok(new CJAstAugmentedAssignmentStatement(mark, owner, typeOwner, name, type, valExpr));
                    }
                }

                if (consume('=')) {
                    var tryTarget = expressionToExtendedAssignmentTarget(tryExpr.get());
                    if (tryTarget.isFail()) {
                        return tryTarget.castFail();
                    }
                    var target = tryTarget.get();
                    var tryValExpr = parseExpression();
                    if (tryValExpr.isFail()) {
                        return tryValExpr.castFail();
                    }
                    var valExpr = tryValExpr.get();
                    return Try.ok(new CJAstAssignmentStatement(mark, target, valExpr));
                } else {
                    if (!atDelimiterOrComment()) {
                        return expectedKind("statement delimiter");
                    }
                    return Try.ok(new CJAstExpressionStatement(mark, tryExpr.get()));
                }
            }
        }
    }

    private Try<CJAstExtendedAssignmentTarget> expressionToExtendedAssignmentTarget(CJAstExpression expression) {
        var mark = expression.getMark();
        if (expression instanceof CJAstNameExpression) {
            var name = ((CJAstNameExpression) expression).getName();
            return Try.ok(new CJAstNameTarget(mark, name));
        } else if (expression instanceof CJAstTupleDisplayExpression) {
            var elements = ((CJAstTupleDisplayExpression) expression).getElements();
            var subtargets = List.<CJAstAssignmentTarget>of();
            for (var element : elements) {
                var trySubtarget = expressionToExtendedAssignmentTarget(element);
                if (trySubtarget.isFail()) {
                    return trySubtarget;
                }
                var subtarget = trySubtarget.get();
                if (!(subtarget instanceof CJAstAssignmentTarget)) {
                    return failWithMark("Field access not allowed here", element.getMark());
                }
                subtargets.add((CJAstAssignmentTarget) subtarget);
            }
            return Try.ok(new CJAstTupleTarget(mark, subtargets));
        } else if (expression instanceof CJAstFieldAccessExpression) {
            var owner = ((CJAstFieldAccessExpression) expression).getOwner();
            var name = ((CJAstFieldAccessExpression) expression).getName();
            return Try.ok(new CJAstFieldAccessTarget(mark, owner, name));
        } else if (expression instanceof CJAstStaticFieldAccessExpression) {
            var owner = ((CJAstStaticFieldAccessExpression) expression).getOwner();
            var name = ((CJAstStaticFieldAccessExpression) expression).getName();
            return Try.ok(new CJAstStaticFieldTarget(mark, owner, name));
        }
        return failWithMark("Expected assignment target", mark);
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
                    var tryOther = at('{') ? parseBlockStatement() : parseStatement();
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

    private Try<CJAstCompoundExpression> parseCompoundExpression() {
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
        Optional<CJAstExpression> expression = Optional.empty();
        if (list.size() > 0 && list.last() instanceof CJAstExpressionStatement) {
            expression = Optional.of(((CJAstExpressionStatement) list.pop()).getExpression());
        }
        return Try.ok(new CJAstCompoundExpression(mark, list, expression));
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
        var mutable = consume(CJToken.KW_VAR);
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
        return Try.ok(new CJAstParameter(mark, mutable, name, tryTrait.get()));
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

    // binds just a little tighter than multiplicative operators
    private static final int DEFAULT_UNARY_OP_BINDING_POWER = getTokenPrecedence('*') + 5;

    private static int getTokenPrecedence(int tokenType) {
        // mostly follows Python, except uses Rust style '?'
        switch (tokenType) {
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
            case '^':
                return 80;
            case '&':
                return 90;
            case CJToken.LSHIFT:
            case CJToken.RSHIFT:
            case CJToken.RSHIFTU:
                return 100;
            case '+':
            case '-':
                return 110;
            case '*':
            case '/':
            case '%':
            case CJToken.TRUNCDIV:
                return 120;
            case CJToken.POWER:
                return 130;
            case '.':
            case '?':
                return 140;
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
                case CJToken.KW_OR:
                case CJToken.KW_AND: {
                    int type = next().type == CJToken.KW_OR ? CJAstLogicalBinaryExpression.OR
                            : CJAstLogicalBinaryExpression.AND;
                    var tryRight = parseExpressionWithPrecedence(precedence + 1);
                    if (tryRight.isFail()) {
                        return tryRight;
                    }
                    var right = tryRight.get();
                    tryExpr = Try.ok(new CJAstLogicalBinaryExpression(mark, type, tryExpr.get(), right));
                    break;
                }
                case '+':
                case '-':
                case '*':
                case '/':
                case '%':
                case '<':
                case '>':
                case '|':
                case '^':
                case '&':
                case CJToken.LSHIFT:
                case CJToken.RSHIFT:
                case CJToken.RSHIFTU:
                case CJToken.POWER:
                case CJToken.TRUNCDIV:
                case CJToken.EQ:
                case CJToken.NE:
                case CJToken.LE:
                case CJToken.GE:
                case CJToken.KW_IN:
                case CJToken.KW_NOT: {
                    String methodName = null;
                    boolean logicalNot = false;
                    boolean rightAssociative = false;
                    boolean swap = false;
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
                            methodName = "__rem";
                            break;
                        case '<':
                            methodName = "__lt";
                            break;
                        case '>':
                            methodName = "__gt";
                            break;
                        case '|':
                            methodName = "__or";
                            break;
                        case '^':
                            methodName = "__xor";
                            break;
                        case '&':
                            methodName = "__and";
                            break;
                        case CJToken.LSHIFT:
                            methodName = "__lshift";
                            break;
                        case CJToken.RSHIFT:
                            methodName = "__rshift";
                            break;
                        case CJToken.RSHIFTU:
                            methodName = "__rshiftu";
                            break;
                        case CJToken.POWER:
                            methodName = "__pow";
                            rightAssociative = true;
                            break;
                        case CJToken.TRUNCDIV:
                            methodName = "__truncdiv";
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
                            swap = true;
                            break;
                        case CJToken.KW_NOT:
                            if (!consume(CJToken.KW_IN)) {
                                return expectedType(CJToken.KW_IN);
                            }
                            methodName = "__contains";
                            logicalNot = true;
                            swap = true;
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
                    if (swap) {
                        var tmp = rhs;
                        rhs = lhs;
                        lhs = tmp;
                    }
                    tryExpr = Try.ok(new CJAstInstanceMethodCallExpression(mark, methodName, List.of(lhs, rhs)));
                    if (logicalNot) {
                        tryExpr = Try.ok(new CJAstLogicalNotExpression(mark, tryExpr.get()));
                    }
                    break;
                }
                case '.': {
                    next();
                    if (consume(CJToken.KW_AWAIT)) {
                        tryExpr = Try.ok(new CJAstAwaitExpression(mark, tryExpr.get()));
                    } else {
                        if (!at(CJToken.ID)) {
                            return expectedType(CJToken.ID);
                        }
                        var name = parseID();
                        if (at('(')) {
                            var tryArgs = parseArguments();
                            if (tryArgs.isFail()) {
                                return tryArgs.castFail();
                            }
                            var otherArgs = tryArgs.get();
                            var arg = tryExpr.get();
                            var args = List.of(List.of(arg), otherArgs).flatMap(x -> x);
                            tryExpr = Try.ok(new CJAstInstanceMethodCallExpression(mark, name, args));
                        } else {
                            tryExpr = Try.ok(new CJAstFieldAccessExpression(mark, tryExpr.get(), name));
                        }
                    }
                    break;
                }
                case '?': {
                    next();
                    tryExpr = Try.ok(new CJAstErrorPropagationExpression(mark, tryExpr.get()));
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
            case CJToken.ID: { // local variable name or lambda expression
                if (atLambda()) {
                    return parseLambdaExpression().map(x -> x);
                } else {
                    var name = parseID();
                    return Try.ok(new CJAstNameExpression(mark, name));
                }
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
                    consumeDelimitersAndComments();
                    if (consume(':')) {
                        if (!consume(']')) {
                            return expectedType(']');
                        }
                        return Try.ok(new CJAstStaticMethodCallExpression(mark,
                                new CJAstTypeExpression(mark, "MutableMap", List.of()), "empty", List.of()));
                    } else if (at(CJToken.TYPE_ID)) {
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
                    } else {
                        var elements = List.<CJAstExpression>of();
                        if (consume(']')) {
                            return Try.ok(new CJAstListDisplayExpression(mark, true, elements));
                        }
                        var tryFirst = parseExpression();
                        if (tryFirst.isFail()) {
                            return tryFirst.castFail();
                        }
                        var first = tryFirst.get();
                        if (consume(':')) {
                            var trySecond = parseExpression();
                            if (trySecond.isFail()) {
                                return trySecond.castFail();
                            }
                            elements.add(new CJAstTupleDisplayExpression(mark, List.of(first, trySecond.get())));
                            if (consume(',')) {
                                consumeDelimitersAndComments();
                                while (!at(']')) {
                                    var tryElement = parseExpression();
                                    if (tryElement.isFail()) {
                                        return tryElement.castFail();
                                    }
                                    var key = tryElement.get();
                                    if (!consume(':')) {
                                        return expectedType(':');
                                    }
                                    tryElement = parseExpression();
                                    if (tryElement.isFail()) {
                                        return tryElement.castFail();
                                    }
                                    var val = tryElement.get();
                                    elements.add(new CJAstTupleDisplayExpression(mark, List.of(key, val)));
                                    if (!consume(',') && !at(']')) {
                                        return expectedType(']');
                                    }
                                    consumeDelimitersAndComments();
                                }
                            }
                            if (!consume(']')) {
                                return expectedType(']');
                            }
                            return Try.ok(new CJAstStaticMethodCallExpression(mark,
                                    new CJAstTypeExpression(mark, "MutableMap", List.of()), "of",
                                    List.of(new CJAstListDisplayExpression(mark, false, elements))));
                        } else {
                            elements.add(first);
                            if (consume(',')) {
                                while (!at(']')) {
                                    var tryElement = parseExpression();
                                    if (tryElement.isFail()) {
                                        return tryElement.castFail();
                                    }
                                    elements.add(tryElement.get());
                                    if (!consume(',') && !at(']')) {
                                        return expectedType(']');
                                    }
                                }
                            }
                            if (!consume(']')) {
                                return expectedType(']');
                            }
                            return Try.ok(new CJAstListDisplayExpression(mark, true, elements));
                        }
                    }
                } else {
                    return expectedType('[');
                }
            }
            case '{': { // block expression
                return parseCompoundExpression().map(x -> x);
            }
            case '[': { // list and map displays
                next();
                consumeDelimitersAndComments();
                if (consume(':')) {
                    if (!consume(']')) {
                        return expectedType(']');
                    }
                    return Try.ok(new CJAstStaticMethodCallExpression(mark,
                            new CJAstTypeExpression(mark, "Map", List.of()), "empty", List.of()));
                } else {
                    var elements = List.<CJAstExpression>of();
                    if (consume(']')) {
                        return Try.ok(new CJAstListDisplayExpression(mark, false, elements));
                    }
                    var tryFirst = parseExpression();
                    if (tryFirst.isFail()) {
                        return tryFirst.castFail();
                    }
                    var first = tryFirst.get();
                    if (consume(':')) {
                        var trySecond = parseExpression();
                        if (trySecond.isFail()) {
                            return trySecond.castFail();
                        }
                        elements.add(new CJAstTupleDisplayExpression(mark, List.of(first, trySecond.get())));
                        if (consume(',')) {
                            consumeDelimitersAndComments();
                            while (!at(']')) {
                                var tryElement = parseExpression();
                                if (tryElement.isFail()) {
                                    return tryElement.castFail();
                                }
                                var key = tryElement.get();
                                if (!consume(':')) {
                                    return expectedType(':');
                                }
                                tryElement = parseExpression();
                                if (tryElement.isFail()) {
                                    return tryElement.castFail();
                                }
                                var val = tryElement.get();
                                elements.add(new CJAstTupleDisplayExpression(mark, List.of(key, val)));
                                if (!consume(',') && !at(']')) {
                                    return expectedType(']');
                                }
                                consumeDelimitersAndComments();
                            }
                        }
                        if (!consume(']')) {
                            return expectedType(']');
                        }
                        return Try.ok(new CJAstStaticMethodCallExpression(mark,
                                new CJAstTypeExpression(mark, "Map", List.of()), "of",
                                List.of(new CJAstListDisplayExpression(mark, false, elements))));
                    } else {
                        elements.add(first);
                        if (consume(',')) {
                            consumeDelimitersAndComments();
                            while (!at(']')) {
                                var tryElement = parseExpression();
                                if (tryElement.isFail()) {
                                    return tryElement.castFail();
                                }
                                elements.add(tryElement.get());
                                if (!consume(',') && !at(']')) {
                                    return expectedType(']');
                                }
                                consumeDelimitersAndComments();
                            }
                        }
                        if (!consume(']')) {
                            return expectedType(']');
                        }
                        return Try.ok(new CJAstListDisplayExpression(mark, false, elements));
                    }
                }
            }
            case CJToken.KW_SWITCH: { // raw match expression
                next();
                var tryTarget = parseExpression();
                if (tryTarget.isFail()) {
                    return tryTarget.castFail();
                }
                var target = tryTarget.get();
                consumeDelimitersAndComments();
                if (!consume('{')) {
                    return expectedType('{');
                }
                var unionCases = List.<CJAstRawMatchCase>of();
                var defaultBody = Optional.<CJAstExpression>empty();
                consumeDelimitersAndComments();
                while (at(CJToken.KW_CASE)) {
                    var caseMark = getMark();
                    var valueExprs = List.<CJAstExpression>of();
                    while (consume(CJToken.KW_CASE)) {
                        var tryExpr = parseExpression();
                        if (tryExpr.isFail()) {
                            return tryExpr.castFail();
                        }
                        valueExprs.add(tryExpr.get());
                        consumeDelimitersAndComments();
                    }
                    if (!consume('=')) {
                        return expectedType('=');
                    }
                    var tryBody = parseExpression();
                    if (tryBody.isFail()) {
                        return tryBody.castFail();
                    }
                    var body = tryBody.get();
                    consumeDelimitersAndComments();
                    unionCases.add(new CJAstRawMatchCase(caseMark, valueExprs, body));
                    consumeDelimitersAndComments();
                }
                if (consume(CJToken.KW_DEFAULT)) {
                    if (!consume('=')) {
                        return expectedType('=');
                    }
                    var tryBody = parseExpression();
                    if (tryBody.isFail()) {
                        return tryBody.castFail();
                    }
                    var body = tryBody.get();
                    defaultBody = Optional.of(body);
                    consumeDelimitersAndComments();
                }
                if (!consume('}')) {
                    return expectedType('}');
                }
                return Try.ok(new CJAstRawMatchExpression(mark, target, unionCases, defaultBody));
            }
            case CJToken.KW_UNION: { // union match expression
                next();
                var tryTarget = parseExpression();
                if (tryTarget.isFail()) {
                    return tryTarget.castFail();
                }
                var target = tryTarget.get();
                consumeDelimitersAndComments();
                if (!consume('{')) {
                    return expectedType('{');
                }
                var unionCases = List.<CJAstUnionMatchCase>of();
                var defaultBody = Optional.<CJAstExpression>empty();
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
                    if (!consume('=')) {
                        return expectedType('=');
                    }
                    var tryBody = parseExpression();
                    if (tryBody.isFail()) {
                        return tryBody.castFail();
                    }
                    var body = tryBody.get();
                    unionCases.add(new CJAstUnionMatchCase(caseMark, name, valueNames, body));
                    consumeDelimitersAndComments();
                }
                if (consume(CJToken.KW_DEFAULT)) {
                    if (!consume('=')) {
                        return expectedType('=');
                    }
                    var tryBody = parseExpression();
                    if (tryBody.isFail()) {
                        return tryBody.castFail();
                    }
                    var body = tryBody.get();
                    defaultBody = Optional.of(body);
                    consumeDelimitersAndComments();
                }
                if (!consume('}')) {
                    return expectedType('}');
                }
                return Try.ok(new CJAstUnionMatchExpression(mark, target, unionCases, defaultBody));
            }
            case CJToken.KW_IF: { // if expression
                next();
                if (!consume('(')) {
                    return expectedType('(');
                }
                var tryCondition = parseExpression();
                if (tryCondition.isFail()) {
                    return tryCondition.castFail();
                }
                var condition = tryCondition.get();
                if (!consume(')')) {
                    return expectedType(')');
                }
                var tryLeft = parseExpression();
                if (tryLeft.isFail()) {
                    return tryLeft.castFail();
                }
                var left = tryLeft.get();
                if (!consume(CJToken.KW_ELSE)) {
                    return expectedType(CJToken.KW_ELSE);
                }
                var tryRight = parseExpression();
                if (tryRight.isFail()) {
                    return tryRight.castFail();
                }
                var right = tryRight.get();
                return Try.ok(new CJAstConditionalExpression(mark, condition, left, right));
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
                    List<CJAstExpression> args;
                    if (at('(')) {
                        var tryArgs = parseArguments();
                        if (tryArgs.isFail()) {
                            return tryArgs.castFail();
                        }
                        args = tryArgs.get();
                    } else {
                        args = List.of();
                    }
                    return Try.ok(new CJAstNewUnionExpression(mark, type, name, args));
                } else { // method call and field access expressions
                    if (!at(CJToken.ID)) {
                        return expectedType(CJToken.ID);
                    }
                    var name = parseID();

                    if (at('[') || at('(')) {
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
                            return Try.ok(new CJAstStaticMethodCallExpression(mark, type, name, args));
                        } else {
                            return Try.ok(new CJAstMethodCallExpression(mark, type, name, typeArgs, args));
                        }
                    } else {
                        return Try.ok(new CJAstStaticFieldAccessExpression(mark, type, name));
                    }
                }
            }
            case '+':
            case '-':
            case '~': { // unary operators
                String methodName;
                int optype = next().type;
                switch (optype) {
                    case '+':
                        methodName = "__pos";
                        break;
                    case '-':
                        methodName = "__neg";
                        break;
                    case '~':
                        methodName = "__invert";
                        break;
                    default:
                        throw XError.withMessage("Unsupported unary optype: " + CJToken.typeToString(optype));
                }
                var tryExpr = parseExpressionWithPrecedence(DEFAULT_UNARY_OP_BINDING_POWER);
                if (tryExpr.isFail()) {
                    return tryExpr;
                }
                return Try.ok(new CJAstInstanceMethodCallExpression(mark, methodName, List.of(tryExpr.get())));
            }
            case '(': { // parenthetical, lambda, unit or tuple display expression
                if (atLambda()) {
                    return parseLambdaExpression().map(x -> x);
                } else if (atOffset(')', 1)) {
                    next();
                    next();
                    return Try.ok(new CJAstLiteralExpression(mark, CJAstLiteralExpression.UNIT, "()"));
                } else {
                    next();
                    var tryExpr = parseExpression();
                    if (tryExpr.isFail()) {
                        return tryExpr.castFail();
                    }
                    if (consume(',')) {
                        // tuple display
                        var elements = List.of(tryExpr.get());
                        while (!consume(')')) {
                            var tryElement = parseExpression();
                            if (tryElement.isFail()) {
                                return tryElement.castFail();
                            }
                            elements.add(tryElement.get());
                            if (!consume(',') && !at(')')) {
                                return expectedType(')');
                            }
                        }
                        return Try.ok(new CJAstTupleDisplayExpression(mark, elements));
                    } else {
                        // parenthetical expression
                        if (!consume(')')) {
                            return expectedType(')');
                        }
                        return tryExpr;
                    }
                }
            }
            default:
                return expectedKind("expression");
        }
    }

    /**
     * Look ahead a few tokens to see if we're currently at a lambda expression
     */
    private boolean atLambda() {
        if (at(CJToken.ID) && atOffset(CJToken.RIGHT_ARROW, 1)) {
            return true;
        }
        if (!at('(')) {
            return false;
        }

        int savedI = i;

        next(); // skip the first '('
        while (consume(CJToken.ID) || consume(',')) {
        }
        boolean matched = consume(')') && consume(CJToken.RIGHT_ARROW);

        i = savedI;
        return matched;
    }

    private Try<CJAstLambdaExpression> parseLambdaExpression() {
        var mark = getMark();
        var parameterNames = List.<String>of();
        if (at(CJToken.ID)) {
            parameterNames.add(parseID());
        } else {
            if (!consume('(')) {
                return expectedType('(');
            }
            while (!consume(')')) {
                if (!at(CJToken.ID)) {
                    return expectedType(CJToken.ID);
                }
                parameterNames.add(parseID());
                if (!consume(',') && !at(')')) {
                    return expectedType(')');
                }
            }
        }
        if (!consume(CJToken.RIGHT_ARROW)) {
            return expectedType(CJToken.RIGHT_ARROW);
        }
        var tryBody = at('{') ? parseBlockStatement()
                : parseExpression().map(e -> new CJAstReturnStatement(e.getMark(), e));
        var body = tryBody.get();
        return Try.ok(new CJAstLambdaExpression(mark, parameterNames, body));
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
