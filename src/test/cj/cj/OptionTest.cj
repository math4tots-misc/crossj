package cj

import cj.Option

class OptionTest {
    def testOf() {
        Assert.equal(Option.of(23).get(), 23)
    }
}
