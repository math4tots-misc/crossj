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
            var f : Fn[Int, Int] = def(x) = x + 1
            var x = f.call(2)
            Assert.equal(x, 3)
        }
        {
            var x = Self.useLambda(def(x) = x * x)
            Assert.equal(x, 576)
        }
        {
            var xs = [1, 2, 3, 4, 5].map(def(x) = x * x)
            Assert.equal(xs, [1, 4, 9, 16, 25])
        }
    }

    def useLambda(f: Fn[Int, Int]) : Int {
        return f.call(24)
    }
}
