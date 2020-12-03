package cj


class MapTest {

    def testEq() {
        {
            val map = Map.of([(1, 2)])
            Assert.equal(map, map)
            Assert.equal(map, Map.of([(1, 2)]))
        }
    }

    def testLiterals() {
        {
            val map = Map.of([(1, 2)])
            Assert.equal(map, [1: 2])
            Assert.equal(map, Map.of([(1, 2)]))
        }
        Assert.equal(Map[Int, String].empty(), [:])
        {
            val map = Map.of([(1, 2), (2, 3)])
            Assert.equal(map.size(), 2)
            Assert.equal(map, [2: 3, 1: 2])
            Assert.equal(map, Map.of([(2, 3), (1, 2)]))
        }
    }

    def testHash() {
        {
            val key1 = Map.of([(1, 2)])
            val key2 = Map.of([(1, 3)])
            val key3 = Map.of([(2, 5)])
            val map = MutableMap[Map[Int, Int], String].empty()
            Assert.equal(map.size(), 0)
            map.put(key1, "first")
            Assert.equal(map.size(), 1)
            Assert.equal(map.pairs().toList(), [(Map.of([(1, 2)]), "first")])
            Assert.equal(map.get(key1), "first")
        }
    }
}
