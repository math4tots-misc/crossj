package cjx.re

import cjx.re.Regex

class RegexTest {
    def testMisc() {
        {
            val re = Regex.fromPatterns(["abc"]).get()
            IO.println(re.inspect())
            Assert.withMessage(re.matches("abc"), "match abc -> abc")
            Assert.withMessage(re.matches("asdfabc"), "match abc -> asdfabc")
        }
    }
}
