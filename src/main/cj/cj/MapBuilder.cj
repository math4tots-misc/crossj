package cj


class MapBuilder[K: Hash, V] {
    var _map: MutableMap[K, V]

    def size(self: Self): Int {
        return self._map.size()
    }

    def put(self: Self, key: K, value: V): Self {
        self._map.put(key, value)
    }

    def getOrNull(self: Self, key: K): Nullable[V] {
        return self._map.getOrNull(key)
    }

    def get(self: Self, key: K): V {
        return self._map.get(key)
    }

    def __contains(self: Self, key: K): Bool {
        return key in self._map
    }

    def build(self: Self): Map[K, V] {
        val map = Map[K, V].new(self._map)
        self._map = @[:]
        return map
    }
}
