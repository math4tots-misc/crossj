package cjx.compiler

import cjx.compiler.CJContext
import cjx.compiler.CJIRType

## Javascript type translator
## Translates cj types to javascript types.
class CJJSTypeTranslator {
    val _ctx: CJContext

    def init(ctx: CJContext): Self {
        return Self.new(ctx)
    }

    def typeVariableNameToJS(self: Self, typeVariableName: String): String {
        return "TV$" + typeVariableName
    }

    def itemNameToJSMetaClassName(self: Self, itemName: String): String {
        return "MC$" + itemName.replace(".", "$")
    }

    def itemNameToJSMetaObjectName(self: Self, itemName: String): String {
        return "MO$" + itemName.replace(".", "$")
    }

    ## translates a type to a javascript expression that resolves to the type's
    ## corresponding meta object
    def translate(self: Self, type: CJIRType): String {
        return union type {
            case Variable(name, itemDefn, nullableMethodDefn) = self.typeVariableNameToJS(name)
            case Class(defn, args) = if (args.isEmpty()) {
                self.itemNameToJSMetaObjectName(defn.fullName())
            } else { (
                "new " + self.itemNameToJSMetaClassName(defn.fullName()) + "(" +
                    ",".join(args.map(arg -> self.translate(arg)))
                + ")"
            ) }
        }
    }
}
