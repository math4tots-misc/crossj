package crossj.hacks.image;

import crossj.Assert;
import crossj.Bytes;
import crossj.IntArray;
import crossj.List;
import crossj.XIterable;
import crossj.XIterator;

/**
 * Quick and dirty class for playing with bitmaps
 *
 * Coordinates start from (0, 0) in the upper-left corner
 *
 * (NOTE, this is different from the BMP file format which starts from (0, 0) in
 * the lower-left corner).
 *
 */
public final class Bitmap {
    private final int width;
    private final IntArray data; // array of 32-bit RGBA data

    private Bitmap(int width, IntArray data) {
        Assert.divides(width, data.size());
        this.width = width;
        this.data = data;
    }

    public static Bitmap withDimensions(int width, int height) {
        IntArray data = IntArray.withSize(width * height);
        return new Bitmap(width, data);
    }

    public static Bitmap fromColors(int width, XIterable<Color> colors) {
        IntArray data = IntArray.fromIterable(colors.iter().map(cl -> cl.toI32RGBA()));
        return new Bitmap(width, data);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return data.size() / width;
    }

    public void setColor(int x, int y, Color pixel) {
        int i = y * width + x;
        data.set(i, pixel.toI32RGBA());
    }

    public Color getColor(int x, int y) {
        int i = y * width + x;
        return Color.fromI32RGBA(data.get(i));
    }

    public XIterator<Color> colors() {
        return data.iter().map(x -> Color.fromI32RGBA(x));
    }

    /**
     * Converts out the bitmap data into 32bpp BMP data, and returns the contents as
     * a Bytes value. This Bytes object can be directly written out to a file and be
     * a valid *.bmp.
     *
     * Reference: https://en.wikipedia.org/wiki/BMP_file_format
     *
     * @return
     */
    public Bytes toBMPBytes() {
        // 14 (file header)
        // 40 (dib header (Windows BITMAPINFOHEADER))
        // 4 * data.size() (pixel data)
        int bytesize = 14 + 40 + 4 * data.size();
        int width = getWidth();
        int height = getHeight();

        Bytes out = Bytes.withCapacity(bytesize);

        // -- file header --
        out.addASCII("BM"); // magic
        out.addI32(bytesize); // size of BMP file in bytes
        out.addU16(0); // reserved
        out.addU16(0); // reserved
        out.addI32(14 + 40); // offset to start of pixel data
        Assert.equals(out.size(), 14);

        // -- dib header (Windows BITMAPINFOHEADER) --
        out.addI32(40); // size of this header in bytes
        out.addI32(width); // width in pixels
        out.addI32(height); // height in pixels
        out.addU16(1); // # color planes (must be 1?)
        out.addU16(32); // bits per pixel
        out.addI32(0); // compression (none)
        out.addI32(0); // uncompressed image size (if compressed)
        out.addI32(2835); // horizontal resolution (2835 used in wiki example)
        out.addI32(2835); // vertical resolution (2835 used in wiki example)
        out.addI32(0); // # colors in palette (palette is not used here)
        out.addI32(0); // # important colors (generally ignored)
        Assert.equals(out.size(), 14 + 40);

        // -- pixel data --
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                out.addI32(getColor(x, y).toI32ARGB());
            }
        }
        Assert.equals(out.size(), bytesize);

        return out;
    }

    /**
     * Returns a plain PPM representation of this bitmap
     *
     * NOTE: technically, this doesn't always produce valid P3 PPM files
     * since P3 actually requires making sure lines do not exceed 70 characters.
     * This implementation doesn't really cut things off properly.
     *
     * Primarily for testing
     */
    public String toP3() {
        StringBuilder sb = new StringBuilder();
        int width = getWidth();
        int height = getHeight();
        sb.append("P3\n");
        sb.append(width + " " + height + "\n");
        sb.append("255\n");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x > 0) {
                    sb.append(" ");
                }
                Color color = getColor(x, y);
                List<Integer> channels = color.toIntegerList();
                sb.append(channels.get(0) + " " + channels.get(1) + " " + channels.get(2));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
