package crossj;

import crossj.base.Assert;
import crossj.base.Int;
import crossj.base.M;
import crossj.base.Test;

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

    @Test
    public static void hexLiteralOverflow() {
        // Potential issue here -- in Java, (positive) values you provide in hex that don't fit in
        // signed 32-bit int, will automatically return their negative value counterpart.
        // In some target languages (e.g. JS), if this isn't handled carefully, it may just result in
        // an invalid value that is outside the expected range (since a JS number can fit all unsinged
        // 32-bit int values).
        // We check that this is properly handled.
        Assert.equals(0xe8b7be43, -390611389);
    }
}
