package cjx.compiler

union CJIRItemKind: Repr {
    case Class
    case Union
    case Trait

    def repr(self: Self): String {
        return union self {
            case Class = "class"
            case Union = "union"
            case Trait = "trait"
        }
    }
}
