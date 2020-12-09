package cj


native class Iterator[T] : Iterable[T] {
    def map[R](x: Self, f: Fn[R, T]) : Iterator[R]
    def flatMap[I, C: Iterable[I]](x: Self, f: Fn[C, T]) : Iterator[I]
    def filter(self: Self, f: Fn[Bool, T]) : Iterator[T]
    def fold[A](self: Self, acc: A, f: Fn[A, A, T]): A

    def toList(x: Self) : List[T]
    def iter(self: Self) : Iterator[T]

    def next(self: Self): Nullable[T]
}
