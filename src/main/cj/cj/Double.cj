package cj

native class Double : Eq, Ord, Hash, Repr {
    def __eq(a : Self, b : Self) : Bool
    def __lt(a : Self, b : Self) : Bool
    def __gt(a : Self, b : Self) : Bool
    def __le(a : Self, b : Self) : Bool
    def __ge(a : Self, b : Self) : Bool
    def __add(a : Self, b : Self) : Self
    def __sub(a : Self, b : Self) : Self
    def __mul(a : Self, b : Self) : Self
    def __div(a : Self, b : Self) : Self
    def __floordiv(a: Self, b: Self) : Int
    def __pow(a: Self, b: Self) : Self

    def hash(x: Self) : Self
    def repr(x: Self) : String
}
