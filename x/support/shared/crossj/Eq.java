package crossj;

/**
 * Utility for doing equality checks
 */
public final class Eq {
    private Eq() {}

    public static <T> boolean of(T a, T b) {
        return a == null ? b == null : a.equals(b);
    }
}
