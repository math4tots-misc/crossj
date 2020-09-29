package sanity.hacks.cas;

import crossj.Assert;
import crossj.Test;
import crossj.hacks.cas.expr.Number;
import crossj.hacks.cas.expr.Product;
import crossj.hacks.cas.expr.Rational;
import crossj.hacks.cas.expr.Sum;

public final class SimpleExpressionTest {
    @Test
    public static void intoString() {
        var sum = Sum.of(Product.of(Number.fromInt(2), Rational.ofInts(1, 2)), Number.fromInt(5));
        Assert.equals(sum.toString(), "(2)(1/2) + 5");
    }
}
