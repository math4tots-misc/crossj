package cjx.compiler

import cjx.compiler.CJAstMethodDefinition
import cjx.compiler.CJAstItemDefinition

union CJIRType: Repr {
    ## A type variable carries with it the item (and method) where it's declared and used.
    ##
    ## Even if the variable is defined on the item, the method where it's referred to
    ## is still useful because the method may have additional constraints on the variable.
    ##
    case Variable(String, CJAstItemDefinition, Nullable[CJAstMethodDefinition])

    ## A class type (note, unions are also classes) contains the class's item definition
    ## and type arguments.
    case Class(CJAstItemDefinition, List[Self])

    def repr(self: Self): String {
        return union self {
            case Variable(name, itemDefn, methodDefn) = name
            case Class(defn, args) = {
                val fullName = defn.fullName()
                val argstr = if (args.isEmpty()) {
                    ""
                } else {
                    "[" + ",".join(args) + "]"
                }
                fullName + argstr
            }
        }
    }
}
