package cj


native class Iterator[T] {
    def map[R](f: Fn[R, T]) : Iterator[R]
}
