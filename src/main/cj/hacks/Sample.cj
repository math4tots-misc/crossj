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
    }
}
