package cj

trait Repr : ToString {
    def repr(x: Self) : String

    def toString(x: Self) : String {
        return x.repr()
    }
}
