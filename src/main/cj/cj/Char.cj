package cj

native class Char : Eq, Ord, Hash, Repr, ToInt {
    def __eq(a: Self, b: Self) : Bool
    def __lt(a: Self, b: Self) : Bool
    def toInt(self: Self) : Int
    def repr(self: Self) : String
    def toString(self: Self) : String
    def hash(self: Self) : Int
}
