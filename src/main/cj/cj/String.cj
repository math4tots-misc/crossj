package cj

native class String: Eq, Ord, Hash, Repr, ToString {
    def __eq(a: Self, b: Self) : Bool
    def __lt(a: Self, b: Self) : Bool

    def __add(a: Self, b: Self) : Self

    def hash(x: Self) : Int

    def repr(x: Self) : String
    def toString(x: Self) : String
}
