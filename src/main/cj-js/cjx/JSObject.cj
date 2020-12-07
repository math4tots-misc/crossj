package cjx

##
## API for interacting with JavaScript objects from cj
##
## This class is only available when targeting a javascript platform.
##
native class JSObject: Eq, Repr {

    ## Retrieves the 'document' global variable in a browser context.
    def document(): Self

    ## Creates a new javascript object as though it were created with
    ## an object literal (i.e. "{}").
    def empty(): Self

    ## Returns the 'null' javascript value
    def null_(): Self

    ## Creates a new javascript object with "Object.create(..)"
    def create(self: Self): Self

    ## Cast the value into a JSObject
    def from[T](t: T): Self

    ## Call 'self' like a javascript function
    def apply(self: Self, args: List[Self]): Self

    ## Call a javascript method
    def method(self: Self, name: String, args: List[Self]): Self

    ## Get a javascript field
    def field(self: Self, name: String): Self

    ## Set a javascript field
    def setField[T](self: Self, name: String, t: T)

    ## Get an item in an array
    def get(self: Self, i: Int): Self

    ## Set an item in an array
    def set[T](self: Self, i: Int, t: T)

    ## apply the 'typeof' operator on self
    def typeOf(self: Self): String

    ## Call Array.isArray on self
    def isArray(self: Self): Bool

    ## Cast to string (panics if self is not a string)
    def asString(self: Self): String

    ## Cast to double (panics if self is not a number)
    def asDouble(self: Self): Double

    ## Cast to int (panics if self is not a number)
    ## The number is not an integer, it will be '|0' to convert
    ## it into one
    def asInt(self: Self): Int

    ## Call JSON.stringify()
    def stringify(self: Self): String

    ## Use '===' compare two javascript objects
    def __eq(self: Self, other: Self): Bool

    ## JSObject(...) repr
    def repr(self: Self): String

    ## Use javascript's 'toString()' to convert to a string
    def toString(self: Self): String
}
