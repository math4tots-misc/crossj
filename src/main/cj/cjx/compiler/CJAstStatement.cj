package cjx.compiler

import cjx.compiler.CJAstExpression
import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

union CJAstStatement: CJAstNode {
    case Expression(CJMark, CJAstExpression)
    case Return(CJMark, CJAstExpression)
    case Block(CJMark, List[CJAstStatement])
    case If(CJMark, CJAstExpression, CJAstStatement, Nullable[CJAstStatement])
    case While(CJMark, CJAstExpression, CJAstStatement)

    def mark(self: Self): CJMark {
        return union self {
            case Expression(mark, expr) = mark
            case Return(mark, expr) = mark
            case Block(mark, stmts) = mark
            case If(mark, cond, body, other) = mark
            case While(mark, cond, body) = mark
        }
    }
}
