package crossj;

/**
 * For when a List just feels too inefficient, and Bytes is too untyped.
 */
public final class IntArray implements XIterable<Integer> {
    public native static IntArray of(int... args);

    public native static IntArray fromJavaIntArray(int[] args);

    public native static IntArray withSize(int size);

    public native static IntArray fromList(List<Integer> list);

    public native static IntArray fromIterable(XIterable<Integer> iterable);

    public native int size();

    public native int get(int i);

    public native void set(int i, int value);

    @Override
    public native XIterator<Integer> iter();

    @Override
    public native String toString();

    @Override
    public native boolean equals(Object obj);

    public native IntArray clone();
}