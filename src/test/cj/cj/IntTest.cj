package cj


import cj.Assert


class IntTest {

    def testUnary() {
        {
            var x = -24
            Assert.equal(x, 0 - 24)
            Assert.equal(-x, 24)
        }
        {
            var x = ~1
            Assert.equal(x, -2)
        }
    }

    def testBitwiseOperators() {
        {
            var x = 2 & 6
            Assert.equal(x, 2)
        }
        Assert.equal(1 | 4 | 8, 1 + 4 + 8)
    }

    def testArithmetic() {
        {
            Assert.equal(5 / 2, 2.5)
            Assert.equal(5 // 2, 2)
        }
    }
}
