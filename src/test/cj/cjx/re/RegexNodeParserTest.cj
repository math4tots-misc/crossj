package cjx.re

import cjx.re.RegexNode

class RegexNodeParserTest {

    def testSample() {
        {
            val re = RegexNode.fromPattern("abc")
            Assert.equal(re.repr(), "Try.Ok(Cat(Cat(Letter('a'), Letter('b')), Letter('c')))")
        }
        {
            val re = RegexNode.fromPattern("a|bc")
            Assert.equal(re.repr(), "Try.Ok(Or(Letter('a'), Cat(Letter('b'), Letter('c'))))")
        }
        {
            val re = RegexNode.fromPattern("(a|b)c")
            Assert.equal(re.repr(), "Try.Ok(Cat(Or(Letter('a'), Letter('b')), Letter('c')))")
        }
        {
            val re = RegexNode.fromPattern("(a|b)*c")
            Assert.equal(
                re.repr(), "Try.Ok(Cat(Star(Or(Letter('a'), Letter('b'))), Letter('c')))")
        }
    }
}
