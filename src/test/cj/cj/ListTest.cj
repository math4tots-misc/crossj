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
        Assert.equal(["hello", "world", "bob"].map(def(s) = s.size()), [5, 5, 3])
    }
}
