package cjx.compiler

import cjx.compiler.CJIRModifier
import cjx.compiler.CJIRItemKind
import cjx.compiler.CJIRTypeParameter
import cjx.compiler.CJMark

class CJIRItemInfo {
    val _mark: CJMark
    val _packageName: String
    val _imports: List[String]
    val _comment: Nullable[String]
    val _modifiers: List[CJIRModifier]
    val _kind: CJIRItemKind
    val _shortName: String
    val _typeParameters: List[CJIRTypeParameter]

    def init(
            mark: CJMark,
            packageName: String,
            imports: List[String],
            comment: Nullable[String],
            modifiers: List[CJIRModifier],
            kind: CJIRItemKind,
            shortName: String,
            typeParameters: List[CJIRTypeParameter]): Self {
        return Self.new(
            mark,
            packageName,
            imports,
            comment,
            modifiers,
            kind,
            shortName,
            typeParameters,
        )
    }

    def mark(self: Self): CJMark {
        return self._mark
    }

    def imports(self: Self): List[String] {
        return self._imports
    }

    def modifiers(self: Self): List[CJIRModifier] {
        return self._modifiers
    }

    def kind(self: Self): CJIRItemKind {
        return self._kind
    }

    def packageName(self: Self): String {
        return self._packageName
    }

    def shortName(self: Self): String {
        return self._shortName
    }

    def fullName(self: Self): String {
        return self._packageName + "." + self._shortName
    }

    def typeParameters(self: Self): List[CJIRTypeParameter] {
        return self._typeParameters
    }
}
