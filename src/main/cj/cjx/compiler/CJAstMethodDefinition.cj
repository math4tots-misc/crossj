package cjx.compiler

import cjx.compiler.CJAstNode
import cjx.compiler.CJAstStatement
import cjx.compiler.CJAstTypeExpression
import cjx.compiler.CJAstParameter
import cjx.compiler.CJAstTypeParameter
import cjx.compiler.CJIRModifier
import cjx.compiler.CJAstTypeCondition
import cjx.compiler.CJMark

class CJAstMethodDefinition: CJAstNode {
    val _mark: CJMark
    val _conditions: List[CJAstTypeCondition]
    val _comment: Nullable[String]
    val _modifiers: List[CJIRModifier]
    val _name: String
    val _typeParameters: List[CJAstTypeParameter]
    val _parameters: List[CJAstParameter]
    val _returnType: Nullable[CJAstTypeExpression]
    val _body: Nullable[CJAstStatement]

    def mark(self: Self): CJMark {
        return self._mark
    }

    def conditions(self: Self): List[CJAstTypeCondition] {
        return self._conditions
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

    def typeParameters(self: Self): List[CJAstTypeParameter] {
        return self._typeParameters
    }

    def parameters(self: Self): List[CJAstParameter] {
        return self._parameters
    }

    def returnType(self: Self): Nullable[CJAstTypeExpression] {
        return self._returnType
    }

    def body(self: Self): Nullable[CJAstStatement] {
        return self._body
    }
}
