package cjx.compiler

import cjx.compiler.CJIRTrait
import cjx.compiler.CJMark

class CJIRTypeParameter {
    val _mark: CJMark
    val _name: String
    val _bounds: MutableList[CJIRTrait]

    def init(mark: CJMark, name: String): Self {
        return Self.new(mark, name, @[])
    }

    def mark(self: Self): CJMark {
        return self._mark
    }

    def name(self: Self): String {
        return self._name
    }

    def bounds(self: Self): List[CJIRTrait] {
        return self._bounds.toList()
    }

    def addBound(self: Self, trait_: CJIRTrait) {
        self._bounds.add(trait_)
    }
}
