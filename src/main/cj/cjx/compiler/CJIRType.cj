package cjx.compiler

import cjx.compiler.CJAstMethodDefinition
import cjx.compiler.CJAstItemDefinition

union CJIRType: Repr {
    ## A self type only really appears in traits (in a class/union, 'Self' is really
    ## just an alias for the class itself reified with its own type parameters).
    ## Refers to the final class type that is implemented by this trait.
    case Self(CJAstItemDefinition)

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
            case Self(itemDefn) = "Self"
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

    def reifyClassWithOwnTypeParameters(defn: CJAstItemDefinition): Self {
        union defn.kind() {
            case Trait {
                IO.panic("reifyClassWithOwnTypeParameters can only be called on class items")
            }
            default {}
        }
        val args = List[Self].builder()
        for typeParameter in defn.typeParameters() {
            args.add(Self.Variable(
                typeParameter.name(), defn, Nullable[CJAstMethodDefinition].None))
        }
        return Self.Class(defn, args.build())
    }
}
