package crossj.hacks.cas.expr;

/**
 * Exponential expressions.
 */
public final class Power implements Expression {
    private final Expression base;
    private final Expression exponent;

    private Power(Expression base, Expression exponent) {
        this.base = base;
        this.exponent = exponent;
    }

    public static Power of(Expression base, Expression exponent) {
        return new Power(base, exponent);
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitPower(this);
    }

    public Expression getBase() {
        return base;
    }

    public Expression getExponent() {
        return exponent;
    }
}
