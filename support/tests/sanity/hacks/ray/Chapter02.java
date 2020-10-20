package sanity.hacks.ray;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Str;
import crossj.base.Test;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;

/**
 * Chapter 2: Drawing on a Canvas
 */
public final class Chapter02 {
    @Test
    public static void colors() {
        {
            // Colors are (red, green, blue) tuples
            // actually Colors are not tuples (Matrix) in this implementation.
            Color c = Color.rgb(-0.5, 0.4, 1.7);
            Assert.equals(c.r, -0.5);
            Assert.equals(c.g, 0.4);
            Assert.equals(c.b, 1.7);
        }
        {
            // adding colors
            Color c1 = Color.rgb(0.9, 0.6, 0.75);
            Color c2 = Color.rgb(0.7, 0.1, 0.25);
            Assert.equals(c1.add(c2), Color.rgb(1.6, 0.7, 1.0));
        }
        {
            // subtracting coors
            Color c1 = Color.rgb(0.9, 0.6, 0.75);
            Color c2 = Color.rgb(0.7, 0.1, 0.25);
            Assert.almostEquals(c1.subtract(c2), Color.rgb(0.2, 0.5, 0.5));
        }
        {
            // multiply by a scalar
            Color c = Color.rgb(0.2, 0.3, 0.4);
            Assert.almostEquals(c.scale(2), Color.rgb(0.4, 0.6, 0.8));
        }
        {
            // multiplying colors
            Color c1 = Color.rgb(1, 0.2, 0.4);
            Color c2 = Color.rgb(0.9, 1, 0.1);
            Assert.almostEquals(c1.hadamardProduct(c2), Color.rgb(0.9, 0.2, 0.04));
        }
    }

    @Test
    public static void canvas() {
        // I just use my Bitmap class in place for Canvas
        {
            Bitmap c = Bitmap.withDimensions(10, 20);
            Assert.equals(c.getWidth(), 10);
            Assert.equals(c.getHeight(), 20);
            Assert.withMessage(c.colors().all(cl -> cl.equals(Color.of(0, 0, 0, 0))),
                    "Bitmaps should start out all black");
        }
        {
            // writing pixels to a canvas
            Bitmap c = Bitmap.withDimensions(10, 20);
            Color red = Color.rgb(1, 0, 0);
            c.setColor(2, 3, red);
            Assert.equals(c.getColor(2, 3), red);
        }
    }

    @Test
    public static void ppm() {
        {
            Bitmap c = Bitmap.withDimensions(5, 3);
            String ppm = c.toP3();
            List<String> lines = Str.lines(ppm);
            Assert.equals(Str.join("\n", lines.iter().take(3)), "P3\n5 3\n255");
        }
        {
            Bitmap c = Bitmap.withDimensions(5, 3);
            Color c1 = Color.rgb(1.5, 0, 0);
            Color c2 = Color.rgb(0, 0.5, 0);
            Color c3 = Color.rgb(-0.5, 0, 2);
            c.setColor(0, 0, c1);
            c.setColor(2, 1, c2);
            c.setColor(4, 2, c3);
            String ppm = c.toP3();
            // lines 4-6
            List<String> lines = Str.lines(ppm).iter().skip(3).take(3).list();
            Assert.equals(lines, List.of("255 0 0 0 0 0 0 0 0 0 0 0 0 0 0", "0 0 0 0 0 0 0 128 0 0 0 0 0 0 0",
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 0 255"));

            // eh, skip the other PPM tests -- I'm going to be emitting bmp files instead anyway.
        }
    }
}
