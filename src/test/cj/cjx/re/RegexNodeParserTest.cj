package cjx.re

import cjx.re.RegexNodeParser
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

    def testCharSet() {
        {
            Assert.withMessage(RegexNodeParser._isRangeableLetter('0'.toInt()), "0 is rangeable")
            Assert.withMessage(RegexNodeParser._isRangeableLetter('2'.toInt()), "2 is rangeable")
            Assert.withMessage(RegexNodeParser._isRangeableLetter('9'.toInt()), "9 is rangeable")
            Assert.withMessage(RegexNodeParser._isRangeableLetter('a'.toInt()), "a is rangeable")
            Assert.withMessage(RegexNodeParser._isRangeableLetter('b'.toInt()), "b is rangeable")
            Assert.withMessage(RegexNodeParser._isRangeableLetter('z'.toInt()), "z is rangeable")
            Assert.withMessage(RegexNodeParser._isRangeableLetter('A'.toInt()), "A is rangeable")
            Assert.withMessage(RegexNodeParser._isRangeableLetter('B'.toInt()), "B is rangeable")
            Assert.withMessage(RegexNodeParser._isRangeableLetter('Z'.toInt()), "Z is rangeable")
        }
    }
}
