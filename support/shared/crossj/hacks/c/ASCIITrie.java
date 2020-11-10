package crossj.hacks.c;

import crossj.base.Assert;
import crossj.base.IntArray;
import crossj.base.List;
import crossj.base.Repr;
import crossj.base.Str;
import crossj.base.StrBuilder;
import crossj.base.XError;
import crossj.base.XIterable;
import crossj.base.XIterator;

/**
 * String trie for quick keyword lookup.
 *
 * Iterating over the tree will yield the strings in lexicographic order.
 */
public final class ASCIITrie implements XIterable<String> {
    private String value = null;
    private List<ASCIITrie> children = null;

    private ASCIITrie() {
    }

    public static ASCIITrie fromIterable(XIterable<String> strings) {
        var trie = newEmpty();
        for (var string : strings) {
            trie.insert(string);
        }
        return trie;
    }

    public static ASCIITrie of(String... strings) {
        return fromIterable(List.fromJavaArray(strings));
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
        // Since this is an ASCII tree, length of the matched string must equal number
        // of
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
                return lastMatch;
            }
            trie = trie.children.get(ch);
        }
        return trie != null && trie.value != null ? trie.value : lastMatch;
    }

    @Override
    public XIterator<String> iter() {
        var stack = List.of(this);
        return XIterator.fromParts(() -> pump(stack), () -> {
            Assert.withMessage(pump(stack), "getNext on empty XIterator (ASCIITree)");
            var node = stack.pop();
            addChildren(node, stack);
            return node.value;
        });
    }

    private static boolean pump(List<ASCIITrie> stack) {
        // ensures that either the stack is empty, or the top element has a non-null
        // value.
        while (stack.size() > 0 && stack.last().value == null) {
            addChildren(stack.pop(), stack);
        }
        return stack.size() > 0;
    }

    private static void addChildren(ASCIITrie node, List<ASCIITrie> stack) {
        if (node.children != null) {
            var children = node.children;
            for (int i = children.size() - 1; i >= 0; i--) {
                var child = children.get(i);
                if (child != null) {
                    stack.add(child);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ASCIITrie.of(" + Str.join(", ", iter().map(s -> Repr.of(s))) + ")";
    }

    /**
     * Dump the trie's internal data structure for debugging purposes
     */
    public String dump() {
        var sb = Str.builder();
        dumpSB(sb);
        return sb.toString();
    }

    private void dumpSB(StrBuilder sb) {
        sb.s("(");
        if (value != null) {
            sb.s(value);
        }
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                var child = children.get(i);
                if (child != null) {
                    sb.s("[" + i + "]:");
                    child.dumpSB(sb);
                }
            }
        }
        sb.s(")");
    }
}
