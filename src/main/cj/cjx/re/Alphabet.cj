package cjx.re


## The set of alphabets that dragon.ch03 works on.
##
## To keep things simple, our alphabet range is ASCII, and any value
## outside this range is mapped to Alphabet.catchall (127).
class Alphabet {
    ## The number of letters in this alphabet
    static val count: Int = 128

    ## The number of letters in this alphabet plus epsilon
    static val extendedCount: Int = 129

    ## The catch-all value non-ASCII values will be mapped to
    static val catchall: Int = 127

    ## (Only applicable in NFA) the 'epsilon' transition
    ## This allows us to avoid using a 'nullable/option' type
    ## in the transition map
    static val epsilon: Int = 128
}
