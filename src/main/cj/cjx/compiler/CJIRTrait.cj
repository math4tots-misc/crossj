package cjx.compiler

import cjx.compiler.CJIRType

class CJIRTrait {
    val _fullName: String
    val _args: List[CJIRType]

    def fullName(self: Self): String {
        return self._fullName
    }

    def args(self: Self): List[CJIRType] {
        return self._args
    }
}
