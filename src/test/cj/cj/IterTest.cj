package cj

import cj.Assert


class IterTest {
    def testFilter() {
        {
            var list = [1, 2, 3, 4, 5, 6].iter().filter(x -> x % 2 == 0).toList()
            Assert.equal(list, [2, 4, 6])
        }
    }
}
