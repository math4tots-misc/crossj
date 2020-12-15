package cj

native class List[T] :
        Collection[T],
        ToList[T],
        ToBool,
        Repr if T : Repr,
        Eq if T : Eq,
        Ord if T : Ord,
        Hash if T : Hash {

    def ofSize(size: Int, f: Fn[T, Int]) : Self

    if T : Eq {
        def __eq(a: Self, b: Self) : Bool
    }

    if T : Hash {
        def hash(x: Self) : Int
    }

    if T : Ord {
        def __lt(a: Self, b: Self) : Bool
    }

    if T : Repr {
        def repr(list: Self) : String
    }

    def get(self: Self, i: Int): T

    def size(x: Self) : Int
    def iter(x: Self) : Iterator[T]
    def __contains(self: Self, t: T): Bool

    def __add(self: Self, other: Self): Self

    def map[R](self: Self, f: Fn[R, T]) : List[R]
    def flatMap[R, C: Iterable[R]](self: Self, f: Fn[C, T]) : List[R]
    def flatten[I](self: List[List[I]]): List[I]
    def filter(self: Self, f: Fn[Bool, T]): Self

    if T : Ord {
        def sorted[C: Iterable[T]](self: C): Self
    }

    def toList(self: Self) : Self
    def toBool(self: Self) : Bool

    def builder() : ListBuilder[T]
}
