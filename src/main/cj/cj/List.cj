package cj

import cj.Eq
import cj.Hash
import cj.Ord

native class List[T] :
        Iterable[T],
        Eq if T : Eq,
        Hash if T : Hash,
        Ord if T : Ord {

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
