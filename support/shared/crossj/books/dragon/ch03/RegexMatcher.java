package crossj.books.dragon.ch03;

import crossj.base.Str;
import crossj.base.StrIter;

public final class RegexMatcher {
    private DFA dfa;
    private final StrIter iter;
    private int matchIndex = -1;
    private int matchStartPosition = -1;

    RegexMatcher(DFA dfa, String string) {
        this.dfa = dfa;
        this.iter = Str.iter(string);
    }

    /**
     * Replaces the regex used to match the current string.
     */
    public void useRegex(Regex regex) {
        dfa = regex.getDfa();
    }

    /**
     * Gets the underlying StrIter instance used to walk the string for matching.
     *
     * This can be used to detect the current position in the string and whether the
     * matcher has reached the end of the string
     */
    public StrIter getStrIter() {
        return iter;
    }

    /**
     * Tries to find the longest regex match starting from the current position.
     *
     * If the match is successful, true is returned, and the position is incremented
     * to just after the match.
     */
    public boolean match() {
        int state = dfa.getStartState();
        int startPosition = iter.getPosition();
        int lastMatchIndex = dfa.getMatchIndex(state);
        int lastMatchPosition = lastMatchIndex >= 0 ? startPosition : -1;
        while (iter.hasCodePoint() && !dfa.isDeadState(state)) {
            state = dfa.transition(state, iter.nextCodePoint());
            int matchIndex = dfa.getMatchIndex(state);
            if (matchIndex >= 0) {
                lastMatchPosition = iter.getPosition();
                lastMatchIndex = matchIndex;
            }
        }
        if (lastMatchPosition == -1) {
            // no match found
            iter.setPosition(startPosition);
            matchStartPosition = -1;
            matchIndex = -1;
            return false;
        } else {
            // match found
            iter.setPosition(lastMatchPosition);
            matchStartPosition = startPosition;
            matchIndex = lastMatchIndex;
            return true;
        }
    }

    /**
     * Like match(), but requires that the entire string matches to the end.
     */
    public boolean matchAll() {
        int startPosition = iter.getPosition();
        if (match()) {
            if (iter.hasCodePoint()) {
                // the longest possible match didn't reach the end, so
                // the match actually fails.
                iter.setPosition(startPosition);
                matchStartPosition = -1;
                matchIndex = -1;
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * After a successful match(), calling this method returns the index of the
     * matching pattern of the associated Regex.
     */
    public int getMatchIndex() {
        return matchIndex;
    }

    /**
     * After a successful match(), calling this method returns the matching section
     * of the text.
     */
    public String getMatchText() {
        return iter.sliceFrom(matchStartPosition);
    }
}
