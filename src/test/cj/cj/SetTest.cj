package cj


class SetTest {
    def testContains() {
        Assert.that(3 in Set.of([1, 2, 3]))
        Assert.that(not (3 not in Set.of([1, 2, 3])))
        Assert.that(-1 not in Set.of([1, 2, 3]))
    }
}
