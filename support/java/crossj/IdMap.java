package crossj;

import java.util.IdentityHashMap;

public final class IdMap<K, V> {
    private final IdentityHashMap<K, V> map;

    private IdMap(IdentityHashMap<K, V> map) {
        this.map = map;
    }

    @SafeVarargs
    public static <K, V> IdMap<K, V> of(Pair<K, V>... pairs) {
        IdentityHashMap<K, V> map = new IdentityHashMap<>();
        for (Pair<K, V> pair: pairs) {
            map.put(pair.get1(), pair.get2());
        }
        return new IdMap<>(map);
    }

    public int size() {
        return map.size();
    }

    public V get(K key) {
        return map.get(key);
    }

    public void put(K key, V value) {
        map.put(key, value);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public XIterable<K> keys() {
        return new XIterable<K>(){
            @Override
            public XIterator<K> iter() {
                return XIterator.fromIterator(map.keySet().iterator());
            }
        };
    }
}
