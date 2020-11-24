package cj

import cj.Assert

class SampleTest {
    def testSample() : Unit {
        Assert.that(true)
        Assert.equal(2, 2)
    }

    def testAnotherSample() : Unit {
        Assert.withMessage(not false, "'not false' is not true")
        Assert.equal("hi", "hi")
    }
}
