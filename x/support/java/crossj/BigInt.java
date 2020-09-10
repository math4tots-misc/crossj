package crossj;

import java.math.BigInteger;

/**
 * Normalizing wrapper around Java's BigInteger
 */
public final class BigInt {
    private static final BigInt ONE = new BigInt(BigInteger.ONE);
    private static final BigInt ZERO = new BigInt(BigInteger.ZERO);

    private final BigInteger value;

    private BigInt(BigInteger value) {
        this.value = value;
    }

    public static BigInt one() {
        return ONE;
    }

    public static BigInt zero() {
        return ZERO;
    }

    public static BigInt fromInt(int value) {
        return new BigInt(BigInteger.valueOf(value));
    }

    public static BigInt fromDouble(double value) {
        return new BigInt(BigInteger.valueOf((long) value));
    }

    public int intValue() {
        return value.intValue();
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public BigInt abs() {
        return new BigInt(value.abs());
    }

    public BigInt negate() {
        return new BigInt(value.negate());
    }

    public BigInt add(BigInt other) {
        return new BigInt(value.add(other.value));
    }

    public BigInt subtract(BigInt other) {
        return new BigInt(value.subtract(other.value));
    }

    public BigInt multiply(BigInt other) {
        return new BigInt(value.multiply(other.value));
    }

    public BigInt divide(BigInt other) {
        return new BigInt(value.divide(other.value));
    }

    public BigInt remainder(BigInt other) {
        return new BigInt(value.remainder(other.value));
    }

    public BigInt pow(int other) {
        return new BigInt(value.pow(other));
    }

    @Override
    public boolean equals(Object obj) {
        return value.equals(((BigInt) obj).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
