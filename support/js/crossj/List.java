package crossj;

public final class List<T> {
    @SafeVarargs
    public static native <T> List<T> of(T... args);
    public native int size();
    public native T get(int i);
    public native void set(int i, T t);

    @Override
    public native boolean equals(Object other);

    @Override
    public native String toString();

    public native <R> List<R> map(Func1<R, T> f);
}
