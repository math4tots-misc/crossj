package cj

import cj.Assert

class LambdaTest {

    def testMultipleParameters() {
        {
            val f : Fn[Int, Int, String] = (a, b) -> a + b.size()
            Assert.equal(f.call(24, "hi"), 26)
        }
    }

    def testBlockBody() {
        {
            val f : Fn[Int, Int, String] = (a, b) -> {
                return a * b.size()
            }
            Assert.equal(f.call(77, "hello"), 77 * "hello".size())
        }
    }
}
