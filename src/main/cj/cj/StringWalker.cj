package cj

##
## Like a String iterator, but also peekable and seekable.
##
class StringWalker {
    val _s: String
    var _i: Int

    def of(string: String): Self {
        return Self.new(string, 0)
    }

    def hasNext(self: Self): Bool {
        return self._i < self._s.size()
    }

    ## Gets the current position of the walker
    def pos(self: Self): Int {
        return self._i
    }

    ## Sets the current position of the walker.
    ## To ensure validity, the argument passed here should
    ## always be a value returned before by the 'pos()' argument
    ## of the same `StringWalker`.
    def seek(self: Self, i: Int) {
        self._i = i
    }

    ## Returns the codepoint at the current position in the string.
    ## You should call 'hasNext()' to check that a peekable value exists.
    def peek(self: Self): Char {
        return self._s.codePointAt(self._i)
    }

    def next(self: Self): Char {
        val ch = self.peek()
        self._i += ch.codeUnitCount()
        return ch
    }

    def seekToStart(self: Self) {
        self._i = 0
    }

    def seekToEnd(self: Self) {
        self._i = self._s.size()
    }

    def endsWith(self: Self, suffix: String): Bool {
        val len = suffix.size()
        return self._s.regionMatches(self._i - len, suffix, 0, len)
    }
}
