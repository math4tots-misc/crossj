package cj

import cj.Assert


class SwitchUnionTest {
    def testSample() {
        {
            var x = Option.Some(24)
            var i = 0

            switch union (x) {
                case Some(k) {
                    i = k
                }
                case None {
                    i = 700
                }
            }

            Assert.equal(i, 24)
        }
        {
            var x = Option[Int].None
            var i = 0

            switch union (x) {
                case Some(k) {
                    i = k
                }
                case None {
                    i = 700
                }
            }

            Assert.equal(i, 700)
        }
    }
}
