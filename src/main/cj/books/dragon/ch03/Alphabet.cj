package cj.books.dragon.ch03


## The set of alphabets that dragon.ch03 works on.
##
## To keep things simple, our alphabet range is ASCII, and any value
## outside this range is mapped to Alphabet.catchall (127).
class Alphabet {
    ## The number of letters in this alphabet
    static val count: Int = 128

    ## The catch-all value non-ASCII values will be mapped to
    static val catchall: Char = (127).toChar()
}
