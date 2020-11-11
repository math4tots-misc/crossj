package sanity.books.dragon.ch03;

import crossj.base.Assert;
import crossj.base.Test;
import crossj.books.dragon.ch03.NFA;
import crossj.books.dragon.ch03.RegexNode;

public final class NFATest {
    @Test
    public static void sample() {

        // single letter match
        {
            var re = RegexNode.ofChar('a');
            var nfa = NFA.fromRegexNodes(re);
            Assert.equals(nfa.match("ab"), 1);
            Assert.equals(nfa.match("ba"), -1);
        }

        // cat
        {
            var re = RegexNode.ofChar('x').and(RegexNode.ofChar('y'));
            var nfa = NFA.fromRegexNodes(re);
            Assert.equals(nfa.match("xy"), 2);
            Assert.equals(nfa.match("xx"), -1);
        }

        // star
        {
            var re = RegexNode.ofChar('x').and(RegexNode.ofChar('y').star());
            var nfa = NFA.fromRegexNodes(re);
            Assert.equals(nfa.match("xy"), 2);
            Assert.equals(nfa.match("xx"), 1);
            Assert.equals(nfa.match("xxxy"), 1);
            Assert.equals(nfa.match("xyyyy"), 5);
            Assert.equals(nfa.match("xyyyyyyy"), 8);
        }
    }
}
