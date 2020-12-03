package cj


class MapBuilder[K: Hash, V] {
    val _map: MutableMap[K, V]

    def put(self: Self, key: K, value: V): Self {
        self._map.put(key, value)
    }

    def build(self: Self): Map[K, V] {
        val map = Map[K, V].new(self._map)
        self._map = @[:]
        return map
    }
}
