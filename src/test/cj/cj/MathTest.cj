package cj



class MathTest {
    def testConst() {
        Assert.approximatelyEqual(Math.pi, 3.141592653589793)
    }

    def testExtrema() {
        Assert.equal(Math.min("abc", "def"), "abc")
        Assert.equal(Math.max("abc", "def"), "def")
        Assert.equal(Math.maxOf([1.0, 2, 3]), 3.0);
        Assert.equal(Math.maxOf([1.0, 2, -13.0]), 2.0);
        Assert.equal(Math.minOf([1.0, 2, 3]), 1.0);
        Assert.equal(Math.minOf([1.0, 2, -13.0]), -13.0);
        Assert.equal(Math.maxOf([1, 2, 3]), 3);
        Assert.equal(Math.maxOf([1, 2, -13]), 2);
        Assert.equal(Math.minOf([1, 2, 3]), 1);
        Assert.equal(Math.minOf([1, 2, -13]), -13);
    }

    def testRounding() {
        Assert.equal(Math.ceil(0.5), 1)
        Assert.equal(Math.floor(0.5), 0)
    }

    def testPow() {
        Assert.approximatelyEqual(Math.sqrt(2) * Math.sqrt(2), 2)
    }

    def testTrig() {
        # Just a barebones check calling each trig function at least once
        val pi = Math.pi
        Assert.approximatelyEqual(Math.sin(pi / 4), 1.0 / Math.sqrt(2))
        Assert.approximatelyEqual(Math.cos(pi / 4), 1.0 / Math.sqrt(2))
        Assert.approximatelyEqual(Math.tan(pi / 4), 1.0)
        Assert.approximatelyEqual(Math.asin(Math.sin(pi / 4)), pi / 4)
        Assert.approximatelyEqual(Math.acos(Math.cos(pi / 4)), pi / 4)
        Assert.approximatelyEqual(Math.atan(Math.tan(pi / 4)), pi / 4)
        Assert.approximatelyEqual(Math.atan2(4, 4), pi / 4)
    }
}
