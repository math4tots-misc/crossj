package crossj;

import java.util.HashMap;

public final class Map<K, V> {
    private final HashMap<K, V> map;

    private Map(HashMap<K, V> map) {
        this.map = map;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> of(Pair<K, V>... pairs) {
        HashMap<K, V> map = new HashMap<>();
        for (Pair<K, V> pair: pairs) {
            map.put(pair.get1(), pair.get2());
        }
        return new Map<>(map);
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
