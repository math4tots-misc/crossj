package cj

trait Iterable[T] {
    def iter(x: Self) : Iterator[T]
}
