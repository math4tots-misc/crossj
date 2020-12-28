package cjx.compiler

import cjx.compiler.CJAstItemDefinition
import cjx.compiler.CJAstMethodDefinition

## A reference to a method without any of the item or method type parameters
## specified
class CJIRRawMethod {
    ## The item in which this method was declared
    val _ownerDefn: CJAstItemDefinition

    ## Definition of this method
    val _defn: CJAstMethodDefinition

    def init(ownerDefn: CJAstItemDefinition, defn: CJAstMethodDefinition): Self {
        return Self.new(ownerDefn, defn)
    }

    def shortName(self: Self): String {
        return self._defn.name()
    }

    def isAbstract(self: Self): Bool {
        return not self._defn.body().isPresent() and not self._ownerDefn.isNative()
    }

    def isNative(self: Self): Bool {
        return self._ownerDefn.isNative()
    }
}
