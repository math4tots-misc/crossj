package cjx.compiler

import cjx.compiler.CJAstExpression
import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

union CJAstAssignmentTarget: CJAstNode {
    case Name(CJMark, String)
    case Tuple(CJMark, List[Self])
    case Field(CJMark, CJAstExpression, String)

    def mark(self: Self): CJMark {
        return union self {
            case Name(mark, name) = mark
            case Tuple(mark, subtargets) = mark
            case Field(mark, owner, name) = mark
        }
    }
}
