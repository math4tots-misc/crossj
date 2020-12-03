package cj


class MutableSet[T: Hash]:
        Collection[T],
        Eq,
        Repr if T: Repr {
    val _map: MutableMap[T, Bool]

    def empty(): Self {
        return Self.of([])
    }

    def of(list: List[T]): Self {
        return MutableSet[T].new(MutableMap.of(list.map(t -> (t, true))))
    }

    def size(self: Self): Int {
        return self._map.size()
    }

    def iter(self: Self): Iterator[T] {
        return self._map.keys()
    }

    def __eq(self: Self, other: Self): Bool {
        return self._map == other._map
    }

    if T: Repr {
        def repr(self: Self): String {
            return "MutableSet.of([" + ", ".join(self) + "])"
        }
    }

    def __contains(self: Self, t: T): Bool {
        return t in self._map
    }

    def add(self: Self, t: T) {
        self._map.put(t, true)
    }

    def remove(self: Self, t: T) {
        self._map.remove(t)
    }

    def removeOrFalse(self: Self, t: T): Bool {
        return self._map.removeOrFalse(t)
    }
}
