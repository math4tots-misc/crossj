package cj

native class Assert {
    def that(x: Bool) : Unit
    def withMessage(x: Bool, message: String) : Unit
    def equal[T: Eq & Repr](a: T, b: T) : Unit

    def divides(divisor: Int, dividend: Int)
}
