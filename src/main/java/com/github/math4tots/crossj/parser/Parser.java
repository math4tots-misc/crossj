package com.github.math4tots.crossj.parser;

import com.github.math4tots.crossj.ast.BlockStatement;
import com.github.math4tots.crossj.ast.ClassOrInterfaceDeclaration;
import com.github.math4tots.crossj.ast.DoubleLiteralExpression;
import com.github.math4tots.crossj.ast.Expression;
import com.github.math4tots.crossj.ast.ExpressionStatement;
import com.github.math4tots.crossj.ast.FieldAccessExpression;
import com.github.math4tots.crossj.ast.FieldDeclaration;
import com.github.math4tots.crossj.ast.InstanceOfExpression;
import com.github.math4tots.crossj.ast.IntegerLiteralExpression;
import com.github.math4tots.crossj.ast.MemberDeclaration;
import com.github.math4tots.crossj.ast.MethodCallExpression;
import com.github.math4tots.crossj.ast.MethodDeclaration;
import com.github.math4tots.crossj.ast.NameExpression;
import com.github.math4tots.crossj.ast.OperationExpression;
import com.github.math4tots.crossj.ast.ReturnStatement;
import com.github.math4tots.crossj.ast.Statement;
import com.github.math4tots.crossj.ast.StringLiteralExpression;
import com.github.math4tots.crossj.ast.TypeCastExpression;
import com.github.math4tots.crossj.ast.TypeExpression;
import com.github.math4tots.crossj.ast.TypeParameterDeclaration;
import com.github.math4tots.crossj.ast.VariableDeclaration;
import com.github.math4tots.crossj.ast.World;

import crossj.*;

public final class Parser {

    public static void parse(World world, Source source) {
        new Parser(world, source).parseFile();
    }

    private final World world;
    private final List<Token> tokens;
    private int i = 0;

    private Parser(World world, Source source) {
        this.world = world;
        this.tokens = Lexer.lex(source);
    }

    private Token peek() {
        return tokens.get(i);
    }

    private Mark getMark() {
        return peek().getMark();
    }

    private boolean at(String type) {
        return peek().getType().equals(type);
    }

    private boolean consume(String type) {
        if (at(type)) {
            next();
            return true;
        }
        return false;
    }

    private Token next() {
        Token token = peek();
        i++;
        return token;
    }

    private Token expect(String type) {
        if (at(type)) {
            return next();
        }
        throw err("Expected " + type + " but got " + peek().getType());
    }

    private XError err(String message) {
        throw XError.withMessage(getMark().format() + message);
    }

    private String parseName() {
        return expect("name").getString();
    }

    private List<String> parseModifiers() {
        List<String> modifiers = List.of();
        while (true) {
            boolean matched = false;
            switch (peek().getType()) {
                case "public":
                case "private":
                case "protected":
                case "static":
                case "default":
                case "final":
                case "abstract":
                    matched = true;
                    modifiers.add(next().getType());
            }
            if (!matched) {
                break;
            }
        }
        return modifiers;
    }

    private String parseQualifiedName() {
        StringBuilder sb = new StringBuilder();
        sb.append(parseName());
        while (consume(".")) {
            sb.append(".");
            // check for wildcard imports
            if (consume("*")) {
                sb.append("*");
                break;
            } else {
                sb.append(parseName());
            }
        }
        return sb.toString();
    }

    private String parsePackageDeclaration() {
        expect("package");
        String name = parseQualifiedName();
        expect(";");
        return name;
    }

    private List<String> parseImports() {
        List<String> imports = List.of();
        while (at("import")) {
            expect("import");
            imports.add(parseQualifiedName());
            expect(";");
        }
        return imports;
    }

