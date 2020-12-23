package cjx.compiler

import cjx.compiler.CJAstItemMemberDefinition
import cjx.compiler.CJAstTypeCondition
import cjx.compiler.CJAstTraitExpression
import cjx.compiler.CJAstTypeParameter
import cjx.compiler.CJIRModifier
import cjx.compiler.CJAstImport
import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

## Class, trait or union definition
class CJAstItemDefinition: CJAstNode {
    val _mark: CJMark
    val _packageName: String
    val _imports: List[CJAstImport]
    val _comment: Nullable[String]
    val _modifiers: List[CJIRModifier]
    val _shortName: String
    val _typeParameters: List[CJAstTypeParameter]
    val _traits: List[Tuple[CJAstTraitExpression, List[CJAstTypeCondition]]]
    val _members: List[CJAstItemMemberDefinition]

    def mark(self: Self): CJMark {
        return self._mark
    }

    def packageName(self: Self): String {
        return self._packageName
    }

    def imports(self: Self): List[CJAstImport] {
        return self._imports
    }

    def comment(self: Self): Nullable[String] {
        return self._comment
    }

    def modifiers(self: Self): List[CJIRModifier] {
        return self._modifiers
    }

    def shortName(self: Self): String {
        return self._shortName
    }

    def fullName(self: Self): String {
        return self._packageName + "." + self._shortName
    }

    def typeParameters(self: Self): List[CJAstTypeParameter] {
        return self._typeParameters
    }

    def traits(self: Self): List[Tuple[CJAstTraitExpression, List[CJAstTypeCondition]]] {
        return self._traits
    }

    def members(self: Self): List[CJAstItemMemberDefinition] {
        return self._members
    }
}
