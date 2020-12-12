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

    def toSet(self: Self): Set[T] {
        var builder = Set[T].builder()
        for t in self {
            builder.add(t)
        }
        return builder.build()
    }

    def toMap[K: Hash, V, S: Iterable[Tuple[K, V]]](self: S): Map[K, V] {
        var builder = Map[K, V].builder()
        for (k, v) in self {
            builder.put(k, v)
        }
        return builder.build()
    }

    def toMutableList(self: Self): MutableList[T] {
        val ret = @[T]
        for t in self {
            ret.add(t)
        }
        return ret
    }
}
