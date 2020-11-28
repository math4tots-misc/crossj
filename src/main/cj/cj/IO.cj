package cj

native class IO {
    def println[T : ToString](t: T) : Unit
    def panic[T: ToString](t: T) : NoReturn
}
