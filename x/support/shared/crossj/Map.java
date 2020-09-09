package crossj;

public final class Map<K, V> {
    private int siz = 0;
    private List<List<Tuple3<Integer, K, V>>> list = null;
    private Map() {}

    @SafeVarargs
    public static <K, V> Map<K, V> of(Pair<K, V>... pairs) {
        Map<K, V> map = new Map<>();
        for (Pair<K, V> pair : pairs) {
            map.put(pair.get1(), pair.get2());
        }
        return map;
    }

    private void rehash(int newCap) {
        if (list == null || list.size() < newCap) {
            List<List<Tuple3<Integer, K, V>>> oldList = list;
            siz = 0;
            list = List.ofSize(newCap, () -> List.of());
            if (oldList != null) {
                for (List<Tuple3<Integer, K, V>> bucket : oldList) {
                    for (Tuple3<Integer, K, V> triple : bucket) {
                        insertNoRehash(triple);
                    }
                }
            }
        }
    }

    private void insertNoRehash(Tuple3<Integer, K, V> triple) {
        int hash = triple.get1();
        K key = triple.get2();
        int index = hash % list.size();
        List<Tuple3<Integer, K, V>> bucket = list.get(index);
        for (int i = 0; i < bucket.size(); i++) {
            Tuple3<Integer, K, V> entry = bucket.get(i);
            if (hash == entry.get1() && entry.get2().equals(key)) {
                bucket.set(i, triple);
                return;
            }
        }
        siz++;
        bucket.add(triple);
    }

    private void checkForRehashBeforeInsert() {
        if (list == null || list.size() == 0) {
            rehash(16);
        } else if (4 * siz >= 3 * list.size()) {
            rehash(list.size() * 2);
        }
    }

    public int size() {
        return siz;
    }

    public void put(K key, V value) {
        if (value == null) {
            throw XError.withMessage("Maps cannot have null values");
        }
        checkForRehashBeforeInsert();
        int hash = key.hashCode();
        insertNoRehash(Tuple3.of(hash, key, value));
    }

    public V getOrNull(K key) {
        if (list == null) {
            return null;
        }
        int hash = key.hashCode();
        int index = hash % list.size();
        List<Tuple3<Integer, K, V>> bucket = list.get(index);
        for (Tuple3<Integer, K, V> triple : bucket) {
            if (triple.get1().equals(hash) && triple.get2().equals(key)) {
                return triple.get3();
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        return getOrNull(key) != null;
    }

    public V get(K key) {
        V value = getOrNull(key);
        if (value == null) {
            throw XError.withMessage("Key " + Repr.of(key) + " not found in this map");
        }
        return value;
    }

    public V removeOrNull(K key) {
        if (list == null) {
            return null;
        }
        int hash = key.hashCode();
        int index = hash % list.size();
        List<Tuple3<Integer, K, V>> bucket = list.get(index);
        for (int i = 0; i < bucket.size(); i++) {
            Tuple3<Integer, K, V> triple = bucket.get(i);
            if (triple.get1().equals(hash) && triple.get2().equals(key)) {
                siz--;
                return bucket.removeIndex(i).get3();
            }
        }
        return null;
    }

    public void remove(K key) {
        if (removeOrNull(key) == null) {
            throw XError.withMessage("Key " + Repr.of(key) + " not found in this map");
        }
    }
}
