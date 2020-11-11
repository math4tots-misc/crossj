package sanity.books.dragon.ch03;

import crossj.base.Assert;
import crossj.base.IO;
import crossj.base.Test;
import crossj.books.dragon.ch03.nfa.NFA;

public final class NFATest {
    @Test
    public static void sample() {

        // single letter match
        {
            var nfa = NFA.withRegexBuilder(b -> b.letter('a'));
            Assert.equals(nfa.match("ab"), 1);
            Assert.equals(nfa.match("ba"), -1);
        }

        // cat
        {
            var nfa = NFA.withRegexBuilder(b -> b.cat(b.letter('x'), b.letter('y')));
            Assert.equals(nfa.match("xy"), 2);
            Assert.equals(nfa.match("xx"), -1);
        }

        // star
        {
            var nfa = NFA.withRegexBuilder(b -> b.cat(b.letter('x'), b.star(b.letter('y'))));
            Assert.equals(nfa.match("xy"), 2);
            Assert.equals(nfa.match("xx"), 1);
            Assert.equals(nfa.match("xxxy"), 1);
            Assert.equals(nfa.match("xyyyy"), 5);
            Assert.equals(nfa.match("xyyyyyyy"), 8);
        }
    }
}
