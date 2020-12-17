package cjx.re

import cjx.re.RegexNode
import cjx.re.NFA
import cjx.re.Regex

class RegexTest {
    def testMisc() {
        {
            val re = Regex.fromPatterns(["abc"]).get()
            Assert.withMessage(re.matches("abc"), "match abc -> abc")
            Assert.withMessage(not re.matches("asdfabc"), "match abc -> asdfabc")
        }
    }

    def testMatchToFirstUnmatch() {
        val re = Regex.fromPatterns(["ab*x", "yy*", "z"]).get()
        val matcher = re.matcher("abbxyyyzzabbbbxaa")
        Assert.that(matcher.match())
        Assert.equal(matcher.matchIndex(), 0)
        Assert.equal(matcher.matchText(), "abbx")
        Assert.that(matcher.match())
        Assert.equal(matcher.matchIndex(), 1)
        Assert.equal(matcher.matchText(), "yyy")
        Assert.that(matcher.match())
        Assert.equal(matcher.matchIndex(), 2)
        Assert.equal(matcher.matchText(), "z")
        Assert.that(matcher.match())
        Assert.equal(matcher.matchIndex(), 2)
        Assert.equal(matcher.matchText(), "z")
        Assert.that(matcher.match())
        Assert.equal(matcher.matchIndex(), 0)
        Assert.equal(matcher.matchText(), "abbbbx")
        Assert.that(not matcher.match())
        Assert.equal(matcher.matchIndex(), -1)
    }

    def testMatchToEnd() {
        val re = Regex.fromPatterns(["aa*", "bb*", "cc*"]).get()
        var matcher = re.matcher("aaaaabbbc")
        Assert.that(matcher.match())
        Assert.equal(matcher.matchIndex(), 0)
        Assert.equal(matcher.matchText(), "aaaaa")
        Assert.that(matcher.match())
        Assert.equal(matcher.matchIndex(), 1)
        Assert.equal(matcher.matchText(), "bbb")
        Assert.that(matcher.match())
        Assert.equal(matcher.matchIndex(), 2)
        Assert.equal(matcher.matchText(), "c")
        Assert.that(not matcher.match())
    }

    def testMatchStar() {
        var re = Regex.fromPatterns(["x*"]).get()
        Assert.that(re.matches(""))
        Assert.that(re.matches("x"))
        Assert.that(re.matches("xx"))
        Assert.that(not re.matches("y"))
    }

    def testMatchPlus() {
        var re = Regex.fromPatterns(["x+"]).get()
        Assert.that(not re.matches(""))
        Assert.that(re.matches("x"))
        Assert.that(re.matches("xx"))
        Assert.that(not re.matches("y"))
    }

    def testEmptyMatch() {
        var re = Regex.fromPatterns(["x*"]).get()
        var matcher = re.matcher("xxx")
        Assert.that(matcher.match())
        Assert.equal(matcher.matchText(), "xxx")
        Assert.that(matcher.match())
        Assert.equal(matcher.matchText(), "")
    }

    def testMisc() {
        {
            var re = Regex.fromPatterns(["aaa|bb"]).get()
            Assert.that(not re.matches(""))
            Assert.that(not re.matches("aa"))
            Assert.that(not re.matches("b"))
            Assert.that(re.matches("aaa"))
            Assert.that(re.matches("bb"))
        }
        {
            var re = Regex.fromPatterns(["a(xy)?b"]).get()
            Assert.that(re.matches("ab"))
            Assert.that(re.matches("axyb"))
            Assert.that(not re.matches(""))
            Assert.that(not re.matches("axb"))
            Assert.that(not re.matches("axyxyb"))
        }
        {
            var re = Regex.fromPatterns(["a\\?"]).get()
            Assert.that(re.matches("a?"))
            Assert.that(not re.matches(""))
            Assert.that(not re.matches("a"))
            Assert.that(not re.matches("a\\?"))
        }
        {
            var re = Regex.fromPatterns([
                "abc|(01|2|3)+|(x|y|z)+",
                "777|888|999",
                "(j*|(rt+)+)*"
            ]).get()
            var matcher = re.matcher("abc7770101888jrtrtttjjabcxxyzz")
            Assert.that(matcher.match())
            Assert.equal(matcher.matchIndex(), 0)
            Assert.equal(matcher.matchText(), "abc")
            Assert.equal(matcher.walker().tell(), 3)
            Assert.that(matcher.match())
            Assert.equal(matcher.matchIndex(), 1)
            Assert.equal(matcher.matchText(), "777")
            Assert.that(matcher.match())
            Assert.equal(matcher.matchIndex(), 0)
            Assert.equal(matcher.matchText(), "0101")
            Assert.that(matcher.match())
            Assert.equal(matcher.matchIndex(), 1)
            Assert.equal(matcher.matchText(), "888")
            Assert.that(matcher.match())
            Assert.equal(matcher.matchIndex(), 2)
            Assert.equal(matcher.matchText(), "jrtrtttjj")
            Assert.that(matcher.match())
            Assert.equal(matcher.matchIndex(), 0)
            Assert.equal(matcher.matchText(), "abc")
            Assert.that(matcher.match())
            Assert.equal(matcher.matchIndex(), 0)
            Assert.equal(matcher.matchText(), "xxyzz")

            # empty match ... gets stuck here
            Assert.that(matcher.match())
            Assert.equal(matcher.matchIndex(), 2)
            Assert.equal(matcher.matchText(), "")
            Assert.that(matcher.match())
            Assert.equal(matcher.matchIndex(), 2)
            Assert.equal(matcher.matchText(), "")
        }
    }

