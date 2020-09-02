package crossj;

import java.util.ArrayList;
import java.util.Arrays;

public final class List<T> {
    private final ArrayList<T> list;

    private List(ArrayList<T> list) {
        this.list = list;
    }

    @SafeVarargs
    public static <T> List<T> of(T... args) {
        return new List<T>(new ArrayList<>(Arrays.asList(args)));
    }

    public int size() {
        return list.size();
    }
}
