package cj

native class ListBuilder[T] {
    def add(self: Self, x: T) : ListBuilder[T]
    def addAll[C: Iterable[T]](self: Self, ts: C): ListBuilder[T]
    def build(self: Self) : List[T]
}
