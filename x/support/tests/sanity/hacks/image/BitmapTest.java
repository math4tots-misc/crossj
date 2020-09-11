package sanity.hacks.image;

import crossj.Assert;
import crossj.Bytes;
import crossj.IO;
import crossj.Test;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Pixel;

public final class BitmapTest {

    /**
     * This test really needs to be checked manually to see if it works
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
                bmp.setPixel(x, y, Pixel.of(((double) x) / width, ((double) y) / height, 0, 1));
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
                Pixel pixel = Pixel.of(((double) x) / width, ((double) y) / height, 0, 1);
                bmp.setPixel(x, y, pixel);
            }
        }
        Bytes bytes = bmp.toBMPBytes();
        Assert.equals(bytes.size(), 14 + 40 + width * height * 4);
        Bytes data = bytes.getBytes(14 + 40, bytes.size());
        Assert.equals(data,
                Bytes.ofU8s(0, 192, 0, 255, 0, 192, 64, 255, 0, 192, 128, 255, 0, 192, 192, 255, 0, 128, 0, 255, 0, 128,
                        64, 255, 0, 128, 128, 255, 0, 128, 192, 255, 0, 64, 0, 255, 0, 64, 64, 255, 0, 64, 128, 255, 0,
                        64, 192, 255, 0, 0, 0, 255, 0, 0, 64, 255, 0, 0, 128, 255, 0, 0, 192, 255));
    }
}
