package cj

trait Ord : Eq {
    def __lt(a: Self, b: Self) : Bool

    def __gt(a: Self, b: Self) : Bool {
        return b < a
    }

    def __ge(a: Self, b: Self) : Bool {
        return not (a < b)
    }

    def __le(a: Self, b: Self) : Bool {
        return not (b < a)
    }
}
