package cj

import cj.Assert


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
            var chars = "hello world".iter().toList()
            Assert.equal(chars, ['h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd'])
        }
        {
            var chars = "한글".iter().toList()
            Assert.equal(chars, ['한', '글'])
        }
        {
            var chars = "a한글b".iter().toList()
            Assert.equal(chars, ['a', '한', '글', 'b'])
        }
        {
            var chars = @[Char]
            for c in "xyz" {
                chars.push(c)
            }
            Assert.equal(chars, @['x', 'y', 'z'])
        }
    }
}
