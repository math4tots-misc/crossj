package crossj.hacks.cas.expr;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Tuple;
import crossj.base.XIterable;

/**
 * Product of expressions.
 *
 * Associativity is always assumed.
 *
 * Like sums, products may not nest directly.
 */
public final class Product implements Expression {
    private final Tuple<Expression> factors;

    private Product(Tuple<Expression> factors) {
        Assert.that(factors.size() > 1);
        this.factors = factors;
    }

    public static Expression fromTuple(Tuple<Expression> factors) {
        if (factors.size() == 0) {
            // empty product
            return Expression.one();
        } else if (factors.size() == 1) {
            return factors.get(0);
        } else if (factors.iter().any(e -> (e instanceof Product))) {
            // nested products have to be expanded
            var list = List.<Expression>of();
            for (var factor : factors) {
                if (factor instanceof Product) {
                    list.addAll(((Product) factor).factors);
                } else {
                    list.add(factor);
                }
            }
            return new Product(Tuple.fromIterable(list));
        } else {
            return new Product(factors);
        }
    }

    public static Expression fromIterable(XIterable<Expression> factors) {
        return fromTuple(Tuple.fromIterable(factors));
    }

    public static Expression of(Expression... factors) {
        return fromTuple(Tuple.fromJavaArray(factors));
    }

    public Tuple<Expression> getOperands() {
        return factors;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitProduct(this);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (var factor : factors) {
            sb.append("(" + factor + ")");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return List.of("Product", factors).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Product && ((Product) obj).factors.equals(factors);
    }
}
