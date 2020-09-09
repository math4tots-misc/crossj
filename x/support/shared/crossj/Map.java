package crossj;

public final class Map<K, V> {
    private int siz = 0;
    private List<List<Tuple3<Integer, K, V>>> list;

    private Map() {
        this.list = null;
    }

    private void rehash(int newCap) {
        if (list == null || list.size() < newCap) {
            List<List<Tuple3<Integer, K, V>>> oldList = list;
            siz = 0;
            list = List.ofSize(newCap, () -> List.of());
            for (List<Tuple3<Integer, K, V>> bucket : oldList) {
                for (Tuple3<Integer, K, V> triple : bucket) {
                    insertNoRehash(triple);
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

    public void put(K key, V value) {
        checkForRehashBeforeInsert();
        int hash = key.hashCode();
        insertNoRehash(Tuple3.of(hash, key, value));
    }
}
