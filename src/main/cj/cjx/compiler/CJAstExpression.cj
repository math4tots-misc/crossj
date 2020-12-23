package cjx.compiler

import cjx.compiler.CJAstStatement
import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

union CJAstExpression: CJAstNode {
    case Unit(CJMark)
    case Bool(CJMark, String)
    case Char(CJMark, String)
    case Int(CJMark, String)
    case Double(CJMark, String)
    case Name(CJMark, String)
    case Lambda(CJMark, List[String], CJAstStatement)

    def mark(self: Self): CJMark {
        return union self {
            case Unit(mark) = mark
            case Bool(mark, s) = mark
            case Char(mark, s) = mark
            case Int(mark, s) = mark
            case Double(mark, s) = mark
            case Name(mark, name) = mark
            case Lambda(mark, args, body) = mark
        }
    }
}
