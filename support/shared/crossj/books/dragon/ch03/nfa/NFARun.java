package crossj.books.dragon.ch03.nfa;

import crossj.base.Set;

/**
 * A running instance of an NFA
 */
public final class NFARun {
    private final NFA nfa;
    private Set<Integer> currentStates;

    NFARun(NFA nfa) {
        this.nfa = nfa;
        this.currentStates = nfa.epsilonClosureOf(Set.of(nfa.getStartState()));
    }

    /**
     * Returns true if we are currently in an accepting state
     */
    public boolean isMatching() {
        return currentStates.contains(nfa.getAcceptState());
    }

    /**
     * Returns true if our set of states is currently empty, and it's
     * impossible to enter the accepting state.
     */
    public boolean isDead() {
        return currentStates.size() == 0;
    }

    /**
     * Updates the current state based on the next input letter.
     */
    public void accept(int letter) {
        currentStates = nfa.transitionOf(currentStates, letter);
    }
}
