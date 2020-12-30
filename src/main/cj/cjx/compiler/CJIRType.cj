package cjx.compiler

import cjx.compiler.CJAstItemDefinition

class CJIRType: Repr {
    val _defn: CJAstItemDefinition
    val _args: List[Self]

    def init(defn: CJAstItemDefinition, args: List[Self]): Self {
        return Self.new(defn, args)
    }

    def repr(self: Self): String {
        val fullName = self._defn.fullName()
        val args = if (self._args.isEmpty()) { "" } else { "[" + ",".join(self._args) + "]" }
        return fullName + args
    }
}
