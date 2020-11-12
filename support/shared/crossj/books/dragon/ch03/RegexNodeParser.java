package crossj.books.dragon.ch03;

import crossj.base.Str;
import crossj.base.StrIter;
import crossj.base.Try;
import crossj.base.XError;

final class RegexNodeParser {

    public static Try<RegexNode> parse(String pattern) {
        return new RegexNodeParser(Str.iter(pattern)).parseAll();
    }

    private final StrIter iter;

    RegexNodeParser(StrIter iter) {
        this.iter = iter;
    }

    private boolean at(char c) {
        return iter.hasCodePoint() && iter.peekCodePoint() == c;
    }

    private boolean consume(char c) {
        if (at(c)) {
            iter.nextCodePoint();
            return true;
        } else {
            return false;
        }
    }

    private Try<RegexNode> parseAll() {
        var tryNode = parseAltExpr();
        if (iter.hasCodePoint()) {
            return Try.fail("Invalid trailing data in regex pattern: " + iter.getString());
        } else {
            return tryNode;
        }
    }

    private Try<RegexNode> parseAltExpr() {
        var tryNode = parseCatExpr();
        while (tryNode.isOk() && consume('|')) {
            tryNode = tryNode.flatMap(lhs -> parseCatExpr().map(rhs -> lhs.or(rhs)));
        }
        return tryNode;
    }

    private Try<RegexNode> parseCatExpr() {
        var tryNode = Try.ok(RegexNode.epsilon());
        while (tryNode.isOk() && iter.hasCodePoint() && !at(')') && !at('|')) {
            tryNode = tryNode.flatMap(lhs -> parsePostfix().map(rhs -> lhs.and(rhs)));
        }
        return tryNode;
    }

    private Try<RegexNode> parsePostfix() {
        var tryNode = parseAtom();
        if (tryNode.isOk() && iter.hasCodePoint()) {
            switch (iter.peekCodePoint()) {
                case '+':
                    tryNode = tryNode.map(node -> node.plus());
                    iter.nextCodePoint();
                    break;
                case '*':
                    tryNode = tryNode.map(node -> node.star());
                    iter.nextCodePoint();
                    break;
                case '?':
                    tryNode = tryNode.map(node -> node.qmark());
                    iter.nextCodePoint();
                    break;
            }
        }
        return tryNode;
    }

    private Try<RegexNode> parseAtom() {
        int code = iter.nextCodePoint();
        switch (code) {
            case '\\':
                if (iter.hasCodePoint()) {
                    var escape = iter.nextCodePoint();
                    switch (escape) {
                        case '\\':
                        case '+':
                        case '*':
                        case '?':
                        case '(':
                        case ')':
                        case '[':
                        case ']':
                        case '{':
                        case '}':
                        case '|':
                        case '.':
                            return Try.ok(RegexNode.ofCodePoint(escape));
                        case 'n':
                            return Try.ok(RegexNode.ofChar('\n'));
                        case 'r':
                            return Try.ok(RegexNode.ofChar('\r'));
                        case 't':
                            return Try.ok(RegexNode.ofChar('\t'));
                        default:
                            return Try.fail("Invalid escape character " + escape + " in :" + iter.getString());
                    }
                } else {
                    return Try.fail("Regex pattern ends in unterminated escape sequence: " + iter.getString());
                }
            case '(': {
                var tryNode = parseAltExpr();
                if (tryNode.isOk() && (!iter.hasCodePoint() || iter.nextCodePoint() != ')')) {
                    return Try.fail("Unmatched open parenthesis in regex: " + iter.getString());
                }
                return tryNode;
            }
            case '*':
            case '+':
            case '?':
            case '{':
            case '}':
                return Try.fail("Misplaced postfix operator in regex: " + iter.getString());
            case '|':
                throw XError.withMessage("Internal regex parsing issue (|): " + iter.getString());
            case ')':
                return Try.fail("Unexpected close parenthesis in regex: " + iter.getString());
            case '[':
            case ']':
                return Try.fail("Unsupported regex feature \"[]\": " + iter.getString());
            default:
                return Try.ok(RegexNode.ofCodePoint(code));
        }
    }
}
