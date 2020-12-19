package cjx.re

import cjx.re.RegexNode
import cjx.re.NFA


class NFATest {
    def testSample() {
        # Single letter match
        {
            val re = RegexNode.ofChar('a')
            val nfa = NFA.fromRegexNodes([re])
            Assert.equal(nfa.match("ab"), 1)
            Assert.equal(nfa.match("ba"), -1)
        }

        # Cat
        {
            val re = RegexNode.Cat(
                RegexNode.ofChar('x'),
                RegexNode.ofChar('y'),
            )
            val nfa = NFA.fromRegexNodes([re])
            Assert.equal(nfa.match("xy"), 2)
            Assert.equal(nfa.match("xx"), -1)
        }

        # Star
        {
            val re = RegexNode.Cat(
                RegexNode.ofChar('x'),
                RegexNode.Star(
                    RegexNode.ofChar('y'),
                ),
            )
            val nfa = NFA.fromRegexNodes([re])
            Assert.equal(nfa.match("xy"), 2)
            Assert.equal(nfa.match("xx"), 1)
            Assert.equal(nfa.match("xxxy"), 1)
            Assert.equal(nfa.match("xyyyy"), 5)
            Assert.equal(nfa.match("xyyyyyyy"), 8)
        }
    }
}
