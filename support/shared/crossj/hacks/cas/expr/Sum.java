package crossj.hacks.cas.expr;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Str;
import crossj.base.Tuple;
import crossj.base.XIterable;

/**
 * A sum of expressions.
 *
 * Associativity is always assumed.
 *
 * "Sum" expressions cannot be directly nested -- if it were allowed,
 * it would be visually confusing (if a separation or nesting is really desired
 * use a Parenthetical expression).
 */
public final class Sum implements Expression {
    private final Tuple<Expression> summands;

    private Sum(Tuple<Expression> summands) {
        Assert.that(summands.size() > 1);
        this.summands = summands;
    }

    public static Expression fromTuple(Tuple<Expression> summands) {
        if (summands.size() == 0) {
            // empty sum
            return Expression.zero();
        } else if (summands.size() == 1) {
            return summands.get(0);
        } else if (summands.iter().any(e -> (e instanceof Sum))) {
            // the nested sums have to be expanded
            var list = List.<Expression>of();
            for (var summand : summands) {
                if (summand instanceof Sum) {
                    list.addAll(((Sum) summand).summands);
                } else {
                    list.add(summand);
                }
            }
            return new Sum(Tuple.fromIterable(list));
        } else {
            return new Sum(summands);
        }
    }

    public static Expression fromIterable(XIterable<Expression> summands) {
        return fromTuple(Tuple.fromIterable(summands));
    }

    public static Expression of(Expression... summands) {
        return fromTuple(Tuple.fromJavaArray(summands));
    }

    public Tuple<Expression> getOperands() {
        return summands;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitSum(this);
    }

    @Override
    public String toString() {
        var sb = Str.builder();
        sb.s("" + summands.get(0));
        for (int i = 1; i < summands.size(); i++) {
            sb.s(" + " + summands.get(i));
        }
        return sb.build();
    }

    @Override
    public int hashCode() {
        return List.of("Sum", summands).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Sum && ((Sum) obj).summands.equals(summands);
    }
}
