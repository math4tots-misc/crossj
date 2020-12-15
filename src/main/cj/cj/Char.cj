package cj

native class Char : Eq, Ord, Hash, Repr, ToInt {
    def __eq(a: Self, b: Self) : Bool
    def __lt(a: Self, b: Self) : Bool
    def toInt(self: Self) : Int
    def repr(self: Self) : String
    def toString(self: Self) : String
    def hash(self: Self) : Int

    ## Returns the number of code units that this character requires.
    ## On UTF-16 platforms, this is a 1 or 2 depending on whether a surrogate
    ## pair is required.
    ## On UTF-8 platforms, this method may return 1, 2, 3, or 4.
    def codeUnitCount(self: Self): Int
}
