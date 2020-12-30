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
    var _item: Nullable[CJAstItemDefinition]
    var _method: Nullable[CJAstMethodDefinition]

    def initWithContext(ctx: CJContext): Self {
        return Self.new(ctx, Nullable.None, Nullable.None)
    }

    ## Gets the global context
    def global(self: Self): CJContext {
        return self._ctx
    }

    def item(self: Self): CJAstItemDefinition {
        return self._item.get()
    }

    def method(self: Self): CJAstMethodDefinition {
        return self._method.get()
    }

    def enterItem(self: Self, defn: CJAstItemDefinition) {
        union self._item {
            case Some(item) {
                IO.panic("Enter " + defn.fullName() + " before exiting " + item.fullName())
            }
            case None {
                self._item = Nullable.Some(defn)
            }
        }
    }

    def exitItem(self: Self) {
        union self._item {
            case Some(item) {
                self._item = Nullable.None
            }
            case None {
                IO.panic("Tried to exit from item but not in item")
            }
        }
    }

    def enterMethod(self: Self, defn: CJAstMethodDefinition) {
        if self._item.isEmpty() {
            IO.panic("Tried to enter method before entering item")
        }
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

    def resolveType(self: Self, typeExpression: CJAstTypeExpression): Try[CJIRType] {
        return CJIRResolver.init(self).resolveType(typeExpression)
    }
}
