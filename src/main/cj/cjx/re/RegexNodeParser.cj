package cjx.re

import cjx.re.RegexNode
import cj.StringWalker

class RegexNodeParser {
    val _w: StringWalker

    def parse(pattern: String): Try[RegexNode] {
        return (
            Self.new(StringWalker.of(pattern))
                ._parseAll()
                .addContext("RegexNodeParser.parse")
                .addContext(pattern)
        )
    }

    def _at(self: Self, c: Char): Bool {
        return union self._w.peek() {
            case Some(p) = p == c
            case None = false
        }
    }

    def _tell(self: Self): Int {
        return self._w.tell()
    }

    def _seek(self: Self, i: Int) {
        self._w.seek(i)
    }

    def _next(self: Self): Nullable[Char] {
        return self._w.next()
    }

    def _peek(self: Self): Nullable[Char] {
        return self._w.peek()
    }

    def _consume(self: Self, c: Char): Bool {
        if self._at(c) {
            self._next()
            return true
        } else {
            return false
        }
    }

    def _parseAll(self: Self): Try[RegexNode] {
        val node = self._parseAlt()?
        if self._peek().isPresent() {
            return Try.fail("Extraneous trailing data")
        } else {
            return Try.Ok(node)
        }
    }

    def _parseAlt(self: Self): Try[RegexNode] {
        var node = self._parseCat()?
        while self._consume('|') {
            node = node._or(self._parseCat()?)
        }
        return Try.Ok(node)
    }

    def _parseCat(self: Self): Try[RegexNode] {
        var node = RegexNode.Epsilon
        while self._peek().map(c -> c != ')' and c != '|').getOrDefault(false) {
            node = node._cat(self._parsePostfix()?)
        }
        return Try.Ok(node)
    }

    def _parseDigits(self: Self): Try[Int] {
        union self._peek() {
            case None {
                return Try.fail("Expected digit but got end of string")
            }
            case Some(c) {
                if not c.isDigit() {
                    return Try.fail("Expected digit but got " + c.repr())
                }
                val start = self._tell()
                while self._peek().map(ch -> ch.isDigit()).getOrDefault(false) {
                    self._next()
                }
                return Try.Ok(self._w.cutFrom(start).parseInt().get())
            }
        }
    }

    def _parsePostfix(self: Self): Try[RegexNode] {
        var node = self._parseAtom()?
        union self._peek() {
            case None {}
            case Some(code) {
                switch (code) {
                    case '+' {
                        node = node._plus()
                        self._next()
                    }
                    case '*' {
                        node = node._star()
                        self._next()
                    }
                }
            }
        }
        return Try.Ok(node)
    }

    def _parseAtom(self: Self): Try[RegexNode] {
        union self._next() {
            case None {
                return Try.fail("Expected atom but got EOS")
            }
            case Some(code) {
                switch (code) {
                    case '\\' {
                        union self._next() {
                            case None {
                                return Try.fail(
                                    "Regex pattern ends in unterminated escape sequence")
                            }
                            case Some(escape) {
                                switch (escape) {
                                    case '\\'
                                    case '+'
                                    case '*'
                                    case '?'
                                    case '('
                                    case ')'
                                    case '['
                                    case ']'
                                    case '{'
                                    case '}'
                                    case '|'
                                    case '.'
                                    case '$' {
                                        return Try.Ok(RegexNode.ofChar(escape))
                                    }
                                    default {
                                        return Try.fail(
                                            "Invalid escape character " + escape.repr())
                                    }
                                }
                            }
                        }
                    }
                    case '.' {
                        IO.panic("TODO: RegexNode.All")
                    }
                    case '(' {
                        val inner = self._parseAlt()?
                        if not self._next().map(ch -> ch == ')').getOrDefault(false) {
                            return Try.fail("Unmatched open parenthesis")
                        } else {
                            return Try.Ok(inner)
                        }
                    }
                    case '*'
                    case '+'
                    case '?'
                    case '{'
                    case '}' {
                        return Try.fail("Misplaced postfix operator (" + code + ")")
                    }
                    case '|' {
                        IO.panic("FUBAR: Internal regex parsing issue (|): " + self._w.string())
                    }
                    default {
                        return Try.Ok(RegexNode.ofChar(code))
                    }
                }
            }
        }
        Assert.withMessage(false, "FUBAR RegexNodeParser._parseAtom")
    }
}
