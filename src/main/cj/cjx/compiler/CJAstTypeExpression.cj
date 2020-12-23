package cjx.compiler

import cjx.compiler.CJMark
import cjx.compiler.CJAstNode

class CJAstTypeExpression: CJAstNode, Repr {
    val _mark: CJMark
    val _name: String
    val _args: List[Self]

    def mark(self: Self): CJMark {
        return self._mark
    }

    def name(self: Self): String {
        return self._name
    }

    def args(self: Self): List[Self] {
        return self._args
    }

    def repr(self: Self): String {
        return (
            if (self._args.toBool()) (
                self._name + "[" + ", ".join(self._args.map(r -> r.repr())) + "]")
            else self._name)
    }
}
