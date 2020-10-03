package sanity.hacks.c;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Test;
import crossj.hacks.c.ASCIITrie;

public final class TrieTest {

    @Test
    public static void sample() {
        var trie = ASCIITrie.fromIterable(List.of("foo", "bar"));
        var s = trie.findUsingStringOrNull("foo");
        Assert.equals(s, "foo");
        s = trie.findUsingStringOrNull("bar");
        Assert.equals(s, "bar");
        s = trie.findUsingStringOrNull("asdf");
        Assert.equals(s, null);
    }

    @Test
    public static void iterate() {
        var strings = List.of("aa", "bb", "abcdef", "a", "gg");
        var trie = ASCIITrie.fromIterable(strings);
        Assert.equals(List.fromIterable(trie), List.sorted(strings));
    }
}
