package cjx.compiler

import cjx.compiler.CJIRType

## The constraint on an expression as it's being translated
union CJIRExpressionConstraint {
    ## Indicates nothing is known about the expression being translated
    case None

    ## Indicates that the expression to translate should have the
    ## exact given type.
    case Type(CJIRType)

    def none(): Self {
        return Self.None
    }

    def type(type: CJIRType): Self {
        return Self.Type(type)
    }
}
