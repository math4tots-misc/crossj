package cj


class PromiseTest {

    def testDelay() {
        val list = @[Int]

        val p2: Promise[Unit] = AIO.delay(0.02).map(_ -> {
            list.add(222)
        })

        val p1: Promise[Unit] = AIO.delay(0.01).map(_ -> {
            list.add(111)
        })

        Promise.all([p1, p2]).finally(() -> {
            Assert.equal(list, @[111, 222])
        })
    }

    def testError() {
        {
            var pr: Promise[String] = AIO.delay(0).map(_ -> {
                IO.panic("some error msg")
                return "hello"
            })
            pr = pr.recover(e -> e.getMessage())
            pr.onResolve(s -> {
                Assert.equal(s, "some error msg")
            })
        }
    }
}
