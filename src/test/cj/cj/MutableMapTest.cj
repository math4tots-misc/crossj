package cj


class MutableMapTest {

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
}
