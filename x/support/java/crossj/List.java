package crossj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public final class List<T> implements XIterable<T>, Comparable<List<T>> {
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

    public static <T> List<T> ofSize(int n, Func0<T> f) {
        List<T> ret = List.of();
        for (int i = 0; i < n; i++) {
            ret.add(f.apply());
        }
        return ret;
    }

    public static <T> List<T> reversed(Iterable<T> iterable) {
        List<T> ret = List.fromIterable(iterable);
        Collections.reverse(ret.list);
        return ret;
    }

    public static <T extends Comparable<T>> List<T> sorted(Iterable<T> iterable) {
        List<T> ret = List.fromIterable(iterable);
        Collections.sort(ret.list);
        return ret;
    }

    public int size() {
        return list.size();
    }

    public T get(int i) {
        return list.get(i);
    }

    public T last() {
        return list.get(list.size() - 1);
    }

    public void set(int i, T t) {
        list.set(i, t);
    }

    public T pop() {
        return list.remove(list.size() - 1);
    }

    public void reverse() {
        Collections.reverse(list);
    }

    @SuppressWarnings("unchecked")
    public void sort() {
        Collections.sort(list, (a, b) -> ((Comparable<T>) a).compareTo(b));
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

    public List<T> repeat(int n) {
        List<T> ret = List.of();
        for (int i = 0; i < n; i++) {
            ret.addAll(this);
        }
        return ret;
    }

    public <R> List<R> flatMap(Func1<Iterable<R>, T> f) {
        ArrayList<R> ret = new ArrayList<>();
        for (T t: list) {
            for (R r: f.apply(t)) {
                ret.add(r);
            }
        }
        return new List<>(ret);
    }

    public <R> List<R> map(Func1<R, T> f) {
        ArrayList<R> ret = new ArrayList<>();
        for (T t: list) {
            ret.add(f.apply(t));
        }
        return new List<>(ret);
    }

    public List<T> filter(Func1<Boolean, T> f) {
        ArrayList<T> ret = new ArrayList<>();
        for (T t: list) {
            if (f.apply(t)) {
                ret.add(t);
            }
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

    public void addAll(XIterable<T> ts) {
        ts.forEach(list::add);
    }

    public T removeIndex(int i) {
        return list.remove(i);
    }

    public void removeValue(T t) {
        list.remove(t);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(List<T> o) {
        int len = Math.min(size(), o.size());
        for (int i = 0; i < len; i++) {
            T a = list.get(i);
            T b = o.list.get(i);
            int cmp = ((Comparable<T>) a).compareTo(b);
            if (cmp != 0) {
                return cmp;
            }
        }
        return Integer.compare(len, o.size());
    }

    public List<T> clone() {
        return new List<>(new ArrayList<>(list));
    }
}
