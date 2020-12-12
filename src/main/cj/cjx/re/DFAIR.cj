package cjx.re

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
class DFAIR {
}
