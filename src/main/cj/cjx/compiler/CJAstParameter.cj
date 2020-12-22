package cjx.compiler

import cjx.compiler.CJAstTypeExpression
import cjx.compiler.CJAstTraitExpression
import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

class CJAstParameter: CJAstNode {
    val _mark: CJMark
    val _mutable: Bool
    val _name: String
    val _type: CJAstTypeExpression

    def mark(self: Self): CJMark {
        return self._mark
    }
    def mutable(self: Self): Bool {
        return self._mutable
    }

    def name(self: Self): String {
        return self._name
    }

    def type(self: Self): CJAstTypeExpression {
        return self._type
    }
}
