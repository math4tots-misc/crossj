package cj

import cj.Assert


class BoolTest {
    def testMisc() {
        Assert.that(true)
        Assert.that(not false)
        Assert.that(not (true and false))
        Assert.that(true and true)
        Assert.that(true and true and true)
        Assert.that(not (true and true and false))
        Assert.that(true or false)

        Assert.that(1 == 1)
        Assert.that(not (1 == 2))
    }
}
