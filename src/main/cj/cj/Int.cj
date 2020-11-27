package cj

native class Int : Eq, Ord, Hash, Repr {
    def __eq(a : Self, b : Self) : Bool
    def __lt(a : Self, b : Self) : Bool
    def __add(a : Self, b : Self) : Self
    def __sub(a : Self, b : Self) : Self
    def __mul(a : Self, b : Self) : Self
    def __div(a : Self, b : Self) : Double
    def __mod(a: Self, b: Self) : Self
    def __floordiv(a: Self, b: Self) : Self

    def __neg(x: Self) : Self
    def __pos(x: Self) : Self

    def __invert(x: Self) : Self
    def __and(a: Self, b: Self) : Self
    def __or(a: Self, b: Self) : Self

    def hash(x: Self) : Self

    def toString(x: Self) : String
    def repr(x: Self) : String
}
