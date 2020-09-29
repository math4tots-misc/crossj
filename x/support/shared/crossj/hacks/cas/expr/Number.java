package crossj.hacks.cas.expr;

import crossj.Optional;

/**
 * An expression that is just a number.
 * Can be considered a constant in all contexts.
 */
public interface Number extends Expression {
    public static Rational one() {
        return Rational.ONE;
    }

    public static Rational oneHalf() {
        return Rational.ONE_HALF;
    }

    public static Rational zero() {
        return Rational.ZERO;
    }

    public static Rational minusOne() {
        return Rational.MINUS_ONE;
    }

    public static Rational fromInt(int i) {
        return Rational.fromInt(i);
    }

    @Override
    default Optional<Boolean> isNumber(Number number) {
        return Optional.of(equals(number));
    }
}
