package cj

import cj.Assert


class TupleTest {
    def testGet() {
        {
            val tuple = (1 + 7, "hi")
            Assert.equal(tuple.get0(), 8)
            Assert.equal(tuple.get1(), "hi")
        }
        {
            val tuple = (1 + 7, 'c', "hi")
            Assert.equal(tuple.get0(), 8)
            Assert.equal(tuple.get1(), 'c')
            Assert.equal(tuple.get2(), "hi")
        }
        {
            val tuple = (1 + 7, "hi", 2.2, "world".size())
            Assert.equal(tuple.get0(), 8)
            Assert.equal(tuple.get1(), "hi")
            Assert.equal(tuple.get2(), 2.2)
            Assert.equal(tuple.get3(), 5)
        }
    }

    def testAssignment() {
        {
            val (a, b, c) = (1, 2, 3)
            Assert.equal(a, 1)
            Assert.equal(b, 2)
            Assert.equal(c, 3)
        }
        {
            val (x, y) = ('x', "hello")
            Assert.equal(x, 'x')
            Assert.equal(y, "hello")
        }
    }
}
