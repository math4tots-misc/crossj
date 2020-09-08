package crossj;

public interface XIterable<T> extends Iterable<T> {
    XIterator<T> iter();
}
