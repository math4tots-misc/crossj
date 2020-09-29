package sanity;

import crossj.Assert;
import crossj.BigInt;
import crossj.Test;

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
}
