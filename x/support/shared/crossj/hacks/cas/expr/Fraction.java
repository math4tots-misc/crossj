package crossj.hacks.cas.expr;

import crossj.BigInt;
import crossj.List;
import crossj.Optional;
import crossj.Pair;

/**
 * A fraction of two expressions.
 */
public final class Fraction implements Expression {
    static final Fraction ONE_HALF = ofInts(1, 2);

    private final Expression numerator;
    private final Expression denominator;

    private Fraction(Expression numerator, Expression denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public static Fraction of(Expression numerator, Expression denominator) {
        return new Fraction(numerator, denominator);
    }

    public static Fraction ofInts(int numerator, int denominator) {
        return of(Expression.fromInt(numerator), Expression.fromInt(denominator));
    }

    public static Fraction ofBigInts(BigInt numerator, BigInt denominator) {
        return of(Expression.fromBigInt(numerator), Expression.fromBigInt(denominator));
    }

    /**
     * Returns a fraction with the given numerator and denominators, but reduced
     * so that the numerator and denominator are coprime.
     */
    public static Expression reduceFromInts(int numerator, int denominator) {
        return reduceFromBigInts(BigInt.fromInt(numerator), BigInt.fromInt(denominator));
    }

    /**
     * Returns a fraction with the given numerator and denominators, but reduced
     * so that the numerator and denominator are coprime.
     */
    public static Expression reduceFromBigInts(BigInt numerator, BigInt denominator) {
        var pair = reduceBigInts(numerator, denominator);
        if (pair.get2().equals(BigInt.one())) {
            return IntegerLiteral.fromBigInt(pair.get1());
        } else {
            return ofBigInts(pair.get1(), pair.get2());
        }
    }

    /**
     * Given a pair of BigInts, returns a pair of BigInts divided by their GCD.
     */
    public static Pair<BigInt, BigInt> reduceBigInts(BigInt numerator, BigInt denominator) {
        var gcd = numerator.gcd(denominator);
        return Pair.of(numerator.divide(gcd), denominator.divide(gcd));
    }

    public Expression getNumerator() {
        return numerator;
    }

    public Expression getDenominator() {
        return denominator;
    }

    @Override
    public Optional<Fraction> asFraction() {
        return Optional.of(this);
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitFraction(this);
    }

    @Override
    public String toString() {
        return numerator.toString() + "/" + denominator.toString();
    }

    @Override
    public int hashCode() {
        return List.of("Fraction", numerator, denominator).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Fraction)) {
            return false;
        }
        var other = (Fraction) obj;
        return numerator.equals(other.numerator) && denominator.equals(other.denominator);
    }
}
