package cj

class DoubleTest {
    def testPow() {
        Assert.equal(2.0 ** 3.0, 8.0)
    }

    def testToInt() {
        Assert.equal((2.9).toInt(), 2)
        Assert.equal((2.0).toInt(), 2)
        Assert.equal((2.2).toInt(), 2)
        Assert.equal((3.2).toInt(), 3)
        Assert.equal((-2.9).toInt(), -2)
        Assert.equal((-2.0).toInt(), -2)
        Assert.equal((-2.2).toInt(), -2)
        Assert.equal((-3.2).toInt(), -3)
    }

    def testArithmetic() {
        {
            Assert.equal(5.0 / 2.5, 2.0)
            Assert.equal(5.0 // 2.5, 2)
            Assert.equal(5.0 // 2.6, 1)
        }
    }
}
