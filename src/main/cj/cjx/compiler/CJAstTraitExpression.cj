package cjx.compiler

import cjx.compiler.CJAstTypeExpression
import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

class CJAstTraitExpression: CJAstNode {
    val _mark: CJMark
    val _name: String
    val _args: List[CJAstTypeExpression]

    def mark(self: Self): CJMark {
        return self._mark
    }

    def name(self: Self): String {
        return self._name
    }

    def args(self: Self): List[CJAstTypeExpression] {
        return self._args
    }
}
