package cj

native class Double : Eq, Ord, Hash, Repr, ToInt {
    def __eq(a : Self, b : Self) : Bool
    def __lt(a : Self, b : Self) : Bool
    def __gt(a : Self, b : Self) : Bool
    def __le(a : Self, b : Self) : Bool
    def __ge(a : Self, b : Self) : Bool
    def __add(a : Self, b : Self) : Self
    def __sub(a : Self, b : Self) : Self
    def __mul(a : Self, b : Self) : Self
    def __div(a : Self, b : Self) : Self
    def __truncdiv(a: Self, b: Self) : Int
    def __pow(a: Self, b: Self) : Self
    def __neg(self: Self) : Self
    def __pos(self: Self) : Self

    def hash(x: Self) : Self
    def repr(x: Self) : String

    # Returns an integer by truncating the double
    def toInt(x: Self) : Int
}
