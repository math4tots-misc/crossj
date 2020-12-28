package cjx.compiler

import cjx.compiler.CJContext

class CJJSTranslator {
    val _ctx: CJContext
    val _out: StringBuilder

    def init(): Self {
        return Self.initWithContext(CJContext.init())
    }

    def initWithContext(ctx: CJContext): Self {
        return Self.new(ctx, String.builder())
    }

    def ctx(self: Self): CJContext {
        return self._ctx
    }
}
