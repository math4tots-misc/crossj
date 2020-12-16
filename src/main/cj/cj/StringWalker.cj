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

    ## Gets the string that self is walking over
    def string(self: Self): String {
        return self._s
    }

    ## Gets the current position of the walker
    def tell(self: Self): Int {
        return self._i
    }

    ## Sets the current position of the walker.
    ## To ensure validity, the argument passed here should
    ## always be a value returned before by the 'tell()' argument
    ## of the same `StringWalker`.
    def seek(self: Self, i: Int) {
        self._i = i
    }

    ## Returns the codepoint at the current position in the string.
    def peek(self: Self): Nullable[Char] {
        return (
            if (self._i < self._s.size())
                Nullable.Some(self._s.codePointAt(self._i)) else
                Nullable.None
        )
    }

    def next(self: Self): Nullable[Char] {
        return union self.peek() {
            case Some(c) = {
                self._i += c.codeUnitCount()
                Nullable.Some(c)
            }
            case None = Nullable.None
        }
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

    def cutFrom(self: Self, i: Int): String {
        return self._s.cut(i, self._i)
    }
}
