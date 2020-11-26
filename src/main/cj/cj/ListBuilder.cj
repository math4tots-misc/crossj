package cj


native class ListBuilder[T] {
    def push(self: Self, x: T) : ListBuilder[T]
    def build(self: Self) : List[T]
}
