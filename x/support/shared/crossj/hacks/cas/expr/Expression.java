package crossj.hacks.cas.expr;

import crossj.base.BigInt;
import crossj.base.Optional;
import crossj.base.Pair;

/**
 * An abstract algebraic expression.
 */
public interface Expression {
    public static IntegerLiteral fromInt(int i) {
        return IntegerLiteral.fromInt(i);
    }

    public static IntegerLiteral fromBigInt(BigInt i) {
        return IntegerLiteral.fromBigInt(i);
    }

    public static Fraction ofInts(int numerator, int denominator) {
        return Fraction.ofInts(numerator, denominator);
    }

    public static Expression ofReducedInts(int numerator, int denominator) {
        return Fraction.reduceFromInts(numerator, denominator);
    }

    public static Fraction ofBigInts(BigInt numerator, BigInt denominator) {
        return Fraction.ofBigInts(numerator, denominator);
    }

    public static Expression ofReducedBigInts(BigInt numerator, BigInt denominator) {
        return Fraction.reduceFromBigInts(numerator, denominator);
    }

    public static IntegerLiteral zero() {
        return IntegerLiteral.ZERO;
    }

    public static IntegerLiteral one() {
        return IntegerLiteral.ONE;
    }

    public static IntegerLiteral minusOne() {
        return IntegerLiteral.MINUS_ONE;
    }

    public static Fraction oneHalf() {
        return Fraction.ONE_HALF;
    }

    <R> R accept(ExpressionVisitor<R> visitor);

    /**
     * Returns this if this is an IntegerLiteral. Otherwise returns empty.
     */
    default public Optional<IntegerLiteral> asIntegerLiteral() {
        return Optional.empty();
    }

    /**
     * Returns this if this is a Fraction. Otherwise returns empty.
     */
    default public Optional<Fraction> asFraction() {
        return Optional.empty();
    }

    /**
     * If this expression is an integer literal, returns its value.
     */
    default public Optional<BigInt> asBigInt() {
        return asIntegerLiteral().map(i -> i.getValue());
    }

    /**
     * If this expression is an integer literal, or a fraction of integer literals,
     * returns the numerator denominator pairs.
     */
    default public Optional<Pair<BigInt, BigInt>> asBigIntFraction() {
        return asBigInt().map(i -> Pair.of(i, BigInt.one()))
                .orElseTry(() -> asFraction().flatMap(frac -> frac.getNumerator().asBigInt()
                        .flatMap(num -> frac.getDenominator().asBigInt().map(den -> Pair.of(num, den)))));
    }
}
