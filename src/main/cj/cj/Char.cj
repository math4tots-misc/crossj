package cj

native class Char : Eq, Ord, Hash, Repr, ToInt {
    def __eq(a: Self, b: Self) : Bool
    def __lt(a: Self, b: Self) : Bool
    def toInt(self: Self) : Int
    def repr(self: Self) : String
    def toString(self: Self) : String
    def hash(self: Self) : Int

    ## true if self is an uppercase ascii letter
    def isUpper(self: Self): Bool

    ## true if self is a lowercase ascii letter
    def isLower(self: Self): Bool

    ## true if self is an ascii letter
    def isLetter(self: Self): Bool

    ## true if self is an ascii digit
    def isDigit(self: Self): Bool

    ## true if self is an ascii letter or digit
    def isLetterOrDigit(self: Self): Bool

    ## true if self is an ascii letter or digit or '_'
    def isWord(self: Self): Bool

    ## Returns the number of code units that this character requires.
    ## On UTF-16 platforms, this is a 1 or 2 depending on whether a surrogate
    ## pair is required.
    ## On UTF-8 platforms, this method may return 1, 2, 3, or 4.
    def codeUnitCount(self: Self): Int
}
