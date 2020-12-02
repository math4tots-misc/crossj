package cj

class IntTest {

    def testUnary() {
        {
            val x = -24
            Assert.equal(x, 0 - 24)
            Assert.equal(-x, 24)
        }
        {
            val x = ~1
            Assert.equal(x, -2)
        }
    }

    def testBitwiseOperators() {
        {
            val x = 2 & 6
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

    def testAugAssign() {
        {
            var i = 824
            Assert.equal(i, 824)
            i += 1
            Assert.equal(i, 825)
        }
        {
            var i = 1000
            Assert.equal(i, 1000)
            i *= 2
            Assert.equal(i, 2000)
        }
        {
            var i = 5000
            Assert.equal(i, 5000)
            i -= 284
            Assert.equal(i, 4716)
        }
    }
}
