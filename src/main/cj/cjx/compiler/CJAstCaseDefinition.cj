package cjx.compiler

import cjx.compiler.CJAstTypeExpression
import cjx.compiler.CJIRModifier
import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

class CJAstCaseDefinition: CJAstNode {
    val _mark: CJMark
    val _comment: Nullable[String]
    val _modifiers: List[CJIRModifier]
    val _name: String
    val _types: List[CJAstTypeExpression]

    def mark(self: Self): CJMark {
        return self._mark
    }

    def comment(self: Self): Nullable[String] {
        return self._comment
    }

    def modifiers(self: Self): List[CJIRModifier] {
        return self._modifiers
    }

    def name(self: Self): String {
        return self._name
    }

    def types(self: Self): List[CJAstTypeExpression] {
        return self._types
    }
}
