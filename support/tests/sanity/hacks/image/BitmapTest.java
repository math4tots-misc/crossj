package sanity.hacks.image;

import crossj.base.Assert;
import crossj.base.Bytes;
import crossj.base.IO;
import crossj.base.Test;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;

public final class BitmapTest {

    /**
     * This test really needs to be checked manually to see if it ran correctly
     * the smallSample test is very similar except uses much fewer pixels so that
     * the values can be checked pixel by pixel here.
     *
     * This test writes out a file to out/sample.bmp
     *
     * You should see an image 100px by 100px:
     * <li>upper-left corner should be black</li>
     * <li>upper-right corner should be red</li>
     * <li>lower-left corner should be green</li>
     * <li>lower-right corner should be yellow (both red and green)</li>
     * <li>all other pixels should be colors in between in a smooth gradient</li>
     */
    @Test
    public static void manualSample() {
        int width = 100, height = 100;
        Bitmap bmp = Bitmap.withDimensions(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bmp.setColor(x, y, Color.of(((double) x) / width, ((double) y) / height, 0, 1));
            }
        }
        IO.writeFileBytes("out/sample.bmp", bmp.toBMPBytes());
    }

    @Test
    public static void smallSample() {
        int width = 4, height = 4;
        Bitmap bmp = Bitmap.withDimensions(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixel = Color.of(((double) x) / width, ((double) y) / height, 0, 1);
                bmp.setColor(x, y, pixel);
            }
        }
        Bytes bytes = bmp.toBMPBytes();
        Assert.equals(bytes.size(), 14 + 40 + width * height * 4);
        Bytes data = bytes.getBytes(14 + 40, bytes.size());
        Assert.equals(data, Bytes.ofU8s(
                // NOTE: pixel channels appear in BGRA order when
                // written out in little endian.
                // NOTE: BMP expects pixel data to start from the lower left corner
                // moving up and to the right
                // (0, 3)
                0, 192, 0, 255,
                // (1, 3)
                0, 192, 64, 255,
                // (2, 3)
                0, 192, 128, 255,
                // (3, 3)
                0, 192, 192, 255,
                // (0, 2)
                0, 128, 0, 255,
                // (1, 2)
                0, 128, 64, 255,
                // (2, 2)
                0, 128, 128, 255,
                // (3, 2)
                0, 128, 192, 255,
                // (0, 1)
                0, 64, 0, 255,
                // (1, 1)
                0, 64, 64, 255,
                // (2, 1)
                0, 64, 128, 255,
                // (3, 1)
                0, 64, 192, 255,
                // (0, 0)
                0, 0, 0, 255,
                // (1, 0)
                0, 0, 64, 255,
                // (2, 0)
                0, 0, 128, 255,
                // (3, 0)
                0, 0, 192, 255));
    }
}
