package cjx.compiler

import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

class CJAstImport: CJAstNode {
    val _mark: CJMark
    val _qualifiedName: String

    def mark(self: Self): CJMark {
        return self._mark
    }

    def qualifiedName(self: Self): String {
        return self._qualifiedName
    }

    def alias(self: Self): String {
        # For now, alias is always just the last component in the qualified name
        return self._qualifiedName.split(".").last()
    }
}
