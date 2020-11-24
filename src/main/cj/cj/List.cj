package cj


native class List[T] :
        Collection[T],
        Repr if T : Repr,
        ToString if T : Repr,
        Eq if T : Eq,
        Ord if T : Ord,
        Hash if T : Hash {

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
        def toString(list: Self) : String
    }

    def map[R](list: Self, f: Fn[R, T]) : List[R]

    def flatMap[R, C: Iterable[R]](list: Self, f: Fn[C, T]) : List[R]

    def size(x: Self) : Int
}
