package cjx.compiler

import cjx.compiler.CJJSTranslator
import cjx.compiler.CJAstTypeExpression
import cjx.compiler.CJLocalContext
import cjx.compiler.CJIRType

## Javascript type translator
## Translates cj types to javascript types.
class CJJSTypeTranslator {
    val _lctx: CJLocalContext

    def init(ctx: CJLocalContext): Self {
        return Self.new(ctx)
    }

    ## translates a type to a javascript expression that resolves to the type's
    ## corresponding meta object
    def translateResolvedType(self: Self, type: CJIRType): String {
        return union type {
            case Variable(name, itemDefn, nullableMethodDefn) = CJJSTranslator._typevar(name)
            case Class(defn, args) = if (args.isEmpty()) {
                CJJSTranslator._itemMetaObjectName(defn.fullName())
            } else { (
                "new " + CJJSTranslator._itemMetaName(defn.fullName()) + "(" +
                    ",".join(args.map(arg -> self.translateResolvedType(arg)))
                + ")"
            ) }
        }
    }
}
