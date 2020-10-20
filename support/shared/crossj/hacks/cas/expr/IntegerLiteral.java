package crossj.hacks.cas.expr;

import crossj.base.BigInt;
import crossj.base.Optional;

/**
 * An integer literal expression.
 */
public final class IntegerLiteral implements Expression {
    static final IntegerLiteral ZERO = fromInt(0);
    static final IntegerLiteral ONE = fromInt(1);
    static final IntegerLiteral MINUS_ONE = fromInt(-1);

    private final BigInt value;

    private IntegerLiteral(BigInt value) {
        this.value = value;
    }

    public static IntegerLiteral fromInt(int i) {
        return fromBigInt(BigInt.fromInt(i));
    }

    public static IntegerLiteral fromBigInt(BigInt i) {
        return new IntegerLiteral(i);
    }

    public BigInt getValue() {
        return value;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitIntegerLiteral(this);
    }

    @Override
    public Optional<IntegerLiteral> asIntegerLiteral() {
        return Optional.of(this);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntegerLiteral && ((IntegerLiteral) obj).value.equals(value);
    }
}
