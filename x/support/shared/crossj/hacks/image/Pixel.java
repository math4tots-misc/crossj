package crossj.hacks.image;

import crossj.List;

/**
 * Pixel data
 *
 * the to/from I32* methods assume "big endian" (MSB to LSB) when interpreting
 * the colors.
 *
 * So e.g. RGBA means that the red channel occupies the most
 * significant byte. For better or for worse, it just seems that's how these
 * colors tend to be described in most docs I've been reading.
 */
public final class Pixel {
    public final double r;
    public final double g;
    public final double b;
    public final double a;

    private Pixel(double r, double g, double b, double a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public static Pixel of(double r, double g, double b, double a) {
        return new Pixel(r, g, b, a);
    }

    public static Pixel fromI32RGBA(int rgba) {
        List<Double> channels = splitIntoDouble(rgba);
        return of(channels.get(0), channels.get(1), channels.get(2), channels.get(3));
    }

    public int toI32RGBA() {
        return combineFloats(r, g, b, a);
    }

    public int toI32ARGB() {
        return combineFloats(a, r, g, b);
    }

    private static int floatToIntChannel(double x) {
        int i = (int) (x * 256);
        return i < 0 ? 0 : i > 255 ? 255 : i;
    }

    private static double intToFloatChannel(int x) {
        return ((double) x) / 256;
    }

    private static int combine(int a, int b, int c, int d) {
        return (a << 24) | (b << 16) | (c << 8) | d;
    }

    private static int combineFloats(double a, double b, double c, double d) {
        return combine(floatToIntChannel(a), floatToIntChannel(b), floatToIntChannel(c), floatToIntChannel(d));
    }

    private static List<Integer> split(int x) {
        return List.of((x >>> 24) & 0xFF, (x >>> 16) & 0xFF, (x >>> 8) & 0xFF, x & 0xFF);
    }

    private static List<Double> splitIntoDouble(int x) {
        return split(x).map(i -> intToFloatChannel(i));
    }

    @Override
    public String toString() {
        return "Pixel.of(" + r + ", " + g + ", " + b + ", " + a + ")";
    }
}
