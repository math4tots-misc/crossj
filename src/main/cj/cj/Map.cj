package cj


class Map[K: Hash, V]:
        Hash if V: Hash,
        Eq if V: Eq,
        Repr if K: Repr and V: Repr {

    val _map: MutableMap[K, V]

    def empty(): Self {
        return Self.new(MutableMap[K, V].empty())
    }

    def of(pairs: List[Tuple[K, V]]): Self {
        return Self.new(MutableMap[K, V].of(pairs))
    }

    def fromIterable[C: Iterable[Tuple[K, V]]](pairs: C): Self {
        return Self.of(pairs.toList())
    }

    def size(self: Self): Int {
        return self._map.size()
    }

    def keys(self: Self): Iterator[K] {
        return self._map.keys()
    }

    def values(self: Self): Iterator[V] {
        return self._map.values()
    }

    def pairs(self: Self): Iterator[Tuple[K, V]] {
        return self._map.pairs()
    }

    def get(self: Self, key: K): V {
        return self._map.get(key)
    }

    def getOrNull(self: Self, key: K): Nullable[V] {
        return self._map.getOrNull(key)
    }

    def getOrDefault(self: Self, key: K, d: V): V {
        return self.getOrNull(key).getOrDefault(d)
    }

    def getOrElse(self: Self, key: K, f: Fn[V]): V {
        return self.getOrNull(key).getOrElse(f)
    }

    def __contains(self: Self, key: K): Bool {
        return key in self._map
    }

    def __add(self: Self, other: Self): Self {
        return Self.new(self._map + other._map)
    }

    if V: Hash {
        def hash(self: Self): Int {
            return self._map.pairs().fold(
                0, (acc, pair) -> acc + pair.get0().hash() + pair.get1().hash())
        }
    }

    if V: Eq {
        def __eq(self: Self, other: Self): Bool {
            return self._map == other._map
        }
    }

    if K: Repr and V: Repr {
        def repr(self: Self): String {
            if self.size() == 0 {
                return "[:]"
            } else {
                return (
                    "[" +
                    ", ".join(
                        self.pairs().map(kv -> kv.get0().repr() + ":" + kv.get1().repr())) +
                    "]"
                )
            }
        }
    }

    def builder(): MapBuilder[K, V] {
        return MapBuilder[K, V].new(@[:])
    }
}
