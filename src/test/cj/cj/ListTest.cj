package cj

import cj.Assert

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
}
