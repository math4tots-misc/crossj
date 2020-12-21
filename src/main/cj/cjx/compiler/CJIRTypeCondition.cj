package cjx.compiler

import cjx.compiler.CJIRTrait
import cjx.compiler.CJIRType
import cjx.compiler.CJMark

## A condition asserting that the type variable with the given name implements
## the listed traits
class CJIRTypeCondition {
    val _mark: CJMark
    val _variableName: String
    val _traits: List[CJIRTrait]

    def init(mark: CJMark, variableName: String, traits: List[CJIRTrait]): Self {
        return Self.new(mark, variableName, traits)
    }

    def mark(self: Self): CJMark {
        return self._mark
    }

    def variableName(self: Self): String {
        return self._variableName
    }

    def traits(self: Self): List[CJIRTrait] {
        return self._traits
    }
}
