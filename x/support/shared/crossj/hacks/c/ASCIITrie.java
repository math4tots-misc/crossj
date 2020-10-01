package crossj.hacks.c;

import crossj.IntArray;
import crossj.List;
import crossj.Str;
import crossj.XError;
import crossj.XIterable;

/**
 * String trie for quick keyword lookup
 */
final class ASCIITrie {
    private String value = null;
    private List<ASCIITrie> children = null;

    private ASCIITrie() {}

    public static ASCIITrie fromIterable(XIterable<String> strings) {
        var trie = newEmpty();
        for (var string : strings) {
            trie.insert(string);
        }
        return trie;
    }

    private static ASCIITrie newEmpty() {
        return new ASCIITrie();
    }

    private void insert(String value) {
        insertWithChars(value, Str.toUTF32(value), 0);
    }

    private void insertWithChars(String value, IntArray chars, int i) {
        if (i == chars.size()) {
            this.value = value;
        } else if (i < chars.size()) {
            var ch = chars.get(i);
            if (ch < 0 || ch >= 128) {
                throw XError.withMessage("When inserting, ASCIITrie char out of range (" + ch + ")");
            }
            if (children == null) {
                children = List.ofSize(128, () -> null);
            }
            if (children.get(ch) == null) {
                children.set(ch, newEmpty());
            }
            children.get(ch).insertWithChars(value, chars, i + 1);
        }
    }

    public String findUsingStringOrNull(String string) {
        var chars = Str.toUTF32(string);
        return findUsingCharsOrNull(chars, 0, chars.size());
    }

    public String findUsingCharsOrNull(IntArray chars, int start, int end) {
        var match = findLongestMatchUsingCharsOrNull(chars, start, end);
        // Since this is an ASCII tree, length of the matched string must equal number of
        // codepoints in the string.
        if (match != null && match.length() == (end - start)) {
            return match;
        } else {
            return null;
        }
    }

    public String findLongestMatchUsingCharsOrNull(IntArray chars, int start, int limit) {
        var trie = this;
        String lastMatch = null;
        for (var i = start; i < limit && trie != null; i++) {
            if (trie.value != null) {
                lastMatch = trie.value;
            }
            var ch = chars.get(i);
            if (ch < 0 || ch >= 128) {
                return lastMatch;
            }
            if (trie.children == null) {
                return null;
            }
            trie = trie.children.get(ch);
        }
        return lastMatch;
    }
}
