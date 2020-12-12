package cj

native class String: Eq, Ord, Hash, Repr, ToString, Iterable[Char], ToBool {
    def __eq(a: Self, b: Self) : Bool
    def __lt(a: Self, b: Self) : Bool

    def __add[T: ToString](a: Self, b: T) : Self

    ## The number of code units in this string.
    ## For UTF-8 targets, this returns the number of bytes,
    ## For UTF-16 targets, this returns the number of 16-bit code units.
    def size(x: Self) : Int

    ## Gets the code-unit of the given string.
    ## For UTF-8 targets, this returns a number between 0 and 255,
    ## For UTF-16 targets, this returns a number between 0 and 65535
    def codeAt(self: Self, i: Int): Int

    def hash(x: Self) : Int

    def repr(x: Self) : String
    def toString(x: Self) : String

    def iter(self: Self) : Iterator[Char]

    def join[I: ToString, C: Iterable[I]](self: Self, parts: C): String

    def toBool(self: Self) : Bool

    def builder(): StringBuilder
}
