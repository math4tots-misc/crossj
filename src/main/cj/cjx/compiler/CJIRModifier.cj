package cjx.compiler

union CJIRModifier: Repr, Eq {
    case Private
    case Public
    case Native
    case Async
    case Static

    def repr(self: Self): String {
        return union self {
            case Private = "private"
            case Public = "public"
            case Native = "native"
            case Async = "async"
            case Static = "static"
        }
    }

    def __eq(self: Self, other: Self): Bool {
        return self.repr() == other.repr()
    }
}
