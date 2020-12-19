package cjx.re

import cjx.re.NFABlock
import cjx.re.Alphabet
import cjx.re.RegexNode
import cjx.re.NFA

class NFABuilder {

    def buildFromRegexNodeList(nodes: List[RegexNode]): NFA {
        val epsilon = Alphabet.epsilon
        val builder = Self.new(@[])

        # The first nodes.size() states are all accepting states,
        # each corresponding to the accepting state of the corresponding RegexNode.
        # The state 'nodes.size()' is the universal accepting state (i.e. all other accepting
        # states have an epsilon transition into it),
        # and state 'nodes.size() + 1' is the start state.
        for i = 0; i < nodes.size(); i += 1 {
            Assert.equal(builder.newState(), i)
        }
        val joinAcceptState = builder.newState()
        val startState = builder.newState()

        for i = 0; i < nodes.size(); i += 1 {
            val node = nodes.get(i)
            val block = builder.buildBlock(node, -1, i)
            builder.connect(startState, epsilon, block.startState)
            builder.connect(i, epsilon, joinAcceptState)
        }

        Assert.equal(joinAcceptState + 1, startState)
        Assert.equal(joinAcceptState, nodes.size())

        val transitionMap = builder._transitionMap.iter().map(
            map -> Map.fromIterable(map.pairs().map(pair -> (pair.get0(), pair.get1().toSet())))
        ).toList()

        return NFA.new(transitionMap, joinAcceptState)
    }

    val _transitionMap: MutableList[MutableMap[Int, MutableSet[Int]]]

    def newState(self: Self): Int {
        val state = self._transitionMap.size()
        self._transitionMap.add(@[:])
        return state
    }

    def connect(self: Self, startState: Int, rawLabel: Int, acceptState: Int) {
        val label = Alphabet.normalize(rawLabel)
        if startState == acceptState and label == Alphabet.epsilon {
            # there's always an epsilon transition from a state to itself, so
            # there's no need to explicitly create one.
            return ()
        }

        val localTransitions = self._transitionMap.get(startState)
        if label not in localTransitions {
            localTransitions.put(label, MutableSet.empty())
        }
        localTransitions.get(label).add(acceptState)
    }

    ## More or less as described in pages 159 - 161 for how to create a NFA
    ## from a regex syntax tree.
    def buildBlock(self: Self, node: RegexNode, rawStartState: Int, rawAcceptState: Int): NFABlock {
        val startState = if (rawStartState == -1) self.newState() else rawStartState
        val acceptState = if (rawAcceptState == -1) self.newState() else rawAcceptState
        val epsilon = Alphabet.epsilon
        union node {
            case Epsilon {
                self.connect(startState, epsilon, acceptState)
            }
            case Letter(letter) {
                self.connect(startState, Alphabet.normalize(letter), acceptState)
            }
            case Cat(left, right) {
                val leftBlock = self.buildBlock(left, startState, -1)
                self.buildBlock(right, leftBlock.acceptState, acceptState)
            }
            case Or(left, right) {
                val leftBlock = self.buildBlock(left, -1, -1)
                val rightBlock = self.buildBlock(right, -1, -1)
                self.connect(startState, epsilon, leftBlock.startState)
                self.connect(startState, epsilon, rightBlock.startState)
                self.connect(leftBlock.acceptState, epsilon, acceptState)
                self.connect(rightBlock.acceptState, epsilon, acceptState)
            }
            case Star(inner) {
                self._buildIntervalBlock(startState, acceptState, inner, 0, -1)
            }
            case Plus(inner) {
                self._buildIntervalBlock(startState, acceptState, inner, 1, -1)
            }
            case Optional(inner) {
                self._buildIntervalBlock(startState, acceptState, inner, 0, 1)
            }
            case Interval(inner, min, max) {
                self._buildIntervalBlock(startState, acceptState, inner, min, max)
            }
            case CharSet(name, chars) {
                for letter in chars {
                    self.connect(startState, Alphabet.normalize(letter), acceptState)
                }
            }
            default {
                IO.panic("TODO: buildBlock: " + node)
            }
        }
        return NFABlock.new(startState, acceptState)
    }

    def _buildIntervalBlock(
            self: Self,
            var startState: Int,
            acceptState: Int,
            inner: RegexNode,
            min: Int,
            max: Int) {
        val epsilon = Alphabet.epsilon
        switch (max) {
            case -1 {
                if min == 0 {
                    # case 1: max is infinite and min = 0
                    # this is equivalent to '*' (kleene star)
                    self.buildBlock(inner, startState, acceptState)
                    self.connect(startState, epsilon, acceptState)
                    self.connect(acceptState, epsilon, startState)
                } else {
                    # case 2: max is infinite and min > 0
                    # we build min blocks, with the last one using the overall acceptState
                    # as its acceptState.
                    # when min = 1, this is equivalent to '+'
                    for i = 0; i + 1 < min; i += 1 {
                        startState = self.buildBlock(inner, startState, -1).acceptState
                    }
                    self.buildBlock(inner, startState, acceptState)
                    self.connect(acceptState, epsilon, startState)
                }
            }
            case 0 {
                # case 3: max = 0
                self.connect(startState, epsilon, acceptState)
            }
            default {
                # case 4: max is finite, max > 0
                # when min = 0 and max = 1 this is '?'
                for i = 0; i < max; i += 1 {
                    if i >= min {
                        self.connect(startState, epsilon, acceptState)
                    }
                    startState = self.buildBlock(
                        inner,
                        startState,
                        if (i + 1 == max) acceptState else - 1).acceptState
                }
            }
        }
    }
}
