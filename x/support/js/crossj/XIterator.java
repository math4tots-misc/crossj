package crossj;

// import java.util.Iterator;

/**
 * Iterator with many convenience methods
 */
public final class XIterator<T> implements XIterable<T> /* , Iterator<T> */ {
    public native <R> XIterator<R> flatMap(Func1<XIterable<R>, T> f);
    public native <R> XIterator<R> map(Func1<R, T> f);
    public native XIterator<T> filter(Func1<Boolean, T> f);
    public native <R> R fold(R start, Func2<R, R, T> f);
    public native T reduce(Func2<T, T, T> f);

    @Override
    public native XIterator<T> iter();

    public native List<T> list();

    /*
    public native boolean hasNext();
    public native T next();
    */
}