    private TypeExpression parseTypeExpression() {
        Mark mark = getMark();
        switch (peek().getType()) {
            case "void":
            case "char":
            case "int":
            case "double":
                return new TypeExpression(mark, next().getType(), null);
        }
        String name = parseQualifiedName();
        List<TypeExpression> arguments = parseTypeArguments();
        TypeExpression expression = new TypeExpression(mark, name, arguments);
        Mark arrayMark = getMark();
        while (consume("[")) {
            expect("]");
            expression = new TypeExpression(arrayMark, "[]", List.of(expression));
            arrayMark = getMark();
        }
        return expression;
    }

    private boolean atCastExpression() {
        // this is a real dirty hack to parse type cast expressions
        int save = i;
        try {
            // Check for the (<type>) pattern
            // If we do encounter it, we just assume we are at a cast expression.
            return consume("(") && skipCastTypeExpression() && consume(")");
        } finally {
            i = save;
        }
    }

    private boolean skipCastTypeExpression() {
        // primitive type
        switch (peek().getType()) {
            case "void":
            case "char":
            case "int":
            case "double":
                next();
                return true;
        }
        // reference type
        if (at("name")) {
            String last = parseName();
            while (consume(".")) {
                if (!at("name")) {
                    return false;
                }
                last = parseName();
            }
            // we cheat a little bit here and check the name's case
            if (!Character.isUpperCase(last.charAt(0))) {
                return false;
            }
            // we may potentially encounter generic arguments.
            // or they might be less-than greater-than operators.
            if (consume("<")) {
                int depth = 1;
                while (depth > 0 && (at("name") || at("<") || at(">") || at(".") || at("?"))) {
                    if (at("<")) {
                        depth++;
                    } else if (at(">")) {
                        depth--;
                    }
                    next();
                }
                // if while traversing over specially permitted token types, we were able to
                // balance the '<>', chances seem pretty good that this is actually a type.
                return depth == 0;
            } else {
                return true;
            }
        }
        return false;
    }

    private List<TypeExpression> parseTypeArguments() {
        if (!consume("<")) {
            return null;
        }
        List<TypeExpression> args = List.of();
        while (!consume(">")) {
            args.add(parseTypeExpression());
            if (!consume(",")) {
                expect(">");
                break;
            }
        }
        return args;
    }

    private TypeParameterDeclaration parseTypeParameter() {
        Mark mark = getMark();
        String name = parseName();
        TypeExpression bound = null;
        if (consume("extends")) {
            bound = parseTypeExpression();
        }
        return new TypeParameterDeclaration(mark, name, bound);
    }

    private List<TypeParameterDeclaration> parseTypeParameters() {
        if (!consume("<")) {
            return null;
        }
        List<TypeParameterDeclaration> parameters = List.of();
        while (!consume(">")) {
            parameters.add(parseTypeParameter());
            if (!consume(",")) {
                expect(">");
                break;
            }
        }
        return parameters;
    }

    private void parseFile() {
        Mark mark = getMark();
        String packageName = parsePackageDeclaration();
        List<String> imports = parseImports();
        List<String> modifiers = parseModifiers();
        boolean isInterface = consume("interface");
        if (!isInterface) {
            expect("class");
        }
        String name = parseName();
        List<TypeParameterDeclaration> typeParameters = parseTypeParameters();
        List<TypeExpression> interfaces = List.of();
        if (isInterface && consume("extends") || !isInterface && consume("implements")) {
            interfaces.add(parseTypeExpression());
            while (consume(",")) {
                interfaces.add(parseTypeExpression());
            }
        }
        expect("{");
        List<MemberDeclaration> members = List.of();
        while (!consume("}")) {
            members.add(parseMember());
        }
        expect("EOF");
        new ClassOrInterfaceDeclaration(world, mark, packageName, imports, modifiers, isInterface, name, typeParameters,
                interfaces, members);
    }

