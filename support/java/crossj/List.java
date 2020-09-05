package crossj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public final class List<T> implements XIterable<T> {
    private final ArrayList<T> list;

    private List(ArrayList<T> list) {
        this.list = list;
    }

    public static <T> List<T> fromIterator(Iterator<T> iterator) {
        ArrayList<T> list = new ArrayList<>();
        iterator.forEachRemaining(list::add);
        return new List<>(list);
    }

    public static <T> List<T> fromIterable(Iterable<T> iterable) {
        return fromIterator(iterable.iterator());
    }

    @SafeVarargs
    public static <T> List<T> of(T... args) {
        return new List<T>(new ArrayList<>(Arrays.asList(args)));
    }

    public int size() {
        return list.size();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof List<?>)) {
            return false;
        }
        List<?> olist = (List<?>) other;
        return list.equals(olist.list);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        boolean first = true;
        for (T t: list) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(Repr.of(t));
        }
        sb.append(']');
        return sb.toString();
    }

    public <R> List<R> map(Func1<R, T> f) {
        ArrayList<R> ret = new ArrayList<>();
        for (T t: list) {
            ret.add(f.apply(t));
        }
        return new List<>(ret);
    }

    public <R> R fold(R start, Func2<R, R, T> f) {
        for (T t: list) {
            start = f.apply(start, t);
        }
        return start;
    }

    public T reduce(Func2<T, T, T> f) {
        if (list.isEmpty()) {
            throw new RuntimeException("Reduce on an empty list");
        }
        T start = list.get(0);
        for (T t: list.subList(1, list.size())) {
            start = f.apply(start, t);
        }
        return start;
    }

    @Override
    public XIterator<T> iter() {
        return XIterator.fromIterator(list.iterator());
    }

    public boolean contains(T t) {
        return list.contains(t);
    }

    public void add(T t) {
        list.add(t);
    }

    public T removeIndex(int i) {
        return list.remove(i);
    }

    public void removeValue(T t) {
        list.remove(t);
    }
}
