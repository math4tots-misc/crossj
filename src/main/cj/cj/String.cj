package cj

native class String: Eq, Ord, Hash, Repr, ToString, Iterable[Char], ToBool {
    def __eq(a: Self, b: Self) : Bool
    def __lt(a: Self, b: Self) : Bool

    def __add[T: ToString](a: Self, b: T) : Self

    def size(x: Self) : Int

    def hash(x: Self) : Int

    def repr(x: Self) : String
    def toString(x: Self) : String

    def iter(self: Self) : Iterator[Char]

    def join[I: ToString, C: Iterable[I]](self: Self, parts: C): String

    def toBool(self: Self) : Bool

    def builder(): StringBuilder
}