    private MemberDeclaration parseMember() {
        Mark mark = getMark();
        List<String> modifiers = parseModifiers();
        List<TypeParameterDeclaration> typeParameters = parseTypeParameters();
        TypeExpression type = parseTypeExpression();
        String name = parseName();
        if (typeParameters != null || at("(")) {
            // method
            Pair<List<VariableDeclaration>, Boolean> pair = parseParameters();
            List<VariableDeclaration> parameters = pair.get1();
            boolean isVariadic = pair.get2();
            BlockStatement body = null;
            if (!consume(";")) {
                body = parseBlock();
            }
            return new MethodDeclaration(mark, modifiers, type, name, typeParameters, parameters, isVariadic, body);
        } else {
            // field
            Expression initializer = null;
            if (consume("=")) {
                initializer = parseExpression();
            }
            expect(";");
            return new FieldDeclaration(mark, modifiers, type, name, initializer);
        }
    }

    private Pair<List<VariableDeclaration>, Boolean> parseParameters() {
        expect("(");
        List<VariableDeclaration> list = List.of();
        boolean isVariadic = false;
        while (!consume(")")) {
            Mark mark = getMark();
            TypeExpression type = parseTypeExpression();
            if (consume("...")) {
                // variadic parameter (it needs to be the last one)
                String name = parseName();
                isVariadic = true;
                type = new TypeExpression(mark, "[]", List.of(type));
                list.add(new VariableDeclaration(mark, type, name, null));
                expect(")");
                break;
            } else {
                String name = parseName();
                list.add(new VariableDeclaration(mark, type, name, null));
                if (!consume(",")) {
                    expect(")");
                    break;
                }
            }
        }
        return Pair.of(list, isVariadic);
    }

    private Statement parseStatement() {
        Mark mark = getMark();
        if (at("{")) {
            return parseBlock();
        }
        if (consume("return")) {
            Expression expression = parseExpression();
            expect(";");
            return new ReturnStatement(mark, expression);
        }
        {
            Expression expression = parseExpression();
            expect(";");
            return new ExpressionStatement(mark, expression);
        }
    }

    private BlockStatement parseBlock() {
        Mark mark = getMark();
        List<Statement> statements = List.of();
        expect("{");
        while (!consume("}")) {
            statements.add(parseStatement());
        }
        return new BlockStatement(mark, statements);
    }

    private Expression parseExpression() {
        return parseInfix(0);
    }

    // precedence map for expression parsing
    private static Map<String, Integer> PRECMAP = Map.of(
        Pair.of("*", 12),
        Pair.of("/", 12),
        Pair.of("%", 12),
        Pair.of("+", 11),
        Pair.of("-", 11),
        Pair.of("<<", 10),
        Pair.of(">>", 10),
        Pair.of(">>>", 10),
        Pair.of("<", 9),
        Pair.of("<=", 9),
        Pair.of(">", 9),
        Pair.of(">=", 9),
        Pair.of("instanceof", 9),
        Pair.of("==", 8),
        Pair.of("!=", 8),
        Pair.of("&", 7),
        Pair.of("^", 6),
        Pair.of("|", 5),
        Pair.of("&&", 4),
        Pair.of("||", 3),
        Pair.of("?", 2),
        Pair.of("+=", 1),
        Pair.of("-=", 1),
        Pair.of("*=", 1),
        Pair.of("/=", 1),
        Pair.of("%=", 1),
        Pair.of("=", 1)
    );

