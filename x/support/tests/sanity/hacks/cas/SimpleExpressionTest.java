package sanity.hacks.cas;

import crossj.Assert;
import crossj.BigInt;
import crossj.Pair;
import crossj.Test;
import crossj.hacks.cas.AlgebraContext;
import crossj.hacks.cas.expr.Expression;
import crossj.hacks.cas.expr.Product;
import crossj.hacks.cas.expr.Sum;

public final class SimpleExpressionTest {
    @Test
    public static void intoString() {
        var sum = Sum.of(Product.of(Expression.fromInt(2), Expression.ofInts(1, 2)), Expression.fromInt(5));
        Assert.equals(sum.toString(), "(2)(1/2) + 5");
    }

    @Test
    public static void simplifyRationals() {
        var ctx = AlgebraContext.getDefault();
        {
            var expr = Expression.ofInts(4, 5);
            Assert.equals(expr.asBigIntFraction().get(), Pair.of(BigInt.fromInt(4), BigInt.fromInt(5)));
        }
        {
            var expr = Product.of(Expression.ofInts(4, 5), Expression.ofInts(4, 5));
            Assert.equals(ctx.reduceToRationalLiteral(expr).get(), Expression.ofReducedInts(16, 25));
        }
        {
            var expr = Sum.of(Expression.ofInts(1, 3), Product.of(Expression.ofInts(4, 5), Expression.ofInts(4, 5)));
            Assert.equals(ctx.reduceToRationalLiteral(expr).get(), Expression.ofInts(25 + 48, 75));
        }
    }
}
