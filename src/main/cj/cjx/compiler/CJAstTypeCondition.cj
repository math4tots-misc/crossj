package cjx.compiler

import cjx.compiler.CJAstTraitExpression
import cjx.compiler.CJAstTypeExpression
import cjx.compiler.CJMark

## A condition asserting that the type resulting from the given TypeExpression has
## the listed traits.
class CJAstTypeCondition {
    val _mark: CJMark
    val _type: CJAstTypeExpression
    val _traits: List[CJAstTraitExpression]

    def mark(self: Self): CJMark {
        return self._mark
    }

    def type(self: Self): CJAstTypeExpression {
        return self._type
    }

    def traits(self: Self): List[CJAstTraitExpression] {
        return self._traits
    }
}
