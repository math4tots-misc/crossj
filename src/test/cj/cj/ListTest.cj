package cj

import cj.Assert

class ListTest {
    def testEq() : Unit {
        # Assert.that([] == [])
        Assert.that([1] != [])
        Assert.that([0] != [])
        Assert.that([1, 2, 3] == [1, 2, 3])
    }

    def testToString() : Unit {
        Assert.equal([5,6,7].toString(), "[5, 6, 7]")
    }
}
