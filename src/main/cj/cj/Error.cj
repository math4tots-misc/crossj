package cj

native class Error : Repr, Eq {
    def fromMessage(message: String) : Self
    def getMessage(self: Self) : String
    def repr(self: Self) : String
    def __eq(a: Self, b: Self) : Bool
    def addContext(self: Self, message: String)
    def format(self: Self): String
}
