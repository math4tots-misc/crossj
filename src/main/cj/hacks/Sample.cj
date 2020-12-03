package hacks

import cj.IO

class Sample : ToString {
    def main() : Unit {
        if 1 > 0 {
            IO.println("1 > 0 is true")
        } else {
            IO.println("1 > 0 is false")
        }
        IO.println(2 + 3)
        IO.println("Hello Sample")

        if (true) {
            IO.println("true is true")
        }

        var i = 0
        while i < 10 {
            IO.println(i)
            i = i + 1
        }

        Self.foo(5)

        {
            var xs = @[Int]
            IO.println(xs.size())
            xs.add(42)
            IO.println(xs)
            IO.println(xs.size())
        }

        {
            var x = Try.Ok(23)

            union (x) {
                case Ok(j) {
                    IO.println("Ok(j): j -> ")
                    IO.println(j)
                }
                case Fail(e) {
                    IO.println("Fail(e)")
                }
            }

            IO.println(x)
        }

        IO.println(Sample.new())
    }

    def foo(i: Int) : Int {
        IO.println("Inside foo")
        return i
    }

    def toString() : String {
        return "<Sample instance>"
    }
}
