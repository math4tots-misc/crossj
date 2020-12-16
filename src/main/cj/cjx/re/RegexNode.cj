package cjx.re

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
    case Interval(RegexNode, Int, Int)

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
            case Interval(x, a, b) = "Interval(" + x.repr() + ", " + a + ", " + b + ")"
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

    def _interval(x: Self, a: Int, b: Int): Self {
        return Self.Interval(x, a, b)
    }
}
