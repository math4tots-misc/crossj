package crossj.misc;

/**
 * Dead simple counter class.
 *
 * E.g. for generating unique ids.
 */
public final class Counter {
    private int i;

    private Counter(int i) {
        this.i = i;
    }

    public static Counter from(int start) {
        return new Counter(start);
    }

    public static Counter fromZero() {
        return from(0);
    }

    public int next() {
        return i++;
    }

    public int peek() {
        return i;
    }
}
