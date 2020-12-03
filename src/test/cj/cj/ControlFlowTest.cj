package cj

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

    def returnTryWithLowercaseFail(throwError: Bool) : Try[Int] {
        if (throwError) {
            return Try.fail("some-error")
        }
        return Try.Ok(24)
    }

    def testClassicFor() {
        {
            val list = @[Int]
            for i = 10; i < 20; i += 1 {
                list.add(i)
            }
            Assert.equal(list, @[10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
        }
    }

    def testSwitch() {
        {
            val x = switch "hello" {
                case "foo" = "got foo"
                case "hello" = "got hello"
                case "world" = "got world"
                default = "got none"
            }
            Assert.equal(x, "got hello")
        }
        {
            val x = switch "asdf" {
                case "foo" = "got foo"
                case "hello" = "got hello"
                case "world" = "got world"
                default = "got none"
            }
            Assert.equal(x, "got none")
        }
        {
            val x = switch "hello" {
                case "foo" = "got foo"
                case "hello"
                case "world" = "got world"
                default = "got none"
            }
            Assert.equal(x, "got world")
        }
        {
            val x = switch "world" {
                case "foo" = "got foo"
                case "hello"
                case "world" = "got world"
                default = "got none"
            }
            Assert.equal(x, "got world")
        }
        {
            var x = "original"
            switch "hello" {
                case "foo" {
                    x = "got foo"
                }
                case "hello" {
                    x = "got hello1"
                }
                case "world" {
                    x = "got world1"
                }
                default {
                    x = "got none"
                }
            }
            Assert.equal(x, "got hello1")
        }
        {
            var x = "original"
            switch "hello" {
                case "foo" {
                    x = "got foo"
                }
                case "hello" {
                    x = "got hello2"
                }
                case "world" {
                    x = "got world"
                }
                default {
                    x = "got none"
                }
            }
            Assert.equal(x, "got hello2")
        }
        {
            var x = "original"
            switch "hello" {
                case "foo" {
                    x = "got foo"
                }
                case "hello"
                case "world" {
                    x = "got world3"
                }
                default {
                    x = "got none"
                }
            }
            Assert.equal(x, "got world3")
        }
        {
            var x = "original"
            switch "world" {
                case "foo" {
                    x = "got foo"
                }
                case "hello"
                case "world" {
                    x = "got world4"
                }
                default {
                    x = "got none"
                }
            }
            Assert.equal(x, "got world4")
        }
    }
}
