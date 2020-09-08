package com.github.math4tots.crossj.parser;

import crossj.*;

/**
 * Roughly based on https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html
 */
public final class Lexer {
    public static List<Token> lex(Source source) {
        return new Lexer(source).lex();
    }

    private static List<String> SEPARATORS = List.of("(", ")", "{", "}", "[", "]", ";", ",", ".", "...", "@", "::");
    private static List<String> OPERATORS = List.of("=", ">", "<", "!", "~", "?", ":", "->", "==", ">=", "<=", "!=",
            "&&", "||", "++", "--", "+", "-", "*", "/", "&", "|", "^", "%", "<<", ">>", ">>>", "+=", "-=", "*=", "/=",
            "&=", "|=", "^=", "%=", "<<=", ">>=", ">>>=");
    private static List<String> UNSORTED_SYMBOLS = List.of(SEPARATORS, OPERATORS).flatMap(x -> x);
    private static List<String> SYMBOLS = List.reversed(List.sorted(UNSORTED_SYMBOLS));
    private static Set<String> KEYWORDS = Set.of("abstract", "continue", "for", "new", "switch", "assert", "default",
            "if", "package", "synchronized", "boolean", "do", "goto", "private", "this", "break", "double",
            "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum",
            "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final",
            "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float",
            "native", "super", "while", "true", "false", "null",
            // Technically not a keyword, but I want to reserve it anyway
            "var");

    private final Source source;
    private final String s;
    private int i = 0;
    private int line = 1;
    private int column = 1;

    private Lexer(Source source) {
        this.source = source;
        this.s = source.data;
    }

    private void incr() {
        if (more()) {
            char ch = peek();
            i++;
            if (ch == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }
    }

    private char peek() {
        return s.charAt(i);
    }

    private Mark newMark() {
        return new Mark(source, line, column);
    }

    private Token newToken(String type, Object value) {
        return new Token(newMark(), type, value);
    }

    private void incrn(int n) {
        for (int i = 0; i < n; i++) {
            incr();
        }
    }

    private boolean more() {
        return i < s.length();
    }

    private boolean startsWith(String... strings) {
        for (String prefix : strings) {
            if (s.startsWith(prefix, i)) {
                return true;
            }
        }
        return false;
    }

    private void skipSpacesAndComments() {
        while (more() && (Character.isWhitespace(peek()) || startsWith("//", "/*"))) {
            if (startsWith("//")) {
                while (more() && peek() != '\n') {
                    incr();
                }
            } else if (startsWith("/*")) {
                incrn(2);
                while (more() && !startsWith("*/")) {
                    incr();
                }
            } else {
                incr();
            }
        }
    }

    private List<Token> lex() {
        List<Token> ret = List.of();
        while (true) {
            skipSpacesAndComments();
            if (!more()) {
                break;
            }
            char ch = peek();
            int start = i;

            // int and double literals
            if (Character.isDigit(ch)) {
                incr();
                while (more() && Character.isDigit(peek())) {
                    incr();
                }
                if (more() && peek() == '.') {
                    // double literal
                    incr();
                    while (more() && Character.isDigit(peek())) {
                        incr();
                    }
                    double value = Double.parseDouble(s.substring(start, i));
                    ret.add(newToken("double", value));
                } else {
                    // integer literal
                    int value = Integer.parseInt(s.substring(start, i));
                    ret.add(newToken("int", value));
                }
                continue;
            }

            // identifiers and keywords
            if (isWordChar(ch)) {
                incr();
                while (more() && isWordChar(peek())) {
                    incr();
                }
                String name = s.substring(start, i);
                if (KEYWORDS.contains(name)) {
                    ret.add(newToken(name, null));
                } else {
                    ret.add(newToken("name", name));
                }
                continue;
            }

            // string and character literals
            if (ch == '"' || ch == '\'') {
                char quote = ch;
                StringBuilder sb = new StringBuilder();
                incr();
                while (more() && peek() != quote) {
                    char c = peek();
                    if (c == '\\') {
                        incr();
                        if (!more()) {
                            throw err("Expected string literal escape sequence");
                        }
                        char e = peek();
                        switch (e) {
                            case 'b':
                                sb.append("\b");
                                break;
                            case 't':
                                sb.append("\t");
                                break;
                            case 'n':
                                sb.append("\n");
                                break;
                            case 'f':
                                sb.append("\f");
                                break;
                            case 'r':
                                sb.append("\r");
                                break;
                            case '\"':
                                sb.append("\"");
                                break;
                            case '\'':
                                sb.append("\'");
                                break;
                            default:
                                throw err("Invalid string literal escape: " + e);
                        }
                    } else {
                        sb.append(c);
                    }
                    incr();
                }
                if (!more()) {
                    throw err("Unterminated string literal");
                }
                incr();
                String type = quote == '"' ? "string" : "char";
                String value = sb.toString();
                if (type.equals("char") && value.length() != 1) {
                    throw err("Char literals must have length 1, but got " + value.length());
                }
                ret.add(newToken(type, value));
                continue;
            }

            // separators and operators
            {
                boolean found = false;
                for (String symbol : SYMBOLS) {
                    if (startsWith(symbol)) {
                        incrn(symbol.length());
                        ret.add(newToken(symbol, null));
                        found = true;
                        break;
                    }
                }
                if (found) {
                    continue;
                }
            }

            // unrecognized...
            throw err("Unrecognized token starting " + ch);
        }
        ret.add(newToken("EOF", null));
        return ret;
    }

    private boolean isWordChar(char ch) {
        return ch == '_' || ch == '$' || Character.isLetterOrDigit(ch);
    }

    private XError err(String message) {
        return XError.withMessage(newMark().format() + message);
    }
}
