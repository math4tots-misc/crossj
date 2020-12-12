package cj


class SetBuilder[T: Hash] {
    var _map: MutableMap[T, Bool]

    def add(self: Self, t: T): Self {
        self._map.put(t, true)
        return self
    }

    def addAll[C: Iterable[T] & Repr](self: Self, ts: C): Self {
        for t in ts {
            self.add(t)
        }
        return self
    }

    def __contains(self: Self, key: T): Bool {
        return key in self._map
    }

    def build(self: Self): Set[T] {
        val set = Set[T].new(self._map)
        self._map = @[:]
        return set
    }
}
