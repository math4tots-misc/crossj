package crossj.base;

public final class Set<T> implements XIterable<T> {
    private final Map<T, Boolean> map;

    private Set(Map<T, Boolean> map) {
        this.map = map;
    }

    @SafeVarargs
    public static <T> Set<T> of(T... args) {
        Map<T, Boolean> map = Map.of();
        for (T arg: args) {
            map.put(arg, true);
        }
        return new Set<>(map);
    }

    public static <T> Set<T> fromIterable(Iterable<T> args) {
        Set<T> set = Set.of();
        for (T arg: args) {
            set.add(arg);
        }
        return set;
    }

    public int size() {
        return map.size();
    }

    public boolean contains(T key) {
        return map.containsKey(key);
    }

    public void add(T key) {
        map.put(key, true);
    }

    public boolean removeOrFalse(T key) {
        return map.removeOrFalse(key);
    }

    public void remove(T key) {
        map.remove(key);
    }

    @Override
    public XIterator<T> iter() {
        return map.keys();
    }
}
