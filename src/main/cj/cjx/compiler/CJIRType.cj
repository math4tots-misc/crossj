package cjx.compiler

## Fully qualified type
union CJIRType: Repr {
    case Class(String, List[CJIRType])
    case Variable(String)

    def repr(self: Self): String {
        return union self {
            case Class(fullName, args) = fullName + "(" + ",".join(args) + ")"
            case Variable(name) = name
        }
    }

    def fullName(self: Self): String {
        return union self {
            case Class(fullName, args) = fullName
            case Variable(name) = name
        }
    }

    def args(self: Self): List[CJIRType] {
        return union self {
            case Class(fullName, args) = args
            case Variable(name) = []
        }
    }
}
