package crossj;

import java.util.Arrays;
import java.util.HashSet;

public class Set<T> implements XIterable<T> {
    private final HashSet<T> set;

    private Set(HashSet<T> set) {
        this.set = set;
    }

    @SafeVarargs
    public static <T> Set<T> of(T... args) {
        return new Set<>(new HashSet<>(Arrays.asList(args)));
    }

    public int size() {
        return set.size();
    }

    public boolean contains(T key) {
        return set.contains(key);
    }

    public void add(T key) {
        set.add(key);
    }

    public void remove(T key) {
        set.remove(key);
    }

    @Override
    public XIterator<T> iter() {
        return XIterator.fromIterator(set.iterator());
    }
}