    def testMultiway() {
        {
            var re = Regex.fromPatterns([
                "1|2|3|4|5|6|7|8|9|0",
                "a|b|c|d|e|f|g"
            ]).get()
            Assert.that(not re.matches(""))
            Assert.that(re.matches("2"))
            Assert.that(re.matcher("24").match())
            Assert.that(re.matches("a"))
            Assert.that(re.matcher("a").match())
            Assert.that(re.matcher("abb").match())
        }
        {
            var re = Regex.fromPatterns([
                "(1|2|3|4|5|6|7|8|9|0)+",
                "(a|b|c|d|e|f|g)+"
            ]).get()
            Assert.that(not re.matches(""))
            Assert.that(re.matches("2"))
            Assert.that(re.matcher("24").match())
            Assert.that(re.matches("a"))
            Assert.that(re.matcher("a").match())
            Assert.that(re.matcher("abb").match())

            var matcher = re.matcher("224abc99")
            Assert.that(matcher.match())
            Assert.equal(matcher.matchIndex(), 0)
            Assert.equal(matcher.matchText(), "224")
            Assert.that(matcher.match())
            Assert.equal(matcher.matchIndex(), 1)
            Assert.equal(matcher.matchText(), "abc")
            Assert.that(matcher.match())
            Assert.equal(matcher.matchIndex(), 0)
            Assert.equal(matcher.matchText(), "99")
            Assert.that(not matcher.match())
        }
    }

