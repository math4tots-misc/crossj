package cj

native class MutableList[T] :
        Ord if T : Ord,
        Eq if T : Eq {

    if T : Eq {
        def __eq(a: Self, b: Self) : Bool
    }

    if T : Ord {
        def __lt(a: Self, b: Self) : Bool
    }

    def size(list: Self) : Int
    def get(list: Self, i: Int) : T
    def set(list: Self, i: Int, t: T) : Unit
    def push(list: Self, t: T) : Unit
    def pop(list: Self) : T
}
