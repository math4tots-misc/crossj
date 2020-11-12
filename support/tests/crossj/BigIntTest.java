package crossj;

import crossj.base.Assert;
import crossj.base.BigInt;
import crossj.base.Test;

public final class BigIntTest {

    @Test
    public static void arithmetic() {
        BigInt one = BigInt.one();
        Assert.equals(one.add(one), BigInt.fromInt(2));
        // Assert.equals(one.add(one), BigInt.fromInt(3));
    }

    @Test
    public static void gcd() {
        var a = BigInt.fromInt(2424);
        var b = BigInt.fromInt(48);
        Assert.equals(a.gcd(b), BigInt.fromInt(24));
    }

    @Test
    public static void fromHex() {
        var x = BigInt.fromHexString("FF");
        Assert.equals(x, BigInt.fromInt(255));
        x = BigInt.fromHexString("af");
        Assert.equals(x, BigInt.fromInt(10 * 16 + 15));
        x = BigInt.fromHexString("12");
        Assert.equals(x, BigInt.fromInt(16 + 2));
    }

    @Test
    public static void fromOct() {
        var x = BigInt.fromOctString("77");
        Assert.equals(x, BigInt.fromInt(7 * 8 + 7));
        x = BigInt.fromOctString("44");
        Assert.equals(x, BigInt.fromInt(4 * 8 + 4));
        x = BigInt.fromOctString("12");
        Assert.equals(x, BigInt.fromInt(8 + 2));
    }
}