    def testCharacterClasses() {
        {
            var re = Regex.fromPatterns(["a\\d+"]).get()
            Assert.that(not re.matches(""))
            Assert.that(not re.matches("a"))
            Assert.that(not re.matches("55"))
            Assert.that(not re.matches("b09876123445"))
            Assert.that(not re.matches("bb"))
            Assert.that(re.matches("a1"))
            Assert.that(re.matches("a23"))
            Assert.that(re.matches("a09876123445"))
        }
        {
            var re = Regex.fromPatterns(["\\w\\d+"]).get()
            Assert.that(not re.matches(""))
            Assert.that(not re.matches("a"))
            Assert.that(not re.matches("bb"))
            Assert.that(re.matches("b09876123445"))
            Assert.that(re.matches("55"))
            Assert.that(re.matches("a1"))
            Assert.that(re.matches("a23"))
            Assert.that(re.matches("a09876123445"))
            Assert.that(re.matches("_09876123445"))
        }
        {
            var re = Regex.fromPatterns(["[adf]+"]).get()
            Assert.that(not re.matches(""))
            Assert.that(not re.matches("b"))
            Assert.that(not re.matches("c"))
            Assert.that(not re.matches("xx"))
            Assert.that(re.matches("d"))
            Assert.that(re.matches("fdaa"))
            Assert.that(re.matches("ddafd"))
        }
        {
            var re = Regex.fromPatterns(["[d-z]+"]).get()
            Assert.that(not re.matches(""))
            Assert.that(not re.matches("b"))
            Assert.that(not re.matches("c"))
            Assert.that(re.matches("dd"))
            Assert.that(re.matches("d"))
            Assert.that(not re.matches("fdaa"))
            Assert.that(re.matches("fd"))
            Assert.that(re.matches("ddxfd"))
            Assert.that(not re.matches("-"))
        }
        {
            var re = Regex.fromPatterns(["[d-z\\s]+"]).get()
            Assert.that(not re.matches(""))
            Assert.that(not re.matches("b"))
            Assert.that(not re.matches("c"))
            Assert.that(re.matches("dd"))
            Assert.that(re.matches("d"))
            Assert.that(not re.matches("fdaa"))
            Assert.that(re.matches("fdxx  qwer"))
            Assert.that(re.matches("fd"))
            Assert.that(re.matches("ddxfd"))
            Assert.that(not re.matches("-"))
        }
        {
            var re = Regex.fromPatterns(["[d-z\\s-]+"]).get()
            Assert.that(re.matches("ddxfd"))
            Assert.that(re.matches("-"))
            Assert.that(re.matches("d-d"))
        }
        {
            # should match any one character
            var re = Regex.fromPatterns(["."]).get()
            Assert.that(not re.matches(""))
            Assert.that(re.matches("x"))
            Assert.that(re.matches("."))
            Assert.that(not re.matches("xx"))
        }
        {
            var re = Regex.fromPatterns(["..\\d+"]).get()
            Assert.that(not re.matches(""))
            Assert.that(not re.matches("a"))
            Assert.that(re.matches("^&3"))
            Assert.that(re.matches("a 8484"))
            Assert.that(re.matches("  8484"))
            Assert.that(re.matches("12345"))
            Assert.that(re.matches(" y1235"))
        }
    }

    def testIntervals() {
        {
            # basic
            var re = Regex.fromPatterns(["x{3}"]).get()
            Assert.that(not re.matches(""))
            Assert.that(not re.matches("x"))
            Assert.that(not re.matches("xx"))
            Assert.that(re.matches("xxx"))
            Assert.that(not re.matches("yyy"))
            Assert.that(not re.matches("xxxx"))
            Assert.that(not re.matches("xxxxx"))
        }
        {
            # mixed patterns
            var re = Regex.fromPatterns(["(x|y){3}"]).get()
            Assert.that(not re.matches(""))
            Assert.that(not re.matches("x"))
            Assert.that(not re.matches("xx"))
            Assert.that(re.matches("xxx"))
            Assert.that(re.matches("yyy"))
            Assert.that(re.matches("yxy"))
            Assert.that(re.matches("yxx"))
            Assert.that(not re.matches("xxxx"))
            Assert.that(not re.matches("yyyy"))
            Assert.that(not re.matches("xxxxx"))
        }
        {
            # min count, no upper limit
            var re = Regex.fromPatterns(["(x|y){3,}"]).get()
            Assert.that(not re.matches(""))
            Assert.that(not re.matches("x"))
            Assert.that(not re.matches("xx"))
            Assert.that(not re.matches("xy"))
            Assert.that(not re.matches("yy"))
            Assert.that(re.matches("xxx"))
            Assert.that(re.matches("yyy"))
            Assert.that(re.matches("yxy"))
            Assert.that(re.matches("yxx"))
            Assert.that(re.matches("xxxx"))
            Assert.that(re.matches("yyyy"))
            Assert.that(re.matches("xxxxx"))
        }
        {
            # 0 count
            var re = Regex.fromPatterns(["(x|y){0}"]).get()
            Assert.that(re.matches(""))
            Assert.that(not re.matches("x"))
            Assert.that(not re.matches("xy"))
            Assert.that(not re.matches("yy"))
            Assert.that(not re.matches("xxx"))
            Assert.that(not re.matches("yyyy"))
            Assert.that(not re.matches("xxxxx"))
        }
        {
            # non-zero lower and finite upper
            var re = Regex.fromPatterns(["(x|y){2,3}"]).get()
            Assert.that(not re.matches(""))
            Assert.that(not re.matches("x"))
            Assert.that(re.matches("xx"))
            Assert.that(re.matches("xxx"))
            Assert.that(re.matches("yyy"))
            Assert.that(re.matches("yxy"))
            Assert.that(re.matches("yxx"))
            Assert.that(not re.matches("xxxx"))
            Assert.that(not re.matches("yyyy"))
            Assert.that(not re.matches("xxxxx"))
        }
    }
}
