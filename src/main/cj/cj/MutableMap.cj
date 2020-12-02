package cj


class MutableMap[K : Hash, V] {
    private var _siz : Int
    private var _list : MutableList[MutableList[Tuple[Int, K, V]]]

    def _newEmpty() : Self {
        val self = Self.new(0, @[MutableList[Tuple[Int, K, V]]])
        return self
    }

    def of(pairs: List[Tuple[K, V]]) : Self {
        val self = Self._newEmpty()
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
            }
            i = i + 1
        }
        self._siz = self._siz + 1
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

    def _getTriple(self: Self, key: K) : Option[Tuple[Int, K, V]] {
        return self._getTripleWithHash(key, key.hash())
    }

    def _getTripleWithHash(self: Self, key: K, hash: Int) : Option[Tuple[Int, K, V]] {
        if self._siz == 0 {
            return Option.None
        }
        val list = self._list
        val index = Self._getIndex(hash, list.size())
        val bucket = list.get(index)
        for triple in bucket {
            if triple.get0() == hash and triple.get1() == key {
                return Option.Some(triple)
            }
        }
        return Option.None
    }

    def getOrInsert(self: Self, key: K, f: Fn[V]): V {
        switch union self._getTriple(key) {
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

    def getOption(self: Self, key: K) : Option[V] {
        return self._getTriple(key).map(triple -> triple.get2())
    }

    def get(self: Self, key: K): V {
        return self._getTriple(key).get().get2()
    }

    def _getIndex(hash: Int, size: Int) : Int {
        return (hash % size + size) % size
    }
}
