package crossj.hacks.c;

import crossj.Assert;
import crossj.IntArray;
import crossj.List;
import crossj.Str;
import crossj.XError;
import crossj.XIterable;
import crossj.XIterator;

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
                return null;
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

    /**
     * Dump the trie's internal data structure for debugging purposes
     */
    public String dump() {
        var sb = new StringBuilder();
        dumpSB(sb);
        return sb.toString();
    }

    private void dumpSB(StringBuilder sb) {
        sb.append("(");
        if (value != null) {
            sb.append(value);
        }
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                var child = children.get(i);
                if (child != null) {
                    sb.append("[" + i + "]:");
                    child.dumpSB(sb);
                }
            }
        }
        sb.append(")");
    }
}
