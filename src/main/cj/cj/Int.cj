package cj

native class Int : Eq, Hash {
    def __eq(a : Self, b : Self) : Bool
    def __lt(a : Self, b : Self) : Bool
    def __hash(x: Self) : Self
    def __add(a : Self, b : Self) : Self
    def __sub(a : Self, b : Self) : Self
    def __mul(a : Self, b : Self) : Self
    def __div(a : Self, b : Self) : Self
}
