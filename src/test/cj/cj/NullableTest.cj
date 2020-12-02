package cj


class NullableTest {

    def testSample() {
        {
            val x = Nullable.Some(24)
            var i = 0

            union x {
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
            val x = Nullable[Int].None
            var i = 0

            union x {
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
            val i = union Nullable.Some(29) {
                case Some(k) = k
                case None = 800
            }
            Assert.equal(i, 29)
        }
        {
            val i = union Nullable[Int].None {
                case Some(k) = k
                case None = 800
            }
            Assert.equal(i, 800)
        }
    }
}
