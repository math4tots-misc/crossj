package sanity.books.dragon.ch03;

import crossj.base.Assert;
import crossj.base.IO;
import crossj.base.Test;
import crossj.books.dragon.ch03.DFA;
import crossj.books.dragon.ch03.RegexNode;

public final class DFATest {
    @Test
    public static void sample() {

        // single letter match
        {
            var re = RegexNode.ofChar('a');
            var dfa = DFA.fromRegexNodes(re);
            Assert.equals(dfa.match("ab"), 1);
            Assert.equals(dfa.match("ba"), -1);
        }

        // cat
        {
            var re = RegexNode.ofChar('x').and(RegexNode.ofChar('y'));
            var dfa = DFA.fromRegexNodes(re);
            Assert.equals(dfa.match("xy"), 2);
            Assert.equals(dfa.match("xx"), -1);
        }

        // star
        {
            var re = RegexNode.ofChar('x').and(RegexNode.ofChar('y').star());
            var dfa = DFA.fromRegexNodes(re);
            IO.print(dfa.inspect());
            Assert.equals(dfa.match("xy"), 2);
            Assert.equals(dfa.match("xx"), 1);
            Assert.equals(dfa.match("xxxy"), 1);
            Assert.equals(dfa.match("xyyyy"), 5);
            Assert.equals(dfa.match("xyyyyyyy"), 8);
        }
    }
}
