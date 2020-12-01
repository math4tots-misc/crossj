package cj

class BufferTest {
    def testSample() {
        {
            var buf = Buffer.ofU8s([1, 2, 3, 4])
            Assert.equal(buf.size(), 4)
        }
        {
            val buf = Buffer.ofU8s([])
            Assert.equal(buf.size(), 0)
        }
    }

    def testI8ToU8() {
        val buf = Buffer.ofI8s([-1, -2, -3])
        Assert.equal(buf.asU8s().toList(), [255, 254, 253])
        Assert.equal(buf, Buffer.ofU8s([255, 254, 253]))
    }

    def testSlice() {
        val buf = Buffer.ofI8s([])
        var i = 0
        while (i < 100) {
            buf.addU8(i)
            i = i + 1
        }
        Assert.equal(
            buf.slice(50, 60).asU8s().toList(),
            [50, 51, 52, 53, 54, 55, 56, 57, 58, 59])
    }

    def testMultibyte() {
        {
            # should default to little endian
            val buf = Buffer.ofI8s([])
            buf.addU16(3000)
            Assert.equal(buf, Buffer.ofU8s([3000 % 256, 3000 // 256]))
        }
        {
            # try again with big endian
            val buf = Buffer.ofI8s([])
            buf.useBigEndian(true)
            buf.addU16(3000)
            Assert.equal(buf, Buffer.ofU8s([3000 // 256, 3000 % 256]))
            Assert.that(buf != Buffer.ofU8s([3000 % 256, 3000 // 256]))
        }
    }

    def testStr() {
        val buf = Buffer.ofI8s([1, -2, 3, -4])
        Assert.equal(buf.repr(), "Bytes.ofU8s([1, 254, 3, 252])")
    }

    def testASCII() {
        val buf = Buffer.fromASCII("hello")
        Assert.equal(buf, Buffer.ofU8s([104, 101, 108, 108, 111]))
    }

    def testAddI32() {
        val buf = Buffer.withCapacity(24)
        val x = 2000
        val y = 3000
        val z = 1000000
        buf.addI32(x)
        buf.addI32(y)
        buf.addI32(z)
        Assert.equal(buf, Buffer.ofU8s([
            x % 256, x // 256, 0, 0,
            y % 256, y // 256, 0, 0,
            z % 256, (z // 256) % 256, (z // 256 // 256) % 256, z // 256 // 256 // 256,
        ]))
    }

    def testClone() {
        val buf = Buffer.ofU8s([1, 2, 3, 4, 5])
        val clone = buf.clone()
        # Assert.that(buf is not clone)
        Assert.equal(buf, clone)
        clone.setU16(0, 1000)
        Assert.equal(buf, Buffer.ofU8s([1, 2, 3, 4, 5]))
        Assert.equal(clone, Buffer.ofU8s([232, 3, 3, 4, 5]))
    }
}
