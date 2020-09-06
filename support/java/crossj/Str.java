package crossj;

import java.util.Iterator;

/**
 * Some utility functions for dealing with strings
 */
public final class Str {
    /**
     * Given a string, return an iterator that visits each unicode codepoint.
     * @param s
     * @return
     */
    public static XIterator<Integer> chars(String s) {
        return XIterator.fromIterator(new Iterator<Integer>(){
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < s.length();
            }

            @Override
            public Integer next() {
                int codepoint = s.codePointAt(i);
                i += Character.charCount(codepoint);
                return codepoint;
            }

        });
    }
}
