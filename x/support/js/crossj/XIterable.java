package crossj;

import java.util.Iterator;

public interface XIterable<T> extends Iterable<T> {
    XIterator<T> iter();

    @Override
    default Iterator<T> iterator() {
        return iter();
    }
}
