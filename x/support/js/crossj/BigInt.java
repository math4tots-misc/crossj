package crossj;

/**
 * A Facade class for Javascript's own BigInt
 *
 * There is no actual $CJ['crossj.BigInt'] entry.
 * The transpiler will special case all uses of BigInt
 * as needed.
 */
public final class BigInt {
    public native static BigInt one();

    public native static BigInt zero();

    public native static BigInt fromInt(int value);

    public native static BigInt fromDouble(double value);

    public native int intValue();

    public native double doubleValue();

    public native BigInt abs();

    public native BigInt negate();

    public native BigInt add(BigInt other);

    public native BigInt subtract(BigInt other);

    public native BigInt multiply(BigInt other);

    public native BigInt divide(BigInt other);

    public native BigInt remainder(BigInt other);

    public native BigInt pow(int other);
}
