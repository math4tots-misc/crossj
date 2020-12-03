package cj


class MutableMapTest {

    def testEq() {
        {
            val map = MutableMap.of([(1, 2)])
            Assert.equal(map, map)
            Assert.equal(map, MutableMap.of([(1, 2)]))
        }
    }

    def testLiterals() {
        {
            val map = MutableMap.of([(1, 2)])
            Assert.equal(map, @[1: 2])
            Assert.equal(map, MutableMap.of([(1, 2)]))
        }
        Assert.equal(MutableMap[Int, String].empty(), @[:])
        {
            val map = MutableMap.of([(1, 2), (2, 3)])
            Assert.equal(map, @[2: 3, 1: 2])
            Assert.equal(map, MutableMap.of([(2, 3), (1, 2)]))
        }
    }

    def testSample() {
        var map = MutableMap[String, Int].of([])
        Assert.equal(map.size(), 0)
        map.put("hi", 123)
        Assert.equal(map.size(), 1)
        Assert.equal(map.get("hi"), 123)

        map = MutableMap.of([("a", 123), ("b", 234)])
        Assert.equal(map.size(), 2)

        map.put("c", 345)
        Assert.equal(map.size(), 3)
        Assert.that(map.getOrNull("aa").isEmpty())
        Assert.equal(map.get("b"), 234)
        Assert.equal(map.get("a"), 123)
        Assert.equal(map.get("c"), 345)

        # Assert.equal(map.getOrNull("aa"), Nullable.None)
        # Assert.equal(map.getOrNull("a"), Nullable.Some(123))
        # Assert.equal(map.getOrNull("b"), Nullable.Some(234))
        # Assert.equal(map.getOrNull("c"), Nullable.Some(345))
    }

    def testContains() {
        Assert.that("hello" in @["hello": "world"])
        Assert.that("world" not in @["hello": "world"])
        Assert.that("asdf" not in @["hello": "world"])
    }
}
