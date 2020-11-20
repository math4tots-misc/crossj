package cj

native class List[T] :
        Iterable[T],
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
}
