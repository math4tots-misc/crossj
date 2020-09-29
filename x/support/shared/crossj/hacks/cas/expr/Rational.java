package crossj.hacks.cas.expr;

import crossj.BigInt;
import crossj.XError;

/**
 * A constant rational number
 */
public final class Rational implements Number {
    static final Rational ZERO = fromInt(0);
    static final Rational ONE = fromInt(1);
    static final Rational MINUS_ONE = fromInt(-1);
    static final Rational ONE_HALF = ofInts(1, 2);

    private final BigInt numerator;
    private final BigInt denominator;

    private Rational(BigInt numerator, BigInt denominator) {
        if (denominator.equals(BigInt.zero())) {
            throw XError.withMessage("Division by zero");
        }
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public static Rational of(BigInt numerator, BigInt denominator) {
        return new Rational(numerator, denominator);
    }

    public static Rational ofInts(int numerator, int denominator) {
        return of(BigInt.fromInt(numerator), BigInt.fromInt(denominator));
    }

    public static Rational fromInt(int x) {
        return of(BigInt.fromInt(x), BigInt.one());
    }

    public BigInt getNumerator() {
        return numerator;
    }

    public BigInt getDenominator() {
        return denominator;
    }

    public Rational normalizeRational() {
        if (denominator.equals(BigInt.one())) {
            return this;
        } else {
            var gcd = numerator.gcd(denominator);
            if (gcd.equals(BigInt.one())) {
                return this;
            } else {
                return of(numerator.divide(gcd), denominator.divide(gcd));
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rational) {
            var a = normalizeRational();
            var b = ((Rational) obj).normalizeRational();
            return a.numerator.equals(b.numerator) && a.denominator.equals(b.denominator);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (denominator.equals(BigInt.one())) {
            return numerator.toString();
        } else {
            return numerator + "/" + denominator;
        }
    }
}
