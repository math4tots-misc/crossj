package crossj.books.dragon.ch03;

import crossj.base.Assert;
import crossj.base.Optional;
import crossj.base.Str;
import crossj.base.XError;

/**
 * DFA for matching ASCII strings.
 */
public final class DFA {
    private final int nstates;
    private final int startState;
    private final int[] transitionTable;
    private final String[] acceptingTag;

    private DFA(int startState, int[] transitionTable, String[] acceptingTag) {
        this.nstates = acceptingTag.length;
        this.startState = startState;
        this.transitionTable = transitionTable;
        this.acceptingTag = acceptingTag;

        Assert.equals(transitionTable.length, nstates * Alphabet.COUNT);

        // make sure that every entry in the transition table is either -1 or in
        // range(nstates).
        for (int i = 0; i < transitionTable.length; i++) {
            Assert.order(-1, transitionTable[i], nstates - 1);
        }
    }

    public int getStartState() {
        return startState;
    }

    public int nextState(int currentState, int charCode) {
        if (charCode < 0) {
            throw XError.withMessage("Negative char code (" + charCode + ")");
        }
        // All non-ASCII values are mapped to the catchall value
        if (charCode > Alphabet.UNICODE_CATCH_ALL) {
            charCode = Alphabet.UNICODE_CATCH_ALL;
        }
        return transitionTable[currentState * Alphabet.COUNT + charCode];
    }

    public boolean isTerminal(int state) {
        return isRejectingState(state) || isAcceptingState(state);
    }

    public boolean isAcceptingState(int state) {
        return state >= 0 && state < nstates && acceptingTag[state] != null;
    }

    public boolean isRejectingState(int state) {
        return state < 0;
    }

    /**
     * If the given state is accepting, return the associated tag.
     * Otherwise return empty.
     */
    public Optional<String> getAcceptingTag(int state) {
        return Optional.of(acceptingTag[state]);
    }

    /**
     * Tries to find the longest valid match starting at the given index
     */
    public Optional<Match> match(String string, int startIndex) {
        int state = getStartState();
        int i = startIndex;
        String lastAcceptingTag = null;
        int lastAcceptingIndex = i;
        while (state >= 0) {
            if (acceptingTag[state] != null) {
                lastAcceptingTag = acceptingTag[state];
                lastAcceptingIndex = i;
            }

            int charCode = Str.codeAt(string, i);
            state = nextState(state, charCode);
            i++;
        }
        if (lastAcceptingTag == null) {
            return Optional.empty();
        } else {
            return Optional.of(new Match(string, startIndex, lastAcceptingIndex, lastAcceptingTag));
        }
    }
}
