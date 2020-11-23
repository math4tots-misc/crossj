package cj

native union Try[T] {
    case Ok(T)
    case Fail(Error)
}
