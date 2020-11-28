package cj

import cj.Assert


class TupleTest {
    def testGet() {
        {
            var tuple = (1 + 7, "hi")
            Assert.equal(tuple.get0(), 8)
            Assert.equal(tuple.get1(), "hi")
        }
        {
            var tuple = (1 + 7, 2.8, "hi")
            Assert.equal(tuple.get0(), 8)
            Assert.equal(tuple.get1(), 2.8)
            Assert.equal(tuple.get2(), "hi")
        }
        {
            var tuple = (1 + 7, "hi", 2.2, "world".size())
            Assert.equal(tuple.get0(), 8)
            Assert.equal(tuple.get1(), "hi")
            Assert.equal(tuple.get2(), 2.2)
            Assert.equal(tuple.get3(), 5)
        }
    }
}
