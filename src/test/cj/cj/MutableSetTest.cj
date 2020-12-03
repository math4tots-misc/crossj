package cj


class MutableSetTest {

    def testAdd() {
        {
            val set = MutableSet[String].empty()
            Assert.equal(set.size(), 0)
            set.add("hello")
            Assert.that("hello" in set)
            Assert.equal(set.size(), 1)
            set.add("hello")
            Assert.that("world" not in set)
            Assert.equal(set.size(), 1)
            set.add("world")
            Assert.equal(set.size(), 2)
            Assert.that("world" in set)
        }
    }
}
