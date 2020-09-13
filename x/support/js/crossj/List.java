package crossj;

public final class List<T> implements XIterable<T>, Comparable<List<T>> {
    @SafeVarargs
    public static native <T> List<T> of(T... args);

    public static native <T> List<T> fromJavaArray(T[] args);

    public static native <T> List<T> ofSize(int n, Func0<T> f);

    public static native <T> List<T> fromIterable(XIterable<T> iterable);

    public static native <T> List<T> reversed(XIterable<T> iterable);

    public static native <T extends Comparable<T>> List<T> sorted(Iterable<T> iterable);

    public native int size();
    public native T get(int i);
    public native T last();
    public native void set(int i, T t);
    public native T pop();

    public native void reverse();
    public native void sort();

    @Override
    public native boolean equals(Object other);

    @Override
    public native String toString();

    public native <R> List<R> flatMap(Func1<XIterable<R>, T> f);
    public native <R> List<R> map(Func1<R, T> f);
    public native <R> R fold(R start, Func2<R, R, T> f);
    public native T reduce(Func2<T, T, T> f);
    public native boolean contains(T t);
    public native void add(T t);
    public native T removeIndex(int i);
    public native void removeValue(T t);

    @Override
    public native XIterator<T> iter();

    public native List<T> repeat(int n);

    @Override
    public native int compareTo(List<T> o);

    public native List<T> clone();
}
