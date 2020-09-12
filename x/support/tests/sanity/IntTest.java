package sanity;

import crossj.Assert;
import crossj.Int;
import crossj.M;
import crossj.Test;

public final class IntTest {
    @Test
    public static void signs() {
        Assert.equals(Int.toSigned(M.pow(2, 31) + 20), Int.MIN_VALUE + 20);
        Assert.equals(Int.toUnsigned(-1), -2.0 * Int.MIN_VALUE - 1);

        Assert.equals(Int.toU8(-1), 255);
        Assert.equals(Int.toI8(255), -1);

        Assert.equals(Int.toU16(-1), M.ipow(2, 16) - 1);
        Assert.equals(Int.toI16(M.ipow(2, 16) - 1), -1);

        Assert.equals(Int.toI16(123), 123);
        Assert.equals(Int.toI16(60000), -5536);
        Assert.equals(Int.toI8(123), 123);
        Assert.equals(Int.toI8(200), -56);
    }
}
