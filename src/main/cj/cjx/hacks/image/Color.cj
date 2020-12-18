package cjx.hacks.image


class Color {
    static var white: Self = Self.rgb(1, 1, 1)

    val r: Double
    val g: Double
    val b: Double
    val a: Double

    def of(r: Double, g: Double, b: Double, a: Double): Self {
        return Self.new(r, g, b, a)
    }

    def rgb(r: Double, g: Double, b: Double): Self {
        return Self.of(r, g, b, 1)
    }

    def fromI32RGBA(rgba: Int): Self {
        val (r, g, b, a) = Self._splitIntoDouble(rgba)
        return Self.of(r, g, b, a)
    }

    def toI32RGBA(self: Self): Int {
        return Self._combineFloats(self.r, self.g, self.b, self.a)
    }

    def toI32ARGB(self: Self): Int {
        return Self._combineFloats(self.a, self.r, self.g, self.b)
    }

    def _split(x: Int): Tuple[Int, Int, Int, Int] {
        return (
            (x >>> 24) & 0xFF,
            (x >>> 16) & 0xFF,
            (x >>> 8) & 0xFF,
            x & 0xFF,
        )
    }

    def _splitIntoDouble(x: Int): Tuple[Double, Double, Double, Double] {
        val (a, b, c, d) = Self._split(x)
        return (
            Self._intToFloatChannel(a),
            Self._intToFloatChannel(b),
            Self._intToFloatChannel(c),
            Self._intToFloatChannel(d),
        )
    }

    def _floatToIntChannel(x: Double): Int {
        # TODO: here and _intToFloatChannel: better conversion method
        val i = (x * 256).toInt();
        return (
            if (i < 0) 0 else
            if (i > 255) 255 else
            i
        );
    }

    def _intToFloatChannel(x: Int): Double {
        return if (x == 255) 1.0 else x.toDouble() / 256
    }

    def _combine(a: Int, b: Int, c: Int, d: Int): Int {
        return (a << 24) | (b << 16) | (c << 8) | d
    }

    def _combineFloats(a: Double, b: Double, c: Double, d: Double): Int {
        return Self._combine(
            Self._floatToIntChannel(a),
            Self._floatToIntChannel(b),
            Self._floatToIntChannel(c),
            Self._floatToIntChannel(d),
        )
    }
}
