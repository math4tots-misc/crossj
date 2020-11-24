package cj


trait Collection[T] : Iterable[T] {
    def size() : Int
}
