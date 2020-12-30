package cjx.compiler

import cjx.compiler.CJAstExpression
import cjx.compiler.CJAstStatement
import cjx.compiler.CJLocalContext

## Resolve statements and expressions
class CJIRResolver {
    val _lctx: CJLocalContext

    def init(lctx: CJLocalContext): Self {
        return Self.new(lctx)
    }

    def resolveStatement(self: Self, stmt: CJAstStatement): Try[CJAstStatement] {
        return union stmt {
            default = IO.panic("TODO unhandled resolveStatement: " + stmt.kind())
        }
    }

    def resolveExpression(self: Self, expr: CJAstExpression): Try[CJAstExpression] {
        return union expr {
            default = IO.panic("TODO unhandled resolveExpression: " + expr.kind())
        }
    }
}
