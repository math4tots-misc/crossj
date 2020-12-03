package cj.hacks.image

import cj.hacks.image.Color

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
}
