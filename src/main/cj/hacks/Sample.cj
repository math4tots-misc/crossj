package hacks

import cj.IO

class Sample {
    def main() : Unit {
        if (1) {
            IO.println("1 is true")
        } else {
            IO.println("1 is false")
        }
        IO.println(Int.__add(2, 3))
        IO.println("Hello Sample")

        if (true) {
            IO.println("true is true")
        }

        var i = 0
        while (Int.__lt(i, 10)) {
            IO.println(i)
            i = Int.__add(i, 1)
        }

        Self.foo(5)
    }

    def foo(i: Int) : Int {
        IO.println("Inside foo")
        return i
    }
}
