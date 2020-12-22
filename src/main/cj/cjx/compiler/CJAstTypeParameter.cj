package cjx.compiler

import cjx.compiler.CJAstTraitExpression
import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

class CJAstTypeParameter: CJAstNode {
    val _mark: CJMark
    val _name: String
    val _bounds: List[CJAstTraitExpression]

    def mark(self: Self): CJMark {
        return self._mark
    }

    def name(self: Self): String {
        return self._name
    }

    def bounds(self: Self): List[CJAstTraitExpression] {
        return self._bounds
    }
}
