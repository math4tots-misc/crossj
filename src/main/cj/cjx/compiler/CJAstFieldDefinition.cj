package cjx.compiler

import cjx.compiler.CJAstExpression
import cjx.compiler.CJAstTypeExpression
import cjx.compiler.CJIRModifier
import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

class CJAstFieldDefinition: CJAstNode {
    val _mark: CJMark
    val _comment: Nullable[String]
    val _modifiers: List[CJIRModifier]
    val _mutable: Bool
    val _name: String
    val _type: CJAstTypeExpression
    val _expression: Nullable[CJAstExpression]

    def mark(self: Self): CJMark {
        return self._mark
    }

    def comment(self: Self): Nullable[String] {
        return self._comment
    }

    def modifiers(self: Self): List[CJIRModifier] {
        return self._modifiers
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

    def expression(self: Self): Nullable[CJAstExpression] {
        return self._expression
    }

    def isInstance(self: Self): Bool {
        return not self.isStatic()
    }

    def isStatic(self: Self): Bool {
        return CJIRModifier.Static in self.modifiers()
    }
}
