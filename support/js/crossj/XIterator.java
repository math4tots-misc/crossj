package crossj;

/**
 * Iterator with many convenience methods
 */
public final class XIterator<T> {
    public native <R> XIterator<R> map(Func1<R, T> f);
    public native XIterator<T> filter(Func1<Boolean, T> f);
    public native <R> R fold(R start, Func2<R, R, T> f);
    public native T reduce(Func2<T, T, T> f);
}
