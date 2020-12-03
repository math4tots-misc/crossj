package cj

class ListTest {
    def testEq() {
        # Assert.that([] == [])
        Assert.that([1] != [])
        Assert.that([0] != [])
        Assert.that([1, 2, 3] == [1, 2, 3])
    }

    def testToString() {
        Assert.equal([5,6,7].toString(), "[5, 6, 7]")
    }

    def testMap() {
        Assert.equal(["hello", "world", "bob"].map(s -> s.size()), [5, 5, 3])
    }

    def testIter() {
        Assert.equal([1, 2, 3].iter().map(x -> x * x).toList(), [1, 4, 9])
        Assert.equal([1, 2, 3].iter().flatMap(x -> [x, x * x]).toList(), [1, 1, 2, 4, 3, 9])
    }

    def testMutable() {
        Assert.equal(@[Int], @[Int])
        Assert.equal(@[Int], @[])
        Assert.equal(@[1, 2, 3], @[1, 2, 3])
        Assert.that(@[1, 2, 3] == @[1, 2, 3])
        Assert.that(@[1, 2, 3] != @[3, 2, 1])
        Assert.that(@[1, 2, 3] != @[1, 2, 3, 4])
        Assert.that(@[1, 2, 3, 4] != @[1, 2, 3])
        Assert.equal(@[1, 2, 3].toList(), [1, 2, 3])
    }

    def testFor() {
        val strings = @[String]
        for x in ["hello", "world", "bob"] {
            strings.push(x)
        }
        Assert.equal(strings, @["hello", "world", "bob"])
    }

    def testNewline() {
        var list = [
            1,
            2
            ,
            3,
        ]
        Assert.equal(list, [1, 2, 3])
    }

    def testHashCode() {
        # For better or worse, the hashes should more or less match Java's
        # TODO: Consider whether hashes should vary
        Assert.equal((5).hash(), 5)
        Assert.equal([1, 2, 3].hash(), 30817)
    }

    def testTruthy() {
        {
            val emptyList : List[Int] = []
            Assert.equal(emptyList.toBool(), false)
            Assert.equal([1, 2, 3].toBool(), true)
        }
    }

    def testContains() {
        Assert.that(3 in [1, 2, 3])
        Assert.that(not (20 in [1, 2, 3]))
        Assert.that(not (3 not in [1, 2, 3]))
        Assert.that(20 not in [1, 2, 3])
    }
}
