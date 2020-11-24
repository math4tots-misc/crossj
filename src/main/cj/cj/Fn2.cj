package cj


native class Fn2[R, A1, A2] {
    def call(f: Self, a1: A1, a2: A2) : R
}