    // parses an expression whose precendece is > given bound.
    private Expression parseInfix(int precedenceBound) {
        Expression expression = parseUnary();
        while (true) {
            if (!PRECMAP.containsKey(peek().getType())) {
                return expression;
            }
            int foundPrecedence = PRECMAP.get(peek().getType());
            if (foundPrecedence <= precedenceBound) {
                return expression;
            }
            Mark mark = getMark();
            switch (peek().getType()) {
                case "*":
                case "/":
                case "%":
                case "+":
                case "-":
                case "<<":
                case ">>":
                case ">>>":
                case "<":
                case "<=":
                case ">":
                case ">=":
                case "==":
                case "!=":
                case "&":
                case "^":
                case "|":
                case "&&":
                case "||": {
                    // typical left associative operators
                    String operator = "a" + next().getType() + "b";
                    Expression rhs = parseInfix(foundPrecedence);
                    expression = new OperationExpression(mark, operator, List.of(expression, rhs));
                    break;
                }
                case "+=":
                case "-=":
                case "*=":
                case "/=":
                case "%=":
                case "=": {
                    // right associative
                    String operator = "a" + next().getType() + "b";
                    Expression rhs = parseInfix(foundPrecedence - 1);
                    expression = new OperationExpression(mark, operator, List.of(expression, rhs));
                    break;
                }
                case "?": {
                    // ternary operator
                    String operator = "a?b:c";
                    next();
                    Expression condition = parseExpression();
                    expect(":");
                    Expression rhs = parseInfix(foundPrecedence - 1);
                    expression = new OperationExpression(mark, operator, List.of(expression, condition, rhs));
                    break;
                }
                case "instanceof": {
                    next();
                    TypeExpression type = parseTypeExpression();
                    expression = new InstanceOfExpression(mark, expression, type);
                    break;
                }
                default: {
                    throw err("Operator unaccounted for: " + peek().getType());
                }
            }
        }
    }

    private Expression parseUnary() {
        Mark mark = getMark();
        switch (peek().getType()) {
            case "(": {
                if (atCastExpression()) {
                    expect("(");
                    TypeExpression type = parseTypeExpression();
                    expect(")");
                    Expression expression = parsePostfix();
                    return new TypeCastExpression(mark, type, expression);
                }
                break;
            }
            case "++":
            case "--":
            case "+":
            case "-":
            case "!":
            case "~": {
                String operator = next().getType() + "a";
                return new OperationExpression(mark, operator, List.of(parsePostfix()));
            }
        }
        return parsePostfix();
    }

    private Expression parsePostfix() {
        Expression expression = parseAtom();
        while (true) {
            Mark mark = getMark();
            if (consume(".")) {
                String name = parseName();
                if (at("(")) {
                    List<Expression> arguments = parseArguments();
                    expression = new MethodCallExpression(mark, expression, null, name, arguments);
                } else {
                    expression = new FieldAccessExpression(mark, expression, name);
                }
                continue;
            }
            if (at("++") || at("--")) {
                // postfix increment/decrement operators
                String operator = "a" + next().getType();
                expression = new OperationExpression(mark, operator, List.of(expression));
                continue;
            }
            // Skip '[]' operator; I don't really intend on supporting array types (much).
            // Just use crossj.List as much as possible. Array types still exists in crossj
            // to support variadic arguments.
            break;
        }
        return expression;
    }

    private List<Expression> parseArguments() {
        List<Expression> ret = List.of();
        expect("(");
        while (!consume(")")) {
            ret.add(parseExpression());
            if (!consume(",")) {
                expect(")");
                break;
            }
        }
        return ret;
    }

    private Expression parseAtom() {
        if (consume("(")) {
            Expression expression = parseExpression();
            expect(")");
            return expression;
        }
        Mark mark = getMark();
        switch (peek().getType()) {
            case "true":
            case "false":
            case "null": {
                return new OperationExpression(mark, next().getType(), List.of());
            }
            case "int": {
                int value = next().getInt();
                return new IntegerLiteralExpression(mark, value);
            }
            case "double": {
                double value = next().getDouble();
                return new DoubleLiteralExpression(mark, value);
            }
            case "string": {
                String value = next().getString();
                return new StringLiteralExpression(mark, value);
            }
            case "name": {
                String name = parseName();
                if (at("(")) {
                    List<Expression> arguments = parseArguments();
                    return new MethodCallExpression(mark, null, null, name, arguments);
                } else {
                    return new NameExpression(mark, name);
                }
            }
        }
        throw err("Expected expression");
    }
}
