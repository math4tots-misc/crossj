package cj

class UnionTest {
    def testSample() {
        {
            val x = Option.Some(24)
            var i = 0

            union (x) {
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
            val x = Option[Int].None
            var i = 0

            union (x) {
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

    def testExpr() {
        {
            val i = union Option.Some(24) {
                case Some(k) = k
                case None = 800
            }
            Assert.equal(i, 24)
        }
        {
            val i = union Option[Int].None {
                case Some(k) = k
                case None = 800
            }
            Assert.equal(i, 800)
        }
    }
}
