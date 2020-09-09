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

    public static <K, V> Map<K, V> fromIterable(Iterable<Pair<K, V>> pairs) {
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
        int index = getIndex(hash, list.size());
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
        checkForRehashBeforeInsert();
        int hash = key.hashCode();
        insertNoRehash(Tuple3.of(hash, key, value));
    }

    private Tuple3<Integer, K, V> getTripleOrNull(K key) {
        if (list == null) {
            return null;
        }
        int hash = key.hashCode();
        int index = getIndex(hash, list.size());
        List<Tuple3<Integer, K, V>> bucket = list.get(index);
        for (Tuple3<Integer, K, V> triple : bucket) {
            if (triple.get1().equals(hash) && triple.get2().equals(key)) {
                return triple;
            }
        }
        return null;
    }

    public V getOrNull(K key) {
        if (list == null) {
            return null;
        }
        int hash = key.hashCode();
        int index = getIndex(hash, list.size());
        List<Tuple3<Integer, K, V>> bucket = list.get(index);
        for (Tuple3<Integer, K, V> triple : bucket) {
            if (triple.get1().equals(hash) && triple.get2().equals(key)) {
                return triple.get3();
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        return getTripleOrNull(key) != null;
    }

    public V get(K key) {
        Tuple3<Integer, K, V> triple = getTripleOrNull(key);
        if (triple == null) {
            throw XError.withMessage("Key " + Repr.of(key) + " not found in this map");
        }
        return triple.get3();
    }

    public V getOrElse(K key, Func0<V> f) {
        Tuple3<Integer, K, V> triple = getTripleOrNull(key);
        return triple == null ? f.apply() : triple.get3();
    }

    private Tuple3<Integer, K, V> removeTripleOrNull(K key) {
        if (list == null) {
            return null;
        }
        int hash = key.hashCode();
        int index = getIndex(hash, list.size());
        List<Tuple3<Integer, K, V>> bucket = list.get(index);
        for (int i = 0; i < bucket.size(); i++) {
            Tuple3<Integer, K, V> triple = bucket.get(i);
            if (triple.get1().equals(hash) && triple.get2().equals(key)) {
                siz--;
                return bucket.removeIndex(i);
            }
        }
        return null;
    }

    public boolean removeOrFalse(K key) {
        Tuple3<Integer, K, V> triple = removeTripleOrNull(key);
        if (triple == null) {
            return false;
        } else {
            return true;
        }
    }

    public void remove(K key) {
        if (removeTripleOrNull(key) == null) {
            throw XError.withMessage("Key " + Repr.of(key) + " not found in this map");
        }
    }

    public XIterator<K> keys() {
        if (list == null) {
            return List.<K>of().iter();
        } else {
            return list.iter().flatMap(bucket -> bucket.map(triple -> triple.get2()));
        }
    }

    private static int getIndex(int hash, int size) {
        return (hash % size + size) % size;
    }
}
