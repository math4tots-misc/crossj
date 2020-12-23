package cjx.compiler

import cjx.compiler.CJAstExpression
import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

union CJAstStatement: CJAstNode {
    case Expression(CJMark, CJAstExpression)
    case Return(CJMark, CJAstExpression)
    case Block(CJMark, List[CJAstStatement])

    def mark(self: Self): CJMark {
        return union self {
            case Expression(mark, expr) = mark
            case Return(mark, expr) = mark
            case Block(mark, stmts) = mark
        }
    }
}
