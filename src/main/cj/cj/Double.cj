package cj

native class Double : Eq, Ord, Hash {
    def __eq(a: Self, b: Self) : Bool
    def __lt(a: Self, b: Self) : Bool

    def hash(x: Self) : Int
}
