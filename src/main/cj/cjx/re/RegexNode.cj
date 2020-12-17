package cjx.re

import cjx.re.Alphabet
import cj.Range
import cjx.re.RegexNodeParser

## Describes the components of a regular expression.
##
## NOTE: In `cjx.re` regular expressions, all non-ASCII values are
## mapped to the value 127. (See `cjx.re.Alphabet`)
union RegexNode: Repr {
    case Epsilon
    case Letter(Int)
    case Cat(RegexNode, RegexNode)
    case Or(RegexNode, RegexNode)
    case Star(RegexNode)
    case Plus(RegexNode)
    case Optional(RegexNode)
    case Interval(RegexNode, Int, Int)
    case CharSet(String, List[Int])

    static val _charsetAll: Self = Self._charset(".", Range.of(0, Alphabet.count).toList())
    static val _charsetDigit: Self = Self._charset(
        "\\d", Range.of('0'.toInt(), '9'.toInt() + 1).toList())
    static val _charsetNonDigit: Self = Self._charsetDigit._negate("\\D")
    static val _charsetWord: Self = Self._charset(
        "\\w",
        [
            Range.of('0'.toInt(), '9'.toInt() + 1).toList(),
            Range.of('a'.toInt(), 'z'.toInt() + 1).toList(),
            Range.of('A'.toInt(), 'Z'.toInt() + 1).toList(),
            ['_'.toInt()],
        ].flatten()
    )
    static val _charsetNonWord: Self = Self._charsetWord._negate("\\W")
    static val _charsetWhitespace: Self = Self._charset(
        "\\s",
        [
            # https://developer.mozilla.org/en-US/docs/Glossary/Whitespace
            0x09, # tab ('\t')
            0x0a, # newline ('\n')
            0x0b, # vertical tab ('\v')
            0x0c, # formfeed ('\f')
            0x0d, # carriage return ('\r')
            0x20, # space (' ')
        ]
    )
    static val _charsetNonWhitespace: Self = Self._charsetWhitespace._negate("\\S")

    def fromPattern(pattern: String): Try[Self] {
        return RegexNodeParser.parse(pattern)
    }

    def ofChar(ch: Char): Self {
        return Self.Letter(ch.toInt())
    }

    def repr(self: Self): String {
        return union self {
            case Epsilon = "Epsilon"
            case Letter(i) = "Letter(" + i.toChar().repr() + ")"
            case Cat(a, b) = "Cat(" + a.repr() + ", " + b.repr() + ")"
            case Or(a, b) = "Or(" + a.repr() + ", " + b.repr() + ")"
            case Star(x) = "Star(" + x.repr() + ")"
            case Plus(x) = "Plus(" + x.repr() + ")"
            case Optional(x) = "Optional(" + x.repr() + ")"
            case Interval(x, a, b) = "Interval(" + x.repr() + ", " + a + ", " + b + ")"
            case CharSet(name, chars) = "CharSet(" + name + ",...)"
        }
    }

    def _cat(a: Self, b: Self): Self {
        return union a {
            case Epsilon = b
            default = union b {
                case Epsilon = a
                default = Self.Cat(a, b)
            }
        }
    }

    def _or(a: Self, b: Self): Self {
        return Self.Or(a, b)
    }

    def _star(x: Self): Self {
        return Self.Star(x)
    }

    def _plus(x: Self): Self {
        return Self.Plus(x)
    }

    def _opt(x: Self): Self {
        return Self.Optional(x)
    }

    def _interval(x: Self, a: Int, b: Int): Self {
        return Self.Interval(x, a, b)
    }

    ## Returns a CharSets' letter list
    ## panics if called on anything other than a CharSet
    def _letterList(self: Self): List[Int] {
        union self {
            case CharSet(name, chars) {
                return chars
            }
            default {
                IO.panic("RegexNode._letterList on " + self)
            }
        }
    }

    def _charset(name: String, letters: List[Int]): Self {
        return Self.CharSet(name, letters)
    }

    ## Negate a CharSet and give it a new name.
    ## panics if called on anything other than a CharSet
    def _negate(self: Self, newName: String): Self {
        union self {
            case CharSet(name, chars) {
                val set = chars.toSet()
                return Self.CharSet(
                    newName,
                    Range.of(0, Alphabet.count).iter().filter(c -> c not in set).toList()
                )
            }
            default {
                IO.panic("RegexNode._negate on " + self)
            }
        }
    }
}
