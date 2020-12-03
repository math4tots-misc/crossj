package cj


class Set[T: Hash]:
        Collection[T],
        Hash,
        Eq,
        Repr if T: Repr {

    val _map: MutableMap[T, Bool]

    def of(list: List[T]): Self {
        return Set[T].new(MutableMap.of(list.map(t -> (t, true))))
    }

    def iter(self: Self): Iterator[T] {
        return self._map.keys()
    }

    def size(self: Self): Int {
        return self._map.size()
    }

    def __contains(self: Self, t: T): Bool {
        return t in self._map
    }

    def hash(self: Self): Int {
        return self.iter().fold(0, (acc, t) -> acc + t.hash())
    }

    def __eq(self: Self, other: Self): Bool {
        return self._map == other._map
    }

    if T: Repr {
        def repr(self: Self): String {
            return "Set.of([" + ", ".join(self) + "])"
        }
    }

    def builder(): SetBuilder[T] {
        return SetBuilder[T].new(MutableMap[T, Bool].empty())
    }
}
