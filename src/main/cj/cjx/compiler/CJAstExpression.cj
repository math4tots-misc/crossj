package cjx.compiler

import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

union CJAstExpression: CJAstNode {
    def mark(self: Self): CJMark {
        union self {
        }
    }
}
