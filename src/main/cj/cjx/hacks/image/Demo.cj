package cjx.hacks.image


import cjx.hacks.image.Bitmap
import cjx.hacks.image.Color


class Demo {
    def main() {
        val width = 300
        val height = 300
        val radius = 150
        val bmp = Bitmap.withDimensions(width, height)

        for y = 0; y < height; y += 1 {
            for x = 0; x < width; x += 1 {
                bmp.setColor(x, y, Color.of(0.2, 0.2, 0.2, 1))
            }
        }

        for y = 0; y < height; y += 1 {
            for x = 0; x < width; x += 1 {
                val dist = (
                    (y.toDouble() - height.toDouble() / 2) ** 2 +
                    (x.toDouble() - width.toDouble() / 2) ** 2
                ) ** 0.5
                val normDist = dist / radius.toDouble()
                val lumen = 1.0 - normDist
                if lumen >= 0 and lumen <= 1 {
                    bmp.setColor(x, y, Color.of(0.3, lumen, lumen, 1))
                }
            }
        }

        FS.writeBuffer("out/demo.bmp", bmp.toBMPBuffer())
    }
}
