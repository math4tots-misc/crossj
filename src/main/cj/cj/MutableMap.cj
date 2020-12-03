package cj


class MutableMap[K : Hash, V]:
        Eq if V: Eq,
        Repr if K: Repr and V: Repr {
    private var _siz : Int
    private var _list : MutableList[MutableList[Tuple[Int, K, V]]]

    def empty() : Self {
        val self = Self.new(0, @[MutableList[Tuple[Int, K, V]]])
        return self
    }

    def of(pairs: List[Tuple[K, V]]) : Self {
        val self = Self.empty()
        for (k, v) in pairs {
            self.put(k, v)
        }
        return self
    }

    def _rehash(self: Self, newCap: Int) {
        if (self._list.size() < newCap) {
            val oldList = self._list
            self._siz = 0
            self._list = MutableList.ofSize(newCap, i -> @[])
            for bucket in oldList {
                for triple in bucket {
                    self._insertNoRehash(triple)
                }
            }
        }
    }

    def _insertNoRehash(self: Self, triple: Tuple[Int, K, V]) {
        val hash = triple.get0()
        val key = triple.get1()
        val list = self._list
        val index = Self._getIndex(hash, list.size())
        val bucket = list.get(index)
        var i = 0
        while (i < bucket.size()) {
            val entry = bucket.get(i)
            if (hash == entry.get0() and entry.get1() == key) {
                bucket.set(i, triple)
                return ()
            }
            i += 1
        }
        self._siz += 1
        bucket.add(triple)
    }

    def _checkForRehashBeforeInsert(self: Self) {
        if (self._list.size() == 0) {
            self._rehash(16)
        } else if (4 * self._siz >= 3 * self._list.size()) {
            self._rehash(self._list.size() * 2)
        }
    }

    def size(self: Self): Int {
        return self._siz
    }

    def put(self: Self, key: K, value: V) {
        self._checkForRehashBeforeInsert()
        val hash = key.hash()
        self._insertNoRehash((hash, key, value))
    }

    def _getTriple(self: Self, key: K) : Nullable[Tuple[Int, K, V]] {
        return self._getTripleWithHash(key, key.hash())
    }

    def _getTripleWithHash(self: Self, key: K, hash: Int) : Nullable[Tuple[Int, K, V]] {
        if self._siz == 0 {
            return Nullable.None
        }
        val list = self._list
        val index = Self._getIndex(hash, list.size())
        val bucket = list.get(index)
        for triple in bucket {
            if triple.get0() == hash and triple.get1() == key {
                return Nullable.Some(triple)
            }
        }
        return Nullable.None
    }

    def getOrInsert(self: Self, key: K, f: Fn[V]): V {
        union self._getTriple(key) {
            case Some(triple) {
                return triple.get2()
            }
            case None {
                val value = f.call()
                self.put(key, value)
                return value
            }
        }
    }

    def getOrNull(self: Self, key: K) : Nullable[V] {
        return self._getTriple(key).map(triple -> triple.get2())
    }

    def __contains(self: Self, key: K): Bool {
        return self._getTriple(key).isPresent()
    }

    def get(self: Self, key: K): V {
        return self._getTriple(key).get().get2()
    }

    def _removeTriple(self: Self, key: K) : Nullable[Tuple[Int, K, V]] {
        if (self._list.size() == 0) {
            return Nullable.None
        }
        val hash = key.hash()
        val index = Self._getIndex(hash, self._list.size())
        val bucket = self._list.get(index)
        var i = 0
        while (i < bucket.size()) {
            val triple = bucket.get(i)
            if triple.get0() == hash and triple.get1() == key {
                self._siz -= 1
                return Nullable.Some(bucket.removeIndex(i))
            }
            i += 1
        }
        return Nullable.None
    }

    def removeOrFalse(self: Self, key: K): Bool {
        return self._removeTriple(key).isPresent()
    }

    def remove(self: Self, key: K) {
        if (self._removeTriple(key).isEmpty()) {
            IO.panic("Key " + key.repr() + " not found in this map")
        }
    }

    def keys(self: Self) : Iterator[K] {
        return self._list.iter().flatMap(bucket -> bucket.map(triple -> triple.get1()))
    }

    def values(self: Self) : Iterator[V] {
        return self._list.iter().flatMap(bucket -> bucket.map(triple -> triple.get2()))
    }

    def pairs(self: Self): Iterator[Tuple[K, V]] {
        return self._list.iter().flatMap(
            bucket -> bucket.map(triple -> (triple.get1(), triple.get2())))
    }

    def _getIndex(hash: Int, size: Int) : Int {
        return (hash % size + size) % size
    }

    if V: Eq {
        def __eq(self: Self, other: Self): Bool {
            if (self._siz != other._siz) {
                return false
            } else if (self._siz == 0) {
                return true
            }
            for (hash, k, v) in self._list.iter().flatMap(bucket -> bucket) {
                union other._getTripleWithHash(k, hash) {
                    case Some(otherTriple) {
                        if v != otherTriple.get2() {
                            return false
                        }
                    }
                    case None {
                        return false
                    }
                }
            }
            return true
        }
    }

    if K: Repr and V: Repr {
        def repr(self: Self): String {
            if self._siz == 0 {
                return "@[:]"
            } else {
                return (
                    "@[" +
                    ",".join(
                        self.pairs().map(kv -> kv.get0().repr() + ": " + kv.get1().repr())) +
                    "]"
                )
            }
        }
    }
}
