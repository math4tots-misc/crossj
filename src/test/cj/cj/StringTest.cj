package cj

class StringTest {
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
                chars.push(c)
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
}
