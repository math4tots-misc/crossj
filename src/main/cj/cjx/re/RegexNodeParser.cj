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

    def _expect(self: Self, c: Char): Try[Unit] {
        if self._consume(c) {
            return Try.Ok(())
        } else {
            return Try.fail("Expected " + c + " but got " + self._peek())
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
                    case '?' {
                        node = node._opt()
                        self._next()
                    }
                    case '{' {
                        self._next()
                        val min = self._parseDigits()?
                        var max = min
                        if self._consume(',') {
                            if self._consume('}') {
                                max = -1
                            } else {
                                max = self._parseDigits()?
                                '{'
                                self._expect('}')?
                            }
                        } else {
                            self._expect('}')?
                        }
                        node = node._interval(min, max)
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
                                    case 'n' {
                                        return Try.Ok(RegexNode.ofChar('\n'))
                                    }
                                    case 'r' {
                                        return Try.Ok(RegexNode.ofChar('\r'))
                                    }
                                    case 't' {
                                        return Try.Ok(RegexNode.ofChar('\t'))
                                    }
                                    case 'd' {
                                        return Try.Ok(RegexNode._charsetDigit)
                                    }
                                    case 'D' {
                                        return Try.Ok(RegexNode._charsetNonDigit)
                                    }
                                    case 'w' {
                                        return Try.Ok(RegexNode._charsetWord)
                                    }
                                    case 'W' {
                                        return Try.Ok(RegexNode._charsetNonWord)
                                    }
                                    case 's' {
                                        return Try.Ok(RegexNode._charsetWhitespace)
                                    }
                                    case 'S' {
                                        return Try.Ok(RegexNode._charsetNonWhitespace)
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
                        return Try.Ok(RegexNode._charsetAll)
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
                    case ')' {
                        return Try.fail("Unmatched close paren")
                    }
                    case '[' {
                        val startPosition = self._tell()
                        val negate = self._consume('^')
                        val letters = Set[Int].builder()
                        while self._peek().isPresent() and not self._at(']') {
                            val letter = self._next().get()
                            if Self._isRangeableLetter(letter.toInt()) and self._consume('-') {
                                if self._peek().map(c -> Self._isRangeableLetter(c.toInt())).getOrDefault(false) {
                                    # range (e.g. 0-9 or A-H)
                                    val upper = self._next().get().toInt()
                                    for i = letter.toInt(); i <= upper; i += 1 {
                                        letters.add(i)
                                    }
                                } else if self._at(']') {
                                    # ends with '-'
                                    # it's just another letter
                                    letters.add(letter.toInt())
                                    letters.add('-'.toInt())
                                } else {
                                    return Try.fail("Range starting " + letter + "- never terminated")
                                }
                            } else {
                                if letter == '\\' and self._peek().isPresent() {
                                    val escape = self._next().get()
                                    switch escape {
                                        case 'd' {
                                            letters.addAll(RegexNode._charsetDigit._letterList())
                                        }
                                        case 'D' {
                                            letters.addAll(RegexNode._charsetNonDigit._letterList())
                                        }
                                        case 'w' {
                                            letters.addAll(RegexNode._charsetWord._letterList())
                                        }
                                        case 'W' {
                                            letters.addAll(RegexNode._charsetNonWord._letterList())
                                        }
                                        case 's' {
                                            letters.addAll(RegexNode._charsetWhitespace._letterList())
                                        }
                                        case 'S' {
                                            letters.addAll(RegexNode._charsetNonWhitespace._letterList())
                                        }
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
                                        case '-' {
                                            letters.add(escape.toInt())
                                        }
                                        default {
                                            return Try.fail(
                                                "Unexpected character escaped in " +
                                                "character class: " +
                                                letter)
                                        }
                                    }
                                } else {
                                    letters.add(letter.toInt())
                                }
                            }
                        }
                        val name = "[" + self._w.cutFrom(startPosition) + "]"
                        if not self._consume(']') {
                            return Try.fail("Unmatched open bracket")
                        }
                        var charClass = RegexNode._charset(name, letters.build().toList())
                        if negate {
                            charClass = charClass._negate(name)
                        }
                        return Try.Ok(charClass)
                    }
                    case ']' {
                        return Try.fail("Unmatched close bracket")
                    }
                    default {
                        return Try.Ok(RegexNode.ofChar(code))
                    }
                }
            }
        }
        Assert.withMessage(false, "FUBAR RegexNodeParser._parseAtom")
    }

    def _isRangeableLetter(letter: Int): Bool {
        return (
            'a'.toInt() <= letter and letter <= 'z'.toInt() or
            'A'.toInt() <= letter and letter <= 'Z'.toInt() or
            '0'.toInt() <= letter and letter <= '9'.toInt()
        )
    }
}
