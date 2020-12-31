package cjx.compiler

import cjx.compiler.CJMark
import cjx.compiler.CJIRExpression

union CJIRStatement {
    case Expression(CJMark, CJIRExpression)
    case Return(CJMark, CJIRExpression)

    def mark(self: Self): CJMark {
        return union self {
            case Expression(mark, expr) = mark
            case Return(mark, expr) = mark
        }
    }
}
