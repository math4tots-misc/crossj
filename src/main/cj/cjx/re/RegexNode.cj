package cjx.re

## Describes the components of a regular expression.
##
## NOTE: In `cjx.re` regular expressions, all non-ASCII values are
## mapped to the value 127. (See `cjx.re.Alphabet`)
union RegexNode {
    case Epsilon
    case Letter(Int)
    case Cat(RegexNode, RegexNode)
    case Or(RegexNode, RegexNode)
    case Star(RegexNode)
    case Interval(RegexNode, Int, Int)

    def ofChar(ch: Char): Self {
        return Self.Letter(ch.toInt())
    }
}
