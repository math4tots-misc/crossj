package cjx.hacks.image

import cjx.hacks.image.Color

class Bitmap {
    val _width: Int

    # array of 32-bit RGBA data
    val _data: MutableList[Int]

    def _new(width: Int, data: MutableList[Int]): Bitmap {
        Assert.divides(width, data.size())
        return Self.new(width, data)
    }

    def withDimensions(width: Int, height: Int): Bitmap {
        val data = MutableList.ofSize(width * height, i -> 0)
        return Self._new(width, data)
    }

    def fromColors[CC: Iterable[Color]](width: Int, colors: CC): Bitmap {
        val data = MutableList.fromIterable(colors.iter().map(cl -> cl.toI32RGBA()))
    }

    def width(self: Self): Int {
        return self._width
    }

    def height(self: Self): Int {
        return self._data.size() // self._width
    }

    def setColor(self: Self, x: Int, y: Int, color: Color) {
        val i = y * self._width + x
        self._data.set(i, color.toI32RGBA())
    }

    def getColor(self: Self, x: Int, y: Int): Color {
        val i = y * self._width + x
        return Color.fromI32RGBA(self._data.get(i))
    }

    # Converts the bitmap data into 32bpp BMP data, and returns
    # the contents as a Buffer.
    # This Buffer object can be directly written out to a file
    # and be a valid *.bmp.
    #
    # Reference: https://en.wikipedia.org/wiki/BMP_file_format
    #
    def toBMPBuffer(self: Self): Buffer {
        val bytesize = 14 + 40 + 4 * self._data.size()
        val width = self.width()
        val height = self.height()
        val out = Buffer.withCapacity(bytesize)

        # -- file header --
        out.addASCII("BM") # magic
        out.addI32(bytesize) # size of BMP file in bytes
        out.addU16(0) # reserved
        out.addU16(0) # reserved
        out.addI32(14 + 40) # offset to start of pixel data

        # -- dib header (Windows BITMAPINFOHEADER) --
        out.addI32(40) # size of this header in bytes
        out.addI32(width) # width in pixels
        out.addI32(height) # height in pixels
        out.addU16(1) # # color planes (must be 1?)
        out.addU16(32) # bits per pixel
        out.addI32(0) # compression (none)
        out.addI32(0) # uncompressed image size (if compressed)
        out.addI32(2835) # horizontal resolution (2835 used in wiki example)
        out.addI32(2835) # vertical resolution (2835 used in wiki example)
        out.addI32(0) # # colors in palette (palette is not used here)
        out.addI32(0) # # important colors (generally ignored)
        Assert.equal(out.size(), 14 + 40);

        for y = height - 1; y >= 0; y -= 1 {
            for x = 0; x < width; x += 1 {
                out.addI32(self.getColor(x, y).toI32ARGB())
            }
        }
        Assert.equal(out.size(), bytesize)

        return out
    }
}
