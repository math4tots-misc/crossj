package crossj.books.dragon.ch03;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Optional;
import crossj.base.Set;
import crossj.base.XError;

final class NFABuilder {

    static NFA buildFromRegexNode(RegexNode node) {
        return buildFromRegexNodeList(List.of(node));
    }

    static NFA buildFromRegexNodeList(List<RegexNode> nodes) {
        var builder = new NFABuilder();

        // The first nodes.size() states are all accepting states,
        // each corresponding to the accepting state of the corresponding RegexNode.
        // The state 'nodes.size()' is the universal accepting state (i.e. all other accepting
        // states have an epsilon transition into it),
        // and state 'nodes.size() + 1' is the start state.
        for (int i = 0; i < nodes.size(); i++) {
            Assert.equals(builder.newState(), i);
        }
        var joinAcceptState = builder.newState();
        var startState = builder.newState();

        for (int i = 0; i < nodes.size(); i++) {
            var node = nodes.get(i);
            builder.buildBlock(node, startState, i);
            builder.connect(i, Optional.empty(), joinAcceptState);
        }

        Assert.equals(joinAcceptState + 1, startState);
        Assert.equals(joinAcceptState, nodes.size());
        return new NFA(builder.transitionMap, joinAcceptState);
    }

    private List<Map<Optional<Integer>, Set<Integer>>> transitionMap = List.of();

    private NFABuilder() {
    }

    private int newState() {
        int state = transitionMap.size();
        transitionMap.add(Map.of());
        return state;
    }

    private void connect(int startState, Optional<Integer> label, int acceptState) {
        var localTransitions = transitionMap.get(startState);
        if (!localTransitions.containsKey(label)) {
            localTransitions.put(label, Set.of());
        }
        localTransitions.get(label).add(acceptState);
    }

    // More or less as described in pages 159 - 161 for how to create a NFA
    // from a regex syntax tree.
    private NFABlock buildBlock(RegexNode node, int startState, int acceptState) {
        if (startState == -1) {
            startState = newState();
        }
        if (acceptState == -1) {
            acceptState = newState();
        }

        if (node instanceof EpsilonRegexNode) {
            connect(startState, Optional.empty(), acceptState);
        } else if (node instanceof LetterRegexNode) {
            int letter = ((LetterRegexNode) node).letter;
            connect(startState, Optional.of(letter), acceptState);
        } else if (node instanceof OrRegexNode) {
            var orNode = (OrRegexNode) node;
            var left = orNode.left;
            var right = orNode.right;
            var leftBlock = buildBlock(left, -1, -1);
            var rightBlock = buildBlock(right, -1, -1);
            connect(startState, Optional.empty(), leftBlock.startState);
            connect(startState, Optional.empty(), rightBlock.startState);
            connect(leftBlock.acceptState, Optional.empty(), acceptState);
            connect(rightBlock.acceptState, Optional.empty(), acceptState);
        } else if (node instanceof CatRegexNode) {
            var catNode = (CatRegexNode) node;
            var left = catNode.left;
            var right = catNode.right;
            var leftBlock = buildBlock(left, startState, -1);
            buildBlock(right, leftBlock.acceptState, acceptState);
        } else if (node instanceof StarRegexNode) {
            var innerNode = ((StarRegexNode) node).child;
            var innerBlock = buildBlock(innerNode, -1, -1);
            connect(startState, Optional.empty(), acceptState);
            connect(startState, Optional.empty(), innerBlock.startState);
            connect(innerBlock.acceptState, Optional.empty(), acceptState);
            connect(innerBlock.acceptState, Optional.empty(), innerBlock.startState);
        } else {
            throw XError.withMessage("Unrecognized NFARegexNode type: " + node);
        }
        return new NFABlock(startState, acceptState);
    }
}
