package cj

native class MutableList[T] :
        Collection[T],
        ToBool,
        Repr if T : Repr,
        Ord if T : Ord,
        Eq if T : Eq {

    def ofSize(size: Int, f: Fn[T, Int]): MutableList[T]

    def fromIterable[I: Iterable[T]](iterable: I): Self

    if T : Repr {
        def repr(x: Self) : String
    }

    if T : Eq {
        def __eq(a: Self, b: Self) : Bool
    }

    if T : Ord {
        def __lt(a: Self, b: Self) : Bool
    }

    def iter(self: Self) : Iterator[T]
    def size(list: Self) : Int
    def get(list: Self, i: Int) : T
    def set(list: Self, i: Int, t: T) : Unit
    def last(list: Self): T
    def add(list: Self, t: T) : Unit
    def addAll[C: Iterable[T]](list: Self, ts: C): Unit
    def pop(list: Self) : T
    def toBool(self: Self) : Bool

    def map[R](self: Self, f: Fn[R, T]): MutableList[R]
    def flatMap[R, C: Iterable[R]](self: Self, f: Fn[C, T]): MutableList[R]
    def filter(self: Self, f: Fn[Bool, T]): MutableList[T]

    def removeIndex(self: Self, i: Int): T
    def swap(self: Self, i: Int, j: Int)

    def cut(self: Self, start: Int, end: Int): Self
    def cutFrom(self: Self, start: Int): Self

    ## Treats the data in this list as the data for a 'List[T]'.
    ## This is fast, but unsafe if there are multiple references to this
    ## MutableList.
    def toListUnsafe(self: Self): List[T]
}
