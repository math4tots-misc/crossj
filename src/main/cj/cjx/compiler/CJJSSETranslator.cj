package cjx.compiler

import cjx.compiler.CJJSStringBuilder
import cjx.compiler.CJAstExpression
import cjx.compiler.CJAstStatement
import cjx.compiler.CJJSTypeTranslator

## javascript statement and expression translator
private class CJJSSETranslator {
    val _out: CJJSStringBuilder
    val _typeTranslator: CJJSTypeTranslator

    def init(out: CJJSStringBuilder, typeTranslator: CJJSTypeTranslator): Self {
        return Self.new(out, typeTranslator)
    }

    def translateStatement(self: Self, stmt: CJAstStatement) {
        union stmt {
            case Expression(mark, expr) {
                self.translateExpression(expr)
            }
            case Return(mark, expr) {
                val exprstr = self.translateExpression(expr)
                self._out.line(exprstr + ";")
            }
            default { IO.panic("TODO translateStatement " + stmt.kind()) }
        }
    }

    ## Translates the expression by maybe emitting some statements,
    ## then returning a js expression with no side-effects that contains
    ## the result of the expression.
    def translateExpression(self: Self, expr: CJAstExpression): String {
        return union expr {
            default = IO.panic("TODO translateExpression " + expr.kind())
        }
    }
}
