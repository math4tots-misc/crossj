package cj

native class Int : Eq, Ord, Hash, Repr, ToDouble, ToChar, ToBool {
    def __eq(a : Self, b : Self) : Bool
    def __lt(a : Self, b : Self) : Bool
    def __le(a : Self, b : Self) : Bool
    def __gt(a : Self, b : Self) : Bool
    def __ge(a : Self, b : Self) : Bool
    def __add(a : Self, b : Self) : Self
    def __sub(a : Self, b : Self) : Self
    def __mul(a : Self, b : Self) : Self
    def __div(a : Self, b : Self) : Double
    def __rem(a: Self, b: Self) : Self
    def __truncdiv(a: Self, b: Self) : Self

    def __neg(x: Self) : Self
    def __pos(x: Self) : Self

    def __invert(x: Self) : Self
    def __and(a: Self, b: Self) : Self
    def __or(a: Self, b: Self) : Self

    def hash(x: Self) : Self

    def toString(x: Self) : String
    def repr(x: Self) : String

    def toDouble(x: Self) : Double

    # Converts an Int to a Char.
    #
    # If the integer is not a valid codepoint, the '\0' character
    # is returned.
    #
    # Otherwise, the character matching the codepoint of the given
    # integer is returned.
    #
    def toChar(self: Self) : Char

    # An Int is truthy iff it is not zero
    def toBool(self: Self) : Bool
}
