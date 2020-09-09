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
}
