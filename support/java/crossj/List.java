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
}
