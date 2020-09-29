package crossj.hacks.cas.algo;

import crossj.BigInt;
import crossj.Optional;
import crossj.Pair;
import crossj.hacks.cas.AlgebraContext;
import crossj.hacks.cas.expr.Expression;
import crossj.hacks.cas.expr.ExpressionVisitor;
import crossj.hacks.cas.expr.Fraction;
import crossj.hacks.cas.expr.IntegerLiteral;
import crossj.hacks.cas.expr.Parenthetical;
import crossj.hacks.cas.expr.Product;
import crossj.hacks.cas.expr.Sum;
import crossj.hacks.cas.expr.Variable;

/**
 * Tries to reduce the expression to a rational literal number in lowest terms,
 * if possible, on best effort.
 *
 * If the reduction succeeds, calling 'asBigIntFraction()' should never return
 * empty.
 */
public final class ReduceToRationalLiteral implements ExpressionVisitor<Optional<Expression>> {
    private final AlgebraContext ctx;

    private ReduceToRationalLiteral(AlgebraContext ctx) {
        this.ctx = ctx;
    }

    public static ReduceToRationalLiteral withContext(AlgebraContext ctx) {
        return new ReduceToRationalLiteral(ctx);
    }

    @Override
    public Optional<Expression> visitFraction(Fraction e) {
        var tryNum = e.getNumerator().accept(this);
        if (tryNum.isEmpty()) {
            return Optional.empty();
        }
        var tryDen = e.getDenominator().accept(this);
        if (tryDen.isEmpty()) {
            return Optional.empty();
        }
        var num = tryNum.get();
        var den = tryDen.get();
        var frac1 = num.asBigIntFraction().get();
        var frac2 = den.asBigIntFraction().get();
        var newNumerator = frac1.get1().multiply(frac2.get2());
        var newDenominator = frac1.get2().multiply(frac2.get1());
        return Optional.of(Fraction.reduceFromBigInts(newNumerator, newDenominator));
    }

    @Override
    public Optional<Expression> visitIntegerLiteral(IntegerLiteral e) {
        return Optional.of(e);
    }

    @Override
    public Optional<Expression> visitParenthetical(Parenthetical e) {
        return e.getInner().accept(this);
    }

    @Override
    public Optional<Expression> visitProduct(Product e) {
        var pair = Pair.of(BigInt.one(), BigInt.one());
        for (var factor : e.getOperands()) {
            var tryFrac = factor.accept(this);
            if (tryFrac.isEmpty()) {
                return Optional.empty();
            }
            var frac = tryFrac.get().asBigIntFraction().get();
            pair = Fraction.reduceBigInts(pair.get1().multiply(frac.get1()), pair.get2().multiply(frac.get2()));
        }
        return Optional.of(Expression.ofReducedBigInts(pair.get1(), pair.get2()));
    }

    @Override
    public Optional<Expression> visitSum(Sum e) {
        var pair = Pair.of(BigInt.zero(), BigInt.one());
        for (var summand : e.getOperands()) {
            var tryFrac = summand.accept(this);
            if (tryFrac.isEmpty()) {
                return Optional.empty();
            }
            var frac = tryFrac.get().asBigIntFraction().get();
            pair = Fraction.reduceBigInts(pair.get1().multiply(frac.get2()).add(pair.get2().multiply(frac.get1())),
                    pair.get2().multiply(frac.get2()));
        }
        return Optional.of(Expression.ofReducedBigInts(pair.get1(), pair.get2()));
    }

    @Override
    public Optional<Expression> visitVariable(Variable e) {
        return ctx.getSubstitutionForVariable(e).flatMap(newExpr -> newExpr.accept(this));
    }
}
