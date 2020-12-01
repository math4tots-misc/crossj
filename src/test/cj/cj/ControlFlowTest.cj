package cj

import cj.Assert


class ControlFlowTest {

    def testIf() {
        {
            # simple if expression
            var x = if (true) 5 else 4
            Assert.equal(x, 5)
        }
        {
            # compound if expression
            var x = if (true) {
                var y = 35
                y * y
            } else 82 // 2
            Assert.equal(x, 35 * 35)
            Assert.equal(x, 1225)
        }
    }
}
