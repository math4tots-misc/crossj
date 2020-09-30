package sanity;

import crossj.*;

public final class MathTest {
    @Test
    public static void misc() {
        Assert.that(M.abs(M.PI - 3.141592653589793) < 0.0000001);
    }

    @Test
    public static void abs() {
        Assert.equals(M.abs(-12), 12.0);
    }

    @Test
    public static void intDivision() {
        Assert.equals(5 / 2, 2);
        Assert.equals(5 / 2.0, 2.5);
        Assert.equals(5.0 / 2, 2.5);

        Assert.equals(7 / 2 + 1.0, 4.0);

        Assert.equals(1234 / 10 / 10.0 * 100, 1230.0);
        Assert.equals(1234 / 10.0 / 10 * 100, 1234.0);
    }

    @Test
    public static void pow() {
        Assert.equals(M.pow(2, 3), 8.0);
    }

    @Test
    public static void extrema() {
        Assert.equals(M.max(1, 2, 3), 3.0);
        Assert.equals(M.max(1, 2, -13), 2.0);
        Assert.equals(M.min(1, 2, 3), 1.0);
        Assert.equals(M.min(1, 2, -13), -13.0);
        Assert.equals(M.imax(1, 2, 3), 3);
        Assert.equals(M.imax(1, 2, -13), 2);
        Assert.equals(M.imin(1, 2, 3), 1);
        Assert.equals(M.imin(1, 2, -13), -13);
    }

    @Test
    public static void round() {
        Assert.equals(M.round(3), 3.0);
        Assert.equals(M.round(3.5), 4.0);
        Assert.equals(M.round(3.4), 3.0);
    }

    @Test
    public static void log() {
        Assert.equals(M.ln(M.E), 1.0);
        Assert.equals(M.ln(M.E * M.E), 2.0);
    }

    @Test
    public static void gcd() {
        Assert.equals(M.gcd(23, 24), 1);
        Assert.equals(M.gcd(15, 45), 15);
        Assert.equals(M.gcd(123454321, 1234321), 1);
        Assert.equals(M.gcd(124124, 2442), 22);
    }
}
