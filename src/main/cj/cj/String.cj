package cj

native class String: Eq, Ord, Hash, Repr, ToString, Iterable[Char], ToBool {
    def __eq(a: Self, b: Self) : Bool
    def __lt(a: Self, b: Self) : Bool

    def __add[T: ToString](a: Self, b: T) : Self

    ## The number of code units in this string.
    ## For UTF-8 targets, this returns the number of bytes,
    ## For UTF-16 targets, this returns the number of 16-bit code units.
    def size(x: Self) : Int

    ## Gets the code-unit of the given string.
    ## For UTF-8 targets, this returns a number between 0 and 255,
    ## For UTF-16 targets, this returns a number between 0 and 65535
    def codeAt(self: Self, i: Int): Int

    ## Gets the codepoint at the given position in the string.
    ## If the index is not at the start of a codepoint, the result is
    ## undefined.
    def codePointAt(self: Self, i: Int): Char

    def hash(x: Self) : Int

    def repr(x: Self) : String
    def toString(x: Self) : String

    def iter(self: Self) : Iterator[Char]

    def join[I: ToString, C: Iterable[I]](self: Self, parts: C): String

    ## Splits self with the given separator.
    ## (CJJSSpecialMethods implementation only)
    def split(self: Self, separator: String): List[String]

    ## Checks whether a given string appears as a prefix of self.
    ## (CJJSSpecialMethods implementation only)
    def startsWith(self: Self, prefix: String): Bool

    ## Checks whether a given string starts with the given prefix at the given position
    ## (CJJSSpecialMethods implementation only)
    def startsWithAt(self: Self, prefix: String, start: Int): Bool

    ## Checks whether a given string appears as a suffix of self.
    ## (CJJSSpecialMethods implementation only)
    def endsWith(self: Self, suffix: String): Bool

    ## Checks whether a given string ends with the given prefix at the given end position
    ## (CJJSSpecialMethods implementation only)
    def endsWithAt(self: Self, prefix: String, end: Int): Bool

    ## Checks whether a given string appears as a substring in self.
    ## (CJJSSpecialMethods implementation only)
    def __contains(self: Self, substring: String): Bool

    ## Create a substring from self with the given start and end indices.
    ## (CJJSSpecialMethods implementation only)
    def cut(self: Self, start: Int, end: Int): String

    ## Create a substring from self starting from the given index upto the end.
    ## (CJJSSpecialMethods implementation only)
    def cutFrom(self: Self, start: Int): String

    ## Returns a string with all letters converted to lowercase
    def lower(self: Self): String

    ## Returns a string with all letters converted to uppercase
    def upper(self: Self): String

    ## Tries to parse this string as an integer
    def parseInt(self: Self): Nullable[Int]

    ## Tries to parse this string as a hex integer
    def parseHex(self: Self): Nullable[Int]

    ## Tries to parse this string as a double
    def parseDouble(self: Self): Nullable[Double]

    def toBool(self: Self) : Bool

    def builder(): StringBuilder

    ## Checks if the substring matches another substring
    def regionMatches(self: Self, start0: Int, other: Self, start1: Int, len: Int): Bool
}
