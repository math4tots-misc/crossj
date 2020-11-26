package cj


native class Iterator[T] {
    def map[R](x: Self, f: Fn[R, T]) : Iterator[R]

    def flatMap[I, C: Iterable[I]](x: Self, f: Fn[C, T]) : Iterator[I]

    def toList(x: Self) : List[T]
}
