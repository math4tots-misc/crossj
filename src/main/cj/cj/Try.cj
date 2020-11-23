package cj

native union Try[T] :
        Repr if T : Repr,
        ToString if T : Repr {
    case Ok(T)
    case Fail(Error)

    if T : Repr {
        def repr(x: Self) : String
    }

    if T : String {
        def toString(x: Self) : String
    }
}
