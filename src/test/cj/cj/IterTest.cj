package cj

class IterTest {
    def testFilter() {
        {
            val list = [1, 2, 3, 4, 5, 6].iter().filter(x -> x % 2 == 0).toList()
            Assert.equal(list, [2, 4, 6])
        }
    }

    def testForWithTupleAssignment() {
        {
            var ret = @[Tuple[Int, Int]]
            for (a, b) in [(1, 2), (3, 4)] {
                ret.add((b, a))
            }
            Assert.equal(ret, @[(2, 1), (4, 3)])
        }
    }
}
