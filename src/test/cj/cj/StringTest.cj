package cj

import cj.Assert


class StringTest {

    def testCharToInt() {
        Assert.equal('a'.toInt(), 97)
        Assert.equal('A'.toInt(), 65)
        Assert.equal('한'.toInt(), 54620)
    }
}
