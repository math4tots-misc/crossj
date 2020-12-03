package cj

trait Iterable[T] : ToList[T] {
    def iter(x: Self) : Iterator[T]

    def toList(self: Self) : List[T] {
        var builder = List[T].builder()
        for t in self {
            builder.add(t)
        }
        return builder.build()
    }
}
