package crossj;

/**
 * For when a List just feels too inefficient, and Bytes is too untyped.
 */
public final class DoubleArray implements XIterable<Double> {
    public native static DoubleArray of(double... args);

    public native static DoubleArray fromJavaDoubleArray(double[] args);

    public native static DoubleArray withSize(int size);

    public native static DoubleArray fromList(List<Double> list);

    public native static DoubleArray fromIterable(XIterable<Double> iterable);

    public native int size();

    public native double get(int i);

    public native void set(int i, double value);

    @Override
    public native XIterator<Double> iter();

    @Override
    public native String toString();

    @Override
    public native boolean equals(Object obj);

    public native DoubleArray clone();
}
