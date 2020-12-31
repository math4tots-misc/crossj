package cjx.compiler

import cjx.compiler.CJIRResolver
import cjx.compiler.CJIRType
import cjx.compiler.CJAstTypeExpression
import cjx.compiler.CJContextMixin
import cjx.compiler.CJAstMethodDefinition
import cjx.compiler.CJAstItemDefinition
import cjx.compiler.CJContext


## Like CJContext, but also carry local information
class CJLocalContext: CJContextMixin {
    val _ctx: CJContext
    val _item: CJAstItemDefinition
    val _method: Nullable[CJAstMethodDefinition]

    def init(
            ctx: CJContext,
            item: CJAstItemDefinition,
            method: Nullable[CJAstMethodDefinition]): Self {
        return Self.new(ctx, item, method)
    }

    ## Gets the global context
    def global(self: Self): CJContext {
        return self._ctx
    }

    def item(self: Self): CJAstItemDefinition {
        return self._item
    }

    def method(self: Self): CJAstMethodDefinition {
        return self._method.get()
    }

    def enterMethod(self: Self, defn: CJAstMethodDefinition) {
        union self._method {
            case Some(method) {
                IO.panic("Enter " + defn.name() + " before exiting " + method.name())
            }
            case None {
                self._method = Nullable.Some(defn)
            }
        }
    }

    def exitMethod(self: Self) {
        union self._method {
            case Some(method) {
                self._method = Nullable.None
            }
            case None {
                IO.panic("Tried to exit from method but not in method")
            }
        }
    }
}
