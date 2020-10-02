package sanity.hacks.c;

import crossj.Assert;
import crossj.List;
import crossj.Test;
import crossj.hacks.c.ASCIITrie;

public final class TrieTest {

    @Test
    public static void sample() {
        var trie = ASCIITrie.fromIterable(List.of(
            "foo",
            "bar"
        ));
        var s = trie.findUsingStringOrNull("foo");
        Assert.equals(s, "foo");
        s = trie.findUsingStringOrNull("asdf");
        Assert.equals(s, null);
    }
}
