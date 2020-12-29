package cjx.compiler

import cjx.compiler.CJAstItemDefinition
import cjx.compiler.CJContext

class CJJSTranslator {
    static val _preludePath: String = "support/app/crossj.hacks.cj/prelude.js"
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

    def _emitPrelude(self: Self): Try[Unit] {
        self._out.add(FS.read(Self._preludePath)?)
    }

    def _translateItemPass1(self: Self, fullName: String): Try[Unit] {
        val defn = self._ctx.loadItem(fullName)?
        return Try.Ok(())
    }
}
