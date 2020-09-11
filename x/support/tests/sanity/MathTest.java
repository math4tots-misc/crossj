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
    }
}
