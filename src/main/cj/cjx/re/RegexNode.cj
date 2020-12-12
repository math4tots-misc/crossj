package cjx.re

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
