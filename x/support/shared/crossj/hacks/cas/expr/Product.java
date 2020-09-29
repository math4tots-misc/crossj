package crossj.hacks.cas.expr;

import crossj.List;
import crossj.Tuple;
import crossj.XIterable;

/**
 * Product of expressions.
 *
 * Like sums, products may not nest directly.
 */
public final class Product implements Expression {
    private final Tuple<Expression> factors;

    private Product(Tuple<Expression> factors) {
        this.factors = factors;
    }

    public static Expression fromTuple(Tuple<Expression> factors) {
        if (factors.size() == 0) {
            // empty product
            return Number.one();
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

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (var factor : factors) {
            sb.append("(" + factor + ")");
        }
        return sb.toString();
    }
}
