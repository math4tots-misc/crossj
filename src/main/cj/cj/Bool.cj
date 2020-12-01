package cj

native class Bool : Eq, Ord, Hash, Repr, ToBool {
    def __eq(a: Self, b: Self) : Bool
    def __lt(a: Self, b: Self) : Bool

    def hash(x: Self) : Int
    def toBool(x: Self) : Bool
    def repr(self: Self) : String
}
