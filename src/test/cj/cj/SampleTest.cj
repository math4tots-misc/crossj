package cj

import cj.IO
import cj.Assert

class SampleTest {
    def testSample() : Unit {
        Assert.that(true)
        Assert.equal(2, 2)
    }

    def testAnotherSample() : Unit {
        Assert.withMessage(not false, "'not false' is not true")
        Assert.equal("hi", "hi")
    }

    def testLambda() : Unit {
        {
            val f : Fn[Int, Int] = x -> x + 1
            val x = f.call(2)
            Assert.equal(x, 3)
        }
        {
            val x = Self.useLambda(x -> x * x)
            Assert.equal(x, 576)
        }
        {
            val xs = [1, 2, 3, 4, 5].map(x -> x * x)
            Assert.equal(xs, [1, 4, 9, 16, 25])
        }
    }

    def useLambda(f: Fn[Int, Int]) : Int {
        return f.call(24)
    }

    def testBlockExpressions() {
        {
            val x = {
                val a = 5
                a * a * a
            }
            Assert.equal(x, 125)
        }
    }
}
