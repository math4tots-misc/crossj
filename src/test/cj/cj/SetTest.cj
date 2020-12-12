package cj


class SetTest {
    def testContains() {
        Assert.that(3 in Set.of([1, 2, 3]))
        Assert.that(not (3 not in Set.of([1, 2, 3])))
        Assert.that(-1 not in Set.of([1, 2, 3]))
    }

    def testBuilder() {
        {
            val builder = Set[String].builder()
            builder.add("a").add("bb")
            builder.add("ccc")
            val set = builder.build()
            Assert.equal(set.size(), 3)
            Assert.that("a" in set)
            Assert.that("aa" not in set)
            Assert.that("bb" in set)
            Assert.that("ccc" in set)
        }
        {
            val builder = Set[Char].builder()
            builder.addAll(['a', 'b', 'c']).add('d')
            val set = builder.build()
            Assert.equal(set.size(), 4)
            Assert.that('a' in set)
            Assert.that('b' in set)
            Assert.that('c' in set)
            Assert.that('z' not in set)
            Assert.that('A' not in set)
        }
        {
            val builder = Set[Int].builder()
            builder.addAll(@[24])
            val set = builder.build()
            Assert.equal(set.size(), 1)
            Assert.equal(set.toString(), "Set.of([24])")
            Assert.equal(set.toList(), [24])
        }
    }
}
