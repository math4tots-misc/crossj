package cj

native union Try[T] :
        Repr if T : Repr {
    case Ok(T)
    case Fail(Error)

    if T : Repr {
        def repr(x: Self) : String
    }

    def fail(message: String) : Self

    def isOk(self: Self) : Bool
    def isFail(self: Self) : Bool

    def get(self: Self) : T
    def getError(self: Self) : Error
    def getErrorMessage(self: Self) : String
}
