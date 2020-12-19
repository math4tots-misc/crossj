package cj

class StringTest {

    def testMisc() {
        {
            val list = "Hello World asdf".split(" ")
            Assert.equal(list, ["Hello", "World", "asdf"])
        }
        Assert.equal(
            "Hello world  asdf  qwer".split("  "),
            ["Hello world", "asdf", "qwer"],
        )
        Assert.withMessage("Hello".startsWith("Hell"), "Hello starts with Hell")
        Assert.withMessage(not "Hello".startsWith("hello"), "Hello does not start with hello")
        Assert.withMessage("Hello".endsWith("ello"), "Hello ends with ello")
        Assert.withMessage(not "Hello".endsWith("ell"), "Hello does not end with ell")
        Assert.withMessage("ell" in "Hello", "ell appears in Hello")
        Assert.withMessage("hell" not in "Hello", "hell does not appear in Hello")
        Assert.withMessage("Hell" in "Hello", "Hell appears in Hello")
        Assert.equal("Hello world".cut(1, 7), "ello w")
        Assert.equal("Hello world".cutFrom(1), "ello world")
    }

    def testCharToInt() {
        Assert.equal('a'.toInt(), 97)
        Assert.equal('A'.toInt(), 65)
        Assert.equal('한'.toInt(), 54620)

        Assert.equal((97).toChar(), 'a')
        Assert.equal((98).toChar(), 'b')
        Assert.equal((54620).toChar(), '한')
    }

    def testCharToString() {
        Assert.equal('x'.toString(), "x")
    }

    def testIter() {
        {
            val chars = "hello world".iter().toList()
            Assert.equal(chars, ['h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd'])
        }
        {
            val chars = "한글".iter().toList()
            Assert.equal(chars, ['한', '글'])
        }
        {
            val chars = "a한글b".iter().toList()
            Assert.equal(chars, ['a', '한', '글', 'b'])
        }
        {
            val chars = @[Char]
            for c in "xyz" {
                chars.add(c)
            }
            Assert.equal(chars, @['x', 'y', 'z'])
        }
    }

    def testHash() {
        # For better or for worse, these should match the java hash results.
        # TODO: Consider whether hashes should vary
        Assert.equal("hello".hash(), 99162322)
        Assert.equal("world".hash(), 113318802)
    }

    def testAdd() {
        Assert.equal("asdf" + [1, 2, 3], "asdf[1, 2, 3]")
        Assert.equal("hello" + " " + "world", "hello world")
    }

    def testBuilder() {
        {
            val sb = String.builder()
            sb.add("hello ")
            sb.add("world")
            Assert.equal(sb.build(), "hello world")
        }
    }

    def testEndAndStart() {
        Assert.withMessage("asdf".startsWith("as"), "starts with as")
        Assert.withMessage("asdf".startsWith("asdf"), "starts with asdf")
        Assert.withMessage(not "asdf".startsWith("asdfx"), "starts with asdfx")
        Assert.withMessage(not "asdf".startsWith("qwer"), "starts with qwer")

        Assert.withMessage("qwerasdf".startsWithAt("as", 4), "starts with at as")
        Assert.withMessage("qwerasdf".startsWithAt("asdf", 4), "starts with at asdf")
        Assert.withMessage(not "qwerasdf".startsWithAt("asdfx", 4), "starts with at asdfx")
        Assert.withMessage(not "qwerasdf".startsWithAt("qwer", 4), "starts with at qwer")

        Assert.withMessage("asdf".endsWith("df"), "ends with df")
        Assert.withMessage("asdf".endsWith("asdf"), "ends with asdf")
        Assert.withMessage(not "asdf".endsWith("xasdf"), "ends with xasdf")
        Assert.withMessage(not "asdf".endsWith("asdfx"), "ends with asdfx")
        Assert.withMessage(not "asdf".endsWith("qwer"), "ends with qwer")

        Assert.withMessage(not "qwerasdf".endsWithAt("df", 4), "ends with at df")
        Assert.withMessage(not "qwerasdf".endsWithAt("asdf", 4), "ends with at asdf")
        Assert.withMessage(not "qwerasdf".endsWithAt("asdfx", 4), "ends with at asdfx")
        Assert.withMessage("qwerasdf".endsWithAt("qwer", 4), "ends with at qwer")
        Assert.withMessage("qwerasdf".endsWithAt("er", 4), "ends with at er")
    }

    def testCharRepr() {
        {
            val r = 0x0B.toChar().repr()
            Assert.equal(r, "'\\x0B'")
        }
    }
}
