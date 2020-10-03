package crossj.base;

// import java.util.Iterator;

/**
 * Iterator with many convenience methods
 */
public final class XIterator<T> implements XIterable<T> /* , Iterator<T> */ {
    public static native <T> XIterator<T> fromParts(Func0<Boolean> hasNext, Func0<T> getNext);
    public native <R> XIterator<R> flatMap(Func1<XIterable<R>, T> f);
    public native <R> XIterator<R> map(Func1<R, T> f);
    public native XIterator<T> filter(Func1<Boolean, T> f);
    public native <R> R fold(R start, Func2<R, R, T> f);
    public native T reduce(Func2<T, T, T> f);

    public native boolean all(Func1<Boolean, T> f);
    public native boolean any(Func1<Boolean, T> f);

    @Override
    public native XIterator<T> iter();

    public native List<T> list();

    public native XIterator<List<T>> chunk(int n);

    public native XIterator<T> take(int n);

    public native XIterator<T> skip(int n);

    public native List<T> pop(int n);

    /*
    public native boolean hasNext();
    public native T next();
    */
}
