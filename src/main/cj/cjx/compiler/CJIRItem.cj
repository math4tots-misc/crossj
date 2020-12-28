package cjx.compiler

import cjx.compiler.CJIRItemKind
import cjx.compiler.CJMark
import cjx.compiler.CJAstItemDefinition

## Metadata about items (traits, classes and unions)
class CJIRItem {
    val _defn: CJAstItemDefinition

    def init(defn: CJAstItemDefinition): Self {
        return Self.new(defn)
    }

    def mark(self: Self): CJMark {
        return self._defn.mark()
    }

    def packageName(self: Self): String {
        return self._defn.packageName()
    }

    def kind(self: Self): CJIRItemKind {
        return self._defn.kind()
    }

    def fullName(self: Self): String {
        return self._defn.fullName()
    }
}
