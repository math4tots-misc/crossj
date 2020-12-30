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

    def translateLocalVariableName(self: Self, name: String): String {
        return "L$" + name
    }

    def translateFieldName(self: Self, name: String): String {
        return "F$" + name
    }

    def translateMethodName(self: Self, name: String): String {
        return "M$" + name
    }

    def translateStatement(self: Self, stmt: CJAstStatement): Try[Unit] {
        union stmt {
            case Expression(mark, expr) {
                self.translateExpression(expr)
            }
            case Return(mark, expr) {
                val exprstr = self.translateExpression(expr)?
                self._out.line(exprstr + ";")
            }
            default { IO.panic("TODO translateStatement " + stmt.kind()) }
        }
        return Try.Ok(())
    }

    ## Translates the expression by maybe emitting some statements,
    ## then returning a js expression with no side-effects that contains
    ## the result of the expression.
    def translateExpression(self: Self, expr: CJAstExpression): Try[String] {
        return union expr {
            case Unit(mark) = Try.Ok("0")
            case Bool(mark, rawText) = Try.Ok(rawText)
            case Char(mark, rawText) = Try.Ok(rawText + ".codePointAt(0)")
            case Int(mark, rawText) = Try.Ok(rawText)
            case Double(mark, rawText) = Try.Ok(rawText)
            case String(mark, rawText) = Try.Ok(rawText)
            case Name(mark, name) = Try.Ok(self.translateLocalVariableName(name))
            default = IO.panic("TODO translateExpression " + expr.kind())
        }
    }
}
