package cjx.re

import cjx.re.Alphabet
import cjx.re.DFA

## Intermediate representation of a DFA.
##
## The main purpose of the DFAIR is to reduce the number of states in the DFA
## before creating the optimized DFA representation. We do this because the
## initial DFA generated from the NFA may have a fairly large number of states,
## and the optimized DFA representation uses a fixed amount of memory for each
## state regardless of how dense the DFA transition graph is.
##
## The DFAIR is equivalent to the DFA representation, but maintains a sparse
## representation to better handle larger number of states.
##
private class DFAIR {

    def _fromFrozenSetDescription(
            allStates: MutableList[Set[Int]],
            startState: Set[Int],
            transitionMap: MutableMap[Set[Int], MutableMap[Int, Set[Int]]],
            acceptMap: MutableMap[Set[Int], Int]): Self {

        # ensure that startState is always zero.
        {
            var repeat = true
            for i = 0; repeat and i < allStates.size(); i += 1 {
                if allStates.get(i) == startState {
                    if i != 0 {
                        allStates.swap(i, 0)
                    }
                    repeat = false
                }
            }
        }

        val frozenSetToIndex = allStates.iter().enumerate((i, s) -> (s, i)).toMap()

        # determine newTransitionMap
        val newTransitionMap = {
            val newMapBuilder = Map[Int, Map[Int, Int]].builder()
            for newState = 0; newState < allStates.size(); newState += 1 {
                val state = allStates.get(newState)
                val newLocalMapBuilder = Map[Int, Int].builder()
                val localTransitionMap = transitionMap.get(state)
                for (letter, neighborState) in localTransitionMap.pairs() {
                    val newNeighborState = frozenSetToIndex.get(neighborState)
                    newLocalMapBuilder.put(letter, newNeighborState)
                }
                newMapBuilder.put(newState, newLocalMapBuilder.build())
            }
            newMapBuilder.build()
        }

        # determine newAcceptMap
        var nAlternatives = -1
        val newAcceptMap = {
            val newMapBuilder = Map[Int, Int].builder()
            for (oldState, alt) in acceptMap.pairs() {
                val newState = frozenSetToIndex.get(oldState)
                nAlternatives = Math.max(nAlternatives, alt)
                newMapBuilder.put(newState, alt)
            }
            newMapBuilder.build()
        }
        nAlternatives += 1

        return DFAIR.new(allStates.size(), 0, nAlternatives, newTransitionMap, newAcceptMap)
    }

    val _nStates: Int
    val _startState: Int
    val _nAlternatives: Int
    val _transitionMap: Map[Int, Map[Int, Int]]
    val _acceptMap: Map[Int, Int]

    def _transition(self: Self, state: Int, letter: Int): Int {
        return self._transitionMap.get(state).getOrDefault(letter, -1)
    }

    def _transitionToGroupId(self: Self, stateToGroupId: MutableList[Int], state: Int, letter: Int): Int {
        val transitionState = self._transition(state, letter)
        return if (transitionState < 0) -1 else stateToGroupId.get(transitionState)
    }

    ## Returns an equivalent DFA with the minimum number of states
    ##
    ## In the dragon book, this algorithm is described in section 3.9.6 pages
    ## 180-184
    def withMinimumNumberOfStates(self: Self): DFA {
        # Initially, partition every state by their accept status.
        # groups 0..<nAlternatives are the various accept states.
        # the group indexed nAlternatives are all the unaccepted states.
        var partition = MutableList.ofSize(self._nAlternatives + 1, i -> @[Int])
        val stateToGroupId = MutableList.ofSize(self._nStates, i -> 0)
        for state = 0; state < self._nStates; state += 1 {
            val groupId = self._acceptMap.getOrNull(state).map(i -> i + 1).getOrDefault(0)
            partition.get(groupId).add(state)
            stateToGroupId.set(state, groupId)
        }
        if partition.get(0).size() == 0 {
            # the first group composed entirely of non-accpting states may potentially be
            # empty.
            partition = partition.cutFrom(1)
        }
        for i = 0; i < partition.size(); i += 1 {
            Assert.that(partition.get(i).size() != 0)
        }

        # split the partitions
        var repeat = true
        while repeat {
            # we store the partition size because we will add to the partition
            # in the loop itself.
            val startPartitionSize = partition.size()
            for groupId = 0; groupId < startPartitionSize; groupId += 1 {
                for letter = 0; letter < Alphabet.count; letter += 1 {
                    val statesByTransitionGroupIds: MutableMap[Int, MutableList[Int]] = @[:]
                    for state in partition.get(groupId) {
                        val transitionGroupId = self._transitionToGroupId(stateToGroupId, state, letter)
                        statesByTransitionGroupIds.getOrInsert(transitionGroupId, () -> @[]).add(state)
                    }
                    Assert.that(statesByTransitionGroupIds.size() != 0)

                    if statesByTransitionGroupIds.size() != 1 {
                        # the transition on this letter has split this group.
                        val resultingGroups = List.sorted(statesByTransitionGroupIds.values())

                        # let the first resulting group take the place of the old group.
                        partition.set(groupId, resultingGroups.get(0))

                        # add the remaining groups at the end of the partition.
                        for i = 1; i < resultingGroups.size(); i += 1 {
                            val newGroupId = partition.size()
                            partition.add(resultingGroups.get(i))

                            # the 'stateToGroupId' table also needs to be updated to reflect the new
                            # groups that these states belong in.
                            for movedState in resultingGroups.get(i) {
                                stateToGroupId.set(movedState, newGroupId)
                            }
                        }
                    }
                }
            }
            # if no new groups were added to the partition, we're done
            repeat = partition.size() != startPartitionSize
        }

        # Now encode the resulting data into a DFA
        val stateRemap = MutableList.ofSize(self._nStates, i -> 0)
        for newState = 0; newState < partition.size(); newState += 1 {
            for oldState in partition.get(newState) {
                stateRemap.set(oldState, newState)
            }
        }

        val newStartState = stateRemap.get(self._startState)

        val newTransitionMap = MutableList.ofSize(partition.size() * Alphabet.count, i -> -1)
        for (oldSourceState, tmap) in self._transitionMap.pairs() {
            val sourceState = stateRemap.get(oldSourceState)
            for (letter, oldDestinationState) in tmap.pairs() {
                val destinationState = stateRemap.get(oldDestinationState)
                newTransitionMap.set(sourceState * Alphabet.count + letter, destinationState)
            }
        }

        val newAcceptMap = MutableList.ofSize(partition.size(), i -> -1)
        for (oldState, v) in self._acceptMap.pairs() {
            val state = stateRemap.get(oldState)
            newAcceptMap.set(state, v)
        }

        return DFA._new(
            newStartState,
            newTransitionMap.toListUnsafe(),
            newAcceptMap.toListUnsafe())
    }
}
