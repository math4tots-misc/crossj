package cj

import cj.Assert
import cj.Error


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

    def testPropagateError() {
        {
            # check when error is "thrown"
            var result = Self.propagateError(true)
            Assert.that(result.isFail())
            Assert.that(not result.isOk())
            Assert.equal(result.getError(), Error.fromMessage("some-error"))
            Assert.equal(result.getErrorMessage(), "some-error")

            # check when result is successful
            result = Self.propagateError(false)
            Assert.that(result.isOk())
            Assert.that(not result.isFail())
            Assert.equal(result.get(), "successful result")
        }
    }

    def propagateError(throwError: Bool) : Try[String] {
        Self.returnTry(throwError)?
        return Try.Ok("successful result")
    }

    def returnTry(throwError: Bool) : Try[Int] {
        if (throwError) {
            return Try.Fail(Error.fromMessage("some-error"))
        }
        return Try.Ok(24)
    }
}
