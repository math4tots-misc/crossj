package cjx.compiler

import cjx.compiler.CJJSTranslator
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
}
